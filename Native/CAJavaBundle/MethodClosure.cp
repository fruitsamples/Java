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
	File:		MethodClosure.cp
	
	Contents:	Creates a transition vector that is a closure over some Java object and
				a method defined on it. Used by callbacks in Java.
	
	Authors:	Patrick C. Beard.
	
	<Revision History>
 */
 

#include "MethodClosure.h"

// This stores a global instance of the VM so that the callback procs
// can attach the thread they are called back from to the VM and get 
// the JNIEnv for that thread and call into java
static JavaVM* javaVM;

/**
 *	Represents a PowerPC/CFM68K function pointer, which always contains a raw ProcPtr
 *	and a value for RTOC/A5. Subclasses add extra data that is available when a function
 *	is called through the sub-class pointer. Nice way to implement closures.
 */

struct TransitionVector {
	int (*proc) (int r3, ...);
	int rtoc; //would only be needed for CFM not MachO
	
	TransitionVector(TransitionVector* tv) : proc(tv->proc), rtoc(tv->rtoc) {}
};

struct MethodClosure : TransitionVector {
	jobject object;
	jmethodID method;
	JNIEnv* env;
	int threadID;
	
	MethodClosure(TransitionVector* vector, jobject object, jmethodID method);
};

MethodClosure::MethodClosure(TransitionVector* vector, jobject object, jmethodID method)
	:	TransitionVector(vector)
{
	this->object = object;
	this->method = method;
	this->env = NULL;
	this->threadID = 0;
}

static /*asm*/ TransitionVector* getTransitionVector()
{
return NULL;
//	mr	r3,r12			// r12 points to the PowerPC ABI transition vector.
//	blr
}

static int GetCurrentThreadID () { return 1; }

static int CallMethodClosure(int arg, ...)
{
	MethodClosure* closure = (MethodClosure*)getTransitionVector();
	va_list args = (va_list)&arg;
	
	if (closure->threadID != GetCurrentThreadID()) {
		javaVM->AttachCurrentThread ((void**)&closure->env, (void*)NULL);
		closure->threadID = GetCurrentThreadID();
	}
	return closure->env->CallIntMethodV(closure->object, closure->method, args);
}



JNIEXPORT jint JNICALL
Java_com_apple_audio_jdirect_MethodClosure_NewMethodClosure(JNIEnv* env, jclass javaClass, jobject object, jstring methodName, jstring methodSignature)
{
	jclass objectClass = env->GetObjectClass (object);
	const char* methodNameUTF = env->GetStringUTFChars (methodName, NULL);
	const char* methodSigUTF = env->GetStringUTFChars (methodSignature, NULL);
	jmethodID method = env->GetMethodID (objectClass, methodNameUTF, methodSigUTF);
	if (!env->ExceptionOccurred()) {
		TransitionVector* vector = (TransitionVector*)&CallMethodClosure;
		MethodClosure* closure = new MethodClosure(vector, object, method);
		return int(closure);
	}
	return 0;
}

JNIEXPORT jint JNICALL
Java_com_apple_audio_jdirect_MethodClosure_DisposeMethodClosure(JNIEnv* env, jclass javaClass, void* closure)
{
	if (closure != NULL)
		delete (MethodClosure*)closure;
	return 0;
}

JNIEXPORT void JNICALL
Java_com_apple_audio_jdirect_MethodClosure_InitializeMethodClosure (JNIEnv *env, jclass cls)
{
	env->GetJavaVM (&javaVM);
}	

