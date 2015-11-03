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
#include <CoreFoundation/CoreFoundation.h>

#include <stdlib.h>
#include <string.h>

#include "com_apple_component_CompInfoSupport.h"
#include "com_apple_component_Component.h"
#include "com_apple_component_ComponentDescription.h"
#include "com_apple_audio_util_CAMemoryObject.h"
#include "com_apple_audio_util_CAFData.h"
#include "com_apple_audio_util_CAFString.h"
#include "JNI_Custom.h"

/*
 * Class:     com_apple_component_CompInfoSupport
 * Method:    GetHandleSize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_component_CompInfoSupport_GetHandleSize
  (JNIEnv *, jclass, jint handle)
{
	return GetHandleSize ((Handle)handle);
}

/*
 * Class:     com_apple_component_CompInfoSupport
 * Method:    NewHandle
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_component_CompInfoSupport_NewHandle
  (JNIEnv *, jclass, jint size)
{
	return (jint)NewHandle (size);
}

/*
 * Class:     com_apple_component_CompInfoSupport
 * Method:    DisposeHandle
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_apple_component_CompInfoSupport_DisposeHandle
  (JNIEnv *, jclass, jint handle)
{
	DisposeHandle ((Handle)handle);
}

/* Inaccessible static: class_00024com_00024apple_00024component_00024ComponentDescription */
/*
 * Class:     com_apple_component_ComponentDescription
 * Method:    CountComponents
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_component_ComponentDescription_CountComponents
  (JNIEnv *, jclass, jint componentDesPtr)
{
	return (jint)CountComponents ((ComponentDescription*)componentDesPtr);
}

/*
 * Class:     com_apple_component_ComponentDescription
 * Method:    FindNextComponent
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_apple_component_ComponentDescription_FindNextComponent
  (JNIEnv *, jclass, jint component, jint componentDescPtr)
{
	return (jint)FindNextComponent ((Component)component, (ComponentDescription*)componentDescPtr);
}

/*
 * Class:     com_apple_component_Component
 * Method:    FindNextComponent
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_apple_component_Component_FindNextComponent
  (JNIEnv *, jclass, jint component, jint componentDescPtr)
{
	return (jint)FindNextComponent ((Component)component, (ComponentDescription*)componentDescPtr);
}

/*
 * Class:     com_apple_component_Component
 * Method:    CountComponentInstances
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_component_Component_CountComponentInstances
  (JNIEnv *, jclass, jint component)
{
	return (jint)CountComponentInstances ((Component)component);
}

/*
 * Class:     com_apple_component_Component
 * Method:    GetComponentInfo
 * Signature: (IIIII)S
 */
JNIEXPORT jshort JNICALL Java_com_apple_component_Component_GetComponentInfo
  (JNIEnv *, jclass, jint comp, jint compDescPtr, jint compName, jint compInfo, jint compIcon)
{
	return (jshort)GetComponentInfo ((Component)comp,
							(ComponentDescription*)compDescPtr,
							(Handle)compName,
							(Handle)compInfo,
							(Handle)compIcon);
}

extern OSErr 
SetDefaultComponent(
  Component   aComponent,
  short       flags);

/*
 * Class:     com_apple_component_Component
 * Method:    SetDefaultComponent
 * Signature: (IS)S
 */
JNIEXPORT jshort JNICALL Java_com_apple_component_Component_SetDefaultComponent
  (JNIEnv *, jclass, jint comp, jshort flags)
{
	return (jshort)SetDefaultComponent ((Component)comp, (short)flags);
}

/* Inaccessible static: linkage */
/* Inaccessible static: class_00024com_00024apple_00024audio_00024util_00024CAMemoryObject */
/*
 * Class:     com_apple_audio_util_CAMemoryObject
 * Method:    malloc
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_util_CAMemoryObject_malloc
  (JNIEnv *, jclass, jint numBytes)
{
	return (jint)malloc (numBytes);
}

/*
 * Class:     com_apple_audio_util_CAMemoryObject
 * Method:    mallocClear
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_util_CAMemoryObject_mallocClear
  (JNIEnv *, jclass, jint numBytes)
{
	void* ptr = malloc (numBytes);
	return (jint)memset ((void*)ptr, 0, numBytes);
}

/*
 * Class:     com_apple_audio_util_CAMemoryObject
 * Method:    memcpy
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_util_CAMemoryObject_memcpy
  (JNIEnv *, jclass, jint destPtr, jint srcPtr, jint numBytes)
{
	return (jint)memcpy ((void*)destPtr, (void*)srcPtr, numBytes);
}


/*
 * Class:     com_apple_audio_units_AudioUnit
 * Method:    GetComponentVersion
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_units_AudioUnit_GetComponentVersion
  (JNIEnv *, jclass, jint comp) 
{
	return (jint)GetComponentVersion ((ComponentInstance)comp);
}

/*
 * Class:     com_apple_audio_units_AUComponent
 * Method:    OpenDefaultComponent
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_units_AUComponent_OpenDefaultComponent
  (JNIEnv *, jclass, jint ostypeType, jint ostypeSubType)
{
	return (jint)OpenDefaultComponent (ostypeType, ostypeSubType);
}

/*
 * Class:     com_apple_audio_units_AUComponent
 * Method:    OpenComponent
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_units_AUComponent_OpenComponent
  (JNIEnv *, jclass, jint comp) 
{
	return (jint)OpenComponent((Component)comp);
}

/*
 * Class:     com_apple_audio_units_AUComponent
 * Method:    OpenAComponent
 * Signature: (II)S
 */
JNIEXPORT jshort JNICALL Java_com_apple_audio_units_AUComponent_OpenAComponent
  (JNIEnv *, jclass, jint comp, jint compInstPtr) 
{
	return (jshort)OpenAComponent((Component)comp, (ComponentInstance*)compInstPtr);
}

/*
 * Class:     com_apple_audio_units_AUComponent
 * Method:    GetComponentInfo
 * Signature: (IIIII)S
 */
JNIEXPORT jshort JNICALL Java_com_apple_audio_units_AUComponent_GetComponentInfo
  (JNIEnv *, jclass, jint comp, jint compDescPtr, jint compName, jint compInfo, jint compIcon)
{
	return (jshort)GetComponentInfo ((Component)comp,
							(ComponentDescription*)compDescPtr,
							(Handle)compName,
							(Handle)compInfo,
							(Handle)compIcon);
}
  
/*
 * Class:     com_apple_audio_CAObjectManagement
 * Method:    free
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_CAObjectManagement_free
  (JNIEnv *, jclass, jint ptr) 
{
	free ((void*)ptr);
}
  
/*
 * Class:     com_apple_audio_CAObjectManagement
 * Method:    CloseComponent
 * Signature: (I)S
 */
JNIEXPORT jshort JNICALL Java_com_apple_audio_CAObjectManagement_CloseComponent
  (JNIEnv *, jclass, jint compInst) 
{
	return (jshort)CloseComponent((ComponentInstance)compInst);
}

/*
 * Class:     com_apple_audio_CAObjectManagement
 * Method:    CFRelease
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_CAObjectManagement_CFRelease
  (JNIEnv *, jclass, jint cfObj)
{
	CFRelease ((CFTypeRef)cfObj);
}

/*
 * Class:     com_apple_audio_util_CAFString
 * Method:    CFStringCreateWithCharacters
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_util_CAFString_CFStringCreateWithCharacters
  (JNIEnv *, jclass, jint alloc, jint chars, jint numChars)
{
	return (jint)CFStringCreateWithCharacters((CFAllocatorRef)alloc, 
								(const UniChar *)chars, 
								(CFIndex)numChars);
}

/*
 * Class:     com_apple_audio_util_CAFString
 * Method:    CFStringGetCharacters
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_util_CAFString_CFStringGetCharacters
  (JNIEnv *, jclass, jint theString, jint range, jint buffer)
{
	CFStringGetCharacters((CFStringRef)theString, *(CFRange*)range, (UniChar *)buffer);
}

/*
 * Class:     com_apple_audio_util_CAFString
 * Method:    CFStringGetLength
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_util_CAFString_CFStringGetLength
  (JNIEnv *, jclass, jint cfString)
{
	return (jint)CFStringGetLength ((CFStringRef)cfString);
}

/*
 * Class:     com_apple_audio_util_CAFData
 * Method:    CFDataCreate
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_util_CAFData_CFDataCreate
  (JNIEnv *, jclass, jint allocRef, jint bytes, jint length)
{
	return (jint)CFDataCreate ((CFAllocatorRef)allocRef, (const UInt8*)bytes, (CFIndex)length);
}

/*
 * Class:     com_apple_audio_util_CAFData
 * Method:    CFDataGetLength
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_apple_audio_util_CAFData_CFDataGetLength
  (JNIEnv *, jclass, jint theData)
{
	return (jint)CFDataGetLength((CFDataRef)theData);
}

/*
 * Class:     com_apple_audio_util_CAFData
 * Method:    CFDataGetBytes
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_com_apple_audio_util_CAFData_CFDataGetBytes
  (JNIEnv *, jclass, jint theData, jint range, jint buffer)
{
	CFDataGetBytes((CFDataRef)theData, *(CFRange*)range, (UInt8 *)buffer); 
}
