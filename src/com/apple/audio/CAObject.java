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
//  CAObject.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio;

/**
 * This is a class that defines an unique identifier for CA objects.
 */
public abstract class CAObject {
//_________________________ CLASS METHODS
	/*
	 * This call returns an unique identifer for the object. If the incoming
	 * object is null it returns 0;
	 */
	public static final int ID (CAObject id) {
		return (id == null ? 0 : id.nativeRep);
	}
	
	/**
	 * Subclasses pass in the native identifier that will be disposed
	 * when this object is finalized
	 */
	protected CAObject (int id) {
		this (id, null, 0);
	}
	
	/**
	 * Used in special cases where a native object owns this object, and 
	 * this native owner is required when this object is to be disposed of.
	 * Subclasses pass in the native identifier that will be disposed
	 * when this object is finalized
	 */
	protected CAObject (int id, int ownerID) {
		this (id, null, ownerID);
	}
	
	/**
	 * Subclasses pass in the native identifier and an object that may own this object.
	 * If the owner ref object is NOT null, then this object's native identifier will not be disposed
	 * when this object is finalized
	 */
	protected CAObject (int id, Object ref) {
		this (id, ref, 0);
	}
	
	private CAObject (int id, Object ref, int ownerID) {
		super();
		_doSC();		
		nativeRep = id;
		this.ref = ref;
		if (ref == null) {
			CAObjectManagement.add (this, nativeRep, ownerID);
		}
	}
	
//_________________________ INSTANCE VARIABLES
	private transient int nativeRep = 0;
	private transient Object ref = null;
//	protected transient final int sessionAllocated = CAObjectManagement.sessionID;
	
//_________________________ INSTANCE METHODS	
	private final void _doSC() {
		if (CASession.hasSecurityRestrictions()) {
			Class clazz = getClass();
			if (clazz.getClassLoader() != null) {
				if (clazz.getName().startsWith ("com.apple.audio.") == false)
					throw new SecurityException ("Only when a signed class is registered can user code define subclasses that are loaded by a ClassLoader of CAObject");
			}
		}
	}
	
	protected final int _ID () {
		if (nativeRep == 0) {
			String className = getClass().getName();
			throw new CANullPointerException ("The CA native object represented by this CAJava object is no longer valid:" + className);
		}
		return nativeRep; 
	}
	
	protected final Object getOwner () {
		return ref;
	}
		
	protected final void finalize () throws Throwable {
		doDispose();
		super.finalize();
	}
	
	private final void doDispose () throws CAException {
		try {
			if (nativeRep != 0 && ref == null) {
				preDispose();
				if (CAObjectManagement.isMember(nativeRep))
					CAObjectManagement.remove (nativeRep);
			}
		} finally {
			nativeRep = 0;
		}
	}
	
	/**
	 * This call will dispose the underlying CoreAudio object that is associated with the CAJava object.
	 * After this call the CAJava object is no longer usable and will throw CARuntimeExceptions to indicate
	 * that the CAJava object's native object has been disposed.
	 */
	public void dispose () throws CAException {
		doDispose();
	}
	
	/**
	 * This is called in the disposal of that native identifier, generally in the finalizer call.
	 * A subclass can use this method to do any specific work that it requires before the native
	 * representation of the CAObject is disposed and removed.
	 */
	protected void preDispose () throws CAException {}
	
	/**
	 * @return true if the CAObject's native resource is still valid.
	 */
	protected final boolean isValid () {
		return nativeRep != 0;
	}
	
	/** Indicates whether some other object is equal to this one.
	If true, indicates that the two objects refer to the same native identifier.*/
	public boolean equals (Object anObject) {
		if (this == anObject)
			return true;
		if ((anObject != null) && (anObject instanceof CAObject)) {
			return nativeRep == CAObject.ID((CAObject)anObject);
		}
		return false;
	}
	
	/** A String representation of the class.*/
	public String toString () { 
		return getClass().getName() + "@" + Integer.toHexString(nativeRep); 
	}
	
	/**
	 * This call allows CAObject classes to represent different
	 * native identifiers - essentially allowing the cacheing and reuse of a Java CAObject
	 * with different allocations.
	 * <P>
	 * There are conditions that should be observed however in the use of this call.
	 * <UL>
	 * <LI>If the object is NOT owned by another, then the ONLY time that this call
	 * is allowed to be used, is when the original value of the native identifier is 0 and
	 * the incoming identifier is not zero. Typically if the owner is null, there are disposal
	 * semantics that are applied to the native identifier, and allowing the reset of it would
	 * mean a resource no longer being accessible - a classic memory leak
	 * <LI>If the object is owned, no native disposal semantics are applied, so the native identifier
	 * can be set to any value. Of course, it is entirely the responsibility of the subclass to determine
	 * if such an action is appropriate.
	 * </UL>
	 * Ownership of an object can ONLY be set upon construction
	 */
	protected void setNR (int newNativeIdentifier) {
		if (nativeRep == newNativeIdentifier)
			return;
		if (ref == null) {
			if (nativeRep == 0 && newNativeIdentifier != 0)
				nativeRep = newNativeIdentifier;
			else
				throw new CANullPointerException ("Cannot reset a disposable reference " + getClass().getName());
		} else { // ref != null 
			nativeRep = newNativeIdentifier;
		}
	}
}
	
/*
 */
