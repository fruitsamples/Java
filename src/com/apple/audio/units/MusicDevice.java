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
//  MusicDevice.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.units;

import com.apple.audio.*;
import com.apple.audio.util.*;
import com.apple.audio.jdirect.Accessor;
import java.io.*;
/**
 * The MusicDevice class typically represents a Software Synthesiser. It presents
 * two sets of event API's (MIDI and extended ):
 * <UL>
 * <LI>the MIDI API's address the device using the standardized MIDI semantics
 * <BR>MusicDeviceMIDIEvent()
 * <BR>MusicDeviceSysEx()
 * <LI>the extended API's allow more sophisticated control of notes, but would
 * not be available for all MusicDevices (external MIDI devices)
 * <BR>MusicDeviceStartNote()
 * <BR> MusicDeviceStopNote()
 * <BR> AudioUnitSetParameter()   (for group control changes...)
 * </UL>
 * Any arbitrary MIDI event should be possible with MusicDeviceMIDIEvent() and MusicDeviceSysEx()
 */

public class MusicDevice extends AudioUnit {
//_________________________ INITIALIZATION
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}

//_________________________ CLASS METHODS
	/**
	 * This constructor
	 * is used when the <CODE>AUComponent</CODE>'s <CODE>openAU</CODE> or <CODE>openDefaultAU</CODE>
	 * methods are called. 
	 */
	MusicDevice (int inst, Object owner) {
		super (inst, owner);
	}
	
//_________________________ INSTANCE METHODS
	private CAMemoryObject fsspec = null;

//_________________________ INSTANCE METHODS
	/**
	 * Instruct the MusicDevice to use a particular sound bank.
	 * @param soundBankFile the sound bank to use
	 */
	public void setProperty_SoundBank (File soundBankFile) throws CAException, IOException {
		String path = soundBankFile.getCanonicalPath();
		File f = new File (path);
		if (f.exists() == false)
			throw new FileNotFoundException (path);
		if (CASession.hasSecurityRestrictions()) {
			SecurityManager sm = System.getSecurityManager();
			if (sm != null)
				sm.checkRead (path);
		}
					
/*
  short               vRefNum;
  long                parID;
  StrFileName         name;                   give it 256 to be safe!!!
*/
		
		if (fsspec == null)
			fsspec = new CAMemoryObject (262, true);
		int res = JNIUnits.makeFSSpec (path, CAObject.ID(fsspec));
		CAException.checkError (res);
		setProperty (AUProperties.kMusicDeviceProperty_SoundBankFSSpec, 
										0,
										0, 
										fsspec);
	}

	/**
	 * Send a MIDI formatted message to the device.
	 * <BR><BR><b>CoreAudio::MusicDeviceMIDIEvent</b><BR>
	 * @param inStatus the status byte
	 * @param inData1 the first data byte
	 * @param inData2 the second data byte
	 * @param inOffsetSampleFrame can be used to schedule this event at some offset within the currently rendering buffer
	 * rendered slice of the device
	 */
	public void sendMIDIEvent (int inStatus, int inData1, int inData2, int inOffsetSampleFrame) throws CAException {
		int res = MusicDeviceMIDIEvent(_ID(), inStatus, inData1, inData2,  inOffsetSampleFrame);
		CAException.checkError(res);
	}
	
	/**
	 * Send a MIDI SysEx message to the device.
	 * <BR><BR><b>CoreAudio::MusicDeviceSysEx</b><BR> 
	 * @param inData the sys ex data to send
	 * @exception java.lang.ClassCastException if the MIDIData type is NOT packet or raw
	 */
	public void sendSysEx (MIDIData inData) throws CAException {
		if (inData.isType(MIDIData.kMIDIRawData) || inData.isType(MIDIData.kMIDIPacketData)) {
			int res = MusicDeviceSysEx(_ID(), 
								(CAObject.ID(inData) + inData.getMIDIDataOffset()), 
								inData.getMIDIDataLength());
			CAException.checkError(res);
		} else
			throw new ClassCastException ("Wrong type of MIDI Data for sendSysEx");
	}

	/**
	 * Allows MusicDevice to prepare the instrument for play (loading any required resources,
	 * for example, sample data from hard-disk).
	 * <BR><BR><b>CoreAudio::MusicDevicePrepareInstrument</b><BR> 
	 * @param inInstrumentID the unique ID for a particular instrument
	 */
	public void prepareInstrument (int inInstrumentID) throws CAException {
		int res = MusicDevicePrepareInstrument(_ID(), inInstrumentID);
		CAException.checkError(res);
	}

	/**
	 * Allows MusicDevice to relesae the acquired resources of the specified instrument.
	 * <BR><BR><b>CoreAudio::MusicDevicePrepareInstrument</b><BR> 
	 * @param inInstrumentID the unique ID for a particular instrument
	 */
	public void releaseInstrument (int inInstrumentID) throws CAException {
		int res = MusicDeviceReleaseInstrument(_ID(), inInstrumentID);
		CAException.checkError(res);
	}

	/**
	 * Starts a note on the specified instrument using the supplied parameters. The
	 * identity of the note is returned to allow the program to manipulate the note
	 * whilst it is still sounding.
	 * <BR><BR><b>CoreAudio::MusicDeviceStartNote</b><BR>
	 * @param inInstrumentID the unique ID for a particular instrument
	 * @param inGroupID the group that this particular note will belong too.
	 * @param inOffsetSampleFrame can be used to schedule this event at some offset within the currently rendering buffer
	 * @param inParams the optional parameters that can be sent to the synthesiser when starting a note.
	 * @return this integer identifies the note instance that the synth has created. It can be passed in to 
	 * the control and stop methods to alter the note.
	 */
	public int startNote (int inInstrumentID, int inGroupID, int inOffsetSampleFrame, ExtendedNoteParams inParams) throws CAException {
		synchronized (AUComponent.syncObject) {
			int res = MusicDeviceStartNote(_ID(),
										inInstrumentID,
										inGroupID,
										AUComponent.firstArg4Ptr,
										inOffsetSampleFrame,
										CAObject.ID(inParams));
			CAException.checkError(res);
			return Accessor.getIntFromPointer (AUComponent.firstArg4Ptr, 0);
		}
	}

	/**
	 * Stops a note on the specified instrument using the supplied parameters. The
	 * identity of the note or the group identifier can be supplied
	 * <BR><BR><b>CoreAudio::MusicDeviceStopNote</b><BR>
	 * @param inGroupID the group ID used in the startNote method
	 * @param inNoteID the note ID returned by the startNote method
	 * @param inOffsetSampleFrame can be used to schedule this event at some offset within the currently rendering buffer
	 */
	public void stopNote (int inGroupID, int inNoteID, int inOffsetSampleFrame) throws CAException {
		int res = MusicDeviceStopNote(_ID(),  
											inGroupID,
											inNoteID,
											inOffsetSampleFrame);
		CAException.checkError(res);
	}
	
	/**
	 * Send a control message to all of the currently sounding notes on a particular group.
	 * <BR><BR><b>CoreAudio::AudioUnitSetParameter</b><BR>
	 * @param inControlID the control message to send
	 * @param inGroupID the group of notes that should react to this message
	 * @param inValue the new value for the specified control message for that group
	 * @param inOffsetSampleFrame can be used to schedule this event some offset within the currently
	 */
	public void setGroupControlValue (int inControlID, int inGroupID, float inValue, int inBufferOffsetInFrames) throws CAException {
		setParameter (inControlID, AUConstants.kAudioUnitScope_Group, inGroupID, inValue, inBufferOffsetInFrames);
	}

	/**
	 * Send a control message to all of the currently sounding notes on a particular group.
	 * <BR><BR><b>CoreAudio::AudioUnitGetParameter</b><BR>
	 * @param inControlID the control message to send
	 * @param inGroupID the group of notes that should react to this message
	 * @return the current value for the specified control message for that note
	 */
	public float getGroupControlValue (int inControlID, int inGroupID) throws CAException {
		return getParameter (inControlID, AUConstants.kAudioUnitScope_Group, inGroupID);
	}

	/**
	 * Send a control message the music device.
	 * <BR><BR><b>CoreAudio::AudioUnitSetParameter</b><BR>
	 * @param inControlID the control message to send
	 * @param inScopeID the scope that defines how broadly to apply this control message
	 * @param inGroupID the group of notes that should react to this message
	 * @param inValue the new value for the specified control message for that group
	 * @param inOffsetSampleFrame can be used to schedule this event some offset within the currently
	 */
	public void setControlValue (int inControlID, int inScopeID, int inGroupID, float inValue, int inBufferOffsetInFrames) throws CAException {
		setParameter(inControlID, 
						inScopeID, 
						inGroupID, 
						inValue, 
						inBufferOffsetInFrames);
	}

	/**
	 * Get the value of the control message of the music device.
	 * <BR><BR><b>CoreAudio::AudioUnitGetParameter</b><BR>
	 * @param inControlID the control message to send
	 * @param inScopeID the scope that defines how broadly this control message is applied
	 * @param inGroupID the group of notes that should react to this message
	 * @return the current value for the specified control message for that note
	 */
	public float getControlValue (int inControlID, int inScopeID, int inGroupID) throws CAException {
		return getParameter (inControlID, inScopeID, inGroupID);
	}


//_ NATIVE METHODS
	private static native int MusicDeviceMIDIEvent(int ci,
										int inStatus,
										int inData1,
										int inData2,
										int inOffsetSampleFrame);
	private static native int MusicDeviceSysEx(int ci,
										int inDataPtr,
										int inLength);
	private static native int MusicDevicePrepareInstrument(int ci, int inInstrumentID);
	private static native int MusicDeviceReleaseInstrument(int ci, int inInstrumentID);
	private static native int MusicDeviceStartNote(int ci,
											int inInstrument,
											int inGroupID,
											int outNoteInstanceIDPtr,
											int inOffsetSampleFrame,
											int inParamsPtr);
	private static native int MusicDeviceStopNote(int ci,
  											int inGroupID,
											int inNoteInstanceID,
											int inOffsetSampleFrame);
}

/*
 */

