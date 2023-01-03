package com.imagespot.Utils;

import com.imagespot.View.ViewFactory;
import com.imagespot.model.Post;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
import java.util.List;

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
        BufferedImage output = new BufferedImage(bufferedImage.getWidth(),
                bufferedImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        output.createGraphics()
                        .drawImage(bufferedImage, 0, 0, Color.WHITE, null);
        ImageIO.write(output, "jpeg", baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public static String getRes(File file) {
        Image i = new Image(file.getAbsolutePath());
        return ((int)i.getHeight() + "x" + (int)i.getWidth());
    }
    public static String getExt(File file) {
        return FilenameUtils.getExtension(file.getName());
    }
    public static int getSize(File file) {
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        return (int)(file.length()/1024);
    }

    public static void retrievePostsTask(Task<java.util.List<Post>> task, FlowPane flowPane) {
        new Thread(task).start();
        task.setOnSucceeded(workerStateEvent -> {
            List<Post> yourPosts = task.getValue();

            for (Post yourPost : yourPosts) {

                VBox postBox = ViewFactory.getInstance().getPostPreview(yourPost);

                flowPane.getChildren().add(postBox);

            }
        });
    }

}
