package com.kangyonggan.extra.processor;

import com.kangyonggan.extra.annotation.Monitor;
import com.kangyonggan.extra.model.Constants;
import com.kangyonggan.extra.util.JCTreeUtil;
import com.kangyonggan.extra.util.MonitorUtil;
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

import static com.kangyonggan.extra.util.JCTreeUtil.*;

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
                 * create code: MonitorUtil.monitor(serversStr, app, type, handlePackage, packageName, className, methodName, args);
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

                List<JCTree.JCExpression> args = JCTreeUtil.getParameters(element);
                for (JCTree.JCExpression arg : args) {
                    monitorArgs.append(arg);
                }
                JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, monitorArgs.toList());
                statements.append(treeMaker.Exec(methodInvocation));

                for (int i = 0; i < tree.getStatements().size(); i++) {
                    statements.append(tree.getStatements().get(i));
                }

                result = treeMaker.Block(0, statements.toList());
            }

        });
    }


}
