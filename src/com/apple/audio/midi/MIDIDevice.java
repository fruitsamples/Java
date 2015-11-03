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
//  MIDIDevice.java
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
 * Implements the MIDIDeviceRef as defined in MIDIServices.h
 */
// typedef struct OpaqueMIDIDevice*        MIDIDeviceRef;
public final class MIDIDevice extends MIDIObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}
	
	private static final Object holder = new Object();
	
//_________________________ CLASS METHODS
	/**
	 * The number of MIDI devices in the system
     * <BR><BR><b>CoreAudio::MIDIGetNumberOfDevices()</b><BR><BR>
	 * @return the number of devices in the system, or 0 if an error occurred.
	 */
	public static int getNumberOfDevices () {
		return MIDIGetNumberOfDevices ();
	}

	/**
	 * Get one of the devices in the system
	 * As an Iterator through the devices and entities in the system, it will not ever visit any virtual sources and
       destinations created by other clients. Clients should prefer endpoint.getNumberOfSources(), MIDIEndpoint.getSource(),
       MIDIEndpoint.getNumberOfDestinations() and MIDIEndpoint.getDestination() to iterating through devices/entities/endpoints.
     * <BR><BR><b>CoreAudio::MIDIGetDevice()</b><BR><BR>
	 * @param deviceIndex0 The index (0...MIDIGetNumberOfDevices()-1) of the device to return
	 * @return A device in the system
	 * @exception on invalid device
	 */
	public static MIDIDevice getDevice (int deviceIndex0) {
		int device = MIDIGetDevice (deviceIndex0);
		if (device == 0) {
			return null;
		}
		return new MIDIDevice (device);
	}
		
	/**
	 * External MIDI devices are MIDI devices connected to endpoints via a standard MIDI cable. 
	 * Their presence is completely optional, only when a UI somewhere adds them.
	 * <P>
	 * New for CoreMIDI 1.1.
	 * <P>
     * <BR><BR><b>CoreAudio::MIDIGetNumberOfExternalDevices()</b><BR><BR>
	 * @return The number of external devices in the system, or 0 if an error occurred.
	 */
	public static int getNumberOfExternalDevices () {
		return MIDIGetNumberOfExternalDevices();
	}
	
	/**
	* Use this to enumerate the external devices in the system.
	* <P>
	* New for CoreMIDI 1.1.
	* <P>
     * <BR><BR><b>CoreAudio::MIDIGetExternalDevice()</b><BR><BR>
	* @param deviceIndex0 The index (0...MIDIGetNumberOfDevices()-1) of the device to return.
	* @return A reference to a device.
	*/
	public static MIDIDevice getExternalDevice (int deviceIndex0) throws CAException {
		int dev = MIDIGetExternalDevice (deviceIndex0);
		if (dev == 0)
			throw new CAException ("Error in retreiving device at index=" + deviceIndex0);
		return new MIDIDevice (dev);
	}

	/**
	 *	- package scoped - constructor encapsulates a native CoreAudio MIDIDeviceRef
     * @param id native MIDIDeviceRef
	 */
	MIDIDevice (int ptr) {
		super (ptr, holder);
	}
	
//_________________________ INSTANCE METHODS	
	/**
	 * Return the number of entities in a given device
     * <BR><BR><b>CoreAudio::MIDIDeviceGetNumberOfEntities()</b><BR><BR>
	 * @return the number of entities the device contains, or 0 if an error occurred.
	 */
	public int getNumberOfEntities () {
		return MIDIDeviceGetNumberOfEntities (_ID());
	}

	/**
	 * Get one of the device's entities
     * <BR><BR><b>CoreAudio::MIDIDeviceGetEntity()</b><BR><BR>
	 * @param entityIndex0 The index (0...MIDIDeviceGetNumberOfEntities()-1) of the entity to return.
	 * @return A indexed MIDIEntity for this device
	 * @exception on invalid entity
	 */
	public MIDIEntity getEntity (int entityIndex0) {
		int entity = MIDIDeviceGetEntity (_ID(), entityIndex0);
		if (entity == 0)
			return null;
		return new MIDIEntity (entity, holder);
	}

//_ NATIVE METHODS
	private static native int MIDIGetNumberOfDevices ();
	private static native int MIDIGetDevice (int  deviceIndex0);
	private static native int MIDIDeviceGetNumberOfEntities (int device);
	private static native int MIDIDeviceGetEntity (int device, int entityIndex0);
	private static native int MIDIGetNumberOfExternalDevices();
	private static native int MIDIGetExternalDevice(int deviceIndex0);
}

/*
 */
