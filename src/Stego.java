import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Stego {

	public static void main(String[] args) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("inputImage.bmp"));
        } catch (IOException e) {

        }
        int height = img.getHeight();
        int width = img.getWidth();

        int amountPixel = 0;

	// This prints the image height and width and a specific pixel. 

        System.out.println(height  + "  " +  width + " " + img.getRGB(30, 30));
	}

}
