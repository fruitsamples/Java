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
//  MIDIDispatcher.java
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
 * Implements a Dispatcher for MIDI callbacks as defined in MIDIServices.h
 */
final class MIDIDispatcher {

//_________________________ CLASS METHODS
	/**
	 * Instantiate a MIDINotifyHandler method closure 
	 */
// typedef void
// (*MIDINotifyProc)(const MIDINotification *message, void *refCon);
	MIDIDispatcher (MIDINotifyProc proc) {
		midiMethodClosure = new MethodClosure (this, "notifyProc", "(II)V");
		notifyExecute = proc;
		cacheNotify = new MIDINotification (0, this);	
	}

	/**
	 * Instantiate a MIDIReadHandler method closure 
	 */
// typedef CALLBACK_API_C( void , MIDIReadProc )(const MIDIPacketList *pktlist, void *readProcRefCon, void *srcConnRefCon);
	MIDIDispatcher (MIDIReadProc proc) {
		midiMethodClosure = new MethodClosure (this, "readProc", "(III)V");
		readExecute = proc;	
		packetList = new MIDIPacketList (true);
	}

	/**
	 * Instantiate a MIDICompletionHandler method closure 
	 */
// typedef CALLBACK_API_C( void , MIDICompletionProc )(MIDISysexSendRequest *request);
	MIDIDispatcher (MIDISysexSendRequest request, MIDICompletionProc proc) {
		midiMethodClosure = new MethodClosure (this, "completionProc", "(I)V");
		completionExecute = proc;
		this.request = request;
	}

//_________________________ INSTANCE FIELDS
	private MethodClosure midiMethodClosure;

	private MIDINotifyProc  notifyExecute;
	private MIDIClient mClient;
	private MIDINotification cacheNotify;
	
	private MIDISysexSendRequest request;
	private MIDICompletionProc  completionExecute;

	private MIDIReadProc  	readExecute;
	private MIDIInputPort 	port;
	private MIDIPacketList packetList;
	
	private MIDIEndpoint 	virtualDestEP;
		
//_________________________ INSTANCE METHODS
	/**
	 * Callback function in the client, to receive messages from the MIDI server
 	 * <BR><BR><b>CoreAudio::MIDINotifyProc</b><BR><BR>
     * @param messageID The type of notification
     * @param msgData Additional information, depending on the type of notification
     * @param refCon The client's refCon passed to MIDIClientCreate
	 */
// typedef void
// (*MIDINotifyProc)(const MIDINotification *message, void *refCon);
	private void notifyProc (int messagePtr, int refCon) {
		cacheNotify._setNR (messagePtr, Accessor.getIntFromPointer (messagePtr, 4));
		notifyExecute.execute (mClient, cacheNotify);
	}

	/**
	 * Callback function through which a client receives MIDI messages
 	 * <BR><BR><b>CoreAudio::MIDIReadProc</b><BR><BR>
     * @param pktlist The incoming MIDI message(s)
     * @param readProcRefCon The refCon you passed to MIDIInputPortCreate or MIDIDestinationCreate
     * @param srcConnRefCon A refCon you passed to MIDIPortConnectSource, which identifies the source of the data
	 */
// typedef CALLBACK_API_C( void , MIDIReadProc )(const MIDIPacketList *pktlist, void *readProcRefCon, void *srcConnRefCon);
	private void readProc (int pktListPtr, int readProcRefCon, int srcConnRefCon) {
		MIDIEndpoint ep = null;
		try {
			packetList._setNR (pktListPtr);	//this calculates the size
			if (port != null)
				ep = port.getEndPoint(srcConnRefCon);
			else
				ep = virtualDestEP;
				
				// this shouldn't make one -> retrieves the one we know about already
			readExecute.execute (port, ep, packetList);

		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (port != null)
					port.disconnectSource(ep);
			} catch (CAException ee) {//we'd be really hosed...
			}
		}
	}

	/**
	 * Callback function to notify the client of the completion of a call to MIDISendSysex
 	 * <BR><BR><b>CoreAudio::MIDICompletionProc</b><BR><BR>
     * @param requestID The MIDISysexSendRequest which has completed, or been aborted
	 */
//	typedef CALLBACK_API_C( void , MIDICompletionProc )(MIDISysexSendRequest *request)
	private void completionProc (int requestID) {
		try {
			completionExecute.execute ( request );
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cleanup();
		}
	}
	
	void setInputPort (MIDIInputPort p) {
		port = p;
	}

	void setMClient (MIDIClient mc) {
		mClient = mc;
	}
	
	void setVirtualDest (MIDIEndpoint ep) {
		port = null;
		virtualDestEP = ep;
	}
	
	/**
	 * Get the MethodClosure (UPP)
	 */
	int ID () {			
		return midiMethodClosure.getClosure ();
	}
		
	/**
	 * Release the MethodClosure
	 */
	void cleanup () {
		if (midiMethodClosure != null) {
			midiMethodClosure.dispose();
			midiMethodClosure = null;
		}
	}
}

/*
 */
