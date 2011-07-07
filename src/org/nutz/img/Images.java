package org.nutz.img;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;

/**
 * 对图像操作的简化 API
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Images {

	/**
	 * 自动缩放剪切一个图片，令其符合给定的尺寸，并将其保存成目标图像文件
	 * <p>
	 * 图片格式支持 png | gif | jpg | bmp | wbmp
	 * 
	 * @param srcIm
	 *            源图像文件对象
	 * @param taIm
	 *            目标图像文件对象
	 * @param w
	 *            宽度
	 * @param h
	 *            高度
	 * @return 被转换前的图像对象
	 * 
	 * @throws IOException
	 *             当读写文件失败时抛出
	 */
	public static BufferedImage clipScale(File srcIm, File taIm, int w, int h) throws IOException {
		BufferedImage old = read(srcIm);
		BufferedImage im = Images.clipScale(old, w, h);
		write(im, taIm);
		return old;
	}

	/**
	 * 自动缩放剪切一个图片，令其符合给定的尺寸，并将其保存到目标图像路径
	 * <p>
	 * 图片格式支持 png | gif | jpg | bmp | wbmp
	 * 
	 * @param srcPath
	 *            源图像路径
	 * @param taPath
	 *            目标图像路径，如果不存在，则创建
	 * @param w
	 *            宽度
	 * @param h
	 *            高度
	 * 
	 * @throws IOException
	 *             当读写文件失败时抛出
	 */
	public static void clipScale(String srcPath, String taPath, int w, int h) throws IOException {
		File srcIm = Files.findFile(srcPath);
		if (null == srcIm)
			throw Lang.makeThrow("Fail to find image file '%s'!", srcPath);

		File taIm = Files.createFileIfNoExists(taPath);
		clipScale(srcIm, taIm, w, h);
	}

	/**
	 * 自动缩放剪切一个图片，令其符合给定的尺寸
	 * <p>
	 * 如果图片太大，则将其缩小，如果图片太小，则将其放大，多余的部分被裁减
	 * 
	 * @param im
	 *            图像对象
	 * @param w
	 *            宽度
	 * @param h
	 *            高度
	 * @return 被转换后的图像
	 */
	public static BufferedImage clipScale(BufferedImage im, int w, int h) {
		// 获得尺寸
		int oW = im.getWidth();
		int oH = im.getHeight();
		float oR = (float) oW / (float) oH;
		float nR = (float) w / (float) h;

		int nW, nH, x, y;
		/*
		 * 裁减
		 */
		// 原图太宽，计算当原图与画布同高时，原图的等比宽度
		if (oR > nR) {
			nW = (h * oW) / oH;
			nH = h;
			x = (w - nW) / 2;
			y = 0;
		}
		// 原图太长
		else if (oR < nR) {
			nW = w;
			nH = (w * oH) / oW;
			x = 0;
			y = (h - nH) / 2;
		}
		// 比例相同
		else {
			nW = w;
			nH = h;
			x = 0;
			y = 0;
		}

		BufferedImage re = new BufferedImage(w, h, ColorSpace.TYPE_RGB);
		re.getGraphics().drawImage(im, x, y, nW, nH, Color.black, null);

		return re;
	}

	/**
	 * 将一个图片文件读入内存
	 * 
	 * @param imgFile
	 *            图片文件
	 * @return 图片对象
	 */
	public static BufferedImage read(File imgFile) {
		try {
			return ImageIO.read(imgFile);
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	/**
	 * 将内存中一个图片写入目标文件
	 * 
	 * @param im
	 *            图片对象
	 * @param targetFile
	 *            目标文件，根据其后缀，来决定写入何种图片格式
	 */
	public static void write(RenderedImage im, File targetFile) {
		try {
			ImageIO.write(im, Files.getSuffixName(targetFile), targetFile);
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	/**
	 * 写入一个 JPG 图像
	 * 
	 * @param im
	 *            图像对象
	 * @param targetJpg
	 *            目标输出 JPG 图像文件
	 * @param quality
	 *            质量 0.1f ~ 1.0f
	 */
	public static void writeJpeg(RenderedImage im, File targetJpg, float quality) {
		try {
			ImageWriter writer = ImageIO.getImageWritersBySuffix("jpg").next();
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(quality);
			ImageOutputStream os = ImageIO.createImageOutputStream(targetJpg);
			writer.setOutput(os);
			writer.write((IIOMetadata) null, new IIOImage(im, null, null), param);
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
