package com.kunlun.firmwaresystem.interceptor;



import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class HttpServletRequestReplacedFilter implements Filter {
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {


       // myPrintln(request.getContentType()+request.getClass().getName());
        ServletRequest requestWrapper = null;
        if(request instanceof HttpServletRequest ){
           // myPrintln("普通数据请求");
            requestWrapper = new RequestReaderHttpServletRequestWrapper((HttpServletRequest ) request);
        }
        /*if(request.getContentType().contains("multipart/form-data")) {
            myPrintln("上传文件数据请求");
            requestWrapper = new RequestReaderHttpServletRequestWrapper((MultipartHttpServletRequest) request);
        }else if(request instanceof HttpServletRequest ){
            myPrintln("普通数据请求");
            requestWrapper = new RequestReaderHttpServletRequestWrapper((HttpServletRequest ) request);
        }else{
            myPrintln("都不是");
        }*/
      //  myPrintln("取出数据流"+response);
        //获取请求中的流如何，将取出来的字符串，再次转换成流，然后把它放入到新request对象中。
        // 在chain.doFiler方法中传递新的request对象
        if(requestWrapper == null) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }
    /*@Bean(name ="multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding(StandardCharsets.UTF_8.name());
        multipartResolver.setMaxUploadSize(2097152);//314572800
        multipartResolver.setMaxUploadSizePerFile(2097152);//104857600
        multipartResolver.setMaxInMemorySize(2048);

        return multipartResolver;
    }*/

/*
    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if(request instanceof HttpServletRequest) {
            requestWrapper = new RequestReaderHttpServletRequestWrapper((HttpServletRequest) request);
        }
        //获取请求中的流如何，将取出来的字符串，再次转换成流，然后把它放入到新request对象中。
        // 在chain.doFiler方法中传递新的request对象
        if(requestWrapper == null) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }
    @Bean(name ="multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding(StandardCharsets.UTF_8.name());
        multipartResolver.setMaxUploadSize(-1);
        return multipartResolver;
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }*/
}