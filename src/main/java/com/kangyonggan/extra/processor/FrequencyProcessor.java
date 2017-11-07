package com.kangyonggan.extra.processor;

import com.kangyonggan.extra.annotation.Frequency;
import com.kangyonggan.extra.exception.MethodCalledFrequencyException;
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

import static com.kangyonggan.extra.util.JCTreeUtil.names;
import static com.kangyonggan.extra.util.JCTreeUtil.treeMaker;

/**
 * @author kangyonggan
 * @since 11/6/17
 */
public class FrequencyProcessor {

    public static void process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(Frequency.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                String handlePackageName = (String) JCTreeUtil.getAnnotationParameter(element, Frequency.class, Constants.FREQUENCY_HANDLE_NAME, PropertiesUtil.getFrequencyHandle());
                JCTreeUtil.importPackage(element, handlePackageName);
                JCTreeUtil.importPackage(element, MethodCalledFrequencyException.class.getName());

                String className = handlePackageName.substring(handlePackageName.lastIndexOf(".") + 1);
                JCTreeUtil.defineVariable(element, className, List.nil());

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
        JCTree tree = (JCTree) JCTreeUtil.trees.getTree(element);

        tree.accept(new TreeTranslator() {
            @Override
            public void visitBlock(JCTree.JCBlock tree) {
                ListBuffer<JCTree.JCStatement> statements = new ListBuffer();

                /**
                 * create code: _memoryFrementHandle.limit(key, interval, interrupt);
                 */
                JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(varName)), names.fromString(Constants.METHOD_LIMIT));
                Long interval = (Long) JCTreeUtil.getAnnotationParameter(element, Frequency.class, Constants.FREQUENCY_INTERVAL_NAME);
                Boolean interrupt = (Boolean) JCTreeUtil.getAnnotationParameter(element, Frequency.class, Constants.FREQUENCY_INTERRUPT_NAME, PropertiesUtil.getFrequencyInterrupt());
                JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.of(treeMaker.Literal(JCTreeUtil.getPackageName(element) + "." + element.toString()), treeMaker.Literal(interval), treeMaker.Literal(interrupt)));
                statements.append(treeMaker.Exec(methodInvocation));

                for (int i = 0; i < tree.getStatements().size(); i++) {
                    statements.append(tree.getStatements().get(i));
                }

                result = treeMaker.Block(0, statements.toList());
            }
        });
    }

}
