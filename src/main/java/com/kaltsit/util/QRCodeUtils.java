package com.kaltsit.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @Author wangcy
 * @Date 2021/3/17 16:15
 */
public class QRCodeUtils {

    public static void createPNG(String contents, int size, File file) throws Exception {
        if(file == null)
            throw new Exception("file is null");
        String filename = file.getName();
        String format= "png";
        if(!format.equalsIgnoreCase(filename.substring(filename.length() - 3)))
            throw new Exception("file ext not supported");
        byte[] qr = create(contents, format, size);
        OutputStream os = new FileOutputStream(file);
        os.write(qr);
        os.flush();
        os.close();
    }

    public static byte[] create(String contents, String format, int size) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        BitMatrix bitMatrix = new QRCodeWriter().encode(contents, BarcodeFormat.QR_CODE, size, size, hints);
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        int height = bitMatrix.getHeight();
        int width = bitMatrix.getWidth();
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? Color.black.getRGB() : Color.white.getRGB());
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, format, os);
        return os.toByteArray();
    }

}
