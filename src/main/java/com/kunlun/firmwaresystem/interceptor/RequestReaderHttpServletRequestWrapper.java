package com.kunlun.firmwaresystem.interceptor;


import java.io.*;
import java.nio.charset.Charset;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestReaderHttpServletRequestWrapper extends HttpServletRequestWrapper{
/*

    private final byte[] body;

    public RequestReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = HttpHelper.getBodyString(request).getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream bais = new ByteArrayInputStream(body);

        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }

}*/
    /**
     * 存储body数据的容器
     */
    private final byte[] body;


    public  RequestReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);


        // 将body数据存储起来
        String bodyStr = getBodyString(request);
        body = bodyStr.getBytes(Charset.defaultCharset());
    }


    /**
     * 获取请求Body
     *
     * @param request request
     * @return String
     */
    public String getBodyString(final ServletRequest request) throws IOException {
        return inputStream2String(request.getInputStream());
    }


    /**
     * 获取请求Body
     *
     * @return String
     */
    public String getBodyString() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(body);


        return inputStream2String(inputStream);
    }


    /**
     * 将inputStream里的数据读取出来并转换成字符串
     *
     * @param inputStream inputStream
     * @return String
     */
    private String inputStream2String(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;


        reader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }


    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }


    @Override
    public ServletInputStream getInputStream() throws IOException {


        final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);


        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return inputStream.read();
            }


            @Override
            public boolean isFinished() {
                return false;
            }


            @Override
            public boolean isReady() {
                return false;
            }


            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }
}

