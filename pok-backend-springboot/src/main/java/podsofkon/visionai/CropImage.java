package podsofkon.visionai;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CropImage {



    public static void cropImage() throws IOException {
        File imageFile = new File("C:/Javapointers/image.jpg");
        BufferedImage bufferedImage = ImageIO.read(imageFile);

        File pathFile = new File("C:/Javapointers/image-crop.jpg");
        ImageIO.write(cropImage(bufferedImage, 0, 0, 1, 1),"jpg", pathFile);
    }

    /**
     * Crops an image to the specified region
     * @param bufferedImage the image that will be crop
     * @param x the upper left x coordinate that this region will start
     * @param y the upper left y coordinate that this region will start
     * @param width the width of the region that will be crop
     * @param height the height of the region that will be crop
     * @return the image that was cropped.
     */
    public static BufferedImage cropImage(BufferedImage bufferedImage, int x, int y, int width, int height){
        BufferedImage croppedImage = bufferedImage.getSubimage(x, y, width, height);
        return croppedImage;
    }
}
