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
/*
 */

#include <CoreServices/CoreServices.h>
#include <sys/param.h>
#include <stdlib.h>

#include "JNIUnits.h"
#include "JNIMidi.h"
#include "JNIToolbox.h"
#include "JNIHardware.h"
#include "CA_Exception.h"
#include "com_apple_audio_jdirect_JDirectNative.h"

JNIEXPORT jint JNICALL Java_com_apple_audio_units_JNIUnits_malloc
  (JNIEnv *, jclass, jint numBytes)
{
	return (jint)malloc (numBytes);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_JNIToolbox_malloc
  (JNIEnv *, jclass, jint numBytes)
{
	return (jint)malloc (numBytes);
}

JNIEXPORT void JNICALL Java_com_apple_audio_units_JNIUnits_free
  (JNIEnv *, jclass, jint ptr) 
{
	free ((void*)ptr);
}

JNIEXPORT void JNICALL Java_com_apple_audio_toolbox_JNIToolbox_free
  (JNIEnv *, jclass, jint ptr) 
{
	free ((void*)ptr);
}
  
inline jint MakeFSSpec (JNIEnv* env, jstring charArray, jint fsspecPtr)
{
	const char* fullpath = env->GetStringUTFChars (charArray, NULL);	// do we care whether it is a copy or not

	char		temppath[MAXPATHLEN];
	FSRef		fsref;
	OSStatus	err;
	// THIS NEEDS TO BE FIXED FOR FILES THAT DON'T EXIST...
	if (realpath(fullpath, temppath) == NULL)
		return dirNFErr;
	err = FSPathMakeRef((UInt8 *)temppath, &fsref, NULL);
	if (err == 0) {
		err = FSGetCatalogInfo(&fsref,
				0,		// FSCatalogInfoBitmap   whichInfo
				NULL,	// FSCatalogInfo *       catalogInfo
				NULL,	// HFSUniStr255 *        outName
				(FSSpec*)fsspecPtr,	// FSSpec *              fsSpec
				NULL);	// FSRef *               parentRef
	}
	return err;
}

JNIEXPORT jint JNICALL 
Java_com_apple_audio_units_JNIUnits_makeFSSpec (JNIEnv *env, jclass clazz, jstring charArray, jint fsspecPtr)
{
	return MakeFSSpec (env, charArray, fsspecPtr);
}

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    makeFSSpec
 * Signature: ()I;
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_JNIToolbox_makeFSSpec
  (JNIEnv *env, jclass, jstring charArray, jint fsspecPtr)
{
	return MakeFSSpec (env, charArray, fsspecPtr);
}
  
inline jobject _CreateCFString (JNIEnv* jni_env, jint strPtr) 
{
	jclass  CAFString = jni_env->FindClass ("com/apple/audio/util/CAFString");
	if (CAFString == NULL) {
		JNIThrowClassNotFoundException (jni_env, "com.apple.audio.util.CAFString");
		return NULL;
	}
	
	jmethodID constructor = jni_env->GetMethodID (CAFString, "<init>", "(I)V");
	if (constructor == NULL) {
		JNIThrowCAException (jni_env, "can't find constructor for CAFString");
		return NULL;
	}
	
	return jni_env->NewObject (CAFString, constructor, strPtr);
} // _CreateCFString

inline jobject _CreateDataRef (JNIEnv* jni_env, jint dataRefPtr) 
{
	jclass  CAFData = jni_env->FindClass ("com/apple/audio/util/CAFData");
	if (CAFData == NULL) {
		JNIThrowClassNotFoundException (jni_env, "com.apple.audio.util.CAFData");
		return NULL;
	}
	
	jmethodID constructor = jni_env->GetMethodID (CAFData, "<init>", "(I)V");
	if (constructor == NULL) {
		JNIThrowCAException (jni_env, "can't find constructor for CAFData");
		return NULL;
	}
	
	return jni_env->NewObject (CAFData, constructor, dataRefPtr);
} // _CreateDataRef

inline jobject _CreateCAMemoryObject (JNIEnv *jni_env, jint thePtr, jint theSize, jobject theOwner)
{
	jclass  CAMemoryObject = jni_env->FindClass ("com/apple/audio/util/CAMemoryObject");
	if (CAMemoryObject == NULL) {
		JNIThrowClassNotFoundException (jni_env, "com.apple.audio.util.CAMemoryObject");
		return NULL;
	}
	
	jmethodID constructor = jni_env->GetMethodID (CAMemoryObject, "<init>", "(IILjava/lang/Object;)V");
	if (constructor == NULL) {
		JNIThrowCAException (jni_env, "can't find constructor for CAMemoryObject");
		return NULL;
	}
	
	return jni_env->NewObject (CAMemoryObject, constructor, thePtr, theSize, theOwner);
} // _CreateCAMemoryObject

inline jobject _CreateMIDIData (JNIEnv *jni_env, jint thePtr, jint theSize, jobject theOwner, jint whichOne)
{
	jclass  MIDIData = jni_env->FindClass ("com/apple/audio/util/MIDIData");
	if (MIDIData == NULL) {
		JNIThrowClassNotFoundException (jni_env, "com.apple.audio.util.MIDIData");
		return NULL;
	}
	
	jmethodID constructor = jni_env->GetMethodID (MIDIData, "<init>", "(IILjava/lang/Object;I)V");
	if (constructor == NULL) {
		JNIThrowCAException (jni_env, "can't find constructor for MIDIData");
		return NULL;
	}
	
	return jni_env->NewObject (MIDIData, constructor, thePtr, theSize, theOwner, whichOne);
} // _CreateMIDIData

inline jobject _CreateMIDIEndpoint (JNIEnv *jni_env, jint thePtr, jobject theOwner)
{
	jclass  MIDIEndpoint = jni_env->FindClass ("com/apple/audio/midi/MIDIEndpoint");
	if (MIDIEndpoint == NULL) {
		JNIThrowClassNotFoundException (jni_env, "com.apple.audio.midi.MIDIEndpoint");
		return NULL;
	}
	
	jmethodID constructor = jni_env->GetMethodID (MIDIEndpoint, "<init>", "(ILjava/lang/Object;)V");
	if (constructor == NULL) {
		JNIThrowCAException (jni_env, "can't find constructor for MIDIEndpoint");
		return NULL;
	}
	
	return jni_env->NewObject (MIDIEndpoint, constructor, thePtr, theOwner);
} // _CreateMIDIEndpoint

/*
 * Class:     com_apple_audio_midi_JNIMidi
 * Method:    newCFData
 * Signature: (I)Lcom/apple/audio/util/CAFData;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_midi_JNIMidi_newCFData (JNIEnv *jni_env, jclass, jint dataPtr) 
{
	return _CreateDataRef (jni_env, dataPtr);
}

/*
 * Class:     com_apple_audio_midi_JNIMidi
 * Method:    newCFString
 * Signature: (I)Lcom/apple/audio/util/CAFString;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_midi_JNIMidi_newCFString (JNIEnv *jni_env, jclass, jint strPtr)
{
	return _CreateCFString (jni_env, strPtr);
}

/*
 * Class:     com_apple_audio_midi_JNIMidi
 * Method:    runRunLoop
 * Signature: (D)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_midi_JNIMidi_runRunLoop (JNIEnv *jni_env, jclass, jdouble timeOut)
{
	CFRunLoopRunInMode(kCFRunLoopDefaultMode, (double)timeOut, false);
}

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    newCAMemoryObject
 * Signature: (IILjava/lang/Object;)Lcom/apple/audio/util/CAMemoryObject;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_toolbox_JNIToolbox_newCAMemoryObject (JNIEnv *jni_env, jclass, jint thePtr, jint theSize, jobject theOwner)
{
	return _CreateCAMemoryObject (jni_env, thePtr, theSize, theOwner);
}
/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    newMusicUserEvent
 * Signature: (IILjava/lang/Object;)Lcom/apple/audio/util/MusicUserEvent;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_toolbox_JNIToolbox_newMusicUserEvent
(JNIEnv *jni_env, jclass, jint thePtr, jint theSize, jobject theOwner)
{
	jclass  MusicUserEventClass = jni_env->FindClass ("com/apple/audio/util/MusicUserEvent");
	if (MusicUserEventClass == NULL) {
		JNIThrowClassNotFoundException (jni_env, "com.apple.audio.util.MusicUserEvent");
		return NULL;
	}
	
	jmethodID constructor = jni_env->GetMethodID (MusicUserEventClass, "<init>", "(IILjava/lang/Object;)V");
	if (constructor == NULL) {
		JNIThrowCAException (jni_env, "can't find constructor for MusicUserEvent");
		return NULL;
	}
	
	return jni_env->NewObject (MusicUserEventClass, constructor, thePtr, theSize, theOwner);
}

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    newParameterEvent
 * Signature: (IILjava/lang/Object;)Lcom/apple/audio/util/ParameterEvent;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_toolbox_JNIToolbox_newParameterEvent (JNIEnv *jni_env, jclass, jint thePtr, jint theSize, jobject theOwner)
{
	jclass  ParameterEventClass = jni_env->FindClass ("com/apple/audio/util/ParameterEvent");
	if (ParameterEventClass == NULL) {
		JNIThrowClassNotFoundException (jni_env, "com.apple.audio.util.ParameterEvent");
		return NULL;
	}
	
	jmethodID constructor = jni_env->GetMethodID (ParameterEventClass, "<init>", "(IILjava/lang/Object;)V");
	if (constructor == NULL) {
		JNIThrowCAException (jni_env, "can't find constructor for ParameterEvent");
		return NULL;
	}
	
	return jni_env->NewObject (ParameterEventClass, constructor, thePtr, theSize, theOwner);
}

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    findClassForComponentInstance
 * Signature: (ILjava/lang/Object;)Lcom/apple/audio/units/AudioUnit;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_toolbox_JNIToolbox_findClassForComponentInstance (JNIEnv *jni_env, jclass, jint compInst, jobject theOwner)
{
	jclass  AUComponent = jni_env->FindClass ( "com/apple/audio/units/AUComponent" );
	if (AUComponent == NULL) {
		JNIThrowClassNotFoundException (jni_env, "com.apple.audio.units.AUComponent");
		return NULL;
	}
	
	jmethodID findClassForCompInstance = jni_env->GetStaticMethodID (AUComponent, "findClassForComponentInstance", "(ILjava/lang/Object;)Lcom/apple/audio/units/AudioUnit;");
	if (findClassForCompInstance == NULL) {
		JNIThrowCAException (jni_env, "can't find method AudioDevice.findClassForComponentInstance(IL...;)L...;");
		return NULL;
	}
	
	return jni_env->CallStaticObjectMethod (AUComponent, findClassForCompInstance, compInst, theOwner);

}

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    newExtendedNoteParams
 * Signature: (IILjava/lang/Object;)Lcom/apple/audio/util/ExtendedNoteParams;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_toolbox_JNIToolbox_newExtendedNoteParams (JNIEnv *jni_env, jclass, jint thePtr, jint numFloats, jobject theOwner)
{
	jclass  ExtNoteParams = jni_env->FindClass ("com/apple/audio/util/ExtendedNoteParams");
	if (ExtNoteParams == NULL) {
		JNIThrowClassNotFoundException (jni_env, "com.apple.audio.util.ExtendedNoteParams");
		return NULL;
	}
	
	jmethodID constructor = jni_env->GetMethodID (ExtNoteParams, "<init>", "(IILjava/lang/Object;)V");
	if (constructor == NULL) {
		JNIThrowCAException (jni_env, "can't find constructor for ExtendedNoteParams");
		return NULL;
	}
	
	return jni_env->NewObject (ExtNoteParams, constructor, thePtr, numFloats, theOwner);
}

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    newMIDIData
 * Signature: (IILjava/lang/Object;I)Lcom/apple/audio/util/MIDIData;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_toolbox_JNIToolbox_newMIDIData (JNIEnv *jni_env, jclass, jint thePtr, jint theSize, jobject theOwner, jint whichOne)
{
	return _CreateMIDIData (jni_env, thePtr, theSize, theOwner, whichOne);
}

/*
 * Class:     com_apple_audio_toolbox_JNIToolbox
 * Method:    newMIDIEndpoint
 * Signature: (ILjava/lang/Object;)Lcom/apple/audio/midi/MIDIEndpoint;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_toolbox_JNIToolbox_newMIDIEndpoint (JNIEnv *jni_env, jclass, jint thePtr, jobject theOwner)
{
	return _CreateMIDIEndpoint (jni_env, thePtr, theOwner);

}

/*
 * Class:     com_apple_audio_units_JNIUnits
 * Method:    newAudioDevice
 * Signature: (I)Lcom/apple/audio/hardware/AudioDevice;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_units_JNIUnits_newAudioDevice (JNIEnv *jni_env, jclass, jint audioDevicePtr) 
{
	jclass  AudioDevice = jni_env->FindClass ( "com/apple/audio/hardware/AudioDevice" );
	if (AudioDevice == NULL) {
		JNIThrowClassNotFoundException (jni_env, "com.apple.audio.hardware.AudioDevice");
		return NULL;
	}
	
	jmethodID makeDevice = jni_env->GetStaticMethodID (AudioDevice, "makeDevice", "(I)Lcom/apple/audio/hardware/AudioDevice;");
	if (makeDevice == NULL) {
		JNIThrowCAException (jni_env, "can't find method AudioDevice.makeDevice(I)L...;");
		return NULL;
	}
	
	return jni_env->CallStaticObjectMethod (AudioDevice, makeDevice, audioDevicePtr);
} // Java_com_apple_audio_units_AU_JNI_Native_getCurrentDeviceFromDefault

/*
 * Class:     com_apple_audio_midi_JNIMidi
 * Method:    newCFString
 * Signature: (I)Lcom/apple/audio/util/CAFString;
 */
JNIEXPORT jobject JNICALL Java_com_apple_audio_hardware_JNIHardware_newCFString (JNIEnv *jni_env, jclass, jint strPtr)
{
	return _CreateCFString (jni_env, strPtr);
}

JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_JDirectNative_CFRelease
  (JNIEnv *, jclass, jint cfObj)
{
	CFRelease ((const void*)cfObj);
}

JNIEXPORT jobject JNICALL Java_com_apple_audio_jdirect_JDirectNative_newCAFString
  (JNIEnv *jni_env, jclass, jint strPtr)
{
	return _CreateCFString (jni_env, strPtr);
}

JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_JDirectNative_CFRetain
  (JNIEnv *jni_env, jclass, jint strPtr)
{
    CFRetain ((CFStringRef)strPtr);
}

/*
inline void _SetNativeRep (JNIEnv *jni_env, jobject theCAObject, jint theNewPtr)
{
	jclass  CAObject = jni_env->GetObjectClass (theCAObject);
	if (CAObject == NULL) {
		JNIThrowClassNotFoundException (jni_env, "com.apple.audio.CAObject");
		return;
	}
	
	jmethodID setNR = jni_env->GetMethodID (CAObject, "setNR", "(I)V");
	if (setNR == NULL) {
		JNIThrowCAException (jni_env, "can't find setNR method");
		return;
	}

	return jni_env->CallVoidMethod (theCAObject, setNR, theNewPtr);
} // _SetNativeRep
*/
