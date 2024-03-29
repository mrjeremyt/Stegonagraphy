import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import javax.imageio.ImageIO;

public class Stegonography {
	
	public static int height;
	public static int width;
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

		//prints the image statistics no matter whether enkode or dekode
        System.out.println("Input image file: " + Paths.get(args[1]).getFileName().toString() + 
        		", number of pixels: " + (height * width) + ", height: " + height  + ", width: " +  width + ".");
        
        //switches based on enkode or dekode
		if(enkode){
			//read the entire file into this byte array
			byte [] data = Files.readAllBytes(Paths.get(args[2]));
			
			//create a stream on the array
			ByteArrayInputStream is = new ByteArrayInputStream(data);
			
			String img_info[] = Paths.get(args[1]).getFileName().toString().split("\\.");
			if (img_info[1].equals("jpeg") || img_info[1].equals("jpg"))
				img_info[1] = "bmp";
			String out_img_name = img_info[0] + "-steg." + img_info[1]; 
			File output_file = new File(out_img_name);
			
			BufferedImage temp = img;
			
			LinkedList<Byte> the_bits = full_of_byte(is);
			Iterator<Byte> it = the_bits.iterator();
			int num_times_for_zero = 0;
			
			outerloop:
	        for(int i = 0; i < width; i++){
	        	for(int j = 0; j < height; j++){
	        		int pixel = img.getRGB(i, j);
	                int r = (pixel >> 16) & 0xFF;
	                int g = (pixel >> 8) & 0xFF;
	                int b = pixel & 0xFF;
	                
	        		if(it.hasNext())
	        		{
	        			byte test_byte = it.next();
//	        			System.out.println(Integer.toBinaryString((test_byte & 0xFF) + 0x10).substring(1)); //Converts byte to binary string.
		                int newrgb = newrgb(r, g, b, test_byte);
		                
		                temp.setRGB(i, j, newrgb);
	        		}
	        		else
	        		{
        				if (num_times_for_zero < 3)
        				{
        					int rgbnew = write_eof(r, g, b);
        					temp.setRGB(i,j,rgbnew);
        					num_times_for_zero++;
        					continue;
        				}
        				else
        					break outerloop;
	        		}

	        	}
	        }
	        ImageIO.write(temp, img_info[1].toString(), output_file);
		}else{
			File output_file = new File(Paths.get(args[2]).getFileName().toString() + "-out");
			FileOutputStream fs = new FileOutputStream(output_file);
			Byte buffer = 0;
			int current_iter = 0;

			outerloop:
	        for(int i = 0; i < width; i++){
	        	for(int j = 0; j < height; j++)
	        	{
	                if(current_iter < 3)
	                {
		        		Color c = new Color(img.getRGB(i, j), true);
		        		int r = c.getRed();
		        		int g = c.getGreen();
		        		int b = c.getBlue();
	                	buffer = decode_rgb(r, g, b, current_iter, buffer);
	                	current_iter++;
	                }
	                else
	                {
	                	j--;
	                	current_iter = 0;
	                	if(buffer.byteValue() != 0)
	                	{
	                		fs.write(new byte[]{buffer.byteValue()});
	                		buffer = 0;
	                	}
	                	else  		break outerloop;
	                }
	        	}
	        }
	        fs.close();
		}        

	}
	
	private static int write_eof (int r, int g, int b)
	{
		if ((r % 2) == 1){
			r-=1;
		}
		if ((g % 2) == 1){
			g-=1;
		}
		if ((b % 2) == 1){
			b-=1;
		}
		int newrgb = 0;
		newrgb = (b | newrgb);
		newrgb = (g << 8) | newrgb;
		newrgb = (r << 16) | newrgb;
		return newrgb;
	}
	
	private static void print_rgb (int rgb, boolean old)
	{
		if (old)
			System.out.println("Old Rgb is: " + " " + ((rgb >> 16) & 0xFF) + " " + ((rgb >> 8) & 0xFF) + " " + (rgb & 0xFF));
		else
			System.out.println("New Rgb is: " + " " + ((rgb >> 16) & 0xFF) + " " + ((rgb >> 8) & 0xFF) + " " + (rgb & 0xFF));
	}
	
	private static Byte decode_rgb (int r, int g, int b, int pos, Byte buffer) // pos = 0 means highest. 2 = lowest.
	{
		if (pos == 0)
		{
			if ((g %2) == 1) 	buffer = (byte) (buffer | (0 << 7));
			else	buffer = (byte) (buffer | (0 << 7));
			
			if ((b %2) == 1) 	buffer = (byte) (buffer | (1 << 6));
			else	buffer = (byte) (buffer | (0 << 6));
		}
		else if (pos == 1){			
			if ((r %2) == 1) 	buffer = (byte) (buffer | (1 << 5));
			else	buffer = (byte) (buffer | (0 << 5));
			
			if ((g %2) == 1) 	buffer = (byte) (buffer | (1 << 4));
			else	buffer = (byte) (buffer | (0 << 4));
			
			if ((b %2) == 1) 	buffer = (byte) (buffer | (1 << 3));
			else	buffer = (byte) (buffer | (0 << 3));
		}
		else if (pos == 2)
		{
			if ((r %2) == 1) 	buffer = (byte) (buffer | (1 << 2));
			else	buffer = (byte) (buffer | (0 << 2));
			
			if ((g %2) == 1) 	buffer = (byte) (buffer | (1 << 1));
			else	buffer = (byte) (buffer | (0 << 1));
			
			if ((b %2) == 1) 	buffer = (byte) (buffer | (1));
			else	buffer = (byte) (buffer | (0));
		}
		else{
			System.err.println("Oh crap, something went horrible wrong...All is doom!");
			System.exit(-42); //OMGERZ you broke it!
		}
		return buffer;
	}
	

	private static boolean enough_bits (ByteArrayInputStream is)
	{		return ((is.available() + 1) <= ((height * width ) / 3));  	}  // Abandon all hope, ye who try to understand this code.
	
	
	private static LinkedList<Byte> full_of_byte(ByteArrayInputStream is){
		LinkedList<Byte> x = new LinkedList<Byte>();
		
		int current_byte = 0;
		
		if(enough_bits(is)){
			while((current_byte = is.read()) != -1){
				byte a = (byte) ((current_byte >> 6) & 0x1);
				byte b = (byte) ((current_byte >> 3) & 0x7);
				byte c = (byte) (current_byte & 0x7);
				x.add(a); x.add(b); x.add(c);
			}
			return x;	
		}else{
			int num_bytes_can_write = ((height * width) / 3) - 1;
			int num_bytes_written = 0;
			
			while(num_bytes_written <= num_bytes_can_write){
				current_byte = is.read();
				byte a = (byte) ((current_byte >> 6) & 0x1);
				byte b = (byte) ((current_byte >> 3) & 0x7);
				byte c = (byte) (current_byte & 0x7);
				x.add(a); x.add(b); x.add(c);
				num_bytes_written++;
			}
			System.out.println("Error: Message is larger than the image. Message has been truncated.");
			return x;
		}
	}
	
	private static int newrgb(int r, int g, int b, Byte by){
		byte one= (byte) ((by.byteValue() >> 2) & 0x1);
		byte two = (byte) ((by.byteValue() >> 1) & 0x1);
		byte three = (byte) ((by.byteValue() >> 0) & 0x1);
				
		if((byte)(r % 2) != one){
			if(r == 0)	r+=1;
			else r-=1;
		}
			
		if((byte)(g % 2) != two){
			if(g == 0) 	g+=1;
			else	g-=1;
		}
				
		if((byte)(b % 2) != three){
			if(b == 0) 	b+=1;
			else b-=1;
		}	
		
		int newrgb = 0;
		newrgb = (b | newrgb);
		newrgb = (g << 8) | newrgb;
		newrgb = (r << 16) | newrgb;
		return newrgb;
	}
}
