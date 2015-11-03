/*	Copyright: 	© Copyright 2003 Apple Computer, Inc. All rights reserved.

	Disclaimer:	IMPORTANT:  This Apple software is supplied to you by Apple Computer, Inc.
			("Apple") in consideration of your agreement to the following terms, and your
			use, installation, modification or redistribution of this Apple software
			constitutes acceptance of these terms.  If you do not agree with these terms,
			please do not use, install, modify or redistribute this Apple software.

			In consideration of your agreement to abide by the following terms, and subject
			to these terms, Apple grants you a personal, non-exclusive license, under AppleÕs
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
        File:			OutputUnitTester.java
        
        Description:	This uses the default output unit to output audio data too - more notes below.
						
        Author:			William Stewart
*/

import com.apple.audio.*;
import com.apple.audio.hardware.*; 
import com.apple.audio.units.*;
import com.apple.audio.util.*;

/*
	We use the AudioConverter (AudioProvider) to provide audio data to the OutputUnit.
	Using the default output unit, we don't care about which actual device we're outputing too.
	We use an InputCallback to provide data to the unit.
*/
public class OutputUnitTester implements AURenderCallback, IOControlInterface 
{
	OutputUnitTester () throws CAException
	{
		outputUnit = AUComponent.openDefaultOutput();

		outputUnit.initialize();

			// feed input to the first stream of the device -> that's what the zero means!!!
		outputUnit.setInputCallback (this, whichBusToProvideInput);
		
		audioProvider = new AudioProvider ();
			
			// this resets the provider to provide data to the output unit in its current format
		audioProvider.setupConverter (getStreamFormat());
		
			//find the volume parameter of the audio output unit
		int [] parametres = outputUnit.getParameterList (AUConstants.kAudioUnitScope_Global);
			// we should parse this!!!!
			// but currently there is only one parameter -> volume
		volumeParam = parametres[0];
		AUParameterInfo paramInfo = outputUnit.getParameterInfo (AUConstants.kAudioUnitScope_Global, parametres[0]);
		System.out.println ("Volume param:\n" + paramInfo);
	}
	
	private AudioProvider audioProvider;
	
	private AudioDeviceOutputUnit outputUnit;
	private AudioStreamDescription streamFormat = new AudioStreamDescription();
	private boolean isPlaying = false;
	private int whichBusToProvideInput = 0;
	private int volumeParam;
	
	public AudioProvider getAudioProvider() {
		return audioProvider;
	}
	
	public void setVolume (float vol) {
		try {
			outputUnit.setParameter (volumeParam, AUConstants.kAudioUnitScope_Global, 0, vol, 0);
		} catch (CAException ae) {
			ae.printStackTrace();
		}
	}
	
	public void start() {
		try {
	    	outputUnit.start();
			isPlaying = true;
		} catch (CAException e) {
			e.printStackTrace();
		}
	} 
	
	public void stop () {
		try {
			outputUnit.stop ();
			isPlaying = false;
		} catch (CAException e) {
			e.printStackTrace();
		}
	}
		
	private AudioStreamDescription getStreamFormat() throws CAException {
			// get the stream format of the 1st stream
			// of the device that this class feeds input to
		outputUnit.getProperty (AUProperties.kAudioUnitProperty_StreamFormat, 
								AUConstants.kAudioUnitScope_Output, 
								whichBusToProvideInput,
								streamFormat);
		return streamFormat;
	}

		// this is the callback that supplies data to the output unit	
	public int execute (AudioUnit renderUnit, int inActionFlags, 
							AudioTimeStamp inTimeStamp, int inBusNumber, 
							AudioBuffer inData)
	{
		if (inBusNumber == whichBusToProvideInput)
			audioProvider.getNextDataPacket (inData);
		
		return 0;//noErr
   	}
}
