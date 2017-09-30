package com.hy.util;

import java.io.*;

/**
 * 上传文件相关
 * 
 */
public class UploadFile {

	private static final int BUFFER_SIZE = 3 * 1024; // 设置缓存的大小

	/*****
	 * 拷贝文件去指定的文件夹
	 * 
	 * @param file
	 *            源文件
	 * @param path
	 *            保存的文件夹路径
	 * @param name
	 *            保存的文件名
	 */
	@SuppressWarnings("finally")
	public static String copy(File file, String path, String name, String suffix) {
		String filepath = path + name + suffix;
		try {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = new BufferedInputStream(new FileInputStream(file),
						BUFFER_SIZE);
				File dst = new File(filepath);
				File folder = new File(path);
				if (!folder.exists()) {
					folder.mkdirs();
				}
				out = new BufferedOutputStream(new FileOutputStream(dst),
						BUFFER_SIZE);
				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesRead = 0;
				while ((bytesRead = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
			} finally {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return filepath;
		}
	}

	/**
	 * 复制文件(用于生成水印图片时候保留原图)
	 * 
	 * @param in_path
	 * @param out_path
	 */
	public static void copyImg(String in_path, String out_path) {
		InputStream is = null;
		OutputStream os = null;
		File folder = new File(out_path.substring(0, out_path.lastIndexOf("/")));
		if (!folder.exists()) {
			folder.mkdirs();
		}
		try {
			is = new FileInputStream(in_path);
			os = new FileOutputStream(out_path, false);
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = 0;
			while ((bytesRead = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 把制定的内容写入制定的文件
	 * 
	 * @param content
	 * @param filePath
	 */
	public static void writerFile(String content, String filePath) {
		File folder = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File(filePath);
		RandomAccessFile dos = null;
		try {
			dos = new RandomAccessFile(file, "rw");
			try {
				dos.setLength(0);
				dos.write(content.getBytes("UTF-8"));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} finally {
			try {
				if (dos != null)
					dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
