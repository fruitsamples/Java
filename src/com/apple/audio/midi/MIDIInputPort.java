/*	Copyright: 	© Copyright 2003 Apple Computer, Inc. All rights reserved.

	Disclaimer:	IMPORTANT:  This Apple software is supplied to you by Apple Computer, Inc.
			("Apple") in consideration of your agreement to the following terms, and your
			use, installation, modification or redistribution of this Apple software
			constitutes acceptance of these terms.  If you do not agree with these terms,
			please do not use, install, modify or redistribute this Apple software.

			In consideration of your agreement to abide by the following terms, and subject
			to these terms, Apple grants you a personal, non-exclusive license, under AppleÕs
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
//  MIDIInputPort.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Jon Summers
//
package com.apple.audio.midi;

import com.apple.audio.*;
import com.apple.audio.jdirect.*;
import java.util.*;
/**
 * Implements the MIDIInputPort as defined in MIDIServices.h
 */
public final class MIDIInputPort extends MIDIPort {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}
	/*
 	 *	typedef struct OpaqueMIDIPort*        MIDIPortRef;
	 */
	
//_________________________ CLASS METHODS

    /**
	 * - package scoped- constructor encapsulates a native CoreAudio MIDIPortRef object
     * @param id native MIDIPortRef
     */
	MIDIInputPort (int id, MIDIDispatcher readDispatcher, MIDIClient client) {
		super (id, client);	// these guys are NEVER explicitly disposed on finalize -> always done from client in that case
		this.readHandler = readDispatcher;
	}
	
//_________________________ INSTANCE FIELDS
	private MIDIDispatcher readHandler = null;	// stop garbage collection of upp
	private Vector endPointVec = new Vector();
	
//_________________________ INSTANCE METHODS	
    MIDIEndpoint getEndPoint (int pointPtr) {
    	for (int i = 0; i < endPointVec.size(); i++) {
    		MIDIEndpoint pt = (MIDIEndpoint)endPointVec.elementAt (i);
    		if (CAObject.ID(pt) == pointPtr)
    			return pt;
    	}
    	return null;
    }
    		
    /**
     * Establish a connection from a source to a client's input port.
     * Note: input connections are automatically lost when the setup changes
     * (as signified by a callback to the client's MIDINotifyProc).
     * <BR><BR><b>CoreAudio::MIDIPortConnectSource()</b><BR><BR>
	 * @param source The source from which to create the connection
	 * @exception On error within MIDIPortConnectSource
    */
	public void connectSource (MIDIEndpoint source) throws CAException {
		int res = MIDIPortConnectSource (_ID(), CAObject.ID(source), CAObject.ID(source));
		CAException.checkError (res);
		endPointVec.addElement(source);
	}

    /**
     * Close a previously-established source-to-input port connection.
     * <BR><BR><b>CoreAudio::MIDIPortDisconnectSource()</b><BR><BR>
	 * @param source The source from which to close a connection
	 * @exception On error within MIDIPortDisconnectSource
    */
	public void disconnectSource (MIDIEndpoint source) throws CAException {
		int res = MIDIPortDisconnectSource (_ID(), CAObject.ID(source));
		CAException.checkError (res);
		endPointVec.removeElement(source);
	}

	protected void preDispose () throws CAException {
		super.preDispose();
		if (readHandler != null) {
			readHandler.cleanup();
			readHandler = null;
		}
		endPointVec.clear();
	}
	
//_ NATIVE METHODS
	private static native int MIDIPortConnectSource (int port, int source, int connRefCon);
	private static native int MIDIPortDisconnectSource (int port, int source);
}

/*
 */
