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
//  MusicPlayer.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.toolbox;

import com.apple.audio.*;
import com.apple.audio.jdirect.*;
import com.apple.audio.util.*;
import com.apple.audio.jdirect.Accessor;

/**
 * A MusicPlayer plays a MusicSequence.
 */
public final class MusicPlayer extends CAObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}
	
//_________________________ STATIC METHODS
	/**
	 * Create a MusicPlayer object.
	 * <BR><BR><b>CoreAudio::NewMusicPlayer</b>
	 */
	public MusicPlayer () throws CAException {
		super (allocate());
	}
	
	private static int allocate () throws CAException {
		synchronized (MusicSequence.syncObject) {
			int res = NewMusicPlayer (MusicSequence.firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (MusicSequence.firstArg4Ptr, 0);
		}
	}	
//_________________________ INSTANCE FIELDS
	private MusicSequence holdOntoSeq;

//_________________________ INSTANCE METHODS
	/**
	 * Sets a sequence that will be used by the player.
	 * <BR><BR><b>CoreAudio::MusicPlayerSetSequence</b>
	 * @param inSequence the sequence that will be used by the MusicPlayer
	 */
	public void setSequence (MusicSequence inSequence) throws CAException {
		int res = MusicPlayerSetSequence(_ID(), CAObject.ID(inSequence));
		CAException.checkError (res);
		holdOntoSeq = inSequence;
	}
	
	/**
	 * Sets the time for the player in beats according to 
	 * its attached MusicSequence.
	 * <BR><BR><b>CoreAudio::MusicPlayerSetTime</b>
	 * @param inTime the new time for the player
	 */
	public void setTime (double inTime) throws CAException {
		int res = MusicPlayerSetTime(_ID(), inTime);
		CAException.checkError (res);
	}

	/**
	 * Gets the time of the player in beats according to 
	 * its attached MusicSequence.
	 * <BR><BR><b>CoreAudio::MusicPlayerGetTime</b>
	 * @return the current time of the player
	 */
	public double getTime () throws CAException {
		synchronized (MusicSequence.syncObject) {
			int res = MusicPlayerGetTime(_ID(), MusicSequence.secondArg8Ptr);
			CAException.checkError (res);
			return Accessor.getDoubleFromPointer (MusicSequence.secondArg8Ptr, 0);
		}
	}

	/**
	 * Returns a host time value for a given beats time based on the
	 * time that the sequence started playing.
	 * 
	 * As this call is only valid if the player is playing this
	 * call will return an error if the player is not playing or
	 * the postion of the player (its "starting beat") when the
	 * player was started.
	 * (see the MusicSequence calls for beat<->seconds calls for these speculations)
	 * 
	 * This call is totally dependent on the Sequence attached to the Player,
	 * (primarily the tempo map of the Sequence), so the player must have
	 * a sequence attached to it or an error is returned.
	 * <BR><BR><b>CoreAudio::MusicPlayerGetHostTimeForBeats</b>
	 */
	public long getHostTimeForBeats (double inBeats) throws CAException {
		synchronized (MusicSequence.syncObject) {
		int res = MusicPlayerGetHostTimeForBeats (_ID(), 
											inBeats,
 											MusicSequence.secondArg8Ptr);
			CAException.checkError (res);
			return Accessor.getLongFromPointer (MusicSequence.secondArg8Ptr, 0);
		}
	}
  
	/**
	 * Returns a time in beats given a host time based on the
	 * time that the sequence started playing.
	 * 
	 * As this call is only valid if the player is playing this
	 * call will return an error if the player is not playing, or
	 * if inHostTime is sometime before the Player was started.
	 * (see the MusicSequence calls for beat<->seconds calls for these speculations)
	 * 
	 * This call is totally dependent on the Sequence attached to the Player,
	 * (primarily the tempo map of the Sequence), so the player must have
	 * a sequence attached to it or an error is returned.
	 * <BR><BR><b>CoreAudio::MusicPlayerGetBeatsForHostTime</b>
	 */
	public double getBeatsForHostTime (long inHostTime) throws CAException {
		synchronized (MusicSequence.syncObject) {
		int res = MusicPlayerGetBeatsForHostTime (_ID(), 
											inHostTime,
 											MusicSequence.secondArg8Ptr);
			CAException.checkError (res);
			return Accessor.getDoubleFromPointer (MusicSequence.secondArg8Ptr, 0);
		}
	}
		
	/**
	 * Allow members of the sequence to prepare themselves for playback
	 * <BR><BR><b>CoreAudio::MusicPlayerPreroll</b>
	 */
	public void preroll () throws CAException {
		int res = MusicPlayerPreroll(_ID());
		CAException.checkError (res);
	}

	/**
	 * Starts a music player.
	 * <BR><BR><b>CoreAudio::MusicPlayerStart</b>
	 */
	public void start () throws CAException {
		int res = MusicPlayerStart(_ID());
		CAException.checkError (res);
	}

	/**
	 * Stops a music player.
	 * <BR><BR><b>CoreAudio::MusicPlayerStop</b>
	 */
	public void stop () throws CAException {
		int res = MusicPlayerStop(_ID());
		CAException.checkError (res);
	}

	/**
	 * This call returns a non-zero value in outIsPlaying if the player has been
	 * started and not stopped. 
	 * It may have "played" past the valid events of the attached
	 * MusicSequence, but it is still considered to be playing 
	 * (and its time value increasing) in that situation.
	 * <BR><BR><b>CoreAudio::MusicPlayerIsPlaying</b>
	 */
	public boolean isPlaying () throws CAException {
		synchronized (MusicSequence.syncObject) {
			int res = MusicPlayerIsPlaying (_ID(),MusicSequence.secondArg8Ptr);
			CAException.checkError (res);
			return Accessor.getByteFromPointer (MusicSequence.secondArg8Ptr, 0) != 0;
		}
	}

//_ NATIVE METHODS
	private static native int NewMusicPlayer(int outPlayerPtr);
	private static native int MusicPlayerSetSequence(int inPlayer,int inSequence);
	private static native int MusicPlayerSetTime(int inPlayer, double inTime);
	private static native int MusicPlayerGetTime(int inPlayer, int outTimePtr);
	private static native int MusicPlayerPreroll(int inPlayer);	
	private static native int MusicPlayerStart(int inPlayer);
	private static native int MusicPlayerStop(int inPlayer);
	private static native int MusicPlayerGetBeatsForHostTime (int	inPlayer,
 												   long			inHostTime,
 												   int			outBeats );
	private static native int MusicPlayerGetHostTimeForBeats (int 	inPlayer,
 												   double		inBeats,
 												   int			outHostTime);
	private static native int MusicPlayerIsPlaying (int inPlayer, int outIsPlaying);
}

/*
 */
