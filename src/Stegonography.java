import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

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
			
			LinkedList<Byte> the_bits = full_of_byte(is);
			Iterator<Byte> it = the_bits.iterator();
			boolean do_one_more = false;
	        for(int i = 0; i < width; i++){
	        	for(int j = 0; j < height; j++){
	        		if(it.hasNext()){
		        		int pixel = img.getRGB(i, j);
		                int alpha = (pixel >> 24) & 0xFF;
		                int r = (pixel >> 16) & 0xFF;
		                int g = (pixel >> 8) & 0xFF;
		                int b = pixel & 0xFF;
		                int newrgb = newrgb(alpha, r, g, b, it.next());
		                temp.setRGB(i, j, newrgb);
	        		}else{
	        			if(do_one_more){
	        				//insert EOF part 2
	        				//exit loop
	        			}else{
	        				//insert EOF part 1
	        				do_one_more = true;
	        			}
	        			
	        		}

	        	}
	        }
        	
			
			
			
			
	        ImageIO.write(temp, img_info[1].toString(), output_file);     
	        
			
		}else{
			File output_file = new File(Paths.get(args[2]).getFileName().toString());
			
			
		}
		
  
//		int test_byte = 0;
//		int [] threebits = {0,0,0};
//		ArrayList<Integer> my_bits = new ArrayList<Integer>();
//		int number_of_bytes = is.available();
//		
//		while ((test_byte = is.read()) != -1)
//		{
//			fill_array(my_bits, test_byte);
//			System.out.println(my_bits.size());
//		}


       
//		Random ran = new Random();
//        for(int i = 0; i < width; i++){
//        	for(int j = 0; j < height; j++){
//        		int pixel = img.getRGB(i, j);
//                int alpha = (pixel >> 24) & 0xFF;
//                int r = (pixel >> 16) & 0xFF;
//                int g = (pixel >> 8) & 0xFF;
//                int b = pixel & 0xFF;
//                alpha = ran.nextInt(256);
////                r += 100;
//                int newrgb = 0;
//                newrgb = (b | newrgb);
//                newrgb = (g << 8) | newrgb;
//                newrgb = (r << 16) | newrgb;
//                newrgb = (alpha << 24) | newrgb;
//                System.out.println(alpha + " " + r + " " + g + " " + b);
////                img.setRGB(i, j, newrgb);
////                System.out.println("New Rgb is: " + ((newrgb >> 24) & 0xFF)  + " " + ((newrgb >> 16) & 0xFF) + " " + g + " " + b);
//        	}
//        }
        
        

	}
	
	
	/*
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!maybe wrong
	 */
	private static boolean enough_bits (ByteArrayInputStream is)
	{		return ((is.available() + 1) <= ((height * width * 4) / 8));  	}  // Abandon all hope, ye who try to understand this code.
	
	
	private static LinkedList<Byte> full_of_byte(ByteArrayInputStream is){
		LinkedList<Byte> x = new LinkedList<Byte>();
		int current_byte = 0;
			
		while((current_byte = is.read()) != -1){
			byte a = (byte) ((current_byte >> 4) & 0xF);
			byte b = (byte) (current_byte & 0xF);
			x.add(a); x.add(b);
		}
		return x;	
	}
	
	private static int newrgb(int alpha, int r, int g, int b, Byte by){
		int newrgb = 0;
		newrgb = (b | newrgb);
		newrgb = (g << 8) | newrgb;
		newrgb = (r << 16) | newrgb;
		newrgb = (alpha << 24) | newrgb;
		return newrgb;
	}
	
	
//	private static ArrayList<Integer> fill_array (ArrayList<Integer> bytes, int test_byte) throws IOException
//	{
//		System.out.println("Test byte: " + test_byte);
//		bytes.add((test_byte & 0x7));
//		test_byte >>= 3;
//		bytes.add((test_byte & 0x7));
//		test_byte >>= 3;
//		bytes.add((test_byte & 0x7));
//		return bytes;
//	}
	
//	private static int recon_char (int [] bytes)
//	{
//		int recon = 0;
//		recon |= bytes[0];
//		recon |= bytes[1] << 3;
//		recon |= bytes[2] << 6;
//		return recon;
//	}
	
//	//The below code is horrible, and I feel ashamed I wrote it...
//	//you should be ashamed. This is sheet
//	//I am ashamed...
//	private static int get_3bits (ByteArrayInputStream is)
//	{
//		int newbit = 0;
//		if (bits_in_buffer > 3)
//		{
//			System.out.println("Buffer in bits > 3 begins at: " + buffer);
//			bits_in_buffer -= 3;
//			int threebits = buffer & 0x7;
//			buffer >>= 3;
//			System.out.println("Threebits in bits >3 is: " + threebits);
//			System.out.println("Bits in buffer is: " + bits_in_buffer);
//			return threebits;
//		}
//		else
//		{
//			if ((newbit = is.read()) !=-1)
//			{
//				System.out.println("Newbit is : " + newbit);
//				buffer = (newbit << bits_in_buffer) | buffer;
//				System.out.println("Buffer after insertion is: " + buffer);
//				int threebit = buffer & 0x7;
//				buffer >>= 3;
//				System.out.println("Three bit is : " + threebit);
//				System.out.println("Buffer after threebit removal is: " + buffer);
//				bits_in_buffer += 5;
//				return threebit;
//			}
//			else
//				return my_eof;
//		}
//	}
}
