package com.kangyonggan.extra.processor;

import com.kangyonggan.extra.annotation.CacheDel;
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

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class CacheDelProcessor {

    public static void process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(CacheDel.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                String handlePackageName = (String) JCTreeUtil.getAnnotationParameter(element, CacheDel.class, Constants.CACHE_HANDLE_NAME, PropertiesUtil.getCacheHandle());
                JCTreeUtil.importPackage(element, handlePackageName);

                String className = handlePackageName.substring(handlePackageName.lastIndexOf(".") + 1);
                JCTreeUtil.defineVariable(element, className, List.nil());
;
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
                 * create code: xxxHandle.delete(key);
                 */
                String keys[] = (String[]) JCTreeUtil.getAnnotationParameter(element, CacheDel.class, Constants.CACHE_KEY_NAME);
                JCTree.JCExpression keyExpr[] = new JCTree.JCExpression[keys.length];
                for (int i = 0; i < keys.length; i++) {
                    keyExpr[i] = KeyExpressionUtil.parse(keys[i]);
                }

                String prefix = (String) JCTreeUtil.getAnnotationParameter(element, CacheDel.class, Constants.CACHE_PREFIX_NAME, PropertiesUtil.getCachePrefix());
                if (StringUtil.isNotEmpty(prefix)) {
                    JCTree.JCExpression prefixExpr = JCTreeUtil.treeMaker.Literal(prefix);
                    for (int i = 0; i < keys.length; i++) {
                        keyExpr[i] = JCTreeUtil.treeMaker.Binary(JCTree.Tag.PLUS, prefixExpr, keyExpr[i]);
                    }
                }


                ListBuffer<JCTree.JCExpression> keyExprList = new ListBuffer();
                for (int i = 0; i < keys.length; i++) {
                    keyExprList.append(keyExpr[i]);
                }

                JCTree.JCFieldAccess fieldAccess = JCTreeUtil.treeMaker.Select(JCTreeUtil.treeMaker.Ident(JCTreeUtil.names.fromString(varName)), JCTreeUtil.names.fromString(Constants.METHOD_DELETE));
                JCTree.JCMethodInvocation methodInvocation = JCTreeUtil.treeMaker.Apply(List.nil(), fieldAccess, keyExprList.toList());
                statements.append(JCTreeUtil.treeMaker.Exec(methodInvocation));

                for (JCTree.JCStatement jcStatement : tree.getStatements()) {
                    statements.append(jcStatement);
                }

                result = JCTreeUtil.treeMaker.Block(0, statements.toList());
            }
        });
    }

}
