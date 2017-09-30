package com.hy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	private static Logger logger = LoggerFactory.getLogger((Class) MD5Util.class);
	protected static char[] hexDigits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
			'd', 'e', 'f' };
	protected static MessageDigest messagedigest = null;
	
	public static String getMD5(String message) {  
        MessageDigest messageDigest = null;  
        StringBuffer md5StrBuff = new StringBuffer();  
        try {  
            messageDigest = MessageDigest.getInstance("MD5");  
            messageDigest.reset();  
            messageDigest.update(message.getBytes("UTF-8"));  
               
            byte[] byteArray = messageDigest.digest();  
            for (int i = 0; i < byteArray.length; i++)   
            {  
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)  
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));  
                else 
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));  
            }  
        } catch (Exception e) {  
            throw new RuntimeException();  
        }  
        return md5StrBuff.toString().toUpperCase();
    }

	public static String getFileMD5String(File file) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(file);
		FileChannel fileChannel = fileInputStream.getChannel();
		MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
		messagedigest.update(mappedByteBuffer);
		fileInputStream.close();
		return MD5Util.bufferToHex(messagedigest.digest());
	}

	public static String getMD5String(String string) {
		return MD5Util.getMD5String(string.getBytes());
	}

	public static String getMD5String(byte[] arrby) {
		messagedigest.update(arrby);
		return MD5Util.bufferToHex(messagedigest.digest());
	}

	private static String bufferToHex(byte[] arrby) {
		return MD5Util.bufferToHex(arrby, 0, arrby.length);
	}

	private static String bufferToHex(byte[] arrby, int n, int n2) {
		StringBuffer stringBuffer = new StringBuffer(2 * n2);
		int n3 = n + n2;
		for (int i = n; i < n3; ++i) {
			MD5Util.appendHexPair(arrby[i], stringBuffer);
		}
		return stringBuffer.toString();
	}

	private static void appendHexPair(byte by, StringBuffer stringBuffer) {
		char c = hexDigits[(by & 240) >> 4];
		char c2 = hexDigits[by & 15];
		stringBuffer.append(c);
		stringBuffer.append(c2);
	}

	public static boolean checkPassword(String string, String string2) {
		String string3 = MD5Util.getMD5String(string);
		return string3.equals(string2);
	}

	static {
		try {
			messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException var0) {
			logger.error("MD5FileUtil messagedigest初始化失败", (Throwable) var0);
		}
	}

}
