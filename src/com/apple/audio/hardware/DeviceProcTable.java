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
//  DeviceProcTable.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.hardware;

import java.util.*;
import com.apple.audio.*;
// ASSUME that all read/write access to these table are guarded by caller synchronization
final class DeviceProcTable {
	DeviceProcTable (AudioDevice dev) {
		this.device = dev;
	}
	
	private AudioDevice device;
	
/*
[IO Table
	[ioProc1 -> Dispatcher1]
	[ioProc2 -> Dispatcher2]
]
*/
	private final Hashtable ioProcTable = new Hashtable();

/*
Unique Properties
	have Unique Channel Listeners
		has a Listener
			has a Unique Dispatcher

BUT same listener can be registered for different properties or different channels on same property

Assume per device per in/out a sparse ann thinly populated vector of nodes
	-> no sorting
	
inOrOutTable
	[[Property-channel-listener]->dispatcher]
*/
	private final Vector listenerInputTable = new Vector();
	private final Vector listenerOutputTable = new Vector();
	private PropListNode searchNode = new PropListNode ();

	private void ifNoProcsRemoveFromDeviceTable () {
		if (ioProcTable.isEmpty() && listenerInputTable.isEmpty() && listenerOutputTable.isEmpty()) {
				AudioDevice.deviceTable.remove (device);
				device = null;
		}
	}
	
	boolean contains (AudioDeviceIOProc ioProc) {
		return ioProcTable.containsKey (ioProc);
	}

	void put (AudioDeviceIOProc ioProc, HardwareDispatcher dispatcher) {
		ioProcTable.put (ioProc, dispatcher);
	}
	
	HardwareDispatcher remove (AudioDeviceIOProc ioProc) {
		Object value = ioProcTable.remove (ioProc);
		ifNoProcsRemoveFromDeviceTable ();
		return (value != null ? (HardwareDispatcher)value : null);
	}

	HardwareDispatcher get (AudioDeviceIOProc ioProc) {
		Object value = ioProcTable.get (ioProc);
		return (value != null ? (HardwareDispatcher)value : null);
	}
	
	void removeAllIOProcs () {
		Enumeration iter = ioProcTable.elements();
		while (iter.hasMoreElements()) {
			HardwareDispatcher disp = (HardwareDispatcher)iter.nextElement();
			device._stop (disp);
			device.removeIOProc (disp);
		}
		ioProcTable.clear();
		ifNoProcsRemoveFromDeviceTable();
	}
	
	void addInput (int propertyID, ADevicePropertyListener listener, int channel, HardwareDispatcher disp) {
		addListener (listenerInputTable, propertyID, listener, channel, disp);
	}
	
	void addOutput (int propertyID, ADevicePropertyListener listener, int channel, HardwareDispatcher disp) {
		addListener (listenerOutputTable, propertyID, listener, channel, disp);
	}
	
	boolean containsInput (int propertyID, ADevicePropertyListener listener, int channel) {
		return containsListener (listenerInputTable, propertyID, listener, channel);
	}
	
	boolean containsOutput (int propertyID, ADevicePropertyListener listener, int channel) {
		return containsListener (listenerOutputTable, propertyID, listener, channel);
	}
	
	HardwareDispatcher removeInput (int propertyID, ADevicePropertyListener listener, int channel) {
		return removeListener (listenerInputTable, propertyID, listener, channel);
	}

	void removeAllInputs () {
		removeAllListeners (listenerInputTable, AudioDevice.kInput);
	}
	
	HardwareDispatcher removeOutput (int propertyID, ADevicePropertyListener listener, int channel) {
		return removeListener (listenerOutputTable, propertyID, listener, channel);
	}
	
	void removeAllOutputs () {
		removeAllListeners (listenerOutputTable, AudioDevice.kOutput);
	}
	
	private boolean containsListener (Vector theTable, int propertyID, ADevicePropertyListener listener, int channel) {
		searchNode.property = propertyID;
		searchNode.channel = channel;
		searchNode.listener = listener;
		return theTable.contains (searchNode);
	}
	
	private void addListener (Vector theTable, int propertyID, ADevicePropertyListener listener, int channel, HardwareDispatcher disp) {
		searchNode.property = propertyID;
		searchNode.channel = channel;
		searchNode.listener = listener;
		if (theTable.contains (searchNode))
			return;
		
		theTable.addElement (new PropListNode (propertyID, channel, listener, disp));
	}

	private HardwareDispatcher removeListener (Vector theTable, int propertyID, ADevicePropertyListener listener, int channel) {
		searchNode.property = propertyID;
		searchNode.channel = channel;
		searchNode.listener = listener;
		
		int length = theTable.size();
		HardwareDispatcher ret = null;
		for (int i = 0; i < length; i++) {
			Object node = theTable.elementAt (i);
			if (searchNode.equals(node)) {
				theTable.removeElementAt (i);
				ret = ((PropListNode)node).dispatcher;
				break;
			}
		}
		ifNoProcsRemoveFromDeviceTable ();
		return ret;
	}

	private void removeAllListeners (Vector theTable, byte isInput) {
		while (theTable.isEmpty() == false) {
			PropListNode node = (PropListNode)theTable.elementAt (0);
			try {
				device.removePropertyListener (node.channel, isInput, node.property, node.listener, node.dispatcher);
			} catch (CAException ae) {
				//ignore
			}
		}
		ifNoProcsRemoveFromDeviceTable ();
	}
}

/*
 */
