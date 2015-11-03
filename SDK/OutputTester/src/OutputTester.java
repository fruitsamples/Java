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
        File:			OutputTester.java
        
        Description:	This is the main file - it will use either OutputUnitTester or the HALTester 
						to use either the DefaultOutputUnit or the default device of the HAL APIs. 
						
						Both result in the same audio data going out, but require different amounts of work
						for the developer. In most situations use of the DefaultOutputUnit is strongly recommended.
						
        Author:			William Stewart
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import com.apple.audio.hardware.*;
import com.apple.audio.toolbox.*;
import com.apple.audio.util.*;
import com.apple.audio.*;

public class OutputTester {
	static OutputTester handler;

			// set to false to use the HAL directly 
	static final boolean useAudioUnit = true;
		
	public static void main (String[] args) {
		try {
			handler = new OutputTester();
            handler.window = new Frame ("Audio Output Tester");
			handler.window.pack(); //workaround for Java bug
			
			if (useAudioUnit) {
				handler.outTester = new OutputUnitTester ();
			} else {
				handler.outTester = new HALTester ();
			}

				// do UI and show window
			handler.doUI();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private IOControlInterface outTester;
	private Frame window;
	private Label statusLabel;
	
	private void doUI () {
		statusLabel = new Label ("Choose a small file to play");
		window.add (statusLabel, "North");
		
		Button b1 = new Button ("Start");
		window.add (b1, "West");
		b1.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				if (outTester.getAudioProvider().hasSourceData() == false) {
					statusLabel.setText ("Can't start without valid audio data");
					statusLabel.repaint();
					return;
				}
				outTester.start();
			}
		});
		
		Button b2 = new Button ("Stop"); 
		window.add (b2, "East");
		b2.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				outTester.stop();
			}
		});
		
		Button b3 = new Button ("Load AIF File");
		window.add (b3, "Center");
		b3.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				FileDialog fd = new FileDialog (window, "Choose AIF File...", FileDialog.LOAD);
				fd.setFilenameFilter (new FilenameFilter () {
					public boolean accept (File dir, String name) {
						return name.endsWith(".aif") || name.endsWith(".AIF") ||
								name.endsWith (".aiff") || name.endsWith(".AIFF");
					}
				});
				fd.show();
				if (fd.getFile() != null) { //ie we chose a file...
					File chosenFile = new File (fd.getDirectory() + fd.getFile());
					outTester.stop();
					if (outTester.getAudioProvider().prepareAIFFData (chosenFile)) {
						statusLabel.setText ("File chosen:" + chosenFile.getName());
					} else {
						statusLabel.setText ("Invalid AIF file");
					}
					statusLabel.repaint();
				}
			}
		});
				
		if (useAudioUnit) { // add a volume control for output unit
			Panel p = new Panel();
			p.setLayout (new BorderLayout());
			Scrollbar sbar = new Scrollbar(Scrollbar.HORIZONTAL, 200, 1, 0, 201);
			sbar.addAdjustmentListener (new AdjustmentListener () {
				public void adjustmentValueChanged (AdjustmentEvent ae) {
					((OutputUnitTester)outTester).setVolume((float)(ae.getValue() / 200.0));
				}
			});
			p.add (sbar, "Center");
			p.add (new Label ("Volume"), "North");
			window.add (p, "South");
		}
		
		window.addWindowListener (new WindowAdapter () {
			public void windowClosing (WindowEvent we) {
				try {
					outTester.stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
				window.dispose();
			}
			
			public void windowClosed (WindowEvent we) {
				System.exit(0);
			}
		});
		window.setResizable (false);
		window.pack();
		window.show();
	}	
}
