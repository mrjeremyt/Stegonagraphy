import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

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
      
        Path msg_path = Paths.get(args[2]);
        
		//read the entire file into this byte array
		byte [] data = Files.readAllBytes(msg_path);

		//create a stream on the array
		ByteArrayInputStream is = new ByteArrayInputStream(data);
		
		
		
		File f = null;
		String img_info[] = null;
		
		if(enkode){
			img_info = Paths.get(args[1]).getFileName().toString().split("\\.");
			String out_img_name = img_info[0] + "-steg." + img_info[1]; 
			f = new File(out_img_name);
		}else{
			
		}
		
		
		
		
        

		
		BufferedImage temp = img;
		
        boolean x = ImageIO.write(temp, img_info[1].toString(), f);       
		
		
        int height = img.getHeight();
        int width = img.getWidth();

        int amountPixel = 0;

        System.out.println(height  + "  " +  width + " " + img.getRGB(30, 30));

        
/*        for(int i = 0; i < width; i++){
        	for(int j = 0; j < height; j++){
        		int pixel = img.getRGB(i, j);
                int alpha = (pixel >> 24) & 0xFF;
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                System.out.println(alpha + " " + r + " " + g + " " + b);
                amountPixel++;
        	}
        }
        System.out.println("Full number of pixels: " + amountPixel);*/
        
        
	// This prints the image height and width and a specific pixel. 

	}

}
