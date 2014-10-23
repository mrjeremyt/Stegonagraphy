import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;

public class Stegonography {
	
	public static int height;
	public static int width;
	public static int buffer = 0;
	public static final byte my_eof = -1; //Indicates stop reading inside of image while decoding.
	public static int bits_in_buffer = 0;
	
	public static void main(String[] args) throws IOException 
	{
		//determines if the program needs to enkode or dekode
		Boolean enkode = true;
		if(!args[0].substring(1).toLowerCase().equals("e"))
			enkode = false;
			
		//reads in the image file 
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(args[1]));
            height = img.getHeight();
            width = img.getWidth();
        } catch (IOException e) {
        	System.out.println("Invalid image file");
        	System.exit(-1);
        }
        
              
		//read the entire file into this byte array
		byte [] data = Files.readAllBytes(Paths.get(args[2]));

		//create a stream on the array
		ByteArrayInputStream is = new ByteArrayInputStream(data);


		//prints the image statistics no matter whether enkode or dekode
        System.out.println("Input image file: " + Paths.get(args[1]).getFileName().toString() + 
        		", number of pixels: " + (height * width) + ", height: " + height  + ", width: " +  width + ".");
        
        //switches based on enkode or dekode
		if(enkode){
			String img_info[] = Paths.get(args[1]).getFileName().toString().split("\\.");
			String out_img_name = img_info[0] + "-steg." + img_info[1]; 
			File output_file = new File(out_img_name);
			
			
			BufferedImage temp = img;
			
	        ImageIO.write(temp, img_info[1].toString(), output_file);     
	        
			
		}else{
			File output_file = new File(Paths.get(args[2]).getFileName().toString());
			
			
		}
		
  
		int test_byte = 0;
		int [] threebits = {0,0,0};
		LinkedList<Byte> the_bits = new LinkedList<Byte>();
		ArrayList<Integer> my_bits = new ArrayList<Integer>();
		int number_of_bytes = is.available();
		
		while ((test_byte = is.read()) != -1)
		{
			fill_array(my_bits, test_byte);
			System.out.println(my_bits.size());
		}


       
        for(int i = 0; i < width; i++){
        	for(int j = 0; j < height; j++){
        		int pixel = img.getRGB(i, j);
                int alpha = (pixel >> 24) & 0xFF;
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                System.out.println(alpha + " " + r + " " + g + " " + b);
        	}
        }
        
        

	}
	
	
	/*
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!maybe wrong
	 */
	private static boolean enough_bits (ByteArrayInputStream is)
	{		return (is.available() < ((height * width * 3) / (8 * 1024)));  	}  // Abandon all hope, ye who try to understand this code.
	
	
	private static LinkedList<Byte> full_of_byte(ByteArrayInputStream is){
		LinkedList<Byte> x = new LinkedList<Byte>();
		int current_byte = 0;
		int numbits = 0;
		int buffer = 0;
		
		while(current_byte != -1){
			if (numbits >= 3) {
				x.add((byte)(buffer & 0x7));
				buffer >>= 3;
			}else{
				
			}
		}
		
		
		
		return x;	
	}
	
	private static ArrayList<Integer> fill_array (ArrayList<Integer> bytes, int test_byte) throws IOException
	{
		System.out.println("Test byte: " + test_byte);
		((Appendable) bytes).append((char) (test_byte & 0x7));
		test_byte >>= 3;
		((Appendable) bytes).append((char) (test_byte & 0x7));
		test_byte >>= 3;
		( (Appendable) bytes).append((char) (test_byte & 0x7));
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
