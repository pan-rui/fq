package com.hy.util;

import com.hy.core.Constants;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.codec.Base64;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImgUtil {
	
	public static final String BASE_PATH = "/var/data/attach/";
//	public static final String BASE_PATH = "/var/testdata/attach/";				//测试环境

	public static final String TEMP_PATH = "temp/img/";
	
	public static final String USER_IMG_PATH = "user/img/";
	public static final String SALE_IMG_PATH = "sale/img/";

	public static final String USER_CERT_PATH = "user/cert/";

	public static final String APK_PATH = "apk/";

	public static final String ACCEPTANCE_PERIOD_IMG_PATH = "acceptance/img/period/";

	public static final String TEMP_APK_PATH = "temp/apk/";

	//一户一档隐蔽照片在户下还多一层级
	public static final String HIDDEEN_ACCEPTANCE_IMG_PATH = "/hidden";

	public static String saveAndUpdateFile(String fileName,String newPath, String oldPath, String path, String id) {
		File newFile = null;
		try {
			newFile = new File(BASE_PATH + newPath);
		} catch (Exception e) {
			System.out.println("new文件路径不对---" + e);
			return null;
		}
		int iLen = 0;
		byte[] btBuffer = new byte[1024];
		FileOutputStream outStream = null;
		FileInputStream inStream = null;
		new File(BASE_PATH + path + id + "/").mkdirs();
		
		String picFileName = path + id + "/" + newFile.getName();
		if(StringUtils.isNotBlank(fileName)){
			picFileName = path + id + "/" + fileName;
		}
		
		try {
			outStream = new FileOutputStream(BASE_PATH + picFileName);
			inStream = new FileInputStream(newFile);
			while ((iLen = inStream.read(btBuffer)) > 0) {
				outStream.write(btBuffer, 0, iLen);
			}
			inStream.close();
			outStream.close();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		newFile.delete();

		if (oldPath != null && !"".equals(oldPath)) {
			File oldPic = null;
			try {
				oldPic = new File(BASE_PATH + oldPath);
			} catch (Exception e) {
				System.out.println("old文件路径不对---" + e);
			}
			oldPic.delete();
		}
		return picFileName;
	}

	/**
	 * 保存图片 @param image @param dirName 上传服务器目录名 @param width 宽度限制 @param height
	 * 长度限制 @param widthMin TODO @param heightMax TODO @param imgTypes
	 * 图片类型 @throws IOException @throws
	 */
	public static void saveImg(MultipartFile image, String dirName, int width, int height, String imgTypes,
                                     int widthMin, int heightMax) throws IOException, RuntimeException {
		if (image != null && !image.isEmpty()) {
			// 图片类型校验
			if (!validateFileType(image.getOriginalFilename(), imgTypes)) {
				throw new RuntimeException("message.image.type.error==>" + imgTypes);
			}
			// 图片尺寸校验
			BufferedImage bufferImage = ImageIO.read(image.getInputStream());
			final int w = bufferImage.getWidth();
			final int h = bufferImage.getHeight();
			if (width < w || height < h) {
				throw new RuntimeException("message.image.size.error in width==>" + width + "\theight==>" + height);
			}
			if (widthMin > w || heightMax > h) {
				throw new RuntimeException(
						"message.image.min.size.error in widthMin==>" + widthMin + "\theightMax==>" + heightMax);
			}
			File file = new File(Constants.getSystemStringValue("UPLOAD_FILE_ROOT_PATH") + "/" + dirName);
			file.mkdirs();
			file = new File(Constants.getSystemStringValue("UPLOAD_FILE_ROOT_PATH") + "/" + dirName + "/"
					+ image.getOriginalFilename());
			file.createNewFile();
			FileCopyUtils.copy(image.getBytes(), file);
		}

	}

	/**
	 * 保存图片
	 * 
	 * @param image
	 * @param dirName
	 *            上传服务器目录名
	 * @param imgTypes
	 *            图片类型
	 * @throws IOException
	 * @throws return
	 */

	public static String saveImg(MultipartFile image, String dirName, String imgTypes) {
		String imgUrl = "";
		SimpleDateFormat sm = new SimpleDateFormat("yyyyMMddhhmmss");
		String nowDate = sm.format(new Date());
		if (image != null && !image.isEmpty()) {
			// 图片类型校验
			if (!validateFileType(image.getOriginalFilename(), imgTypes)) {
				try {
					throw new RuntimeException("message.image.type.error==>" + imgTypes);
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			File file = new File(Constants.getSystemStringValue("UPLOAD_FILE_ROOT_PATH") + "/" + dirName);
			if (!file.exists()) {
				file.mkdirs();
			}
			String imgName = "/" + dirName + "/" + nowDate + image.getOriginalFilename();
			file = new File(Constants.getSystemStringValue("UPLOAD_FILE_ROOT_PATH") + imgName);
			try {
				file.createNewFile();
				FileCopyUtils.copy(image.getBytes(), file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			imgUrl = Constants.getSystemStringValue("DOWNLOAD_FILE_ROOT_URL") + imgName;
		}
		return imgUrl;
	}

	/**
	 * 保存图片 NO URL
	 * 
	 * @param image
	 * @param dirName
	 *            上传服务器目录名
	 * @param imgTypes
	 *            图片类型
	 * @throws IOException
	 * @throws return
	 */

	public static String saveImgNoUrl(MultipartFile image, String dirName, String imgTypes, String fileName) {
		String imgUrl = "";
		SimpleDateFormat sm = new SimpleDateFormat("yyyyMMddhhmmss");
		String nowDate = sm.format(new Date());

		if (image != null && !image.isEmpty()) {
			// 图片类型校验
			if (!validateFileType(image.getOriginalFilename(), imgTypes)) {
				try {
					throw new RuntimeException("message.image.type.error==>" + imgTypes);
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			String originalImgName = image.getOriginalFilename();
			int index = originalImgName.lastIndexOf(".");
			String imgSuffix = originalImgName.substring(index);

			File file = new File(Constants.getSystemStringValue("UPLOAD_FILE_ROOT_PATH") + "/" + dirName);
			if (!file.exists()) {
				file.mkdirs();
			}
			String imgName = "/" + dirName + "/" + fileName + nowDate + imgSuffix;
			file = new File(Constants.getSystemStringValue("UPLOAD_FILE_ROOT_PATH") + imgName);
			try {
				file.createNewFile();
				FileCopyUtils.copy(image.getBytes(), file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			imgUrl = imgName;
		}
		return imgUrl;
	}

	/**
	 * 剪裁图片
	 * 
	 * @param image
	 * @param newImageUrl
	 * @param width
	 * @param height
	 *            return
	 * @throws Exception
	 */
	public static String cutImages(MultipartFile image, String dirName, int width, int height) throws Exception {
		String imgUrl = "";
		SimpleDateFormat sm = new SimpleDateFormat("yyyyMMddhhmmss");
		String nowDate = sm.format(new Date());

		// System.out.println(file.getAbsolutePath());
		Image src = ImageIO.read(image.getInputStream()); // 构???Image对象
		int old_w = src.getWidth(null); // 得到源图??
		int old_h = src.getHeight(null);
		// 得到源图??
		double a = old_w * 100 / width;
		double b = old_h * 100 / height;
		double c = (a > b ? b : a) / 100;
		// System.out.println(c);
		// System.out.println(old_w+"/"+width+"="+old_w/width+"~~~"+1/3);
		int new_w = (int) (c * width);
		int new_h = (int) (c * height);
		BufferedImage tag = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
		tag.getGraphics().drawImage(src, -(old_w - new_w) / 2, -(old_h - new_h) / 2, old_w, old_h, null); // 绘制缩小后的??

		File file = new File(Constants.getSystemStringValue("UPLOAD_FILE_ROOT_PATH") + "/" + dirName);
		if (!file.exists()) {
			file.mkdirs();
		}
		String imgName = "/" + dirName + "/" + nowDate + image.getOriginalFilename();
		file = new File(Constants.getSystemStringValue("UPLOAD_FILE_ROOT_PATH") + imgName);
		try {
			file.createNewFile();
			FileCopyUtils.copy(image.getBytes(), file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// destImg=Constants.DOWNLOAD_FILE_ROOT_URL+imgName;

		/*
		 * FileOutputStream newimage=new FileOutputStream(newImageUrl); //输出到文件流
		 * JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimage);
		 * encoder.encode(tag); //近JPEG编码 newimage.close();
		 */
		// 剪切后他图片路径
		String destImgUrl = Constants.getSystemStringValue("UPLOAD_FILE_ROOT_PATH") + imgName;
		ImgUtil.litImg(destImgUrl, destImgUrl, Integer.parseInt(Constants.getSystemStringValue("IMG_WIDTH")));
		imgUrl = Constants.getSystemStringValue("DOWNLOAD_FILE_ROOT_URL") + imgName;

		return imgUrl;
	}

	/**
	 * 剪裁图片 NO URL
	 * 
	 * @param image
	 * @param
	 * @param width
	 * @param height
	 *            return
	 * @throws Exception
	 */
	public static String cutImagesNoUrl(MultipartFile image, String dirName, int width, int height, String fileName)
			throws Exception {
		String imgUrl = "";
		SimpleDateFormat sm = new SimpleDateFormat("yyyyMMddhhmmss");
		/*
		 * SimpleDateFormat sm2 = new SimpleDateFormat("yyyyMMddhhmmssSS");
		 * String nowDate2 = sm2.format(new Date());
		 */
		String nowDate = sm.format(new Date());
		String originalImgName = image.getOriginalFilename();
		int index = originalImgName.lastIndexOf(".");
		String imgSuffix = originalImgName.substring(index);
		// System.out.println("*****"+imgSuffix);

		// System.out.println(file.getAbsolutePath());
		Image src = ImageIO.read(image.getInputStream()); // 构???Image对象
		int old_w = src.getWidth(null); // 得到源图??
		int old_h = src.getHeight(null);
		// 得到源图??
		double a = old_w * 100 / width;
		double b = old_h * 100 / height;
		double c = (a > b ? b : a) / 100;
		// System.out.println(c);
		// System.out.println(old_w+"/"+width+"="+old_w/width+"~~~"+1/3);
		int new_w = (int) (c * width);
		int new_h = (int) (c * height);
		BufferedImage tag = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
		tag.getGraphics().drawImage(src, -(old_w - new_w) / 2, -(old_h - new_h) / 2, old_w, old_h, null); // 绘制缩小后的??

		File file = new File(Constants.getSystemStringValue("UPLOAD_FILE_ROOT_PATH") + "/" + dirName);
		if (!file.exists()) {
			file.mkdirs();
		}
		String imgName = "/" + dirName + "/" + fileName + nowDate + imgSuffix;
		file = new File(Constants.getSystemStringValue("UPLOAD_FILE_ROOT_PATH") + imgName);
		try {
			file.createNewFile();
			FileCopyUtils.copy(image.getBytes(), file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// destImg=Constants.DOWNLOAD_FILE_ROOT_URL+imgName;

		/*
		 * FileOutputStream newimage=new FileOutputStream(newImageUrl); //输出到文件流
		 * JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimage);
		 * encoder.encode(tag); //近JPEG编码 newimage.close();
		 */
		// 剪切后他图片路径
		String destImgUrl = Constants.getSystemStringValue("UPLOAD_FILE_ROOT_PATH") + imgName;
		ImgUtil.litImg(destImgUrl, destImgUrl, Integer.parseInt(Constants.getSystemStringValue("IMG_WIDTH")));
		imgUrl = imgName;

		return imgUrl;
	}

	/**
	 * 生成缩略??
	 * 
	 * @param inputUrl
	 *            要缩略的文件地址
	 * @param outUrl
	 *            要保存的地址
	 * @param size
	 *            定义保存的宽或高 大小
	 */
	public static String litImg(String inputUrl, String outUrl, float size) {
		FileOutputStream out = null; // 输出到文件流
		String imgUrl = "";// ftp图片路径

		try {
			/** **加载** */
			BufferedImage bi1 = ImageIO.read(new File(inputUrl));
			BufferedImage bi2 = null;
			int old_w = bi1.getWidth(null); // 得到源图??
			int old_h = bi1.getHeight(null);
			int new_w = 0;
			int new_h = 0; // 得到源图??
			float tempdouble;
			if (old_w > old_h) {
				tempdouble = old_w / size;
			} else {
				tempdouble = old_h / size;
			}
			new_w = Math.round(old_w / tempdouble);
			new_h = Math.round(old_h / tempdouble);// 计算新图长宽
			/** *生成缩略??** */
			SmallPic pic = new SmallPic();
			bi2 = pic.imageZoomOut(bi1, new_w, new_h);
			/** *绘图** */
			out = new FileOutputStream(outUrl);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			/** *无损压缩** */
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi2);
			param.setQuality(1f, false);
			encoder.setJPEGEncodeParam(param);
			/** ********* */
			encoder.encode(bi2);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		imgUrl = outUrl;

		return imgUrl;
	}

	/**
	 * 生成缩略??
	 * 
	 * @param file
	 *            要缩略的文件
	 * @param outUrl
	 *            要保存的地址
	 * @param size
	 *            定义保存的宽或高 大小
	 */
	public static void suoluetu(File file, String outUrl, float size) {
		FileOutputStream out = null; // 输出到文件流
		try {
			/** **加载** */
			BufferedImage bi1 = ImageIO.read(file);
			BufferedImage bi2 = null;
			int old_w = bi1.getWidth(null); // 得到源图??
			int old_h = bi1.getHeight(null);
			int new_w = 0;
			int new_h = 0; // 得到源图??
			float tempdouble;
			if (old_w > old_h) {
				tempdouble = old_w / size;
			} else {
				tempdouble = old_h / size;
			}
			new_w = Math.round(old_w / tempdouble);
			new_h = Math.round(old_h / tempdouble);// 计算新图长宽
			/** *生成缩略??** */
			SmallPic pic = new SmallPic();
			bi2 = pic.imageZoomOut(bi1, new_w, new_h);
			/** *绘图** */
			out = new FileOutputStream(outUrl);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			/** *无损压缩** */
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi2);
			param.setQuality(1f, false);
			encoder.setJPEGEncodeParam(param);
			/** ********* */
			encoder.encode(bi2);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public static void saveImg(MultipartFile image, String dirName, String filename, int width, int height,
                                     String imgTypes, int widthMin, int heightMax) throws IOException, RuntimeException {
		if (image != null && !image.isEmpty()) {
			// 图片类型校验
			if (!validateFileType(image.getOriginalFilename(), imgTypes)) {
				throw new RuntimeException("message.image.type.error==>" + imgTypes);
			}
			// 图片尺寸校验
			BufferedImage bufferImage = ImageIO.read(image.getInputStream());
			final int w = bufferImage.getWidth();
			final int h = bufferImage.getHeight();
			if (width < w || height < h) {
				throw new RuntimeException("message.image.size.error in width==>" + width + "\theight===>" + height);
			}
			if (widthMin > w || heightMax > h) {
				throw new RuntimeException(
						"message.image.min.size.error in widthMin===>" + widthMin + "\theightMax==>" + heightMax);
			}
			File file = new File(Constants.getSystemStringValue("UPLOAD_FILE_ROOT_PATH") + "/" + dirName);
			file.mkdirs();
			file = new File(Constants.getSystemStringValue("UPLOAD_FILE_ROOT_PATH") + "/" + dirName + "/" + filename);
			file.createNewFile();
			FileCopyUtils.copy(image.getBytes(), file);
		}

	}

	public static void deleteImg(String creditOrderCompAttPath) {
		File file = new File(Constants.getSystemStringValue("UPLOAD_FILE_ROOT_PATH") + "/" + creditOrderCompAttPath);
		file.delete();
	}

	/**
	 * 生成缩略图，
	 *
	 * @param inputFile 需要生成缩略图的原始图片文件 @param outputPath
	 * 缩略图文件保存路径，输出格式为JPEG @param maxSize 最大尺寸 @return @throws
	 * IOException @throws
	 *
	 */
	public static String getThumbnailImg(File inputFile, String outputPath, int maxSize)
			throws IOException, RuntimeException {
		BufferedImage inputImage = ImageIO.read(inputFile);
		if (null == inputImage) {
			throw new RuntimeException("message.not.image.file");
		}

		// 计算新缩略图尺寸
		int oldWidth = inputImage.getWidth();
		int oldHeight = inputImage.getHeight();
		int newWidth = oldWidth;
		int newHeight = oldHeight;
		if (oldWidth > oldHeight) {
			newWidth = maxSize;
			newHeight = oldHeight * maxSize / oldWidth;
		} else {
			newWidth = oldWidth * maxSize / oldHeight;
			newHeight = maxSize;
		}

		// 生成缩略
		Image newImage = inputImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		outputImage.getGraphics().drawImage(newImage, 0, 0, null);
		outputImage.getGraphics().dispose();
		ImageIO.write(outputImage, "JPEG", new File(outputPath));

		return outputPath;
	}

	public static boolean validateFileType(String fileName, String type) {
		return fileName.substring(fileName.lastIndexOf(".") + 1).equalsIgnoreCase(type);
	}
	
	public static String encodeBase64Img(String path){
		try{
			File file = new File(path);;
			FileInputStream inputFile = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			inputFile.read(buffer);
			inputFile.close();
			return Base64.encodeToString(buffer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
