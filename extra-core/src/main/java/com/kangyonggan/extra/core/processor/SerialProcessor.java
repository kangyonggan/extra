package com.kangyonggan.extra.core.processor;

import com.kangyonggan.extra.core.annotation.Serial;
import com.kangyonggan.extra.core.util.JCTreeUtil;
import com.kangyonggan.extra.core.util.Sequence;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.ListBuffer;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import static com.kangyonggan.extra.core.util.JCTreeUtil.*;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class SerialProcessor {

    private static final String VAR_NAME = "serialVersionUID";
    private static Sequence sequence = new Sequence();

    public static void process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(Serial.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                JCTree tree = (JCTree) trees.getTree(element);

                tree.accept(new TreeTranslator() {
                    @Override
                    public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                        List<JCTree.JCAnnotation> annotations = jcClassDecl.mods.annotations;
                        JCTree.JCAnnotation serialAnno = null;
                        for (JCTree.JCAnnotation anno : annotations) {
                            if (anno.type.toString().equals(Serial.class.getName())) {
                                serialAnno = anno;
                                break;
                            }
                        }

                        if (serialAnno != null) {
                            List<JCTree.JCExpression> impls = jcClassDecl.implementing;
                            if (!hasSerializableImpl(impls)) {
                                ListBuffer<JCTree.JCExpression> statements = new ListBuffer();
                                for (JCTree.JCExpression impl : impls) {
                                    statements.append(impl);
                                }

                                Symbol.ClassSymbol sym = new Symbol.ClassSymbol(sequence.nextId(), names.fromString(Serializable.class.getSimpleName()), null);
                                JCTree.JCIdent ident = treeMaker.Ident(sym);
                                statements.append(ident);

                                JCTreeUtil.importPackage4Class(element, Serializable.class.getName());
                                jcClassDecl.implementing = statements.toList();
                            }

                            // private static final long serialVersionUID = xxxL;
                            if (!JCTreeUtil.hasVariable(jcClassDecl.defs, "long", VAR_NAME)) {
                                ListBuffer<JCTree> statements = new ListBuffer();

                                int modifiers = Flags.PRIVATE | Flags.STATIC | Flags.FINAL;
                                JCTree.JCPrimitiveTypeTree typeExpr = treeMaker.TypeIdent(TypeTag.LONG);
                                JCTree.JCExpression value = treeMaker.Literal(sequence.nextId());
                                JCTree.JCVariableDecl variableDecl = treeMaker.VarDef(treeMaker.Modifiers(modifiers), names.fromString(VAR_NAME), typeExpr, value);
                                statements.append(variableDecl);

                                for (JCTree jcTree : jcClassDecl.defs) {
                                    statements.append(jcTree);
                                }

                                jcClassDecl.defs = statements.toList();
                            }
                        }
                        super.visitClassDef(jcClassDecl);
                    }
                });
            }
        }
    }

    private static boolean hasSerializableImpl(List<JCTree.JCExpression> impls) {
        for (JCTree.JCExpression impl : impls) {
            if (impl.type.toString().equals(Serializable.class.getName())) {
                return true;
            }
        }

        return false;
    }

}
