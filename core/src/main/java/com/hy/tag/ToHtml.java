package com.hy.tag;

import com.alibaba.fastjson.JSON;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/15.
 */
public class ToHtml extends TagSupport {
   private String text;

    public static String toHTMLString(String in) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; in != null && i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == '\'')
                out.append("\'");
            else if (c == '"')
                out.append("\\\"");
            else if (c == '<')
                out.append("&lt;");
            else if (c == '>')
                out.append("&gt;");
            else if (c == '&')
                out.append("&amp;");
            else if (c == ' ')
                out.append("");
            else if (c == '\n'||c=='\r') {
                if(in.charAt(i-1)=='\n'||in.charAt(i-1)=='\r') continue;
                out.append("<br>");
            }else
                out.append(c);
        }
        return out.toString();
    }

    public static String showJSON(String jsonStr) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; jsonStr != null && i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if(c=='\\')
                out.append("\\\\");
            else if (c == '"')
                out.append("'");
            else out.append(c);
        }
        return out.toString();
    }

    public static String concat(String before, String end) {
        return before + end;
    }

    public static Object getComment(String comments, String key) {
        return JSON.parseObject(comments, Map.class).get(key);
    }

    public static LinkedHashMap toLinkedMap(String text) {
        return JSON.parseObject(text, LinkedHashMap.class);
    }

/*    public static String toJSON(String in){
        StringBuffer out = new StringBuffer();
        for (int i = 0; in != null && i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == '\'')
    }*/

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

/*    public static Integer parseIneger(String string) {
        return Integer.parseInt(string);
    }*/

    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.write(toHTMLString(text));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EVAL_BODY_INCLUDE;
    }
}
