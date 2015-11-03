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
//  MIDISetup.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Jon Summers
//
package com.apple.audio.midi;

import com.apple.audio.*;
import com.apple.audio.util.*;
import com.apple.audio.jdirect.*;

/**
 * Implements the MIDISetup as defined in MIDIServices.h
 */
public final class MIDISetup extends MIDIObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}
	/*
 	 *	typedef struct OpaqueMIDISetup*        MIDISetupRef;
	 */
//_________________________ CLASS INSTANCES
	private static MIDISetup  system;
	private static CAMemoryObject staticOutArg = new CAMemoryObject (4, false);
	
//_________________________ CLASS METHODS
	/**
	* This is useful for forcing the system to have drivers rescan hardware, especially in the case of serial MIDI interfaces.
	* <P>
	* New for CoreMIDI 1.1.
     * <BR><BR><b>CoreAudio::MIDIRestart()</b><BR><BR>
	*/
	public static void RestartMIDISystem () throws CAException {
		int res = MIDIRestart();
		CAException.checkError (res);
	}
	
	/**
	 * Returns the number of sources in the current MIDISetup.
     * <BR><BR><b>CoreAudio::MIDIGetNumberOfSources()</b><BR><BR>
	 * @result  The number of MIDIEndpoint sources in the system, or 0 if an error occurred.
	 */
	public static int getNumberOfSources () {
		return MIDIGetNumberOfSources ();
	}

	/**
	 * Return one of the sources in the current MIDISetup.
     * <BR><BR><b>CoreAudio::MIDIGetSource()</b><BR><BR>
     * @param sourceIndex0 The index (0...MIDIGetNumberOfSources()-1) of the source to return
	 * @result  A source endpoint
	 * @exception if invalid destination index or an error occurs
	 */
	public static MIDIEndpoint getSource (int sourceIndex0) {
		int  source = MIDIGetSource (sourceIndex0);
		if (source == 0) {
			return null;
		}
		return new MIDIEndpoint (source, staticOutArg);	//just need *SOME* object to hold onto this ref
	}

	/**
	 * Returns the number of destinations in the system.
     * <BR><BR><b>CoreAudio::MIDIGetNumberOfDestinations()</b><BR><BR>
	 * @result  The number of destinations in the system, or 0 if an error occurred.
	 */
	public static int getNumberOfDestinations() {
		return MIDIGetNumberOfDestinations ();
	}

	/**
	 * Return one of the destinations in the system.
     * <BR><BR><b>CoreAudio::MIDIGetDestination()</b><BR><BR>
     * @param destinationIndex0 The index (0...MIDIGetNumberOfSources()-1) of the source to return
	 * @result  A destination endpoint
	 * @exception if invalid destination index
	 */
	public static MIDIEndpoint getDestination (int destinationIndex0) {
		int  destination = MIDIGetDestination (destinationIndex0);
		if (destination == 0) 
			return null;
		return  new MIDIEndpoint (destination, staticOutArg);	//just need *SOME* object to hold onto this ref
	}

   /**
     * Return the system's current MIDISetup
     * <BR><BR><b>CoreAudio::MIDISetupGetCurrent()</b><BR><BR>
	 * @return current MIDISetup
 	 * @exception On error within MIDISetupGetCurrent
    */
	public synchronized static MIDISetup getCurrent () throws CAException {
		int res = MIDISetupGetCurrent(CAObject.ID(staticOutArg));
		CAException.checkError(res);
		int newCur = staticOutArg.getIntAt(0);
		if (newCur == 0) {
			system = null;
			return null;
		}
		if (newCur == CAObject.ID(system))
			return system;
		else
			return new MIDISetup(newCur, staticOutArg);
	}

	/**
	* The new device is not added to the current MIDISetupRef to do this, use MIDISetupAddExternalDevice.
	* Useful for a studio configuration editor.  
	* <P>
	* New for CoreMIDI 1.1.
	* <P>
     * <BR><BR><b>CoreAudio::MIDIExternalDeviceCreate()</b><BR><BR>
	* @param name The name of the new device.
	* @param manufacturer The name of the device's manufacturer.
	* @param model The device's model name.
	* @return On successful return, points to the newly-created device.
	*/
	public synchronized static MIDIDevice createExternalDevice (CAFString name, CAFString manufacturer, CAFString model) throws CAException {
		int res = MIDIExternalDeviceCreate (CAObject.ID(name), CAObject.ID(manufacturer), CAObject.ID(model), CAObject.ID(staticOutArg));
		CAException.checkError (res);
		return new MIDIDevice (staticOutArg.getIntAt(0));
	}
	
	/**
	* Removes an external MIDI device from the current MIDISetup. Useful for a studio configuration editor.  
	* <P>
	* New for CoreMIDI 1.1.
	* <P>
	* <BR><BR><b>CoreAudio::MIDISetupRemoveExternalDevice()</b><BR><BR>
	* @param deviceThe device to be removed.
	*/
	public static void removeExternalDevice (MIDIDevice device) throws CAException {
		int res = MIDISetupRemoveExternalDevice (CAObject.ID(device));
		CAException.checkError (res);
	}

	/**
	* Adds an external MIDI device from the current MIDISetup. Useful for a studio configuration editor.  
	* <P>
	* New for CoreMIDI 1.1.
	* <P>
	* <BR><BR><b>CoreAudio::MIDISetupAddExternalDevice()</b><BR><BR>
	* @param deviceThe device to be added.
	*/
	public static void addExternalDevice (MIDIDevice device) throws CAException {
		int res = MIDISetupAddExternalDevice (CAObject.ID(device));
		CAException.checkError (res);
	}

	/**
	* Generally this should only be called from a studio configuration
	* editor, to remove a device which is offline and which the user
	* has specified as being permanently missing.
	* 	
	* Instead of removing devices from the setup, drivers should
	* set the device's kMIDIPropertyOffline to 1 so that if the
	* device reappears later, none of its properties are lost.
	* <P>
	* New for CoreMIDI 1.1.
	* <P>
	* <BR><BR><b>CoreAudio::MIDISetupRemoveDevice()</b><BR><BR>
	* @param deviceThe device to be removed.
	*/
	public static void removeDevice (MIDIDevice device) throws CAException {
		int res = MIDISetupRemoveDevice (CAObject.ID(device));
		CAException.checkError (res);
	}

	/**
     * Constructor that creates a potentially non-disposable reference.
     * @param id 	The native MIDISetupRef that is being referenced
     */
	private MIDISetup (int ptr, Object owner) {
		super (ptr, owner);
		setCurrent(this);
	}
		
    /**
     * Create a newly-created MIDISetup
     * <BR><BR><b>CoreAudio::MIDISetupCreate()</b><BR><BR>
     */
	public MIDISetup () throws CAException {
		super (allocate(), null);
	}
	
    /**
     * Create a MIDISetup object from an XML stream
     * <BR><BR><b>CoreAudio::MIDISetupFromData()</b><BR><BR>
	 * @param cfData The XML text from which a MIDISetup object is to be built
	 * @exception On error within MIDISetupFromData
     */
	public MIDISetup (CAFData cfData) throws CAException {
		super (allocate (cfData), null);
	}
	
	private static int allocate () throws CAException {
		int res = MIDISetupCreate (CAObject.ID(staticOutArg));
		CAException.checkError(res);
		return staticOutArg.getIntAt(0);
	}
	
	private static int allocate (CAFData cfData) throws CAException {
		int res = MIDISetupFromData (CAObject.ID(cfData), CAObject.ID(staticOutArg));
		CAException.checkError(res);
		return staticOutArg.getIntAt(0);
	}

	private static void setCurrent (MIDISetup newCurrent) {
		system = newCurrent;
	}
//_________________________ INSTANCE VARIABLES
	private CAMemoryObject outArg = new CAMemoryObject (4, false);
	
//_________________________ INSTANCE METHODS
    /**
     * before dispose is called
     * <BR><BR><b>CoreAudio::MIDISetupDispose()</b><BR><BR>
     */
	protected void preDispose () {
		if (_ID() != 0) {
			if (isCurrent()) {
				system = null;
			}
		}
	}	

    /**
     * Return true if this setup is the system's current MIDISetup
     */
	public boolean isCurrent () {
		return (this == system);
	}
	
    /**
     * Create an XML representation of a MIDISetup object
     * <BR><BR><b>CoreAudio::MIDISetupToData()</b><BR><BR>
     * @return newly-created CFData containing the XML text
     */
	public CAFData toData () throws CAException {
		int res = MIDISetupToData (_ID(), CAObject.ID(outArg));
		CAException.checkError (res);
		return JNIMidi.newCFData (outArg.getIntAt(0));
	}
	
    /**
     * Install a MIDISetup as the system's current state
     * <BR><BR><b>CoreAudio::MIDISetupInstall()</b><BR><BR>
	 * @exception On error within MIDISetupInstall
     */
	public void install () throws CAException {
		int res = MIDISetupInstall (_ID());
		CAException.checkError (res);
		setCurrent (this);
	}

	
//_ NATIVE METHODS
	private static native int MIDISetupCreate (int outSetup);
	private static native int MIDISetupInstall (int setup);
	private static native int MIDISetupGetCurrent (int outSetup);
	private static native int MIDISetupToData (int setup, int outData);
	private static native int MIDISetupFromData (int data, int outSetup);

	private static native int MIDIGetNumberOfSources ();
	private static native int MIDIGetSource (int sourceIndex0);
	private static native int MIDIGetNumberOfDestinations ();
	private static native int MIDIGetDestination (int destIndex0);

	private static native int MIDIRestart();
	private static native int MIDISetupRemoveDevice(int device);
	private static native int MIDISetupAddExternalDevice(int device);
	private static native int MIDIExternalDeviceCreate (int name, int manufacturer, int model, int outDevicePtr);
	private static native int MIDISetupRemoveExternalDevice (int device);
}

/*
 */
 
 // NOT DOING -> need to do other stuff you can't from Java anyway...
//  -----------------------------------------------------------------------------
/*! DRIVER DOES THIS
	@function		MIDISetupAddDevice

	@abstract 		Adds a driver-owner MIDI device to the current MIDISetup
	
	@discussion		Only MIDI drivers may make this call; it is in this header
					file only for consistency with MIDISetupRemoveDevice.
	
					New for CoreMIDI 1.1.
	
	@param			device
						The device to be added.
extern OSStatus
MIDISetupAddDevice(		MIDIDeviceRef device );
*/
//  -----------------------------------------------------------------------------
/*!
	@function		MIDIGetSerialPortOwner

	@abstract 		Returns the MIDI driver that owns a serial port.
	
	@discussion		The current MIDISetup tracks ownership of serial ports
					to one of the MIDI drivers installed in the system.
	
					Serial ports can be enumerated using IOServiceMatching(
					kIOSerialBSDServiceValue).  The port's unique name is
					the IOService's IOTTYBaseName property. 

					New for CoreMIDI 1.1.
	
	@param			portName
						The name of a serial port.
	@param			outDriverName
						On exit, the name of the driver owning the port,
						or NULL if no driver owns it.

	@result			An OSStatus result code.	

extern OSStatus
MIDIGetSerialPortOwner(	CFStringRef			portName, 
						CFStringRef *		outDriverName );
*/

//  -----------------------------------------------------------------------------
/*!
	@function		MIDISetSerialPortOwner

	@abstract 		Specifies the MIDI driver that owns a serial port.
	
	@discussion		Use this to assign ownership of a serial port
					to one of the MIDI drivers installed in the system.
	
					New for CoreMIDI 1.1.
	
	@param			portName
						The name of a serial port.
	@param			driverName
						The name of the driver that owns the serial port,
						or NULL to specify that no driver owns it.

	@result			An OSStatus result code.	

extern OSStatus
MIDISetSerialPortOwner(	CFStringRef			portName, 
						CFStringRef			driverName );
*/

 //  -----------------------------------------------------------------------------
/*!
	@function		MIDIGetSerialPortDrivers

	@abstract 		Returns a list of installed MIDI drivers for serial port
					MIDI devices.
	
	@discussion		Use this to determine which of the installed MIDI drivers
					are for devices which may attach to serial ports.
	
					New for CoreMIDI 1.1.
	
	@param			outDriverNames
						On exit, a CFArrayRef containing a list of CFStringRef's
						which are the names of the serial port MIDI drivers.
						The array should be released by the caller.

	@result			An OSStatus result code.	

extern OSStatus
MIDIGetSerialPortDrivers(	CFArrayRef *outDriverNames );
*/
