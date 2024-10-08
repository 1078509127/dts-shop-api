package com.qiguliuxing.dts.wx.util;


import com.google.zxing.Result;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

/**
 * 二维码的工具类
 * @author fyh
 *
 */
public class ZXingUtil {

    //定义int类型的常量用于存放生成二维码时的类型
    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    /**
     * 加密：文字-->图片  将文本变成二维数组，
     * @param imagePath
     * @param format:文件格式及后缀名
     * @param content
     * @throws WriterException
     * @throws IOException
     */
    public static BitMatrix encodeimage(String imagePath , String format , String content , int width , int height , String logo) throws WriterException, IOException{

        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType , Object>();
        //容错率:L(7%)<M(15%)<Q(25%)<H(35%);H容错率最高
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //外边距
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height , hints);
        return bitMatrix;
    }

    /**
     * 解密，将生成的二维码转换成文字
     * @param file:二维码文件
     * @throws Exception
     */
    public static String decodeImage(File file) throws Exception{

        //首先判断文件是否存在
        if(!file.exists()){
            return "";
        }
        //将file转换成内存中的一张图片
        BufferedImage image = ImageIO.read(file);
        MultiFormatReader formatter = new MultiFormatReader();
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        Binarizer binarizer = new HybridBinarizer(source);
        BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
        //将图片的文字信息解析到result中
        Result result = formatter.decode(binaryBitmap);
        System.out.println(result.getText());
        return result.getText();
    }

    /**
     * 传logo和二维码，生成-->带logo的二维码
     * @param matrixImage :二维码
     * @param logo ： 中间的logo
     * @return
     * @throws IOException
     */
    public static BufferedImage logoImg(BufferedImage matrixImage ,String logo) throws IOException{
        //在二维码上画logo:产生一个二维码画板
        Graphics2D g2 = matrixImage.createGraphics();
        //画logo,将String类型的logo图片存放入内存中;即 string-->BufferedImage
        BufferedImage logoImage = ImageIO.read(new File(logo));
        //获取二维码的高和宽
        int height = matrixImage.getHeight();
        int width = matrixImage.getWidth();
        /**
         * 纯log图片
         * logoImage：内存中的图片
         * 在二维码的高和宽的2/5,2/5开始画log,logo占用整个二维码的高和宽的1/5,1/5
         */
        g2.drawImage(logoImage,width*2/5, height*2/5, width*1/5, height*1/5, null);

        /**
         * 画白色的外边框
         * 产生一个画白色圆角正方形的画笔
         * BasicStroke.CAP_ROUND:画笔的圆滑程度，此处设置为圆滑
         * BasicStroke.JOIN_ROUND：在边与边的连接点的圆滑程度，此处设置为圆滑
         */
        BasicStroke stroke = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        //将画板和画笔关联起来
        g2.setStroke(stroke);

        /**
         * 画一个正方形
         * RoundRectangle2D是一个画长方形的类，folat是他的内部类
         */
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(width*2/5, height*2/5, width*1/5, height*1/5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        //设置为画白色
        g2.setColor(Color.WHITE);
        g2.draw(round);

        //画灰色的内边框，原理与画白色边框相同
        BasicStroke stroke2 = new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke2);

        RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(width*2/5+2, height*2/5+2, width*1/5-4, height*1/5-4,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        //另一种设置灰色的方法：Color color = new Color(128,128,128);其中三个参数是 R G  B
        g2.setColor(Color.GRAY);
        g2.draw(round2);

        //释放内存
        g2.dispose();
        //刷新二维码
        matrixImage.flush();
        return matrixImage;
    }
}
