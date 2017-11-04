package com.kangyonggan.extra.core.util;

import com.kangyonggan.jcel.JCExpressionParser;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * @author kangyonggan
 * @since 2017/11/4 0004
 */
public class KeyExpressionUtil {

    private static JCExpressionParser parser;

    public static void init(ProcessingEnvironment env) {
        Context context = ((JavacProcessingEnvironment) env).getContext();
        parser = new JCExpressionParser(TreeMaker.instance(context), Names.instance(context).table);
    }

    public static JCTree.JCExpression parse(String kery) {
        return parser.parse(kery);
    }

}
