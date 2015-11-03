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
//  CAMemoryObject.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.util;

import com.apple.audio.*;
import com.apple.audio.jdirect.*;
import com.apple.audio.jdirect.Accessor;
import com.apple.audio.jdirect.ArrayCopy;

/**
 * A CAMemoryObject represents a region of memory. Various methods provide access to the 
 * contents of the memory that is represented by this class, and all of those access calls are bounds checked
 * against the known allocation size.
 */
public class CAMemoryObject extends CAObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);	
	}
		
//_________________________ CLASS METHODS	
	/**
	 * Copy the <CODE>numBytes</CODE> from the source object starting at <CODE>srcOffset</CODE> bytes
	 * into that object to the destination object starting at <CODE>destOffset</CODE>.
	 * @param src the source object to copy from
	 * @param srcOffset the number of bytes from the start of the source object to start copying from
	 * @param dest the destination object to copy the memory to.
	 * @param destOffset the number of bytes from the start of the destination object to copy too.
	 * @param numBytesToCopy the number of bytes to copy
	 * @exception CAOutOfBoundsException if there is not enough room in either object
	 */
 	public static void copyFromTo (CAMemoryObject src, int srcOffset, CAMemoryObject dest, int destOffset, int numBytesToCopy) throws CAOutOfBoundsException {
		if ((numBytesToCopy + srcOffset) > src.getSize()) 
			throw new CAOutOfBoundsException ("Not enough bytes in source to copy from");
		if ((numBytesToCopy + destOffset) > dest.getSize()) 
			throw new CAOutOfBoundsException ("Not enough bytes in destination to copy too");
		memcpy (CAObject.ID(dest) + destOffset, CAObject.ID(src) + srcOffset, numBytesToCopy);
 	}
 	
    /**
     * Constructor that allocates a block of <CODE>size</CODE> bytes of memory.
     * @param size the size of memory needed.
     * @param clear if true the memory is cleared to zeros, if false the memory is uninitialized.
     */
	public CAMemoryObject (int size, boolean clear) {
		this (allocate (size, clear), size, null);
	}
	
	/**
	 * This is used by subclasses to construct a CAMemoryObject from another
	 * CAMemoryObject. An example usage is:
	 * An application would do this when it is more convenient or required for a user to be given
	 * an object of a particular class, rather than a generic CAMemoryObject.
	 * <P>
	 * This call <B>DOES NOT</B> copy the original object's memory, but refers to it. The newly 
	 * created class will keep the original object around for as long as the new object is held
	 * by the application code.
	 * <P>
	 * The specified original object may not be null.
	 * @param original the object to which the new object will refer
	 */
	protected CAMemoryObject (CAMemoryObject original) {
		super (getID(original), original);
		this.size = original.size;
	}
	
    /**
     * Constructor that creates a potentially non-disposable reference.
     * @param ptr   the ptr that is being referenced
     * @param size  the size of the memory pointed to by the ptr parameter
     * @param owner the parent object which owns the data which is being referenced. If this is null
     * the object will be freed when it is disposed.
     */
	protected CAMemoryObject (int ptr, int size, Object owner) {
		super (ptr, owner);
		this.size = size;
 	}
		
	// oversees the allocation process - doAllocationCheck tells us
	// if we have the capability of allocating the requested size of memory.
	// if it returns false then we should employ alternate strategies
	// like for the Mac using the System Heap. 
	private static int allocate (int size, boolean clear) {
		return (clear ? mallocClear (size) : malloc (size));
	}

	private final static int getID (CAMemoryObject mem) {
		if (mem == null)
			throw new NullPointerException();
		return CAObject.ID(mem);
	}
	
//_________________________ INSTANCE VARIABLES
	private transient int size;

//_________________________ INSTANCE METHODS	
	private final void doBoundsCheck (int srcOffsetBytes, int arrayElsToCopy, int arrayElSize, int arrayElOffset, int arrayElLength) {
		if (srcOffsetBytes + (arrayElsToCopy * arrayElSize) > size	//how many bytes to copy to/from ptr is within ptr range
			|| arrayElOffset + arrayElsToCopy > arrayElLength	//enough elements in array to copy
			|| srcOffsetBytes < 0 
			|| arrayElOffset < 0)
				throw new CAOutOfBoundsException("When writing to or from an array the constraints of those memory locations was exceeded");
	}

	private final void doBoundsCheck (int srcOffset, int sizeRequested) {
		if (srcOffset < 0
			|| (srcOffset + sizeRequested) > size)
			throw new CAOutOfBoundsException("When reading or writing a value the location of that operation exceed the known memory allocation");
	}
	
	/*
	 * This method should be used with care.
	 * <P>
	 * It resets what this object thinks it is pointing too, and sets the size that this previously allocated 
	 * It is used when a native call passes
	 * a pointer to previously allocated memory, and a CAMemoryObject is being
	 * cached to return that memory to the user.
	 */
	protected void setNR (int nr, int newSize) {
		super.setNR (nr);
		size = newSize;
	}
		
	/**
	 * Returns the size in bytes of the Memory Object.
	 */
	public int getSize () { return size; }
	
	/**
	 * An efficient byte[] copy that copies numBytesToCopy from the memory object to the
	 * byte array.
	 * @param srcOffset the byte offset from the memory object to copy the bytes from
	 * @param destArray the destination byte array
	 * @param destOffset the offset within the byte array to start copying to
	 * @param numBytesToCopy the number of bytes to copy
	 */
	public final void copyToArray (int srcOffset, byte[] destArray, int destOffset, int numBytesToCopy) {
		doBoundsCheck (srcOffset, numBytesToCopy, 1, destOffset, destArray.length);
		ArrayCopy.copyPointerToArray (_ID(), srcOffset, destArray, destOffset, numBytesToCopy);	
	}

	/**
	 * An efficient short[] copy that copies numShortsToCopy from the memory object to the
	 * short array.
	 * @param srcOffset the byte offset from the memory object to copy the shorts from
	 * @param destArray the destination short array
	 * @param destOffset the offset within the short array to start copying to
	 * @param numShortsToCopy the number of shorts to copy
	 */
	public final void copyToArray (int srcOffset, short[] destArray, int destOffset, int numShortsToCopy) {
		doBoundsCheck (srcOffset, numShortsToCopy, 2, destOffset, destArray.length);
		ArrayCopy.copyPointerToArray (_ID(), srcOffset, destArray, destOffset * 2, numShortsToCopy * 2);	
	}

	/**
	 * An efficient char[] copy that copies numCharsToCopy from the memory object to the
	 * char array.
	 * @param srcOffset the byte offset from the memory object to copy the chars from
	 * @param destArray the destination char array
	 * @param destOffset the offset within the char array to start copying to
	 * @param numCharsToCopy the number of chars to copy
	 */
	public final void copyToArray (int srcOffset, char[] destArray, int destOffset, int numCharsToCopy) {
		doBoundsCheck (srcOffset, numCharsToCopy, 2, destOffset, destArray.length);
		ArrayCopy.copyPointerToArray (_ID(), srcOffset, destArray, destOffset * 2, numCharsToCopy * 2);	
	}

	/**
	 * An efficient int[] copy that copies numIntsToCopy from the memory object to the
	 * int array.
	 * @param srcOffset the byte offset from the memory object to copy the ints from
	 * @param destArray the destination int array
	 * @param destOffset the offset within the int array to start copying to
	 * @param numIntsToCopy the number of ints to copy
	 */
	public final void copyToArray (int srcOffset, int[] destArray, int destOffset, int numIntsToCopy) {
		doBoundsCheck (srcOffset, numIntsToCopy, 4, destOffset, destArray.length);
		ArrayCopy.copyPointerToArray (_ID(), srcOffset, destArray, destOffset * 4, numIntsToCopy * 4);	
	}

	/**
	 * An efficient float[] copy that copies numFloatsToCopy from the memory object to the
	 * float array.
	 * @param srcOffset the byte offset from the memory object to copy the floats from
	 * @param destArray the destination float array
	 * @param destOffset the offset within the float array to start copying to
	 * @param numFloatsToCopy the number of floats to copy
	 */
	public final void copyToArray (int srcOffset, float[] destArray, int destOffset, int numFloatsToCopy) {
		doBoundsCheck (srcOffset, numFloatsToCopy, 4, destOffset, destArray.length);
		ArrayCopy.copyPointerToArray (_ID(), srcOffset, destArray, destOffset * 4, numFloatsToCopy * 4);	
	}

	/**
	 * An efficient long[] copy that copies numLongsToCopy from the memory object to the
	 * long array.
	 * @param srcOffset the byte offset from the memory object to copy the longs from
	 * @param destArray the destination long array
	 * @param destOffset the offset within the long array to start copying to
	 * @param numLongsToCopy the number of longs to copy
	 */
	public final void copyToArray (int srcOffset, long[] destArray, int destOffset, int numLongsToCopy) {
		doBoundsCheck (srcOffset, numLongsToCopy, 8, destOffset, destArray.length);
		ArrayCopy.copyPointerToArray (_ID(), srcOffset, destArray, destOffset * 8, numLongsToCopy * 8);	
	}

	/**
	 * An efficient double[] copy that copies numDoublesToCopy from the memory object to the
	 * double array.
	 * @param srcOffset the byte offset from the memory object to copy the doubles from
	 * @param destArray the destination double array
	 * @param destOffset the offset within the double array to start copying to
	 * @param numDoublesToCopy the number of doubles to copy
	 */
	public final void copyToArray (int srcOffset, double[] destArray, int destOffset, int numDoublesToCopy) {
		doBoundsCheck (srcOffset, numDoublesToCopy, 8, destOffset, destArray.length);
		ArrayCopy.copyPointerToArray (_ID(), srcOffset, destArray, destOffset * 8, numDoublesToCopy * 8);	
	}

	/**
	 * An efficient byte[] copy that copies numBytesToCopy from the byte array to
	 * the memory object.
	 * @param destOffset how many bytes offset from the base memory object to copy to.
	 * @param srcArray the byte[] to copy
	 * @param srcOffset the nth index within the byte [] to start copying from
	 * @param numBytesToCopy how many elements of the byte array to copy
	 */
	public final void copyFromArray (int destOffset, byte[] srcArray, int srcOffset, int numBytesToCopy) {
		doBoundsCheck (destOffset, numBytesToCopy, 1, srcOffset, srcArray.length);
		ArrayCopy.copyArrayToPointer (srcArray, srcOffset, _ID(), destOffset, numBytesToCopy);	
	}

	/**
	 * An efficient short[] copy that copies numShortsToCopy from the short array to
	 * the memory object.
	 * @param destOffset how many bytes offset from the base memory object to copy to.
	 * @param srcArray the short[] to copy
	 * @param srcOffset the nth index within the short [] to start copying from
	 * @param numShortsToCopy how many elements of the short array to copy
	 */
	public final void copyFromArray (int destOffset, short[] srcArray, int srcOffset, int numShortsToCopy) {
		doBoundsCheck (destOffset, numShortsToCopy, 2, srcOffset, srcArray.length);
		ArrayCopy.copyArrayToPointer (srcArray, srcOffset * 2, _ID(), destOffset, numShortsToCopy * 2);	
	}

	/**
	 * An efficient char[] copy that copies numCharsToCopy from the char array to
	 * the memory object.
	 * @param destOffset how many bytes offset from the base memory object to copy to.
	 * @param srcArray the char[] to copy
	 * @param srcOffset the nth index within the char [] to start copying from
	 * @param numCharsToCopy how many elements of the char array to copy
	 */
	public final void copyFromArray (int destOffset, char[] srcArray, int srcOffset, int numCharsToCopy) {
		doBoundsCheck (destOffset, numCharsToCopy, 2, srcOffset, srcArray.length);
		ArrayCopy.copyArrayToPointer (srcArray, srcOffset * 2, _ID(), destOffset, numCharsToCopy * 2);	
	}

	/**
	 * An efficient int[] copy that copies numIntsToCopy from the int array to
	 * the memory object.
	 * @param destOffset how many bytes offset from the base memory object to copy to.
	 * @param srcArray the int[] to copy
	 * @param srcOffset the nth index within the int [] to start copying from
	 * @param numIntsToCopy how many elements of the int array to copy
	 */
	public final void copyFromArray (int destOffset, int[] srcArray, int srcOffset, int numIntsToCopy) {
		doBoundsCheck (destOffset, numIntsToCopy, 4, srcOffset, srcArray.length);
		ArrayCopy.copyArrayToPointer (srcArray, srcOffset * 4, _ID(), destOffset, numIntsToCopy * 4);	
	}

	/**
	 * An efficient float[] copy that copies numFloatsToCopy from the float array to
	 * the memory object.
	 * @param destOffset how many bytes offset from the base memory object to copy to.
	 * @param srcArray the float[] to copy
	 * @param srcOffset the nth index within the float [] to start copying from
	 * @param numFloatsToCopy how many elements of the float array to copy
	 */
	public final void copyFromArray (int destOffset, float[] srcArray, int srcOffset, int numFloatsToCopy) {
		doBoundsCheck (destOffset, numFloatsToCopy, 4, srcOffset, srcArray.length);
		ArrayCopy.copyArrayToPointer (srcArray, srcOffset * 4, _ID(), destOffset, numFloatsToCopy * 4);	
	}

	/**
	 * An efficient long[] copy that copies numLongsToCopy from the long array to
	 * the memory object.
	 * @param destOffset how many bytes offset from the base memory object to copy to.
	 * @param srcArray the long[] to copy
	 * @param srcOffset the nth index within the long [] to start copying from
	 * @param numLongsToCopy how many elements of the long array to copy
	 */
	public final void copyFromArray (int destOffset, long[] srcArray, int srcOffset, int numLongsToCopy) {
		doBoundsCheck (destOffset, numLongsToCopy, 8, srcOffset, srcArray.length);
		ArrayCopy.copyArrayToPointer (srcArray, srcOffset * 8, _ID(), destOffset, numLongsToCopy * 8);	
	}

	/**
	 * An efficient double[] copy that copies numDoublesToCopy from the double array to
	 * the memory object.
	 * @param destOffset how many bytes offset from the base memory object to copy to.
	 * @param srcArray the double[] to copy
	 * @param srcOffset the nth index within the double [] to start copying from
	 * @param numDoublesToCopy how many elements of the double array to copy
	 */
	public final void copyFromArray (int destOffset, double[] srcArray, int srcOffset, int numDoublesToCopy) {
		doBoundsCheck (destOffset, numDoublesToCopy, 8, srcOffset, srcArray.length);
		ArrayCopy.copyArrayToPointer (srcArray, srcOffset * 8, _ID(), destOffset, numDoublesToCopy * 8);	
	}

	/** 
	 * Return the boolean value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @return the value at the specified offset
	 */
	public boolean getBooleanAt(int offset) { 
		doBoundsCheck (offset, 1);
		return (Accessor.getByteFromPointer(_ID(), offset) != 0); 
	}

	/** 
	 * Return the byte value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @return the value at the specified offset
	 */
	public byte getByteAt(int offset) { 
		doBoundsCheck (offset, 1);
		return Accessor.getByteFromPointer(_ID(), offset); 
	}
	
	/** 
	 * Return the unsigned byte value as an int at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @return the value at the specified offset
	 */
	public int getUByteAt(int offset) { 
		doBoundsCheck (offset, 1);
		return (Accessor.getByteFromPointer(_ID(), offset) & 0xff); 
	}
	
	/** 
	 * Return the short value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @return the value at the specified offset
	 */
	public short getShortAt(int offset) { 
		doBoundsCheck (offset, 2);
		return Accessor.getShortFromPointer(_ID(), offset); 
	}
	
	/** 
	 * Return the unsigned short value as an int at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @return the value at the specified offset
	 */
	public int getUShortAt(int offset) { 
		doBoundsCheck (offset, 2);
		return (Accessor.getShortFromPointer(_ID(), offset) & 0xffff); 
	}

	/** 
	 * Return the char value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @return the value at the specified offset
	 */
	public char getCharAt(int offset) { 
		doBoundsCheck (offset, 2);
		return Accessor.getCharFromPointer(_ID(), offset); 
	}
	
	/** 
	 * Return the int value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @return the value at the specified offset
	 */
	public int getIntAt(int offset) { 
		doBoundsCheck (offset, 4);
		return Accessor.getIntFromPointer(_ID(), offset); 
	}
	
	/** 
	 * Return the unsigned int value as a long at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @return the value at the specified offset
	 */
	public long getUIntAt(int offset) { 
		doBoundsCheck (offset, 4);
		return (Accessor.getIntFromPointer(_ID(), offset) & 0xFFFFFFFFL); 
	}

	/** 
	 * Return the float value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @return the value at the specified offset
	 */
	public float getFloatAt(int offset) { 
		doBoundsCheck (offset, 4);
		return Accessor.getFloatFromPointer(_ID(), offset);
	}

	/** 
	 * Return the long value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @return the value at the specified offset
	 */
	public long getLongAt(int offset) { 
		doBoundsCheck (offset, 8);
		return Accessor.getLongFromPointer(_ID(), offset); 
	}
		
	/** 
	 * Return the double value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @return the value at the specified offset
	 */
	public double getDoubleAt(int offset) { 
		doBoundsCheck (offset, 8);
		return Accessor.getDoubleFromPointer(_ID(), offset); 
	}
	
	/** 
	 * Set the boolean value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @param value the value that will be set at the specified offset
	 */
	public void setBooleanAt(int offset, boolean value) { 
		doBoundsCheck (offset, 1);
		Accessor.setByteInPointer(_ID(), offset, (byte)(value ? 1 : 0)); 
	}

	/** 
	 * Set the byte value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @param value the value that will be set at the specified offset
	 */
	public void setByteAt(int offset, byte value) { 
		doBoundsCheck (offset, 1);
		Accessor.setByteInPointer(_ID(), offset, value); 
	}

	/** 
	 * Set the byte value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @param value the value that will be set at the specified offset
	 */
	public void setShortAt(int offset, short value) { 
		doBoundsCheck (offset, 2);
		Accessor.setShortInPointer(_ID(), offset, value); 
	}

	/** 
	 * Set the byte value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @param value the value that will be set at the specified offset
	 */
	public void setCharAt(int offset, char value) { 
		doBoundsCheck (offset, 2);
		Accessor.setCharInPointer(_ID(), offset, value); 
	}

	/** 
	 * Set the byte value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @param value the value that will be set at the specified offset
	 */
	public void setIntAt(int offset, int value) { 
		doBoundsCheck (offset, 4);
		Accessor.setIntInPointer(_ID(), offset, value); 
	}

	/** 
	 * Set the byte value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @param value the value that will be set at the specified offset
	 */
	public void setFloatAt(int offset, float value) { 
		doBoundsCheck (offset, 4);
		Accessor.setFloatInPointer(_ID(), offset, value); 
	}
	
	/** 
	 * Set the byte value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @param value the value that will be set at the specified offset
	 */
	public void setLongAt(int offset, long value) { 
		doBoundsCheck (offset, 8);
		Accessor.setLongInPointer(_ID(), offset, value); 
	}
	
	/** 
	 * Set the byte value at the specified offset.
	 * @param offset the offset in bytes from the start of the memory object.
	 * @param value the value that will be set at the specified offset
	 */
	public void setDoubleAt(int offset, double value) { 
		doBoundsCheck (offset, 8);
		Accessor.setDoubleInPointer(_ID(), offset, value);
	}

	/**
	 * Presumes that the specified part of the object is a C string - ie. ASCII encoded byte values - and returns
	 * this as a java.lang.String object. A length value of zero will scan from the offset memory location
	 * until the first value of zero (or the size of the object) is found.
	 * @param offset the byte offset where the CString begins
	 * @return a String
	 */
	public String getCStringAt (int offset) {
		int i = offset;
		for (; i < size; i++) {
			byte value = getByteAt (i);
			if (value == 0)
				break;
		}
		if (i == offset)
			return "";
			
		byte[] ar = new byte [i - offset];
		copyToArray (offset, ar, 0, ar.length);
		return new String (ar);
	}
	
	/**
	 * Presumes that the specified part of the object is a CFString - ie. Unicode values - and returns
	 * this as a CAFString object.
	 * @param offset the byte offset where the CFString begins
	 * @return a CAFString
	 */
	public CAFString getCFStringAt (int offset) {
		return new CAFString(getIntAt(offset));
	}
	 
	/** A String representation of the class.
	 * @return a String
	 */
	public String toString () { 
		return super.toString() + "[size=" + getSize() + "]"; 
	}
	

//_ NATIVE METHODS	
	private static native int malloc (int size);
	private static native int mallocClear (int size);
	private static native int memcpy (int destPtr, int srcPtr, int numBytes);
}

/*
 */
