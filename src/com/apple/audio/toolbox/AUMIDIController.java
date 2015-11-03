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
//  AUMIDIController.java
//  CoreAudio.proj
//
//  Copyright (c) 2001 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.toolbox;

import com.apple.audio.*;
import com.apple.audio.midi.*;
import com.apple.audio.util.*;
import com.apple.audio.units.*;
import com.apple.audio.jdirect.Accessor;

public class AUMIDIController extends CAObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}
	
	static final int firstArg4Ptr = JNIToolbox.malloc(4);

//_________________________ STATIC METHODS
	/**
 	 * <BR><BR><b>CoreAudio::AUMIDIControllerCreate</b><BR><BR>
	 */
	public AUMIDIController () throws CAException {
		this (null);
	}
	
	/**
	 * inVirtualDestinationName is null to create no virtual destination
 	 * <BR><BR><b>CoreAudio::AUMIDIControllerCreate</b><BR><BR>
	 */
	public AUMIDIController (CAFString inVirtualDestinationName) throws CAException {
		super (init (inVirtualDestinationName));
	}
	
	private static int init (CAFString inDestName) throws CAException {
		int res = AUMIDIControllerCreate (CAObject.ID(inDestName),firstArg4Ptr);
		CAException.checkError (res);
		return Accessor.getIntFromPointer (firstArg4Ptr, 0);
	}

//_________________________ INSTANCE METHODS
	
	/**
	 * Set up a mapping between a MIDI channel to an AudioUnit
 	 * <BR><BR><b>CoreAudio::AUMIDIControllerMapChannelToAU</b><BR><BR>
	 * @param inSourceMIDIChannel is 0-15, or -1 to map all 16 channels to one AU.
	 * @param inAudioUnit can be 0 to remove channel mappings.
	 * @param inDestMIDIChannel is 0-15.  If inSourceMIDIChannel is -1, it's ignored.  Otherwise,
			when channel events are routed to a MusicDevice, they are rechannelized to this
			channel.
	 * @param inCreateDefaultControlMappings is true, the MIDI->parameter mappings are obtained
			from the AudioUnit, if it supplies them, or the default control mapping described
			in detail above is used.  When inCreateDefaultControlMappings is false, no mappings
			are made.  If the AudioUnit is a MusicDevice, this argument is ignored and default
			control mappings are NOT made; the MusicDevice must implement response to MIDI
			controls itself.
	 */
	public void mapChannelToAU (int 		inSourceMIDIChannel, 
								AudioUnit 	inAudioUnit,
								int			inDestMIDIChannel,
								boolean		inCreateDefaultControlMappings) throws CAException
	{
		int res = AUMIDIControllerMapChannelToAU(_ID(),
								inSourceMIDIChannel,
								CAObject.ID(inAudioUnit),
								inDestMIDIChannel,
								(byte)(inCreateDefaultControlMappings ? 1 : 0));
		CAException.checkError (res);
	}

	/**
	 * Set up a mapping between a MIDI controller and an AudioUnit's SetParameter.
 	 * <BR><BR><b>CoreAudio::AUMIDIControllerMapEventToParameter</b><BR><BR>
	 * @param inMIDIStatusByte specifies the event type and channel to be mapped (only
		control events, status bytes 0xB0-0xBF, are supported).
	 * @param inMIDIControl specifies the MIDI control number to be mapped.  MIDI controls 32-63
		are always parsed as the LSB's of controls 0-31, so they may not be mapped
		separately.  Also, the following MIDI controls have special meanings and thus
		may not be mapped in this manner:
			6, 38		data entry MSB, LSB
			96-101		data increment, decrement, RPN, NRPN select
		
		To specify a NRPN, put the 14-bit MSB/LSB of the parameter number in the low 14 bits
		of the UInt16, and or that with 0x8000.
	
	 * @param inParameter the AudioUnit member can be null to remove a mapping.
	 */
	public void mapEventToParameter (int inMIDIStatusByte, 
										int inMIDIControl,
										AUParameter inParameter) throws CAException 
	{
		int res = AUMIDIControllerMapEventToParameter(_ID(),
										(byte)(0xFF & inMIDIStatusByte),
										(short)inMIDIControl,
										CAObject.ID(inParameter));
		CAException.checkError (res);
	}
 	
	/**
	 * <BR><BR><b>CoreAudio::AUMIDIControllerHandleMIDI</b><BR><BR>
	 */
	public void handleMIDI (MIDIPacketList inMIDIPacketList) throws CAException
	{
		int res = AUMIDIControllerHandleMIDI(_ID(), CAObject.ID(inMIDIPacketList));
		CAException.checkError (res);
	}

	/**
	 * <BR><BR><b>CoreAudio::AUMIDIControllerConnectSource</b><BR><BR>
	 */
	public void connectSource (MIDIEndpoint inSource) throws CAException
	{
		int res = AUMIDIControllerConnectSource (_ID(), CAObject.ID(inSource));
		CAException.checkError (res);
	}

	/**
	 * <BR><BR><b>CoreAudio::AUMIDIControllerDisconnectSource</b><BR><BR>
	 */
	public void disconnectSource (MIDIEndpoint inSource) throws CAException
	{
		int res = AUMIDIControllerDisconnectSource (_ID(), CAObject.ID(inSource));
		CAException.checkError (res);
	}
	
//_ NATIVE METHODS
	private static native int AUMIDIControllerCreate(int	inVirtualDestinationName,
												int outController);

	private static native int AUMIDIControllerMapChannelToAU(int inController,
									int		inSourceMIDIChannel,
									int		inAudioUnit,
									int		inDestMIDIChannel,
									byte	inCreateDefaultControlMappings);

	private static native int AUMIDIControllerMapEventToParameter (int inController,
										byte		inMIDIStatusByte,
										short		inMIDIControl,
										int 		inParameter);
										
	private static native int AUMIDIControllerHandleMIDI (int inController,
										int 		inMIDIPacketList);


	private static native int AUMIDIControllerConnectSource (int inController,
										int			inSource);

	private static native int AUMIDIControllerDisconnectSource (int inController,
										int			inSource);
}

/*
 */
