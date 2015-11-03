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
//  AudioStream.java
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
 * Audio Streams are the gate keepers of format information. Each Audio Stream
 * on an Audio Device may have it's own format. Further, changes to the format
 * of one Audio Stream can and often do affect the format of the other Audio Streams
 * on the Audio Device. This is particularly true of the sample rate aspect of the
 * stream's format, as all streams attached to a device share the same sample rate.
 * It is possible for Audio Streams to provide and consume data in any format
 * including compressed formats. The format proprerties specify the basic format of
 * the data. It can be further specified by other properties such as the compression
 * description property.
 * <P>
 * Note that if an Audio Stream presents its format as linear PCM, it will always
 * present its data as 32 bit floating point data. Any necessary conversion to the
 * actual physical hardware format (such as 16 or 24 bit integer) are handled by the
 * driver in order to preserve the headroom of the device's mix bus.
 * <P>
 * <B>Stream Property Management</B>
 * <P>
 * When getting and setting a stream's properties, it is necessary to always
 * specify exactly which part of the stream to interrogate. The channel is specified
 * with an unsigned integer argument (generally called inChannel) where 0 means
 * the master channel and greater than zero refers to the Nth indexed channel
 * starting with index 1.
 */
public final class AudioStream extends CAObject {	
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}

	AudioStream (int id, Object owner, boolean isOutFlag) {
		super (id, owner);
		this.isOutputFlag = isOutFlag;
	}
	
	private boolean isOutputFlag;
	private CAMemoryObject outSizeA = new CAMemoryObject (8, false);
	private CAMemoryObject outSizeB = new CAMemoryObject (8, false);
	private StreamProcTable table = new StreamProcTable (this);

	/** Returns true if the stream is an output stream, false if an input stream*/
	public boolean isOutput () {
		return isOutputFlag;
	}
	
	/** Returns the Device that own's this stream*/
	public AudioDevice getOwningDevice () throws CAException {
		synchronized (outSizeA) {
			outSizeA.setIntAt (0, 4);
			int res = AudioStreamGetProperty(_ID(), 0, AHConstants.kAudioStreamPropertyOwningDevice, CAObject.ID(outSizeA), CAObject.ID(outSizeB));
			CAException.checkError(res);
			return AudioDevice.makeDevice (outSizeB.getIntAt(0));
		}
	}

	/**
	 * Retrieve information about the given property on the given
	 * channel. 
 	 * <BR><BR><b>CoreAudio::AudioStreamGetPropertyInfo</b><BR><BR>
	 * @param inChannel the channel 
	 * @param inPropertyID the property to retrieve
	 * @return true if the property is writable
	 */
	public boolean getPropertyInfoWritable (int inChannel, int inPropertyID) throws CAException {
		synchronized (outSizeA) {
			outSizeA.setIntAt (0, 4);
			int res = AudioStreamGetPropertyInfo(_ID(), inChannel, inPropertyID, CAObject.ID(outSizeA), CAObject.ID(outSizeB));
			CAException.checkError (res);
			return outSizeB.getByteAt (0) != 0;
		}
	}

	/**
	 * Retrieve information about the given property on the given
	 * channel.
 	 * <BR><BR><b>CoreAudio::AudioStreamGetPropertyInfo</b><BR><BR>
	 * @param inChannel the channel 
	 * @param inPropertyID the property to retrieve
	 * @return the required size to retrieve that property's value
 	 */
	public int getPropertyInfoSize (int inChannel, int inPropertyID) throws CAException {
		synchronized (outSizeA) {
			outSizeA.setIntAt (0, 4);
			int res = AudioStreamGetPropertyInfo(_ID(), inChannel, inPropertyID, CAObject.ID(outSizeA), CAObject.ID(outSizeB));
			CAException.checkError (res);
			return outSizeA.getIntAt (0);
		}
	}

	/**
	 * Retrieve the indicated property data for the given channel. A property
	 * is specified as an ID and a channel number. The channel number allows for
	 * access to properties on the channel level. On input, ioDataSize has the size
	 * of the data pointed to by outPropertyData. On output, it will contain
	 * the amount written. If outPropertydata is NULL and ioPropertyDataSize is
	 * not, the amount that would have been written will be reported.
 	 * <BR><BR><b>CoreAudio::AudioStreamGetProperty</b><BR><BR>
	 * @param inChannel the channel 
	 * @param inPropertyID the property to retrieve
	 * @param outPropertyData this should be large enough to contain the requested property
	 * @return the number of actual bytes written to the outPropertyData object
	 */
	public int getProperty (int inChannel, int inPropertyID, CAMemoryObject outPropertyData) throws CAException {
		synchronized (outSizeA) {
			outSizeA.setIntAt (0, outPropertyData.getSize());
			int res = AudioStreamGetProperty(_ID(), inChannel, inPropertyID, CAObject.ID(outSizeA), CAObject.ID(outPropertyData));
			CAException.checkError(res);
			return outSizeA.getIntAt(0);
		}
	}

	/**
	 * Set the indicated property data for the given channel. 
	 * <BR><BR><b>CoreAudio::AudioStreamSetProperty</b><BR><BR>
	 * @param inChannel the channel 
	 * @param inPropertyID the property to retrieve
	 * @param outPropertyData this should be large enough to contain the requested property
	 * @return the number of actual bytes written to the outPropertyData object
	 */
	public void setProperty (AudioTimeStamp inWhen, int inChannel, int inPropertyID, CAMemoryObject inPropertyData) throws CAException {
		int res = AudioStreamSetProperty(_ID(),
						CAObject.ID(inWhen),
						inChannel,
						inPropertyID,
						inPropertyData.getSize(),
						CAObject.ID(inPropertyData));
		CAException.checkError (res);
	}
	
															
	/**
	 * Set up a routine that gets called when the property of a stream is changed.
	 * <BR><BR><b>CoreAudio::AudioStreamAddPropertyListener</b><BR><BR>
	 * @param inChannel the channel you wish to receive notifications of changes
	 * @param inPropertyID the property of that channel you want to receive notifications of changes
	 * @param listener the listener's execute method is called when the specified property changes
	 */
	public synchronized void addPropertyListener (int inChannel, int inPropertyID, AStreamPropertyListener listener) throws CAException {
		if (table.contains (inPropertyID, listener, inChannel))
			throw new CAException ("Cannot add the same listener:" + listener + " to the same stream's property:" + Integer.toHexString (inPropertyID) + " and channel:" + inChannel);
			
		HardwareDispatcher disp = new HardwareDispatcher (this, listener);
		int res = AudioStreamAddPropertyListener(_ID(),
							inChannel,
							inPropertyID,
							disp.ID(),
							0);
		if (res != 0) {
			disp.cleanup();
			CAException.checkError (res);
			return;
		}
		table.add (inPropertyID, listener, inChannel, disp);

	}
	
	/**
	 * Remove a previously installed property listener.
	 * <BR><BR><b>CoreAudio::AudioStreamRemovePropertyListener</b><BR><BR>
	 * @param inChannel the channel you wish to remove notifications of changes
	 * @param inPropertyID the property of that channel you want to remove notifications of changes
	 * @param listener the listener's execute method is called when the specified property changes
	 */
	public synchronized void removePropertyListener (int inChannel, int inPropertyID, AStreamPropertyListener listener) throws CAException {
		if (table.contains (inPropertyID, listener, inChannel) == false)
			throw new CAException ("Cannot remove the listener:" + listener + " on the stream's property:" + Integer.toHexString (inPropertyID) + " and channel:" + inChannel + ". It hasn't been added");
		
		HardwareDispatcher toBeRemoved = table.remove (inPropertyID, listener, inChannel);		
		removePropertyListener (inChannel, inPropertyID, listener, toBeRemoved);
	}

	void removePropertyListener (int inChannel, int inPropertyID, AStreamPropertyListener listener, HardwareDispatcher disp) throws CAException {
		int res = AudioStreamRemovePropertyListener(_ID(),
									inChannel,
									inPropertyID,
									disp.ID());
		disp.cleanup();		
		CAException.checkError (res);
	}
	
	/**
	 * This call will remove all of the registered property listeners for this stream.
	 */
	public synchronized void removeAllListeners () {
		table.removeAll();
	}

//_ NATIVE METHODS
	private static native int AudioStreamGetPropertyInfo (int inStream,
								int inChannel,
								int inPropertyID,
								int outSize,
								int outWritable);
	private static native int AudioStreamGetProperty(int inStream,
								int inChannel,
								int inPropertyID,
								int ioPropertyDataSize,
								int outPropertyData);
	private static native int AudioStreamSetProperty(int inStream,
								int inWhen,
								int inChannel,
								int inPropertyID,
								int inPropertyDataSize,
								int inPropertyData);
	private static native int AudioStreamAddPropertyListener(int inStream,
								int inChannel,
								int inPropertyID,
								int inProc,
								int inClientData);
	private static native int AudioStreamRemovePropertyListener(int inStream,
								int inChannel,
								int inPropertyID,
								int inProc);
}

/*
 */
