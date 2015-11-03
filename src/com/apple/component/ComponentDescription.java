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
//  ComponentDescription.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.component;

import java.io.*;

	//these classes are just used for implemenation NOT present in public interface
import com.apple.audio.CAObject;
import com.apple.audio.util.CAMemoryObject;
import com.apple.audio.util.CAUtils;

/** Describes component features. A value of zero indicates a non-specific value and can also
be used as a wildcard in searches. */
public class ComponentDescription implements Cloneable {
//	public static final String JDirect_MacOSX = "/System/Library/Frameworks/CoreServices.framework/Versions/A/CoreServices";	
//	private static Object linkage; 
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}

	final static int kNativeSize = 20;
	private final static int
		kIdentifierForName = 1,
		kIdentifierForInfo = 2;

//_________________________ CLASS METHODS
	/** Create a default ComponentDescription with all zero values.*/
	public ComponentDescription () { 
		desc = new CAMemoryObject (kNativeSize, true);
		descPtr = CAObject.ID(desc);
	}

	/** Specify the type of the ComponentDescription.*/
	public ComponentDescription (int type) { 
		this(); 
		setType(type); 
	}
	
	/** Specify a full ComponentDescription class.
	 * @param type Specifies the component type to search for. Set to 0 if not used in search.
	 * @param subType Specifies the component subType. Set to 0 if not used in search.
	 * @param manu Specifies the component manufacturer. Set to 0 if not used in search.
	 * @param flag Specifies the component control flag used. Set to 0 if not used in search.
	 * @param mask Specifies the component flag mask used. Set to 0 if not used in search.
	 */
	public ComponentDescription (int type, int subType, int manu, int flag, int mask) {
		this();
		setType(type);
		setSubType(subType);
		setManufacturer(manu);
		setFlags(flag);
		setMask(mask);
	}

	/** Creates a ComponentDescription from the contents of a byte[]. This byte[] 
	 * should have been retrieved from the asBytes() call as it represents a custom
	 * format of the stored contents.
	 * @param bytes this is the array that conatins the information from the ComponentDescription
	 */
	public ComponentDescription (byte[] bytes) {	
		this();
		if (bytes.length < kNativeSize) {
			throw new RuntimeException ("Not enough inforamtion to create a ComponentDescription from the given byte[]");
		} else {
			int currentIndex = 0;
			desc.copyFromArray (0, bytes, currentIndex, kNativeSize);
			currentIndex = kNativeSize;
			while (bytes.length < currentIndex) { // have name/info strings
				if (bytes[currentIndex] == kIdentifierForName) {
					currentIndex++;
					int length = CAUtils.UByte2Int(bytes[currentIndex++]);
					name = new String (bytes, currentIndex, length);
					currentIndex += length;
				} else if (bytes[currentIndex] == kIdentifierForInfo) {
					currentIndex++;
					int length = CAUtils.UByte2Int(bytes[currentIndex++]);
					info = new String (bytes, currentIndex, length);
					currentIndex += length;
				} else
					throw new RuntimeException ("Incorrect format for given byte[]");
			}
		}
	}
	
	/** used in cloning*/
	private ComponentDescription (CAMemoryObject src) { 
		this();	
			//this is slow but easy!!!
		for (int i = 0; i < kNativeSize; i+=4)
			desc.setIntAt (i, src.getIntAt (i));
	}
		
//_________________________ INSTANCE VARIABLES
	private transient int descPtr;
	private transient CAMemoryObject desc;
	String name, info; // these are created as null -> stay null if that inforamtion is not available
	
//_________________________ INSTANCE METHODS
	/** Returns the native address that contains the memory of this object*/
	public int _ID () {
		if (descPtr == 0)
			throw new NullPointerException ("Can't call this on a NULL native memory location");
		return descPtr;
	}
	
	/**
	 * @returns a copy of the ComponentDescription representation as a byte array
	 */
	public byte[] asByteArray() {
		int length = kNativeSize;
		if (name != null) {
			length += name.length();
			length++; //need an id byte
			length++; //need an term byte -> these are PStrings
		}
		if (info != null) {
			length += info.length();
			length++; //need an id byte
			length++; //need an term byte -> these are PStrings
		}
		int currentPosInArray = 0;
		byte[] ar = new byte[length];
		desc.copyToArray (0, ar, 0, kNativeSize);
		currentPosInArray = kNativeSize;
		if (name != null) {
			ar[currentPosInArray++] = kIdentifierForName;
			ar[currentPosInArray++] = (byte)name.length();
			System.arraycopy (name.getBytes(), 0, ar, currentPosInArray, name.length());
			currentPosInArray += name.length();
		}
		if (info != null) {
			ar[currentPosInArray++] = kIdentifierForInfo;
			ar[currentPosInArray++] = (byte)info.length();
			System.arraycopy (info.getBytes(), 0, ar, currentPosInArray, info.length());
			currentPosInArray += info.length();
		}
		if (currentPosInArray != ar.length)
			System.err.println ("CD:asByteArray:not correctly written");
		return ar;
	}

	/** Returns the component type. */
	public int getType () { return desc.getIntAt(0); }
	
	/** Sets the type. */
	public void setType (int type) { desc.setIntAt (0, type); }

	/** Returns the component subType. */
	public int getSubType () { return desc.getIntAt(4); }
	
	/** Sets the type. */
	public void setSubType (int subType) { desc.setIntAt (4, subType); }

	/** Returns the component manufacturer. */
	public int getManufacturer () { return desc.getIntAt(8); }
	
	/** Sets the manufacturer. */
	public void setManufacturer (int manufacturer) { desc.setIntAt (8, manufacturer); }

	/** Returns the component flag. */
	public int getFlags () { return desc.getIntAt(12); }
	
	/** Sets the flag. */
	public void setFlags (int flag) { desc.setIntAt (12, flag); }

	/** Returns the component mask. */
	public int getMask () { return desc.getIntAt(16); }
	
	/** Sets the mask. */
	public void setMask (int mask) { desc.setIntAt (16, mask); }

	/**
	 * Count the Components that much the description parameters.
	 * <BR><BR><b>QuickTime::CountComponents</b><BR>
	 * @param desc describes the search parameters.
	 * @return The number of components fitting the specified parameters.
	 */
	public int count () {
		return CountComponents(descPtr);
	}

	/**
	 * Returns the name of the component retrieved from the ComponentIdentifier.getInfo() call.
	 * @return the component's name or null if no name retrieved
	 */
	public String getName () {
		if (name == null)
			establishInformationalStrings();
		return name;
	}

	/**
	 * Returns information about the component retrieved from the ComponentIdentifier.getInfo() call.
	 * @return the component's information string or null if none retrieved
	 */
	public String getInformationString () {
		if (info == null)
			establishInformationalStrings();
		return info;
	}
	
	/**
	 * If this instance of the ComponentDescription does not contain the name or information Strings
	 * of the component, you can call this method to establish those variables. If either of these
	 * are still null after that call, then the component does not provide that information.
	 * <P>
	 * This would typically be used when you've called the default constructor, and have retrieved
	 * information about the component from a native call.
	 */
	public void establishInformationalStrings () {
		if (name == null & info == null) {
			int res = FindNextComponent (0, _ID());
			if (res == 0)
				throw new RuntimeException ("Can't find a component that matches this Description");
			Component.fillInInfo (res, this);
		}	
	}
	
	/** @return a string representation of this object */ 
	public String toString() {
		String str = "";
		if (name != null)
			str += ",name=" + name;
		if (info != null)
			str += ",info=" + info;
		
		return getClass().getName()
				+ "[type=" + CAUtils.fromOSType(getType())
				+ ",subType=" + CAUtils.fromOSType(getSubType()) 
				+ ",manufacturer=" + CAUtils.fromOSType(getManufacturer()) 
				+ ",flags=" + getFlags()
				+ str + "]";
	}
	
	/** @return a copy of this object. */
	public Object clone () { 
		return new ComponentDescription (desc);
	}

	/** @return Indicates whether some other object is equal to this one.
	If true, indicates that the two objects refer to the same native identifier.*/
	public boolean equals (Object anObject) {
		if (this == anObject)
			return true;
		if ((anObject != null) && (anObject instanceof ComponentDescription)) {
			CAMemoryObject other = ((ComponentDescription)anObject).desc;
			for (int i = 0; i < kNativeSize; i += 4) {
				if (desc.getIntAt (i) != other.getIntAt (i))
					return false;
			}
			return true;
		}
		return false;
	}
	

 //_ NATIVE METHODS
	private native static int CountComponents (int compDescPtr);
 	private native static int FindNextComponent (int comp, int compDescPtr);
}

/*
 */
