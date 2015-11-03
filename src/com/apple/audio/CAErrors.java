/*	Copyright: 	� Copyright 2003 Apple Computer, Inc. All rights reserved.

	Disclaimer:	IMPORTANT:  This Apple software is supplied to you by Apple Computer, Inc.
			("Apple") in consideration of your agreement to the following terms, and your
			use, installation, modification or redistribution of this Apple software
			constitutes acceptance of these terms.  If you do not agree with these terms,
			please do not use, install, modify or redistribute this Apple software.

			In consideration of your agreement to abide by the following terms, and subject
			to these terms, Apple grants you a personal, non-exclusive license, under Apple�s
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
//  CAException.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio;

/**
 * Contains the errors that can be generated by CoreAudio calls
 */
public final class CAErrors {
	protected CAErrors () {}

	public static final int kNoErr = 0;
	
	public static final int paramErr = -50;
	
	public static final int
		kAudioHardwareNoError				= 0,
		kAudioHardwareNotRunningError		= 0x73746f70,  //'stop'FOUR_CHAR_CODE('stop'),
		kAudioHardwareUnspecifiedError		= 0x77686174,  //'what'FOUR_CHAR_CODE('what'),
		kAudioHardwareUnknownPropertyError	= 0x77686f3f,  //'who?'FOUR_CHAR_CODE('who?'),
		kAudioHardwareBadPropertySizeError	= 0x2173697a,  //'!siz'FOUR_CHAR_CODE('!siz'),
		kAudioHardwareIllegalOperationError	= 0x6e6f7065,  //'nope'FOUR_CHAR_CODE('nope')
		kAudioHardwareBadDeviceError = 0x21646576,//!dev,
		kAudioHardwareBadStreamError = 0x21737472,//!str
		kAudioDeviceUnsupportedFormatError	= 0x21646174,  //'!dat'FOUR_CHAR_CODE('!dat'),
		kAudioDevicePermissionsError = 0x21686f67;//'!hog'
	
	public static final int
		kMIDIInvalidClient          = -10830,
		kMIDIInvalidPort            = -10831,
		kMIDIWrongEndpointType      = -10832,	/* want source, got destination, or vice versa*/
		kMIDINoConnection           = -10833,	/* attempt to close a non-existant connection*/
		kMIDIUnknownEndpoint        = -10834,
		kMIDIUnknownProperty        = -10835,
		kMIDIWrongPropertyType      = -10836,
		kMIDINoCurrentSetup         = -10837,	/* there is no current setup, or it contains no devices*/
		kMIDIMessageSendErr         = -10838,	/* communication with server failed*/
		kMIDIServerStartErr         = -10839,	/* couldn't start the server*/
		kMIDISetupFormatErr         = -10840,   /* unparseable saved state*/
		kMIDIWrongThread		= -10841;		/* driver is calling non I/O function in server than from a thread other server's main one*/
		
	public static final int
		kAudioUnitErr_InvalidProperty = -10879,
		kAudioUnitErr_InvalidParameter = -10878,
		kAudioUnitErr_InvalidElement = -10877,
		kAudioUnitErr_NoConnection = -10876,
		kAudioUnitErr_FailedInitialization = -10875,
		kAudioUnitErr_TooManyFramesToProcess = -10874,
		kAudioUnitErr_IllegalInstrument = -10873,
		kAudioUnitErr_InstrumentTypeNotFound = -10872,
		kAudioUnitErr_InvalidFile = -10871,
		kAudioUnitErr_UnknownFileType = -10870,
		kAudioUnitErr_FileNotSpecified = -10869,
  		kAudioUnitErr_FormatNotSupported = -10868,
  		kAudioUnitErr_Uninitialized   = -10867;

	public static final int
		kAUGraphErr_NodeNotFound = -10860,	
		kAUGraphErr_InvalidConnection 	= -10861;

	public static final int
		kAudioToolboxErr_TrackIndexError 		= -10859,
		kAudioToolboxErr_TrackNotFound			= -10858,
		kAudioToolboxErr_EndOfTrack				= -10857,
		kAudioToolboxErr_StartOfTrack			= -10856,
		kAudioToolboxErr_IllegalTrackDestination = -10855,
		kAudioToolboxErr_NoSequence 			= -10854,
		kAudioToolboxErr_InvalidEventType		= -10853,
		kAudioToolboxErr_InvalidPlayerState		= -10852;

	public static final int
		kAudioConverterErr_FormatNotSupported		= 0x666d743f,//'fmt?',
		kAudioConverterErr_OperationNotSupported	= 0x6f703f3f,//'op??',
		kAudioConverterErr_PropertyNotSupported		= 0x70726f70,//'prop',
		kAudioConverterErr_InvalidInputSize			= 0x696e737a,//'insz',
		kAudioConverterErr_InvalidOutputSize		= 0x6f74737a;//'otsz'



/** @deprecated misspelled! */
	public static final int
		kAudioToobloxErr_TrackIndexError 		= -10859;
/** @deprecated misspelled! */
	public static final int
		kAudioToobloxErr_TrackNotFound			= -10858;
/** @deprecated misspelled! */
	public static final int
		kAudioToobloxErr_EndOfTrack				= -10857;
/** @deprecated misspelled! */
	public static final int
		kAudioToobloxErr_StartOfTrack			= -10856;
}

/*
 */