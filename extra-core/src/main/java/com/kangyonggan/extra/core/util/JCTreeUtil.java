package com.kangyonggan.extra.core.util;

import com.kangyonggan.extra.core.model.Constants;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class JCTreeUtil {

    public static Trees trees;
    public static TreeMaker treeMaker;
    public static Name.Table names;

    public static void init(ProcessingEnvironment env) {
        trees = Trees.instance(env);
        Context context = ((JavacProcessingEnvironment) env).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context).table;
    }

    public static JCTree.JCExpression getReturnType(Element element) {
        final JCTree.JCExpression[] returnType = new JCTree.JCExpression[1];

        JCTree tree = (JCTree) trees.getTree(element);
        tree.accept(new TreeTranslator() {
            @Override
            public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                if (jcMethodDecl.sym != null && element.toString().equals(jcMethodDecl.sym.toString())) {
                    returnType[0] = jcMethodDecl.restype;
                }
                super.visitMethodDef(jcMethodDecl);
            }
        });

        return returnType[0];
    }

    public static Object getAnnotationParameter(Element element, Class annoClass, String name) {
        return getAnnotationParameter(element, annoClass, name, StringUtil.EXPTY);
    }

    public static Object getAnnotationParameter(Element element, Class annoClass, String name, Object defaultValue) {
        AnnotationMirror annotationMirror = JCTreeUtil.getAnnotationMirror(element, annoClass.getName());
        if (annotationMirror == null) {
            return defaultValue;
        }

        for (ExecutableElement ee : annotationMirror.getElementValues().keySet()) {
            if (ee.getSimpleName().toString().equals(name)) {
                Object value = annotationMirror.getElementValues().get(ee).getValue();
                if (value instanceof List) {// array
                    List list = (List) value;
                    String result[] = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        Attribute.Constant constant = (Attribute.Constant) list.get(i);
                        // this value type possible not String
                        result[i] = (String) constant.getValue();
                    }
                    return result;
                } else if (value instanceof com.sun.tools.javac.code.Type.ClassType) {
                    return value.toString();
                }

                return value;
            }
        }

        return defaultValue;
    }

    public static AnnotationMirror getAnnotationMirror(Element element, String name) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (name.equals(annotationMirror.getAnnotationType().toString())) {
                return annotationMirror;
            }
        }

        return null;
    }

    public static void importPackage(Element element, String packageName) {
        JCTree.JCCompilationUnit compilationUnit = (JCTree.JCCompilationUnit) trees.getPath(element.getEnclosingElement()).getCompilationUnit();
        String className = packageName.substring(packageName.lastIndexOf(".") + 1);
        packageName = packageName.substring(0, packageName.lastIndexOf("."));

        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(packageName)), names.fromString(className));
        JCTree.JCImport jcImport = treeMaker.Import(fieldAccess, false);

        ListBuffer<JCTree> imports = new ListBuffer();
        imports.append(jcImport);

        for (int i = 0; i < compilationUnit.defs.size(); i++) {
            imports.append(compilationUnit.defs.get(i));
        }

        compilationUnit.defs = imports.toList();
    }

    public static void importPackage4Class(Element element, String packageName) {
        JCTree.JCCompilationUnit compilationUnit = (JCTree.JCCompilationUnit) trees.getPath(element).getCompilationUnit();
        String className = packageName.substring(packageName.lastIndexOf(".") + 1);
        packageName = packageName.substring(0, packageName.lastIndexOf("."));

        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(packageName)), names.fromString(className));
        JCTree.JCImport jcImport = treeMaker.Import(fieldAccess, false);

        ListBuffer<JCTree> imports = new ListBuffer();
        imports.append(jcImport);

        for (int i = 0; i < compilationUnit.defs.size(); i++) {
            imports.append(compilationUnit.defs.get(i));
        }

        compilationUnit.defs = imports.toList();
    }

    public static void defineVariable(Element element, String className, List<JCTree.JCExpression> args) {
        JCTree tree = (JCTree) trees.getTree(element.getEnclosingElement());
        tree.accept(new TreeTranslator() {
            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                String varName = Constants.VARIABLE_PREFIX + StringUtil.firstToLowerCase(className);
                boolean hasVariable = hasVariable(translate(jcClassDecl.defs), className, varName);

                ListBuffer<JCTree> statements = new ListBuffer();
                for (JCTree jcTree : jcClassDecl.defs) {
                    statements.append(jcTree);
                }

                if (!hasVariable) {
                    JCTree.JCExpression typeExpr = treeMaker.Ident(names.fromString(className));
                    JCTree.JCNewClass newClassExpr = treeMaker.NewClass(null, List.nil(), typeExpr, args, null);

                    // not inner class, variable is static
                    int modifiers = Flags.PRIVATE;
                    if (jcClassDecl.sym != null) {
                        if (jcClassDecl.sym.flatname.toString().equals(jcClassDecl.sym.fullname.toString())) {
                            modifiers = modifiers | Flags.STATIC;
                        }

                        JCTree.JCVariableDecl variableDecl = treeMaker.VarDef(treeMaker.Modifiers(modifiers), names.fromString(varName), typeExpr, newClassExpr);
                        statements.append(variableDecl);
                        jcClassDecl.defs = statements.toList();
                    }
                }

                super.visitClassDef(jcClassDecl);
            }
        });
    }

    public static boolean hasVariable(List<JCTree> oldList, String className, String varName) {
        boolean hasField = false;

        for (JCTree jcTree : oldList) {
            if (jcTree.getKind() == Tree.Kind.VARIABLE) {
                JCTree.JCVariableDecl variableDecl = (JCTree.JCVariableDecl) jcTree;

                if (varName.equals(variableDecl.name.toString()) && className.equals(variableDecl.vartype.toString())) {
                    hasField = true;
                    break;
                }
            }
        }

        return hasField;
    }

    public static JCTree.JCLiteral getNull() {
        return treeMaker.Literal(TypeTag.BOT, null);
    }

    public static JCTree.JCVariableDecl callMethodWithReturn(String varType, String varName, String targetVarName, String methodName, List args) {
        JCTree.JCIdent varIdent = treeMaker.Ident(names.fromString(varName));
        JCTree.JCExpression typeExpr = treeMaker.Ident(names.fromString(varType));
        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(varIdent, names.fromString(methodName));

        JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), fieldAccess, args);
        return treeMaker.VarDef(treeMaker.Modifiers(0), names.fromString(targetVarName), typeExpr, methodInvocation);
    }

    public static JCTree.JCExpression notNull(String varName) {
        return treeMaker.Binary(JCTree.Tag.NE, treeMaker.Ident(names.fromString(varName)), treeMaker.Literal(TypeTag.BOT, null));
    }

    public static List<JCTree.JCExpression> getParameters(Element element) {
        final List<JCTree.JCExpression>[] params = new List[]{List.nil()};
        JCTree tree = (JCTree) trees.getTree(element);

        tree.accept(new TreeTranslator() {
            @Override
            public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                for (JCTree.JCVariableDecl decl : jcMethodDecl.getParameters()) {
                    params[0] = params[0].append(treeMaker.Ident(decl));
                }
                super.visitMethodDef(jcMethodDecl);
            }
        });

        return params[0];
    }

    public static JCTree.JCLiteral getMethodName(Element element) {
        final JCTree.JCLiteral[] methodName = {null};
        JCTree tree = (JCTree) trees.getTree(element);

        tree.accept(new TreeTranslator() {
            @Override
            public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                if (methodName[0] == null) {
                    methodName[0] = treeMaker.Literal(jcMethodDecl.getName().toString());
                }
                super.visitMethodDef(jcMethodDecl);
            }
        });

        return methodName[0];
    }

    public static String getPackageName(Element element) {
        return ((JCTree.JCClassDecl) trees.getTree(element.getEnclosingElement())).sym.toString();
    }

    public static JCTree.JCExpression getTypeDefaultValue(JCTree.JCExpression type) {
        String tp = type.toString();
        if (tp.equals("byte")) {
            return treeMaker.Literal(0);
        } else if (tp.equals("short")) {
            return treeMaker.Literal(0);
        } else if (tp.equals("int")) {
            return treeMaker.Literal(0);
        } else if (tp.equals("long")) {
            return treeMaker.Literal(0);
        } else if (tp.equals("float")) {
            return treeMaker.Literal(0);
        } else if (tp.equals("double")) {
            return treeMaker.Literal(0);
        } else if (tp.equals("char")) {
            return treeMaker.Literal(0);
        } else if (tp.equals("boolean")) {
            return treeMaker.Literal(false);
        }
        return getNull();
    }
}
