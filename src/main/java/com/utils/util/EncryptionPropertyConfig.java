package com.utils.util;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * properties 属性解密工具配置 使用第三方jasypt-spring-boot 实现
 *
 * 先使用EncryptionPropertyConfig.stringEncryptor返回StringEncryptor
 * 使用StringEncryptor.encrypt("password")对password加密 使用ENC(密文)的格式替换properties中敏感信息
 * 如：spring.datasource.password=ENC(B6WtfUYKq0qi84iuFcjExg==)
 *
 * 读取时不用处理，会自动对properties中的值ENC(密文)进行自动解密返回原文
 *
 * @author zhaofeng
 * @date 2019-09-20
 */
@Configuration
public class EncryptionPropertyConfig {

    private static final String JASYPT = "==ohixkKDFMi27485yk1mfrewaifdfgi57803==";


    /**
     * 重写这个方法的目的，是把key写死，不用配置了
     *
     * @return
     */
    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(JASYPT);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName(null);
        config.setProviderClassName(null);
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }

}
