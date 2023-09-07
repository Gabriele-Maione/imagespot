package com.imagespot.Utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Circle;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.imgscalr.Scalr;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.util.*;
import java.util.List;

public class Utils {
    public static Image crop(Image img) {
        int width = (int) img.getWidth();
        int height = (int) img.getHeight();
        int cropSize = Math.min(width, height);

        // Calculate the x and y coordinates of the top-left corner of the cropped image
        int x = (width - cropSize) / 2;
        int y = (height - cropSize) / 2;

        PixelReader pixelReader = img.getPixelReader();
        return new WritableImage(pixelReader, x, y, cropSize, cropSize);
    }
    public static InputStream photoScaler(File photo) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(photo.getAbsoluteFile());
        bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, 700, 700, Scalr.OP_ANTIALIAS);
        bufferedImage = Scalr.crop(bufferedImage, (bufferedImage.getWidth() - Math.min(bufferedImage.getWidth(), bufferedImage.getHeight())) / 2,
                (bufferedImage.getHeight() - Math.min(bufferedImage.getWidth(), bufferedImage.getHeight())) / 2, Math.min(bufferedImage.getWidth(), bufferedImage.getHeight()),
                Math.min(bufferedImage.getWidth(), bufferedImage.getHeight()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage output = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        output.createGraphics()
                .drawImage(bufferedImage, 0, 0, Color.WHITE, null);
        ImageIO.write(output, "jpeg", baos);

        return new ByteArrayInputStream(baos.toByteArray());
    }
    public static File photoScaler2(File photo) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(photo.getAbsoluteFile());
        bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, 700, 700, Scalr.OP_ANTIALIAS);
        bufferedImage = Scalr.crop(bufferedImage, (bufferedImage.getWidth() - Math.min(bufferedImage.getWidth(), bufferedImage.getHeight())) / 2,
                (bufferedImage.getHeight() - Math.min(bufferedImage.getWidth(), bufferedImage.getHeight())) / 2, Math.min(bufferedImage.getWidth(), bufferedImage.getHeight()),
                Math.min(bufferedImage.getWidth(), bufferedImage.getHeight()));

        File tempFile = File.createTempFile("scaledphoto", ".jpeg");

        BufferedImage output = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        output.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
        ImageIO.write(output, "jpeg", tempFile);

        return tempFile;
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

        if(list.size() == 0)
            return "255, 255, 255";

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
    public static void setAvatarRounde(ImageView avatar){
        double avatarWidth = avatar.getFitWidth() / 2;
        Circle circle = new Circle(avatarWidth, avatarWidth, avatarWidth);
        avatar.setClip(circle);
    }
    public static Image getCollectionPreview(List<Image> images){
        try {
            int collageSize = 400;
            int imagesSize = collageSize / 2;
            BufferedImage image1 = resizeImage(imageToBufferedImage(images.get(0)), imagesSize);
            BufferedImage image2 = resizeImage(imageToBufferedImage(images.get(1)), imagesSize);
            BufferedImage image3 = resizeImage(imageToBufferedImage(images.get(2)), imagesSize);
            BufferedImage image4 = resizeImage(imageToBufferedImage(images.get(3)), imagesSize);


            BufferedImage collage = new BufferedImage(collageSize, collageSize, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = collage.createGraphics();

            g2d.drawImage(image1, 0, 0, null);
            g2d.drawImage(image2, image1.getWidth(), 0, null);
            g2d.drawImage(image3, 0, image1.getHeight(), null);
            g2d.drawImage(image4, image1.getWidth(), image1.getHeight(), null);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage output = new BufferedImage(collage.getWidth(), collage.getHeight(), BufferedImage.TYPE_INT_RGB);
            output.createGraphics().drawImage(collage, 0, 0, Color.WHITE, null);
            ImageIO.write(output, "jpeg", baos);

            return new Image(new ByteArrayInputStream(baos.toByteArray()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static BufferedImage imageToBufferedImage(Image image){
        // Convert the JavaFX Image to a BufferedImage
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Get the PixelReader from the JavaFX Image
        PixelReader pixelReader = image.getPixelReader();

        // Get the writable pixel array from the BufferedImage
        int[] pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();

        // Copy pixel data from JavaFX Image to BufferedImage
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = pixelReader.getArgb(x, y);
            }
        }

        return bufferedImage;
    }
    private static BufferedImage resizeImage(BufferedImage originalImage, int size) throws IOException {
        BufferedImage resizedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, size, size, null);
        graphics2D.dispose();
        return resizedImage;
    }
    public static String uploadFile(File file, File preview){
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("image", new FileBody(file));
            builder.addPart("preview", new FileBody(preview));
            HttpUriRequest request = RequestBuilder.post()
                    .setUri("https://myapplications.altervista.org/imagespot/upload.php")
                    .setHeader(HttpHeaders.ACCEPT, "application/json")
                    .setEntity(builder.build()).build();
            HttpResponse response = httpClient.execute(request);

            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
