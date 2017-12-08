package com.hy.util;

import com.alibaba.fastjson.JSON;
import com.hy.base.IBase;
import com.hy.core.ParamsMap;
import com.hy.vo.Protocol;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-17 16:24)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class HttpUtil {
    private static HttpClientBuilder builder;
    private static final Pattern regexp = Pattern.compile("(\\w+)=(\\w+)");

    static {
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        initHttpClient();
    }

    static void initHttpClient() {
        HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
                if (i > 3)                 //请求失败重试次数
                    return false;
                if (e instanceof NoHttpResponseException)
                    return true;
                if (e instanceof UnknownHostException)
                    return false;
                if (e instanceof InterruptedIOException)
                    return false;
                if (e instanceof ConnectException)
                    return false;
                if (e instanceof SSLException)
                    return false;
                if (!new Boolean(String.valueOf(httpContext.getAttribute(HttpCoreContext.HTTP_REQ_SENT))))
                    return true;
                return false;

            }
        };
        SSLConnectionSocketFactory sssf = null;
        try {
            sssf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SSL Connection could not be Initialized,Default SSLContext");
            e.printStackTrace();
        }
        ;
        PoolingHttpClientConnectionManager pcm = new PoolingHttpClientConnectionManager(3000L, TimeUnit.MILLISECONDS);
        pcm.setMaxTotal(20);
        pcm.setDefaultMaxPerRoute(128);
        builder = HttpClients.custom();
        builder.setRetryHandler(retryHandler);
        builder.setConnectionManager(pcm);
        builder.setDefaultHeaders(Arrays.asList(new BasicHeader("Content-Type", "application/json; charset=utf-8"), new BasicHeader("Connection", "keep-alive")));
    }

    public static HttpClient getHttpClient(boolean ssl) {
        SSLContext ctx = null;
        SSLSocketFactory ssf = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] arg0,
                                               String arg1) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] arg0,
                                               String arg1) throws CertificateException {
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            ssf = new SSLSocketFactory(ctx,
                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return ssl ? builder.setSSLSocketFactory(ssf).build() : builder.build();
    }

    public static String execute(Protocol protocol, final Map<String, Object> params) {
        String resultData = "";
        try {
            String url = genGetURL(protocol.getUrl(), params);
            HttpUriRequest request = getRequestForMethod(protocol.getMethod(), url);
            String contentType = protocol.getContentType();
            if (!org.springframework.util.CollectionUtils.isEmpty(protocol.getPostParams())|| !CollectionUtils.isEmpty(params) ) {
//                request = new HttpPost(url);
                if (contentType.equals(Protocol.TEXT) || contentType.equals(Protocol.FORM)) {
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    params.forEach((k, v) -> nameValuePairs.add(new BasicNameValuePair(k, v.toString())));
//                    ((HttpPost)request).setEntity(EntityBuilder.create().setParameters(nameValuePairs).setContentEncoding(IBase.DEF_CHATSET).build());
                    ((HttpPost) request).setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                } else if (contentType.equals(Protocol.JSON)) {
                    ((HttpPost) request).setEntity(new StringEntity(JSON.toJSONString(params), IBase.DEF_CHATSET));
                } else if (contentType.equals(Protocol.BYTE)) {
                    ((HttpPost) request).setEntity(EntityBuilder.create().setBinary((byte[]) params.get("byte")).build());
                }
/*                else if (contentType.equals(Protocol.MULTI)) {
                    File file = (File) params.get("file");
                    ((HttpPost)request).setEntity(MultipartEntityBuilder.create().addBinaryBody("file",file, ContentType.APPLICATION_OCTET_STREAM,file.getName()).build());
                }*/
            }
            Header[] headers = new Header[protocol.getHeads().size() + 1];
            headers[0]=new BasicHeader("Content-Type", contentType);
            int i = 1;
            for (Iterator<Map.Entry<String, Object>> it = protocol.getHeads().entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Object> entry = it.next();
                headers[i ] = new BasicHeader(entry.getKey(), String.valueOf(entry.getValue()));
                i++;
            }
            request.setHeaders(headers);
            HttpEntity entity = getHttpClient(url.startsWith("https")).execute(request).getEntity();
//            if (convertType.equalsIgnoreCase("string"))//传输类型
            resultData = EntityUtils.toString(entity, "UTF-8");
//            else resultData = new String(EntityUtils.toByteArray(entity), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultData;
    }

    /*    public static String execute(WxProtocol wxProtocol, String convertType) {
            String resultData = "";
            try {
                request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + charset);
                HttpEntity entity = getHttpClient(request.getURI().getScheme().startsWith("https")).execute(request).getEntity();
                if (convertType.equalsIgnoreCase("string"))//传输类型
                    resultData = EntityUtils.toString(entity, charset);
                else resultData = new String(EntityUtils.toByteArray(entity), charset);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultData;
        }*/
    public static String genGetURL(String sUrl, final Map<String, Object> params) {
        Matcher matcher = regexp.matcher(sUrl);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String k = matcher.group(1);
//            String v = matcher.group(2);
            String n = params.remove(k).toString();
            if (StringUtils.isNotEmpty(n))
                matcher.appendReplacement(sb, "$1=" + n);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static HttpUriRequest getRequestForMethod(HttpMethod method,String url) {
        switch (method) {
            case GET:
                return new HttpGet(url);
            case PUT:
                return new HttpPut(url);
            case POST:
                return new HttpPost(url);
            case PATCH:
                return new HttpPatch(url);
            case DELETE:
                return new HttpDelete(url);
        }
        return null;
    }
}
