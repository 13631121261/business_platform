package com.kunlun.firmwaresystem.interceptor;

import org.apache.http.impl.client.RequestWrapper;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;


public class SignStreamFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String contentType = req.getContentType();
        String method = "multipart/form-data";

        if (contentType != null && contentType.contains(method)) {
            // 将转化后的 request 放入过滤链中
                         // CommonsMultipartResolver
          //  myPrintln("这里是文件");
            request = new StandardServletMultipartResolver().resolveMultipart(request);
        }
        try {
            // 扩展request，使其能够能够重复读取requestBody
            ServletRequest requestWrapper = new HttpServletRequestWrapper(request);
            // 这里需要放行，但是要注意放行的 request是requestWrapper
            chain.doFilter(requestWrapper, resp);
        }catch (Exception e){

        }

    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

}
