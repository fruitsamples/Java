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
//
//  AUConnectionInfo.java
//  CoreAudio.proj
//
//  Copyright (c) 2001 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.toolbox;

import com.apple.audio.CAException;
/**
 * This class contains information about a connection that exists in an AUGraph.
 */
public class AUConnectionInfo {
//_________________________ STATIC METHODS
	/**
	 * This will establish a connection on the given graph between the source node's output bus
	 * to the destination node's input bus.
 	 * <BR><BR><b>CoreAudio::AUGraphConnectNodeInput</b><BR><BR>
	 * @param inOwnerGraph the graph that the connection will be made on
 	 * @param inSourceNode the source node
 	 * @param inSourceOutputNumber the source node's output bus number
 	 * @param inDestNode the destination node
 	 * @param inDestInputNumber the destinations node's input bus numer
 	 * @see boolean update(boolean)
	 */
	public AUConnectionInfo (AUGraph inOwnerGraph, AUNode inSourceNode, int inSourceOutputNumber, AUNode inDestNode, int inDestInputNumber) throws CAException {
		inOwnerGraph.connectNodeInput (inSourceNode, inSourceOutputNumber, inDestNode, inDestInputNumber);
		this.sourceNode = inSourceNode;
		this.sourceOutputNumber = inSourceOutputNumber;
		this.destNode = inDestNode;
		this.destInputNumber = inDestInputNumber;
		this.ownerGraph = inOwnerGraph;
	}
	
	AUConnectionInfo (AUNode srcNode, int srcOut, AUNode dstNode, int dstIn, AUGraph owner) {
		sourceNode = srcNode;
		sourceOutputNumber = srcOut;
		destNode = dstNode;
		destInputNumber = dstIn;
		ownerGraph = owner;
	}
	
	/** The Source node of this connection */
	public final AUNode sourceNode;
	/** The source node's output bus*/
	public final int sourceOutputNumber;
	/** The destination node */
	public final AUNode destNode;
	/** The destination node's input bus */
	public final int destInputNumber;
	/** The AUGraph upon which the connection has been made*/
	public final AUGraph ownerGraph;
}

/*
 */
