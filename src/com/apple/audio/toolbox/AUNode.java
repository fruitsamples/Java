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
//
//  AUNode.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.toolbox;

import com.apple.audio.*;
import java.util.Vector;

/**
 * This class represents a node identifier when added to a AUGraph
 */
public final class AUNode extends CAObject {
//_________________________ STATIC METHODS
	private AUNode (int nodeID) {
		super (nodeID, vec);
	}
	
	private static Vector vec = new Vector(); //saves creating new nodes all the time...
					
	static synchronized AUNode getNode (int nodeID) {
 		if (vec.isEmpty()) {
			AUNode node = new AUNode (nodeID);
			vec.addElement (node);	//adds to end
			return node;
		}
		int numElements = vec.size();
		for (int i = 0; i < numElements; ++i) {
			int elNode = nodeAt (i);
			if (nodeID == elNode)
				return (AUNode)vec.elementAt(i);
			if (nodeID < elNode) {
				AUNode node = new AUNode (nodeID);
				vec.insertElementAt (node, i);
				return node;
			}
		}
			// add it at the end
		AUNode node = new AUNode (nodeID);
		vec.addElement (node); //adds to end
		return node;
	} 

	private static int nodeAt (int index) {
		Object node = vec.elementAt(index);
		return CAObject.ID ((AUNode)node);
	}

//_________________________ INSTANCE METHODS
	public int getNodeID () {
		return CAObject.ID(this);
	}
}

/*
 */