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
 *  JNI_NativeUtilities.cpp
 *  CAJava
 *
 *  Created by Bill Stewart on Thu Jun 26 2003.
 *  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
 *
 */


// OFFSETs here are ALL byte offsets...

#include "com_apple_audio_jdirect_Accessor.h"

JNIEXPORT jbyte JNICALL Java_com_apple_audio_jdirect_Accessor_getByteFromHandle
  (JNIEnv *, jclass, jint handle, jint offset)
{
	jbyte** h = (jbyte**)handle;
	jbyte* p = *h;
	p += offset;
	return *p;
}

/*
 * Class:     com_apple_audio_jdirect_Accessor
 * Method:    getByteFromPointer
 * Signature: (II)B
 */
JNIEXPORT jbyte JNICALL Java_com_apple_audio_jdirect_Accessor_getByteFromPointer
  (JNIEnv *, jclass, jint pointer, jint offset)
{
	char* p = (char*)pointer;
	p += offset;
	return * (jbyte*)p;
}

/*
 * Class:     com_apple_audio_jdirect_Accessor
 * Method:    getCharFromPointer
 * Signature: (II)C
 */
JNIEXPORT jchar JNICALL Java_com_apple_audio_jdirect_Accessor_getCharFromPointer
  (JNIEnv *, jclass, jint pointer, jint offset)
{
	char* p = (char*)pointer;
	p += offset;
	return * (jchar*)p;
}

/*
 * Class:     com_apple_audio_jdirect_Accessor
 * Method:    getShortFromPointer
 * Signature: (II)S
 */
JNIEXPORT jshort JNICALL Java_com_apple_audio_jdirect_Accessor_getShortFromPointer
  (JNIEnv *, jclass, jint pointer, jint offset)
{
	char* p = (char*)pointer;
	p += offset;
	return * (jshort*)p;
}

/*
 * Class:     com_apple_audio_jdirect_Accessor
 * Method:    getIntFromPointer
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_jdirect_Accessor_getIntFromPointer
  (JNIEnv *, jclass, jint pointer, jint offset)
{
	char* p = (char*)pointer;
	p += offset;
	return * (jint*)p;
}

/*
 * Class:     com_apple_audio_jdirect_Accessor
 * Method:    getLongFromPointer
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_com_apple_audio_jdirect_Accessor_getLongFromPointer
  (JNIEnv *, jclass, jint pointer, jint offset)
{
	char* p = (char*)pointer;
	p += offset;
	return * (jlong*)p;
}

/*
 * Class:     com_apple_audio_jdirect_Accessor
 * Method:    getFloatFromPointer
 * Signature: (II)F
 */
JNIEXPORT jfloat JNICALL Java_com_apple_audio_jdirect_Accessor_getFloatFromPointer
  (JNIEnv *, jclass, jint pointer, jint offset)
{
	char* p = (char*)pointer;
	p += offset;
	return * (jfloat*)p;
}

/*
 * Class:     com_apple_audio_jdirect_Accessor
 * Method:    getDoubleFromPointer
 * Signature: (II)D
 */
JNIEXPORT jdouble JNICALL Java_com_apple_audio_jdirect_Accessor_getDoubleFromPointer
  (JNIEnv *, jclass, jint pointer, jint offset)
{
	char* p = (char*)pointer;
	p += offset;
	return * (jdouble*)p;
}

/*
 * Class:     com_apple_audio_jdirect_Accessor
 * Method:    setByteInPointer
 * Signature: (IIB)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_Accessor_setByteInPointer
  (JNIEnv *, jclass, jint pointer, jint offset, jbyte value)
{
	char* p = (char*)pointer;
	p += offset;
	* (jbyte*)p = value;
}

/*
 * Class:     com_apple_audio_jdirect_Accessor
 * Method:    setCharInPointer
 * Signature: (IIC)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_Accessor_setCharInPointer
  (JNIEnv *, jclass, jint pointer, jint offset, jchar value)
{
	char* p = (char*)pointer;
	p += offset;
	* (jchar*)p = value;
}

/*
 * Class:     com_apple_audio_jdirect_Accessor
 * Method:    setShortInPointer
 * Signature: (IIS)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_Accessor_setShortInPointer
  (JNIEnv *, jclass, jint pointer, jint offset, jshort value)
{
	char* p = (char*)pointer;
	p += offset;
	* (jshort*)p = value;
}

/*
 * Class:     com_apple_audio_jdirect_Accessor
 * Method:    setIntInPointer
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_Accessor_setIntInPointer
  (JNIEnv *, jclass, jint pointer, jint offset, jint value)
{
	char* p = (char*)pointer;
	p += offset;
	* (jint*)p = value;
}

/*
 * Class:     com_apple_audio_jdirect_Accessor
 * Method:    setLongInPointer
 * Signature: (IIJ)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_Accessor_setLongInPointer
  (JNIEnv *, jclass, jint pointer, jint offset, jlong value)
{
	char* p = (char*)pointer;
	p += offset;
	* (jlong*)p = value;
}

/*
 * Class:     com_apple_audio_jdirect_Accessor
 * Method:    setFloatInPointer
 * Signature: (IIF)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_Accessor_setFloatInPointer
  (JNIEnv *, jclass, jint pointer, jint offset, jfloat value)
{
	char* p = (char*)pointer;
	p += offset;
	* (jfloat*)p = value;
}

/*
 * Class:     com_apple_audio_jdirect_Accessor
 * Method:    setDoubleInPointer
 * Signature: (IID)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_Accessor_setDoubleInPointer
  (JNIEnv *, jclass, jint pointer, jint offset, jdouble value)
{
	char* p = (char*)pointer;
	p += offset;
	* (jdouble*)p = value;
}


#include "com_apple_audio_jdirect_ArrayCopy.h"
#include <strings.h>
/*
 * Class:     com_apple_audio_jdirect_ArrayCopy
 * Method:    copyArrayToPointer
 * Signature: ([BIIII)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_ArrayCopy_copyArrayToPointer___3BIIII
  (JNIEnv *env, jclass, jbyteArray ar, jint srcOffset, jint pointer, jint destOffset, jint numBytes)
{
	jbyte* nativeArray = env->GetByteArrayElements (ar, NULL);
	char* src = (char*)nativeArray;
	src += srcOffset;
	char* dest = (char*)pointer;
	dest += destOffset;
	memcpy (dest, src, numBytes);
	env->ReleaseByteArrayElements (ar, nativeArray, 0);
}

/*
 * Class:     com_apple_audio_jdirect_ArrayCopy
 * Method:    copyArrayToPointer
 * Signature: ([CIIII)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_ArrayCopy_copyArrayToPointer___3CIIII
  (JNIEnv *env, jclass, jcharArray ar, jint srcOffset, jint pointer, jint destOffset, jint numBytes)
{
	jchar* nativeArray = env->GetCharArrayElements (ar, NULL);
	char* src = (char*)nativeArray;
	src += srcOffset;
	char* dest = (char*)pointer;
	dest += destOffset;
	memcpy (dest, src, numBytes);
	env->ReleaseCharArrayElements (ar, nativeArray, 0);
}

/*
 * Class:     com_apple_audio_jdirect_ArrayCopy
 * Method:    copyArrayToPointer
 * Signature: ([SIIII)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_ArrayCopy_copyArrayToPointer___3SIIII
  (JNIEnv *env, jclass, jshortArray ar, jint srcOffset, jint pointer, jint destOffset, jint numBytes)
{
	jshort* nativeArray = env->GetShortArrayElements (ar, NULL);
	char* src = (char*)nativeArray;
	src += srcOffset;
	char* dest = (char*)pointer;
	dest += destOffset;
	memcpy (dest, src, numBytes);
	env->ReleaseShortArrayElements (ar, nativeArray, 0);
}

/*
 * Class:     com_apple_audio_jdirect_ArrayCopy
 * Method:    copyArrayToPointer
 * Signature: ([IIIII)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_ArrayCopy_copyArrayToPointer___3IIIII
  (JNIEnv *env, jclass, jintArray ar, jint srcOffset, jint pointer, jint destOffset, jint numBytes)
{
	jint* nativeArray = env->GetIntArrayElements (ar, NULL);
	char* src = (char*)nativeArray;
	src += srcOffset;
	char* dest = (char*)pointer;
	dest += destOffset;
	memcpy (dest, src, numBytes);
	env->ReleaseIntArrayElements (ar, nativeArray, 0);
}

/*
 * Class:     com_apple_audio_jdirect_ArrayCopy
 * Method:    copyArrayToPointer
 * Signature: ([JIIII)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_ArrayCopy_copyArrayToPointer___3JIIII
  (JNIEnv *env, jclass, jlongArray ar, jint srcOffset, jint pointer, jint destOffset, jint numBytes)
{
	jlong* nativeArray = env->GetLongArrayElements (ar, NULL);
	char* src = (char*)nativeArray;
	src += srcOffset;
	char* dest = (char*)pointer;
	dest += destOffset;
	memcpy (dest, src, numBytes);
	env->ReleaseLongArrayElements (ar, nativeArray, 0);
}

/*
 * Class:     com_apple_audio_jdirect_ArrayCopy
 * Method:    copyArrayToPointer
 * Signature: ([FIIII)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_ArrayCopy_copyArrayToPointer___3FIIII
  (JNIEnv *env, jclass, jfloatArray ar, jint srcOffset, jint pointer, jint destOffset, jint numBytes)
{
	jfloat* nativeArray = env->GetFloatArrayElements (ar, NULL);
	char* src = (char*)nativeArray;
	src += srcOffset;
	char* dest = (char*)pointer;
	dest += destOffset;
	memcpy (dest, src, numBytes);
	env->ReleaseFloatArrayElements (ar, nativeArray, 0);
}

/*
 * Class:     com_apple_audio_jdirect_ArrayCopy
 * Method:    copyArrayToPointer
 * Signature: ([DIIII)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_ArrayCopy_copyArrayToPointer___3DIIII
  (JNIEnv *env, jclass, jdoubleArray ar, jint srcOffset, jint pointer, jint destOffset, jint numBytes)
{
	jdouble* nativeArray = env->GetDoubleArrayElements (ar, NULL);
	char* src = (char*)nativeArray;
	src += srcOffset;
	char* dest = (char*)pointer;
	dest += destOffset;
	memcpy (dest, src, numBytes);
	env->ReleaseDoubleArrayElements (ar, nativeArray, 0);
}

/*
 * Class:     com_apple_audio_jdirect_ArrayCopy
 * Method:    copyPointerToArray
 * Signature: (II[BII)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_ArrayCopy_copyPointerToArray__II_3BII
  (JNIEnv *env, jclass, jint pointer, jint srcOffset, jbyteArray ar, jint destOffset, jint numBytes)
{
	jbyte* nativeArray = env->GetByteArrayElements (ar, NULL);
	char* src = (char*)pointer;
	src += srcOffset;
	char* dest = (char*)nativeArray;
	dest += destOffset;
	memcpy (dest, src, numBytes);
	env->ReleaseByteArrayElements (ar, nativeArray, 0);
}

/*
 * Class:     com_apple_audio_jdirect_ArrayCopy
 * Method:    copyPointerToArray
 * Signature: (II[CII)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_ArrayCopy_copyPointerToArray__II_3CII
  (JNIEnv *env, jclass, jint pointer, jint srcOffset, jcharArray ar, jint destOffset, jint numBytes)
{
	jchar* nativeArray = env->GetCharArrayElements (ar, NULL);
	char* src = (char*)pointer;
	src += srcOffset;
	char* dest = (char*)nativeArray;
	dest += destOffset;
	memcpy (dest, src, numBytes);
	env->ReleaseCharArrayElements (ar, nativeArray, 0);
}

/*
 * Class:     com_apple_audio_jdirect_ArrayCopy
 * Method:    copyPointerToArray
 * Signature: (II[SII)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_ArrayCopy_copyPointerToArray__II_3SII
  (JNIEnv *env, jclass, jint pointer, jint srcOffset, jshortArray ar, jint destOffset, jint numBytes)
{
	jshort* nativeArray = env->GetShortArrayElements (ar, NULL);
	char* src = (char*)pointer;
	src += srcOffset;
	char* dest = (char*)nativeArray;
	dest += destOffset;
	memcpy (dest, src, numBytes);
	env->ReleaseShortArrayElements (ar, nativeArray, 0);
}

/*
 * Class:     com_apple_audio_jdirect_ArrayCopy
 * Method:    copyPointerToArray
 * Signature: (II[III)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_ArrayCopy_copyPointerToArray__II_3III
  (JNIEnv *env, jclass, jint pointer, jint srcOffset, jintArray ar, jint destOffset, jint numBytes)
{
	jint* nativeArray = env->GetIntArrayElements (ar, NULL);
	char* src = (char*)pointer;
	src += srcOffset;
	char* dest = (char*)nativeArray;
	dest += destOffset;
	memcpy (dest, src, numBytes);
	env->ReleaseIntArrayElements (ar, nativeArray, 0);
}

/*
 * Class:     com_apple_audio_jdirect_ArrayCopy
 * Method:    copyPointerToArray
 * Signature: (II[JII)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_ArrayCopy_copyPointerToArray__II_3JII
  (JNIEnv *env, jclass,jint pointer, jint srcOffset, jlongArray ar, jint destOffset, jint numBytes)
{
	jlong* nativeArray = env->GetLongArrayElements (ar, NULL);
	char* src = (char*)pointer;
	src += srcOffset;
	char* dest = (char*)nativeArray;
	dest += destOffset;
	memcpy (dest, src, numBytes);
	env->ReleaseLongArrayElements (ar, nativeArray, 0);
}
/*
 * Class:     com_apple_audio_jdirect_ArrayCopy
 * Method:    copyPointerToArray
 * Signature: (II[FII)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_ArrayCopy_copyPointerToArray__II_3FII
  (JNIEnv *env, jclass, jint pointer, jint srcOffset, jfloatArray ar, jint destOffset, jint numBytes)
{
	jfloat* nativeArray = env->GetFloatArrayElements (ar, NULL);
	char* src = (char*)pointer;
	src += srcOffset;
	char* dest = (char*)nativeArray;
	dest += destOffset;
	memcpy (dest, src, numBytes);
	env->ReleaseFloatArrayElements (ar, nativeArray, 0);
}

/*
 * Class:     com_apple_audio_jdirect_ArrayCopy
 * Method:    copyPointerToArray
 * Signature: (II[DII)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_jdirect_ArrayCopy_copyPointerToArray__II_3DII
  (JNIEnv *env, jclass, jint pointer, jint srcOffset, jdoubleArray ar, jint destOffset, jint numBytes)
{
	jdouble* nativeArray = env->GetDoubleArrayElements (ar, NULL);
	char* src = (char*)pointer;
	src += srcOffset;
	char* dest = (char*)nativeArray;
	dest += destOffset;
	memcpy (dest, src, numBytes);
	env->ReleaseDoubleArrayElements (ar, nativeArray, 0);
}
