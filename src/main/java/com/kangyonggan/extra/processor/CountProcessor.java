package com.kangyonggan.extra.processor;

import com.kangyonggan.extra.annotation.Count;
import com.kangyonggan.extra.exception.MethodCalledOutOfCountException;
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

import static com.kangyonggan.extra.util.JCTreeUtil.*;

/**
 * @author kangyonggan
 * @since 11/6/17
 */
public class CountProcessor {

    public static void process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(Count.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                String handlePackageName = (String) JCTreeUtil.getAnnotationParameter(element, Count.class, Constants.COUNT_HANDLE_NAME, PropertiesUtil.getCountHandle());
                JCTreeUtil.importPackage(element, handlePackageName);
                JCTreeUtil.importPackage(element, MethodCalledOutOfCountException.class.getName());

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
        JCTree tree = (JCTree) trees.getTree(element);

        tree.accept(new TreeTranslator() {
            @Override
            public void visitBlock(JCTree.JCBlock tree) {
                ListBuffer<JCTree.JCStatement> statements = new ListBuffer();

                /**
                 * create code: _memoryCountHandle.limit(key, interval, count, interrupt);
                 */
                JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(varName)), names.fromString(Constants.METHOD_LIMIT));
                String prefix = (String) JCTreeUtil.getAnnotationParameter(element, Count.class, Constants.COUNT_PREFIX_NAME, PropertiesUtil.getCountPrefix());
                String key = (String) JCTreeUtil.getAnnotationParameter(element, Count.class, Constants.COUNT_KEY_NAME, StringUtil.EXPTY);
                JCTree.JCExpression keyExpr;
                if (StringUtil.isEmpty(key)) {
                    keyExpr = treeMaker.Literal(prefix + JCTreeUtil.getPackageName(element) + "." + element.toString());
                } else {
                    keyExpr = KeyExpressionUtil.parse(prefix + key);
                }

                Long interval = (Long) JCTreeUtil.getAnnotationParameter(element, Count.class, Constants.COUNT_INTERVAL_NAME);
                Integer count = (Integer) JCTreeUtil.getAnnotationParameter(element, Count.class, Constants.COUNT_COUNT_NAME);
                Boolean interrupt = (Boolean) JCTreeUtil.getAnnotationParameter(element, Count.class, Constants.COUNT_INTERRUPT_NAME, PropertiesUtil.getCountInterrupt());
                JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.of(keyExpr, treeMaker.Literal(interval), treeMaker.Literal(count), treeMaker.Literal(interrupt)));
                statements.append(treeMaker.Exec(methodInvocation));

                for (int i = 0; i < tree.getStatements().size(); i++) {
                    statements.append(tree.getStatements().get(i));
                }

                result = treeMaker.Block(0, statements.toList());
            }
        });
    }

}
