package com.kangyonggan.extra.core.processor;

import com.kangyonggan.extra.core.annotation.Log;
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
public class LogProcessor {

    public static void process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(Log.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                String handlePackageName = JCTreeUtil.getAnnotationParameter(element, Log.class, Constants.LOG_HANDLE_NAME, PropertiesUtil.getLogHandle());
                JCTreeUtil.importPackage(element, handlePackageName);

                String className = handlePackageName.substring(handlePackageName.lastIndexOf(".") + 1);
                JCTreeUtil.defineVariable(element, className, List.of(treeMaker.Literal(JCTreeUtil.getPackageName(element))));

                generateBlockCode(element, className);
                generateReturnCode(element, className);
            }
        }
    }

    /**
     * @param element
     * @param className
     */
    private static void generateReturnCode(Element element, String className) {
        String varName = Constants.VARIABLE_PREFIX + StringUtil.firstToLowerCase(className);
        JCTree tree = (JCTree) trees.getTree(element.getEnclosingElement());

        tree.accept(new TreeTranslator() {
            private boolean isTargetMethod;

            @Override
            public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                isTargetMethod = element.toString().equals(jcMethodDecl.sym.toString());
                super.visitMethodDef(jcMethodDecl);
            }

            @Override
            public void visitAnnotation(JCTree.JCAnnotation jcAnnotation) {
                if (isTargetMethod) {
                    boolean isTargetAnno = Log.class.getSimpleName().equals(jcAnnotation.annotationType.toString());
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
                 * create code: return (ReturnType) xxxHandle.logAfter(methodName, returnValue);
                 */
                JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(varName)), names.fromString("logAfter"));
                JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.of(JCTreeUtil.getMethodName(element), treeMaker.Ident(names.fromString(Constants.VARIABLE_PREFIX + "methodStartTime")), jcReturn.getExpression()));

                JCTree.JCTypeCast jcTypeCast = treeMaker.TypeCast(JCTreeUtil.getReturnType(element), methodInvocation);
                jcReturn.expr = jcTypeCast;
                this.result = jcReturn;
            }
        });
    }

    /**
     * @param element
     * @param className
     */
    private static void generateBlockCode(Element element, String className) {
        String varName = Constants.VARIABLE_PREFIX + StringUtil.firstToLowerCase(className);
        JCTree tree = (JCTree) trees.getTree(element);

        tree.accept(new TreeTranslator() {
            @Override
            public void visitBlock(JCTree.JCBlock tree) {
                ListBuffer<JCTree.JCStatement> statements = new ListBuffer();

                /**
                 * create code: xxxHandle.logBefore(methodName, args);
                 */
                JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(varName)), names.fromString(Constants.METHOD_LOG_BEFORE));
                JCTree.JCLiteral methodName = JCTreeUtil.getMethodName(element);
                List args = JCTreeUtil.getParameters(element);
                List params = List.of(methodName);
                for (int i = 0; i < args.size(); i++) {
                    params = params.append(args.get(i));
                }
                JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, params);
                JCTree.JCExpressionStatement code = treeMaker.Exec(methodInvocation);
                statements.append(code);

                /**
                 * create code: Long _methodStartTime = System.currentTimeMillis();
                 */
                JCTree.JCExpression typeExpr = treeMaker.Ident(names.fromString("Long"));
                fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString("System")), names.fromString("currentTimeMillis"));
                methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.nil());
                JCTree.JCVariableDecl variableDecl = treeMaker.VarDef(treeMaker.Modifiers(0), names.fromString(Constants.VARIABLE_PREFIX + "methodStartTime"), typeExpr, methodInvocation);
                statements.append(variableDecl);

                for (int i = 0; i < tree.getStatements().size(); i++) {
                    statements.append(tree.getStatements().get(i));
                }

                result = treeMaker.Block(0, statements.toList());
            }
        });
    }

}
