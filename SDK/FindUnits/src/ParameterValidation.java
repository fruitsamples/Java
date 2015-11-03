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
/* ParameterValidation.java */

import com.apple.audio.*;
import com.apple.audio.units.*;

public class ParameterValidation {
	private static String[] scopeStrs = {
		"Global",
		"Input",
		"Ouptut",
		"Group"
	};
	
	public static void validate (AudioUnit theUnit) {
		System.out.print ("Parameter Information:");
		getParameters (theUnit, 0);
		System.out.print ("Parameter Information:");
		getParameters (theUnit, 1);
		System.out.print ("Parameter Information:");
		getParameters (theUnit, 2);
		System.out.print ("Parameter Information:");
		getParameters (theUnit, 3);
	}

	static void getParameters (AudioUnit theUnit, int scope) {
		System.out.print (scopeStrs[scope] + " scope:");
		try {
			int[] list = theUnit.getParameterList(scope);
			doParameters (theUnit, list, scope);
		} catch (CAException ex) {
			System.out.println (FindUnits.kErrorTagStr + " Can't get parameters in scope:" + ex.getErrorCode());
		}
	}
	
	static void doParameters (AudioUnit theUnit, int[] list, int scope) throws CAException {
		if (list != null) {
			System.out.println ("num params:" + list.length);
			for (int i = 0; i < list.length; ++i) {
				System.out.println ("param:" + list[i]);
				AUParameterInfo info = theUnit.getParameterInfo (scope, list[i]);
				System.out.println (info);
				System.out.println ("Get/Set test");
				validateParameterValues (theUnit, list[i], scope, info);
			}
		} else
			System.out.println ("No parameters in this scope");
	}

	static void validateParameterValues (AudioUnit theUnit, int paramID, int scope, AUParameterInfo info) {
		try {
			theUnit.initialize();
			float value = theUnit.getParameter (paramID, scope, 0);
			boolean correct = info.getDefaultValue() == value;
			if (correct)
				System.out.println ("Default Value Is Correct");
			else
				System.out.println (FindUnits.kErrorTagStr + " Default Value incorrect:" + value);
			if ((info.getFlags() & AUConstants.kAudioUnitParameterFlag_IsWritable) != 0) {
				theUnit.setParameter (paramID, scope, 0, info.getMinValue(), 0/*buffer offset*/);
				value = theUnit.getParameter (paramID, scope, 0);
				if (correct)
					System.out.println ("Set Min Value Is Correct");
				else
					System.out.println (FindUnits.kErrorTagStr + " Min Value Set is incorrect:" + value);
		
				theUnit.setParameter (paramID, scope, 0, info.getMaxValue(), 0/*buffer offset*/);
				value = theUnit.getParameter (paramID, scope, 0);
				if (correct)
					System.out.println ("Set Max Value Is Correct");
				else
					System.out.println (FindUnits.kErrorTagStr + " Max Value Set is incorrect:" + value);
			} else {
				System.out.println (FindUnits.kWarningTagStr + "Parameter is Read Only?");
			}
			
			theUnit.uninitialize();
		} catch (CAException e) {
			System.out.println (FindUnits.kErrorTagStr + " Problem with parameter setting:" + e.getErrorCode());
		}
	}
}
