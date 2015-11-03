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
//  MIDIEntity.java
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
 * Implements the MIDIEntityRef as defined in MIDIServices.h
 */
// typedef struct OpaqueMIDIEntity*        MIDIEntityRef;
public final class MIDIEntity extends MIDIObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}
		
//_________________________ CLASS METHODS

    /**
	 *	- package scoped - constructor encapsulates a native CoreAudio MIDIEntityRef
     * @param id native MIDIEntityRef
     */
	MIDIEntity (int ptr, Object owner) {
		super (ptr, owner);
	}
	
//_________________________ INSTANCE METHODS	
	/**
	 * Return the number of sources in a given entity
     * <BR><BR><b>CoreAudio::MIDIEntityGetNumberOfSources()</b><BR><BR>
	 * @return the number of sources the entity contains, or 0 if an error occurred.
	 */
	public int getNumberOfSources () {
		return MIDIEntityGetNumberOfSources (_ID());
	}

	/**
	 * Get one of the entity's sources
     * <BR><BR><b>CoreAudio::MIDIEntityGetSource()</b><BR><BR>
	 * @param entityIndex0 The index (0...MIDIDeviceGetNumberOfEntities()-1) of the entity to return.
	 * @return A MIDIEndpoint
	 * @exception if invalid source
	 */
	public MIDIEndpoint getSource (int sourceIndex0) {
		int source = MIDIEntityGetSource (_ID(), sourceIndex0);
		if (source == 0) {
			return null;
		}
		return new MIDIEndpoint (source, this);
	}

	/**
	 * Return the number of destinations in a given entity
     * <BR><BR><b>CoreAudio::MIDIEntityGetNumberOfDestinations()</b><BR><BR>
	 * @return the number of destinations the entity contains, or 0 if an error occurred.
	 */
	public int getNumberOfDestinations () {
		return MIDIEntityGetNumberOfDestinations (_ID());
	}

	/**
	 * Get one of the entity's destinations
     * <BR><BR><b>CoreAudio::MIDIEntityGetDestination()</b><BR><BR>
	 * @param destIndex0 The index (0...MIDIEntityGetNumberOfDestinations()-1) of the source to return.
	 * @return A MIDIEndpoint
	 * @exception if invalid destination
	 */
	public MIDIEndpoint getDestination (int destIndex0) {
		int destination = MIDIEntityGetDestination (_ID(), destIndex0);
		if (destination == 0) {
			return null;
		}
		return new MIDIEndpoint (destination, this);
	}

//_ NATIVE METHODS
	private static native int MIDIEntityGetNumberOfSources (int entity);
	private static native int MIDIEntityGetSource (int entity, int sourceIndex0);
	private static native int MIDIEntityGetNumberOfDestinations (int entity);
	private static native int MIDIEntityGetDestination (int entity, int destIndex0);
}

/*
 */
