import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Stegonography {

	public static void main(String[] args) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(args[0]));
        } catch (IOException e) {

        }
        int height = img.getHeight();
        int width = img.getWidth();

        int amountPixel = 0;

        int pixel = img.getRGB(30, 30);
        int alpha = (pixel >> 24) & 0xFF;
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;
        
	// This prints the image height and width and a specific pixel. 

        System.out.println(height  + "  " +  width + " " + img.getRGB(30, 30));
        System.out.println(alpha + " " + r + " " + g + " " + b);
	}

}
