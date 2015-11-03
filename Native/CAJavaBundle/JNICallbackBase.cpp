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
	File:		JNICallbackBase.cp
	
	Contents:	Used by callbacks in Java.
	
	Authors:	Doug Wyatt, Patrick C. Beard, Bill Stewart
	
	<Revision History>
*/

#include "MethodClosure.h"
#include "JNICallbackBase.h"
#include "CA_Exception.h"

#include <pthread.h>

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#pragma mark ____JNICallbackBase

JavaVM* JNICallbackBase::sJavaVM = NULL;
	
JNICallbackBase::JNICallbackBase (JNIEnv* env, 
								jobject inJObject, 
								jstring inMethodName, 
								jstring inMethodSignature)
	:mCallMethod (0),
	 mCallObject (0),
	 mJEnv (0),
	 mThreadID (0)
{
	Initialize (env);
	
	jclass localObjectClass = env->GetObjectClass (inJObject);
	const char* methodNameUTF = env->GetStringUTFChars (inMethodName, NULL);
	const char* methodSigUTF = env->GetStringUTFChars (inMethodSignature, NULL);
	jmethodID method = env->GetMethodID (localObjectClass, methodNameUTF, methodSigUTF);
	
	if (env->ExceptionOccurred()) {
		char str[256];
		sprintf (str, "Can't find specified method:%s", methodNameUTF);
		JNIThrowCAException (env, str); 
		mCallObject = 0;
		mCallMethod = 0;
	} 
	else {
		mCallObject = env->NewGlobalRef(inJObject);
		mCallMethod = method;
	}

	env->ReleaseStringUTFChars (inMethodName, methodNameUTF);
	env-> ReleaseStringUTFChars (inMethodSignature, methodSigUTF);
}

void JNICallbackBase::Cleanup(JNIEnv* env)
{
	if (mCallObject != 0) {
		env->DeleteGlobalRef(mCallObject);
		mCallObject = 0;
	}
}

jint JNICallbackBase::CallMethod (int arg, ... )
{
	va_list args = (va_list)&arg;

	if (mThreadID != pthread_self()) {
		sJavaVM->AttachCurrentThread ((void**)&mJEnv, NULL);
		mThreadID = pthread_self();
	}
	
	return mJEnv->CallIntMethodV (mCallObject, mCallMethod, args);
}

void JNICallbackBase::Initialize (JNIEnv* inEnv) 
{
	if (sJavaVM == NULL) {
		inEnv->GetJavaVM (&sJavaVM);
	}
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#pragma mark ____JNI_Interface

JNIEXPORT jint JNICALL
Java_com_apple_audio_jdirect_MethodClosure_JNewMethodClosure
									(JNIEnv* inEnv, 
									jclass javaClass, 
									jobject inJObject, 
									jstring inMethodName, 
									jstring inMethodSignature)
{
	JNICallbackBase* obj = new JNICallbackBase (inEnv, inJObject, inMethodName, inMethodSignature);
		// this means there was a problem in the java code
		// (wrong method sig, etc...)
		// when it returns to Java it will throw an exception
	if (obj->IsNotValid()) {
		delete obj;
		return 0;
	}
	return jint(obj);
}

JNIEXPORT void JNICALL
Java_com_apple_audio_jdirect_MethodClosure_JDisposeMethodClosure(JNIEnv* env, jclass javaClass, void* closure)
{
	((JNICallbackBase*)closure)->Cleanup (env);
	delete (JNICallbackBase*)closure;
}

