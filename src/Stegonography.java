import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class Stegonography {

	public static void main(String[] args) throws IOException {
		Boolean enkode = true;
		if(!args[0].substring(1).equals("E"))
			enkode = false;
			
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(args[1]));
        } catch (IOException e) {
        	System.out.println("Invalid image file");
        }
        
        Path path = Paths.get(args[2]);
        
		//read the entire file into this byte array
		byte [] data = Files.readAllBytes(path);

		//create a stream on the array
		ByteArrayInputStream is = new ByteArrayInputStream(data);
        
        
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
