package com.kangyonggan.extra.processor;

import com.kangyonggan.extra.annotation.Log;
import com.kangyonggan.extra.model.Constants;
import com.kangyonggan.extra.util.JCTreeUtil;
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
public class LogProcessor {

    public static void process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(Log.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                String handlePackageName = (String) JCTreeUtil.getAnnotationParameter(element, Log.class, Constants.LOG_HANDLE_NAME, PropertiesUtil.getLogHandle());
                JCTreeUtil.importPackage(element, handlePackageName);

                String className = handlePackageName.substring(handlePackageName.lastIndexOf(".") + 1);
                JCTreeUtil.defineVariable(element, className, List.of(JCTreeUtil.treeMaker.Literal(JCTreeUtil.getPackageName(element))));

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


                JCTree.JCExpression returnType = JCTreeUtil.getReturnType(element);
                if (returnType != null && !returnType.toString().equals(Constants.RETURN_VOID)) {
                    /**
                     * create code: return (ReturnType) xxxHandle.logAfter(methodName, returnValue);
                     */
                    JCTree.JCFieldAccess fieldAccess = JCTreeUtil.treeMaker.Select(JCTreeUtil.treeMaker.Ident(JCTreeUtil.names.fromString(varName)), JCTreeUtil.names.fromString("logAfter"));
                    JCTree.JCMethodInvocation methodInvocation = JCTreeUtil.treeMaker.Apply(List.nil(), fieldAccess, List.of(JCTreeUtil.getMethodName(element), JCTreeUtil.treeMaker.Ident(JCTreeUtil.names.fromString(Constants.VARIABLE_PREFIX + "methodStartTime")), jcReturn.getExpression()));

                    JCTree.JCTypeCast jcTypeCast = JCTreeUtil.treeMaker.TypeCast(JCTreeUtil.getReturnType(element), methodInvocation);
                    jcReturn.expr = jcTypeCast;
                }
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
        JCTree tree = (JCTree) JCTreeUtil.trees.getTree(element);

        tree.accept(new TreeTranslator() {
            @Override
            public void visitBlock(JCTree.JCBlock tree) {
                ListBuffer<JCTree.JCStatement> statements = new ListBuffer();

                /**
                 * create code: xxxHandle.logBefore(methodName, args);
                 */
                JCTree.JCFieldAccess fieldAccess = JCTreeUtil.treeMaker.Select(JCTreeUtil.treeMaker.Ident(JCTreeUtil.names.fromString(varName)), JCTreeUtil.names.fromString(Constants.METHOD_LOG_BEFORE));
                JCTree.JCLiteral methodName = JCTreeUtil.getMethodName(element);
                List args = JCTreeUtil.getParameters(element);
                List params = List.of(methodName);
                for (int i = 0; i < args.size(); i++) {
                    params = params.append(args.get(i));
                }
                JCTree.JCMethodInvocation methodInvocation = JCTreeUtil.treeMaker.Apply(List.nil(), fieldAccess, params);
                JCTree.JCExpressionStatement code = JCTreeUtil.treeMaker.Exec(methodInvocation);
                statements.append(code);

                /**
                 * create code: Long _methodStartTime = System.currentTimeMillis();
                 */
                JCTree.JCExpression typeExpr = JCTreeUtil.treeMaker.Ident(JCTreeUtil.names.fromString("Long"));
                fieldAccess = JCTreeUtil.treeMaker.Select(JCTreeUtil.treeMaker.Ident(JCTreeUtil.names.fromString("System")), JCTreeUtil.names.fromString("currentTimeMillis"));
                methodInvocation = JCTreeUtil.treeMaker.Apply(List.nil(), fieldAccess, List.nil());
                JCTree.JCVariableDecl variableDecl = JCTreeUtil.treeMaker.VarDef(JCTreeUtil.treeMaker.Modifiers(0), JCTreeUtil.names.fromString(Constants.VARIABLE_PREFIX + "methodStartTime"), typeExpr, methodInvocation);
                statements.append(variableDecl);

                JCTree.JCExpression returnType = JCTreeUtil.getReturnType(element);
                for (int i = 0; i < tree.getStatements().size(); i++) {
                    if (i == tree.getStatements().size() - 1) {
                        if (returnType == null || returnType.toString().equals(Constants.RETURN_VOID)) {
                            if (tree.getStatements().get(i) instanceof JCTree.JCReturn) {
                                /**
                                 * create code: xxxHandle.logAfter(methodName, null);
                                 */
                                fieldAccess = JCTreeUtil.treeMaker.Select(JCTreeUtil.treeMaker.Ident(JCTreeUtil.names.fromString(varName)), JCTreeUtil.names.fromString(Constants.METHOD_LOG_AFTER));
                                methodInvocation = JCTreeUtil.treeMaker.Apply(List.nil(), fieldAccess, List.of(JCTreeUtil.getMethodName(element), JCTreeUtil.treeMaker.Ident(JCTreeUtil.names.fromString(Constants.VARIABLE_PREFIX + "methodStartTime")), JCTreeUtil.getNull()));

                                statements.append(JCTreeUtil.treeMaker.Exec(methodInvocation));
                                statements.append(tree.getStatements().get(i));
                            } else {
                                statements.append(tree.getStatements().get(i));
                                /**
                                 * create code: xxxHandle.logAfter(methodName, null);
                                 */
                                fieldAccess = JCTreeUtil.treeMaker.Select(JCTreeUtil.treeMaker.Ident(JCTreeUtil.names.fromString(varName)), JCTreeUtil.names.fromString(Constants.METHOD_LOG_AFTER));
                                methodInvocation = JCTreeUtil.treeMaker.Apply(List.nil(), fieldAccess, List.of(JCTreeUtil.getMethodName(element), JCTreeUtil.treeMaker.Ident(JCTreeUtil.names.fromString(Constants.VARIABLE_PREFIX + "methodStartTime")), JCTreeUtil.getNull()));

                                statements.append(JCTreeUtil.treeMaker.Exec(methodInvocation));
                            }
                        } else {
                            statements.append(tree.getStatements().get(i));
                        }
                    } else {
                        statements.append(tree.getStatements().get(i));
                    }
                }

                result = JCTreeUtil.treeMaker.Block(0, statements.toList());
            }
        });
    }

}