package com.kangyonggan.extra.core.processor;

import com.kangyonggan.extra.core.annotation.Enum;
import com.kangyonggan.extra.core.annotation.Handle;
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
 * @since 3/30/18
 */
public class EnumProcessor {

    public static void process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Element handleElement = null;
        for (Element element : env.getElementsAnnotatedWith(Handle.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                String type = JCTreeUtil.getAnnotationParameter(element, Handle.class, "type").toString();
                if (type.equals(Handle.Type.ENUM.name())) {
                    handleElement = element;
                    break;
                }
            }
        }
        if (handleElement == null) {
            return;
        }
        for (Element element : env.getElementsAnnotatedWith(Enum.class)) {
            if (element.getKind() == ElementKind.ENUM) {
                generateBlockCode(element, handleElement);
            }
        }
    }

    /**
     * @param element
     * @param handleElement
     */
    private static void generateBlockCode(Element element, Element handleElement) {
        JCTree tree = (JCTree) trees.getTree(handleElement);

        tree.accept(new TreeTranslator() {
            @Override
            public void visitBlock(JCTree.JCBlock jcBlock) {
                super.visitBlock(jcBlock);
            }

            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                List<JCTree> defs = jcClassDecl.defs;

                boolean hasStatic = false;
                for (int i = 0; i < defs.length(); i++) {
                    JCTree jcTree = defs.get(i);
                    if (jcTree instanceof JCTree.JCBlock) {
                        JCTree.JCBlock block = (JCTree.JCBlock) jcTree;
                        if (block.flags == 8) {
                            hasStatic = true;
                            ListBuffer<JCTree.JCStatement> statements = new ListBuffer();
                            for (JCTree.JCStatement statement : block.getStatements()) {
                                statements.append(statement);
                            }

                            statements.append(generateCode(element, handleElement.getSimpleName().toString()));
                            block.stats = statements.toList();
                        }
                    }
                }

                if (!hasStatic) {
                    ListBuffer<JCTree> statements = new ListBuffer();
                    for (JCTree statement : jcClassDecl.defs) {
                        statements.append(statement);
                    }

                    ListBuffer<JCTree.JCStatement> staticStatments = new ListBuffer<>();
                    staticStatments.append(generateCode(element, handleElement.getSimpleName().toString()));

                    JCTree.JCBlock block = treeMaker.Block(8, staticStatments.toList());
                    statements.append(block);
                    jcClassDecl.defs = statements.toList();
                }

                super.visitClassDef(jcClassDecl);
            }
        });
    }

    /**
     * generate code statment
     *
     * @param element
     * @param handleName
     * @return
     */
    private static JCTree.JCExpressionStatement generateCode(Element element, String handleName) {
        String key = (String) JCTreeUtil.getAnnotationParameter(element, Enum.class, Constants.ENUM_KEY_NAME);
        String code = (String) JCTreeUtil.getAnnotationParameter(element, Enum.class, Constants.ENUM_CODE_NAME, PropertiesUtil.getEnumCode());
        String name = (String) JCTreeUtil.getAnnotationParameter(element, Enum.class, Constants.ENUM_NAME_NAME, PropertiesUtil.getEnumName());
        String clazz = element.toString();
        if (StringUtil.isEmpty(key)) {
            key = StringUtil.firstToLowerCase(element.getSimpleName().toString());
        }

        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(handleName)), names.fromString("collectionEnumInfo"));
        JCTree.JCExpression keyExpr = treeMaker.Literal(key);
        JCTree.JCExpression codeExpr = treeMaker.Literal(code);
        JCTree.JCExpression nameExpr = treeMaker.Literal(name);
        JCTree.JCExpression classExpr = treeMaker.Literal(clazz);

        JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, List.of(keyExpr, codeExpr, nameExpr, classExpr));
        JCTree.JCExpressionStatement codeStat = treeMaker.Exec(methodInvocation);

        return codeStat;
    }

}
