package org.nutz.img;

import java.awt.Color;
import java.awt.Graphics;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        Graphics gs = rotatedImage.getGraphics();
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
    public static BufferedImage zoomScale(String srcPath, String taPath, int w, int h, Color bgColor)
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
        bgColor = null == bgColor ? Color.black : bgColor;
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
            nH = (int) (((float) w) / oR);
            x = 0;
            y = (h - nH) / 2;
        }
        // 原图太长
        else if (oR < nR) {
            nH = h;
            nW = (int) (((float) h) * oR);
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
        BufferedImage re = new BufferedImage(w, h, ColorSpace.TYPE_RGB);
        // 得到一个绘制接口
        Graphics gc = re.getGraphics();
        gc.setColor(bgColor);
        gc.fillRect(0, 0, w, h);
        gc.drawImage(im, x, y, nW, nH, bgColor, null);
        // 返回
        return re;
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
    public static BufferedImage zoomScale(BufferedImage im, int w, int h) {
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
        BufferedImage re = new BufferedImage(nW, nH, ColorSpace.TYPE_RGB);
        re.getGraphics().drawImage(im, 0, 0, nW, nH, null);
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
    public static BufferedImage clipScale(Object srcIm, File taIm, int w, int h) throws IOException {
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
        re.getGraphics().drawImage(im, x, y, nW, nH, Color.black, null);
        // 返回
        return re;
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
            if (img instanceof File)
                return ImageIO.read((File) img);
            else if (img instanceof URL)
                img = ((URL) img).openStream();
            if (img instanceof InputStream) {
                File tmp = File.createTempFile("nutz_img", ".jpg");
                Files.write(tmp, (InputStream)img);
                tmp.deleteOnExit();
                return read(tmp);
            }
            throw Lang.makeThrow("Unkown img info!! --> " + img);
        }
        catch (IOException e) {
            try {
                    InputStream in = null;
                    if (img instanceof File)
                        in = new FileInputStream((File)img);
                    else if (img instanceof URL)
                        in = ((URL)img).openStream();
                    else if (img instanceof InputStream)
                        in = (InputStream)img;
                    if (in != null)
                        return readJpeg(in);
            } catch (IOException e2) {
                e2.fillInStackTrace();
            }
            return null;
            //throw Lang.wrapThrow(e);
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

    /**
     * 尝试读取JPEG文件的高级方法,可读取32位的jpeg文件
     * <p/>
     * 来自: http://stackoverflow.com/questions/2408613/problem-reading-jpeg-image-using-imageio-readfile-file
     * 
     * */
    private static BufferedImage readJpeg(InputStream in) throws IOException {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("JPEG");
        ImageReader reader = null;
        while(readers.hasNext()) {
            reader = (ImageReader)readers.next();
            if(reader.canReadRaster()) {
                break;
            }
        }
        ImageInputStream input = ImageIO.createImageInputStream(in);
        reader.setInput(input);
        //Read the image raster
        Raster raster = reader.readRaster(0, null); 
        BufferedImage image = createJPEG4(raster);
        File tmp = File.createTempFile("nutz.img", "jpg"); //需要写到文件,然后重新解析哦
        writeJpeg(image, tmp, 1);
        return read(tmp);
    }
    
      /**                                                                                                                                           
    Java's ImageIO can't process 4-component images                                                                                             
    and Java2D can't apply AffineTransformOp either,                                                                                            
    so convert raster data to RGB.                                                                                                              
    Technique due to MArk Stephens.                                                                                                             
    Free for any use.                                                                                                                           
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
            float k = 220 - K[i], y = 255 - Y[i], cb = 255 - Cb[i],
                    cr = 255 - Cr[i];

            double val = y + 1.402 * (cr - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
                    : (byte) (val + 0.5);

            val = y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base + 1] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
                    : (byte) (val + 0.5);

            val = y + 1.772 * (cb - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base + 2] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
                    : (byte) (val + 0.5);
        }


        raster = Raster.createInterleavedRaster(new DataBufferByte(rgb, rgb.length), w, h, w * 3, 3, new int[]{0, 1, 2}, null);

        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        return new BufferedImage(cm, (WritableRaster) raster, true, null);
    }
}
