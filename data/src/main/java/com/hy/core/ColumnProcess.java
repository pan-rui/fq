package com.hy.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-03-27 20:48)
 * @version: \$Rev: 1158 $
 * @UpdateAuthor: \$Author: panrui $
 * @UpdateDateTime: \$Date: 2017-04-18 15:53:47 +0800 (周二, 18 4月 2017) $
 */
public class ColumnProcess {

    private static final char sp = '_';

    public static String encryptVal(String val) {
        StringBuffer sb = new StringBuffer();
        char[] chars = val.toLowerCase().toCharArray();     //TODO:后期有变更
        boolean nextProc=false;
        for(int i=0;i<chars.length;i++) {
            if(nextProc) {
                nextProc=false;
                sb.append((char) (chars[i]-32));
            }else{
                if (chars[i] == sp)
                    nextProc=true;
                else sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    public static String decryptVal(String val) {
        StringBuffer sb = new StringBuffer();
        char[] chars = val.toCharArray();
        for(int i=0;i<chars.length-1;i++) {
             sb.append(chars[i]);
            if(Character.isUpperCase(chars[i+1]))
                sb.append(sp);
        }
        sb.append(chars[chars.length-1]);
        return sb.toString().toUpperCase();
    }

    public static Map<String, Object> encryMap(Map<String, Object> srcMap) {
        Map<String, Object> dest = new LinkedHashMap<>();
        srcMap.forEach((k, v) -> dest.put(encryptVal(k), v));
        return dest;
    }

    public static Map<String, Object> decryMap(Map<String, Object> srcMap) {
        Map<String, Object> dest = new LinkedHashMap<>();
        srcMap.forEach((k, v) -> dest.put(decryptVal(k), v));
        return dest;
    }

    public static void main(String[] args) {
        System.out.println(ColumnProcess.encryptVal("USER_OPERATE_PRIVILEGES_RELATE"));
        System.out.println(ColumnProcess.decryptVal("userOperatePrivilegesRelate"));
    }
}
