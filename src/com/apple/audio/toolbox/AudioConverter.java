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
//  AudioConverter.java
//  CoreAudio.proj
//
//  Copyright (c) 2001 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.toolbox;

import com.apple.audio.*;
import com.apple.audio.util.*;
import com.apple.audio.jdirect.Accessor;
/**
 * This class represents an AudioConverter that converts formats of audio data.
 * Currently both float<=>int and Sample Rate conversions are supported.
 * If the fillBuffer method is called, then either <CODE>reset</CODE> or <CODE>cleanup</CODE> methods
 * <b>must</b> be called to cleanup resources that are allocated to allow the AudioConverter object to be reclaimed
 * by garbage collection.
 */
public final class AudioConverter extends CAObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}

	// synchronized around firstOutArg_4 -> some methods use both arg caches, 
	// some use just _4, some just _8
	private static final Object syncObject = new Object();

	private static final int firstArg4Ptr = JNIToolbox.malloc (4);
	private static final int secondArg4Ptr = JNIToolbox.malloc (4);

//_________________________ STATIC METHODS
	/**
	 * Create an AudioConverter that will convert from the specified source format to the specified
	 * destination format.
 	 * <BR><BR><b>CoreAudio::AudioConverterNew</b><BR><BR>
	 * @param sourceFormat describes the format of the source
	 * @param destinationFormat describes the format to convert too.
	 */
	public AudioConverter (AudioStreamDescription sourceFormat, AudioStreamDescription destinationFormat) throws CAException {
		super (allocate (sourceFormat, destinationFormat));
	}
	
	private static int allocate (AudioStreamDescription sourceFormat, AudioStreamDescription destinationFormat) throws CAException {
		synchronized (syncObject) {
			int err = AudioConverterNew (CAObject.ID(sourceFormat), 
									CAObject.ID(destinationFormat), 
									firstArg4Ptr);
			CAException.checkError (err);
			return Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
	}
		
//_________________________ INSTANCE VARIABLES	
	private ATDispatcher disp;
	private boolean doingConversion = false;
	private final int convertObjPtr = JNIToolbox.malloc (4);
	
//_________________________ INSTANCE METHODS
	/**
	 * This call will cleanup resources allocated for the conversion process.
	 */
	public synchronized void cleanup () {
		if (doingConversion)
			return;
		if (disp != null) {
			disp.cleanup();
			disp = null;
		}
	}
	
	protected synchronized void preDispose () throws CAException {
		if (doingConversion)
			throw new CAException ("Can't dispose AudioConverter whilst it is converting");
		cleanup();
	}

	/**
	 * Reset the converter's buffers, flushing any saved or unread state that the converter may have
 	 * <BR><BR><b>CoreAudio::AudioConverterReset</b><BR><BR>
	 */
	public void reset () throws CAException {
		int res = AudioConverterReset (_ID());
		CAException.checkError (res);
		cleanup();
	}
	
	/**
	 * Returns true if this property is writable.
 	 * <BR><BR><b>CoreAudio::AudioConverterGetPropertyInfo</b><BR><BR>
	 */
	public boolean getPropertyInfo_writable (int inPropertyID) throws CAException {
		synchronized (syncObject) {
			int res = AudioConverterGetPropertyInfo (_ID(),
								inPropertyID, 
								firstArg4Ptr,
								secondArg4Ptr);
			CAException.checkError (res);
			return Accessor.getByteFromPointer (secondArg4Ptr, 0) != 0;
		}
	}

	/**
	 * Returns the size required to hold this property's value.
 	 * <BR><BR><b>CoreAudio::AudioConverterGetPropertyInfo</b><BR><BR>
	 */
	public int getPropertyInfo_size (int inPropertyID) throws CAException {
		synchronized (syncObject) {
			int res = AudioConverterGetPropertyInfo (_ID(),
								inPropertyID, 
								firstArg4Ptr,
								secondArg4Ptr);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
	}

	/**
	 * Returns the value of the supplied property. The value of the property must be no bigger than a <CODE>int</CODE>
 	 * <BR><BR><b>CoreAudio::AudioConverterGetProperty</b><BR><BR>
	 */
	public int getIntProperty (int inPropertyID) throws CAException {
		synchronized (syncObject) {
			Accessor.setIntInPointer (firstArg4Ptr, 0, 4);
			int res = AudioConverterGetProperty (_ID(),
								inPropertyID, 
								firstArg4Ptr,
								secondArg4Ptr);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (secondArg4Ptr, 0);
		}
	}

	/**
	 * Retrieve the value of the supplied property. The value of the property 
	 * fit into the supplied propertyValue which will contain the value on return. The
	 * argument returns the number of bytes that were written into the propertyValue.
 	 * <BR><BR><b>CoreAudio::AudioConverterGetProperty</b><BR><BR>
	 */
	public int getProperty (int inPropertyID, CAMemoryObject propertyValue) throws CAException {
		synchronized (syncObject) {
			Accessor.setIntInPointer (firstArg4Ptr, 0, propertyValue.getSize());
			int res = AudioConverterGetProperty (_ID(),
								inPropertyID, 
								firstArg4Ptr,
								CAObject.ID(propertyValue));
			CAException.checkError (res);
			return Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
	}

	/**
	 * Sets the value of the supplied property.
 	 * <BR><BR><b>CoreAudio::AudioConverterSetProperty</b><BR><BR>
	 */
	public void setProperty (int inPropertyID, int propertyValue) throws CAException {
		synchronized (syncObject) {
			Accessor.setIntInPointer (firstArg4Ptr, 0, 4);
			Accessor.setIntInPointer (secondArg4Ptr, 0, propertyValue);
			int res = AudioConverterSetProperty (_ID(),
								inPropertyID, 
								firstArg4Ptr,
								secondArg4Ptr);
			CAException.checkError (res);
		}
	}

	/**
	 * Sets the value of the supplied property.
	 * <BR><BR><b>CoreAudio::AudioConverterSetProperty</b><BR><BR>
	 */
	public void setProperty (int inPropertyID, CAMemoryObject propertyValue) throws CAException {
		synchronized (syncObject) {
			Accessor.setIntInPointer (firstArg4Ptr, 0, propertyValue.getSize());
			int res = AudioConverterSetProperty (_ID(),
								inPropertyID, 
								firstArg4Ptr,
								CAObject.ID(propertyValue));
			CAException.checkError (res);
		}
	}
	
	/**
	 * Does a conversion process where the caller supplies data to the conversion process through the supplied AudioConverterDataSupplier interface.
	 * This conversion process finishes when either there is no more input data supplied by the inputProc, or there is no more room
	 * left to write the converted data too in the supplied outputData. <CODE>fillBuffer</CODE> can be called again for the conversion
	 * process to continue without supplying additional input data. The method returns the number of bytes written to the supplied outputData.
	 * <P>
	 * When you are finished with the AudioConverter completely you <b>must</b> call either the reset() or cleanup() method to cleanup
	 * resources used in the conversion process. Typically, after a single pass of a conversion you would call reset(), and only call
	 * cleanup() when you're finished with using the AudioConverter.
	 * <BR><BR><b>CoreAudio::AudioConverterFillBuffer</b><BR><BR>
	 */
	public int fillBuffer (AudioConverterDataSupplier inputProc, CAMemoryObject outputData) throws CAException {
		return fillBuffer (inputProc, outputData, 0, outputData.getSize());
	}

	/**
	 * Does a conversion process where the caller supplies data to the conversion process through the supplied AudioConverterDataSupplier interface.
	 * This conversion process finishes when either there is no more input data supplied by the inputProc, or there is no more room
	 * left to write the converted data too in the supplied outputData. <CODE>fillBuffer</CODE> can be called again for the conversion
	 * process to continue without supplying additional input data. The method returns the number of bytes written to the supplied outputData.
	 * <P>
	 * When you are finished with the AudioConverter completely you <b>must</b> call either the reset() or cleanup() method to cleanup
	 * resources used in the conversion process. Typically, after a single pass of a conversion you would call reset(), and only call
	 * cleanup() when you're finished with using the AudioConverter.
	 * <P>
	 * This version allows you to fill part of a buffer by providing an offset into the outputData buffer and the maximum number of bytes to use.
	 * <BR><BR><b>CoreAudio::AudioConverterFillBuffer</b><BR><BR>
	 */
	public int fillBuffer (AudioConverterDataSupplier inputProc, CAMemoryObject outputData, int byteOffset, int numBytesToUse) throws CAException {
		if (byteOffset < 0 
			|| byteOffset + numBytesToUse > outputData.getSize())
				throw new CAException (CAErrors.paramErr);
		synchronized (this) {
			doingConversion = true;
			if (disp == null)
				disp = new ATDispatcher (this, inputProc);
			
			Accessor.setIntInPointer (convertObjPtr, 0, numBytesToUse);
			int res = AudioConverterFillBuffer(_ID(), 
							disp.ID(),
							0,//inInputDataProcUserData, 
							convertObjPtr, 
							(CAObject.ID(outputData) + byteOffset));
			doingConversion = false;
			CAException.checkError(res);
			return Accessor.getIntFromPointer (convertObjPtr, 0);
		}
	}
	
	/**
	 * Converts the contents of the input buffer, placing the results into the output buffer.
	 * The user must supply an output buffer that is at least big enough to hold the results of the entire conversion of the source.
	 * The method will return the number of bytes that were actually written to the output buffer by the conversion process.
	 * <BR><BR><b>CoreAudio::AudioConverterSetProperty</b><BR><BR>
	 */
	public int convertBuffer (CAMemoryObject inputData, CAMemoryObject outputData) throws CAException {
		return convertBuffer (inputData, 0, inputData.getSize(), outputData, 0, outputData.getSize());
	}

	/**
	 * Converts the contents of the input buffer, placing the results into the output buffer.
	 * The user must supply an output buffer that is at least big enough to hold the results of the entire conversion of the source.
	 * The method will return the number of bytes that were actually written to the output buffer by the conversion process.
	 * <P>
	 * This version allows you to fill or convert part of a buffer by providing an offset into the inputData or outputData buffer 
	 * and the maximum number of bytes to use for each buffer.
	 * <BR><BR><b>CoreAudio::AudioConverterSetProperty</b><BR><BR>
	 */
	public int convertBuffer (CAMemoryObject inputData, int inputByteOffset, int numInputBytesToUse,
							CAMemoryObject outputData, int outputByteOffset, int numOutputBytesToUse) throws CAException {
		
		if (inputByteOffset < 0 
			|| inputByteOffset + numInputBytesToUse > outputData.getSize()
			|| outputByteOffset < 0 
			|| outputByteOffset + numOutputBytesToUse > outputData.getSize())
				throw new CAException (CAErrors.paramErr);
		
		synchronized (this) {
			Accessor.setIntInPointer (convertObjPtr, 0, numOutputBytesToUse);
			int res = AudioConverterConvertBuffer(_ID(), 
							numInputBytesToUse, 
							(CAObject.ID(inputData) + inputByteOffset), 
							convertObjPtr, 
							(CAObject.ID(outputData) + outputByteOffset));
			CAException.checkError (res);
			return Accessor.getIntFromPointer (convertObjPtr, 0);
		}
	}
	
	
//_NATIVE METHODS
	private static native int AudioConverterNew (int inSourceFormatPtr, int inDestinationFormatPtr, int outAudioConverterPtr);
	private static native int AudioConverterReset (int inAudioConverter);
	private static native int AudioConverterGetPropertyInfo (int inAudioConverter, int inPropertyID, int outSizeIntPtr, int outWritableBooleanPtr);
	private static native int AudioConverterGetProperty (int inAudioConverter, int inPropertyID, int ioPropertyDataSizeIntPtr, int outPropertyDataVoidPtr);
	private static native int AudioConverterSetProperty (int inAudioConverter, int inPropertyID, int inPropertyDataSize, int inPropertyDataPtr);
	private static native int AudioConverterFillBuffer(int inAudioConverter, int inInputDataProc, int inInputDataProcUserData, int ioOutputDataSizeIntPtr, int outOutputDataPtr);
	private static native int AudioConverterConvertBuffer(int inAudioConverter, int inInputDataSize, int inInputDataPtr, int ioOutputDataSizePtr, int outOutputDataPtr);
}

/*
 */
