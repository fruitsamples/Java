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
        
        Description:	Presents UI for SMFPlayer.
		
        Author:			William Stewart
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import com.apple.audio.*;
import com.apple.audio.units.*;

public class UIWindow extends Frame {
	UIWindow (String name) {
		super (name);
		addWindowListener (new WindowAdapter () {
			public void windowClosing (WindowEvent we) {
				player.cleanUp();
				UIWindow.this.dispose();
			}
			
			public void windowClosed (WindowEvent we) {
				System.exit(0);
			}
		});
		pack(); //workaround for Java runtime problems...
	}
	
    public void setPlayer (SMFPlayer player) {
        this.player = player;
    }
    
	SMFPlayer player;
	java.awt.List midiFileList, soundBankFileList;
	File midiFileDir;
	private Hashtable soundBanks = new Hashtable();
	private Label timeLabel;
	private boolean stayAlive;

	void stopPlayer () {
		stayAlive = false;
		try { 	
			player.stop();
		} catch (CAException e) {
			e.printStackTrace();
		}
	}
	
	private void createMIDIFileList (String midiFileDirectory) {
		midiFileList = new java.awt.List (10, false);
		midiFileList.getPreferredSize (10);
		midiFileList.addItemListener (new ItemListener () {
			public void itemStateChanged (ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					java.awt.List l = (java.awt.List)ie.getItemSelectable();
					String str = l.getSelectedItem();
					player.loadMIDIFile (new File (midiFileDir + "/" + str));
					timeLabel.setText ("0");
				} else {
					stopPlayer();
				}
			}
		});
		loadMIDIFiles (midiFileDirectory, null);
	}
	
	void loadMIDIFiles (String midiFileDirectory, String midiFile) {
		Vector vec = new Vector();
		midiFileDir = new File (midiFileDirectory);
		if (midiFileDir.exists() == false) {
			midiFileDir = null;
			return;
		}
		String[] files = midiFileDir.list();
		if (files.length == 0) {
			midiFileDir = null;
			return;
		}
		int selectItem = 0;
		for (int num = 0; num < files.length; num++) {
			String fileName = files[num];
			if (fileName.endsWith (".smf")
				|| fileName.endsWith (".mid")
				|| fileName.endsWith (".SMF")
				|| fileName.endsWith (".MID")) {
					vec.addElement (fileName);
					if (fileName.equals(midiFile))
						selectItem = vec.size() - 1;
			}
		}
		
		midiFileList.removeAll();
		for (int i = 0; i < vec.size(); i++)
			midiFileList.add ((String)vec.elementAt(i), i);
		if (vec.size() > 0 && midiFile != null) {
			player.loadMIDIFile (new File (midiFileDir + "/" + midiFile));
			midiFileList.select (selectItem);
		}
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
					timeLabel.setText ("0");
				} else {
					stopPlayer();
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
		if (found)
			player.loadSoundBank (new File (bankDir + "/" + fileName));
	}
	
	void addPlayPanel () throws CAException {
		Panel p = new Panel ();
			
		GridBagLayout gb = new GridBagLayout();
		p.setLayout (gb);

		GridBagConstraints cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.gridheight = 1;		
		cons.insets = new Insets (4, 4, 4, 4);
		cons.gridwidth = 1;
// 1st Row
		cons.gridy = 0;		
		cons.gridx = 0;
	
		Button b1 = new Button ("Stop");
		p.add (b1, cons);
		b1.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				new Thread (new Runnable () {
						public void run() {
							try {
								stopPlayer();
								timeLabel.setText (String.valueOf (player.getTime()));
							} catch (CAException e) {
								e.printStackTrace();
							}
						}
					}).start();
			}
		});

		cons.gridx = 1;
		Button b2 = new Button ("|<");
		p.add (b2, cons);
		b2.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				new Thread (new Runnable () {
						public void run() {
							try {
								player.backToStart();
								timeLabel.setText ("0");
							} catch (CAException e) {
								e.printStackTrace();
							}
						}
					}).start();
			}
		});

		cons.gridx = 2;
		Button b3 = new Button (">");
		p.add (b3, cons);
		b3.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				if (player.isPlaying())
					return;
				new Thread (new Runnable () {
					public void run() {
						try {
							player.play ();
							stayAlive = true;
							while (stayAlive) {
								timeLabel.setText (String.valueOf (player.getTime()));
								Thread.sleep (1000);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
		
// 2nd Row
		cons.gridy = 1;
		cons.gridx = 0;
		p.add (new Label ("Current Time:"), cons);

		cons.gridx = 1;
		timeLabel = new Label ("0", Label.CENTER);
		p.add (timeLabel, cons);

		cons.gridx = 2;
		p.add (new Label ("beats"), cons);
				
// 3rd Row
		cons.gridy = 2;
		cons.gridx = 0;
		p.add (new Label ("Set Current Time:"), cons);

		TextField timeField = new TextField (4);
		timeField.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				try { 
					player.setTime (new Double (event.getActionCommand()).doubleValue());
					timeLabel.setText (String.valueOf (player.getTime()));
				} catch (CAException ce) {
					ce.printStackTrace();
				}
			}
		});
		timeField.setText ("0");
		cons.gridx = 1;
		p.add (timeField, cons);

		cons.gridx = 2;
		p.add (new Label ("beats"), cons);

// 4th Row
		cons.gridwidth = 3;
		cons.gridy = 3;
		cons.gridx = 0;
				
		Panel reverbButtons = new Panel();
		reverbButtons.setLayout(new GridLayout(2, 3));
		reverbButtons.add(smallRButton);
		reverbButtons.add(mediumRButton);
		reverbButtons.add(largeRButton);
		reverbButtons.add(mediumHButton);
		reverbButtons.add(largeHButton);
		reverbButtons.add(plateButton);
		p.add(reverbButtons, cons);
		
		smallRButton.addItemListener (cboxListener);
		mediumRButton.addItemListener (cboxListener);
		largeRButton.addItemListener (cboxListener);
		mediumHButton.addItemListener (cboxListener);
		largeHButton.addItemListener (cboxListener);
		plateButton.addItemListener (cboxListener);

//		player.getMusicSynth().addPropertyListener (
//								AUProperties.kAudioUnitProperty_ReverbRoomType ,
//								new ReverbMaintainer());
		add (p, "North");
	}
	
	class CheckBoxListener implements ItemListener {
		public void itemStateChanged (ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				Checkbox cbox = (Checkbox)e.getItemSelectable();
				player.setReverbType (cbox.getLabel());
			}
		}
	}

	class ReverbMaintainer implements AUPropertyListener {
		public void execute (AudioUnit unit, int inID, int inScopeID, int inElementID) {
			System.out.println ("reverb changed:" + unit);
			switch (inID) {
				case AUProperties.kReverbRoomType_SmallRoom:
					smallRButton.setState (true);
					smallRButton.repaint();
					break;
				case AUProperties.kReverbRoomType_MediumRoom:
					mediumRButton.setState (true);
					mediumRButton.repaint();
					break;
				case AUProperties.kReverbRoomType_LargeRoom:
					largeRButton.setState (true);
					largeRButton.repaint();
					break;
				case AUProperties.kReverbRoomType_MediumHall:
					mediumHButton.setState (true);
					mediumHButton.repaint();
					break;
				case AUProperties.kReverbRoomType_LargeHall:
					largeHButton.setState (true);
					largeHButton.repaint();
					break;
				case AUProperties.kReverbRoomType_Plate:
					plateButton.setState (true);
					plateButton.repaint();
					break;
			}
		}
	}
	
	CheckBoxListener cboxListener = new CheckBoxListener ();
	CheckboxGroup checkBoxGrp   = new CheckboxGroup();
	Checkbox      smallRButton    = new Checkbox("Small Room", checkBoxGrp, false);
	Checkbox      mediumRButton    = new Checkbox("Medium Room", checkBoxGrp, false);
	Checkbox      largeRButton  = new Checkbox("Large Room", checkBoxGrp, false);
	Checkbox      mediumHButton = new Checkbox("Medium Hall", checkBoxGrp, false);
	Checkbox      largeHButton = new Checkbox("Large Hall", checkBoxGrp, false);
	Checkbox      plateButton = new Checkbox("Plate", checkBoxGrp, false);
	
	void addFilePanelAndShow (String systemBanksDir, String userBanksDir, String userMIDIDir) {
		Panel p = new Panel ();
				
		GridBagLayout gb = new GridBagLayout();
		p.setLayout (gb);

		GridBagConstraints cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.insets = new Insets (4, 4, 4, 4);
		
// 1st Row
		cons.gridheight = 1;		
		cons.gridwidth = 4;		
		cons.gridx = 0;
		cons.gridy = 0;		

		Button b1 = new Button ("Add Sound Banks...");
		p.add (b1, cons);
		b1.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				stopPlayer();
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

		cons.gridx = 4;
		Button b2 = new Button ("Add MIDI Files...");
		p.add (b2, cons);
		b2.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				stopPlayer();
//try{                Thread.sleep (1000);
//}catch (Exception ee) {}
				FileDialog fd = new FileDialog (UIWindow.this, "Add MIDI Files...", FileDialog.LOAD);
				fd.setFilenameFilter (new FilenameFilter () {
					public boolean accept (File dir, String name) {
						return name.endsWith(".smf") || name.endsWith (".mid") 
							|| name.endsWith(".SMF") || name.endsWith (".MID");
					}
				});
				fd.show();
				if (fd.getFile() != null) { //ie we chose a file...
					loadMIDIFiles (fd.getDirectory(), fd.getFile());
				}
			}
		});

// 2nd Row
		cons.gridy = 1;		
		cons.gridheight = 10;		
		cons.gridwidth = 5;		
		cons.gridx = 0;

		createSoundBankList ();
		loadSoundBanks (systemBanksDir);
		loadSoundBanks (userBanksDir);
		p.add (soundBankFileList, cons);
		
		cons.gridwidth = 3;		
		cons.gridx = 5;
		createMIDIFileList (userMIDIDir);
		p.add (midiFileList, cons);

		add (p, "Center");

		pack();
		show();
			//do this after show or it doesn't hi-lite selection
		if (soundBankFileList.getItemCount() > 0) {
			soundBankFileList.select (0);
			selectSoundBank (soundBankFileList.getSelectedItem());
		} else
			player.loadSoundBank (null);

		if (midiFileList.getItemCount() > 0) {
			midiFileList.select (0);
			player.loadMIDIFile (new File (midiFileDir + "/" + midiFileList.getSelectedItem()));
		}		
	}
}
