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
/*
		File:			FindUnits.java
		
		Description:	Uses the component manager to find AudioUnits and print out information about them
		
		Author:			William Stewart
*/

import com.apple.component.*;
import com.apple.audio.units.*;
import com.apple.audio.*;

public class FindUnits {
	// You can run this on all found units or on a specific unit

	public static final String kErrorTagStr = "# # #";
	public static final String kWarningTagStr = "@ @ @";
	// Unexpected results being with the string kErrorTagStr
	// so you can search the output summary for those errors
	// If a result is feasible correct but may benefit from examination
	// the result is tagged with the kWarningTagStr
	private static final boolean doAllUnits = true;
	
	public static void main (String[] args) {
		try {
			ComponentDescription auDesc = new ComponentDescription ();
			auDesc.setType (AUConstants.kAudioUnitComponentType);
			if (doAllUnits == false) { //define a subtype for a particular unit
				auDesc.setSubType (AUConstants.kAudioUnitSubType_Effect);
				auDesc.setManufacturer (AUConstants.kAudioUnitID_Delay);
			}
			AUComponent comp = AUComponent.findAU (auDesc);
			while (comp != null) {
				System.out.println ("* * * * * * * * * * * * * * *");
				System.out.println (comp.getInfo().getName());
				System.out.println (comp.getInfo().getInformationString());
				System.out.println (comp);
				AudioUnit theUnit = null;
				try {
					theUnit = comp.open();
					System.out.println (theUnit + "\n");
					ParameterValidation.validate (theUnit);
				} catch (CAException e) {
					System.out.println (kErrorTagStr + e + "\n");
				}
				if (doAllUnits)
					comp = comp.findNextAU (auDesc);
				else
					comp = null; //finish the loop in the single case
				System.out.println ("* * * * * * * * * * * * * * *\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
