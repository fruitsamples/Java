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
// 
//  AHConstants.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.hardware;

/**
 * This class contains constants that are defined in AudioHardware.h
 */
public final class AHConstants {
		//can't be instantiated
	private AHConstants() {}
    
	    /** a vector of the AudioDeviceIDs of 'real' devices only, available in the system */
	public static final int kAudioHardwarePropertyDevices = 0x64657623;  //'dev#'FOUR_CHAR_CODE('dev#')
		/** the AudioDeviceID of the default input device */
	public static final int kAudioHardwarePropertyDefaultInputDevice = 0x64496e20;  //'dIn 'FOUR_CHAR_CODE('dIn ')
	    /** the AudioDeviceID of the default output device*/
	public static final int kAudioHardwarePropertyDefaultOutputDevice = 0x644f7574;  //'dOut'FOUR_CHAR_CODE('dOut');

	    /** A UInt32 where 1 means this process will allow the
		 * machine to sleep and 0 will keep the machine awake.
		 * Note that the machine can still be forced to go to
		 * sleep regardless of the setting of this property.
		 */
	public static final int kAudioHardwarePropertySleepingIsAllowed	= 0x736c6570;  //'slep''slep'

		/**
		 * the AudioDeviceID of the default system output device
		 * Services that generate audio as a system service, like
		 * SysBeep() or digial call progress should use this device.
		 */
	public static final int kAudioHardwarePropertyDefaultSystemOutputDevice	= 0x734F7574;// sOut
	
		/**
		 * a CFRunLoopRef to which the client wishes the HAL to
		 * attach system notifications to. By default the HAL
		 * will attach it's system notifications to the thread
		 * CarbonCore is initialized on. This property can be
		 * used to override this selection. The HAL will move
		 * all it's currently attached notifications to the new
		 * run loop.
		 */
	public static final int kAudioHardwarePropertyRunLoop = 0x726E6C70;// rnlp	
	
    		/**the name of the device as a null terminated C-string*/
	public static final int kAudioDevicePropertyDeviceName					= 0x6e616d65;  //'name'FOUR_CHAR_CODE('name')
	    	/**an OSType representing the manufacturer of the device*/
	public static final int kAudioDevicePropertyDeviceManufacturer			= 0x6d616b72;  //'makr'FOUR_CHAR_CODE('makr')
			/**
			 * a UInt32 where 1 means the device is installed and ready
			 * to handle requests and 0 means the device has been removed
			 * or otherwise disconnected and is about to go away completely.
			 * After receiving notification on this property, any AudioDeviceID's
			 * referring to the destroyed device are invalid. It is highly
			 * recommended that all clients listen for this notification.
			 */
	public static final int kAudioDevicePropertyDeviceIsAlive				= 0x6c69766e;  //'livn''livn';
	    	/**a UInt32 where 0 means the device is off and 1 means the device is running*/
	public static final int kAudioDevicePropertyDeviceIsRunning				= 0x676f696e;  //'goin'FOUR_CHAR_CODE('goin');
	    	/**a UInt32 containing the size of the IO buffers in bytes*/
	public static final int kAudioDevicePropertyBufferSize					= 0x6273697a;  //'bsiz'FOUR_CHAR_CODE('bsiz');	
			/** a UInt32 containing the number of frames of latency in the hardware*/
	public static final int kAudioDevicePropertyStreamLatency				= 0x6c746e63;  //'ltnc'
			/**
			 * This property returns the stream configuration of the device in
			 * an AudioBufferList (with the buffer pointers set to NULL) which
			 * describes the list of streams and the number of channels in each
			 */
	public static final int kAudioDevicePropertyStreamConfiguration			= 0x736c6179; //'slay';
		/**	An array of two UInt32s where the first UInt32 indicates the device
		 *	channel number to use for the left channel and the second one
		 *	indicates the device channel number to use for the right channel.
		 *	This property applies to both the input and the output and the
		 *	channels can be different for each. Further, there are no
		 *	guarantees about the relationship between the two channels (ie.
		 *	they may not be consecutive and may be in separate streams).
		 *	This property won't be implemented for devices that have only
		 *	a single channel.
		 */	  
	public static final int kAudioDevicePropertyPreferredChannelsForStereo  = 0x64636832; //'dch2';   
		/**
		 * The stream format of the stream containing the requested channel
		 * as an AudioStreamBasicDescription. Since formats are stream level
		 * entities, the number of channels returned with this property actually
		 * refers to the number of channels in the stream containing the requested
		 * channel. Consequently, it only gives a partial picture of the overall
		 * number of channels for the device. Use kAudioDevicePropertyStreamConfiguration
		 * to get the information on how the channels are divied up across the streams.
		 * It is highly recommended that all clients listen for this notification.
		 */
	public static final int kAudioDevicePropertyStreamFormat				= 0x73666d74;  //'sfmt'FOUR_CHAR_CODE('sfmt');
	    	/**a vector of the AudioStreamBasicDescription's the device supports*/
	public static final int kAudioDevicePropertyStreamFormats				= 0x73666d23;  //'sfm#'FOUR_CHAR_CODE('sfm#');
		    /**
		     * An AudioStreamBasicDescription is passed in to query whether or not
		     * the format is supported. A kAudioDeviceUnsupportedFormatError will
		     * be returned if the format is not supported and noErr will be returned
		     * if it is supported. AudioStreamBasicDescription fields set to 0
		     * will be ignored in the query, but otherwise values must match
		     * exactly.
		     */
	public static final int kAudioDevicePropertyStreamFormatSupported		= 0x73666d3f;  //'sfm?'FOUR_CHAR_CODE('sfm?');
		    /**
		     * An AudioStreamBasicDescription is passed in which is modified
		     * to describe the closest match to the given format that is
		     * supported by the device. AudioStreamBasicDescription fields set
		     * to 0 should be ignored in the query and the device is free to
		     * substitute any value it sees fit. Note that the device may
		     * return a result that differs dramatically from the requested
		     * format. All matching is at the device's ultimate discretion.
		     */
	public static final int kAudioDevicePropertyStreamFormatMatch			= 0x73666e6d;  //'sfnm'FOUR_CHAR_CODE('sfmm');
	    	/** a Float32 between 0 and 1 that scales the volume of the device/line.*/
	public static final int kAudioDevicePropertyVolumeScalar				= 0x766f6c6d;  //'volm'FOUR_CHAR_CODE('volm');
		    /** a UInt32 where 0 means the device is not muted and 1 means the device is muted*/
	public static final int kAudioDevicePropertyMute						= 0x6d757465;  //'mute'FOUR_CHAR_CODE('mute')
		    /** a UInt32 where 0 means play through is off and 1 means play through is on*/
	public static final int kAudioDevicePropertyPlayThru					= 0x74687275;  //'thru'FOUR_CHAR_CODE('thru')

		    /**
		     * This property exists so that clients can be told when
		     * they are overloading the the IO thread. When the HAL dectects
		     * the situation where the combined processing time exceeds the
		     * duration of the buffer, it will notify all listeners on this
		     * property. Overloading also will cause the HAL to resynch itself
		     * and restart the IO cycle to be sure that the IO thread goes
		     * to sleep. The value of this property is a UInt32, but it's
		     * value has no currently defined meaning.
		     */
	public static final int kAudioDeviceProcessorOverload					= 0x6f766572;  //'over''over'

		/**
		 * the name of the device as a CFStringRef. The CFStringRef
		 * retrieved via this property must be released by the caller.
		 */
	public static final int kAudioDevicePropertyDeviceNameCFString			= 0x6C6E616D; //'lnam',
		
		/**
		 * the manufacturer of the device as a CFStringRef. The CFStringRef 
		 * retrieved via this property must be released by the caller.
		 */
	public static final int kAudioDevicePropertyDeviceManufacturerCFString	= 0x6C6D616B;//'lmak',
		
		/**
		 * An pid_t indicating the process ID that currently owns
		 *	hog mode for the device or a -1 indicating that no process
		 * currently owns it. While a process owns hog mode for a device,
		 * no other process on the system can perform IO with the given
		 * device. When calling AudioDeviceSetProperty to acquire hog mode
		 * for a device, the value on input means nothing, but on output
		 * will contain the process ID of the owner of hog which should be
		 * the ID for the current process if the call is successful.
		 * The HAL uses the process IDs acquired from getpid().
		 */
	public static final int kAudioDevicePropertyHogMode						= 0x6F696E6B;//'oink',
	
		/**
		 * This property allows clients to register a fully populated
		 * AudioBufferList that matches the topology described by
		 * kAudioDevicePropertyStreamConfiguration for doing IO using
		 * AudioDeviceRead. The AudioBufferList will be registered
		 * with the call the AudioDeviceSetProperty and will be
		 * unregistered with the call to AudioDeceGetProperty.
		 */
	public static final int kAudioDevicePropertyRegisterBufferList			= 0x72627566;//'rbuf',
	
		/**
		 * a UInt32 containing the number of frames of latency in the device
		 * Note that input and output latency may differ.
		 */
	public static final int kAudioDevicePropertyLatency						= 0x6C746E63;//'ltnc',
	
		/**
		 * an AudioValueRange specifying the minimum and maximum byte
		 * sizes that will be accepted for the device.
		 */
	public static final int kAudioDevicePropertyBufferSizeRange				= 0x62737A23;//'bsz#',

		/**
		 * a UInt32 containing the size of the IO buffers in frames
		 * It is highly recommended that all clients listen for this
		 * notification.
		 */
	public static final int kAudioDevicePropertyBufferFrameSize				= 0x6673697A;//'fsiz',
	
		/**
		 * an AudioValueRange specifying the minimum and maximum frame
		 * sizes that will be accepted for the device.
		 */
	public static final int kAudioDevicePropertyBufferFrameSizeRange		= 0x66737A23;//'fsz#',

		/**
		 *	An array of the AudioStreamIDs available on the device.
		 *	Note that if a notification for this property is received,
		 *	all of the AudioStreamIDs that refer to streams on this device
		 *	are invalid and should be refreshed. Installed listener routines
		 *	will continue to be called as appropriate.
		 */
	public static final int kAudioDevicePropertyStreams						= 0x73746D23;//'stm#',
		
		/**
		 * a UInt32 containing the number of frames ahead (for
		 * output) or behind (for input) the head that it is
		 * safe to start reading or writing.
		 */
	public static final int kAudioDevicePropertySafetyOffset				= 0x73616674;//'saft',

		/**
		 * a Float32 whose units are decibels where 0 represents unity,
		 * negative values for attenuation and positive values for gain.
		 */
	public static final int kAudioDevicePropertyVolumeDecibels				= 0x766F6C64;//'vold',
	
		/**
		 * an AudioValueRange specifying the minimum and maximum db values.
		 */
	public static final int kAudioDevicePropertyVolumeRangeDecibels			= 0x76646223;//'vdb#',
	
		/**
		 * convert the Float32 scalar volume to decibels
		 */
	public static final int kAudioDevicePropertyVolumeScalarToDecibels		= 0x76326462;//'v2db',

		/**
		 * convert the Float32 decibel volume to scalar
		 */
	public static final int kAudioDevicePropertyVolumeDecibelsToScalar		= 0x64623276;//'db2v',

		    /**
			* a UInt32 which is the ID of the data source for the given channel
			*/
	public static final int kAudioDevicePropertyDataSource					= 0x73737263;//'ssrc',
	
		    /**
			* an array of the available UInt32 source IDs for the given channel
			*/
	public static final int kAudioDevicePropertyDataSources					= 0x73736323;//'ssc#',
	
		/**
		 * Retrieves the name of a source for a given source ID using a
		 * AudioValueTranslation structure. The input data is the UInt32
		 * holding the source ID and the output data is a buffer to hold
		 * the name as a null terminated string.
		 */
	public static final int kAudioDevicePropertyDataSourceNameForID			= 0x7373636E;//'sscn',
	
		/**
		 * Retrieves the name of a source for a given source ID using a
		 * AudioValueTranslation structure. The input data is the UInt32
		 * holding the source ID and the output data is a CFStringRef.
		 * The CFStringRef retrieved via this property must be released
		 * by the caller.
		 */
	public static final int kAudioDevicePropertyDataSourceNameForIDCFString	= 0x6C73636E;//'lscn'

		/**
		 * a UInt32 which is the ID of the clock source for the given channel
		 */
	public static final int kAudioDevicePropertyClockSource						= 0x63737263;//'csrc',
	
		/**
		 * an array of the available UInt32 clock source IDs for the given channel
		 */
	public static final int kAudioDevicePropertyClockSources					= 0x63736323;//'csc#',
	
		/**
		 * Retrieves the name of a clock source for a given clock source ID
		 * using an AudioValueTranslation structure. The input data is the
		 * UInt32 holding the clock source ID and the output data is a buffer to
		 * hold the name as a null terminated string.
		 */
	public static final int kAudioDevicePropertyClockSourceNameForID			= 0x6373636E;//'cscn',
	
		/**
		 * Retrieves the name of a clock source for a given clock source ID
		 * using an AudioValueTranslation structure. The input data is the
		 * UInt32 holding the clock source ID and the output data is a CFStringRef.
		 * The CFStringRef retrieved via this property must be released
		 * by the caller.
		 */
	public static final int kAudioDevicePropertyClockSourceNameForIDCFString	= 0x6C63736E;//'lcsn'

		/**
		 * a UInt32 where 0 means output and 1 means input
		 */
	public static final int kAudioStreamPropertyDirection					= 0x73646972;//'sdir',
		
		/**
		 * a UInt32 that specifies the first device channel number for this stream
		 */
	public static final int kAudioStreamPropertyStartingChannel				= 0x7363686E;//'schn',
		
		/**
		 * An AudioStreamBasicDescription containing the current physical format
		 * of the stream. The physical properties describe and manipulate the
		 * hardware is actually working in. Note that this may be different from
		 * the format used in the IOProc. A physical format change does not
		 * necessarily imply a change in kAudioDevicePropertyStreamFormat.
		 */
	public static final int kAudioStreamPropertyPhysicalFormat				= 0x70667420;//'pft ',
	
		/**
		 * an array of the AudioStreamBasicDescription's the stream supports physically
		 */
	public static final int kAudioStreamPropertyPhysicalFormats				= 0x70667423;//'pft#',
	
		/**
		 * An AudioStreamBasicDescription is passed in to query whether or not
		 * the physical format is supported. A kAudioDeviceUnsupportedFormatError
		 * will be returned if the format is not supported and kAudioHardwareNoError
		 * will be returned if it is supported. AudioStreamBasicDescription
		 * fields set to 0 will be ignored in the query, but otherwise values
		 * must match exactly.
		 */
	public static final int kAudioStreamPropertyPhysicalFormatSupported		= 0x7066743F;//'pft?',
	
		/**
		 * An AudioStreamBasicDescription is passed in which is modified
		 * to describe the closest match to the given physcial format that
		 * is supported by the device. AudioStreamBasicDescription fields set
		 * to 0 will be ignored in the query and the stream is free to
		 * substitute any value it sees fit. Note that the stream may
		 * return a result that differs dramatically from the requested
		 * format. All matching is at the stream's ultimate discretion.
		 */
	public static final int kAudioStreamPropertyPhysicalFormatMatch			= 0x7066746D;//'pftm'

		/**
		 * a wild card for AudioHardwarePropertyIDs, AudioDevicePropertyIDs,
		 * and AudioDevicePropertyIDs
		 */
	public static final int kAudioPropertyWildcardPropertyID			= 0x2A2A2A2A;//'****',
		
		/**
		 * a wild card for specifying the section (aka the isInput parameter)
		 */
	public static final int kAudioPropertyWildcardSection				= 0xFF;
		
		/**
		 * a wild card for the channel
		 */
	public static final int kAudioPropertyWildcardChannel				= 0xFFFFFFFF;

		/** the AudioDeviceID of the device that owns this stream */
	public static final int kAudioStreamPropertyOwningDevice			= 0x73746476;//'stdv',

}

/*
 */
