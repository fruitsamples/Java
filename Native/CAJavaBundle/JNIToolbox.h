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
/* Generated for class com.apple.audio.toolbox.JNIToolbox */
/* Generated from JNIToolbox.java*/

#ifndef _Included_com_apple_audio_toolbox_JNIToolbox
#define _Included_com_apple_audio_toolbox_JNIToolbox
#include <jni.h>
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    newCAMemoryObject
 * Signature: (IILjava/lang/Object;)Lcom/apple/audio/util/CAMemoryObject;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_toolbox_JNIToolbox_newCAMemoryObject
  (JNIEnv *, jclass, jint, jint, jobject);

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    newCAMemoryObject
 * Signature: (IILjava/lang/Object;)Lcom/apple/audio/util/MusicUserEvent;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_toolbox_JNIToolbox_newMusicUserEvent
  (JNIEnv *, jclass, jint, jint, jobject);

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    newParameterEvent
 * Signature: (IILjava/lang/Object;)Lcom/apple/audio/util/ParameterEvent;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_toolbox_JNIToolbox_newParameterEvent
  (JNIEnv *jni_env, jclass, jint thePtr, jint theSize, jobject theOwner);

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    findClassForComponentInstance
 * Signature: (ILjava/lang/Object;)Lcom/apple/audio/units/AudioUnit;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_toolbox_JNIToolbox_findClassForComponentInstance
  (JNIEnv *, jclass, jint, jobject);

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    newExtendedNoteParams
 * Signature: (IILjava/lang/Object;)Lcom/apple/audio/util/ExtendedNoteParams;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_toolbox_JNIToolbox_newExtendedNoteParams
  (JNIEnv *, jclass, jint, jint, jobject);

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    newMIDIData
 * Signature: (IILjava/lang/Object;I)Lcom/apple/audio/util/MIDIData;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_toolbox_JNIToolbox_newMIDIData
  (JNIEnv *, jclass, jint, jint, jobject, jint);

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    newMIDIEndpoint
 * Signature: (ILjava/lang/Object;)Lcom/apple/audio/midi/MIDIEndpoint;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_toolbox_JNIToolbox_newMIDIEndpoint
  (JNIEnv *, jclass, jint, jobject);

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    makeFSSpec
 * Signature: ()I;
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_JNIToolbox_makeFSSpec
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    malloc
 * Signature: (I)I;
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_JNIToolbox_malloc
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    malloc
 * Signature: (I)V;
 */
JNIEXPORT void JNICALL Java_com_apple_audio_toolbox_JNIToolbox_free
  (JNIEnv *, jclass, jint);

#ifdef __cplusplus
}
#endif
#endif