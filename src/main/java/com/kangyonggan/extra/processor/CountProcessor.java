package com.kangyonggan.extra.processor;

import com.kangyonggan.extra.annotation.Count;
import com.kangyonggan.extra.model.Constants;
import com.kangyonggan.extra.util.JCTreeUtil;
import com.kangyonggan.extra.util.PropertiesUtil;
import com.kangyonggan.extra.util.StringUtil;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

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
                 * create code: Boolean _isLimited = _memoryCountHandle.limit(key, interval, count, interrupt);
                 */
                JCTree.JCExpression typeExpr = JCTreeUtil.treeMaker.Ident(JCTreeUtil.names.fromString("Boolean"));
                JCTree.JCFieldAccess fieldAccess = JCTreeUtil.treeMaker.Select(JCTreeUtil.treeMaker.Ident(JCTreeUtil.names.fromString(varName)), JCTreeUtil.names.fromString(Constants.METHOD_LIMIT));
                Long interval = (Long) JCTreeUtil.getAnnotationParameter(element, Count.class, Constants.COUNT_INTERVAL_NAME);
                Integer count = (Integer) JCTreeUtil.getAnnotationParameter(element, Count.class, Constants.COUNT_COUNT_NAME);
                Boolean interrupt = (Boolean) JCTreeUtil.getAnnotationParameter(element, Count.class, Constants.COUNT_INTERRUPT_NAME, PropertiesUtil.getCountInterrupt());
                JCTree.JCMethodInvocation methodInvocation = JCTreeUtil.treeMaker.Apply(List.nil(), fieldAccess, List.of(JCTreeUtil.treeMaker.Literal(JCTreeUtil.getPackageName(element) + "." + element.toString()), JCTreeUtil.treeMaker.Literal(interval), JCTreeUtil.treeMaker.Literal(count), JCTreeUtil.treeMaker.Literal(interrupt)));
                Name isLimit = JCTreeUtil.names.fromString(Constants.VARIABLE_PREFIX + "isLimited");
                JCTree.JCVariableDecl variableDecl = JCTreeUtil.treeMaker.VarDef(JCTreeUtil.treeMaker.Modifiers(0), isLimit, typeExpr, methodInvocation);
                statements.append(variableDecl);

                if (interrupt) {
                    /**
                     * create code: if (_isLimited) { return returnValue; }
                     */
                    JCTree.JCParens condition = JCTreeUtil.treeMaker.Parens(JCTreeUtil.treeMaker.Ident(isLimit));
                    JCTree.JCExpression returnType = JCTreeUtil.getReturnType(element);
                    JCTree.JCStatement statementTrue = JCTreeUtil.treeMaker.Return(null);
                    if (returnType != null && !returnType.toString().equals(Constants.RETURN_VOID)) {
                        statementTrue = JCTreeUtil.treeMaker.Return(JCTreeUtil.treeMaker.TypeCast(returnType, JCTreeUtil.getTypeDefaultValue(returnType)));
                    }
                    JCTree.JCIf jcIf = JCTreeUtil.treeMaker.If(condition, statementTrue, null);
                    statements.append(jcIf);
                }

                for (int i = 0; i < tree.getStatements().size(); i++) {
                    statements.append(tree.getStatements().get(i));
                }

                result = JCTreeUtil.treeMaker.Block(0, statements.toList());
            }
        });
    }

}
