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
        File:			SimpleSMFPlayer.java
        
        Description:	SimpleSMFPlayer is a simple app that asks the user to choose a MIDI file and plays it back.
		
        Author:			William Stewart
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import com.apple.audio.toolbox.*;
import com.apple.audio.*;

public class SimpleSMFPlayer {
	static SimpleSMFPlayer tester;
	static Frame window;

	public static void main (String[] args) {
		try {
			window = new Frame ("Simple SMF Player");

			tester = new SimpleSMFPlayer();
			tester.addUIElements (window);
			
			window.pack();
			window.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	MusicPlayer player;
	
	void loadMIDIData (File midiFile) {
		try {
			if (player != null)
				player.stop();
			MusicSequence seq = new MusicSequence (midiFile);
			player = new MusicPlayer();
			player.setSequence (seq);
                        System.out.println ("Ready To Play:" + midiFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	void addUIElements (Frame f) {
		Button b1 = new Button ("Play");
		f.add (b1, "West");
		b1.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				try {
					player.preroll();
					player.start();
				} catch (CAException e) {
					e.printStackTrace();
				}
			}
		});
		
		Button b2 = new Button ("Stop");
		f.add (b2, "East");
		b2.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				try {
					player.stop();
				} catch (CAException e) {
					e.printStackTrace();
				}
			}
		});
		
		Button b3 = new Button ("Load MIDI File");
		f.add (b3, "North");
		b3.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				FileDialog fd = new FileDialog (window, "Choose MIDI File...", FileDialog.LOAD);
				fd.setFilenameFilter (new FilenameFilter () {
					public boolean accept (File dir, String name) {
						return name.endsWith(".smf") || name.endsWith (".mid") 
								|| name.endsWith(".SMF") || name.endsWith (".MID");
					}
				});
				fd.show();
				if (fd.getFile() != null) { //ie we chose a file...
					loadMIDIData (new File (fd.getDirectory() + fd.getFile()));
				}
			}
		});
		
		f.addWindowListener (new WindowAdapter () {
			public void windowClosing (WindowEvent we) {
				try {
					if (player != null)
						player.stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
				window.dispose();
			}
			
			public void windowClosed (WindowEvent we) {
				System.exit(0);
			}
		});
	}
}
