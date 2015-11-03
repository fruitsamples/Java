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
//  MIDIObject.java
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
 * Implements the MIDIObject as defined in MIDIServices.h
 */
public abstract class MIDIObject extends CAObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}

//_________________________ CLASS METHODS

	/**
	 * - package scoped - constructor encapsulates a native MIDIServices MIDIObject object
     * @param ptr native MIDIObject
	 */
	MIDIObject (int ptr, Object owner) {
		super (ptr, owner);
	}
	
//_________________________ INSTANCE FIELDS
	private CAMemoryObject outArg = new CAMemoryObject (4, false);

//_________________________ INSTANCE METHODS	
    /**
     * Get an object's integer-type property
     * <BR><BR><b>CoreAudio::MIDIObjectGetIntegerProperty()</b><BR><BR>
	 * @param name Name of the property to return
 	 * @return The value of the property
	 * @exception On error within MIDIObjectGetIntegerProperty
     */
	public int getProperty (CAFString property) throws CAException {
		synchronized (outArg) {
			int res = MIDIObjectGetIntegerProperty (_ID(), CAObject.ID(property), CAObject.ID(outArg));
			CAException.checkError (res);
			return outArg.getIntAt(0);
		}
	}
	
    /**
     * Set an object's integer-type property
     * <BR><BR><b>CoreAudio::MIDIObjectSetIntegerProperty()</b><BR><BR>
	 * @param name Name of the property to set
 	 * @param value New value of the property
	 * @exception On error within MIDIObjectSetIntegerProperty
     */
	public void setIntegerProperty (CAFString property, int value) throws CAException {
		int res = MIDIObjectSetIntegerProperty (_ID(), CAObject.ID(property), value);
		CAException.checkError (res);
	}

    /**
     * Get an object's string-type property
     * <BR><BR><b>CoreAudio::MIDIObjectGetStringProperty()</b><BR><BR>
	 * @param name Name of the property to return
 	 * @return The string value of the property
	 * @exception On error within MIDIObjectGetStringProperty
     */
	public CAFString getStringProperty (CAFString property) throws CAException {
		synchronized (outArg) {
			int res =  MIDIObjectGetStringProperty (_ID(), CAObject.ID(property), CAObject.ID(outArg));
			CAException.checkError (res);
			return JNIMidi.newCFString (outArg.getIntAt(0));
		}
	}

    /**
     * Set an object's string-type property
     * <BR><BR><b>CoreAudio::MIDIObjectSetStringProperty()</b><BR><BR>
	 * @param name Name of the property to set
 	 * @param value New string value of the property
	 * @exception On error within MIDIObjectSetStringProperty
     */
	public void setStringProperty (CAFString property, CAFString value) throws CAException {
		int res = MIDIObjectSetStringProperty (_ID(), CAObject.ID(property), CAObject.ID(value));
		CAException.checkError (res);
	}
	
    /**
     * Get an object's data-type property
     * <BR><BR><b>CoreAudio::MIDIObjectGetDataProperty()</b><BR><BR>
	 * @param name Name of the property to return
 	 * @return The CFData containing the value of the property
	 * @exception On error within MIDIObjectGetDataProperty
     */
	public CAFData getDataProperty (CAFString property) throws CAException {
		synchronized (outArg) {
			int res =  MIDIObjectGetDataProperty (_ID(), CAObject.ID(property), CAObject.ID(outArg));
			CAException.checkError (res);
			return JNIMidi.newCFData (outArg.getIntAt(0));
		}
	}

    /**
     * Set an object's data-type property
     * <BR><BR><b>CoreAudio::MIDIObjectSetDataProperty()</b><BR><BR>
	 * @param name Name of the property to set
 	 * @param data New CAFData value of the property
	 * @exception On error within MIDIObjectSetDataProperty
     */
	public void setDataProperty (CAFString property, CAFData data) throws CAException {
		int res = MIDIObjectSetDataProperty (_ID(), CAObject.ID(property), CAObject.ID(data));
		CAException.checkError (res);
	}
	
	
//_ NATIVE METHODS
	private static native int MIDIObjectGetIntegerProperty (int obj, int propertyID, int value);
	private static native int MIDIObjectSetIntegerProperty (int obj, int propertyID, int value);
	private static native int MIDIObjectGetStringProperty (int obj, int propertyID, int str);
	private static native int MIDIObjectSetStringProperty (int obj, int propertyID, int str);
	private static native int MIDIObjectGetDataProperty (int obj, int propertyID, int outData);
	private static native int MIDIObjectSetDataProperty (int obj, int propertyID, int data);
}

/*
 */
 
 // NOT USED AT THIS TIME
 // 	private static native int MIDIObjectGetProperties(int obj, int outPropertiesPtr, byte deep);
// JNIMidi ->	static native com.apple.audio.util.CAFPropertyList newCAFPropertyList (int ptr);

	/*
	* Returns a CFPropertyList of all of an object's properties.
	* The property list may be a dictionary or an array.
	* Dictionaries map property names (CFString) to values, 
	* which may be CFNumber, CFString, or CFData.  Arrays are
	* arrays of such values.
	* <P>					
	* Properties which an object inherits from its owning object (if
	* any) are not included.
	* <P>
	* New for CoreMIDI 1.1.
	* <P>
	* @param deep true if the object's child objects are to be included (e.g. a device's entities, or an entity's endpoints).
	*
	public CAFPropertyList getProperties (boolean deep) throws CAException {
		synchronized (outArg) {
			int res = MIDIObjectGetProperties(_ID(), CAObject.ID(outArg), (byte)(deep ? 1 : 0));
			CAException.checkError (res);
			return JNIMidi.newCAFPropertyList (outArg.getIntAt(0));
		}
	}
*/
