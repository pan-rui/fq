package com.hy.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtil {

	public static final String ADD_OPERATION = "add";
	public static final String SUBTRACT_OPERATION = "subtract";
	public static final String MULTIPLY_OPERATION = "multiply";
	public static final String DIVIDE_OPERATION = "divide";

	private static int DECIMALS_COUNT = 2;

	public static void main(String[] args) {
		System.out.println(calculateByOperation(NumberUtil.ADD_OPERATION, 0.99,
				0.199, true));
	}

	public static BigDecimal calculateByOperation(String operation, double v1,
			double v2, boolean setScale) {// 加法
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		BigDecimal result = null;
		if (ADD_OPERATION.equals(operation)) {
			result = b1.add(b2);
		} else if (SUBTRACT_OPERATION.equals(operation)) {
			result = b1.subtract(b2);
		} else if (MULTIPLY_OPERATION.equals(operation)) {
			result = b1.multiply(b2);
		} else if (DIVIDE_OPERATION.equals(operation)) {
			result = b1.divide(b2);
		}
		if (setScale) {
			return result.setScale(DECIMALS_COUNT, BigDecimal.ROUND_DOWN);
		}
		return result;
	}

	public static String decimalFormat(String pattern, double value) {
		return new DecimalFormat(pattern).format(value);
	}

	public static String decimalFormat(double value) {
		return new DecimalFormat("0.00").format(value);
	}

	public static String decimalFormat(double value, String pattern) {
		return new DecimalFormat(pattern).format(value);
	}

	public static String decimalBlankFormat(double value) {
		return new DecimalFormat("0").format(value);
	}

	public static boolean isNumber(String value) { // 检查是否是数字
		String patternStr = "^\\d+$";
		Pattern p = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE); // 忽略大小写;
		Matcher m = p.matcher(value);
		return m.find();
	}
	/**
	 * 获取begin到end之间的随机数
	 *
	 * @param begin
	 * @param end
	 * @return
	 */
	public static int random(int begin, int end) {
		int rtn = begin + (int) (Math.random() * (end - begin));
		if (rtn == begin || rtn == end) {
			return random(begin, end);
		}
		return rtn;
	}

}