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
//  AudioUnit.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.units;

import com.apple.audio.*;
import com.apple.audio.jdirect.*;
import com.apple.component.*;
import com.apple.audio.util.*;
import java.util.*;
/**
 * AudioUnits represent opened components that perform some transformational actions on
 * Audio data. The audio data in a buffer that an AudioUnit operates on
 * can be in an arbitrary format, but 32bit floating point
 * is the "canonical" form used by most audio units.
 *<P>
 * <B>AudioUnit Initialization</B>
 * No substantial allocation of resources should occur when the AudioUnit component
 * is opened.  Applications call <code>initialize</code> to perform allocations to 
 * use the services of the <CODE>AudioUnit</CODE>. To deallocate those resources, but
 * still keep this AudioUnit open, the <CODE>uninitialize</CODE> method can be called. To
 * use the AudioUnit again, the application can <CODE>initialize</CODE> the unit again.
 *<P>
 * <B>Property Management</B>
 * Properties describe or control some aspect of an AudioUnit that does
 * not vary in time. Properties are addressed via their ID, scope, and
 * element. The ID identifies the property and describes the structure of
 * its data. The scope identifies the functional area of the AudioUnit
 * of interest. The element identifies the specific part of the scope of
 * interest.
 * <P>
 * Examples of properties include user readable names, stream format, data
 * sources, and one shot configuration information.
 * <P>
 * <B>Parameter Management</B>
 * Parameters control a specific aspect of the processing of an AudioUnit
 * that can vary in time. Further, parameters are represented as single
 * floating point values. Parameters are addressed by their ID, scope
 * and element similar to properties.
 * <P>
 * Since parameters vary in time, AudioUnits must support some notion of
 * scheduling. That said, it is not the intent of this API to force an
 * AudioUnit to support full blown scheduling and history. It is assumed
 * that the general workload of an AudioUnit's scheduler will revolve around
 * scheduling events at most a few buffers into the future. Further, the event
 * density is generally expected to be light. Therefore, the client of an
 * AudioUnit should take care in the number of events it schedules as it
 * could drastically affect performance.
 *<P>
 * Examples of parameters include volume, panning, filter cutoff, delay time
 * LFO spead, rate multiplier, etc.
 * <P>
 * <B>IO Management</B>
 * AudioUnits use a pull IO model. A unit specifies through its properties
 * the number and format of its inputs and ouputs. Each output is in itself a
 * whole stream of N interleaved audio channels. Connections between units
 * are also managed via properties. This means that the only routines necessary
 * are the ones to specify how to get data out of a specific output (AudioUnitRenderSlice() )
 * <P>
 * The inActionFlags parameter tells the unit additional operations to
 * perform:
 * <PRE>
       kAudioUnitRenderAction_Accumulate
            The unit should sum its output into the given buffer, rather
            than replace it. This action is only valid for formats that
            support easy stream mixing like linear PCM. Further, a buffer
            will always be supplied.
		kAudioUnitRenderAction_UseProvidedBuffer
			This flag indicates that the rendered audio must be placed in the
			buffer pointed to by the mData member of the ioData argument passed
			into AudioUnitRenderSlice().  In this case mData must, of course, point to a valid
			piece of allocated memory.  If this flag is not set, then the mData
			member of ioData may possibly be changed upon return from AudioUnitRenderSlice(),
			pointing to a different buffer (owned by the AudioUnit ).
			In any case, upon return from AudioUnitRenderSlice(), mData points to
			the rendered audio.
 * </PRE>
 * The inTimeStamp parameter gives the AudioUnit information about what the time
 * is for the start of the rendered audio output.
 * <P>
 * The inOutputBusNumber parameter requests that audio be rendered for a particular
 * audio output of the AudioUnit.  AudioUnitRenderSlice() must be called separately
 * to get rendered audio for each of its outputs.  The AudioUnit is expected to cache
 * its rendered audio for each output in the case that AudioUnitRenderSlice() is called
 * more than once for the same output (inOutputBusNumber is the same) at the same time
 * (inTimeStamp is the same).  This solves the "fanout" problem.
 * <P>
 * The ioData parameter is of special note.  The client must pass a pointer to a
 * AudioBuffer structure filled out completely.  If the ioData mData member is NULL,
 * then AudioUnitRenderSlice()
 * will set mData to a buffer owned by the AudioUnit.  Or, the client
 * may pass in his own buffer to render to if the mData member is non-NULL.
 * In any case, when AudioUnitRenderSlice() returns, the mData value points to the buffer data
 * where audio was rendered for the given audio output bus.
 * See also, the description for the kAudioUnitRenderAction_UseProvidedBuffer flag.
 * <P>
 * The audio data in a buffer can be in an arbitrary format, but mono 32bit floating point 
 * is the "canonical" form used by most AudioUnits for a single output.  If using the
 * canonical output format, then stereo may be implemented as two AudioUnit outputs
 * (each mono 32bit floating point).
 */
public class AudioUnit extends CAObject {
//_________________________ INITIALIZATION
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}
	
//_________________________ STATIC METHODS
		//used by JNI -> AUGraph to return AudioUnits
	/*protected*/private AudioUnit (Object owner) {
		super (0, owner);
	}
		//used by AUComponent to contruct audio units
	AudioUnit  (int inst, Object owner) {
		super (inst, owner);
	}

	void _setNR (int id) {
		setNR (id);
	}
	
//_________________________ INSTANCE METHODS
	/**
	 * Returns the version number of this component.
	 * The high-order 16 bits represent the major version, 
	 * and the low-order 16 bits represent the minor version. 
	 * The major version specifies the component specification level; 
	 * the minor version specifies a particular implementation’s version number.
	 * <BR><BR><b>AppleDocs::GetComponentVersion</b><BR>
	 * @param desc describes the search parameters.
	 * @return a component identifier or null if none found.
	 */
	public int getVersion () {
		return GetComponentVersion (_ID());
	}

	/**
	 * Returns the AUComponent that this AudioUnit is an instance of.
	 * @return a Component
	 */
	public Component getComponent() {
		return getAUComponent();
	}

	/**
	 * Returns the AUComponent that this AudioUnit is an instance of.
	 * @return a AUComponent
	 */
	public AUComponent getAUComponent() {
		return new AUComponent (_ID());
	}

	/**
	 * Initialize the AudioUnit.
	 * <BR><BR><b>CoreAudio::AudioUnitInitialize</b><BR>
	 */
	public void initialize () throws CAException {
		int res = AudioUnitInitialize(_ID());
		CAException.checkError (res);
	}

	/**
	 * Uninitialize the AudioUnit
	 * <BR><BR><b>CoreAudio::AudioUnitUninitialize</b><BR>
	 */
	public void uninitialize () throws CAException {
		int res = AudioUnitUninitialize(_ID());
		CAException.checkError (res);
	}
	
	/**
	 * Returns true if the AudioUnit has the specified property.
	 * <BR><BR><b>CoreAudio::AudioUnitGetPropertyInfo</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @return true if the unit has that property
	 */
	public boolean getPropertyInfo_Writable (int inID, int inScope, int inElement) throws CAException {
		synchronized (AUComponent.syncObject) {
			int res = AudioUnitGetPropertyInfo(_ID(),
											inID,
											inScope,
											inElement,
											0,
											AUComponent.firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getByteFromPointer (AUComponent.firstArg4Ptr, 0) != 0;
		}
	}

	/**
	 * Returns the required size of the AudioUnit's specified property.
	 * <BR><BR><b>CoreAudio::AudioUnitGetPropertyInfo</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @return the required size to hold the value of the specified property
	 */
	public int getPropertySize (int inID, int inScope, int inElement) throws CAException {
		synchronized (AUComponent.syncObject) {
			int res = AudioUnitGetPropertyInfo(_ID(),
											inID,
											inScope,
											inElement,
											AUComponent.firstArg4Ptr,
											0);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (AUComponent.firstArg4Ptr, 0);
		}
	}
	
	/**
	 * Gets the current value of the specified property and returns the number
	 * of bytes that were written to the supplied memory object.
	 * <BR><BR><b>CoreAudio::AudioUnitGetProperty</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @param outPropertyValue the contents of this object will be written too.
	 * @return the number of bytes written to the outPropertyValue object
	 */
	public int getProperty (int inID, int inScope, int inElement, CAMemoryObject outPropertyValue) throws CAException {
		return getPtrProperty (inID,
					inScope,
					inElement,
					CAObject.ID(outPropertyValue),
					outPropertyValue.getSize());
	}

	int getPtrProperty (int inID, int inScope, int inElement, int outValuePtr, int inSize) throws CAException {
		synchronized (AUComponent.syncObject) {
			Accessor.setIntInPointer (AUComponent.firstArg4Ptr, 0, inSize);
			int res = AudioUnitGetProperty(_ID(), 
								inID, 
								inScope, 
								inElement, 
								outValuePtr, 
								AUComponent.firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (AUComponent.firstArg4Ptr, 0);
		}
	}
	
	/**
	 * Gets the current value of the specified property.
	 * <BR><BR><b>CoreAudio::AudioUnitGetProperty</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @return the current valaue of the property
	 */
	public boolean getBooleanProperty (int inID, int inScope, int inElement) throws CAException {
		synchronized (AUComponent.syncObject) {
			Accessor.setIntInPointer (AUComponent.firstArg4Ptr, 0, 4); //our size arg
			int res = AudioUnitGetProperty(_ID(), 
							inID, 
							inScope, 
							inElement, 
							AUComponent.secondArgPtr, 
							AUComponent.firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (AUComponent.secondArgPtr, 0) != 0;
		}
	}

	/**
	 * Gets the current value of the specified property.
	 * <BR><BR><b>CoreAudio::AudioUnitGetProperty</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @return the current valaue of the property
	 */
	public int getIntProperty (int inID, int inScope, int inElement) throws CAException {
		synchronized (AUComponent.syncObject) {
			Accessor.setIntInPointer (AUComponent.firstArg4Ptr, 0, 4); //our size arg
			int res = AudioUnitGetProperty(_ID(), 
							inID, 
							inScope, 
							inElement, 
							AUComponent.secondArgPtr, 
							AUComponent.firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (AUComponent.secondArgPtr, 0);
		}
	}

	/**
	 * Gets the current value of the specified property.
	 * <BR><BR><b>CoreAudio::AudioUnitGetProperty</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @return the current valaue of the property
	 */
	public float getFloatProperty (int inID, int inScope, int inElement) throws CAException {
		synchronized (AUComponent.syncObject) {
			Accessor.setIntInPointer (AUComponent.firstArg4Ptr, 0, 4); //our size arg
			int res = AudioUnitGetProperty(_ID(), 
							inID, 
							inScope, 
							inElement, 
							AUComponent.secondArgPtr, 
							AUComponent.firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getFloatFromPointer (AUComponent.secondArgPtr, 0);
		}
	}

	/**
	 * Gets the current value of the specified property.
	 * <BR><BR><b>CoreAudio::AudioUnitGetProperty</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @return the current valaue of the property
	 */
	public double getDoubleProperty (int inID, int inScope, int inElement) throws CAException {
		synchronized (AUComponent.syncObject) {
			Accessor.setIntInPointer (AUComponent.firstArg4Ptr, 0, 8); //our size arg
			int res = AudioUnitGetProperty(_ID(), 
							inID, 
							inScope, 
							inElement, 
							AUComponent.secondArgPtr, 
							AUComponent.firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getDoubleFromPointer (AUComponent.secondArgPtr, 0);
		}
	}

	/**
	 * Gets the list of parameter ID's that this unit responds to for the global scope. The application
	 * can then use each parameterID to get information about it using getParameterInfo.
	 * This call defaults to global scope, but many parameters are defined on in and out scopes.
	 * <BR><BR><b>CoreAudio::AudioUnitGetProperty</b><BR>
	 * @return list of parameter IDs
	 * @deprecated 
	 */
	public int[] getParameterList () throws CAException {
		return getParameterList (AUConstants.kAudioUnitScope_Global);
	}

	/**
	 * Gets the list of parameter ID's that this unit responds to. The application
	 * can then use each parameterID to get information about it using getParameterInfo.
	 * This call defaults to global scope, but many parameters are defined on in and out scopes.
	 * <BR><BR><b>CoreAudio::AudioUnitGetProperty</b><BR>
	 * @param inScope the scope of the parameters that you are interested in
	 * @return list of parameter IDs
	 */
	public int[] getParameterList (int inScope) throws CAException {
		int res = 0;
		int infoPtr = 0;
		int size = 0;
		synchronized (AUComponent.syncObject) {
			size = getPropertySize (AUProperties.kAudioUnitProperty_ParameterList,
									inScope, 
									0);
			if (size == 0)
				return null;
			Accessor.setIntInPointer (AUComponent.firstArg4Ptr, 0, size);
			infoPtr = JNIUnits.malloc (size);
			res = AudioUnitGetProperty(_ID(), 
							AUProperties.kAudioUnitProperty_ParameterList,
							inScope, 
							0, 
							infoPtr, 
							AUComponent.firstArg4Ptr);
		}
		if (res == 0) {
			int[] infoAr = new int[size/4];
			ArrayCopy.copyPointerToArray (infoPtr, 
						0,
						infoAr, 
						0, 
						size);				
			JNIUnits.free (infoPtr);
			return infoAr;
		}
		JNIUnits.free (infoPtr);
		throw new CAException (res);
	}

	/**
	 * Installs the render notification callback to this AudioUnit. This is generally used when notifications are required by
	 * the AudioUnit that a renderSlice is about to be executed on it. 
	 * <BR><BR><b>CoreAudio::AudioUnitSetRenderNotification</b><BR>
	 * @param callback the callback that is called each time renderSlice is called on this audio unit
	 */
	public void installRenderNotification (AURenderCallback callback) throws CAException {
		if (callback == null) {
			removeRenderNotification();
			return;
		}

		synchronized (AUDispatcher.cbTable) {
			Integer key = new Integer (_ID());
			Hashtable table = _removeRenderNotification (key);

			AUDispatcher dispatcher = new AUDispatcher (this, callback, false);	
			int res = AudioUnitSetRenderNotification(_ID(), 
									dispatcher.ID(),
									AUDispatcher.renderProc);
			if (res != 0) {
				dispatcher.cleanup();
				CAException.checkError (res);
			} 
			else {
				AUDispatcher.addRenderNotification (key, table, dispatcher);
			}
		}
	}
	
	private Hashtable _removeRenderNotification (Integer key) throws CAException {
		Hashtable table = null;
		int res = 0;
		synchronized (AUDispatcher.cbTable) {
			table = AUDispatcher.removeRenderNotification (key);
			if (table != null)
				res = AudioUnitSetRenderNotification(_ID(), 0, 0);
		}
		CAException.checkError (res);
		return table;
	}
	
	/**
	 * Removes the render callback of this AudioUnit.
	 * <BR><BR><b>CoreAudio::AudioUnitSetRenderNotification</b><BR>
	 */
	public void removeRenderNotification () throws CAException {
		_removeRenderNotification (new Integer (_ID()));
	}

	/**
	 * Gets the AUParameterInfo for this unit. This defaults to global scope, but
	 * many parameters are defined on in or out scopes, so those calls should be used.
	 * <BR><BR><b>CoreAudio::AudioUnitGetProperty</b><BR>
	 * @param parameterID the parameter about which information is sought.
	 * @return AUPropertyInfo object
	 * @deprecated should explicitly specify scope of parameter
	 */
	public AUParameterInfo getParameterInfo (int parameterID) throws CAException {
		return getParameterInfo (AUConstants.kAudioUnitScope_Global, parameterID);
	}

	/**
	 * Gets the AUParameterInfo for this unit.
	 * <BR><BR><b>CoreAudio::AudioUnitGetProperty</b><BR>
	 * @param inScope the scope of the parameter
	 * @param parameterID the parameter about which information is sought.
	 * @return AUPropertyInfo object
	 */
	public AUParameterInfo getParameterInfo (int inScope, int parameterID) throws CAException {
		AUParameterInfo info = new AUParameterInfo();
		getProperty (AUProperties.kAudioUnitProperty_ParameterInfo, 
						inScope, 
						parameterID, 
						info);
		return info;
	}

	/**
	 * Sets the specified property of this AudioUnit.
	 * <BR><BR><b>CoreAudio::AudioUnitSetProperty</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @param inPropertyValue the new value for the specified property
	 */
	public void setProperty (int inID, int inScope, int inElement, CAMemoryObject inPropertyValue) throws CAException {
		int res = AudioUnitSetProperty(_ID(), 
						inID, 
						inScope, 
						inElement, 
						CAObject.ID(inPropertyValue), 
						inPropertyValue.getSize());
		CAException.checkError (res);
	}

	/**
	 * Sets the specified property of this AudioUnit.
	 * <BR><BR><b>CoreAudio::AudioUnitSetProperty</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @param inPropertyValue the new value for the specified property
	 */
	public void setProperty (int inID, int inScope, int inElement, boolean inPropertyValue) throws CAException {
		synchronized (AUComponent.syncObject) {
			Accessor.setIntInPointer (AUComponent.firstArg4Ptr, 
										0, 
										(inPropertyValue ? 1 : 0));
			int res = AudioUnitSetProperty(_ID(), 
							inID, 
							inScope, 
							inElement, 
							AUComponent.firstArg4Ptr, 
							4);
			CAException.checkError (res);
		}
	}

	/**
	 * Sets the specified property of this AudioUnit.
	 * <BR><BR><b>CoreAudio::AudioUnitSetProperty</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @param inPropertyValue the new value for the specified property
	 */
	public void setProperty (int inID, int inScope, int inElement, int inPropertyValue) throws CAException {
		synchronized (AUComponent.syncObject) {
			Accessor.setIntInPointer (AUComponent.firstArg4Ptr, 
										0, 
										inPropertyValue);
			int res = AudioUnitSetProperty(_ID(), 
							inID, 
							inScope, 
							inElement, 
							AUComponent.firstArg4Ptr, 
							4);
			CAException.checkError (res);
		}
	}

	/**
	 * Sets the specified property of this AudioUnit.
	 * <BR><BR><b>CoreAudio::AudioUnitSetProperty</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @param inPropertyValue the new value for the specified property
	 */
	public void setProperty (int inID, int inScope, int inElement, float inPropertyValue) throws CAException {
		synchronized (AUComponent.syncObject) {
			Accessor.setFloatInPointer (AUComponent.firstArg4Ptr, 
										0, 
										inPropertyValue);
			int res = AudioUnitSetProperty(_ID(), 
							inID, 
							inScope, 
							inElement, 
							AUComponent.firstArg4Ptr, 
							4);
			CAException.checkError (res);
		}
	}

	/**
	 * Sets the specified property of this AudioUnit.
	 * <BR><BR><b>CoreAudio::AudioUnitSetProperty</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @param inPropertyValue the new value for the specified property
	 */
	public void setProperty (int inID, int inScope, int inElement, double inPropertyValue) throws CAException {
		synchronized (AUComponent.syncObject) {
			Accessor.setDoubleInPointer (AUComponent.secondArgPtr, 
										0, 
										inPropertyValue);
			int res = AudioUnitSetProperty(_ID(), 
							inID, 
							inScope, 
							inElement, 
							AUComponent.secondArgPtr, 
							8);
			CAException.checkError (res);
		}
	}

	/**
	 * Gets the current value of the specified property and returns the number
	 * of bytes that were written to the supplied memory object.
	 * <BR><BR><b>CoreAudio::AudioUnitGetProperty</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @return the class data for this AudioUnit.
	 */
	public CAMemoryObject getClassData () throws CAException {
		int size = getPropertySize (AUProperties.kAudioUnitProperty_ClassInfo, 0, 0);
		CAMemoryObject classData = new CAMemoryObject (size, false);
		getProperty (AUProperties.kAudioUnitProperty_ClassInfo, 0, 0, classData);
		return classData;
	}

	/**
	 * Sets the specified property of this AudioUnit.
	 * <BR><BR><b>CoreAudio::AudioUnitSetProperty</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @param inPropertyValue the new value for the specified property
	 */
	public void setClassData (CAMemoryObject classData) throws CAException {
		setProperty (AUProperties.kAudioUnitProperty_ClassInfo, 0, 0, classData);
	}

	/**
	 * This call is used to provide input data for the specified input channel 
	 * for a particular AudioUnit directly through the callback, rather through
	 * having another AudioUnit be the input for that particular input channel.
	 * <BR><BR><b>CoreAudio::AudioUnitSetProperty</b><BR>
	 * @param callback the callback that is called each time renderSlice is called on this audio unit
	 * @param inputNumber the input that the callback will use to supply input too.
	 */
	public void setInputCallback (AURenderCallback callback, int inputNumber) throws CAException {
		if (callback == null) {
			removeInputCallback (inputNumber);
	 	 	return;
	 	}
		synchronized (AUDispatcher.cbTable) {
			Integer key = new Integer (_ID());
			Integer inputKey = new Integer (inputNumber);
			Hashtable value = null;
			if (AUDispatcher.cbTable.containsKey(key)) {
				value = (Hashtable)AUDispatcher.cbTable.get (key);
					// remove old one
				if (value.containsKey(inputKey)) {
					AudioUnitSetProperty(_ID(), 
								AUProperties.kAudioUnitProperty_SetInputCallback, 
								AUConstants.kAudioUnitScope_Input, 
								inputNumber, 
								CAObject.ID(AUDispatcher.clearCBStruct), //remove callback 
								AUDispatcher.kNativeSize_InCB);
					((AUDispatcher)value.remove(inputKey)).cleanup();
				}
			}
		 	AUDispatcher disp = new AUDispatcher (this, callback, true);
			int res = AudioUnitSetProperty(_ID(), 
							AUProperties.kAudioUnitProperty_SetInputCallback, 
							AUConstants.kAudioUnitScope_Input, 
							inputNumber, 
							disp.ID(), 
							AUDispatcher.kNativeSize_InCB);
			if (res != 0) {
				disp.cleanup();
				CAException.checkError (res);
			}
			else {
				if (value == null)
					value = new Hashtable();
				value.put (inputKey, disp);
				AUDispatcher.cbTable.put (key, value);
			}
		}
	 }
	 		
	/**
	 * This call removes an input callback previously assigned to provide data for the specified
	 * input channel.
	 * <BR><BR><b>CoreAudio::AudioUnitSetProperty</b><BR>
	 * @param inputNumber the input that the callback was used to supply input too.
	 */
	 public void removeInputCallback (int inputNumber) throws CAException {
		synchronized (AUDispatcher.cbTable) {
			Integer key = new Integer (_ID());
			if (AUDispatcher.cbTable.containsKey(key)) {
				Integer inputKey = new Integer (inputNumber);
				Object disp = null;
				Hashtable value = (Hashtable)AUDispatcher.cbTable.get (key);
				if (value.containsKey(inputKey))
					disp = value.remove (inputKey); 
				if (value.isEmpty())
					AUDispatcher.cbTable.remove (key);
				if (disp != null) {
					int res = AudioUnitSetProperty(_ID(), 
								AUProperties.kAudioUnitProperty_SetInputCallback, 
								AUConstants.kAudioUnitScope_Input, 
								inputNumber, 
								CAObject.ID(AUDispatcher.clearCBStruct), //remove callback 
								AUDispatcher.kNativeSize_InCB);
					((AUDispatcher)disp).cleanup();
					CAException.checkError (res);
				}
			}
		}
	}
	 	
	/**
	 * This call removes all input callbacks previously assigned to provide data for any
	 * input channels.
	 * <BR><BR><b>CoreAudio::AudioUnitSetProperty</b><BR>
	 */
	 public void removeAllInputCallbacks () throws CAException {
		synchronized (AUDispatcher.cbTable) {
			Enumeration keys = AUDispatcher.cbTable.keys();
			while (keys.hasMoreElements()) {
				Integer key = (Integer)keys.nextElement();
				if (key.intValue() == _ID()) {
					Hashtable table = (Hashtable)AUDispatcher.cbTable.get (key);
					Enumeration inputs = table.keys();
					while (inputs.hasMoreElements()) {
						Integer theKey = (Integer)inputs.nextElement();
						if (theKey.intValue() != -1) {
							int res = AudioUnitSetProperty(_ID(), 
										AUProperties.kAudioUnitProperty_SetInputCallback, 
										AUConstants.kAudioUnitScope_Input, 
										theKey.intValue(), 
										CAObject.ID(AUDispatcher.clearCBStruct), //remove callback 
										AUDispatcher.kNativeSize_InCB);
							((AUDispatcher)table.remove (theKey)).cleanup();
							inputs = table.keys();//reset iter
						} else {
							// we found the notification key
							// check if it is the only entry!!!
							if (table.size() == 1)
								break;
						}
					}
					if (table.isEmpty())
						AUDispatcher.cbTable.remove(key);
					break;
				}
			}
		}
	}
	
	/**
	 * Gets the specified parameter of this AudioUnit.
	 * <BR><BR><b>CoreAudio::AudioUnitGetParameter</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @return the value of the property
	 */
	public float getParameter (int inID, int inScope, int inElement) throws CAException {
		synchronized (AUComponent.syncObject) {
			int res = AudioUnitGetParameter(_ID(), 
								inID, 
								inScope, 
								inElement, 
								AUComponent.firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getFloatFromPointer (AUComponent.firstArg4Ptr, 0);
		}
	}

	/**
	 * Sets the specified parameter of this AudioUnit.
	 * <BR><BR><b>CoreAudio::AudioUnitSetParameter</b><BR>
	 * @param inID the identifier for the property
	 * @param inScope the scope of the specified property
	 * @param inElement the element of the specified property
	 * @param inValue the value of the property
	 * @param inBufferOffset this is an offset into the buffer so that the parameter change value is scheduled within a buffer
	 */
	public void setParameter (int inID, int inScope, int inElement, float value, int inBufferOffset) throws CAException {
		int res = AudioUnitSetParameter(_ID(), inID, inScope, inElement, value, inBufferOffset);
		CAException.checkError (res);
	}

	/** Resets the parameter values of an AudioUnit. If scope is global then reinitializes a device to its "default" state.
	 * <BR><BR><b>CoreAudio::AudioUnitReset</b><BR>
	 * @param inScope the scope
	 * @param inElement the element
	 */
	public void reset (int inScope, int inElement) throws CAException {
		int res = AudioUnitReset(_ID(), inScope, inElement);
		CAException.checkError (res);
	}
	
	/** 
	 * Instructs the audio unit to render a slice of audio data.
	 * <BR><BR><b>CoreAudio::AudioUnitRenderSlice</b><BR>
	 * @param inActionFlags flags that instruct the unit on how to act on the buffer
	 * @param inTimeStamp the time for the start of the rendered audio output.
	 * @param inOutputBusNumber requests that audio be rendered for a particular audio output of the AudioUnit.
	 * @param dataBuffer this is the data buffer to be processed
	 */
	public void renderSlice (int inActionFlags, AudioTimeStamp inTimeStamp, int inOutputBusNumber, AudioBuffer dataBuffer) throws CAException {
		int res = AudioUnitRenderSlice(_ID(), 
						inActionFlags, 
						CAObject.ID(inTimeStamp), 
						inOutputBusNumber, 
						CAObject.ID(dataBuffer)); 
		CAException.checkError (res);
	}
	
	private Hashtable idTable;
	
	/**
	 * Add a listener to the specified property of this AudioUnit. The listener
	 * will be notified whenever the value of that property changes.
	 * <BR><BR><b>CoreAudio::AudioUnitAddPropertyListener</b><BR>
	 * @param inID the property to listen for changes too.
	 * @param listener the listener that is notified of changes
	 */
	public void addPropertyListener (int inID, AUPropertyListener listener) throws CAException {
		if (listener == null)
			throw new CAException ("Must specifiy a listener to add");
		
		if (idTable == null)
			idTable = new Hashtable();
			
		synchronized (idTable) {
			Integer key = new Integer (inID);
			Object el = idTable.get (key);
			if (el != null) {
				Hashtable listeners = (Hashtable)el;
				Object el1 = listeners.get (listener);
				if (el1 != null)
					throw new CAException ("Can't add same listener to same propertyID");
			}
			AUDispatcher disp = new AUDispatcher (this, listener);
			
			int res = AudioUnitAddPropertyListener(_ID(), inID, disp.ID(), disp.refCon());
			if (res != 0) {
				disp.cleanup();
				throw new CAException (res);
			}
			if (el == null) {
				Hashtable listeners = new Hashtable();
				listeners.put (listener, disp);
				idTable.put (key, listeners);
			} else {
				Hashtable listeners = (Hashtable)el;
				listeners.put (listener, disp);
			}
		}
	}
	
	/**
	 * Remove a listener to the specified property of this AudioUnit.
	 * <BR><BR><b>CoreAudio::AudioUnitRemovePropertyListener</b><BR>
	 * @param inID the property to listen for changes too.
	 * @param listener the listener that is notified of changes
	 */
	public void removePropertyListener (int inID, AUPropertyListener listener) throws CAException {
		if (listener == null)
			throw new CAException ("Must specifiy a listener to remove");
			
		synchronized (idTable) {
			Integer key = new Integer (inID);
			Object el = idTable.get (key);
			if (el == null)
				return;
			Hashtable listeners = (Hashtable)el;
			el = listeners.get (listener);
			if (el == null)
				return;
			AUDispatcher disp = (AUDispatcher)el;
			
			int res = AudioUnitRemovePropertyListener(_ID(), inID, disp.ID());
			CAException.checkError (res);

			disp.cleanup();
			listeners.remove (listener);
			if (listeners.isEmpty())
				idTable.remove (key);
			if (idTable.isEmpty())
				idTable = null;
		}
	}
	
	/**
	 * Removes all of the listener to the specified property of this AudioUnit.
	 * <BR><BR><b>CoreAudio::AudioUnitAddPropertyListener</b><BR>
	 * @param inID the property to listen for changes too.
	 */
	public void removeAllPropertyListeners (int inID) throws CAException {
		synchronized (idTable) {
			Integer key = new Integer (inID);
			Object el = idTable.get (key);
			if (el == null)
				return;
			removeAllPropertyListeners (key, el);			
			idTable.remove (key);
			if (idTable.isEmpty())
				idTable = null;
		}
	}	
	
	private void removeAllPropertyListeners (Integer key, Object value) throws CAException {
		Hashtable listeners = (Hashtable)value;
		Enumeration iter = listeners.elements();
		while (iter.hasMoreElements()) {
			AUDispatcher disp = (AUDispatcher)iter.nextElement();
		
			int res = AudioUnitRemovePropertyListener(_ID(), key.intValue(), disp.ID());
			CAException.checkError (res);

			disp.cleanup();
		}
		listeners.clear();
	}

	/**
	 * Removes all of the listeners of this AudioUnit.
	 * <BR><BR><b>CoreAudio::AudioUnitAddPropertyListener</b><BR>
	 */
	public void removeAllPropertyListeners () throws CAException {
		synchronized (idTable) {
			Enumeration iter = idTable.keys();
			while (iter.hasMoreElements()) {
				Integer key = (Integer)iter.nextElement();
				removeAllPropertyListeners (key, idTable.get(key));
			}
			idTable.clear();
			idTable = null;
		}
	}	


//_ NATIVE METHODS
//CompLib
	private static native int GetComponentVersion (int ci);
//AULib
	private static native int AudioUnitInitialize(int ci);
	private static native int AudioUnitUninitialize(int ci);
	private static native int AudioUnitGetPropertyInfo(int ci,
											int	inID,
											int	inScope,
											int	inElement,
											int	outDataSizePtr,
											int outWritablePtr);
  	private static native int AudioUnitGetProperty(int ci, int inID, int inScope, int inElement, int outDataPtr, int outDataSizePtr);
 	private static native int AudioUnitSetProperty(int ci, int inID, int inScope, int inElement, int inDataPtr, int inDataSize);
	private static native int AudioUnitSetRenderNotification(int ci, int inProc, int inProcRefCon);
  	private static native int AudioUnitGetParameter(int ci, int inID, int inScope, int inElement, int outFloatValuePtr);
  	private static native int AudioUnitSetParameter(int ci, int inID, int inScope, int inElement, float inValue, 
  											int inBufferOffsetInFrames);
  	private static native int AudioUnitReset(int ci, int inScope, int inElement);
  	private static native int AudioUnitRenderSlice(int ci, int inActionFlags, int inTimeStampPtr, 
  											int inOutputBusNumber, int ioDataPtr); 

	private static native int AudioUnitAddPropertyListener(int ci, int inID, int inProc, int inProcRefCon);
	private static native int AudioUnitRemovePropertyListener(int ci, int inID,int inProc);
} 

/*
 */

