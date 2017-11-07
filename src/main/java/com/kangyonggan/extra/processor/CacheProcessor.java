package com.kangyonggan.extra.processor;

import com.kangyonggan.extra.annotation.Cache;
import com.kangyonggan.extra.model.Constants;
import com.kangyonggan.extra.util.JCTreeUtil;
import com.kangyonggan.extra.util.KeyExpressionUtil;
import com.kangyonggan.extra.util.PropertiesUtil;
import com.kangyonggan.extra.util.StringUtil;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class CacheProcessor {

    public static void process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(Cache.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                JCTree.JCExpression returnType = JCTreeUtil.getReturnType(element);
                if (returnType == null || returnType.toString().equals(Constants.RETURN_VOID)) {
                    return;
                }

                String handlePackageName = (String) JCTreeUtil.getAnnotationParameter(element, Cache.class, Constants.CACHE_HANDLE_NAME, PropertiesUtil.getCacheHandle());
                JCTreeUtil.importPackage(element, handlePackageName);

                String className = handlePackageName.substring(handlePackageName.lastIndexOf(".") + 1);
                JCTreeUtil.defineVariable(element, className, List.nil());

                generateReturnCode(element, className, returnType);
                generateBlockCode(element, className, returnType);
            }
        }
    }

    /**
     * @param element
     * @param className
     * @param returnType
     */
    private static void generateReturnCode(Element element, String className, JCTree.JCExpression returnType) {
        String varName = Constants.VARIABLE_PREFIX + StringUtil.firstToLowerCase(className);
        JCTree tree = (JCTree) JCTreeUtil.trees.getTree(element.getEnclosingElement());

        tree.accept(new TreeTranslator() {
            private boolean isTargetMethod;

            @Override
            public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                if (jcMethodDecl.sym != null) {
                    isTargetMethod = element.toString().equals(jcMethodDecl.sym.toString());
                }
                super.visitMethodDef(jcMethodDecl);
            }

            @Override
            public void visitAnnotation(JCTree.JCAnnotation jcAnnotation) {
                if (isTargetMethod) {
                    boolean isTargetAnno = Cache.class.getSimpleName().equals(jcAnnotation.annotationType.toString());
                    if (isTargetAnno) {
                        isTargetMethod = true;
                    }
                }
                super.visitAnnotation(jcAnnotation);
            }

            @Override
            public void visitReturn(JCTree.JCReturn jcReturn) {
                if (!isTargetMethod) {
                    super.visitReturn(jcReturn);
                    return;
                }

                /**
                 * create code: return (ReturnType) xxxHandle.set(key, returnValue, expire);
                 */
                String key = (String) JCTreeUtil.getAnnotationParameter(element, Cache.class, Constants.CACHE_KEY_NAME);
                JCTree.JCExpression keyExpr = KeyExpressionUtil.parse(key);

                String prefix = (String) JCTreeUtil.getAnnotationParameter(element, Cache.class, Constants.CACHE_PREFIX_NAME, PropertiesUtil.getCachePrefix());
                if (StringUtil.isNotEmpty(prefix)) {
                    JCTree.JCExpression prefixExpr = JCTreeUtil.treeMaker.Literal(prefix);
                    keyExpr = JCTreeUtil.treeMaker.Binary(JCTree.Tag.PLUS, prefixExpr, keyExpr);
                }

                Long expire = (Long) JCTreeUtil.getAnnotationParameter(element, Cache.class, Constants.CACHE_EXPIRE_NAME, PropertiesUtil.getCacheExpire());
                JCTree.JCLiteral expireExpr = JCTreeUtil.treeMaker.Literal(expire);

                JCTree.JCFieldAccess fieldAccess = JCTreeUtil.treeMaker.Select(JCTreeUtil.treeMaker.Ident(JCTreeUtil.names.fromString(varName)), JCTreeUtil.names.fromString(Constants.METHOD_SET));
                JCTree.JCMethodInvocation methodInvocation = JCTreeUtil.treeMaker.Apply(List.nil(), fieldAccess, List.of(keyExpr, jcReturn.getExpression(), expireExpr));

                JCTree.JCTypeCast jcTypeCast = JCTreeUtil.treeMaker.TypeCast(returnType, methodInvocation);
                jcReturn.expr = jcTypeCast;
                this.result = jcReturn;
            }
        });
    }

    /**
     * @param element
     * @param className
     * @param returnType
     */
    private static void generateBlockCode(Element element, String className, JCTree.JCExpression returnType) {
        String varName = Constants.VARIABLE_PREFIX + StringUtil.firstToLowerCase(className);
        JCTree tree = (JCTree) JCTreeUtil.trees.getTree(element);

        tree.accept(new TreeTranslator() {
            @Override
            public void visitBlock(JCTree.JCBlock tree) {
                ListBuffer<JCTree.JCStatement> statements = new ListBuffer();

                /**
                 * create code: Object _cacheValue = xxxHandle.get(key);
                 */
                String key = (String) JCTreeUtil.getAnnotationParameter(element, Cache.class, Constants.CACHE_KEY_NAME);
                JCTree.JCExpression keyExpr = KeyExpressionUtil.parse(key);

                String prefix = (String) JCTreeUtil.getAnnotationParameter(element, Cache.class, Constants.CACHE_PREFIX_NAME, PropertiesUtil.getCachePrefix());
                if (StringUtil.isNotEmpty(prefix)) {
                    JCTree.JCExpression prefixExpr = JCTreeUtil.treeMaker.Literal(prefix);
                    keyExpr = JCTreeUtil.treeMaker.Binary(JCTree.Tag.PLUS, prefixExpr, keyExpr);
                }

                statements.append(JCTreeUtil.callMethodWithReturn(Constants.OBJECT_NAME, varName, Constants.VARIABLE_CACHE_VALUE, Constants.METHOD_GET, List.of(keyExpr)));

                /**
                 * create code：if (_cacheValue != null) {return (returnType) _cacheValue;}
                 */
                JCTree.JCParens condition = JCTreeUtil.treeMaker.Parens(JCTreeUtil.notNull(Constants.VARIABLE_CACHE_VALUE));
                JCTree.JCStatement statementTrue = JCTreeUtil.treeMaker.Return(JCTreeUtil.treeMaker.TypeCast(returnType, JCTreeUtil.treeMaker.Ident(JCTreeUtil.names.fromString(Constants.VARIABLE_CACHE_VALUE))));
                JCTree.JCIf jcIf = JCTreeUtil.treeMaker.If(condition, statementTrue, null);
                statements.append(jcIf);

                for (JCTree.JCStatement jcStatement : tree.getStatements()) {
                    statements.append(jcStatement);
                }

                result = JCTreeUtil.treeMaker.Block(0, statements.toList());
            }
        });
    }

}
