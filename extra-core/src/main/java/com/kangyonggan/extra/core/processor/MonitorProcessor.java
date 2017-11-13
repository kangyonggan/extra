package com.kangyonggan.extra.core.processor;

import com.kangyonggan.extra.core.annotation.Monitor;
import com.kangyonggan.extra.core.model.Constants;
import com.kangyonggan.extra.core.util.JCTreeUtil;
import com.kangyonggan.extra.core.util.MonitorUtil;
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
 * @since 11/7/17
 */
public class MonitorProcessor {

    public static void process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(Monitor.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                if (StringUtil.isNotEmpty(PropertiesUtil.getMonitorServers())) {
                    JCTreeUtil.importPackage(element, MonitorUtil.class.getName());
                    generateBlockCode(element);
                }
            }
        }
    }

    private static void generateBlockCode(Element element) {
        JCTree tree = (JCTree) trees.getTree(element);

        tree.accept(new TreeTranslator() {

            @Override
            public void visitBlock(JCTree.JCBlock tree) {
                ListBuffer<JCTree.JCStatement> statements = new ListBuffer();

                /**
                 * create code: Long _monitorStartTime = System.currentTimeMillis();
                 */
                JCTree.JCExpression typeExpr = treeMaker.Ident(names.fromString("Long"));
                JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString("System")), names.fromString("currentTimeMillis"));
                JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.nil());
                JCTree.JCVariableDecl variableDecl = treeMaker.VarDef(treeMaker.Modifiers(0), names.fromString(Constants.VARIABLE_PREFIX + "monitorStartTime"), typeExpr, methodInvocation);
                statements.append(variableDecl);

                JCTree.JCExpression returnType = JCTreeUtil.getReturnType(element);
                for (int i = 0; i < tree.getStatements().size(); i++) {
                    JCTree.JCStatement statement = tree.getStatements().get(i);

                    ListBuffer<JCTree.JCStatement> transStats = processStatment(element, statement);
                    for (JCTree.JCStatement stat : transStats) {
                        statements.append(stat);
                    }

                    if (i == tree.getStatements().size() - 1) {
                        if (returnType == null || returnType.toString().equals(Constants.RETURN_VOID)) {
                            if (!(statement instanceof JCTree.JCReturn)) {// return;
                                statements.append(createMonitor(element));
                            }
                        }
                    }
                }

                result = treeMaker.Block(0, statements.toList());
            }

        });
    }


    private static ListBuffer<JCTree.JCStatement> processStatment(Element element, JCTree.JCStatement statement) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer();
        if (statement instanceof JCTree.JCReturn) {
            JCTree.JCReturn jcReturn = (JCTree.JCReturn) statement;
            if (jcReturn.expr == null) {// return;
                statements.append(createMonitor(element));
                statements.append(statement);
            } else {// return xxx;
                /**
                 * create code: return (ReturnType) MonitorUtil.monitor(serversStr, app, type, handlePackage, packageName, className, methodName, _monitorStartTime, returnValue, args);
                 */
                JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(MonitorUtil.class.getSimpleName())), names.fromString(Constants.METHOD_MONITOR));
                String serversStr = PropertiesUtil.getMonitorServers();
                String app = PropertiesUtil.getMonitorApp();
                String type = (String) JCTreeUtil.getAnnotationParameter(element, Monitor.class, Constants.MONITOR_TYPE_NAME, PropertiesUtil.getMonitorType());
                String handlePackageName = PropertiesUtil.getMonitorHandle();
                String packageName = JCTreeUtil.getPackageName(element);
                String className = packageName.substring(packageName.lastIndexOf(".") + 1);
                packageName = packageName.substring(0, packageName.lastIndexOf("."));

                ListBuffer<JCTree.JCExpression> monitorArgs = new ListBuffer();
                monitorArgs.append(treeMaker.Literal(serversStr));
                monitorArgs.append(treeMaker.Literal(app));
                monitorArgs.append(treeMaker.Literal(type));
                monitorArgs.append(treeMaker.Literal(handlePackageName));
                monitorArgs.append(treeMaker.Literal(packageName));
                monitorArgs.append(treeMaker.Literal(className));
                monitorArgs.append(JCTreeUtil.getMethodName(element));
                monitorArgs.append(treeMaker.Ident(names.fromString(Constants.VARIABLE_PREFIX + "monitorStartTime")));
                monitorArgs.append(jcReturn.getExpression());

                List<JCTree.JCExpression> args = JCTreeUtil.getParameters(element);
                for (JCTree.JCExpression arg : args) {
                    monitorArgs.append(arg);
                }
                JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, monitorArgs.toList());

                JCTree.JCTypeCast jcTypeCast = treeMaker.TypeCast(JCTreeUtil.getReturnType(element), methodInvocation);
                jcReturn.expr = jcTypeCast;
                statements.append(jcReturn);
            }
        } else if (statement instanceof JCTree.JCIf) {
            JCTree.JCIf jcIf = (JCTree.JCIf) statement;
            JCTree.JCBlock block;
            if (jcIf.thenpart != null) {
                block = (JCTree.JCBlock) jcIf.thenpart;
                doBlock(element, block);
                jcIf.thenpart = block;
            } else if (jcIf.elsepart != null) {
                block = (JCTree.JCBlock) jcIf.elsepart;
                doBlock(element, block);
                jcIf.elsepart = block;
            }

            statements.append(jcIf);
        } else if (statement instanceof JCTree.JCForLoop) {
            JCTree.JCForLoop forLoop = (JCTree.JCForLoop) statement;
            JCTree.JCBlock block = (JCTree.JCBlock) forLoop.body;
            doBlock(element, block);
            forLoop.body = block;

            statements.append(forLoop);
        } else if (statement instanceof JCTree.JCDoWhileLoop) {
            JCTree.JCDoWhileLoop doWhileLoop = (JCTree.JCDoWhileLoop) statement;
            JCTree.JCBlock block = (JCTree.JCBlock) doWhileLoop.body;
            doBlock(element, block);
            doWhileLoop.body = block;

            statements.append(doWhileLoop);
        } else if (statement instanceof JCTree.JCWhileLoop) {
            JCTree.JCWhileLoop whileLoop = (JCTree.JCWhileLoop) statement;
            JCTree.JCBlock block = (JCTree.JCBlock) whileLoop.body;
            doBlock(element, block);
            whileLoop.body = block;

            statements.append(whileLoop);
        } else {
            statements.append(statement);
        }

        return statements;
    }

    private static void doBlock(Element element, JCTree.JCBlock block) {
        ListBuffer<JCTree.JCStatement> stats = new ListBuffer();
        for (JCTree.JCStatement st : block.getStatements()) {
            ListBuffer<JCTree.JCStatement> ss = processStatment(element, st);

            for (JCTree.JCStatement stat : ss) {
                stats.append(stat);
            }
        }

        block.stats = stats.toList();
    }

    /**
     * create code: MonitorUtil.monitor(serversStr, app, type, handlePackage, packageName, className, methodName, _monitorStartTime, null, args);
     *
     * @param element
     * @return
     */
    private static JCTree.JCStatement createMonitor(Element element) {
        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(MonitorUtil.class.getSimpleName())), names.fromString(Constants.METHOD_MONITOR));
        String serversStr = PropertiesUtil.getMonitorServers();
        String app = PropertiesUtil.getMonitorApp();
        String type = (String) JCTreeUtil.getAnnotationParameter(element, Monitor.class, Constants.MONITOR_TYPE_NAME, PropertiesUtil.getMonitorType());
        String handlePackageName = PropertiesUtil.getMonitorHandle();
        String packageName = JCTreeUtil.getPackageName(element);
        String className = packageName.substring(packageName.lastIndexOf(".") + 1);
        packageName = packageName.substring(0, packageName.lastIndexOf("."));

        ListBuffer<JCTree.JCExpression> monitorArgs = new ListBuffer();
        monitorArgs.append(treeMaker.Literal(serversStr));
        monitorArgs.append(treeMaker.Literal(app));
        monitorArgs.append(treeMaker.Literal(type));
        monitorArgs.append(treeMaker.Literal(handlePackageName));
        monitorArgs.append(treeMaker.Literal(packageName));
        monitorArgs.append(treeMaker.Literal(className));
        monitorArgs.append(JCTreeUtil.getMethodName(element));
        monitorArgs.append(treeMaker.Ident(names.fromString(Constants.VARIABLE_PREFIX + "monitorStartTime")));
        monitorArgs.append(JCTreeUtil.getNull());

        List<JCTree.JCExpression> args = JCTreeUtil.getParameters(element);
        for (JCTree.JCExpression arg : args) {
            monitorArgs.append(arg);
        }
        JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, monitorArgs.toList());

        return treeMaker.Exec(methodInvocation);
    }


}
