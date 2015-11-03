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
//  CASession.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio;

import java.util.*;
import com.apple.audio.jdirect.*;

// Process for adding class disposal
// (1) add name of class to class constants
// (2) add to add method
// (3) add to disposeSessionObject
// (4) add disposal native call
// (5) Some information may be stored at the end of these class name strings so MUST use startsWith when comparing NOT equals

final class CAObjectManagement {
//_________________________ INITIALIZATION
	static {
		System.load (CASession.caBundleName);
	}
		
		// can't instantiate
	private CAObjectManagement () {}
	
//_________________________ MEMORY MANAGEMENT
//_________________________ CLASS VARIABLES
	private static final boolean debug = false;
	private static final boolean addDebug = false;
	private static final boolean addDebugCallStack = false;
	private static final boolean removeDebug = false;
	private static int addDebugCount = 0;
	private static int removeDebugCount = 0;
	
	private static Hashtable caObjs;
	
	private static final String AudioConverterName = "AudioConverter";
	private static final String AudioUnitName = "AudioUnit";
	private static final String AUGraphName = "AUGraph";
	private static final String AUListenerName = "AUListener";
	private static final String AUMIDIControllerName = "AUMIDIController";
	private static final String CAMemoryObjectName = "CAMemoryObject";
	private static final String CFReleaseName = "CFRelease";
	private static final String MIDIClientName = "MIDIClient";
	private static final String MIDIEndpointName = "MIDIEndpoint";
	private static final String MIDIPortName = "MIDIPort";
	private static final String MIDISetupName = "MIDISetup";
	private static final String MusicEventIteratorName = "MusicEventIterator";
	private static final String MusicPlayerName = "MusicPlayer";
	private static final String MusicSequenceName = "MusicSequence";
	
//_________________________ CLASS METHODS
		// returns true if the supplied address is still a member of this
		// session - ie. it hasn't been disposed	
	static boolean isMember (int id) {
		if (caObjs == null) return false;
		Integer key = new Integer(id);
		return caObjs.containsKey (key);
	}

// actually it adds the nativeID - which is the native QTObject to the internal array
	static boolean add (CAObject obj, int nativeID, int ownerID) {
			String addDebugStr = null;
			if (addDebug) {
				addDebugStr = "ADD:CHECK_Session:" + obj.getClass().getName() + ",0x" + Integer.toHexString (nativeID);
			}
			
		if (obj == null || nativeID == 0) {
			if (addDebug) {
				System.out.println (addDebugStr + ":FAIL" + obj.getClass().getName() + ",0x:" + Integer.toHexString (nativeID));
				if (addDebugCallStack)
					new Exception("* * * Diagnostic Stack Trace * * *").printStackTrace();
			}
			
			return false;
		}
		
		if (caObjs == null) 
			caObjs = new Hashtable();

		if (caObjs.containsKey (new Integer (nativeID))) {
				if (addDebug) {
					System.out.println (addDebugStr + ":FAIL-Duplicate Address:0x" + nativeID + "," + obj.getClass().getName());
					if (addDebugCallStack)
						new Exception("* * * Diagnostic Stack Trace * * *").printStackTrace();
				}
				
			return false;	//we already are in this table
		}

//we store the name of the object so we know what to use when we dispose of it
//that way we also do not hold onto it so the gc will be fine
		String className = null;
		
//className is just the name of the class (no packages)
		if (obj instanceof com.apple.audio.util.CAMemoryObject) {
				className = CAMemoryObjectName;
		} else if (obj instanceof com.apple.audio.CAObject) {	
			if (obj instanceof com.apple.audio.units.AudioUnit) 
				className = AudioUnitName;
			else if (obj instanceof com.apple.audio.toolbox.AUGraph) 
				className = AUGraphName;
			else if (obj instanceof com.apple.audio.toolbox.MusicPlayer) 
				className = MusicPlayerName;
			else if (obj instanceof com.apple.audio.toolbox.MusicSequence) 
				className = MusicSequenceName;
			else if (obj instanceof com.apple.audio.toolbox.MusicEventIterator) 
				className = MusicEventIteratorName;
			else if (obj instanceof com.apple.audio.util.CAFString) 
				className = CFReleaseName;
			else if (obj instanceof com.apple.audio.util.CAFData) 
				className = CFReleaseName;
			else if (obj instanceof com.apple.audio.midi.MIDIClient) 
				className = MIDIClientName;
			else if (obj instanceof com.apple.audio.midi.MIDIEndpoint) 
				className = MIDIEndpointName;
			else if (obj instanceof com.apple.audio.midi.MIDISetup) 
				className = MIDISetupName;
			else if (obj instanceof com.apple.audio.midi.MIDIPort)
				className = MIDIPortName;
			else if (obj instanceof com.apple.audio.toolbox.AudioConverter)
				className = AudioConverterName;
			else if (obj instanceof com.apple.audio.toolbox.AUListener)
				className = AUListenerName;
			else if (obj instanceof com.apple.audio.toolbox.AUMIDIController)
				className = AUMIDIControllerName;
			else {
					if (addDebug) {
						System.out.println (addDebugStr + ":FAIL:DON'T KNOW HOW TO DISPOSE:" + className);
						if (addDebugCallStack)
							new Exception("* * * Diagnostic Stack Trace * * *").printStackTrace();
					}
				return false;
			}
		} else {
				if (addDebug) {
					System.out.println (addDebugStr + ":FAIL:DON'T KNOW HOW TO DISPOSE:" + className);
					if (addDebugCallStack)
						new Exception("* * * Diagnostic Stack Trace * * *").printStackTrace();
				}
			return false;
		}
		
// if we need to have another object to dipose of this on
// then we store it after the name of the class
		if (ownerID != 0)
			className += "_" + Integer.toString(ownerID);

// add it to the table
		caObjs.put (new Integer(nativeID), className);	

			if (debug)
				addDebugCount++;

			if (addDebug) {
				System.out.println (addDebugStr + "==" + className + ":addCount=" + addDebugCount + ",removeCount=" + removeDebugCount);
				if (addDebugCallStack)
					new Exception("* * * Diagnostic Stack Trace * * *").printStackTrace();
			}
			
		return true;
	}

// this guy removes the native CAObject from our internal array of objects to dispose of when finalized
	static boolean remove (int nativeID) throws CAException {
			String removeDebugStr = null;
			if (removeDebug) 
				removeDebugStr = "REMOVE:CHECK_Session:0x" + Integer.toHexString (nativeID);
		
		Integer key = new Integer(nativeID);
		if (caObjs.containsKey (key)) {			
			try {
				disposeSessionObject (nativeID, (String)caObjs.get(key));	//this disposes of the session object
			} catch (CAException e) {
					if (removeDebug) 
						System.out.println (removeDebugStr + ":ERROR ON DISPOSE:0x" + Integer.toHexString(nativeID) + "," + e.getMessage());
				throw e;
			} finally {
				caObjs.remove (key);
			}
			return true;
		} 
			if (removeDebug) 
				System.out.println (removeDebugStr + ":DIDN'T FIND Address:0x" + Integer.toHexString(nativeID));
		
		return false;
	}
		
	private static void disposeSessionObject (int nativeID, String className) throws CAException {
			String str = null; 
			if (removeDebug) 
				str = " Dispose:" + className + "==";
		
		int err = 0;
		if (className.startsWith (AudioConverterName)) {
				if (removeDebug) 
					str += AudioConverterName + ":0x" + Integer.toHexString (nativeID);
			err = AudioConverterDispose(nativeID);
		} else if (className.startsWith (AudioUnitName)) {
				if (removeDebug) 
					str += AudioUnitName + ":0x" + Integer.toHexString (nativeID);
			err = CloseComponent (nativeID);
		} else if (className.startsWith (AUGraphName)) {
				if (removeDebug) 
					str += AUGraphName + ":0x" + Integer.toHexString (nativeID);
			err = DisposeAUGraph (nativeID);
		} else if (className.startsWith (AUListenerName)) {
				if (removeDebug) 
					str += AUListenerName + ":0x" + Integer.toHexString (nativeID);
			err = AUListenerDispose (nativeID);
		} else if (className.startsWith (AUMIDIControllerName)) {
				if (removeDebug) 
					str += AUMIDIControllerName + ":0x" + Integer.toHexString (nativeID);
			err = AUMIDIControllerDispose (nativeID);
		} else if (className.startsWith (CAMemoryObjectName)) {
				if (removeDebug) 
					str += CAMemoryObjectName + ":0x" + Integer.toHexString (nativeID);
			free (nativeID);
		} else if (className.startsWith (CFReleaseName)) {
				if (removeDebug) 
					str += CFReleaseName + ":0x" + Integer.toHexString (nativeID);
			CFRelease (nativeID);
		} else if (className.startsWith (MIDIClientName)) {
				if (removeDebug) 
					str += MIDISetupName + ":0x" + Integer.toHexString (nativeID);
			err = MIDIClientDispose (nativeID);
		} else if (className.startsWith (MIDIEndpointName)) {
				if (removeDebug) 
					str += MIDIEndpointName + ":0x" + Integer.toHexString (nativeID);
			err = MIDIEndpointDispose (nativeID);
		} else if (className.startsWith (MIDIPortName)) {
				if (removeDebug) 
					str += MIDIPortName + ":0x" + Integer.toHexString (nativeID);
			err = MIDIPortDispose (nativeID);
		} else if (className.startsWith (MIDISetupName)) {
				if (removeDebug) 
					str += MIDISetupName + ":0x" + Integer.toHexString (nativeID);
			err = MIDISetupDispose (nativeID);
		} else if (className.startsWith (MusicEventIteratorName)) {
				if (removeDebug) 
					str += MusicEventIteratorName + ":0x" + Integer.toHexString (nativeID);
			err = DisposeMusicEventIterator (nativeID);
		} else if (className.startsWith (MusicPlayerName)) {
				if (removeDebug) 
					str += MusicPlayerName + ":0x" + Integer.toHexString (nativeID);
			err = DisposeMusicPlayer (nativeID);
		} else if (className.startsWith (MusicSequenceName)) {
				if (removeDebug) 
					str += MusicSequenceName + ":0x" + Integer.toHexString (nativeID);
			err = DisposeMusicSequence (nativeID);
		} else {
			if (removeDebug) 
				System.out.println ("UNKNOWN CLASSS to dispose:" + className);
			if (debug) // do this here because after the else it adds to it by default
				removeDebugCount--;
		}
			if (debug)
				removeDebugCount++;

			if (removeDebug) 
				System.out.println (str + ":addCount=" + addDebugCount + ",removeCount=" + removeDebugCount);
			
		CAException.checkError (err);
	}
	
//_ NATIVE METHODS
	private static native void free (int voidPointer);
	private static native short CloseComponent (int comp);
	private static native int DisposeAUGraph (int inGraph);
	private static native int DisposeMusicPlayer(int inPlayer);
	private static native int DisposeMusicSequence(int inSequence);
	private static native int DisposeMusicEventIterator(int inIterator);
	private static native void CFRelease(int cf);
	private static native int MIDISetupDispose (int setup);
	private static native int MIDIClientDispose (int client);
	private static native int MIDIEndpointDispose (int endpoint);
	private static native int MIDIPortDispose (int port);
	private static native int AudioConverterDispose(int	inAudioConverter);
	private static native int AUListenerDispose (int inListener);
	private static native int AUMIDIControllerDispose(int inController);
}
/*
	private static int pullIDFromName (String className) {
		int ind = className.lastIndexOf ("_");
		if (ind != -1) {
			String sstr = className.substring (ind+1);
			return new Integer (sstr).intValue();
		} 
		
		return 0;
	}
	} else if (className.startsWith (nc)) {
			if (removeDebug) str += nc + ":0x" + Integer.toHexString (nativeID);
		int ownerID = pullIDFromName (className);
		if (ownerID != 0)
			err = NADisposeNoteChannel (ownerID, nativeID);

	}
*/

/*
 */


