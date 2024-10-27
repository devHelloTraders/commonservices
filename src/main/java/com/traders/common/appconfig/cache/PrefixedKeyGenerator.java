//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.traders.common.appconfig.cache;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.interceptor.KeyGenerator;

public class PrefixedKeyGenerator implements KeyGenerator {
    private final String prefix;
    private String shortCommitId = null;
    private Instant time = null;
    private String version = null;

    public PrefixedKeyGenerator(GitProperties gitProperties, BuildProperties buildProperties) {
        this.prefix = this.generatePrefix(gitProperties, buildProperties);
    }

    String getPrefix() {
        return this.prefix;
    }

    private String generatePrefix(GitProperties gitProperties, BuildProperties buildProperties) {
        if (Objects.nonNull(gitProperties)) {
            this.shortCommitId = gitProperties.getShortCommitId();
        }

        if (Objects.nonNull(buildProperties)) {
            this.time = buildProperties.getTime();
            this.version = buildProperties.getVersion();
        }

        Object p = ObjectUtils.firstNonNull(new Serializable[]{this.shortCommitId, this.time, this.version, RandomStringUtils.randomAlphanumeric(12)});
        return p instanceof Instant ? DateTimeFormatter.ISO_INSTANT.format((Instant)p) : p.toString();
    }

    public Object generate(Object target, Method method, Object... params) {
        return new PrefixedSimpleKey(this.prefix, method.getName(), params);
    }
}
