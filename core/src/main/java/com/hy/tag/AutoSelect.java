package com.hy.tag;

import org.springframework.util.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Map;

public class AutoSelect extends TagSupport {
    private String name;
    private String id = "";
    private String style = "";
    private Map<String,Object> data;
    private String defVal="";
    private String range = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Map<String, Object> getData() {
        return data;
    }
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getDefVal() {
        return defVal;
    }

    public void setDefVal(String defVal) {
        this.defVal = defVal;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    @Override
    public int doEndTag() throws JspException {
        StringBuffer sb = new StringBuffer("<select ");
        if (!StringUtils.isEmpty(id))
            sb.append("id='" + id + "' ");
        if (!StringUtils.isEmpty(style))
            sb.append("class='" + style + "' ");
        sb.append("name='" + name + "'>");
//        sb.append("<option value=''>\u8bf7\u9009\u62e9...</option>\r\n");
        if(StringUtils.isEmpty(range))
        data.forEach((key,val) ->sb.append("<option value='" + key + "' ").append(key.equals(defVal)?"selected='selected'":"").append(">" + val.toString() + "</option>"));
        else
            data.forEach((key,val) ->{if (range.contains(key)) sb.append("<option value='" + key + "' ").append(key.equals(defVal)?"selected='selected'":"").append(">" + val.toString() + "</option>");});
        sb.append("</select>");
        try {
            pageContext.getOut().print(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EVAL_BODY_INCLUDE;
    }
}
