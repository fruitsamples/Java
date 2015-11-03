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
//  AudioHardware.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.hardware;

import com.apple.audio.*;
import com.apple.audio.util.*;
import com.apple.audio.jdirect.*;

import java.util.*;

/**
 * This class provides static methods to obtain information about
 * the Audio Hardware that is currently available to the running system.
 */
public final class AudioHardware {	
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}

//_________________________ CLASS FIELDS
	// we share a common cache object between calls so ALL the native calls that use 
	// these objects are synchronized
	private static transient final CAMemoryObject retSize4_A = new CAMemoryObject (4, false);
	private static transient final CAMemoryObject retSize4_B = new CAMemoryObject (4, false);
	private static Hashtable table = new Hashtable();
	
//_________________________ CLASS METHODS	
	/**
	 * Retrieve the size of the given property's value. 
 	 * <BR><BR><b>CoreAudio::AudioHardwareGetPropertyInfo</b><BR><BR>
	 * @param inPropertyID the property to query
	 * @return the size in bytes of the current value of the property.
	 */
	public static synchronized int getPropertyInfoSize (int inPropertyID) throws CAException {
		retSize4_A.setIntAt (0, 4);
		int res = AudioHardwareGetPropertyInfo (inPropertyID, CAObject.ID(retSize4_A), CAObject.ID(retSize4_B));
		CAException.checkError (res);
		return retSize4_A.getIntAt (0);
	}

	/**
	 * Returns whether the given property can be changed. 
 	 * <BR><BR><b>CoreAudio::AudioHardwareGetPropertyInfo</b><BR><BR>
	 * @param inPropertyID the property to query
	 * @return whether or not the property in question can be changed.
	 */
	public static synchronized boolean getPropertyInfoWritable (int inPropertyID) throws CAException {
		retSize4_A.setIntAt (0, 4);
		int res = AudioHardwareGetPropertyInfo (inPropertyID, CAObject.ID(retSize4_A), CAObject.ID(retSize4_B));
		CAException.checkError (res);
		return retSize4_B.getBooleanAt (0);
	}

	/**
 	 * Gets the value of the requested property and places that value in the parameter <CODE>outProperty</CODE>. 
 	 * The user must provide a CAMemoryObject that should be at least large enough to hold the value of the 
 	 * queeried property, the size of which is obtained through calling <CODE>getPropertyInfoSize</CODE>.
 	 * <P>
 	 * This is a generic call in that the class that would truly represented
 	 * the specified property is not known.
 	 * <BR><BR><b>CoreAudio::AudioHardwareGetProperty</b><BR><BR>
 	 * @param inPropertyID the property for which the current value is sought
 	 * @param outProperty will contain the value of the specified property upon return.
 	 * @return the amount of bytes that were actually written into outProperty (starting at offset==0)
	 */ 
	public static synchronized int getProperty (int inPropertyID, CAMemoryObject outProperty) throws CAException {
		retSize4_A.setIntAt (0, outProperty.getSize());
		int res = AudioHardwareGetProperty (inPropertyID, CAObject.ID(retSize4_A), CAObject.ID (outProperty));
		CAException.checkError (res);
		return retSize4_A.getIntAt (0);
	}
	
	/**
	 * Returns the System ouput AudioDevice.
 	 * <BR><BR><b>CoreAudio::AudioHardwareGetProperty</b><BR><BR>
 	 * @return default output devcie
	 */
	public static synchronized AudioDevice getSystemOutputDevice () throws CAException {
		retSize4_A.setIntAt (0, 4);
		int res = AudioHardwareGetProperty(AHConstants.kAudioHardwarePropertyDefaultSystemOutputDevice, CAObject.ID(retSize4_A), CAObject.ID(retSize4_B));
		CAException.checkError (res);
		return AudioDevice.makeDevice (retSize4_B.getIntAt(0));
	}
	
	/**
	 * Returns the default ouput AudioDevice.
 	 * <BR><BR><b>CoreAudio::AudioHardwareGetProperty</b><BR><BR>
 	 * @return default output devcie
	 */
	public static synchronized AudioDevice getDefaultOutputDevice () throws CAException {
		retSize4_A.setIntAt (0, 4);
		int res = AudioHardwareGetProperty(AHConstants.kAudioHardwarePropertyDefaultOutputDevice, CAObject.ID(retSize4_A), CAObject.ID(retSize4_B));
		CAException.checkError (res);
		return AudioDevice.makeDevice (retSize4_B.getIntAt(0));
	}

	/**
	 * Returns the default input AudioDevice.
 	 * <BR><BR><b>CoreAudio::AudioHardwareGetProperty</b><BR><BR>
 	 * @return default input device
	 */
	public static synchronized AudioDevice getDefaultInputDevice () throws CAException {
		retSize4_A.setIntAt (0, 4);
		int res = AudioHardwareGetProperty(AHConstants.kAudioHardwarePropertyDefaultInputDevice, CAObject.ID(retSize4_A), CAObject.ID(retSize4_B));
		CAException.checkError (res);
		return AudioDevice.makeDevice (retSize4_B.getIntAt(0));
	}

	/**
	 * Will return an array of AudioDevice objects that represent all of the audio devices that are currently
	 * found within the system.
 	 * <BR><BR><b>CoreAudio::AudioHardwareGetProperty</b><BR><BR>
	 * @return an array of discovered AudioDevices
	 */
	public static synchronized AudioDevice[] getAudioDevices () throws CAException {
		int size = getPropertyInfoSize (AHConstants.kAudioHardwarePropertyDevices);
		CAMemoryObject retVal = null;
		if (size == 4)
			retVal = retSize4_B;
		else
			retVal = new CAMemoryObject (size, false);
		retSize4_A.setIntAt (0, size);
		int res = AudioHardwareGetProperty(AHConstants.kAudioHardwarePropertyDevices, CAObject.ID(retSize4_A), CAObject.ID(retVal));
		CAException.checkError (res);
		AudioDevice devs[] = new AudioDevice [retSize4_A.getIntAt(0) / 4]; //each device ptr takes 4 bytes
		for (int i = 0; i < devs.length; i++)
			devs[i] = AudioDevice.makeDevice (retVal.getIntAt (i * 4));
		return devs;
	}
	
	/**
	 * Sets the supplied device as the default for that task (System, Input or Output).
 	 * <BR><BR><b>CoreAudio::AudioHardwareSetProperty</b><BR><BR>
	 * @param inPropertyID the property to set (System, Input or Output)
	 * @param inDevice the Device that is set as the default
	 */
	public static synchronized void setDefaultDevice (int inPropertyID, AudioDevice inDevice) throws CAException {
		retSize4_A.setIntAt (0, CAObject.ID(inDevice));
		int res = AudioHardwareSetProperty (inPropertyID, 4, CAObject.ID(retSize4_A));
		CAException.checkError (res);
	}

 	/**
	 * Set the indicated property data. Global properties, by definition, don't
	 * directly affect real time, so they don't need a time stamp.
 	 * <BR><BR><b>CoreAudio::AudioHardwareSetProperty</b><BR><BR>
	 * @param inPropertyID the property to set
	 * @param inPropertyData the data to set - the size returned by <CODE>getSize()</CODE> is used as the property size
	 */
	public static synchronized void setProperty (int inPropertyID, CAMemoryObject inPropertyData) throws CAException {
		int res = AudioHardwareSetProperty (inPropertyID, inPropertyData.getSize(), CAObject.ID(inPropertyData));
		CAException.checkError (res);
	}
	
	/**
	 * Set up a routine that gets called when a property is changed.
 	 * <BR><BR><b>CoreAudio::AudioHardwareAddPropertyListener</b><BR><BR>
	 * @param listener the execute method on this object is called when the property changes.
	 */	
	public static synchronized void addPropertyListener (int inPropertyID, AHardwarePropertyListener listener) throws CAException {
		HardwareDispatcher disp = new HardwareDispatcher (listener);
		int res = AudioHardwareAddPropertyListener (inPropertyID, disp.ID(), 0);

		if (res != 0) {
			disp.cleanup();
			CAException.checkError (res);
			return;
		}
		Integer key = new Integer (inPropertyID);
		if (table.containsKey (key) == false) {
			Vector v = new Vector ();
			v.addElement (disp);
			table.put (key, v);
		} else {
			((Vector)table.get (key)).addElement (disp);
		}
	}

	/**
	 * Remove the previously instantiated notification call.
 	 * <BR><BR><b>CoreAudio::AudioHardwareRemovePropertyListener</b><BR><BR>
	 * @param listener the execute method on this object that would have been called when the property changed
	 */	
	public static synchronized void removePropertyListener (int inPropertyID, AHardwarePropertyListener listener) throws CAException {
		Integer key = new Integer (inPropertyID);
		Object value = table.get (key);
		if (value == null)
			throw new CAException ("Can't find the specified listener <" + listener + "> for this property <" + Integer.toHexString(inPropertyID) + ">");
		Vector vec = (Vector)value;	
		Enumeration iter = vec.elements();
		
		HardwareDispatcher toBeRemoved = null;
		while (iter.hasMoreElements()) {
			HardwareDispatcher disp = (HardwareDispatcher) iter.nextElement ();
			if (disp.getAHListener() == listener) {
				toBeRemoved = disp;
				vec.removeElement (toBeRemoved);
				break;
			}
		}
		if (vec.size() == 0)
			table.remove (key);
		if (toBeRemoved == null)
			throw new CAException ("Can't find the specified listener <" + listener + "> for this property <" + Integer.toHexString(inPropertyID) + ">");
		
		int res = AudioHardwareRemovePropertyListener (inPropertyID, toBeRemoved.ID());
		toBeRemoved.cleanup();
		
		CAException.checkError (res);
	}
	
	/**
	* When this routine is called, all IO on all devices within a process will
	* be terminated and all resources capable of being released will be released.
	* This routine essentially returns the HAL to it's uninitialized state.
 	 * <BR><BR><b>CoreAudio::AudioHardwareUnload</b><BR><BR>
	*/
 	public static void unload () throws CAException {
		CAException.checkError(AudioHardwareUnload());
	}
	
	private AudioHardware() {}

//_ NATIVE METHODS
	private static native int AudioHardwareGetPropertyInfo (int inPropertyID, int outSizeIntPtr, int outWritableBoolPtr);
	private static native int AudioHardwareGetProperty (int	inPropertyID, int ioPropertyDataSizePtr, int outPropertyDataPtr);
	private static native int AudioHardwareSetProperty (int	inPropertyID, int inPropertyDataSize, int inPropertyDataPtr);
	private static native int AudioHardwareAddPropertyListener (int inPropertyID, int inProc, int /*void* */inClientData);
	private static native int AudioHardwareRemovePropertyListener(int inPropertyID, int inProc);
	private static native int AudioHardwareUnload();
	
//	private static native int AudioHardwareCreateVirtualSource(int PtrToOutNewDeviceIDPtr);
//	private static native int AudioHardwareDestroyVirtualSource(int deviceIDPtr);
}

/*
 */
