UTEID: jmt2939; dbd453;
FIRSTNAME: Jeremy; Daniel;
LASTNAME: Thompson; Durbin;
CSACCOUNT: jmt2939; majisto;
EMAIL: jthompson2214@gmail.com; majisto@gmail.com;

[Program 3]
[Description]
Our program has encode and decode flags to trigger the two halves of the program. For the encode the user needs to provide an 
image file and a message to encode inside it. For the decode you need to provide an encoded image file and the name of the file 
you want the decoded message to go into. We parse out the message (in the case where the message is smaller than the amount of bytes 
that are possible to encode into the image) byte by byte into 3 different pixels (2 bits in one and 3 each in the other two). Each of 
these pixels is added to a linkedList in the correct order. This list is read from for each pixel when we want to encode to the image. 
We for each pixel we pull out the R, G, and B values and split the bits off of the next chunk from the linkedList. Then, depending 
on whether the bit is a 1 or a 0 we round the R, G, or B value to even or odd in order to represent this. These new values are then rebuilt 
into the rgb int value and set to the correct pixel in the output file. This is done for the entire message until we run out of message to 
encode. 

For the decoding we traverse the image pixel by pixel grabbing 3 and then building the byte from those three. This message is then written 
out to the file whose name the user specified when they ran the program. 

[Finish]
We finished all of the program. Ours works for .png and .bmp files. It also works for jpeg and jpg files in a bit of a roundabout way. 
The problem with jpeg files is that when it saves as a jpeg it applies the lossy compression to it, so as a way around that, if we are given 
a jpeg or jpg image file we change the image type to bmp which won't apply a lossy compression when it saves. This way you can use the image 
of your choice and it will always be encoded in a format that will preserve the content. 

Young said that if we got jpeg to work that would count as extra credit, so I'm going to add this here because I'm not sure where else to 
put it. 

[Test Cases]
(Otherwise known as the questions of the 5th degree)

1. Comparing your original and modified images carefully, can you detect *any* difference visually (that is, in the appearance of the image)?
no, they look identical to us.  	
2. Can you think of other ways you might hide the message in image files (or in other types of files)?
as long as no one knows what the original picture looks like you could do any number of encodings. For example, inverting the rgb values for the pixels. 
3. Can you invent ways to increase the bandwidth of the channel?
figuring out how to use the Alpha channel would allow for 4 bits per pixel leading to 2 pixels/byte which is better than our current 3 pixels/byte
4. Suppose you were tasked to build an "image firewall" that would block images containing hidden messages. Could you do it? How might you approach this problem?
nope. you wouldn't need the original image, but rather need to know the encoding scheme used to encode in the first place. Otherwise how could you possibly know that something was different. 
5. Does this fit our definition of a covert channel? Explain your answer.
We think this fits the definition of a covert storage channel. We are using the image's RGB resource as a way of passing a message from A to B, and storing that message there. 