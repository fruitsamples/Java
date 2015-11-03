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
//  MIDIClient.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Jon Summers
//
package com.apple.audio.midi;

import com.apple.audio.util.*;
import com.apple.audio.*;
import com.apple.audio.jdirect.*;
import java.util.*;
/**
 * Implements the MIDIClient as defined in MIDIServices.h
 */
// typedef struct OpaqueMIDIClient*        MIDIClientRef;

// ISSUES
//	1. Cleanup endpoints with MIDIEndpointDispose

public final class MIDIClient extends MIDIObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}

//_________________________ CLASS FIELDS
	private static MIDIDispatcher  allocDispatcher;	// for alloc(), to set object.notifyHandler
	
	private static Hashtable clientTable = new Hashtable();
	
//_________________________ CLASS METHODS

    /**
     * Create a new MIDIClient. The application should maintain a reference to this object
     * for as long as it is using the ports and endpoints attached to this client.
     * <BR><BR><b>CoreAudio::MIDIClientCreate()</b><BR><BR>
	 * @param name The name of the client to be created
     * @param notifyProc An optional MIDINotifyProc to receive notifications of changes to the system.
     */
	public MIDIClient (CAFString name, MIDINotifyProc notifyProc) throws CAException {
		super (MIDIClient.alloc(name, notifyProc), null);
		notifyHandler = MIDIClient.allocDispatcher;
		if (notifyHandler != null)
			notifyHandler.setMClient (this);
		MIDIClient.allocDispatcher = null;	// release temporary reference
		clientTable.put (this, new Vector());
	}
	
    /**
     * allocator for constructor
	 * @param name The name of the client to be created
     * @param notifyProc the notification proc (may be null)
     */
	private static int alloc(CAFString name, MIDINotifyProc notifyProc) throws CAException {
		MIDIClient.allocDispatcher = (notifyProc == null) ? null : new MIDIDispatcher (notifyProc);
		CAMemoryObject outArg = new CAMemoryObject (4, false);
		int res = MIDIClientCreate (CAObject.ID (name), 
					(MIDIClient.allocDispatcher == null) ? 0 : MIDIClient.allocDispatcher.ID(), 
					0, 
					CAObject.ID (outArg));
		CAException.checkError (res);
		return outArg.getIntAt(0);
	}

	private static MIDIPort addToTable (MIDIClient theClient, MIDIPort port) {
		Vector vec = (Vector)clientTable.get (theClient);
		vec.addElement (port);
		return port;
	}

	private static MIDIEndpoint addToTable (MIDIClient theClient, MIDIEndpoint endpoint) {
		Vector vec = (Vector)clientTable.get (theClient);
		vec.addElement (endpoint);
		return endpoint;
	}

//_________________________ INSTANCE FIELDS
	private MIDIDispatcher  notifyHandler = null;	// stop garbage collection of upp
	private CAMemoryObject outArg = new CAMemoryObject (4, false);

//_________________________ INSTANCE METHODS	
    /**
     * Create an input MIDIPort through which incoming MIDI messages are received from any MIDI source
     * Use port.connectSource() to establish an input connection from any number of sources to your port
     * <BR><BR><b>CoreAudio::MIDIInputPortCreate()</b><BR><BR>
	 * @param name The name of the port to be created
     * @param readProc The read proc to be called with incoming MIDI from sources connected to this port
 	 * @return A newly created input MIDIPort
	 * @exception On error within MIDIInputPortCreate
    */
	public MIDIInputPort inputPortCreate (CAFString name, MIDIReadProc readProc) throws CAException {
		synchronized (outArg) {
			MIDIDispatcher readDispatcher = (readProc == null) ? null : new MIDIDispatcher (readProc);
			int res = MIDIInputPortCreate (_ID(), 
							CAObject.ID (name), 
							(readDispatcher == null) ? 0 : readDispatcher.ID(), 
							0, 
							CAObject.ID (outArg));
			CAException.checkError (res);
			MIDIInputPort port = new MIDIInputPort (outArg.getIntAt(0), readDispatcher, this);
			readDispatcher.setInputPort (port);
			return (MIDIInputPort)addToTable(this, port); // NOT IMPLEMENTED CASession remove = MIDIPortDispose
		}
	}
	
    /**
     * Create an output MIDIPort through which outgoing MIDI messages may be sent to any MIDI destination
     * <BR><BR><b>CoreAudio::MIDIOutputPortCreate()</b><BR><BR>
	 * @param name The name of the port to be created
 	 * @return A newly created output MIDIPort
	 * @exception On error within MIDIOutputPortCreate
     */
	public MIDIOutputPort outputPortCreate (CAFString name) throws CAException {
		synchronized (outArg) {
			int  res = MIDIOutputPortCreate (_ID(), CAObject.ID (name), CAObject.ID (outArg));
			CAException.checkError (res);
			return (MIDIOutputPort)addToTable (this, new MIDIOutputPort (outArg.getIntAt(0), this/*owner*/)); // NOT IMPLEMENTED CASession remove = MIDIPortDispose
		}
	}

	void removeFromClientTable (MIDIPort port) {
		if (clientTable.containsKey(this))
			((Vector)clientTable.get(this)).removeElement (port);
	}

	void removeFromClientTable (MIDIEndpoint endpoint) {
		if (clientTable.containsKey(this))
			((Vector)clientTable.get(this)).removeElement (endpoint);
	}
	
	/**
	 * This call will dispose the MIDIClient from the System.
     * <BR><BR><b>CoreAudio::MIDIClientDispose</b><BR><BR>
	 */
	public void dispose () throws CAException {
		Vector vec = (Vector)clientTable.get(this);
		if (vec != null) {
			while (vec.isEmpty() == false) {
				CAObject member = (CAObject)vec.elementAt(0);
				member.dispose(); //this removes the port from the vector!!! -> see removePort
			}
		}
		clientTable.remove (this);
		super.dispose();
	}
		
    /**
     * Create a virtual destination MIDIEndpoint
     * <BR><BR><b>CoreAudio::MIDIDestinationCreate()</b><BR><BR>
	 * @param name The name of the virtual destination to be created
     * @param readProc The read proc to be called when clients send MIDI to your virtual destination.
 	 * @return The created destination MIDIEndpoint
	 * @exception On error within MIDIDestinationCreate
     */
	public MIDIEndpoint destinationCreate (CAFString name, MIDIReadProc readProc) throws CAException {
		synchronized (outArg) {
			MIDIDispatcher readDispatcher = (readProc == null) ? null : new MIDIDispatcher (readProc);
			int res = MIDIDestinationCreate (_ID(), 
							CAObject.ID (name), 
							(readDispatcher == null) ? 0 : readDispatcher.ID(), 
							0, 
							CAObject.ID (outArg));
			CAException.checkError (res);
			MIDIEndpoint ep = new MIDIEndpoint (outArg.getIntAt(0), readDispatcher, this); 
			// CASession remove = MIDIEndpointDispose
			if (readDispatcher != null)
				readDispatcher.setVirtualDest (ep);
			return ep; // CASession remove = MIDIEndpointDispose
		}
	}
	
    /**
     * Create a virtual source MIDIEndpoint
     * Use endpoint.received() to transmit MIDI messages to any clients connected to the virtual source
     * <BR><BR><b>CoreAudio::MIDISourceCreate()</b><BR><BR>
	 * @param name The name of the port to be created
 	 * @return A newly created source MIDIEndpoint
	 * @exception On error within MIDISourceCreate
     */
	public MIDIEndpoint sourceCreate (CAFString name) throws CAException {
		synchronized (outArg) {
			int  res = MIDISourceCreate (_ID(), CAObject.ID (name), CAObject.ID (outArg));
			CAException.checkError (res);
			return new MIDIEndpoint (outArg.getIntAt(0), null, this); // CASession remove = MIDIEndpointDispose
		}
	}

//_ NATIVE METHODS
	private static native int MIDIClientCreate (int name, int notifyProc, int notifyRefCon, int outClient);
	private static native int MIDIInputPortCreate (int client, int portName, int readProc, int refCon, int outPort);
	private static native int MIDIOutputPortCreate (int client, int portName, int outPort);
	private static native int MIDIDestinationCreate (int client, int name, int readProc, int refCon, int outDest);
	private static native int MIDISourceCreate (int client, int name, int outSrc);
}

/*
 */
