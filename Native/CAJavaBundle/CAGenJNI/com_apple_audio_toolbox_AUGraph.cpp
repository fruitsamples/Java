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
// DO NOT EDIT THIS FILE - it is machine-generated by javacglue.py
#include "com_apple_audio_toolbox_AUGraph.h"
JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_NewAUGraph
  (JNIEnv *, jclass, jint outGraph)
{
	return (jint)NewAUGraph((AUGraph *)outGraph);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphNewNode
  (JNIEnv *, jclass, jint inGraph, jint inDescription, jint inClassDataLength, jint inClassData, jint outNode)
{
	return (jint)AUGraphNewNode((AUGraph)inGraph, (ComponentDescription *)inDescription, (UInt32)inClassDataLength, (const void *)inClassData, (AUNode *)outNode);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphRemoveNode
  (JNIEnv *, jclass, jint inGraph, jint inNode)
{
	return (jint)AUGraphRemoveNode((AUGraph)inGraph, (AUNode)inNode);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphGetNodeCount
  (JNIEnv *, jclass, jint inGraph, jint outNumberOfNodes)
{
	return (jint)AUGraphGetNodeCount((AUGraph)inGraph, (UInt32 *)outNumberOfNodes);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphGetIndNode
  (JNIEnv *, jclass, jint inGraph, jint inIndex, jint outNode)
{
	return (jint)AUGraphGetIndNode((AUGraph)inGraph, (UInt32)inIndex, (AUNode *)outNode);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphClearConnections
  (JNIEnv *, jclass, jint inGraph)
{
	return (jint)AUGraphClearConnections((AUGraph)inGraph);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphGetNodeInfo
  (JNIEnv *, jclass, jint inGraph, jint inNode, jint outDescription, jint outClassDataLength, jint outClassData, jint outAudioUnit)
{
	return (jint)AUGraphGetNodeInfo((AUGraph)inGraph, (AUNode)inNode, (ComponentDescription *)outDescription, (UInt32 *)outClassDataLength, (void **)outClassData, (AudioUnit *)outAudioUnit);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphConnectNodeInput
  (JNIEnv *, jclass, jint inGraph, jint inSourceNode, jint inSourceOutputNumber, jint inDestNode, jint inDestInputNumber)
{
	return (jint)AUGraphConnectNodeInput((AUGraph)inGraph, (AUNode)inSourceNode, (UInt32)inSourceOutputNumber, (AUNode)inDestNode, (UInt32)inDestInputNumber);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphDisconnectNodeInput
  (JNIEnv *, jclass, jint inGraph, jint inDestNode, jint inDestInputNumber)
{
	return (jint)AUGraphDisconnectNodeInput((AUGraph)inGraph, (AUNode)inDestNode, (UInt32)inDestInputNumber);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphUpdate
  (JNIEnv *, jclass, jint inGraph, jint outIsUpdated)
{
	return (jint)AUGraphUpdate((AUGraph)inGraph, (Boolean *)outIsUpdated);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphOpen
  (JNIEnv *, jclass, jint inGraph)
{
	return (jint)AUGraphOpen((AUGraph)inGraph);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphClose
  (JNIEnv *, jclass, jint inGraph)
{
	return (jint)AUGraphClose((AUGraph)inGraph);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphInitialize
  (JNIEnv *, jclass, jint inGraph)
{
	return (jint)AUGraphInitialize((AUGraph)inGraph);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphUninitialize
  (JNIEnv *, jclass, jint inGraph)
{
	return (jint)AUGraphUninitialize((AUGraph)inGraph);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphStart
  (JNIEnv *, jclass, jint inGraph)
{
	return (jint)AUGraphStart((AUGraph)inGraph);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphStop
  (JNIEnv *, jclass, jint inGraph)
{
	return (jint)AUGraphStop((AUGraph)inGraph);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphIsOpen
  (JNIEnv *, jclass, jint inGraph, jint outIsOpen)
{
	return (jint)AUGraphIsOpen((AUGraph)inGraph, (Boolean *)outIsOpen);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphIsInitialized
  (JNIEnv *, jclass, jint inGraph, jint outIsInitialized)
{
	return (jint)AUGraphIsInitialized((AUGraph)inGraph, (Boolean *)outIsInitialized);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphIsRunning
  (JNIEnv *, jclass, jint inGraph, jint outIsRunning)
{
	return (jint)AUGraphIsRunning((AUGraph)inGraph, (Boolean *)outIsRunning);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphSetRenderNotification
  (JNIEnv *, jclass, jint inGraph, jint inCallback, jint inRefCon)
{
	return (jint)AUGraphSetRenderNotification((AUGraph)inGraph, (AudioUnitRenderCallback)inCallback, (void *)inRefCon);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphGetNumberOfConnections
  (JNIEnv *, jclass, jint inGraph, jint outNumberOfConnections)
{
	return (jint)AUGraphGetNumberOfConnections((AUGraph)inGraph, (UInt32 *)outNumberOfConnections);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphGetConnectionInfo
  (JNIEnv *, jclass, jint inGraph, jint inConnectionIndex, jint outSourceNode, jint outSourceOutputNumber, jint outDestNode, jint outDestInputNumber)
{
	return (jint)AUGraphGetConnectionInfo((AUGraph)inGraph, (UInt32)inConnectionIndex, (AUNode *)outSourceNode, (UInt32 *)outSourceOutputNumber, (AUNode *)outDestNode, (UInt32 *)outDestInputNumber);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphGetCPULoad
  (JNIEnv *, jclass, jint inGraph, jint outCPULoad)
{
	return (jint)AUGraphGetCPULoad((AUGraph)inGraph, (Float32 *)outCPULoad);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphNewNodeSubGraph
  (JNIEnv *, jclass, jint inGraph, jint outNode)
{
	return (jint)AUGraphNewNodeSubGraph((AUGraph)inGraph, (AUNode *)outNode);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphGetNodeInfoSubGraph
  (JNIEnv *, jclass, jint inGraph, jint inNode, jint outSubGraph)
{
	return (jint)AUGraphGetNodeInfoSubGraph((const AUGraph)inGraph, (const AUNode)inNode, (AUGraph *)outSubGraph);
}

JNIEXPORT jint JNICALL Java_com_apple_audio_toolbox_AUGraph_AUGraphIsNodeSubGraph
  (JNIEnv *, jclass, jint inGraph, jint inNode, jint outFlag)
{
	return (jint)AUGraphIsNodeSubGraph((const AUGraph)inGraph, (const AUNode)inNode, (Boolean *)outFlag);
}

