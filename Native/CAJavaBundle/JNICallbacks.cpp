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
	File:		JNICallbacks.cpp
	
	Contents:	Used by callbacks in Java.
	
	Authors:	Doug Wyatt, Patrick C. Beard, Bill Stewart
	
	<Revision History>
*/

/*
	HOW THIS WORKS:
	
	The Java code calls the InitializeMethodClosure call
	-> it returns a jint which is the C address of the JNICallbackBase object
		- if there is a problem, the code will throw an exception when it returns to Java
		
	-> The java code stores the jint in the refcon/clientData of the callback func
	
	-> You define a C version of the callback expected by the API that:
		- gets the JNICallbackBase object from the ref con
		- calls JNICallbackBase::CallMethod passing in the arguments
		- return the value of that CallMethod to the C API caller
	
	-> You define a JNI call that will retrieve the address of your callback function
	
	-> When finished with the closure, call DisposeMethodClosure (from Java presumably)
	that passes in the int that you got back from the Initialize call
*/

#include "JNICallbackBase.h"

#include <AudioUnit/AudioUnit.h>
#include <AudioToolbox/AudioUnitUtilities.h>

#ifdef __cplusplus
extern "C" {
#endif

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#pragma mark ____AudioUnitPropertyListenerProc

void MyAudioUnitPropertyListenerProc (void *inRefCon, 
										AudioUnit ci, 
										AudioUnitPropertyID inID, 
										AudioUnitScope inScope, 
										AudioUnitElement inElement)
{
	((JNICallbackBase*)inRefCon)->CallMethod ((int)ci, inID, inScope, inElement);
}

JNIEXPORT jint JNICALL
Java_com_apple_audio_units_AUDispatcher_GetListenerProcAddress
									(JNIEnv* inEnv, 
									jclass javaClass)
{
	return jint(MyAudioUnitPropertyListenerProc);
}


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#pragma mark ____AudioUnitRenderCallback
OSStatus MyAudioUnitRenderCallback (void *inRefCon, 
									AudioUnitRenderActionFlags inActionFlags, 
									const AudioTimeStamp *inTimeStamp, 
									UInt32 inBusNumber, 
									AudioBuffer *ioData)
{
	return ((JNICallbackBase*)inRefCon)->CallMethod ((int)inActionFlags, 
										inTimeStamp, 
										inBusNumber, 
										ioData);
}

JNIEXPORT jint JNICALL
Java_com_apple_audio_units_AUDispatcher_GetRenderCallbackAddress
									(JNIEnv* inEnv, 
									jclass javaClass)
{
	return jint(MyAudioUnitRenderCallback);
}


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#pragma mark ____AUListener

inline jobject _CreateAUParameter (JNIEnv* jni_env, jint objPtr) 
{
	jclass  AUParam = jni_env->FindClass ("com/apple/audio/toolbox/AUParameter");
	if (AUParam == NULL) {
//		JNIThrowClassNotFoundException (jni_env, "com.apple.audio.toolbox.AUParameter");
		return NULL;
	}
	
	jmethodID constructor = jni_env->GetMethodID (AUParam, "<init>", "(I)V");
	if (constructor == NULL) {
//		JNIThrowCAException (jni_env, "can't find constructor for CAFString");
		return NULL;
	}
	
	return jni_env->NewObject (AUParam, constructor, objPtr);
} // _CreateCFString

void MyAUParameterListenerProc (void *inRefCon,
								void *inObject,
								const AudioUnitParameter *	inParameter,
								Float32						inValue)
{
	jobject callObject = jobject(inObject);
	JavaVM* vm = (JavaVM*)inRefCon;
	JNIEnv*	jni_env;
	
	vm->AttachCurrentThread ((void**)&jni_env, NULL);
	
	jobject param = _CreateAUParameter (jni_env, jint (inParameter));

	if (param == NULL)
		return;
	
	jclass clazz = jni_env->GetObjectClass (callObject);
	
	jmethodID executeMethod = jni_env->GetMethodID (clazz, "execute", "(Lcom/apple/audio/toolbox/AUParameter;F)V");
	
	jni_env->CallIntMethod (callObject, executeMethod, param, inValue);
}

JNIEXPORT jint JNICALL 
Java_com_apple_audio_toolbox_AUListener_GetJavaVM 
	(JNIEnv *inEnv, jclass)
{
	JNICallbackBase::Initialize (inEnv);
	return jint (JNICallbackBase::sJavaVM);
}

JNIEXPORT jobject JNICALL 
Java_com_apple_audio_toolbox_AUListener_CreateGlobalReference
	(JNIEnv* inEnv, jclass javaClass, jobject inJObject)
{
	return inEnv->NewGlobalRef(inJObject);
}

JNIEXPORT void JNICALL 
Java_com_apple_audio_toolbox_AUListener_RemoveGlobalReference
(JNIEnv* inEnv, jclass javaClass, jobject inJObject)
{
	inEnv->DeleteGlobalRef(inJObject);
}

JNIEXPORT jint JNICALL 
Java_com_apple_audio_toolbox_AUListener_GetListenerProcAddress
									(JNIEnv* inEnv, 
									jclass javaClass)
{
	return jint(MyAUParameterListenerProc);
}


#ifdef __cplusplus
}
#endif
