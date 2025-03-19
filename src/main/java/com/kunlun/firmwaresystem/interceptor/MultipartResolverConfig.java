package com.kunlun.firmwaresystem.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.nio.charset.StandardCharsets;
//@Configuration
public class MultipartResolverConfig {
    /*@Bean(name ="multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        myPrintln("这生效");
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding(StandardCharsets.UTF_8.name());
        multipartResolver.setMaxUploadSize(2097152);//314572800
        multipartResolver.setMaxUploadSizePerFile(2097152);//104857600
        multipartResolver.setMaxInMemorySize(2048);

        return multipartResolver;
    }*/

}
