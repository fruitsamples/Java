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
//  MIDISysexSendRequest.java
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

/**
 * Implements the MIDISysexSendRequest as defined in MIDIServices.h
 */
public final class MIDISysexSendRequest extends CAMemoryObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}
/*
    struct MIDISysexSendRequest {
        MIDIEndpointRef  destination;		// 0	4 The endpoint to which the event is to be sent
        Byte *           data;			// 4 	4 Initially, a pointer to the sys-ex event to be sent. 
        UInt32           bytesToSend;		// 8 	4 Initially, the number of bytes to be sent. 
        Boolean          complete;		// 12 	1 The client may set this to true at any time to abort transmission. 
        Byte             reserved[3];		// 13 	3 (unused)
        MIDICompletionProc  completionProc;	// 16 	4 Called when all bytes sent, or after the client set complete to true
        void *           completionRefCon;	// 20 	4 Passed as a refCon to completionProc
    }; // sizeof(MIDISysexSendRequest) = 24

// MIDISendSysex will advance the data pointer as bytes are sent
// MIDISendSysex will decrement the byteToSend counter as bytes are sent
// The implementation sets the complete bool to true when all bytes have been sent
*/

	private static final int kDestinationAt = 0;
	private static final int kDataAt = 4;
	private static final int kBytesToSendAt = 8;
	private static final int kCompleteAt = 12;
	private static final int kCompletionProcAt = 16;						
	private static final int kCompletionRefConAt = 20;
	private static final int kNativeSize = 24;
	 
//_________________________ CLASS METHODS
    /**
     * construct a MIDISysexSendRequest object for sending midi data with send()
     * @param data The MIDIData of type kMIDIRawData or kMIDIPacketData to send
     */
    public MIDISysexSendRequest (MIDIEndpoint dest, MIDIData data) {
        super (checkMIDIDataType(data), true);		// CAMemoryObject (int size, boolean clear)
        midiData = data;
        setIntAt (kDataAt, CAObject.ID(data) + data.getMIDIDataOffset());
        setIntAt (kBytesToSendAt, data.getMIDIDataLength());
        setIntAt (kDestinationAt, CAObject.ID(dest));
        destPoint = dest;
    }
	
    /**
     * check that MIDIData is of type compatible for send()
     */
    private static int checkMIDIDataType(MIDIData data) {
        if (data.isType(MIDIData.kMIDIRawData) || data.isType(MIDIData.kMIDIPacketData)) {
            return MIDISysexSendRequest.kNativeSize;
        }
        throw new ClassCastException ("Wrong type of MIDI Data for MIDISysexSendRequest");
    }
	
//_________________________ INSTANCE FIELDS
    private MIDIData midiData = null;
    private MIDIDispatcher completionHandler = null;
    private MIDIEndpoint destPoint;
	
//_________________________ INSTANCE METHODS	
    /**
     * Get the endpoint to which the event is to be sent
     * @return The destination MIDIEndpoint 
     */
    public MIDIEndpoint getDestination () {
        return destPoint;
    }
	
    /**
     * Get the number of bytes remaining to be sent
     * send() decrements this counter as bytes are sent
     */
    public int getBytesToSend () {
        return getIntAt (kBytesToSendAt);
    }
	
    /**
     * send the MIDI data asynchronously
     * <BR><BR><b>CoreAudio::MIDISendSysex</b><BR><BR>
     * @exception if not initialized with midi data
     */
    public void send (MIDICompletionProc completionProc) throws CAException {
        completionHandler = (completionProc == null) ? null : new MIDIDispatcher(this, completionProc);
        setIntAt (kCompletionProcAt, (completionHandler == null) ? 0 : completionHandler.ID());
        setByteAt (kCompleteAt, (byte)0);
        int res = MIDISendSysex (_ID());
        CAException.checkError (res);
    }
	
    /**
     * have all bytes have been sent
     */
    public boolean isComplete () {
        int res = getByteAt(kCompleteAt);
        if (res == 0 && completionHandler != null) {
            completionHandler.cleanup();
            completionHandler = null;
        }
        return (res != 0);
    }
	
    /**
     * abort transmission
     */
    public void stop () {
        setIntAt (kCompletionProcAt, 0);
        setByteAt(kCompleteAt, (byte)1);
        if (completionHandler != null) {
            completionHandler.cleanup();
            completionHandler = null;
        }
    }
	
//_ NATIVE METHODS
    private static native int MIDISendSysex (int request);
}

/*
 */
