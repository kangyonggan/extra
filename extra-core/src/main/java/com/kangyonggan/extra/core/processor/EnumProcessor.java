package com.kangyonggan.extra.core.processor;

import com.kangyonggan.extra.core.annotation.Enum;
import com.kangyonggan.extra.core.handle.impl.EnumHandle;
import com.kangyonggan.extra.core.model.Constants;
import com.kangyonggan.extra.core.util.JCTreeUtil;
import com.kangyonggan.extra.core.util.PropertiesUtil;
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
public class EnumProcessor {

    public static void process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(Enum.class)) {
            if (element.getKind() == ElementKind.ENUM) {
                JCTreeUtil.importPackage4Enum(element, EnumHandle.class.getName());
                generateBlockCode(element);
            }
        }
    }

    /**
     * @param element
     */
    private static void generateBlockCode(Element element) {
        JCTree tree = (JCTree) trees.getTree(element);

        tree.accept(new TreeTranslator() {
            @Override
            public void visitBlock(JCTree.JCBlock jcBlock) {
                super.visitBlock(jcBlock);
            }

            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                List<JCTree> defs = jcClassDecl.defs;
                String code = (String) JCTreeUtil.getAnnotationParameter(element, Enum.class, Constants.ENUM_CODE_NAME, PropertiesUtil.getEnumCode());
                String name = (String) JCTreeUtil.getAnnotationParameter(element, Enum.class, Constants.ENUM_NAME_NAME, PropertiesUtil.getEnumName());

                for (int i = 0; i < defs.length(); i++) {
                    JCTree jcTree = defs.get(i);
                    if (jcTree instanceof JCTree.JCMethodDecl) {
                        JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) jcTree;
                        if ("<init>".equals(methodDecl.name.toString())) {
                            ListBuffer<JCTree.JCStatement> statements = new ListBuffer();
                            for (JCTree.JCStatement statement : methodDecl.body.getStatements()) {
                                statements.append(statement);
                            }

                            JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(EnumHandle.class.getSimpleName())), names.fromString(""));
                            JCTree.JCExpression codeExpr = treeMaker.Literal(code);
                            JCTree.JCExpression nameExpr = treeMaker.Literal(name);
                            JCTree.JCExpression classExpr = treeMaker.Literal(element.toString());
                            JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.of(codeExpr, nameExpr, classExpr));
                            JCTree.JCExpressionStatement codeStat = treeMaker.Exec(methodInvocation);

                            statements.append(codeStat);
                            methodDecl.body.stats = statements.toList();
                        }
                    }
                }

                super.visitClassDef(jcClassDecl);
            }
        });
    }


}
