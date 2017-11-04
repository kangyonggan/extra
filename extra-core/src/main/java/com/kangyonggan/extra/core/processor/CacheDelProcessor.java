package com.kangyonggan.extra.core.processor;

import com.kangyonggan.extra.core.annotation.Cache;
import com.kangyonggan.extra.core.annotation.CacheDel;
import com.kangyonggan.extra.core.model.Constants;
import com.kangyonggan.extra.core.util.JCTreeUtil;
import com.kangyonggan.extra.core.util.KeyExpressionUtil;
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

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class CacheDelProcessor {

    public static void process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(CacheDel.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                String handlePackageName = JCTreeUtil.getAnnotationParameter(element, Cache.class, Constants.CACHE_HANDLE_NAME, PropertiesUtil.getCacheHandle());
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
                String key = JCTreeUtil.getAnnotationParameter(element, CacheDel.class, Constants.CACHE_KEY_NAME);
                JCTree.JCExpression keyExpr = KeyExpressionUtil.parse(key);

                String prefix = JCTreeUtil.getAnnotationParameter(element, CacheDel.class, Constants.CACHE_PREFIX_NAME, PropertiesUtil.getCachePrefix());
                if (StringUtil.isNotEmpty(prefix)) {
                    JCTree.JCExpression prefixExpr = JCTreeUtil.treeMaker.Literal(prefix);
                    keyExpr = JCTreeUtil.treeMaker.Binary(JCTree.Tag.PLUS, prefixExpr, keyExpr);
                }

                JCTree.JCFieldAccess fieldAccess = JCTreeUtil.treeMaker.Select(JCTreeUtil.treeMaker.Ident(JCTreeUtil.names.fromString(varName)), JCTreeUtil.names.fromString(Constants.METHOD_DELETE));
                JCTree.JCMethodInvocation methodInvocation = JCTreeUtil.treeMaker.Apply(List.nil(), fieldAccess, List.of(keyExpr));
                statements.append(JCTreeUtil.treeMaker.Exec(methodInvocation));

                for (JCTree.JCStatement jcStatement : tree.getStatements()) {
                    statements.append(jcStatement);
                }

                result = JCTreeUtil.treeMaker.Block(0, statements.toList());
            }
        });
    }

}
