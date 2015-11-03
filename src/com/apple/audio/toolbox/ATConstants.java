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
//
//  ATConstants.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.toolbox;

public final class ATConstants {
	private ATConstants () {}

	public static final double
		kMusicTimeStamp_EndOfTrack = 1000000000.0;
		
	public static final int
		kMusicEventType_NULL = 0;
	/** note with variable number of arguments (non-MIDI)*/
	public static final int
		kMusicEventType_ExtendedNote = 1;		
	/** control change (non-MIDI)*/
	public static final int
		kMusicEventType_ExtendedControl  = 2;	
	/** tempo change in BPM*/
	public static final int
		kMusicEventType_ExtendedTempo = 3;
	/** user defined data*/
	public static final int
		kMusicEventType_User = 4;	
	/** standard MIDI file meta event*/
	public static final int
		kMusicEventType_Meta = 5;			
	/** MIDI note-on with duration (for note-off)*/
	public static final int
		kMusicEventType_MIDINoteMessage = 6;	
	/** MIDI channel messages (other than note-on/off)*/
	public static final int
		kMusicEventType_MIDIChannelMessage = 7;
	/** for system exclusive data*/
	public static final int
		kMusicEventType_MIDIRawData = 8;
	/** for MIDI events that are mapped to AUParameter*/
	public static final int
		kMusicEventType_Parameter = 9;
	/** Last event marker always keep at end*/
	public static final int
		kMusicEventType_Last = 10;	

	public static final int
		kSequenceTrackProperty_LoopInfo = 0;		// struct {MusicTimeStamp loopLength; long numberOfLoops;};
	public static final int
		kSequenceTrackProperty_OffsetTime = 1;		// struct {MusicTimeStamp offsetTime;};
	public static final int
		kSequenceTrackProperty_MuteStatus = 2;		// struct {Boolean muteState;};
	public static final int
		kSequenceTrackProperty_SoloStatus = 3;		// struct {Boolean soloState;};

		/**
		* a UInt32 that indicates the size in bytes of the smallest
		* buffer of input data that can be supplied via the
		* AudioConverterInputPrc or as the input to
		* AudioConverterConvertBuffer
		*/
	public static final int
		kAudioConverterPropertyMinimumInputBufferSize		= 0x6d696273;//'mibs',
		
		/**
		* a UInt32 that indicates the size in bytes of the smallest
		* buffer of output data that can be supplied to
		* AudioConverterFillBuffer or as the output to
		* AudioConverterConvertBuffer
		*/
	public static final int
		kAudioConverterPropertyMinimumOutputBufferSize		= 0x6d6f6273;//'mobs';
		
		/**
		* a UInt32 that on input holds a size in bytes
		* that is desired for the output data. On output,
		* it will hold the size in bytes of the input buffer
		* requried to generate that much output data. Note
		* that some converters cannot do this calculation.
		*/
	public static final int
		kAudioConverterPropertyCalculateInputBufferSize		= 0x63696273;//;'cibs',
		
		/**
		* a UInt32 that on input holds a size in bytes
		* that is desired for the input data. On output,
		* it will hold the size in bytes of the output buffer
		* required to hold the output data that will be generated.
		* Note that some converters cannot do this calculation.
		*/
	public static final int
		kAudioConverterPropertyCalculateOutputBufferSize	= 0x636f6273;//;'cobs',
		
		/**
		* The value of this property varies from format to format
		* and is considered private to the format. It is treated
		* as a buffer of untyped data.
		*/
	public static final int
		kAudioConverterPropertyInputCodecParameters			= 0x69636470;//'icdp',
		
		/**
		* The value of this property varies from format to format
		* and is considered private to the format. It is treated
		* as a buffer of untyped data.
		*/
	public static final int
		kAudioConverterPropertyOutputCodecParameters		= 0x6f636470;//'ocdp'

		/** An OSType that specifies the sample rate converter to use
		 * (as defined in com.apple.audio.units.AUProperties -- for now only Apple SRC's can be used)
		 */
	public static final int
		kAudioConverterSampleRateConverterAlgorithm			= 0x73726369;//'srci'

	/**
	 * An array of SInt32's used when the number of input and output
	 * channels differ.  The size of the array is the number of output
	 * channels, and each element specifies which input channel's
	 * data is routed to that output channel (using a 0-based index
	 * of the input channels), or -1 if no input channel is to be
	 * routed to that output channel.  The default behavior is as follows.
	 * I = number of input channels, O = number of output channels.
	 * When I > O, the first O inputs are routed to the first O outputs,
	 * and the remaining puts discarded.  When O > I, the first I inputs are 
	 * routed to the first O outputs, and the remaining outputs are zeroed.
	 */
	 public static final int
		kAudioConverterChannelMap							= 0x63686D70;//'chmp'
}

/*
 */
