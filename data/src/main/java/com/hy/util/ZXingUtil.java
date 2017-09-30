package com.hy.util;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;


public class ZXingUtil {
	
	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;
	
	/**
	 * 文字生成二维码
	 *
	 * @param text
	 * @param width
	 * @param height
	 * @return
	 * @throws WriterException
	 */
	public static BufferedImage convert2Image(String text, int width, int height) throws WriterException {
	     MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

	     Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
	     hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

	     BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, width, height,hints);

	     return toBufferedImage(bitMatrix);
	}

	/**
	 * 二维码解析为文字
	 * 
	 * @param image
	 * @return
	 * @throws NotFoundException
	 */
	public static String convert2String(BufferedImage image) throws NotFoundException {
		if (null == image) {
			return null;
		}
		MultiFormatReader multiFormatReader = new MultiFormatReader();
		
		Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
		
		LuminanceSource luminanceSource = new RGBLuminanceSource(image.getWidth(), image.getHeight(), getBufferedImagePixels(image));
		Binarizer binarizer = new HybridBinarizer(luminanceSource );
		BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
		
		Result result = multiFormatReader.decode(binaryBitmap, hints);
		if (null != result) {
			return result.getText();
		} else {
			return null;
		}
	}
	
	private static BufferedImage toBufferedImage(BitMatrix matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
			}
		}
		return image;
	}

	private static int[] getBufferedImagePixels(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[] pixels = new int[width*height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset+x] = image.getRGB(x, y);
			}
		}
		return pixels;
	}
}
