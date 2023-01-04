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
import java.util.*;
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
        BufferedImage output = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
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

    public static String getMostCommonColour(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        HashMap<Integer, Integer> map = new HashMap<>();

        for(int i=0; i < width ; i+=10) {
            for(int j=0; j < height ; j+=10) {
                int rgb = image.getPixelReader().getArgb(i, j);
                int[] rgbArr = getRGBArr(rgb);
                if (!isGray(rgbArr)) {
                    Integer counter = map.get(rgb);
                    if (counter == null)
                        counter = 0;
                    counter++;
                    map.put(rgb, counter);
                }
            }
        }

        ArrayList<Integer> list = new ArrayList<>();
        for(Map.Entry<Integer, Integer> entry : map.entrySet())
            list.add(entry.getValue());

        Collections.sort(list);

        int pixel = getPixel(map, list.get(list.size()-1));

        if(pixel == -1)
            return "255, 255, 255";
        else{
            StringBuilder result = new StringBuilder();
            int[] rgb = getRGBArr(pixel);

            for(int color : rgb){
                result.append(color).append(",");
            }
            return result.substring(0, result.length()-1);
        }
    }

    private static int getPixel(HashMap<Integer, Integer> map, int value){
        for(Map.Entry<Integer, Integer> temp : map.entrySet()){
            if(temp.getValue() == value)
                return temp.getKey();
        }
        return -1;
    }

    private static int[] getRGBArr(int pixel) {
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        return new int[]{red,green,blue};
    }
    private static boolean isGray(int[] rgbArr) {
        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];

        int tolerance = 10;
        if (rgDiff > tolerance || rgDiff < -tolerance)
            return rbDiff <= tolerance && rbDiff >= -tolerance;
        return true;
    }

    public static void retrievePostsTask(Task<java.util.List<Post>> task, FlowPane flowPane) {
        new Thread(task).start();
        task.setOnSucceeded(workerStateEvent -> {
            List<Post> posts = task.getValue();

            for (Post post : posts) {

                VBox postBox = ViewFactory.getInstance().getPostPreview(post);

                flowPane.getChildren().add(postBox);

            }
        });
    }

}
