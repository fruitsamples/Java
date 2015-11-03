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
	File:		MachOMethodClosure.cp
	
	Contents:	Creates a transition vector that is a closure over some Java object and
				a method defined on it. Used by callbacks in Java.
	
	Authors:	Doug Wyatt, Patrick C. Beard.
	
	<Revision History>
 */
 

#include "MethodClosure.h"
#include "Tink.h"
#include <pthread.h>

#include <CoreAudio/CoreAudio.h>

#define DEBUG_CB_TIME_PRINT 0

// This stores a global instance of the VM so that the callback procs
// can attach the thread they are called back from to the VM and get 
// the JNIEnv for that thread and call into java
static JavaVM* javaVM = NULL;

typedef int (*MachOCallbackFunction)(int arg, ...);
static int CallMethodClosure(int arg, ...);

/*
	How this works:
	
	The C function pointer points to a Tink<MachOCallbackFunction>.
	This is simply a direct jump to CallMethodClosure.
	On arrival in CallMethodClosure, r12 will point to the Tink.
	
	(dsw, 9/2/01)
*/

struct MethodClosure : Tink<MachOCallbackFunction> {
	jobject object;
	jmethodID method;
	JNIEnv* env;
	int threadID;

	MethodClosure(jobject object, jmethodID method);
};

MethodClosure::MethodClosure(jobject object, jmethodID method) :
	Tink<MachOCallbackFunction>(CallMethodClosure)
{
	this->object = object;
	this->method = method;
	this->env = NULL;
	this->threadID = 0;
}



inline int GetCurrentThreadID () { return (int)pthread_self(); }

#define INLINE_ASM 1
#if !INLINE_ASM
static void *GetR12()
{
	asm( "mr r3, r12 ; blr" );
}
#endif

static int CallMethodClosure(int arg, ...)
{
	MethodClosure* closure;

#if TARGET_CPU_PPC
	#if INLINE_ASM
		// faster/simpler -- add workaround for 3365352
		// this gives the warning "volatile register variables don't work as you might wish"
		// but this was disassembled & verified on Panther 7B39 & looks correct.
		register volatile int foo asm("r12");
		asm ("mr %0,%1" : "=r" (closure), "=r" (foo));
	#else
		closure = (MethodClosure*)GetR12();
	#endif
#endif

	va_list args = (va_list)&arg;
		
	if (closure->threadID != GetCurrentThreadID()) {
		javaVM->AttachCurrentThread ((void**)&closure->env, NULL);
		closure->threadID = GetCurrentThreadID();
	}
	
#if DEBUG_CB_TIME_PRINT
	UInt64 start = AudioGetCurrentHostTime();
	int ret = closure->env->CallIntMethodV(closure->object, closure->method, args);
	UInt64 end = AudioGetCurrentHostTime();
	printf ("took=%d mics\n", (int)(AudioConvertHostTimeToNanos (end - start) / 1000));
	return ret;
#else
	return closure->env->CallIntMethodV(closure->object, closure->method, args);
#endif
}



JNIEXPORT jint JNICALL
Java_com_apple_audio_jdirect_MethodClosure_NewMethodClosure(JNIEnv* env, jclass javaClass, jobject localObject, jstring methodName, jstring methodSignature)
{
	jclass localObjectClass = env->GetObjectClass (localObject);
	const char* methodNameUTF = env->GetStringUTFChars (methodName, NULL);
	const char* methodSigUTF = env->GetStringUTFChars (methodSignature, NULL);
	jmethodID method = env->GetMethodID (localObjectClass, methodNameUTF, methodSigUTF);
	jint ret = 0;
	
	if (!env->ExceptionOccurred()) {
		MethodClosure* closure = new MethodClosure(env->NewGlobalRef(localObject), method);
		ret = jint(closure);
	}

	env->ReleaseStringUTFChars (methodName, methodNameUTF);
	env-> ReleaseStringUTFChars (methodSignature, methodSigUTF);
	
	return ret;
}

JNIEXPORT jint JNICALL
Java_com_apple_audio_jdirect_MethodClosure_DisposeMethodClosure(JNIEnv* env, jclass javaClass, void* closure)
{
	if (closure != NULL) {
		MethodClosure* clos = (MethodClosure*)closure;
		env->DeleteGlobalRef(clos->object);
		delete clos;
	}
	return 0;
}

JNIEXPORT void JNICALL
Java_com_apple_audio_jdirect_MethodClosure_InitializeMethodClosure (JNIEnv *env, jclass cls)
{
	env->GetJavaVM (&javaVM);
}	

