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
//  AudioDevice.java
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
 * This class represents a single AudioDevice within the audio system.
 * <P>
 * The Audio Hardware API provides multiple clients simultaneous access to
 * all of the audio devices attached to the host, no matter how the
 * connection is made (PCI, USB, Firewire, etc). The goal of this API is to
 * provide as little overhead and as clean a signal path as possible.
 * <P>
 * All clients of this API are assumed to take the appropriate precautions
 * against things like page faults or threading priority issues where ever
 * appropriate.
 * <P>
 * The basis of this API is the Audio Device. It provides a unit of
 * encapsulation for IO, timing services and properties that describe and
 * control the device. Specifically, an Audio Device represents a single IO
 * cycle, a clock source based on it, and all the buffers synchronized to it.
 * <P>
 * An Audio Device is further broken down into Audio Streams. An Audio Stream
 * encapsulates the buffer of memory for transferring the audio data across
 * the user/kernel boundary. Like Audio Devices, the Audio Stream provides
 * properties that describe and control it. Audio Streams always have a single
 * direction, either input or output.
 * <P>
 * Audio Devices are addressed in the API by specifying whether the request is
 * for input or output and it's channel number. Channel 0 always represents the
 * "master" channel for a device. The actual channels of the device then use
 * a one-based indexing scheme and are numbered consecutively up to the total
 * number of channels for all the Audio Device's Audio Streams.
 * <P>
 * Audio Streams are addressed similarly, only they omit the direction as it is
 * implied in the nature of the stream. The channel numbers for each Audio Stream
 * in an Audio Device always start at 0 and are numbered consecutively up to the
 * total number of channels in that particular Audio Stream. It is important to
 * remember that what is channel 2 for an Audio Stream might not be channel 2 for
 * it's Audio Device, depending on the stream configuration of the device.
 * <P>
 * The IO cycle of an Audio Device presents the data for all it's Audio Streams,
 * input and output, in the same call out to the client. It also provides the 
 * timestamp of when the first sample frame of the input data was acquired as well
 * as the timestamp ofwhen the first sample frame of the output data will be
 * consumed. The size of the buffers used for transfer are specified per Audio Device.
 * <P>
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
 * The format related properties of Audio Devices simply vector the request to the
 * stream containing the requested channel and direction. Format requests for
 * channel 0 always map to the first Audio Stream.
 * <P>
 * An Audio Device should, but is not required to, support an arbitrary number of
 * clients. An error will be returned if a given device refuses to accept
 * another client, or the device is in "hog" mode. In hog mode, a device will
 * only allow one client at a time. This is intended to provide a client a way
 * of ensuring that it is getting all of a device's time. Hog mode is a "first
 * come, first served" service.
 * <P>
 * As previously stated, Audio Devices and Audio Streams have properties describe
 * that describe or control a some aspect of a their operation such as the current
 * format or its name. There are also global system properties such as the list of
 * available devices. Properties are represented by a unique ID and have conventions
 * about the kind of data they use for a value. Changes to a property's value can be
 * scheduled to happen in real time (if the device supports it) or they can happen
 * immediately. Clients can sign up to be notified when a property's value changes.
 * <P>
 * Whenever application code adds an IOProc to the device or a device listener
 * the code must also remove that IOProc and/or listeners when the application
 * is finished with that device.
 * <P>
 * <B>Device Property Management</B>
 * <P>
 * Devices gave two sections, an input section and an output section. Each
 * section is broken down into a number of channels. A channel represents a single
 * channel of input or output for the device. It may be an entire stream, or
 * a channel within a stream. Channels are the smallest addressable unit of a
 * device.
 * <P>
 * When getting and setting a device's properties, it is necessary to always
 * specify exactly which part of the device to interrogate. The section is
 * specified with a boolean argument (generally called isInput) where true
 * refers to the input section and false refers to the output section. The
 * channel is specified with an unsigned integer argument (generally called
 * inChannel) where 0 means the master channel and greater than zero refers to the
 * Nth indexed channel starting with index 1.
 * <P>
 * In order to manage the state that is associated with each AudioDevice, only
 * one instance of a particular device is created. When an application is 
 * completely finished with a device, the application must call
 * <BR><CODE>myAudioDevice.dispose ();</CODE><BR>
 * When an application is finised with all AudioDevice services, the application
 * can call
 * <BR><CODE>AudioDevice.disposeAll ();</CODE><BR>
 */
public final class AudioDevice extends CAObject {	
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}
	
		// OK -> it goes like this
		// everytime a ADevice is created it is added to the Hashtable
		// deviceTable
		//		->key Device
		//		-> value DeviceProcTable
	private static final Object holder = new Object();	
	static final Hashtable deviceTable = new Hashtable();	
	
	static final byte kInput = (byte)1;
	static final byte kOutput = (byte)0;
	
//_________________________ CLASS METHODS	
	/**
	 * An application can use this call to dispose of all of the known AudioDevice's to the system.
	 * This will stop each device's IOProcs and remove them, and remove all of each AudioDevice's
	 * property listeners.
	 * <P>
	 * Once this call is made an application can no longer use any of the previously created
	 * AudioDevice objects. They should also be in a state where then can be garbage collected.
	 * If an application wishes to establish a connection with a device again, the application
	 * should get the AudioDevice from the AudioHardware class.
	 * <P>
	 * Typically an application will only call this when it is completely finished with all
	 * audio device use, for instance when the application is exiting.
	 */
	public static void disposeAll () throws CAException {
		Enumeration iter = deviceTable.keys();
		while (iter.hasMoreElements()) {
			AudioDevice dev = (AudioDevice)iter.nextElement();
			dev.dispose();
		}
		deviceTable.clear();
	}
	
	
	static AudioDevice makeDevice (int id) {
		Enumeration iter = deviceTable.keys();
		while (iter.hasMoreElements()) {
			AudioDevice dev = (AudioDevice)iter.nextElement();
			if (id == CAObject.ID (dev))
				return dev;
		}
		return new AudioDevice (id);
	}
	
	private static void removeFromDeviceTable (AudioDevice dev) {
		Enumeration iter = deviceTable.keys();
		while (iter.hasMoreElements()) {
			AudioDevice member = (AudioDevice)iter.nextElement();
			if (member.equals (dev)) {
				deviceTable.remove (dev);
			}
		}
	}	
	
		// these guys are NEVER natively disposed
	private AudioDevice (int nr) {
		super (nr, holder);
	}
	
//_________________________ INSTANCE FIELDS
	private final CAMemoryObject outSize_A = new CAMemoryObject(4, false); 
	private final CAMemoryObject outSize_B = new CAMemoryObject(4, false); 
	private final Object syncIOProc = new Object();
	private final Object syncOutputListener = new Object();
	private final Object syncInputListener = new Object();

//_________________________ INSTANCE METHODS
		// gets the table of procs for this device
	private final DeviceProcTable getDeviceProcTable () {
		Object value = deviceTable.get (this);
		if (value == null) {
			value = new DeviceProcTable(this);
			deviceTable.put (this, value);
		}
		return (DeviceProcTable)value;
	}
	
	private final boolean hasDeviceProcTable () {
		return deviceTable.containsKey (this);
	}
	
	/**
	 * Install the given IO proc for the device. A client may have multiple
	 * IO procs for a given device. The device may refuse to accept an IO proc
	 * if it is in "hog" mode or it has as many clients as it can handle already.
 	 * <BR><BR><b>CoreAudio::AudioDeviceAddIOProc</b><BR><BR>
 	 * @param inProc the AudioDeviceIOProc object that will be executed each IO cycle
 	 * by this AudioDevice
 	 */
	public void addIOProc (AudioDeviceIOProc ioProc) throws CAException {
		synchronized (syncIOProc) {
			DeviceProcTable table = getDeviceProcTable();
			if (table.contains (ioProc))
				throw new CAException ("This ioProc is already registered as an IOProc for this device");
			HardwareDispatcher dispatcher = new HardwareDispatcher (this, ioProc, 1, 1);
			int res = AudioDeviceAddIOProc (_ID(), dispatcher.ID(), 0);
			CAException.checkError (res);	
			table.put (ioProc, dispatcher);
		}
	}
	
	/**
	 * Remove the given IO proc for the device.
 	 * <BR><BR><b>CoreAudio::AudioDeviceAddIOProc</b><BR><BR>
 	 * @param inProc the AudioDeviceIOProc object that will be removed
 	 */
	public void removeIOProc (AudioDeviceIOProc ioProc) throws CAException {
		synchronized (syncIOProc) {
			DeviceProcTable table = getDeviceProcTable();
			HardwareDispatcher dispatcher = table.remove (ioProc);
			if (dispatcher == null)
				throw new CAException ("This ioProc is not registered as an IOProc for this device");
			int res = removeIOProc (dispatcher);
			CAException.checkError (res);	
		}
	}
	
	int removeIOProc (HardwareDispatcher dispatcher) {
		int res = AudioDeviceRemoveIOProc (_ID(), dispatcher.ID());
		dispatcher.cleanup();
		return res;
	}
			
	/**
	 * Start up the given IOProc. Note that the IOProc will likely get called
	 * for the first time before the call to this routine returns.
	 * <P>
	 * Passing <CODE>null</CODE> to AudioDeviceStart is legal and will start the hardware
	 * regardless of whether there are any active IOProcs. Note that each time
	 * <CODE>null</CODE> is passed to AudioDeviceStart, it should be balanced by passing
	 * <CODE>null</CODE> to AudioDeviceStop.
 	 * <BR><BR><b>CoreAudio::AudioDeviceStart</b><BR><BR>
 	 * @param inProc the AudioDeviceIOProc object that will be executed each IO cycle
 	 * by this AudioDevice
 	 */
	public void start (AudioDeviceIOProc ioProc) throws CAException {
		if (ioProc == null) {
			start();
			return;
		}
		synchronized (syncIOProc) {
			int res = 0;
			if (ioProc != null) {
				HardwareDispatcher dispatcher = getDeviceProcTable().get (ioProc);
				if (dispatcher == null)
					throw new CAException ("You must first add this IOProc to this device");
				res = AudioDeviceStart (_ID(), dispatcher.ID());
			} else {
				res = AudioDeviceStart (_ID(), 0);
			}
			CAException.checkError (res);	
		}
	}
	
	/**
	*	This will start the hardware, even though there
	*	may be no IOProcs registered. This is helpful if you plan on using
	*	any of the device's services that require it to be running such as
	*	AudioDeviceRead or AudioDeviceGetCurrentTime. A balancing call to
	*	AudioDevice.stop() with no parameters is required to stop the hardware.
	* <BR><BR><b>CoreAudio::AudioDeviceStart</b><BR><BR>
	*/
	public void start () throws CAException {
		int res = AudioDeviceStart (_ID(), 0);
		CAException.checkError (res);	
	}

	/**
	 * Stop the given device's IOProc.
 	 * <BR><BR><b>CoreAudio::AudioDeviceStop</b><BR><BR>
 	 * @param inProc the AudioDeviceIOProc object that will be stopped
 	 */
	public void stop (AudioDeviceIOProc ioProc) throws CAException {
		synchronized (syncIOProc) {
			int res = 0;
			if (ioProc != null) {
				HardwareDispatcher dispatcher = getDeviceProcTable().get (ioProc);
				if (dispatcher == null)
					throw new CAException ("You cannot stop a device's IOProc that is not added to the device.");
				res = _stop (dispatcher);
			} else
				res = _stop (null);
				
			CAException.checkError (res);
		}
	}

	/**
	*	This will stop the hardware previously started with no IOProc specified.
	*   If you start the device with an IOProc you should also stop it with the same IOProce
	* <BR><BR><b>CoreAudio::AudioDeviceStop</b><BR><BR>
	*/
	public void stop () throws CAException {
		int res = AudioDeviceStop (_ID(), 0);
		CAException.checkError (res);	
	}
	
	int _stop (HardwareDispatcher dispatcher) {
		return (dispatcher != null 
					? AudioDeviceStop (_ID(), dispatcher.ID())
					: AudioDeviceStop (_ID(), 0));
	}
	

	/**
	 * Read some data from a device starting at the given time. The
	 * AudioBufferList must be in the same size and shape as the one
	 * returned via kAudioDevicePropertyStreamConfiguration. The number
	 * of bytes read into each buffer will be written back to the structure.
 	 * <BR><BR><b>CoreAudio::AudioDeviceRead</b><BR><BR>
 	 * @param inStartTime the time at which to start reading
 	 * @param outData will contain the audio data read from this AudioDevice's input buffers when this call is completed.
	 */
	public void read (AudioTimeStamp inStartTime, AudioBufferList outData) throws CAException {
		int res = AudioDeviceRead(_ID(), CAObject.ID(inStartTime), CAObject.ID(outData));
		CAException.checkError (res);
	}

	/**
	 * Retrieve the current time.
 	 * <BR><BR><b>CoreAudio::AudioDeviceGetCurrentTime</b><BR><BR>
 	 * @return the current time.
 	 */
	public AudioTimeStamp getCurrentTime () throws CAException {
		AudioTimeStamp ts = new AudioTimeStamp (false);
		int res = AudioDeviceGetCurrentTime (_ID(), CAObject.ID(ts));
		CAException.checkError (res);	
		return ts;
	}
	/**
	 * Translate the given time. The output time formats are requested using
	 * the flags in the outTime argument. A device may or may not be able to
	 * satisfy all requests so be sure to check the flags again on output.
 	 * <BR><BR><b>CoreAudio::AudioDeviceGetCurrentTime</b><BR><BR>
 	 * @param inTime the time to convert
 	 * @param outTime the time that will be converted
 	 */
 	public void translateTime (AudioTimeStamp inTime, AudioTimeStamp outTime) throws CAException {
		int res = AudioDeviceTranslateTime (_ID(), CAObject.ID(inTime), CAObject.ID(outTime));
		CAException.checkError (res);	
	}
	
	/**
 	 * Retrieve information about the size of the given property on the given
 	 * channel for the input channel of this device.
 	 * <BR><BR><b>CoreAudio::AudioDeviceGetPropertyInfo</b><BR><BR>
 	 * @param channel the input channel from which to gather information
 	 * @param propertyID the ID of the input property about which to gather information
 	 * @return the size need to retrieve that input property's current value
	 */
	public int getInputPropertyInfoSize (int channel, int propertyID) throws CAException {
		synchronized (outSize_A) {
			outSize_A.setIntAt (0, 4);
			int res = AudioDeviceGetPropertyInfo (_ID(), channel, kInput, propertyID, CAObject.ID(outSize_A), CAObject.ID(outSize_B));
			CAException.checkError (res);
			return outSize_A.getIntAt (0);	
		}
	}

	/**
 	 * Retrieve information about whether the given property on the given
 	 * channel for the input channel of this device can be changed.
 	 * <BR><BR><b>CoreAudio::AudioDeviceGetPropertyInfo</b><BR><BR>
 	 * @param channel the input channel from which to gather information
 	 * @param propertyID the ID of the input property about which to gather information
 	 * @return true if the property can be changed, otherwise false
	 */
	public boolean getInputPropertyInfoWritable (int channel, int propertyID) throws CAException {
		synchronized (outSize_A) {
			outSize_A.setIntAt (0, 4);
			int res = AudioDeviceGetPropertyInfo (_ID(), channel, kInput, propertyID, CAObject.ID(outSize_A), CAObject.ID(outSize_B));
			CAException.checkError (res);	
			return (outSize_B.getIntAt (0) != 0);	
		}
	}

	/**
 	 * Retrieve information about the size of the given property on the given
 	 * channel for the output channel of this device.
 	 * <BR><BR><b>CoreAudio::AudioDeviceGetPropertyInfo</b><BR><BR>
 	 * @param channel the output channel from which to gather information
 	 * @param propertyID the ID of the output property about which to gather information
 	 * @return the size need to retrieve that output property's current value
	 */
	public int getOutputPropertyInfoSize (int channel, int propertyID) throws CAException {
		synchronized (outSize_A) {
			outSize_A.setIntAt (0, 4);
			int res = AudioDeviceGetPropertyInfo (_ID(), channel, kOutput, propertyID, CAObject.ID(outSize_A), CAObject.ID(outSize_B));
			CAException.checkError (res);	
			return outSize_A.getIntAt (0);	
		}
	}

	/**
 	 * Retrieve information about whether the given property on the given
 	 * channel for the output channel of this device can be changed.
 	 * <BR><BR><b>CoreAudio::AudioDeviceGetPropertyInfo</b><BR><BR>
 	 * @param channel the output channel from which to gather information
 	 * @param propertyID the ID of the output property about which to gather information
 	 * @return true if the property can be changed, otherwise false
	 */
	public boolean getOutputPropertyInfoWritable (int channel, int propertyID) throws CAException {
		synchronized (outSize_A) {
			outSize_A.setIntAt (0, 4);
			int res = AudioDeviceGetPropertyInfo (_ID(), channel, kOutput, propertyID, CAObject.ID(outSize_A), CAObject.ID(outSize_B));
			CAException.checkError (res);	
			return (outSize_B.getByteAt (0) != 0);	
		}
	}
		
	/**
 	 * Retrieve the indicated property data for the given device's input channel. A property
 	 * is specified as an ID and a channel number. The channel number allows for
 	 * access to properties on the channel level. On input, ioDataSize has the size
 	 * of the data pointed to by outPropertyData. On output, it will contain
 	 * the amount written. If outPropertydata is NULL and ioPropertyDataSize is
 	 * not, the amount that would have been written will be reported.
 	 * <BR><BR><b>CoreAudio::AudioDeviceGetProperty</b><BR><BR>
 	 * @param channel the input channel from which to gather information
 	 * @param propertyID the ID of the input property about which to gather information
 	 * @return the number of bytes that were written to the outPropertyData object.
	 */
	public int getInputProperty (int channel, int propertyID, CAMemoryObject outPropertyData) throws CAException {
		synchronized (outSize_A) {
			outSize_A.setIntAt (0, outPropertyData.getSize());
			int res = AudioDeviceGetProperty (_ID(), channel, kInput, propertyID, CAObject.ID(outSize_A), CAObject.ID(outPropertyData));
			CAException.checkError (res);	
			return outSize_A.getIntAt (0);	
		}
	}

	/**
 	 * Retrieve the indicated property data for the given device's output channel. A property
 	 * is specified as an ID and a channel number. The channel number allows for
 	 * access to properties on the channel level. On input, ioDataSize has the size
 	 * of the data pointed to by outPropertyData. On output, it will contain
 	 * the amount written. If outPropertydata is NULL and ioPropertyDataSize is
 	 * not, the amount that would have been written will be reported.
 	 * <BR><BR><b>CoreAudio::AudioDeviceGetProperty</b><BR><BR>
 	 * @param channel the input channel from which to gather information
 	 * @param propertyID the ID of the input property about which to gather information
 	 * @return the number of bytes that were written to the outPropertyData object.
	 */
	public int getOutputProperty (int channel, int propertyID, CAMemoryObject outPropertyData) throws CAException {
		synchronized (outSize_A) {
			outSize_A.setIntAt (0, outPropertyData.getSize());
			int res = AudioDeviceGetProperty (_ID(), channel, kOutput, propertyID, CAObject.ID(outSize_A), CAObject.ID(outPropertyData));
			CAException.checkError (res);	
			return outSize_A.getIntAt (0);	
		}
	}

	/**
 	 * Retrieve the indicated property data for the given device's input channel. A property
 	 * is specified as an ID and a channel number. The channel number allows for
 	 * access to properties on the channel level. On input, ioDataSize has the size
 	 * of the data pointed to by outPropertyData. On output, it will contain
 	 * the amount written. If outPropertydata is NULL and ioPropertyDataSize is
 	 * not, the amount that would have been written will be reported.
 	 * <BR><BR><b>CoreAudio::AudioDeviceGetProperty</b><BR><BR>
 	 * @param channel the input channel from which to gather information
 	 * @param propertyID the ID of the input property about which to gather information
 	 * @return the number of bytes that were written to the outPropertyData object.
	 */
	public CAFString getInputCAFStringProperty (int channel, int propertyID) throws CAException {
		synchronized (outSize_A) {
			outSize_A.setIntAt (0, 4);
			int res = AudioDeviceGetProperty (_ID(), channel, kInput, propertyID, CAObject.ID(outSize_A), CAObject.ID(outSize_B));
			CAException.checkError (res);	
			return JNIHardware.newCFString (outSize_B.getIntAt (0));	
		}
	}

	/**
 	 * Retrieve the indicated property data for the given device's output channel. A property
 	 * is specified as an ID and a channel number. The channel number allows for
 	 * access to properties on the channel level. On input, ioDataSize has the size
 	 * of the data pointed to by outPropertyData. On output, it will contain
 	 * the amount written. If outPropertydata is NULL and ioPropertyDataSize is
 	 * not, the amount that would have been written will be reported.
 	 * <BR><BR><b>CoreAudio::AudioDeviceGetProperty</b><BR><BR>
 	 * @param channel the input channel from which to gather information
 	 * @param propertyID the ID of the input property about which to gather information
 	 * @return the number of bytes that were written to the outPropertyData object.
	 */
	public CAFString getOutputCAFStringProperty (int channel, int propertyID) throws CAException {
		synchronized (outSize_A) {
			outSize_A.setIntAt (0, 4);
			int res = AudioDeviceGetProperty (_ID(), channel, kOutput, propertyID, CAObject.ID(outSize_A), CAObject.ID(outSize_B));
			CAException.checkError (res);	
			return JNIHardware.newCFString (outSize_B.getIntAt (0));	
		}
	}

	/**
 	 * Sets the indicated property data for the given device's input channel. The
 	 * property's value will change at the specified time.
 	 * <BR><BR><b>CoreAudio::AudioDeviceSetProperty</b><BR><BR>
 	 * @param when indicates when the property should change
 	 * @param channel the input channel of the property to set
 	 * @param propertyID the ID of the input property to set
 	 * @param propertyData the value that the property will be set too.
	 */
	public void setInputProperty (AudioTimeStamp when, int channel, int propertyID, CAMemoryObject propertyData) throws CAException {
		int res = AudioDeviceSetProperty (_ID(), CAObject.ID(when), channel, kInput, propertyID, propertyData.getSize(), CAObject.ID(propertyData));	
		CAException.checkError (res);	
	}
	
	/**
 	 * Sets the indicated property data for the given device's output channel. The
 	 * property's value will change at the specified time.
 	 * <BR><BR><b>CoreAudio::AudioDeviceSetProperty</b><BR><BR>
 	 * @param when indicates when the property should change
 	 * @param channel the output channel of the property to set
 	 * @param propertyID the ID of the input property to set
 	 * @param propertyData the value that the property will be set too.
	 */
	public void setOutputProperty (AudioTimeStamp when, int channel, int propertyID, CAMemoryObject propertyData) throws CAException {
		int res = AudioDeviceSetProperty (_ID(), CAObject.ID(when), channel, kOutput, propertyID, propertyData.getSize(), CAObject.ID(propertyData));	
		CAException.checkError (res);	
	}

	/**
 	 * Sets the indicated property data for the given device's input channel. The
 	 * property's value will change at the specified time.
 	 * <BR><BR><b>CoreAudio::AudioDeviceSetProperty</b><BR><BR>
 	 * @param when indicates when the property should change
 	 * @param channel the input channel of the property to set
 	 * @param propertyID the ID of the input property to set
 	 * @param propertyData the value that the property will be set too.
	 */
	public void setInputProperty (AudioTimeStamp when, int channel, int propertyID, CAFString propertyData) throws CAException {
		int res = AudioDeviceSetProperty (_ID(), CAObject.ID(when), channel, kInput, propertyID, 4, CAObject.ID(propertyData));	
		CAException.checkError (res);	
	}
	
	/**
 	 * Sets the indicated property data for the given device's output channel. The
 	 * property's value will change at the specified time.
 	 * <BR><BR><b>CoreAudio::AudioDeviceSetProperty</b><BR><BR>
 	 * @param when indicates when the property should change
 	 * @param channel the output channel of the property to set
 	 * @param propertyID the ID of the input property to set
 	 * @param propertyData the value that the property will be set too.
	 */
	public void setOutputProperty (AudioTimeStamp when, int channel, int propertyID, CAFString propertyData) throws CAException {
		int res = AudioDeviceSetProperty (_ID(), CAObject.ID(when), channel, kOutput, propertyID, 4, CAObject.ID(propertyData));	
		CAException.checkError (res);	
	}

	/**
	 * Set up a routine that gets called when the property of an input device is changed.
 	 * <BR><BR><b>CoreAudio::AudioDeviceAddPropertyListener</b><BR><BR>
 	 * @param channel the input channel of the property to watch
 	 * @param propertyID the ID of the input property to listen too.
 	 * @parm listener the interface's <CODE>execute</CODE> method is called when that property's value is changed
	 */
	public void addInputPropertyListener (int channel, int propertyID, ADevicePropertyListener listener) throws CAException {
		synchronized (syncInputListener) {
			DeviceProcTable table = getDeviceProcTable();
			if (table.containsInput (propertyID, listener, channel))
				throw new CAException ("Cannot add the same listener:" + listener + " to the same device's input property:" + Integer.toHexString (propertyID) + " and channel:" + channel);
				
			HardwareDispatcher disp = new HardwareDispatcher (this, listener);
			int res = AudioDeviceAddPropertyListener(_ID(), channel, kInput, propertyID, disp.ID(), 0);
			if (res != 0) {
				disp.cleanup();
				CAException.checkError (res);
				return;
			}
			table.addInput (propertyID, listener, channel, disp);
		}
	}

	/**
	 * Set up a routine that gets called when the property of an output device is changed.
 	 * <BR><BR><b>CoreAudio::AudioDeviceAddPropertyListener</b><BR><BR>
 	 * @param channel the output channel of the property to watch
 	 * @param propertyID the ID of the output property to listen too.
 	 * @parm listener the interface's <CODE>execute</CODE> method is called when that property's value is changed
	 */
	public void addOutputPropertyListener (int channel, int propertyID, ADevicePropertyListener listener) throws CAException {
		synchronized (syncOutputListener) {
			DeviceProcTable table = getDeviceProcTable();
			if (table.containsOutput (propertyID, listener, channel))
				throw new CAException ("Cannot add the same listener:" + listener + " to the same device's output property:" + Integer.toHexString (propertyID) + " and channel:" + channel);

			HardwareDispatcher disp = new HardwareDispatcher (this, listener);
			int res = AudioDeviceAddPropertyListener(_ID(), channel, kOutput, propertyID, disp.ID(), 0);
			if (res != 0) {
				disp.cleanup();
				CAException.checkError (res);
				return;
			}
			table.addOutput (propertyID, listener, channel, disp);
		}
	}

	/**
	 * Remove the previously instantiated notification call for an input device.
 	 * <BR><BR><b>CoreAudio::AudioDeviceRemovePropertyListener</b><BR><BR>
	 * @param listener the execute method on this object that would have been called when the property changed
	 */	
	public void removeInputPropertyListener (int channel, int propertyID, ADevicePropertyListener listener) throws CAException {
		synchronized (syncInputListener) {
			DeviceProcTable table = getDeviceProcTable();
			if (table.containsInput (propertyID, listener, channel) == false)
				throw new CAException ("Cannot remove the listener:" + listener + " on the device's input property:" + Integer.toHexString (propertyID) + " and channel:" + channel + ". It hasn't been added");
			
			HardwareDispatcher toBeRemoved = table.removeInput (propertyID, listener, channel);		
			removePropertyListener (channel, kInput, propertyID, listener, toBeRemoved);
		}
	}

	/**
	 * Remove the previously instantiated notification call for an output device.
 	 * <BR><BR><b>CoreAudio::AudioDeviceRemovePropertyListener</b><BR><BR>
	 * @param listener the execute method on this object that would have been called when the property changed
	 */	
	public void removeOutputPropertyListener (int channel, int propertyID, ADevicePropertyListener listener) throws CAException {
		synchronized (syncOutputListener) {
			DeviceProcTable table = getDeviceProcTable();
			if (table.containsOutput (propertyID, listener, channel) == false)
				throw new CAException ("Cannot remove the listener:" + listener + " on the device's output property:" + Integer.toHexString (propertyID) + " and channel:" + channel + ". It hasn't been added");
			
			HardwareDispatcher toBeRemoved = table.removeOutput (propertyID, listener, channel);		
			removePropertyListener (channel, kOutput, propertyID, listener, toBeRemoved);
		}
	}

	void removePropertyListener (int channel, byte isInput, int propertyID, ADevicePropertyListener listener, HardwareDispatcher disp) throws CAException {
		int res = AudioDeviceRemovePropertyListener(_ID(), channel, isInput, propertyID, disp.ID());
		disp.cleanup();
		
		CAException.checkError (res);
	}
	
	/**
	 * This call will remove all of the registered input property listeners for this device.
	 */
	public void removeAllInputListeners () {
		if (hasDeviceProcTable()) {
			synchronized (syncInputListener) {
				getDeviceProcTable().removeAllInputs();
			}
		}
	}

	/**
	 * This call will remove all of the registered output property listeners for this device.
	 */
	public void removeAllOutputListeners () {
		if (hasDeviceProcTable()) {
			synchronized (syncOutputListener) {
				getDeviceProcTable().removeAllOutputs();
			}
		}
	}

	/**
	 * This call will remove all of the property listeners (both input and output) for this device.
	 */
	public void removeAllListeners () {
		removeAllOutputListeners();
		removeAllInputListeners();
	}
	
	/**
	 * This call will stop and remove all of the IOProcs registered with this device.
	 */
	public void removeAllIOProcs () {
		if (hasDeviceProcTable()) {
			synchronized (syncIOProc) {
				getDeviceProcTable().removeAllIOProcs();
			}
		}
	}
	
	/**
	 * When and application is finished with a particular device the <CODE>dispose</CODE>
	 * method can be called. This will stop all of the device's IOProcs and remove all property
	 * listeners that have been added to it.
	 * <P>
	 * After this call is made the AudioDevice object is no longer usable and can be garbage collected
	 * by the system. If an application wants to re-establish a connection to the particular audio device
	 * driver, it should get the AudioDevice again from the AudioHardware class.
	 * <P>
	 * Typically an application will only call this when it is completely finished with a particular AudioDevice.
	 */
	public void dispose () throws CAException {
		if (isValid()) {
			removeAllIOProcs();
			removeAllListeners();
			removeFromDeviceTable(this);
			super.dispose();
		}
	}

	/**
	 * Returns the name of this device.
	 */
	public String getName () throws CAException {
		synchronized (outSize_A) {
			outSize_A.setIntAt (0, 4);
			int res = AudioDeviceGetPropertyInfo (_ID(), 0, kOutput, AHConstants.kAudioDevicePropertyDeviceName, CAObject.ID(outSize_A), CAObject.ID(outSize_B));
			CAException.checkError (res);
			CAMemoryObject str = new CAMemoryObject (outSize_A.getIntAt (0), false);
			res = AudioDeviceGetProperty (_ID(), 0, kOutput, AHConstants.kAudioDevicePropertyDeviceName, CAObject.ID(outSize_A), CAObject.ID(str));
			CAException.checkError (res);	
			return str.getCStringAt(0);	
		}
	}

	/**
	 * @return a String representation of this object.
	 */
	public String toString () {
		try {
			return getClass().getName() + "[name=" + getName() + "]";
		} catch (CAException e) {
			return super.toString();
		}
	}

	/**
 	 * Will return an array of all of the output streams of the device
	 */
	public AudioStream[] getOutputStreams () throws CAException {
		int size = getOutputPropertyInfoSize (0, AHConstants.kAudioDevicePropertyStreams);
		CAMemoryObject ret = new CAMemoryObject (size, false);
		size = getOutputProperty (0, AHConstants.kAudioDevicePropertyStreams, ret);
		AudioStream[] ar = new AudioStream[size/4];
		for (int i = 0; i < ar.length; i++)
			ar[i] = new AudioStream (ret.getIntAt (i*4), this, true);
		return ar;
	}

	/**
 	 * Will return an array of all of the input streams of the device
	 */
	public AudioStream[] getInputStreams () throws CAException {
		int size = getInputPropertyInfoSize (0, AHConstants.kAudioDevicePropertyStreams);
		CAMemoryObject ret = new CAMemoryObject (size, false);
		size = getInputProperty (0, AHConstants.kAudioDevicePropertyStreams, ret);
		AudioStream[] ar = new AudioStream[size/4];
		for (int i = 0; i < ar.length; i++)
			ar[i] = new AudioStream (ret.getIntAt (i*4), this, false);
		return ar;
	}
		
//_ NATIVE METHODS
	private static native int AudioDeviceAddIOProc (int inDevice, int inProc, int inClientData);
	private static native int AudioDeviceRemoveIOProc (int inDevice, int inProc);
	private static native int AudioDeviceStart (int inDevice, int inProc);
	private static native int AudioDeviceStop(int inDevice, int inProc);
	private static native int AudioDeviceRead(int inDevice, int inStartTimePtr, int outDataPtr);
	private static native int AudioDeviceGetCurrentTime(int inDevice, int outTimePtr);
	private static native int AudioDeviceTranslateTime (int	inDevice, int inTimePtr, int outTimePtr);
	private static native int AudioDeviceGetPropertyInfo (int inDevice, int inChannel, byte isInput, 
												int inPropertyID, int outSizeIntPtr, int outWritableBoolPtr);
	private static native int AudioDeviceGetProperty (int inDevice, int inChannel, byte isInput, int inPropertyID, 
												int ioPropertyDataSizeIntPtr, int outPropertyDataVoidPtr);
	private static native int AudioDeviceSetProperty(int inDevice, int inWhenPtr, int inChannel, byte isInput, 
												int inPropertyID, int inPropertyDataSize, int inPropertyDataPtr);
	private static native int AudioDeviceAddPropertyListener(int inDevice, int inChannel, byte isInput, 
												int inPropertyID, int inProc, int inClientData);
	private static native int AudioDeviceRemovePropertyListener(int inDevice, int inChannel, byte isInput,
												int inPropertyID, int inProc);
}

/*
 */
