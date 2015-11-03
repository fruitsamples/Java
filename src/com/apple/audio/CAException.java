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
//  CAException.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio;

import java.io.*;
import java.lang.reflect.Field;
import com.apple.audio.util.*;

/** A General catch all class that is used to signal errors that occur from CoreAudio calls */
public class CAException extends Exception {
//_________________________ CLASS METHODS
	/** Returns a string that represents the error code of the current CAException.
	 * This will return the name of the error code as represented in the com.apple.audio.CAErrors class.
	 * If the error code is unknown then the string "Unknown Error Code" will be returned.
	 * @return a string that represents the current error code of the CAException.
	 */
	public static String errorCodeToString (int eCode) {
		try {
			Class c = Class.forName ("com.apple.audio.CAErrors");
			Field f[] = c.getFields();
			for (int i = 0; i < f.length; i++) {
				if (f[i].getInt(null) == eCode)
					return f[i].getName();
			}
		} catch (Exception e) {
			return "Exception generated: " + e.toString();
		}
		return "Unknown Error Code";
	}

	/**
	 * Creates an exception with a message that could contain information 
	 * that could be displayed to the user.
	 * @param str The message that could be displayed to the user
	 */
	public CAException (String str) { super(str); }
	
	/**
	 * Creates an exception with a specific error number
	 * @param val the error number
	 */
	public CAException (int val) { 
		this ((new Integer(val)).toString()); 
		eCode = val;
	}

	/**
	 * This method will throw an exception if the incoming err argument is 
	 * a non-zero value.
	 * @param err the result or error code if non-zero an exception is thrown
	 */
	public static void checkError (int err) throws CAException {
		if (err != 0) {
			throw new CAException (err);
		}
	}
	
//_________________________ INSTANCE VARIABLES
	/* This is the result code if the error was a number (otherwise its value will be zero)*/
	private int eCode = 0;
	
//_________________________ INSTANCE METHODS	
	/** Return the error code that generated the exception. If there was no CA error code number known
	 * this value will be 0 in which case the message contains information about the exception.
	 * @return an int
	 */
	public int getErrorCode () { return eCode; }
	
	/** Returns a string that represents the error code of the current CAException.
	 * This will return the name of the error code as represented in the com.apple.audio.CAErrors class.
	 * If the error code is unknown then the string "Unknown Error Code" will be returned.
	 * @return a string that represents the current error code of the CAException.
	 */
	public String errorCodeToString () {
		return errorCodeToString (eCode);
	}
		
	/** Print current build information and current exception details.*/
	public String toString () {
		String str = (eCode == 0 ? getMessage() : Integer.toString(eCode) + "=" + errorCodeToString ());
		return getClass().getName() + CASession.buildInfo() + "," + str;
		 	//+ ",QT.vers:" + Integer.toHexString (QTSession.getQTVersion());
	}
}

/*
 */
