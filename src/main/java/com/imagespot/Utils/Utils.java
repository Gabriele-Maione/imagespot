package com.imagespot.Utils;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Utils {

    public static Image crop(Image img) {

        double d = Math.min(img.getWidth(),img.getHeight());
        double x = (d-img.getWidth())/2;
        double y = (d-img.getHeight())/2;

        Canvas canvas = new Canvas(d, d);
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.drawImage(img, x, y);

        return canvas.snapshot(null, null);
    }

    public static InputStream photoScaler(File photo) throws IOException {

        BufferedImage bufferedImage = ImageIO.read(photo.getAbsoluteFile());
        bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, 700, 700, Scalr.OP_ANTIALIAS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpeg", baos);
        InputStream preview = new ByteArrayInputStream(baos.toByteArray());
        return preview;
    }
}
