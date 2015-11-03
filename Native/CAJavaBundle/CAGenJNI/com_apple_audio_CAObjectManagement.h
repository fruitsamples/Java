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
/* Header for class com_apple_audio_CAObjectManagement */
#ifndef _Included_com_apple_audio_CAObjectManagement
#define _Included_com_apple_audio_CAObjectManagement
#ifdef __cplusplus
extern "C" {
#endif
#undef com_apple_audio_CAObjectManagement_debug
#define com_apple_audio_CAObjectManagement_debug 0L
#undef com_apple_audio_CAObjectManagement_addDebug
#define com_apple_audio_CAObjectManagement_addDebug 0L
#undef com_apple_audio_CAObjectManagement_removeDebug
#define com_apple_audio_CAObjectManagement_removeDebug 0L
/* Inaccessible static: addDebugCount */
/* Inaccessible static: removeDebugCount */
/* Inaccessible static: caObjs */
/*
 * Class:     com_apple_audio_CAObjectManagement
 * Method:    free
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_CAObjectManagement_free
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_apple_audio_CAObjectManagement
 * Method:    CloseComponent
 * Signature: (I)S
 */
JNIEXPORT jshort JNICALL Java_com_apple_audio_CAObjectManagement_CloseComponent
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_apple_audio_CAObjectManagement
 * Method:    DisposeAUGraph
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_CAObjectManagement_DisposeAUGraph
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_apple_audio_CAObjectManagement
 * Method:    DisposeMusicPlayer
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_CAObjectManagement_DisposeMusicPlayer
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_apple_audio_CAObjectManagement
 * Method:    DisposeMusicSequence
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_CAObjectManagement_DisposeMusicSequence
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_apple_audio_CAObjectManagement
 * Method:    DisposeMusicEventIterator
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_CAObjectManagement_DisposeMusicEventIterator
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_apple_audio_CAObjectManagement
 * Method:    CFRelease
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_CAObjectManagement_CFRelease
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_apple_audio_CAObjectManagement
 * Method:    MIDISetupDispose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_CAObjectManagement_MIDISetupDispose
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_apple_audio_CAObjectManagement
 * Method:    MIDIClientDispose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_CAObjectManagement_MIDIClientDispose
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_apple_audio_CAObjectManagement
 * Method:    MIDIEndpointDispose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_CAObjectManagement_MIDIEndpointDispose
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_apple_audio_CAObjectManagement
 * Method:    MIDIPortDispose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_CAObjectManagement_MIDIPortDispose
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_apple_audio_CAObjectManagement
 * Method:    AudioConverterDispose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_CAObjectManagement_AudioConverterDispose
  (JNIEnv *, jclass, jint);

JNIEXPORT jint JNICALL Java_com_apple_audio_CAObjectManagement_AUListenerDispose  (JNIEnv *, jclass, jint inListener);

JNIEXPORT jint JNICALL Java_com_apple_audio_CAObjectManagement_AUMIDIControllerDispose  (JNIEnv *, jclass, jint inController);

#ifdef __cplusplus
}
#endif
#endif
