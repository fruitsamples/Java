/*	Copyright: 	© Copyright 2003 Apple Computer, Inc. All rights reserved.

	Disclaimer:	IMPORTANT:  This Apple software is supplied to you by Apple Computer, Inc.
			("Apple") in consideration of your agreement to the following terms, and your
			use, installation, modification or redistribution of this Apple software
			constitutes acceptance of these terms.  If you do not agree with these terms,
			please do not use, install, modify or redistribute this Apple software.

			In consideration of your agreement to abide by the following terms, and subject
			to these terms, Apple grants you a personal, non-exclusive license, under Apple’s
			copyrights in this original Apple software (the "Apple Software"), to use,
			reproduce, modify and redistribute the Apple Software, with or without
			modifications, in source and/or binary forms; provided that if you redistribute
			the Apple Software in its entirety and without modifications, you must retain
			this notice and the following text and disclaimers in all such redistributions of
			the Apple Software.  Neither the name, trademarks, service marks or logos of
			Apple Computer, Inc. may be used to endorse or promote products derived from the
			Apple Software without specific prior written permission from Apple.  Except as
			expressly stated in this notice, no other rights or licenses, express or implied,
			are granted by Apple herein, including but not limited to any patent rights that
			may be infringed by your derivative works or by other works in which the Apple
			Software may be incorporated.

			The Apple Software is provided by Apple on an "AS IS" basis.  APPLE MAKES NO
			WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED
			WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR
			PURPOSE, REGARDING THE APPLE SOFTWARE OR ITS USE AND OPERATION ALONE OR IN
			COMBINATION WITH YOUR PRODUCTS.

			IN NO EVENT SHALL APPLE BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL OR
			CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
			GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
			ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION, MODIFICATION AND/OR DISTRIBUTION
			OF THE APPLE SOFTWARE, HOWEVER CAUSED AND WHETHER UNDER THEORY OF CONTRACT, TORT
			(INCLUDING NEGLIGENCE), STRICT LIABILITY OR OTHERWISE, EVEN IF APPLE HAS BEEN
			ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
/*
        File:			HALTester.java
        
        Description:	HALTester uses the lowest layer of the CoreAudio system (com.apple.audio.hardware) to output
						data to the device. It dispatches to the AudioProvider class to fill the buffer of data from
						the AudioDevice.
						
						It uses the default output device as its target. As such - it listens for notifications of
						any change the user makes to this target through the Sound Prefs or (/Develeper/Examples/CoreAudio/HAL/Daisy)
						
						It also listens for any notifications of changes to the output format of the device.
						
						In both of these cases it resets the AudioProvider object with the new destination format of the device.
        
        Author:			William Stewart
*/

import com.apple.audio.hardware.*;
import com.apple.audio.util.*;
import com.apple.audio.*;

public class HALTester implements AudioDeviceIOProc, 
								IOControlInterface, 
								AHardwarePropertyListener,
								ADevicePropertyListener
{
			// This had to listen for property changes of the audio system
			// so that the audio continues to play to the default output device
			// if the user changes it in the Control Panel
	HALTester () throws CAException {
		audioProvider = new AudioProvider();
		AudioHardware.addPropertyListener (AHConstants.kAudioPropertyWildcardPropertyID, this);
		setup();
	}
	
	private AudioProvider audioProvider;

	private AudioDevice device;
	private AudioStreamDescription streamFormat = new AudioStreamDescription();
	private boolean playing = false;
	
	public AudioProvider getAudioProvider() {
		return audioProvider;
	}

	private void setup () throws CAException {
		if (device != null) { // we already had one so clean it up
			device.removeIOProc (this);
			device.removeOutputPropertyListener (0, 
							AHConstants.kAudioDevicePropertyStreamFormat, 
							this);
		}
		device = AudioHardware.getDefaultOutputDevice();

		device.addOutputPropertyListener (0, //listen to changes to the stream format of the device
							AHConstants.kAudioDevicePropertyStreamFormat, 
							this);
				// add the IOProc so we can supply data to the device
		device.addIOProc (this);

			// this resets the provider to provide data to the new device
			// in the new device's format
		audioProvider.setupConverter (getStreamFormat());
	}
	
	private AudioStreamDescription getStreamFormat () throws CAException {
				// get stream format from device
		device.getOutputProperty (0, AHConstants.kAudioDevicePropertyStreamFormat, streamFormat);
		return streamFormat;
	}
	
	public void start () {   
		if (device == null)
			return;
		try {
	    	device.start(this);
			playing = true;
		} catch (CAException e) {
			e.printStackTrace();
		}
	} 
	
	public void stop () {
		if (device == null)
			return;
		try {
			device.stop (this);
			playing = false;
		} catch (CAException e) {
			e.printStackTrace();
		}
	}
		
		// We asks the AudioProvider (which uses an AudioConverter) to provide us the next buffer of audio data
		// this is the IOProc of the Audio Device	
	public int execute (AudioDevice device, AudioTimeStamp inNow, AudioBufferList inInputData,
							AudioTimeStamp inInputTime, AudioBufferList outOutputData, 
							AudioTimeStamp inOutputTime) 
	{
   		AudioBuffer outBuffer = outOutputData.getBuffer (0);
		
		audioProvider.getNextDataPacket (outBuffer);
		
		return 0;//noErr
   	}
	
		// this is the device notification call
		// we KNOW it is only going to fire when the format of the device is changed
		// as that is the property we registered for notifications on
	public int execute (AudioDevice device, int inChannel, boolean isInput, int inPropertyID) {		
		System.out.println ("\nNew device format");
		try {
			boolean wasPlaying = playing;
			if (wasPlaying)
				stop();
				
				// this resets the provider to provide data to the new device
				// in the new device's format
			audioProvider.setupConverter (getStreamFormat());
			
			if (wasPlaying)
				start();
		} catch (CAException e) {
			e.printStackTrace();
			return e.getErrorCode();
		}
		return 0;
	}
	
		// this is the AudioHardware notification call
	public int execute (int inPropertyID) {
		try {
			// this method fires when some global state of the system changes.
			// we're only interested in changes to the default device
			// print out the rest...
			switch (inPropertyID) {
				case AHConstants.kAudioHardwarePropertyDefaultInputDevice:
					System.out.println ("\nNew Default Input:" + AudioHardware.getDefaultInputDevice());
					break;
						// this is the one we output to - so make sure we're setup to do that
				case AHConstants.kAudioHardwarePropertyDefaultOutputDevice:
					System.out.println ("\nNew Default Output:" + AudioHardware.getDefaultOutputDevice());
					boolean wasPlaying = playing;
					if (wasPlaying)
						stop();
						//this resets the destination to the new device
					setup();

					if (wasPlaying)
						start();
					break;
				case AHConstants.kAudioHardwarePropertyDefaultSystemOutputDevice:
					System.out.println ("\nNew System Output:" + AudioHardware.getSystemOutputDevice());
					break;
				default:
					int size = AudioHardware.getPropertyInfoSize (inPropertyID);
					CAMemoryObject propertyValue = new CAMemoryObject (size, false);
					AudioHardware.getProperty (inPropertyID, propertyValue);
					System.out.println ("Property Changed:" + inPropertyID + ",value=" + propertyValue);
			}
		} catch (CAException e) {
			e.printStackTrace();
			return e.getErrorCode();
		}
		return 0;//noErr
	}
}
