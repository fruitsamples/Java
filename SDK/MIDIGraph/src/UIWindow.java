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
        File:			UIWindow.java
        
        Description:	UI for MIDIGraph.
        
        Author:			William Stewart
*/

/* 
 */
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import com.apple.audio.*;

public class UIWindow extends Frame {
    UIWindow (MIDIGraph grapher) {
        super ("MIDI Graph");
		setResizable(false);
        player = grapher;
		addWindowListener (new WindowAdapter () {
			public void windowClosing (WindowEvent we) {
				player.cleanUp();
				UIWindow.this.dispose();
			}
			
			public void windowClosed (WindowEvent we) {
				System.exit(0);
			}
		});
		pack();//Java bug workaround
    }
    
    private MIDIGraph player;
 
	void addUIElementsAndShow (String systemBanksDir, String userBanksDir) {
		Panel sratePanel = new Panel ();
        GridBagLayout gb1 = new GridBagLayout();
        sratePanel.setLayout (gb1);

        GridBagConstraints cons1 = new GridBagConstraints();
        cons1.fill = GridBagConstraints.HORIZONTAL;
        cons1.gridheight = 1;		
        cons1.insets = new Insets (2, 2, 2, 2);

		Panel midiInPanel = new Panel () {
			public Dimension getMinimumSize () {
				return new Dimension (400, 400);
			}
        };
		
        GridBagLayout gb = new GridBagLayout();
        midiInPanel.setLayout (gb);

        GridBagConstraints cons = new GridBagConstraints();
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.gridheight = 1;		
        cons.insets = new Insets (2, 2, 2, 2);

// 1st Row
        cons.gridy = 0;		
        cons.gridx = 0;
        cons.gridwidth = 6;
        midiInPanel.add (new Label ("MIDI Input"), cons);


// 2nd Row
        cons.gridy = 1;		
        cons.gridheight = 3;		
        cons.gridx = 0;
        String[] inputs = player.getMIDIInputs();
		
        java.awt.List midiInList = new java.awt.List (inputs.length, false);
        for (int i = 0; i < inputs.length; i++)
            midiInList.add ((String)inputs[i], i);

		midiInList.addItemListener (new ItemListener () {
			public void itemStateChanged (ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					java.awt.List midiInList = (java.awt.List)ie.getItemSelectable();
					String str = midiInList.getSelectedItem();
					try {
						player.setMIDISource (str);
					} catch (CAException e) {
						e.printStackTrace();
					}
				}
			}
		});
            
		midiInList.getPreferredSize (3);
				
		midiInPanel.add (midiInList, cons);
		
// 3rd Row
		cons.gridy = 4;
		cons.gridheight = 1;		
		cons.gridwidth = 3;
		cons.gridx = 0;
		midiInPanel.add (new Label ("MIDI Channel:"), cons);
	
		TextField channelField = new TextField (4);
		channelField.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				int channel = new Integer (event.getActionCommand()).intValue();
				if (channel > 16)
					channel = 16;
				else if (channel < 1)
					channel = 1; 
				player.getMIDIProcessor ().setChannel (channel - 1);		
			}
		});
            
		channelField.setText ("1");
		cons.gridx = 3;
		midiInPanel.add (channelField, cons);

// 4th Row
		cons.gridy = 5;
		cons.gridx = 0;
		midiInPanel.add (new Label ("Patch number:"), cons);

		TextField patchField = new TextField (4);
		patchField.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				int patchNumber = new Integer (event.getActionCommand()).intValue();
				player.getMIDIProcessor ().setPatchNumber (patchNumber);
			}
		});
		patchField.setText ("0");
		cons.gridx = 3;
		midiInPanel.add (patchField, cons);

// 5th Row
		cons.gridy = 6;
		cons.gridx = 0;
		midiInPanel.add (new Label ("Transpose:"), cons);
		
		TextField transposeField = new TextField (4);
		transposeField.addActionListener (new ActionListener () {			
			public void actionPerformed (ActionEvent event) {
				player.getMIDIProcessor ().setTransposeValue (new Integer (event.getActionCommand()).intValue());
			}
		});
		transposeField.setText ("0");
		cons.gridx = 3;
		midiInPanel.add (transposeField, cons);

// 6th Row
		cons.gridy = 7;
		cons.gridx = 0;
		midiInPanel.add (new Label ("Synth Volume:"), cons);
		
		TextField volumeField = new TextField (4);
		volumeField.addActionListener (new ActionListener () {			
			public void actionPerformed (ActionEvent event) {
				player.getMIDIProcessor ().setVolume (new Integer (event.getActionCommand()).intValue() & 0x7F);
			}
		});
		volumeField.setText ("127");
		cons.gridx = 3;
		midiInPanel.add (volumeField, cons);
		
		add (midiInPanel, "West");
		
// Sound Bank Panel
        Panel soundBankPanel = new Panel () {
			public Dimension getMinimumSize () {
				return new Dimension (350, 300);
			}
        };
		
        gb = new GridBagLayout();
        soundBankPanel.setLayout (gb);

		cons = new GridBagConstraints();
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.insets = new Insets (2, 2, 2, 2);

// 1st Row
        cons.gridy = 0;		
        cons.gridx = 0;
        cons.gridwidth = 6;
        cons.gridheight = 1;		
			
		Button b1 = new Button ("Add Sound Banks...");
		soundBankPanel.add (b1, cons);
		b1.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				FileDialog fd = new FileDialog (UIWindow.this, "Add Sound Banks...", FileDialog.LOAD);
				fd.setFilenameFilter (new FilenameFilter () {
					public boolean accept (File dir, String name) {
						return name.endsWith(".sf2") || name.endsWith (".dls") 
							|| name.endsWith(".SF2") || name.endsWith (".DLS");
					}
				});
				fd.show();
				if (fd.getFile() != null) { //ie we chose a file...
					loadSoundBanks (fd.getDirectory());
					selectSoundBank (fd.getFile());
				}
			}
		});

// 2nd Row
		cons.gridy = 1;		
		cons.gridheight = 10;		
		cons.gridx = 0;

		createSoundBankList ();
		loadSoundBanks (systemBanksDir);
		loadSoundBanks (userBanksDir);
		soundBankPanel.add (soundBankFileList, cons);
		
		add (soundBankPanel, "East");
		
		pack();
		show();
			//do this after show or it doesn't hi-lite selection
		if (soundBankFileList.getItemCount() > 0) {
			soundBankFileList.select (0);
			selectSoundBank (soundBankFileList.getSelectedItem());
		}
		
		try {
			player.setMIDISource (inputs[0]);
		} catch (CAException e) {
			e.printStackTrace();
		}
		midiInList.select (0);
	}
	
	java.awt.List midiFileList, soundBankFileList;
	File midiFileDir;
	private Hashtable soundBanks = new Hashtable();

	public Dimension getMinimumSize () {
		return new Dimension (500, 500);
	}
	
	private void createSoundBankList () {
		soundBankFileList = new java.awt.List (10, false);
		soundBankFileList.getPreferredSize (10);
		soundBankFileList.addItemListener (new ItemListener () {
			public void itemStateChanged (ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					java.awt.List l = (java.awt.List)ie.getItemSelectable();
					String str = l.getSelectedItem();
					selectSoundBank (str);
				}
			}
		});
	}
	
	void loadSoundBanks (String soundBankDirectory) {
		File sbFileDir = new File (soundBankDirectory);
		if (sbFileDir.exists() == false) {
			return;
		}
		String[] files = sbFileDir.list();
		if (files.length == 0) {
			return;
		}
		int selectItem = 0;
		Vector vec = new Vector();
		for (int num = 0; num < files.length; num++) {
			String fileName = files[num];
			if (fileName.endsWith (".dls")
				|| fileName.endsWith (".sf2")
				|| fileName.endsWith (".DLS")
				|| fileName.endsWith (".SF2")) {
					vec.addElement (fileName);
			}
		}
		if (vec.size() == 0)
			return;
		int count = soundBankFileList.getItemCount();
		for (int i = 0; i < vec.size(); i++)
			soundBankFileList.add ((String)vec.elementAt(i), count+i);
		soundBanks.put (soundBankDirectory, vec);
	}

	void selectSoundBank (String fileName) {
		Enumeration iter = soundBanks.keys();
		boolean found = false;
		String bankDir = null;
		while (iter.hasMoreElements()) {
			Object key = iter.nextElement();
			Vector vec = (Vector)soundBanks.get (key);
			Enumeration iter2 = vec.elements();
			while (iter2.hasMoreElements()) {
				String str = (String)iter2.nextElement();
				if (str.equals (fileName)) {
					found = true;
					break;
				}
			}
			if (found) {
				bankDir = (String)key;
				break;
			}
		}
		if (found) {
			try {
				player.loadSoundBank (new File (bankDir + "/" + fileName));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	
}
