package com.kangyonggan.extra.core.processor;

import com.kangyonggan.extra.core.annotation.Monitor;
import com.kangyonggan.extra.core.model.Constants;
import com.kangyonggan.extra.core.model.MonitorInfo;
import com.kangyonggan.extra.core.util.JCTreeUtil;
import com.kangyonggan.extra.core.util.KeyExpressionUtil;
import com.kangyonggan.extra.core.util.PropertiesUtil;
import com.kangyonggan.extra.core.util.StringUtil;
import com.sun.tools.javac.code.TypeTag;
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
 * @since 3/30/18
 */
public class MonitorProcessor {

    public static void process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(Monitor.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                String handlePackageName = (String) JCTreeUtil.getAnnotationParameter(element, Monitor.class, Constants.MONITOR_HANDLE_NAME, PropertiesUtil.getMonitorHandle());
                JCTreeUtil.importPackage(element, handlePackageName);
                String monitorInfoPkg = MonitorInfo.class.getName();
                JCTreeUtil.importPackage(element, monitorInfoPkg);

                String className = handlePackageName.substring(handlePackageName.lastIndexOf(".") + 1);
                JCTreeUtil.defineVariable(element, className, List.nil());

                generateBlockCode(element, className, JCTreeUtil.getReturnType(element));
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
                 * create code: Long _monitorMethodStartTime = System.currentTimeMillis();
                 */
                JCTree.JCExpression typeExpr = treeMaker.Ident(names.fromString("Long"));
                JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString("System")), names.fromString("currentTimeMillis"));
                JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.nil());
                JCTree.JCVariableDecl variableDecl = treeMaker.VarDef(treeMaker.Modifiers(0), names.fromString(Constants.VARIABLE_PREFIX + "monitorMethodStartTime"), typeExpr, methodInvocation);
                statements.append(variableDecl);

                for (int i = 0; i < tree.getStatements().size(); i++) {
                    JCTree.JCStatement statement = tree.getStatements().get(i);

                    ListBuffer<JCTree.JCStatement> transStats = processStatment(element, varName, statement);
                    for (JCTree.JCStatement stat : transStats) {
                        statements.append(stat);
                    }

                    if (i == tree.getStatements().size() - 1) {
                        if (returnType == null || returnType.toString().equals(Constants.RETURN_VOID)) {
                            if (!(statement instanceof JCTree.JCReturn)) {// return;
                                typeExpr = treeMaker.Ident(names.fromString("Long"));
                                fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString("System")), names.fromString("currentTimeMillis"));
                                methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.nil());
                                variableDecl = treeMaker.VarDef(treeMaker.Modifiers(0), names.fromString(Constants.VARIABLE_PREFIX + "monitorMethodEndTime"), typeExpr, methodInvocation);
                                statements.append(variableDecl);
                                statements.append(createHandle(varName, element));
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
                JCTree.JCExpression typeExpr = treeMaker.Ident(names.fromString("Long"));
                JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString("System")), names.fromString("currentTimeMillis"));
                JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.nil());
                JCTree.JCVariableDecl variableDecl = treeMaker.VarDef(treeMaker.Modifiers(0), names.fromString(Constants.VARIABLE_PREFIX + "monitorMethodEndTime"), typeExpr, methodInvocation);
                statements.append(variableDecl);
                statements.append(createHandle(varName, element));
                statements.append(statement);
            } else {// return xxx;
                /**
                 * create code: return (ReturnType) xxxHandle.handle(new Monitor(app, type, description, monitorMethodStartTime, monitorMethodEndTime, returnObj, args));
                 */
                ListBuffer<JCTree.JCExpression> monitorArgs = new ListBuffer();
                String app = (String) JCTreeUtil.getAnnotationParameter(element, Monitor.class, Constants.MONITOR_APP_NAME, PropertiesUtil.getMonitorApp());
                JCTree.JCExpression appExpr = KeyExpressionUtil.parse(app);
                monitorArgs.append(appExpr);

                String type = (String) JCTreeUtil.getAnnotationParameter(element, Monitor.class, Constants.MONITOR_TYPE_NAME, PropertiesUtil.getMonitorType());
                JCTree.JCExpression typeExpr = KeyExpressionUtil.parse(type);
                monitorArgs.append(typeExpr);

                String description = (String) JCTreeUtil.getAnnotationParameter(element, Monitor.class, Constants.MONITOR_DESCRIPTION_NAME, PropertiesUtil.getMonitorDescription());
                JCTree.JCExpression descriptionExpr = KeyExpressionUtil.parse(description);
                monitorArgs.append(descriptionExpr);

                JCTree.JCExpression methodStartTimeExor = treeMaker.Ident(names.fromString(Constants.VARIABLE_PREFIX + "monitorMethodStartTime"));
                monitorArgs.append(methodStartTimeExor);

                typeExpr = treeMaker.Ident(names.fromString("Long"));
                JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString("System")), names.fromString("currentTimeMillis"));
                JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.nil());
                JCTree.JCVariableDecl variableDecl = treeMaker.VarDef(treeMaker.Modifiers(0), names.fromString(Constants.VARIABLE_PREFIX + "monitorMethodEndTime"), typeExpr, methodInvocation);
                statements.append(variableDecl);
                JCTree.JCExpression methodEndTimeExor = treeMaker.Ident(names.fromString(Constants.VARIABLE_PREFIX + "monitorMethodEndTime"));
                monitorArgs.append(methodEndTimeExor);

                JCTree.JCLiteral jcLiteral = treeMaker.Literal(TypeTag.BOOLEAN, 1);
                monitorArgs.append(jcLiteral);
                monitorArgs.append(jcReturn.getExpression());

                List<JCTree.JCExpression> methodArgs = JCTreeUtil.getParameters(element);
                for (int i = 0; i < methodArgs.size(); i++) {
                    monitorArgs = monitorArgs.append(methodArgs.get(i));
                }
                JCTree.JCNewClass newClass = treeMaker.NewClass(null, null, treeMaker.Ident(names.fromString(MonitorInfo.class.getSimpleName())), monitorArgs.toList(), null);
                fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(varName)), names.fromString(Constants.METHOD_HANDLE));
                methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.of(newClass));
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
     * create code: xxxHandle.handle(new Monitor(app, type, description, monitorMethodStartTime, monitorMethodEndTime, returnObj, args);
     *
     * @param varName
     * @param element
     * @return
     */
    private static JCTree.JCStatement createHandle(String varName, Element element) {
        ListBuffer<JCTree.JCExpression> monitorArgs = new ListBuffer();
        String app = (String) JCTreeUtil.getAnnotationParameter(element, Monitor.class, Constants.MONITOR_APP_NAME, PropertiesUtil.getMonitorApp());
        JCTree.JCExpression appExpr = KeyExpressionUtil.parse(app);
        monitorArgs.append(appExpr);

        String type = (String) JCTreeUtil.getAnnotationParameter(element, Monitor.class, Constants.MONITOR_TYPE_NAME, PropertiesUtil.getMonitorType());
        JCTree.JCExpression typeExpr = KeyExpressionUtil.parse(type);
        monitorArgs.append(typeExpr);

        String description = (String) JCTreeUtil.getAnnotationParameter(element, Monitor.class, Constants.MONITOR_DESCRIPTION_NAME, PropertiesUtil.getMonitorDescription());
        JCTree.JCExpression descriptionExpr = KeyExpressionUtil.parse(description);
        monitorArgs.append(descriptionExpr);

        JCTree.JCExpression methodStartTimeExor = treeMaker.Ident(names.fromString(Constants.VARIABLE_PREFIX + "monitorMethodStartTime"));
        monitorArgs.append(methodStartTimeExor);

        JCTree.JCExpression methodEndTimeExor = treeMaker.Ident(names.fromString(Constants.VARIABLE_PREFIX + "monitorMethodEndTime"));
        monitorArgs.append(methodEndTimeExor);

        JCTree.JCLiteral jcLiteral = treeMaker.Literal(TypeTag.BOOLEAN, 0);
        monitorArgs.append(jcLiteral);

        monitorArgs.append(JCTreeUtil.getNull());

        List<JCTree.JCExpression> methodArgs = JCTreeUtil.getParameters(element);
        for (int i = 0; i < methodArgs.size(); i++) {
            monitorArgs = monitorArgs.append(methodArgs.get(i));
        }
        JCTree.JCNewClass newClass = treeMaker.NewClass(null, null, treeMaker.Ident(names.fromString(MonitorInfo.class.getSimpleName())), monitorArgs.toList(), null);
        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(varName)), names.fromString(Constants.METHOD_HANDLE));
        JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.of(newClass));
        return treeMaker.Exec(methodInvocation);
    }

}
