package com.kangyonggan.extra.core.processor;

import com.kangyonggan.extra.core.annotation.Log;
import com.kangyonggan.extra.core.model.Constants;
import com.kangyonggan.extra.core.util.JCTreeUtil;
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
                String handlePackageName = (String) JCTreeUtil.getAnnotationParameter(element, Log.class, Constants.LOG_HANDLE_NAME, PropertiesUtil.getLogHandle());
                JCTreeUtil.importPackage(element, handlePackageName);

                String className = handlePackageName.substring(handlePackageName.lastIndexOf(".") + 1);
                JCTreeUtil.defineVariable(element, className, List.of(treeMaker.Literal(JCTreeUtil.getPackageName(element))));

                generateBlockCode(element, className);
            }
        }
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

                JCTree.JCExpression returnType = JCTreeUtil.getReturnType(element);
                for (int i = 0; i < tree.getStatements().size(); i++) {
                    JCTree.JCStatement statement = tree.getStatements().get(i);

                    ListBuffer<JCTree.JCStatement> transStats = processStatment(element, varName, statement);
                    for (JCTree.JCStatement stat : transStats) {
                        statements.append(stat);
                    }

                    if (i == tree.getStatements().size() - 1) {
                        if (returnType == null || returnType.toString().equals(Constants.RETURN_VOID)) {
                            if (!(statement instanceof JCTree.JCReturn)) {// return;
                                statements.append(createLogAfter(varName, element));
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
                statements.append(createLogAfter(varName, element));
                statements.append(statement);
            } else {// return xxx;
                /**
                 * create code: return (ReturnType) xxxHandle.logAfter(methodName, returnValue);
                 */
                JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(varName)), names.fromString("logAfter"));
                JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.of(JCTreeUtil.getMethodName(element), treeMaker.Ident(names.fromString(Constants.VARIABLE_PREFIX + "methodStartTime")), jcReturn.getExpression()));

                JCTree.JCTypeCast jcTypeCast = treeMaker.TypeCast(JCTreeUtil.getReturnType(element), methodInvocation);
                jcReturn.expr = jcTypeCast;
                statements.append(jcReturn);
            }
        } else if (statement instanceof JCTree.JCIf) {
            JCTree.JCIf jcIf = (JCTree.JCIf) statement;
            JCTree.JCBlock block;
            if (jcIf.thenpart != null) {
                block = (JCTree.JCBlock) jcIf.thenpart;
                doBlock(element, varName, block);
                jcIf.thenpart = block;
            } else if (jcIf.elsepart != null) {
                block = (JCTree.JCBlock) jcIf.elsepart;
                doBlock(element, varName, block);
                jcIf.elsepart = block;
            }

            statements.append(jcIf);
        } else if (statement instanceof JCTree.JCForLoop) {
            JCTree.JCForLoop forLoop = (JCTree.JCForLoop) statement;
            JCTree.JCBlock block = (JCTree.JCBlock) forLoop.body;
            doBlock(element, varName, block);
            forLoop.body = block;

            statements.append(forLoop);
        } else if (statement instanceof JCTree.JCDoWhileLoop) {
            JCTree.JCDoWhileLoop doWhileLoop = (JCTree.JCDoWhileLoop) statement;
            JCTree.JCBlock block = (JCTree.JCBlock) doWhileLoop.body;
            doBlock(element, varName, block);
            doWhileLoop.body = block;

            statements.append(doWhileLoop);
        } else if (statement instanceof JCTree.JCWhileLoop) {
            JCTree.JCWhileLoop whileLoop = (JCTree.JCWhileLoop) statement;
            JCTree.JCBlock block = (JCTree.JCBlock) whileLoop.body;
            doBlock(element, varName, block);
            whileLoop.body = block;

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
     * create code: xxxHandle.logAfter(methodName, returnValue);
     *
     * @param varName
     * @param element
     * @return
     */
    private static JCTree.JCStatement createLogAfter(String varName, Element element) {
        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(varName)), names.fromString("logAfter"));
        JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.of(JCTreeUtil.getMethodName(element), treeMaker.Ident(names.fromString(Constants.VARIABLE_PREFIX + "methodStartTime")), JCTreeUtil.getNull()));

        return treeMaker.Exec(methodInvocation);
    }

}
