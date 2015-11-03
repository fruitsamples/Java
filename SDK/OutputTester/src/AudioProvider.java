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
        File:			AudioProvider.java
        
        Description:	Uses the AudioConverter to convert data from the source format (of the AIF file) to whatever the 
						destination format of the output device is.
						
						This is a VERY CONVENIENT API to provide data in the source format your choice - see notes below
   
						The source format of the AIF file is parsed using CarbonSound calls to ParseAIFF
							- this code is a little brain dead
							- it will only parse files with 8/16 bit data
							- it will only parse sample rates up to 65KHz
							- files can be interleaved n channels (n does NOT necessarily mean stereo!!!)
							- AIF files can contain other data formats (SR > 65KHz, sample size > 16 bit).
						Its a limitation of the code here. For these other sample formats you have to 
						parse the AIFC files - quicktime.sound has APIs to do that: an exercise for the interested reader.
        
        Author:			William Stewart
*/

import java.io.*;

import com.apple.audio.toolbox.*;
import com.apple.audio.util.*;
import com.apple.audio.*;

//use SoundManager calls to parse the AIF file
import quicktime.io.*;
import quicktime.sound.*; 

public class AudioProvider implements AudioConverterDataSupplier {
	static {
		try {
				// this is the best way to initialize Carbon Sound
				// (Calling QTSesssion.open will do more initialization
				// of quicktime than we require - ie . for using CarbonSound (quicktime.sound) classes
				// If you're using any of the general QuickTime calls you should instead call QTSession.open
			quicktime.QTSession.initialize(0);	
		} catch (quicktime.QTException e) {
			e.printStackTrace();
			throw new RuntimeException (e.getMessage());
		}
	}
	// Assume that AIFF is stereo 44.1/16bit!!!
	// ALSO read entire file into memory - won't work well with big files
	// to deal with big files, you'll need to use a helper thread to read the data
	// so its ready when the audio system asks for the next buffer of audio
	// you CAN'T expect to read data from the disk directly in the audio I/O cycle
	// we also don't parse the header of the AIF file - so you'll hear a click at the start
	private static final int largestChunkAccepted = 6000000;
	
	private AudioConverter converter;	
	private CAMemoryObject sourceData;
	private AudioConverterInputData suppliedData;
	private AudioStreamDescription sourceFormat; // this DOESN'T change in this program
	private AudioStreamDescription destinationFormat;
		
	public void setupConverter (AudioStreamDescription destFormat) throws CAException {
		destinationFormat = destFormat;
		
		if (sourceFormat == null) {
			// first time don't have a source format yet
			return;
		}
			// get output format from device
		System.out.println ("Source:" + sourceFormat);
		System.out.println ("Destination:" + destFormat);

		// If the destination format is != 2 then the converter's default
		// behaviour is to send the stereo AIF to the first 2 channels.
		// You could reset the converter to start at a different channel if you want
		
		// Can't deal with NON-PCM devices as our source data is PCM 
		// the converter currently only deals with PCM data conversions...
		if (destFormat.getFormatID() != CAUtilConstants.kAudioFormatLinearPCM)
			throw new CAException ("Can only talk to linear PCM devices");
		
		if (converter != null)
			converter.dispose(); // chuck the old one away...
		converter = new AudioConverter (sourceFormat, destFormat);
	}

	public void getNextDataPacket (AudioBuffer buffer) {			
			// SIMPLE!!! - just get the converter to fill up the data chunk we need
			// for output
		try {
			converter.fillBuffer (this, buffer.getData());
		} catch (CAException e) {
			e.printStackTrace();
		}
	}	
	
	public AudioConverterInputData execute (AudioConverter converter, int minSizeRequested) throws CAException {
			// JUST parcel out the data in one hit - we have the whole file in memory so just give it
			// The AudioConverter calls this method when it needs more input data.
		System.out.println ("Supplying Data:" + suppliedData); 
		return suppliedData;
	}
	
	public boolean hasSourceData () {
		return sourceData != null;
	}
	
	public boolean prepareAIFFData (File aifFile) {
		try {
				// Read the header information from the AIF file
			OpenFile sndFile = OpenFile.asRead (new QTFile (aifFile));
			SndInfo sndInfo = SndInfo.parseAIFFHeader (sndFile);
			sndFile.close();

			SoundComponentData sndData = sndInfo.sndData;
			System.out.println (sndData);
					// could parse this better... but for the moment
					// ie... deal with other bit depths - see docs on ParseAIFF
				// only dealing with 8/16bit big/little endian formats
				// DON'T be fooled!!! 8bit formats are actually encoded with
				// a format type of twos/sowt so these constants will test true for most 8 bit files
			int sndFormat = sndData.getFormat();
			if (!(sndFormat == SoundConstants.k16BitBigEndianFormat
				|| sndFormat == SoundConstants.k16BitLittleEndianFormat)) {
				return false;
			}

			int sizeToRead = sndData.getSampleSize() / 8 * sndData.getSampleCount();
			if (sizeToRead > largestChunkAccepted)
				sizeToRead = largestChunkAccepted;
		
			FileInputStream fis = new FileInputStream (aifFile);
			byte[] ar = new byte[sizeToRead];
			fis.skip (sndInfo.dataOffset); // skip the bits without the sound data
			fis.read (ar);

			if (sourceData != null)
				sourceData.dispose();
			
			sourceData = new CAMemoryObject (ar.length, true);
			sourceData.copyFromArray (0, ar, 0, ar.length);
			
			suppliedData = new AudioConverterInputData();
				// let the audio converter do the work for us
				// so we just supply a big chunk of data when it wants it
			suppliedData.inputData = sourceData;
			suppliedData.byteOffset = 0;
			suppliedData.numBytes = sourceData.getSize();			

			sourceFormat = new AudioStreamDescription();
			sourceFormat.setFormatID (CAUtilConstants.kAudioFormatLinearPCM);
			int endianFormatFlag = (sndFormat == SoundConstants.k16BitBigEndianFormat
								? CAUtilConstants.kLinearPCMFormatFlagIsBigEndian
								: 0); // no bit set if littel endian format
			sourceFormat.setFormatFlags (endianFormatFlag
									| CAUtilConstants.kLinearPCMFormatFlagIsSignedInteger);
				//this sample rate field is limited to 65KHz
				// there's a field in the AIF file that will capture larger sample rates 
				// than this - but not in the structure we're using to parse the header
			sourceFormat.setSampleRate (sndData.getSampleRate()); 
			int numChannels = sndData.getNumChannels();
			sourceFormat.setChannelsPerFrame (numChannels);
			sourceFormat.setFramesPerPacket (1);
			int byteSizeOfFrame = (sndData.getSampleSize() / 8) * numChannels;//numChannels * (bit depth / 8)
			sourceFormat.setBytesPerPacket (byteSizeOfFrame); 
			sourceFormat.setBytesPerFrame (byteSizeOfFrame);
			sourceFormat.setBitsPerChannel (sndData.getSampleSize());			
			
			setupConverter (destinationFormat);
			System.out.println ("Finished loading AIFFile:" + aifFile);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}



