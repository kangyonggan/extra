package com.kangyonggan.extra.core.processor;

import com.kangyonggan.extra.core.model.Constants;
import com.kangyonggan.extra.core.util.JCTreeUtil;
import com.kangyonggan.extra.core.util.KeyExpressionUtil;
import com.kangyonggan.extra.core.util.PropertiesUtil;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * annotation processor
 *
 * @author kangyonggan
 * @since 10/31/17
 */
@SupportedAnnotationTypes({
        "com.kangyonggan.extra.core.annotation.Count",
        "com.kangyonggan.extra.core.annotation.Frequency",
        "com.kangyonggan.extra.core.annotation.Cache",
        "com.kangyonggan.extra.core.annotation.CacheDel",
        "com.kangyonggan.extra.core.annotation.Log"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ExtraProcessor extends AbstractProcessor {

    /**
     * @param env
     */
    @Override
    public synchronized void init(ProcessingEnvironment env) {
        PropertiesUtil.init(Constants.PROPERTIES_NAME);
        JCTreeUtil.init(env);
        KeyExpressionUtil.init(env);
    }

    /**
     * @param annotations
     * @param env
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        // 这些processor的顺序不可变
        CountProcessor.process(annotations, env);
        FrequencyProcessor.process(annotations, env);
        CacheProcessor.process(annotations, env);
        CacheDelProcessor.process(annotations, env);
        LogProcessor.process(annotations, env);
        return true;
    }

}
