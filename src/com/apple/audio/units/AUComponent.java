/*	Copyright: 	© Copyright 2003 Apple Computer, Inc. All rights reserved.

	Disclaimer:	IMPORTANT:  This Apple software is supplied to you by Apple Computer, Inc.
			("Apple") in consideration of your agreement to the following terms, and your
			use, installation, modification or redistribution of this Apple software
			constitutes acceptance of these terms.  If you do not agree with these terms,
			please do not use, install, modify or redistribute this Apple software.

			In consideration of your agreement to abide by the following terms, and subject
			to these terms, Apple grants you a personal, non-exclusive license, under AppleÕs
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
//  AUComponent.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.units;

import com.apple.component.*;
import com.apple.audio.*;
import com.apple.audio.jdirect.*;
import com.apple.audio.util.*;
import java.util.*;
import com.apple.audio.jdirect.Accessor;

public final class AUComponent extends Component {
	static {
		System.load (CASession.caBundleName);
	}
	// synchronized around firstOutArg_4 -> some methods use both arg caches, 
	// some use just _4, some just _8
	static final Object syncObject = new Object();

	static final int firstArg4Ptr = JNIUnits.malloc (4);
	static final int secondArgPtr = JNIUnits.malloc (20);
	
//_________________________ STATIC METHODS
	/**
	 * Determine the next AUComponent as specified. Will return null if it cannot find a
	 * component that matches the specifications. 
	 * The type of the ComponentDescription is set to <CODE>AUConstants.kAudioUnitComponentType</CODE> iff all of the type, subtype and manu fields are not
	 * specified. Thus - in that case, it will find audio units of a v1 'aunt' type.
	 * If the type is specified, no adjustment is made to the component description.
	 * <BR><BR><b>QuickTime::FindNextComponent</b><BR>
	 * @param desc describes the search parameters.
	 * @return a component identifier or null if none found.
	 */
	public static final AUComponent findAU (ComponentDescription desc) {
		if (desc.getType() == 0
			&& desc.getSubType() == 0 
			&& desc.getManufacturer() == 0)
		{
			desc.setType (AUConstants.kAudioUnitComponentType);
		}
		Component res = find (desc);
		return (res != null ? new AUComponent (res) : null);
	}

	/*
	 * Opens the specified component. This is a factory method that will return a class
	 * known to this system that best represents the component. This can be used if you 
	 * know the subType of the component, but do not know the class that represents that.
	 * <BR><BR><b>CoreAudio::OpenDefaultComponent</b>
	 * @return the opened ComponentInstance or null if unable to open the specified instance.
	 */
	public static final AudioUnit openDefault (int subType) {
		int inst = OpenDefaultComponent (AUConstants.kAudioUnitComponentType, subType);
		if (inst == 0)
			throw new RuntimeException ("Can't find a class to represent a component of type:" + AUConstants.kAudioUnitComponentType + ",subType:" + subType);
		return findClassForComponentInstance (inst, null);
	}	
	
	/*
	 * Opens the default output audio unit. This audio unit will track the default output
	 * device that the users sets in the Sound Control Panel, so applications can always have
	 * their audio directed to the device that the user has selected.
	 * <BR><BR><b>CoreAudio::OpenDefaultAudioOutput</b>
	 * @return the opened ComponentInstance or null if unable to open the specified instance.
	 */
	public static final AudioDeviceOutputUnit openDefaultOutput () {
		int ptr = 0;
		synchronized (syncObject) {
			OpenDefaultAudioOutput (AUComponent.firstArg4Ptr);
			ptr = Accessor.getIntFromPointer (AUComponent.firstArg4Ptr, 0);
		}	
		return (AudioDeviceOutputUnit)findClassForComponentInstance (
									ptr, 
									null);
	}

	/*
	 * Opens the default System sound audio unit. This audio unit will track the system sounds
	 * device that the users sets in the Sound Control Panel, so applications can always have
	 * their audio directed to the device that the user has selected. This is typically used for
	 * alerts sounds, modem sounds, etc.
	 * <BR><BR><b>CoreAudio::OpenSystemSoundAudioOutput</b>
	 * @return the opened ComponentInstance or null if unable to open the specified instance.
	 */
	public static final AudioDeviceOutputUnit openSystemSoundOutput () {
		int ptr = 0;
		synchronized (syncObject) {
			OpenSystemSoundAudioOutput (AUComponent.firstArg4Ptr);
			ptr = Accessor.getIntFromPointer (AUComponent.firstArg4Ptr, 0);
		}
		return (AudioDeviceOutputUnit)findClassForComponentInstance (
									ptr, 
									null);
	}
	
	/**
	 * This is the <B>ONLY</B> means by which an AudioUnit should be constructed.
	 * @param theInst this is the native identifier that is returned from some native call
	 * @param owner - determine if the Java object that is created by this call should close
	 * the opened component when it is disposed of (or finalized). This is entriely context dependant.
	 * <P>
	 * For example if the Java AudioUnit object is created as a result of a call to open or openDefault
	 * then that java object is the owner of the component instance that is opened by those calls, and
	 * thus should close the component when disposed. In that case the owner is null.
	 * <P>
	 * For example if the Java AudioUnit is created as a result of
	 * a call to AUGraph.getNodeInfo_AudioUnit, then the owner of the AudioUnit instance is actually
	 * the AUGraph object. In that case the Java AudioUnit object should not close the component instance
	 * when it is finalized. So, the owner would be the AUGraph object
	 */
	static AudioUnit findClassForComponentInstance (int theInst, Object owner) {
		int componentType = 0;
		int subType = 0;
		int IDType = 0;
		synchronized (syncObject) {
			int res = GetComponentInfo (theInst, secondArgPtr, 0, 0, 0);
			if (res != 0)
				throw new RuntimeException ("Component Native Identifier is not a valid component");
			componentType = Accessor.getIntFromPointer (secondArgPtr, 0);
			subType = Accessor.getIntFromPointer (secondArgPtr, 4);
			IDType = Accessor.getIntFromPointer (secondArgPtr, 8);
		}
		switch (componentType)
		{
		case AUConstants.kAudioUnitComponentType:
			{
				if (subType == AUConstants.kAudioUnitSubType_MusicDevice)
					return new MusicDevice (theInst, owner);
				else if (subType == AUConstants.kAudioUnitSubType_Output) {
					if (AUConstants.kAudioUnitID_HALOutput == IDType
						|| AUConstants.kAudioUnitID_DefaultOutput == IDType
						|| AUConstants.kAudioUnitID_SystemOutput == IDType)
						return new AudioDeviceOutputUnit (theInst, owner);
					else
						return new OutputAudioUnit (theInst, owner);
				} else
					return new AudioUnit (theInst, owner);
	
			}

		case AUConstants.kAudioUnitType_Output:
			{
				if (AUConstants.kAudioUnitSubType_HALOutput == subType
					|| AUConstants.kAudioUnitSubType_DefaultOutput == subType
					|| AUConstants.kAudioUnitSubType_SystemOutput == subType)
						return new AudioDeviceOutputUnit (theInst, owner);
				else
						return new OutputAudioUnit (theInst, owner);
			}
		
		case AUConstants.kAudioUnitType_MusicDevice:
			return new MusicDevice (theInst, owner);
		
		case AUConstants.kAudioUnitType_FormatConverter:
		case AUConstants.kAudioUnitType_Effect:
		case AUConstants.kAudioUnitType_Mixer:
			return new AudioUnit (theInst, owner);
		
		default:
				throw new RuntimeException ("Component Type must be:'" 
									+ CAUtils.fromOSType (AUConstants.kAudioUnitComponentType) 
									+ "', whereas it is:'" 
									+ CAUtils.fromOSType(Accessor.getIntFromPointer (secondArgPtr, 0)) 
									+ "'");
		
		}
	}
	
//_________________________ CONSTRUCTORS
	private AUComponent (Component comp) {
		super (comp);
	}
	
		//used by AudioUnit
	AUComponent (int comp) {
		super (comp);
	}
	
//_________________________ INSTANCE METHODS
	/**
	 * Determine the next Component as specified. Will return null if it cannot find a
	 * component that matches the specifications.
	 * The type of the ComponentDescription is set to <CODE>AUConstants.kAudioUnitComponentType</CODE>
	 * <BR><BR><b>QuickTime::FindNextComponent</b><BR>
	 * @param desc describes the search parameters.
	 * @return a component identifier or null if none found.
	 */
	public final AUComponent findNextAU (ComponentDescription desc) {
		Component ret = findNext (desc);
		return (ret != null ? new AUComponent (ret) : null);
	}

	/**
	 * Given a component identifier it opens the specified component.
	 * <P>
	 * Recommend using the open() call as it can throw an exception if there is an error encountered
	 * when the component is opened.
	 * <BR><BR><b>CoreAudio::OpenComponent</b>
	 * @return the opened ComponentInstance
	 * @exception RuntimeException if unable to open the component
	 */
	public AudioUnit openAU () {
		int inst = OpenComponent(_ID());
		if (inst == 0)
			throw new RuntimeException ("Can't open component:" + this);
		return findClassForComponentInstance (inst, null);
	}
	
	/**
	 * Given a component identifier it opens the specified component.
	 * <BR><BR><b>CoreAudio::OpenAComponent</b>
	 * @return the opened ComponentInstance
	 */
	public AudioUnit open () throws CAException {
		int inst = 0;
		synchronized (syncObject) {
			int res = OpenAComponent (_ID(), firstArg4Ptr);
			CAException.checkError (res);
			inst = Accessor.getIntFromPointer (AUComponent.firstArg4Ptr, 0);
		}
		return findClassForComponentInstance (inst, null);
	}
	
//_ NATIVE METHODS
	private static native short OpenAComponent(int comp, int ciPtr);
	private static native int OpenDefaultComponent(int componentType, int componentSubType);
	private static native int OpenComponent(int aComponent);	
	private static native short GetComponentInfo (int comp, int compDescPtr, int hName, int hInfoStr, int hIcon);
	private static native int OpenDefaultAudioOutput (int outUnitPtr);
	private static native int OpenSystemSoundAudioOutput(int outUnitPtr);
}

/*
 */
 
 
// this is something like the mechanism for dealing with auto registration of Java classes->AU type/subtype
 // could add a manu field for more specific discrimination of specific app classes
/*	private static Hashtable auClassNames = new Hashtable();
	private synchronized static AudioUnit findClassForComponentInstance (int theInst) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		int res = GetComponentInfo (theInst, CAObject.ID(tempInfo), 0, 0, 0);
		if (tempInfo.getIntAt(0) != AUConstants.kAudioUnitComponentType)
			throw new RuntimeException ("Component Type must be:'" + CAUtils.fromOSType (AUConstants.kAudioUnitComponentType) + "', whereas it is:'" + CAUtils.fromOSType(tempInfo.getIntAt(0)) + "'");

		Integer key = new Integer (tempInfo.getIntAt(4));
		Object value = auClassNames.get (key);
		if (value == null)
			throw new RuntimeException ("Can't find a class to represent a component of type:'" + CAUtils.fromOSType (tempInfo.getIntAt(0)) + "',subType:" + CAUtils.fromOSType (tempInfo.getIntAt(4)) + "'");
		String className = (String)value;
		Class c = Class.forName (className);
		
		AudioUnit obj = (AudioUnit)c.newInstance();
		obj._setNR (theInst);
		return obj;
	}
*/	

