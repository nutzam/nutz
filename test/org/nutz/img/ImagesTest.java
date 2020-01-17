package org.nutz.img;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.lang.Files;
import org.nutz.lang.random.R;

public class ImagesTest {

    @Test
    public void test_c() throws Throwable {
        File file = Files.findFile(getClass().getPackage().getName().replace('.', '/')
                                    + "/snapshot.jpg");
        //System.out.println(file.length());
        Images.clipScale(file, File.createTempFile("abc", "jpg"), 256, 256);
    }
    
    @Test
    public void test_clipScale_url() throws Throwable {
        File file = Files.findFile(getClass().getPackage().getName().replace('.', '/')
                                    + "/snapshot.jpg");
        Images.clipScale(file.toURI().toURL(), File.createTempFile("abc", "jpg"), 256, 256);
    }

    @Test
    public void createCaptcha() {
        String text = R.captchaChar(4);
        int  w = 145;
        int h = 35;
        BufferedImage img = Images.createCaptcha(text, w, h, null, "FFF", null);
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());
        Assert.assertEquals(img.getHeight(),h);
        Assert.assertEquals(img.getWidth(),w);
    }

    @Test
    public void createCaptcha1() {
        BufferedImage img =Images.createCaptcha("小明");
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());
    }

    @Test
    public void rotate() {
        BufferedImage img =Images.createAvatar("小明");
        img = Images.rotate(img, 90);
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());

    }

    @Test
    public void rotate1() {
    }

    @Test
    public void rotate2() {
    }

    @Test
    public void zoomScale() {
        BufferedImage img =Images.createAvatar("小明");
        img = Images.zoomScale(img, 160, 180, Color.WHITE);
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());
    }

    @Test
    public void zoomScale1() {
    }

    @Test
    public void zoomScale2() {
    }

    @Test
    public void zoomScale3() {
    }

    @Test
    public void scale() {
    }

    @Test
    public void clipScale() {
        String text = R.captchaChar(4);
        int  w = 145;
        int h = 35;
        BufferedImage srcImg = Images.createText(text, w, h, null, "FFF", null,0,2);
        BufferedImage img = Images.clipScale(srcImg,  w-10, h-10);
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());
        Assert.assertEquals(img.getHeight(),h-10);
        Assert.assertEquals(img.getWidth(),w-10);

    }

    @Test
    public void clipScale1() {
    }

    @Test
    public void clipScale2() {
    }

    @Test
    public void clipScale3() {
    }

    @Test
    public void clipScale4() {
    }

    @Test
    public void clipScale5() {
    }

    @Test
    public void flipHorizontal() {
        BufferedImage img =Images.createAvatar("小明");
        img = Images.flipHorizontal(img);
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());

    }

    @Test
    public void flipHorizontal1() {
    }

    @Test
    public void flipVertical() {
        BufferedImage img =Images.createAvatar("小明");
        img = Images.flipVertical(img);
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());
    }

    @Test
    public void flipVertical1() {
    }

    @Test
    public void twist() {
        BufferedImage img =Images.createAvatar("小明");
        img = Images.twist(img, 1, "#FFF");
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());
    }

    @Test
    public void addWatermark() {
    }

    @Test
    public void grayImage() {
        BufferedImage img =Images.createAvatar("小明");
        BufferedImage img2 =Images.grayImage(img);
        Assert.assertNotNull(img2);
        Assert.assertNotNull(img2.getSource());


    }

    @Test
    public void multiply() {
        BufferedImage bgImg =Images.createAvatar("小明");
        String text = R.captchaChar(4);
        BufferedImage itemImg = Images.createText(text);
        BufferedImage img =Images.multiply(bgImg, itemImg, 0, 0);
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());
    }

    @Test
    public void cutoutByLuminance() {
        BufferedImage srcImg =Images.createAvatar("小明");
        BufferedImage img = Images.cutoutByLuminance(srcImg);
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());
    }

    @Test
    public void cutoutByChannel() {
        BufferedImage srcImg =Images.createAvatar("小明");
        BufferedImage img = Images.cutoutByChannel(srcImg, Images.CHANNEL_BLUE);
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());
    }

    @Test
    public void cutoutByPixel() {
        BufferedImage srcImg =Images.createAvatar("小明");
        BufferedImage img = Images.cutoutByPixel(srcImg, 0, 0, 20);
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());

    }

    @Test
    public void channelImage() {
        BufferedImage img =Images.createAvatar("小明");
        BufferedImage img2 =Images.channelImage(img,Images.CHANNEL_RED);
        Assert.assertNotNull(img2);
        Assert.assertNotNull(img2.getSource());
    }

    @Test
    public void read() {
        // 可以是URL对象
        try {
            BufferedImage img =Images.read(new URL("https://www.baidu.com/img/bdlogo.png"));
            Assert.assertNotNull(img);
            Assert.assertNotNull(img.getSource());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void write() {
    }

    @Test
    public void write1() {
    }

    @Test
    public void writeAndClose() {
    }

    @Test
    public void writeJpeg() {
    }

    @Test
    public void encodeBase64() {
    }

    @Test
    public void encodeBase641() {
    }

    @Test
    public void redraw() {
    }

    @Test
    public void createText() {
        String text = R.captchaChar(4);
        int  w = 145;
        int h = 35;
        BufferedImage img = Images.createText(text, w, h, null, "FFF", null,0,2);
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());
        Assert.assertEquals(img.getHeight(),h);
        Assert.assertEquals(img.getWidth(),w);
    }

    @Test
    public void createText1() {
        String text = R.captchaChar(4);
        BufferedImage img = Images.createText(text);
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());
    }

    @Test
    public void createAvatar() {
        BufferedImage img =Images.createAvatar("小明");
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());
        img = Images.createAvatar("小二", 256, "rgba(255,0,0,1)", "rgb(0,0,255)", "微软雅黑", 64, Font.ITALIC);
        Assert.assertNotNull(img);
        Assert.assertNotNull(img.getSource());
    }


}
