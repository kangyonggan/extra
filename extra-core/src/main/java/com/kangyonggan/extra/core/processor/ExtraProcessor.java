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
        "com.kangyonggan.extra.core.annotation.Valid",
        "com.kangyonggan.extra.core.annotation.Count",
        "com.kangyonggan.extra.core.annotation.Frequency",
        "com.kangyonggan.extra.core.annotation.Cache",
        "com.kangyonggan.extra.core.annotation.CacheDel",
        "com.kangyonggan.extra.core.annotation.Log",
        "com.kangyonggan.extra.core.annotation.Monitor",
        "com.kangyonggan.extra.core.annotation.Enum"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ExtraProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        PropertiesUtil.init(Constants.PROPERTIES_NAME);
        JCTreeUtil.init(env);
        KeyExpressionUtil.init(env);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        // 这些processor的顺序不可变
        if (PropertiesUtil.isValidOpen()) {
            ValidProcess.process(annotations, env);
        }
        if (PropertiesUtil.isCountOpen()) {
            CountProcessor.process(annotations, env);
        }
        if (PropertiesUtil.isFrequencyOpen()) {
            FrequencyProcessor.process(annotations, env);
        }
        if (PropertiesUtil.isCacheOpen()) {
            CacheProcessor.process(annotations, env);
            CacheDelProcessor.process(annotations, env);
        }
        if (PropertiesUtil.isLogOpen()) {
            LogProcessor.process(annotations, env);
        }
        if (PropertiesUtil.isMonitorOpen()) {
            MonitorProcessor.process(annotations, env);
        }
        if (PropertiesUtil.isEnumOpen()) {
            EnumProcessor.process(annotations, env);
        }
        SerialProcessor.process(annotations, env);
        return true;
    }

}
