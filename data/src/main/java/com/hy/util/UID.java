package com.hy.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * UUID工具，生成唯一编号
 *
 * @author DengJB
 */
public class UID {

    private static final int ROTATION = 9999;
    /*private static  Date date = new Date();
    private static  StringBuilder buf = new StringBuilder();*/
    private static int seq = 100;

    /**
     * 产生唯一编号
     */
    public static synchronized String next() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyMMddHHmmssMS");
        //是不是严格按照指定的格式解析日期
        format1.setLenient(false);
        String DateStr = format1.format(new Date());
        if (seq > ROTATION) seq = 100;
        return DateStr + String.valueOf(seq++);
    }


    public static BigDecimal maxBigDecimal(BigDecimal[] bigDecimals) {
        if (bigDecimals != null) {
            for (int i = 0; i < bigDecimals.length - 1; i++) {
                for (int j = i + 1; j < bigDecimals.length; j++) {
                    if (bigDecimals[i] != null && bigDecimals[j] != null) {
                        if (bigDecimals[i].compareTo(bigDecimals[j]) <= 0) {
                            BigDecimal te = bigDecimals[i];
                            bigDecimals[i] = bigDecimals[j];
                            bigDecimals[j] = te;
                        }
                    } else if (bigDecimals[i] == null) {
                        bigDecimals[i] = bigDecimals[j];
                    }
                }
            }
        }
        return bigDecimals[0];
    }

}

