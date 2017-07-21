# Android-HC-05-LED-Controller


Just an app I made for controlling a LED with an Arduino & a Hc-05 bluetooth module. I wasn't intending to upload
my code to GitHub when I initially started working on it, so I apologize if my code isn't very readable. In the case
that someone actually benifits from my code, I'm glad I could help! :D


Help for communicating with & programming your Arduino:
	
	- Tap the bluetooth image to get paired devices or scan for new ones to connect to
	
	- SeekBar/Silder sends out the numbers 0 - 253 to control the brightness of the LED
	  (0 - 253 instead of 0 - 255 because I couldn't receieve numbers higher than 255
	  through the serial port)
	   
	- On button sends out the number 254 to the Hc-05 to turn the LED on
	
	- Off button sends out the number 255 to the Hc-05 to turn LED off
	
	
Note:
	
	- Unfortanately, the app force closes the first to time that you scan for new devices
	  (never managed to figure out the problem. Sorry...) & gives you the NullPointerException of
	  death. Luckily, that has only ever happened to me the first time it scans!
