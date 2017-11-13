package com.kangyonggan.extra.core.processor;

import com.kangyonggan.extra.core.annotation.Valid;
import com.kangyonggan.extra.core.model.Constants;
import com.kangyonggan.extra.core.util.JCTreeUtil;
import com.kangyonggan.extra.core.util.PropertiesUtil;
import com.kangyonggan.extra.core.util.ValidUtil;
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
public class ValidProcess {

    public static void process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(Valid.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                JCTreeUtil.importPackage(element, ValidUtil.class.getName());

                generateBlockCode(element);
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
                 * create code: ValidUtil.valid(interrupt, handlePackage, args);
                 */
                JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(ValidUtil.class.getSimpleName())), names.fromString(Constants.METHOD_VALID));
                Boolean interrupt = (Boolean) JCTreeUtil.getAnnotationParameter(element, Valid.class, Constants.VALID_INTERRUPT_NAME, PropertiesUtil.getValidInterrupt());
                String handlePackageName = (String) JCTreeUtil.getAnnotationParameter(element, Valid.class, Constants.VALID_HANDLE_NAME, PropertiesUtil.getValidHandle());

                ListBuffer<JCTree.JCExpression> validArgs = new ListBuffer();
                validArgs.append(treeMaker.Literal(interrupt));
                validArgs.append(treeMaker.Literal(handlePackageName));

                List<JCTree.JCExpression> args = JCTreeUtil.getParameters(element);
                for (JCTree.JCExpression arg : args) {
                    validArgs.append(arg);
                }
                JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, validArgs.toList());
                statements.append(treeMaker.Exec(methodInvocation));

                for (int i = 0; i < tree.getStatements().size(); i++) {
                    statements.append(tree.getStatements().get(i));
                }

                result = treeMaker.Block(0, statements.toList());
            }

        });
    }


}
