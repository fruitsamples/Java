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
//  MIDIEndpoint.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Jon Summers
//
package com.apple.audio.midi;

import com.apple.audio.*;
import com.apple.audio.jdirect.*;

/**
 * Implements the MIDIEndpoint as defined in MIDIServices.h
 */
// typedef struct OpaqueMIDIEndpoint*        MIDIEndpointRef;
public final class MIDIEndpoint extends MIDIObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}
//_________________________ CLASS METHODS
	/**
	* Clients may use MIDIFlushOutput to cancel the sending of packets that were previously scheduled for future delivery
	* to all MIDIEndpoints.
	* <P>
	* New for CoreMIDI 1.1.
     * <BR><BR><b>CoreAudio::MIDIFlushOutput()</b><BR><BR>
	*/
	public static void flushOutputs () throws CAException {
		int res = MIDIFlushOutput (0);
		CAException.checkError (res);
	}

   /**
     * - package scoped - constructor encapsulates a native CoreAudio MIDIEndpoint
     * @param ptr The native MIDIEndpointRef
     * @param readDispatcher The MIDIDispatcher from new MIDIDispatcher(MIDIReadProc) or null
     * @param owner The CAObject ref. If null, CASession.remove calls MIDIEndpointDispose()  
     */
	MIDIEndpoint (int ptr, Object owner) {
		super (ptr, owner);
	}
	
    /**
     * - package scoped - constructor encapsulates a native CoreAudio MIDIEndpoint
     * @param ptr The native MIDIEndpointRef
     * @param readDispatcher The MIDIDispatcher from new MIDIDispatcher(MIDIReadProc) or null
     * @param owner The CAObject ref. If null, CASession.remove calls MIDIEndpointDispose()  
     */
	MIDIEndpoint (int ptr, MIDIDispatcher readDispatcher, MIDIClient owner) {
		super (ptr, null);
		this.readHandler = readDispatcher;
		this.client = owner;
	}
	
//_________________________ INSTANCE FIELDS
	private MIDIDispatcher  readHandler = null;	// dont gc the method closure
	private MIDIClient client;
	
//_________________________ INSTANCE METHODS
	/*
	 * Distribute MIDI from a source to the client input ports which are connected to that source.
	 * Virtual source endpoints created using client.sourceCreate() should call received() when the source is generating MIDI
     * <BR><BR><b>CoreAudio::MIDIReceived()</b><BR><BR>
	 * @param list The MIDI events to be transmitted
	 * @exception on MIDIReceived error
	 */
	public void received (MIDIPacketList list) throws CAException {
		int  res = MIDIReceived (_ID(), CAObject.ID (list));
		CAException.checkError (res);
	}
		
	/**
	* Clients may use MIDIFlushOutput to cancel the sending of packets that were previously scheduled for future delivery.
	* All pending events scheduled to be sent to this destination are unscheduled.
	* <P>
	* New for CoreMIDI 1.1.
     * <BR><BR><b>CoreAudio::MIDIFlushOutput()</b><BR><BR>
	*/
	public void flushOutput () throws CAException {
		int res = MIDIFlushOutput (_ID());
		CAException.checkError (res);
	}
	
	/**
	* Clients may use MIDIFlushOutput to cancel the sending of packets that were previously
	* scheduled for future delivery to all MIDIEndpoints. This will flush any output pending to 
	* any MIDIEndpoint - should use static call MIDIEndpoint.flushOutputs() instead.
	* <P>
	* New for CoreMIDI 1.1.
     * <BR><BR><b>CoreAudio::MIDIFlushOutput()</b><BR><BR>
	 * @deprecated see MIDIEndpoint.flushOutputs
	*/
	public void flushAllOutputs () throws CAException {
		int res = MIDIFlushOutput (0);
		CAException.checkError (res);
	}

	protected void preDispose () throws CAException {
		if (readHandler != null) {
			readHandler.cleanup();
			readHandler = null;
		}
		if (client != null)
			client.removeFromClientTable (this);	// this DOESN'T CALL BACK TO THE PORT
		client = null;
	}

	private static native int MIDIReceived (int src, int pktlist);
	private static native int MIDIFlushOutput(int dest);
}

/*
 */
