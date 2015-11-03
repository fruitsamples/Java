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
//  AUConstants.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.units;

/**
 * This class contains the general constants defined for use with AudioUnits
 */
public final class AUConstants {
	private AUConstants () {}
 
 		/** AudioUnit Component type*/
	public static final int
 		kAudioUnitComponentType       = 0x61756e74;  //'aunt'FOUR_CHAR_CODE('aunt'),

		/** AudioUnit Component sub-types*/
	public static final int
		kAudioUnitSubType_Output       = 0x6f757420,  //'out 'FOUR_CHAR_CODE('out '),
		kAudioUnitSubType_MusicDevice  = 0x6d757364,  //'musd'FOUR_CHAR_CODE('musd'),
		kAudioUnitSubType_SampleRateConvertor = 0x73726376,  //'srcv''srcv'
		kAudioUnitSubType_Effect      = 0x65666374,  //'efct' FOUR_CHAR_CODE('efct'),
		kAudioUnitSubType_Mixer       = 0x6d697872,  //'mixr'FOUR_CHAR_CODE('mixr'),
		kAudioUnitSubType_Encoder       = 0x61656e63,  //'aenc'FOUR_CHAR_CODE('aenc')
		kAudioUnitSubType_Decoder       = 0x61646563,  //'adec'FOUR_CHAR_CODE('adec')
		kAudioUnitSubType_FormatConverter = 0x666d7463,  //'fmtc'
		kAudioUnitSubType_Adapter     = 0x61647074; //FOUR_CHAR_CODE('adpt'),

		/** AudioUnit Component ID-types (These fill in the manufacturers field*/
	public static final int
		kAudioUnitID_HALOutput = 0x6168616c,  //'ahal'FOUR_CHAR_CODE('ahal'),
		kAudioUnitID_DefaultOutput = 0x64656620,  //'def ''def '
		kAudioUnitID_SystemOutput = 0x73797320,//FOUR_CHAR_CODE('sys ');
		kAudioUnitID_GenericOutput = 0x67656e72; //'genr'
		
		/** AudioUnit Component ID-types (These fill in the manufacturers field*/
	public static final int
		kAudioUnitID_Interleaver      = 0x696e6c76,  //'inlv'FOUR_CHAR_CODE('inlv'),
		kAudioUnitID_Deinterleaver    = 0x646e6c76,  //'dnlv'FOUR_CHAR_CODE('dnlv'),
		kAudioUnitID_LowPassFilter    = 0x6c706173,  //'lpas'FOUR_CHAR_CODE('lpas'),
		kAudioUnitID_MatrixReverb     = 0x6d726576,  //'mrev'FOUR_CHAR_CODE('mrev'),
		kAudioUnitID_Delay            = 0x64656c79,  //'dely'FOUR_CHAR_CODE('dely'),
		kAudioUnitID_PeakLimiter	  = 0x6c6d7472,  //'lmtr' FOUR_CHAR_CODE('lmtr'),
		kAudioUnitID_StereoMixer      = 0x736d7872,  //'smxr'FOUR_CHAR_CODE('smxr'),
		kAudioUnitID_PolyphaseSRC	  = 0x706f6c79,  //'poly'
		kAudioUnitID_DLSSynth  = 0x646c7320,  //'dls 'FOUR_CHAR_CODE('dls ') 
		kAudioUnitID_HighPassFilter   = 0x68706173, // FOUR_CHAR_CODE('hpas'),
		kAudioUnitID_BandPassFilter   = 0x62706173, // FOUR_CHAR_CODE('bpas'),
		kAudioUnitID_DynamicsProcessor = 0x64636d70;//FOUR_CHAR_CODE('dcmp'),
	
	
	// these are the new types for v2 components
	public static final int
		kAudioUnitType_Output = 0x61756f75, //'auou'
		kAudioUnitSubType_HALOutput = 0x6168616c, //'ahal' 	'ahal'
		kAudioUnitSubType_DefaultOutput = 0x64656620, //'def '  'def '
		kAudioUnitSubType_SystemOutput = 0x73797320, //'sys '	'sys '
		kAudioUnitSubType_GenericOutput = 0x67656e72, //'genr' 'genr'
		kAudioUnitType_MusicDevice = 0x61756d75, //'aumu'		'aumu'
		kAudioUnitType_FormatConverter = 0x61756663, //'aufc' 	'aufc'
		kAudioUnitSubType_AUConverter = 0x636f6e76, //'conv'	'conv'
		kAudioUnitType_Effect = 0x61756678, //'aufx'			'aufx'
		kAudioUnitType_Mixer = 0x61756d78, //'aumx'			'aumx'
		kAudioUnitManufacturer_Apple = 0x6170706c; //'appl'	'appl'
	
	public static final int
		kAudioUnitScope_Global        = 0,
		kAudioUnitScope_Input         = 1,
		kAudioUnitScope_Output        = 2,
		kAudioUnitScope_Group         = 3;

	public static final int
		kAudioUnitParameterFlag_Global		= (1 << 0),	//	parameter scope is global
		kAudioUnitParameterFlag_Input		= (1 << 1),	//	parameter scope is input
		kAudioUnitParameterFlag_Output		= (1 << 2),	//	parameter scope is output
		kAudioUnitParameterFlag_Group		= (1 << 3),	//	parameter scope is group
		kAudioUnitParameterFlag_IsReadable	= (1 << 30),
		kAudioUnitParameterFlag_IsWritable	= (1 << 31);
	
	public static final int
		kAudioUnitRenderAction_Accumulate	 		= (1 << 0),
		kAudioUnitRenderAction_UseProvidedBuffer 	= (1 << 1),
		kAudioUnitRenderAction_PreRender 			= (1 << 2),
		kAudioUnitRenderAction_PostRender 			= (1 << 3);
}
