package com.kangyonggan.extra.core.processor;

import com.kangyonggan.extra.core.annotation.Cache;
import com.kangyonggan.extra.core.annotation.Handle;
import com.kangyonggan.extra.core.model.Constants;
import com.kangyonggan.extra.core.util.JCTreeUtil;
import com.kangyonggan.extra.core.util.KeyExpressionUtil;
import com.kangyonggan.extra.core.util.PropertiesUtil;
import com.kangyonggan.extra.core.util.StringUtil;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static com.kangyonggan.extra.core.util.JCTreeUtil.*;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class CacheProcessor {

    public static void process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Element handleElement = null;
        for (Element element : env.getElementsAnnotatedWith(Handle.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                String type = JCTreeUtil.getAnnotationParameter(element, Handle.class, "type").toString();
                if (type.equals(Handle.Type.CACHE.name())) {
                    handleElement = element;
                    break;
                }
            }
        }
        for (Element element : env.getElementsAnnotatedWith(Cache.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                JCTree.JCExpression returnType = JCTreeUtil.getReturnType(element);
                if (returnType == null || returnType.toString().equals(Constants.RETURN_VOID)) {
                    return;
                }

                String handlePackageName = (String) JCTreeUtil.getAnnotationParameter(element, Cache.class, Constants.CACHE_HANDLE_NAME, PropertiesUtil.getCacheHandle());
                if (handleElement != null) {
                    handlePackageName = handleElement.toString();
                }
                JCTreeUtil.importPackage(element, handlePackageName);

                String className = handlePackageName.substring(handlePackageName.lastIndexOf(".") + 1);
                JCTreeUtil.defineVariable(element, className, List.nil());

                generateBlockCode(element, className, returnType);
            }
        }
    }

    /**
     * @param element
     * @param className
     * @param returnType
     */
    private static void generateBlockCode(Element element, String className, JCTree.JCExpression returnType) {
        String varName = Constants.VARIABLE_PREFIX + StringUtil.firstToLowerCase(className);
        JCTree tree = (JCTree) trees.getTree(element);

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
                    JCTree.JCExpression prefixExpr = treeMaker.Literal(prefix);
                    keyExpr = treeMaker.Binary(JCTree.Tag.PLUS, prefixExpr, keyExpr);
                }

                statements.append(JCTreeUtil.callMethodWithReturn(Constants.OBJECT_NAME, varName, Constants.VARIABLE_PREFIX + "cacheValue", Constants.METHOD_GET, List.of(keyExpr)));

                /**
                 * create code：if (_cacheValue != null) {return (returnType) _cacheValue;}
                 */
                JCTree.JCParens condition = treeMaker.Parens(JCTreeUtil.notNull(Constants.VARIABLE_PREFIX + "cacheValue"));
                JCTree.JCStatement statementTrue = treeMaker.Return(treeMaker.TypeCast(returnType, treeMaker.Ident(names.fromString(Constants.VARIABLE_PREFIX + "cacheValue"))));
                JCTree.JCBlock block = treeMaker.Block(1L, List.of(statementTrue));
                JCTree.JCIf jcIf = treeMaker.If(condition, block, null);
                statements.append(jcIf);

                for (int i = 0; i < tree.getStatements().size(); i++) {
                    JCTree.JCStatement statement = tree.getStatements().get(i);

                    ListBuffer<JCTree.JCStatement> transStats = processStatment(element, varName, statement);
                    for (JCTree.JCStatement stat : transStats) {
                        statements.append(stat);
                    }

                    if (i == tree.getStatements().size() - 1) {
                        if (returnType == null || returnType.toString().equals(Constants.RETURN_VOID)) {
                            if (!(statement instanceof JCTree.JCReturn)) {// return;
                                statements.append(createSet(varName, element));
                            }
                        }
                    }
                }

                result = treeMaker.Block(0, statements.toList());
            }
        });
    }

    private static ListBuffer<JCTree.JCStatement> processStatment(Element element, String varName, JCTree.JCStatement statement) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer();
        if (statement instanceof JCTree.JCReturn) {
            JCTree.JCReturn jcReturn = (JCTree.JCReturn) statement;
            if (jcReturn.expr == null) {// return;
                statements.append(createSet(varName, element));
                statements.append(statement);
            } else {// return xxx;
                /**
                 * create code: return (ReturnType) xxxHandle.set(key, returnValue, expire);
                 */
                String key = (String) JCTreeUtil.getAnnotationParameter(element, Cache.class, Constants.CACHE_KEY_NAME);
                JCTree.JCExpression keyExpr = KeyExpressionUtil.parse(key);

                String prefix = (String) JCTreeUtil.getAnnotationParameter(element, Cache.class, Constants.CACHE_PREFIX_NAME, PropertiesUtil.getCachePrefix());
                if (StringUtil.isNotEmpty(prefix)) {
                    JCTree.JCExpression prefixExpr = treeMaker.Literal(prefix);
                    keyExpr = treeMaker.Binary(JCTree.Tag.PLUS, prefixExpr, keyExpr);
                }

                Long expire = (Long) JCTreeUtil.getAnnotationParameter(element, Cache.class, Constants.CACHE_EXPIRE_NAME, PropertiesUtil.getCacheExpire());
                JCTree.JCLiteral expireExpr = treeMaker.Literal(expire);

                JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(varName)), names.fromString(Constants.METHOD_SET));
                JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.of(keyExpr, jcReturn.getExpression(), expireExpr));
                JCTree.JCTypeCast jcTypeCast = treeMaker.TypeCast(JCTreeUtil.getReturnType(element), methodInvocation);

                jcReturn.expr = jcTypeCast;
                statements.append(jcReturn);
            }
        } else if (statement instanceof JCTree.JCIf) {
            JCTree.JCIf jcIf = (JCTree.JCIf) statement;
            JCTree.JCBlock block;
            if (jcIf.thenpart != null) {
                if (jcIf.thenpart instanceof JCTree.JCBlock) {
                    block = (JCTree.JCBlock) jcIf.thenpart;
                    doBlock(element, varName, block);
                    jcIf.thenpart = block;
                } else {
                    ListBuffer<JCTree.JCStatement> stats = processStatment(element, varName, jcIf.thenpart);
                    jcIf.thenpart = treeMaker.Block(stats.size(), stats.toList());
                }
            } else if (jcIf.elsepart != null) {
                if (jcIf.elsepart instanceof JCTree.JCBlock) {
                    block = (JCTree.JCBlock) jcIf.elsepart;
                    doBlock(element, varName, block);
                    jcIf.elsepart = block;
                } else {
                    ListBuffer<JCTree.JCStatement> stats = processStatment(element, varName, jcIf.elsepart);
                    jcIf.elsepart = treeMaker.Block(stats.size(), stats.toList());
                }
            }

            statements.append(jcIf);
        } else if (statement instanceof JCTree.JCForLoop) {
            JCTree.JCForLoop forLoop = (JCTree.JCForLoop) statement;
            if (forLoop.body instanceof JCTree.JCBlock) {
                JCTree.JCBlock block = (JCTree.JCBlock) forLoop.body;
                doBlock(element, varName, block);
                forLoop.body = block;
            } else {
                ListBuffer<JCTree.JCStatement> stats = processStatment(element, varName, forLoop.body);
                forLoop.body = treeMaker.Block(stats.size(), stats.toList());
            }

            statements.append(forLoop);
        } else if (statement instanceof JCTree.JCDoWhileLoop) {
            JCTree.JCDoWhileLoop doWhileLoop = (JCTree.JCDoWhileLoop) statement;
            if (doWhileLoop.body instanceof JCTree.JCBlock) {
                JCTree.JCBlock block = (JCTree.JCBlock) doWhileLoop.body;
                doBlock(element, varName, block);
                doWhileLoop.body = block;
            } else {
                ListBuffer<JCTree.JCStatement> stats = processStatment(element, varName, doWhileLoop.body);
                doWhileLoop.body = treeMaker.Block(stats.size(), stats.toList());
            }

            statements.append(doWhileLoop);
        } else if (statement instanceof JCTree.JCWhileLoop) {
            JCTree.JCWhileLoop whileLoop = (JCTree.JCWhileLoop) statement;
            if (whileLoop.body instanceof JCTree.JCBlock) {
                JCTree.JCBlock block = (JCTree.JCBlock) whileLoop.body;
                doBlock(element, varName, block);
                whileLoop.body = block;
            } else {
                ListBuffer<JCTree.JCStatement> stats = processStatment(element, varName, whileLoop.body);
                whileLoop.body = treeMaker.Block(stats.size(), stats.toList());
            }

            statements.append(whileLoop);
        } else {
            statements.append(statement);
        }

        return statements;
    }

    private static void doBlock(Element element, String varName, JCTree.JCBlock block) {
        ListBuffer<JCTree.JCStatement> stats = new ListBuffer();
        for (JCTree.JCStatement st : block.getStatements()) {
            ListBuffer<JCTree.JCStatement> ss = processStatment(element, varName, st);

            for (JCTree.JCStatement stat : ss) {
                stats.append(stat);
            }
        }

        block.stats = stats.toList();
    }

    /**
     * reate code: xxxHandle.set(key, returnValue, expire);
     *
     * @param varName
     * @param element
     * @return
     */
    private static JCTree.JCStatement createSet(String varName, Element element) {
        String key = (String) JCTreeUtil.getAnnotationParameter(element, Cache.class, Constants.CACHE_KEY_NAME);
        JCTree.JCExpression keyExpr = KeyExpressionUtil.parse(key);

        String prefix = (String) JCTreeUtil.getAnnotationParameter(element, Cache.class, Constants.CACHE_PREFIX_NAME, PropertiesUtil.getCachePrefix());
        if (StringUtil.isNotEmpty(prefix)) {
            JCTree.JCExpression prefixExpr = treeMaker.Literal(prefix);
            keyExpr = treeMaker.Binary(JCTree.Tag.PLUS, prefixExpr, keyExpr);
        }

        Long expire = (Long) JCTreeUtil.getAnnotationParameter(element, Cache.class, Constants.CACHE_EXPIRE_NAME, PropertiesUtil.getCacheExpire());
        JCTree.JCLiteral expireExpr = treeMaker.Literal(expire);

        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(varName)), names.fromString(Constants.METHOD_SET));
        JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.of(keyExpr, JCTreeUtil.getNull(), expireExpr));

        return treeMaker.Exec(methodInvocation);
    }
}
