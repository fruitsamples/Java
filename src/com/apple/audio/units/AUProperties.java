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
//  AUProperties.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.units;

/**
 * These are property identifiers for Audio Units. 
 * Apple reserves property values from 0 -> 63999
 * Developers are free to use property IDs above this range at their own discretion
 *
 * <PRE>
// General AudioUnit properties:
//		Unless otherwise stated, assume that the "inScope" parameter is kAudioUnitScope_Global
//		and the "inElement" parameter is ignored.
//
//		kAudioUnitProperty_ClassInfo				(void* points to AudioUnit-defined internal state)
//
//		kAudioUnitProperty_MakeConnection			(AudioUnitConnection*)
//			pass in kAudioUnitScope_Input for the AudioUnitScope
//			pass in the input number for AudioUnitElement (redundantly stored in AudioUnitConnection)
//
//		kAudioUnitProperty_SampleRate				(Float64*)
//
//		kAudioUnitProperty_ParameterList			(AudioUnitParameterID*)
//			pass in kAudioUnitScope_Global for the AudioUnitScope
//			gives you a list of AudioUnitParameterIDs from which you may query info using
//			kAudioUnitProperty_ParameterInfo
//			
//		kAudioUnitProperty_ParameterInfo			(AudioUnitParameterInfo*)
//			pass in AudioUnitParameterID for the AudioUnitElement
//
//		kAudioUnitProperty_StreamFormat				(AudioStreamBasicDescription*)
//			pass in kAudioUnitScope_Input or kAudioUnitScope_Output for the AudioUnitScope
//			pass in the input or output number (zero-based) for the AudioUnitElement
//
//		kAudioUnitProperty_ThreadPriority			(UInt32*)
//			pass in thread priority in UInt32
//
//		kAudioUnitProperty_ReverbRoomType			(Uint32*)
//			pass in one of the kReverbRoomType enum values (above) as UInt32
//
//		kAudioUnitProperty_BusCount					(UInt32*)
//			scope is either   kAudioUnitScope_Input or kAudioUnitScope_Output
//			to determine number of input or output busses
//			the number of busses is returned as UInt32
//
//		kAudioUnitProperty_Latency					(Float64*)
//			input to output latency in seconds.  AudioUnits which use delay or reverb
//			or similar should report zero latency since the delay is part of the desired effect.
//			Look-ahead compressors/limiters, pitch-shifters, phase-vocoders, buffering AudioUnits, etc.
//			may report a true latency.... 

				


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// MusicDevice properties:
//
//		kMusicDeviceProperty_InstrumentCount		(UInt32* pointing to count )
//
//		kMusicDeviceProperty_InstrumentName			(formatted as char*)
//			pass in MusicDeviceInstrumentID for inElement			
//
//		kMusicDeviceProperty_GroupOutputBus			(UInt32* pointing to bus number )
//			pass in MusicDeviceGroupID for the AudioUnitElement			
//			pass in kAudioUnitScope_Group for the AudioUnitScope
//
//		kMusicDeviceProperty_InstrumentNumber		(MusicDeviceInstrumentID*)
//			pass in the instrument "index" in the inElement argument.  This "index" is zero-based and must be less
//			than the number of instruments (determined using the  kMusicDeviceProperty_InstrumentCount property).
//			The value passed back will be a MusicDeviceInstrumentID.  This MusicDeviceInstrumentID may then be used
//			with the kMusicDeviceProperty_InstrumentName property, or in any of the MusicDevice calls which take
//			a MusicDeviceInstrumentID argument.
//
//		kMusicDeviceProperty_UsesInternalReverb		(UInt32*)
//			The DLS/SoundFont MusicDevice uses an internal reverb by default and has one output bus (bus0 is dry+wet)
//			pass in a value of 0 to configure the synth to output reverb send on output bus 0 (dry output is bus 1)
//				This way it's possible to use an alternate reverb external to the MusicDevice
//			pass in a value of 1 (this is default) for internal reverb
//
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// Output device AudioUnits
//
//		kAudioUnitProperty_GetMicroseconds			(Int32* pointing to microseconds value)
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *</PRE>
 */
public final class AUProperties {
	private AUProperties () {}
	
/** Applicable to all AudioUnits in general	(0 -> 999)*/
	public static final int
		kAudioUnitProperty_ClassInfo				= 0,
		kAudioUnitProperty_MakeConnection			= 1,
		kAudioUnitProperty_SampleRate				= 2,
		kAudioUnitProperty_ParameterList			= 3,
		kAudioUnitProperty_ParameterInfo			= 4,
		kAudioUnitProperty_FastDispatch				= 5,
		kAudioUnitProperty_CPULoad					= 6,
		kAudioUnitProperty_SetInputCallback			= 7,		// value is AudioUnitInputCallback
		kAudioUnitProperty_StreamFormat				= 8,	
		kAudioUnitProperty_SRCAlgorithm				= 9,		// value is OSType - manufacturer of the sample rate converter AU to use
		kAudioUnitProperty_ReverbRoomType			= 10,
		kAudioUnitProperty_BusCount					= 11,
		kAudioUnitProperty_Latency					= 12;
		
/** Applicable to MusicDevices				(1000 -> 1999)*/
	public static final int
		kMusicDeviceProperty_InstrumentCount 		= 1000,
		kMusicDeviceProperty_InstrumentName			= 1001,
		kMusicDeviceProperty_GroupOutputBus			= 1002,
		kMusicDeviceProperty_SoundBankFSSpec		= 1003,
		kMusicDeviceProperty_InstrumentNumber 		= 1004,
		kMusicDeviceProperty_UsesInternalReverb		= 1005;
			
/** Applicable to "output" AudioUnits		(2000 -> 2999)*/
	public static final int
		kAudioOutputUnitProperty_CurrentDevice		= 2000;
	
	/**
	 * Properties that can be used with both the AudioConverter and
	 * the Default and System output units to select the sample rate
	 * conversion algorithm
	 */
	public static final int
		kAudioUnitSRCAlgorithm_Polyphase			= 0x706F6C79, //'poly'
		kAudioUnitSRCAlgorithm_MediumQuality		= 0x63737263;// 'csrc'

	/**
	 * These are the reverb types for different acoustic spaces 
	 */
	public static final int
		kReverbRoomType_SmallRoom = 0,
		kReverbRoomType_MediumRoom = 1,
		kReverbRoomType_LargeRoom = 2,
		kReverbRoomType_MediumHall = 3,
		kReverbRoomType_LargeHall = 4,
		kReverbRoomType_Plate = 8;

/** generic value generally between 0.0 and 1.0 */
	public static final int
		kAudioUnitParameterUnit_Generic				= 0;	
/** takes an integer value (good for menu selections) */
	public static final int
		kAudioUnitParameterUnit_Indexed				= 1;	
/** 0.0 means FALSE, non-zero means TRUE */
	public static final int
		kAudioUnitParameterUnit_Boolean				= 2;	
/** usually from 0 -> 100, sometimes -50 -> +50 */
	public static final int
		kAudioUnitParameterUnit_Percent				= 3;	
/** absolute or relative time */
	public static final int
		kAudioUnitParameterUnit_Seconds				= 4;	
/** one sample frame equals (1.0/sampleRate) seconds */
	public static final int
		kAudioUnitParameterUnit_SampleFrames		= 5;	
/** -180 to 180 degrees */
	public static final int
		kAudioUnitParameterUnit_Phase				= 6;	
/** rate multiplier, for playback speed, etc. (e.g. 2.0 == twice as fast) */
	public static final int
		kAudioUnitParameterUnit_Rate				= 7;	
/** absolute frequency/pitch in cycles/second */
	public static final int
		kAudioUnitParameterUnit_Hertz				= 8;	
/** unit of relative pitch */
	public static final int
		kAudioUnitParameterUnit_Cents				= 9;	
/** useful for coarse detuning */
	public static final int
		kAudioUnitParameterUnit_RelativeSemiTones	= 10;	
/** absolute pitch as defined in the MIDI spec (exact freq may depend on tuning table) */
	public static final int
		kAudioUnitParameterUnit_MIDINoteNumber		= 11;	
/** a generic MIDI controller value from 0 -> 127 */
	public static final int
		kAudioUnitParameterUnit_MIDIController		= 12;	
/** logarithmic relative gain */
	public static final int
		kAudioUnitParameterUnit_Decibels			= 13;	
/** linear relative gain */
	public static final int
		kAudioUnitParameterUnit_LinearGain			= 14;	
/** -180 to 180 degrees, similar to phase but more general (good for 3D coord system) */
	public static final int
		kAudioUnitParameterUnit_Degrees				= 15;	
/** 0.0 -> 1.0, crossfade mix two sources according to sqrt(x) and sqrt(1.0 - x) */
	public static final int
		kAudioUnitParameterUnit_EqualPowerCrossfade = 16;
/** 0.0 -> 1.0, pow(x, 3.0) -> linear gain to simulate a reasonable mixer channel fader response */
	public static final int
		kAudioUnitParameterUnit_MixerFaderCurve1	= 17;	
/** standard left to right mixer pan */
	public static final int
		kAudioUnitParameterUnit_Pan					= 18;	
}
/*
 */
