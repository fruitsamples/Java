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
//  Component.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//

package com.apple.component;

import com.apple.audio.util.CAUtils;
import com.apple.audio.util.CAMemoryObject;
import com.apple.audio.CAObject;

public class Component {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}

//_________________________ STATIC FIELDS
		/** Set Default Component flags */
	public static final int
		defaultComponentIdentical	= 0,
		defaultComponentAnyFlags	= 1,
		defaultComponentAnyManufacturer = 2,
		defaultComponentAnySubType	= 4,
		defaultComponentAnyFlagsAnyManufacturer = (defaultComponentAnyFlags + defaultComponentAnyManufacturer),
		defaultComponentAnyFlagsAnyManufacturerAnySubType = (defaultComponentAnyFlags + defaultComponentAnyManufacturer + defaultComponentAnySubType);

//_________________________ STATIC METHODS
	/**
	 * Determine the next Component as specified. Will return null if it cannot find a
	 * component that matches the specifications.
	 * <BR><BR><b>AppleDocs::FindNextComponent</b><BR>
	 * @param desc describes the search parameters.
	 * @return a component identifier or null if none found.
	 */
	public static final Component find (ComponentDescription desc) {
		int res = FindNextComponent(0, desc._ID());
		return (res	!= 0 ? new Component (res) : null);
	}
	
	// used by subclasses to create the superclass
	protected Component (Component comp) {
		this.compID = comp.compID;
	}

	protected Component (int comp) {
		this.compID = comp;
	}
	
	/**
	 * Returns all of the registration information for a component. 
	 * <BR><BR><b>AppleDocs::GetComponentInfo</b><BR>
	 * @return information about the specified component.
	 */
	final static void fillInInfo (int _ID_, ComponentDescription desc) {
		int h1 = CompInfoSupport.NewHandle(4);
		int h2 = CompInfoSupport.NewHandle(4);
		int res = GetComponentInfo (_ID_, desc._ID(), h1, h2, 0);
		if (res != 0)
			throw new IllegalArgumentException ("Error from GetComponentInfo:" + res);

		CompInfoSupport.setStrings (desc, h1, h2);
	}
	
//_________________________ INSTANCE FIELDS
	protected final int compID;
	
	private ComponentDescription cacheInfo;
	private boolean doneInfo = false;
	
//_________________________ INSTANCE METHODS
	/** Returns the native address that contains the memory of this object*/
	public final int _ID () {
		return compID;
	}

	/**
	 * Determine the next Component as specified. Will return null if it cannot find a
	 * component that matches the specifications. It uses this Component as its starting point.
	 * <BR><BR><b>AppleDocs::FindNextComponent</b><BR>
	 * @param desc describes the search parameters.
	 * @return a component or null if none found.
	 */
	public final Component findNext (ComponentDescription desc) {
		int res = FindNextComponent (_ID(), desc._ID());		
		return (res	!= 0 ? new Component (res) : null);
	}

	/**
	 * Determines the current number of open connections managed by the Component.
	 * <BR><BR><b>AppleDocs::CountComponentInstances</b><BR>
	 * @return The number of component instances of the specified Component.
	 */
	public final int count () {
		return CountComponentInstances(_ID());
	}

	/**
	 * Returns all of the registration information for a component. 
	 * <BR><BR><b>AppleDocs::GetComponentInfo</b><BR>
	 * @return information about the specified component.
	 */
	public final ComponentDescription getInfo () {
		if (doneInfo == false) {
			if (cacheInfo == null)
				cacheInfo = new ComponentDescription();
			fillInInfo (_ID(), cacheInfo);
			doneInfo = true;
		}
		return cacheInfo;
	}

	/**
	 * Returns the type of the component. 
	 * <BR><BR><b>AppleDocs::GetComponentInfo</b><BR>
	 * @return information about the specified component.
	 */
	public final int getType () {
		if (cacheInfo == null) {
			cacheInfo = new ComponentDescription();
			int res = GetComponentInfo (_ID(), cacheInfo._ID(), 0, 0, 0);
		}
		return cacheInfo.getType();
	}
	
	/**
	 * Returns the type of the component. 
	 * <BR><BR><b>AppleDocs::GetComponentInfo</b><BR>
	 * @return information about the specified component.
	 */
	public final int getSubType () {
		if (cacheInfo == null) {
			cacheInfo = new ComponentDescription();
			int res = GetComponentInfo (_ID(), cacheInfo._ID(), 0, 0, 0);
		}
		return cacheInfo.getSubType();
	}

	/**
	 * Returns the type of the component. 
	 * <BR><BR><b>AppleDocs::GetComponentInfo</b><BR>
	 * @return information about the specified component.
	 */
	public final int getManufacturer () {
		if (cacheInfo == null) {
			cacheInfo = new ComponentDescription();
			int res = GetComponentInfo (_ID(), cacheInfo._ID(), 0, 0, 0);
		}
		return cacheInfo.getManufacturer();
	}

	/**
	 * The SetDefaultComponent function changes the search order of registered components 
	 * by moving the specified component to the front of the search chain,
	 * according to the value specified in the flags parameter.
	 * <BR><BR><b>AppleDocs::OpenDefaultComponent</b>
	 * @param flags the flags
 	 */
 	public void setDefault (int flags) {
 		short res = SetDefaultComponent (_ID(), (short)flags);
		if (res != 0)
			throw new IllegalArgumentException ("Error from GetComponentInfo:" + res);
	}
	
	/** @return a String representation of this class */ 
	public String toString() {
		return getClass().getName() +
			 	"[type=" + CAUtils.fromOSType(getType()) + 
				",subType=" + CAUtils.fromOSType(getSubType()) + 
				",manufacturer=" + CAUtils.fromOSType(getManufacturer()) + "]";
	}

 //_ NATIVE METHODS
 	private native static int FindNextComponent (int comp, int compDescPtr);
	private static native int CountComponentInstances(int component); 
	private static native short GetComponentInfo (int comp, int compDescPtr, int hName, int hInfoStr, int hIcon);
	private static native short SetDefaultComponent (int aComponent, short flags);
}

/*
 */
