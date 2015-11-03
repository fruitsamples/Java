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

/**
 * This class provides information about the version of both the CoreAudio native run-time and the JCoreAudio release
 * as well as managing the security protocol for using CoreAudio when a java.lang.SecurityManager is present within
 * the runtime context of the java application or applet.
 * <P>
 * The following security policies are enforced when a security manager is present.
 * <UL>
 * <LI>User code is not allowed to define classes in the com.apple.audio.* packages
 * <LI>Only signed user code is permitted to define subclasses of the CAObject class, when
 * that code is loaded by a ClassLoader. If the code is not loaded by a ClassLoader (ie. the
 * class was pre-installed) then this restriction is lifted.
 * </UL>
 */
public final class CASession {
//_________________________ INITIALIZATION
	static {
		if (System.getSecurityManager() != null) {
			class PrivelegedAction1 {
				void establish () {
					java.security.AccessController.doPrivileged (new java.security.PrivilegedAction () {
						public Object run () {
							System.setProperty ("restrict.package.definition.com.apple.audio", "true");
							return null;
						}
					});
				}
			}
			new PrivelegedAction1().establish();
		}
	}
	
//_________________________ BUILD INFORMATION
	private CASession () {} //can't make one

	/** The version of CoreAudio */
	public final static int releaseVersion = 1;
	/** The sub-version of CoreAudio */
	public final static int releaseSubVersion = 3;
	/** The qualifier for the sub-version of CoreAudio */
	public final static int releasedQualifyingSubVersion = 0;
	/** The build of CoreAudio given same version/subversion. A value of 0 means final for that buildStage.*/
	public final static int buildNumber =1;
	/** The buildType of CoreAudio given same version/subversion.
	* Possible values are 'g' for GoldenMaster, 'f' for final candidate, 'b' for beta, 'a' for alpha
	*/
	public final static String buildStage = "g";	
	
	/** Name of CoreAudioJava bundle */
	public static final String caBundleName = "/System/Library/QuickTimeJava/CoreAudio.bundle/CoreAudio";
	
	/** This returns an info string about the current version/build of JCoreAudio */
	public static String buildInfo () {
		String bStr = (buildNumber > 0 ? Integer.toString(buildNumber) : "" );
		return "[JCoreAudio:" + releaseVersion + "." + releaseSubVersion + "." + releasedQualifyingSubVersion + buildNumber + bStr + "]";
	}

//_________________________ SECURITY MANAGEMENT
	private static Class theSignedClass = null;
	static final boolean checkSecurity = System.getSecurityManager() != null;
	
	/**
	 * If there is no securityManager this method returns false. 
	 * If there is a securityManager and a signedClass is registered 
	 * (see <CODE>CASession.registerSignedClass</CODE>) this method returns false.
	 * If there is a securityManager and <B>no</B> signedClass this method returns true,
	 * and code running in this situation has restrictions around some of the 
	 * operations that can be performed.
	 * @return true if the user code is running under a security manager that restricts
	 * the actions the user code can do.
	 */
	public static boolean hasSecurityRestrictions () {
			// no security manager so no restrictions
		if (checkSecurity == false)
			return false;
			// there is a security manager so if signed class is registered no restrictions
		if (theSignedClass != null)
			return false;
			// there is a security manager AND no signed class is registered so has restrictions
		return true;
	}

	/**
	 * The application passes in a Class that has been signed, and thus gains priveleges to 
	 * perform specific actions in JCoreAudio that would otherwise be prohibited. The application 
	 * or applet must also register the class in the normal method to be granted priveleges
	 * by an existing java.lang.SecurityManager.
	 * <P>
	 * Apps that are running in a secure context should register their class before performing any
	 * other actions on Core Audio objects.
	 * @param clazz the singed Class that is registered. If null, this will remove previously granted security priveleges. 
	 * @return true if the class is signed and thus registered. 
	 * Returns false if the class is null, or is not signed.
	 */
	public static boolean registerSignedClass (Class clazz) {
		if (clazz == null) {
			theSignedClass = null;
			return false;
		} else {
	    	Object[] sig = clazz.getSigners();
	    	if (sig != null)
	    		theSignedClass = clazz;
	    	return (sig != null);
	    }
	}
}

/*
 */ 
