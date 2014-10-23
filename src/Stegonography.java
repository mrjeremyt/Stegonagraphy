import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

public class Stegonography {
	
	public static int height;
	public static int width;
	public static int buffer = 0;
	public static final byte my_eof = -1; //Indicates stop reading inside of image while decoding.
	public static int bits_in_buffer = 0;
	
	public static void main(String[] args) throws IOException 
	{
		Boolean enkode = true;
		if(!args[0].substring(1).toLowerCase().equals("e"))
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

		int test_byte = 0;
		int [] threebits = {0,0,0};
		ArrayList<Integer> my_bits = new ArrayList<Integer>();
		int number_of_bytes = is.available();
		
		while ((test_byte = is.read()) != -1)
		{
			fill_array(my_bits, test_byte);
			System.out.println(my_bits.size());
		}
		
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
		
        height = img.getHeight();
        width = img.getWidth();

        int amountPixel = 0;

        System.out.println(height  + "  " +  width + " " + img.getRGB(30, 30));

        
        for(int i = 0; i < width; i++){
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
        System.out.println("Full number of pixels: " + amountPixel);
        
        
	// This prints the image height and width and a specific pixel. 

	}
	
	private static boolean enough_bits (ByteArrayInputStream is)
	{		return (is.available() < ((height * width * 3) / (8 * 1024)));  	}  // Abandon all hope, ye who try to understand this code.
	
	private static ArrayList<Integer> fill_array (ArrayList<Integer> bytes, int test_byte) throws IOException
	{
		System.out.println("Test byte: " + test_byte);
		((Appendable) bytes).append((char) (test_byte & 0x7));
		test_byte >>= 3;
		((Appendable) bytes).append((char) (test_byte & 0x7));
		test_byte >>= 3;
		((Appendable) bytes).append((char) (test_byte & 0x7));
//		for (int e : bytes)
//			System.out.print(e);
//		System.out.println();
		return bytes;
	}
	
	private static int recon_char (int [] bytes)
	{
		int recon = 0;
		recon |= bytes[0];
		recon |= bytes[1] << 3;
		recon |= bytes[2] << 6;
		return recon;
	}
	
	//The below code is horrible, and I feel ashamed I wrote it...
	//you should be ashamed. This is sheet
	private static int get_3bits (ByteArrayInputStream is)
	{
		int newbit = 0;
		if (bits_in_buffer > 3)
		{
			System.out.println("Buffer in bits > 3 begins at: " + buffer);
			bits_in_buffer -= 3;
			int threebits = buffer & 0x7;
			buffer >>= 3;
			System.out.println("Threebits in bits >3 is: " + threebits);
			System.out.println("Bits in buffer is: " + bits_in_buffer);
			return threebits;
		}
		else
		{
			if ((newbit = is.read()) !=-1)
			{
				System.out.println("Newbit is : " + newbit);
				buffer = (newbit << bits_in_buffer) | buffer;
				System.out.println("Buffer after insertion is: " + buffer);
				int threebit = buffer & 0x7;
				buffer >>= 3;
				System.out.println("Three bit is : " + threebit);
				System.out.println("Buffer after threebit removal is: " + buffer);
				bits_in_buffer += 5;
				return threebit;
			}
			else
				return my_eof;
		}
	}
}
