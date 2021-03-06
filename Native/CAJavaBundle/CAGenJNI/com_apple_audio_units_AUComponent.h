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
/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_apple_audio_units_AUComponent */

#ifndef _Included_com_apple_audio_units_AUComponent
#define _Included_com_apple_audio_units_AUComponent
#ifdef __cplusplus
extern "C" {
#endif
#undef com_apple_audio_units_AUComponent_defaultComponentIdentical
#define com_apple_audio_units_AUComponent_defaultComponentIdentical 0L
#undef com_apple_audio_units_AUComponent_defaultComponentAnyFlags
#define com_apple_audio_units_AUComponent_defaultComponentAnyFlags 1L
#undef com_apple_audio_units_AUComponent_defaultComponentAnyManufacturer
#define com_apple_audio_units_AUComponent_defaultComponentAnyManufacturer 2L
#undef com_apple_audio_units_AUComponent_defaultComponentAnySubType
#define com_apple_audio_units_AUComponent_defaultComponentAnySubType 4L
#undef com_apple_audio_units_AUComponent_defaultComponentAnyFlagsAnyManufacturer
#define com_apple_audio_units_AUComponent_defaultComponentAnyFlagsAnyManufacturer 3L
#undef com_apple_audio_units_AUComponent_defaultComponentAnyFlagsAnyManufacturerAnySubType
#define com_apple_audio_units_AUComponent_defaultComponentAnyFlagsAnyManufacturerAnySubType 7L
/* Inaccessible static: tempInfo */
/*
 * Class:     com_apple_audio_units_AUComponent
 * Method:    OpenDefaultComponent
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_units_AUComponent_OpenDefaultComponent
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     com_apple_audio_units_AUComponent
 * Method:    OpenComponent
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_units_AUComponent_OpenComponent
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_apple_audio_units_AUComponent
 * Method:    GetComponentInfo
 * Signature: (IIIII)S
 */
JNIEXPORT jshort JNICALL Java_com_apple_audio_units_AUComponent_GetComponentInfo
  (JNIEnv *, jclass, jint, jint, jint, jint, jint);

/*
 * Class:     com_apple_audio_units_AUComponent
 * Method:    OpenDefaultAudioOutput
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_units_AUComponent_OpenDefaultAudioOutput
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_apple_audio_units_AUComponent
 * Method:    OpenSystemSoundAudioOutput
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_units_AUComponent_OpenSystemSoundAudioOutput
  (JNIEnv *, jclass, jint);

#ifdef __cplusplus
}
#endif
#endif
