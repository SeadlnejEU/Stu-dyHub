package me.seadlnej.app.utilities;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

public class Base64Converter {

    public static String imageToBase64(Image image) {

        if (image == null) { return null; }

        try {

            // Dimensions
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            PixelReader reader = image.getPixelReader();

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            int[] pixels = new int[width * height];
            reader.getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);
            bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] bytes = baos.toByteArray();

            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Image base64ToImage(String base64) {

        if (base64 == null || base64.isBlank()) { return null; }

        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            return new Image(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}