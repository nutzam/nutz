package org.nutz.img;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.repo.Base64;

/**
 * 对图像操作的简化 API
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Images {
    /**
     * 对一个图像进行旋转
     * 
     * @param srcIm
     *            原图像文件
     * @param taIm
     *            转换后的图像文件
     * @param degree
     *            旋转角度, 90 为顺时针九十度， -90 为逆时针九十度
     * @return 旋转后得图像对象
     */
    public static BufferedImage rotate(Object srcIm, File taIm, int degree) {
        BufferedImage im = Images.read(srcIm);
        BufferedImage im2 = Images.rotate(im, degree);
        Images.write(im2, taIm);
        return im2;
    }

    /**
     * 对一个图像进行旋转
     * 
     * @param srcPath
     *            原图像文件路径
     * @param taPath
     *            转换后的图像文件路径
     * @param degree
     *            旋转角度, 90 为顺时针九十度， -90 为逆时针九十度
     * @return 旋转后得图像对象
     */
    public static BufferedImage rotate(String srcPath, String taPath, int degree)
            throws IOException {
        File srcIm = Files.findFile(srcPath);
        if (null == srcIm)
            throw Lang.makeThrow("Fail to find image file '%s'!", srcPath);

        File taIm = Files.createFileIfNoExists(taPath);
        return rotate(srcIm, taIm, degree);
    }

    /**
     * 对一个图像进行旋转
     * 
     * @param image
     *            图像
     * @param degree
     *            旋转角度, 90 为顺时针九十度， -90 为逆时针九十度
     * @return 旋转后得图像对象
     */
    public static BufferedImage rotate(BufferedImage image, int degree) {
        int iw = image.getWidth();// 原始图象的宽度
        int ih = image.getHeight();// 原始图象的高度
        int w = 0;
        int h = 0;
        int x = 0;
        int y = 0;
        degree = degree % 360;
        if (degree < 0)
            degree = 360 + degree;// 将角度转换到0-360度之间
        double ang = degree * 0.0174532925;// 将角度转为弧度

        /**
         * 确定旋转后的图象的高度和宽度
         */

        if (degree == 180 || degree == 0 || degree == 360) {
            w = iw;
            h = ih;
        } else if (degree == 90 || degree == 270) {
            w = ih;
            h = iw;
        } else {
            int d = iw + ih;
            w = (int) (d * Math.abs(Math.cos(ang)));
            h = (int) (d * Math.abs(Math.sin(ang)));
        }

        x = (w / 2) - (iw / 2);// 确定原点坐标
        y = (h / 2) - (ih / 2);
        BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());
        Graphics2D gs = rotatedImage.createGraphics();
        gs.fillRect(0, 0, w, h);// 以给定颜色绘制旋转后图片的背景
        AffineTransform at = new AffineTransform();
        at.rotate(ang, w / 2, h / 2);// 旋转图象
        at.translate(x, y);
        AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        op.filter(image, rotatedImage);
        image = rotatedImage;
        return image;
    }

    /**
     * 自动等比缩放一个图片，并将其保存成目标图像文件<br />
     * 多余的部分，用给定背景颜色补上<br />
     * 如果参数中的宽度或高度为<b>-1</b>的话，着按照指定的高度或宽度对原图等比例缩放图片，不添加背景颜色
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
     * @param bgColor
     *            背景颜色
     * 
     * @return 被转换前的图像对象
     * 
     * @throws IOException
     *             当读写文件失败时抛出
     */
    public static BufferedImage zoomScale(Object srcIm, File taIm, int w, int h, Color bgColor)
            throws IOException {
        BufferedImage old = read(srcIm);
        BufferedImage im = Images.zoomScale(old, w, h, bgColor);
        write(im, taIm);
        return old;
    }

    /**
     * 自动等比缩放一个图片，并将其保存成目标图像文件<br />
     * 多余的部分，用给定背景颜色补上<br />
     * 如果参数中的宽度或高度为<b>-1</b>的话，着按照指定的高度或宽度对原图等比例缩放图片，不添加背景颜色
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
     * @param bgColor
     *            背景颜色
     * 
     * @return 被转换前的图像对象
     * 
     * @throws IOException
     *             当读写文件失败时抛出
     */
    public static BufferedImage zoomScale(String srcPath,
                                          String taPath,
                                          int w,
                                          int h,
                                          Color bgColor)
            throws IOException {
        File srcIm = Files.findFile(srcPath);
        if (null == srcIm)
            throw Lang.makeThrow("Fail to find image file '%s'!", srcPath);

        File taIm = Files.createFileIfNoExists(taPath);
        return zoomScale(srcIm, taIm, w, h, bgColor);
    }

    /**
     * 自动等比缩放一个图片，多余的部分，用给定背景颜色补上<br />
     * 如果参数中的宽度或高度为<b>-1</b>的话，着按照指定的高度或宽度对原图等比例缩放图片，不添加背景颜色
     * 
     * @param im
     *            图像对象
     * @param w
     *            宽度
     * @param h
     *            高度
     * @param bgColor
     *            背景颜色
     * 
     * @return 被转换后的图像
     */
    public static BufferedImage zoomScale(BufferedImage im, int w, int h, Color bgColor) {
        if (w == -1 || h == -1) {
            return zoomScale(im, w, h);
        }

        // 检查背景颜色
        // bgColor = null == bgColor ? Color.black : bgColor;
        // 获得尺寸
        int oW = im.getWidth();
        int oH = im.getHeight();
        float oR = (float) oW / (float) oH;
        float nR = (float) w / (float) h;

        int nW, nH, x, y;
        /*
         * 缩放
         */
        // 原图太宽，计算当原图与画布同高时，原图的等比宽度
        if (oR > nR) {
            nW = w;
            nH = (int) ((w) / oR);
            x = 0;
            y = (h - nH) / 2;
        }
        // 原图太高
        else if (oR < nR) {
            nH = h;
            nW = (int) ((h) * oR);
            x = (w - nW) / 2;
            y = 0;
        }
        // 比例相同
        else {
            nW = w;
            nH = h;
            x = 0;
            y = 0;
        }

        // 创建图像
        BufferedImage re = new BufferedImage(w, h, im.getType());
        Graphics2D gc = re.createGraphics();
        if (null != bgColor) {
            gc.setColor(bgColor);
            gc.fillRect(0, 0, w, h);
        }

        // 绘制图像
        gc.drawImage(im, x, y, nW, nH, bgColor, null);

        // 释放
        gc.dispose();

        // 返回
        return re;
    }

    /**
     * @see #zoomScale(BufferedImage, int, int, Color)
     */
    public static BufferedImage zoomScale(BufferedImage im, int w, int h) {
        return zoomScale(im, w, h, null);
    }

    /**
     * 自动等比缩放一个图片
     * 
     * @param im
     *            图像对象
     * @param w
     *            宽度
     * @param h
     *            高度
     * 
     * @return 被转换后的图像
     */
    public static BufferedImage scale(BufferedImage im, int w, int h) {
        // 获得尺寸
        int oW = im.getWidth();
        int oH = im.getHeight();

        int nW = w, nH = h;

        /*
         * 缩放
         */
        // 未指定图像高度，根据原图尺寸计算出高度
        if (h == -1) {
            nH = (int) ((float) w / oW * oH);
        }
        // 未指定图像宽度，根据原图尺寸计算出宽度
        else if (w == -1) {
            nW = (int) ((float) h / oH * oW);
        }

        // 创建图像
        BufferedImage re = new BufferedImage(nW, nH, im.getType());
        Graphics2D gc = re.createGraphics();
        gc.drawImage(im, 0, 0, nW, nH, null);
        gc.dispose();
        // 返回
        return re;
    }

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
    public static BufferedImage clipScale(Object srcIm, File taIm, int w, int h)
            throws IOException {
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
     * @return 被转换前的图像对象
     * 
     * @throws IOException
     *             当读写文件失败时抛出
     */
    public static BufferedImage clipScale(String srcPath, String taPath, int w, int h)
            throws IOException {
        File srcIm = Files.findFile(srcPath);
        if (null == srcIm)
            throw Lang.makeThrow("Fail to find image file '%s'!", srcPath);

        File taIm = Files.createFileIfNoExists(taPath);
        return clipScale(srcIm, taIm, w, h);
    }

    public static BufferedImage clipScale(Object srcIm, int[] startPoint, int[] endPoint) {
        // 计算给定坐标后的图片的尺寸
        int width = endPoint[0] - startPoint[0];
        int height = endPoint[1] - startPoint[1];

        BufferedImage old = read(srcIm);
        BufferedImage im = Images.clipScale(old.getSubimage(startPoint[0],
                                                            startPoint[1],
                                                            width,
                                                            height),
                                            width,
                                            height);
        return im;
    }

    /**
     * 根据给定的起始坐标点与结束坐标点来剪切一个图片，令其符合给定的尺寸，并将其保存成目标图像文件
     * <p>
     * 图片格式支持 png | gif | jpg | bmp | wbmp
     * 
     * @param srcIm
     *            源图像文件对象
     * @param taIm
     *            目标图像文件对象
     * @param startPoint
     *            起始坐标点，其值[x, y]为相对原图片左上角的坐标
     * @param endPoint
     *            结束坐标点，其值[x, y]为相对原图片左上角的坐标
     * @return 被转换前的图像对象
     * 
     * @throws IOException
     *             当读写文件失败时抛出
     */
    public static BufferedImage clipScale(Object srcIm, File taIm, int[] startPoint, int[] endPoint)
            throws IOException {
        BufferedImage old = read(srcIm);
        BufferedImage im = clipScale(old, startPoint, endPoint);
        write(im, taIm);
        return old;
    }

    /**
     * 根据给定的起始坐标点与结束坐标点来剪切一个图片，令其符合给定的尺寸，并将其保存成目标图像文件
     * <p>
     * 图片格式支持 png | gif | jpg | bmp | wbmp
     * 
     * @param srcPath
     *            源图像文件对象
     * @param taPath
     *            目标图像文件对象
     * @param startPoint
     *            起始坐标点，其值[x, y]为相对原图片左上角的坐标
     * @param endPoint
     *            结束坐标点，其值[x, y]为相对原图片左上角的坐标
     * @return 被转换前的图像对象
     * 
     * @throws IOException
     *             当读写文件失败时抛出
     */
    public static BufferedImage clipScale(String srcPath,
                                          String taPath,
                                          int[] startPoint,
                                          int[] endPoint)
            throws IOException {
        File srcIm = Files.findFile(srcPath);
        if (null == srcIm)
            throw Lang.makeThrow("Fail to find image file '%s'!", srcPath);

        File taIm = Files.createFileIfNoExists(taPath);
        return clipScale(srcIm, taIm, startPoint, endPoint);
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
        // 创建图像
        BufferedImage re = new BufferedImage(w, h, ColorSpace.TYPE_RGB);
        re.createGraphics().drawImage(im, x, y, nW, nH, Color.black, null);
        // 返回
        return re;
    }

    /**
     * 水平翻转一张图片
     * 
     * @param srcIm
     *            源图片
     * @return 被转换后的图像
     */
    public static BufferedImage flipHorizontal(Object srcIm) {
        BufferedImage im1 = read(srcIm);
        int w = im1.getWidth();
        int h = im1.getHeight();
        BufferedImage flipImage = new BufferedImage(w, h, im1.getType());
        Graphics2D gs = flipImage.createGraphics();
        gs.drawImage(im1, 0, 0, w, h, w, 0, 0, h, null);
        gs.dispose();
        return flipImage;
    }

    /**
     * 水平翻转一张图片
     * 
     * @param srcIm
     *            源图片
     * @param tarIm
     *            目标图片
     * @return 被转换后的图像
     */
    public static BufferedImage flipHorizontal(Object srcIm, File tarIm) {
        BufferedImage flipImage = flipHorizontal(srcIm);
        Images.write(flipImage, tarIm);
        return flipImage;
    }

    /**
     * 垂直翻转一张图片
     * 
     * @param srcIm
     *            源图片
     * @return 被转换后的图像
     */
    public static BufferedImage flipVertical(Object srcIm) {
        BufferedImage im1 = read(srcIm);
        int w = im1.getWidth();
        int h = im1.getHeight();
        BufferedImage flipImage = new BufferedImage(w, h, im1.getType());
        Graphics2D gs = flipImage.createGraphics();
        gs.drawImage(im1, 0, 0, w, h, 0, h, w, 0, null);
        gs.dispose();
        return flipImage;
    }

    /**
     * 垂直翻转一张图片
     * 
     * @param srcIm
     *            源图片
     * @param tarIm
     *            目标图片
     * @return 被转换后的图像
     */
    public static BufferedImage flipVertical(Object srcIm, File tarIm) {
        BufferedImage flipImage = flipVertical(srcIm);
        Images.write(flipImage, tarIm);
        return flipImage;
    }

    public static final int WATERMARK_TOP_LEFT = 1;
    public static final int WATERMARK_TOP_CENTER = 2;
    public static final int WATERMARK_TOP_RIGHT = 3;
    public static final int WATERMARK_CENTER_LEFT = 4;
    public static final int WATERMARK_CENTER = 5;
    public static final int WATERMARK_CENTER_RIGHT = 6;
    public static final int WATERMARK_BOTTOM_LEFT = 7;
    public static final int WATERMARK_BOTTOM_CENTER = 8;
    public static final int WATERMARK_BOTTOM_RIGHT = 9;

    /**
     * 为图片添加水印，可以设定透明度与水印的位置
     * <p>
     * 水印位置默认支持9种，分别是：
     * 
     * TOP_LEFT | TOP_CENTER | TOP_RIGHT CENTER_LEFT | CENTER | CENTER_RIGHT
     * BOTTOM_LEFT | BOTTOM_CENTER | BOTTOM_RIGHT
     * 
     * 
     * @param srcIm
     *            源图片
     * @param markIm
     *            水印图片
     * @param opacity
     *            透明度, 要求大于0小于1, 默认为0.5f
     * @param pos
     *            共9个位置，请使用 Images.WATERMARK_{XXX} 进行设置，默认为
     *            Images.WATERMARK_CENTER
     * @param margin
     *            水印距离四周的边距 默认为0
     * @return
     */
    public static BufferedImage addWatermark(Object srcIm,
                                             Object markIm,
                                             float opacity,
                                             int pos,
                                             int margin) {
        BufferedImage im1 = read(srcIm);
        BufferedImage im2 = read(markIm);

        int cw = im1.getWidth();
        int ch = im1.getHeight();
        int mw = im2.getWidth();
        int mh = im2.getHeight();

        if (opacity > 1 || opacity <= 0) {
            opacity = 0.5f;
        }
        if (pos > 9 || pos <= 0) {
            pos = 5;
        }

        // 计算水印位置
        int px = 0;
        int py = 0;
        switch (pos) {
        case WATERMARK_TOP_LEFT:
            px = margin;
            py = margin;
            break;
        case WATERMARK_TOP_CENTER:
            px = (cw - mw) / 2;
            py = margin;
            break;
        case WATERMARK_TOP_RIGHT:
            px = cw - mw - margin;
            py = margin;
            break;
        case WATERMARK_CENTER_LEFT:
            px = margin;
            py = (ch - mh) / 2;
            break;
        case WATERMARK_CENTER:
            px = (cw - mw) / 2;
            py = (ch - mh) / 2;
            break;
        case WATERMARK_CENTER_RIGHT:
            px = cw - mw - margin;
            py = (ch - mh) / 2;
            break;
        case WATERMARK_BOTTOM_LEFT:
            px = margin;
            py = ch - mh - margin;
            break;
        case WATERMARK_BOTTOM_CENTER:
            px = (cw - mw) / 2;
            py = ch - mh - margin;
            break;
        case WATERMARK_BOTTOM_RIGHT:
            px = cw - mw - margin;
            py = ch - mh - margin;
            break;
        }

        // 添加水印
        Graphics2D gs = im1.createGraphics();
        gs.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        gs.drawImage(im2, px, py, null);
        gs.dispose();

        return im1;
    }

    /**
     * 将一个图片文件读入内存
     * 
     * @param img
     *            图片文件
     * @return 图片对象
     */
    public static BufferedImage read(Object img) {
        try {
            if (img instanceof BufferedImage) {
                return (BufferedImage) img;
            }
            if (img instanceof CharSequence) {
                return ImageIO.read(Files.checkFile(img.toString()));
            }
            if (img instanceof File)
                return ImageIO.read((File) img);

            if (img instanceof URL)
                img = ((URL) img).openStream();

            if (img instanceof InputStream) {
                File tmp = File.createTempFile("nutz_img", ".jpg");
                Files.write(tmp, img);
                try {
                    return read(tmp);
                }
                finally {
                    tmp.delete();
                }
            }
            throw Lang.makeThrow("Unkown img info!! --> " + img);
        }
        catch (IOException e) {
            try {
                InputStream in = null;
                if (img instanceof File)
                    in = new FileInputStream((File) img);
                else if (img instanceof URL)
                    in = ((URL) img).openStream();
                else if (img instanceof InputStream)
                    in = (InputStream) img;
                if (in != null)
                    return readJpeg(in);
            }
            catch (IOException e2) {
                e2.fillInStackTrace();
            }
            return null;
            // throw Lang.wrapThrow(e);
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
     * 将内存中的一个写入输出流
     * 
     * @param im
     *            图片对象
     * @param imFormat
     *            图片格式
     * @param out
     *            输出流
     */
    public static void write(RenderedImage im, String imFormat, OutputStream out) {
        try {
            ImageIO.write(im, imFormat, out);
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * @see #write(RenderedImage, String, OutputStream)
     */
    public static void writeAndClose(RenderedImage im, String imFormat, OutputStream out) {
        try {
            ImageIO.write(im, imFormat, out);
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        finally {
            Streams.safeClose(out);
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
    public static void writeJpeg(RenderedImage im, Object targetJpg, float quality) {
        try {
            ImageWriter writer = ImageIO.getImageWritersBySuffix("jpg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
            ImageOutputStream os = ImageIO.createImageOutputStream(targetJpg);
            writer.setOutput(os);
            writer.write((IIOMetadata) null, new IIOImage(im, null, null), param);
            os.flush();
            os.close();
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * 尝试读取JPEG文件的高级方法,可读取32位的jpeg文件
     * <p/>
     * 来自:
     * http://stackoverflow.com/questions/2408613/problem-reading-jpeg-image-
     * using-imageio-readfile-file
     * 
     */
    private static BufferedImage readJpeg(InputStream in) throws IOException {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("JPEG");
        ImageReader reader = null;
        while (readers.hasNext()) {
            reader = readers.next();
            if (reader.canReadRaster()) {
                break;
            }
        }
        if (reader == null)
            return null;
        ImageInputStream input = ImageIO.createImageInputStream(in);
        reader.setInput(input);
        // Read the image raster
        Raster raster = reader.readRaster(0, null);
        BufferedImage image = createJPEG4(raster);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeJpeg(image, out, 1);
        out.flush();
        return read(new ByteArrayInputStream(out.toByteArray()));
    }

    /**
     * Java's ImageIO can't process 4-component images and Java2D can't apply
     * AffineTransformOp either, so convert raster data to RGB. Technique due to
     * MArk Stephens. Free for any use.
     */
    private static BufferedImage createJPEG4(Raster raster) {
        int w = raster.getWidth();
        int h = raster.getHeight();
        byte[] rgb = new byte[w * h * 3];

        float[] Y = raster.getSamples(0, 0, w, h, 0, (float[]) null);
        float[] Cb = raster.getSamples(0, 0, w, h, 1, (float[]) null);
        float[] Cr = raster.getSamples(0, 0, w, h, 2, (float[]) null);
        float[] K = raster.getSamples(0, 0, w, h, 3, (float[]) null);

        for (int i = 0, imax = Y.length, base = 0; i < imax; i++, base += 3) {
            float k = 220 - K[i], y = 255 - Y[i], cb = 255 - Cb[i], cr = 255 - Cr[i];

            double val = y + 1.402 * (cr - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);

            val = y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base + 1] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);

            val = y + 1.772 * (cb - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base + 2] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);
        }

        raster = Raster.createInterleavedRaster(new DataBufferByte(rgb, rgb.length),
                                                w,
                                                h,
                                                w * 3,
                                                3,
                                                new int[]{0, 1, 2},
                                                null);

        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel cm = new ComponentColorModel(cs,
                                                false,
                                                true,
                                                Transparency.OPAQUE,
                                                DataBuffer.TYPE_BYTE);
        return new BufferedImage(cm, (WritableRaster) raster, true, null);
    }

    /**
     * 生成该图片对应的 Base64 编码的字符串
     * 
     * @param targetFile
     *            图片文件
     * @return 图片对应的 Base64 编码的字符串
     */
    public static String encodeBase64(String targetFile) {
        return encodeBase64(new File(targetFile));
    }

    /**
     * 生成该图片对应的 Base64 编码的字符串
     * 
     * @param targetFile
     *            图片文件
     * @return 图片对应的 Base64 编码的字符串
     */
    public static String encodeBase64(File targetFile) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(targetFile);
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        image.flush();
        try {
            ImageIO.write(image, Files.getSuffixName(targetFile), bos);
            bos.flush();
            bos.close();
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }

        byte[] bImage = baos.toByteArray();

        return Base64.encodeToString(bImage, false);
    }

    /**
     * 在一个RGB画布上重新绘制Image,解决CMYK图像偏色的问题
     */
    public static BufferedImage redraw(BufferedImage img, Color bg) {
        BufferedImage rgbImage = new BufferedImage(img.getWidth(),
                                                   img.getHeight(),
                                                   BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = rgbImage.createGraphics();
        g2d.drawImage(img, 0, 0, bg, null);
        g2d.dispose();
        return rgbImage;
    }
}
