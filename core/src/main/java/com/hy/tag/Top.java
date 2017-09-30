package com.hy.tag;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.impl.conn.DefaultClientConnectionOperator;
import org.apache.http.util.EntityUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by panrui on 2014/12/30.
 */
public class Top extends TagSupport {
    String url;
    String params="";
    String suffix="";
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.write(getContent(url,params));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SKIP_BODY;
    }

    public String getContent(final String url, String param) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url + suffix);
//        httpget.addHeader("Content-Type", "text/html;charset=GB2312");
        String responseStr = "";
        ResponseHandler<String> handler = new ResponseHandler<String>() {
            public String handleResponse(HttpResponse response) throws IOException {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String cont = EntityUtils.toString(entity, Charset.forName("GB2312"));
                    Matcher matcher = Pattern.compile("<c:import\\s++url=\\\"([\\S]+?)\\\"\\s*?(>|/>)").matcher(cont);
                    while (matcher.find()) {
                        String childUrl = matcher.group(1);
                        if (childUrl.startsWith("/") || childUrl.startsWith("../"))
                            childUrl = getWithUrl(url, childUrl);
                        cont = cont.replaceAll(matcher.group(0), getContent(childUrl, null));
                        matcher.reset(cont);
                    }
                    Matcher matcher1 = Pattern.compile("<jsp:include\\s++page=\\\"(.+?)\\\"\\s*?(>|/>)").matcher(cont);
                    while (matcher1.find()) {
                        String childUrl = matcher1.group(1);
                        if (childUrl.startsWith("/") || childUrl.startsWith("../"))
                            childUrl = getWithUrl(url, childUrl);
                        cont = cont.replaceAll(matcher1.group(0), getContent(childUrl, null));
                        matcher1.reset(cont);
                    }
                    return replaceUrl(url, cont);
                } else {
                    return null;
                }
            }
        };
        try {
            responseStr = httpclient.execute(httpget, handler);
//            httpclient.getConnectionManager().shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseStr;
    }

    public String getWithUrl(String parentUrl, String childUrl) {
            String context = parentUrl.substring(0, parentUrl.lastIndexOf("/"));
        String context2 = parentUrl.substring(0, parentUrl.indexOf("/", 7));
        Matcher matcher = Pattern.compile("\\.\\./").matcher(childUrl);
        int last=0;
        while (matcher.find()) {
            context = context.substring(0, context.lastIndexOf("/"));
            last=matcher.end();
        }
        return last==0?context2 + childUrl:context+childUrl.substring(last-1);
    }

    public String replaceUrl(String parentUrl,String content) {
        Matcher matcher = Pattern.compile("src=\\\"([/|.]+?\\S+?)\\\"").matcher(content);
        while (matcher.find()) {
            content = content.replaceAll(matcher.group(1).replaceAll("\\?","\\\\?"), getWithUrl(parentUrl, matcher.group(1)));
            matcher.reset(content);
        }
        Matcher matcher1 = Pattern.compile("href=\\\"([/|.]+?\\S+?)\\\"").matcher(content);
        while (matcher1.find()) {
            content = content.replaceAll(matcher1.group(1).replaceAll("\\?", "\\\\?"), getWithUrl(parentUrl, matcher1.group(1)));
            matcher1.reset(content);
        }
        return content;
    }

    public HttpClient getHttpClient(){
        BasicClientConnectionManager clientConnectionManager=new BasicClientConnectionManager(){
            @Override
            protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
                return new DefaultClientConnectionOperator(schreg, new MyDnsResolver());
            }
        };
        return new DefaultHttpClient(clientConnectionManager);
    }
    private static class MyDnsResolver implements DnsResolver {

        private static final Map<String, InetAddress[]> MAPPINGS = new HashMap<String, InetAddress[]>();

        static {
            addResolve("www.hehenian.cn", "10.111.0.202");
            addResolve("localhost","127.0.0.1");
        }

        private static void addResolve(String host, String ip) {
            try {
                MAPPINGS.put(host, new InetAddress[]{InetAddress.getByName(ip)});
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        @Override
        public InetAddress[] resolve(String host) throws UnknownHostException {
            return MAPPINGS.containsKey(host) ? MAPPINGS.get(host) : new InetAddress[0];
        }
    }
/*    public static void main(String[] args) throws JspException {
        Top top=new Top();
        String str = top.getWithUrl("http://www.google.com/ddf/ggg/rgr/fd.html", "kl../../../abc.jsp");
        System.out.println(str);
        String url = "<head>fdfd</head><c:import url=\"www.fdf.abcdfd.df\"></c:import><body><p>fdf</P><jsp:include page=\"www.acd.ddd\" /></body>";
        String url2 = "<jsp:include page=\"www.acd.ddd\" />";
       Matcher matcher=Pattern.compile("<c:import\\s++url=\\\"(\\S+?)\\\"\\s*?(>|/>)|<jsp:include\\s++page=\\\"(\\S+?)\\\"\\s*?(>|/>)").matcher(url);
        while (matcher.find()) {
            System.out.println("group-->0\t" + matcher.group(0).toString());
*//*            System.out.println("group-->1\t" + matcher.group(1).toString());
            System.out.println("group-->2\t" + matcher.group(2).toString());*//*
*//*            System.out.println("group-->3\t" + matcher.group(3).toString());
            System.out.println("group-->4\t" + matcher.group(4).toString());*//*
        }
//        top.setUrl("http://www.hehenian.com/include/top.jsp");
        top.setUrl("http://10.111.0.202/include/top.jsp");
        top.setParams(";s=1");

        top.doStartTag();
    }*/


}
