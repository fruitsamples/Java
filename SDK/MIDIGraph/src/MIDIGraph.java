/*	Copyright: 	© Copyright 2003 Apple Computer, Inc. All rights reserved.

	Disclaimer:	IMPORTANT:  This Apple software is supplied to you by Apple Computer, Inc.
			("Apple") in consideration of your agreement to the following terms, and your
			use, installation, modification or redistribution of this Apple software
			constitutes acceptance of these terms.  If you do not agree with these terms,
			please do not use, install, modify or redistribute this Apple software.

			In consideration of your agreement to abide by the following terms, and subject
			to these terms, Apple grants you a personal, non-exclusive license, under AppleÕs
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
        File:			MIDIGraph.java
        
        Description:	This demo shows how to create an AUGraph: The graph has a MusicDevice and an output node
	
						The demo also show hows to find MIDI inputs from the MIDI Setup
						and to drive the MusicDevice using note on and off from this MIDI input
						to turn notes on and off on this MusicDevice

						The code also uses the AUGraph notification service to monitor the CPU load of the rendering process
        
        Author:			William Stewart
*/

/* 
 */
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import com.apple.component.*;
import com.apple.audio.midi.*;
import com.apple.audio.hardware.*;
import com.apple.audio.toolbox.*;
import com.apple.audio.units.*;
import com.apple.audio.util.*;
import com.apple.audio.*;

public class MIDIGraph {
    static MIDIGraph grapher;
	static final boolean useUI = false;
	 
    public static void main (String[] args) {
        try {
			grapher = new MIDIGraph();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	MIDIGraph () throws CAException {
		ui = new UIWindow (this);
		ui.pack();
		
		if (configureMIDI() == false) {
			// NO MIDI input sources -> we're outa here
			System.out.println ("NO MIDI Sources detected, exiting...");
			System.exit(1);
		}

		logger = new AUGraphCPUMonitor ();
		ui.add (logger.createUIPanel(), "South");
		theGraph = new MainGraph(); 
		
		logger.logGraph (theGraph.getMainGraph(), "MIDIGraph", 200);

		ui.addUIElementsAndShow ("/Library/Audio/Sounds/Banks", 
						System.getProperty("user.home") + "/Library/Audio/Sounds/Banks");
		
	}
	
    private UIWindow ui;
	private AUGraphCPUMonitor logger;
	private MainGraph theGraph;
	
    private Hashtable midiSources;
    private String currentMIDISrc;
    private MIDIClient myMIDIClient;
	
    private AUMIDIController mAUMIDIController;

    private MIDIProcessor myMIDIProcessor;
    private MIDIInputPort myMIDIInPort;

    MIDIProcessor getMIDIProcessor () {
        return myMIDIProcessor;
    }

    private boolean configureMIDI () throws CAException {
        System.out.println("creating MIDISetup");
		myMIDIClient = new MIDIClient (new CAFString("MIDIGraph client"), null);
		myMIDIProcessor = new MIDIProcessor (this);
        myMIDIInPort = myMIDIClient.inputPortCreate(new CAFString("SMFPlayer In"), myMIDIProcessor);
		int numInputSources = MIDISetup.getNumberOfSources ();

        System.out.println("MIDISetup: num sources= " + numInputSources);

        if (numInputSources == 0)
                return false;
            
        midiSources = new Hashtable();
		            
        for (int i = 0; i < numInputSources; i++) {
            MIDIEndpoint midiEndpoint = MIDISetup.getSource (i);
            CAFString name_src = midiEndpoint.getStringProperty(MIDIConstants.kMIDIPropertyName);
            midiSources.put (name_src.asString(), midiEndpoint);
			if (i == 0)
				setMIDISource (name_src.asString());
        }
		
		System.out.println (midiSources);
		
        return true;
    }

    void setMIDISource (String src) throws CAException {
        System.out.println ("MIDISrc:" + src + "," + midiSources.get(src));
        if (mAUMIDIController == null)
			mAUMIDIController = new AUMIDIController ();
		
  		mAUMIDIController.connectSource ((MIDIEndpoint)midiSources.get (src));
		currentMIDISrc = src;
    }

    boolean hasMIDIInputs() {
        if (midiSources == null)
            return false;
        if (midiSources.isEmpty())
            return false;
        return true;
    }
    
    String[] getMIDIInputs () {
        if (midiSources == null)
            return null;
        if (midiSources.isEmpty())
            return null;
        Enumeration iter = midiSources.keys();
        String[] ar = new String [midiSources.size()];
        for (int i = 0; iter.hasMoreElements(); i++)
            ar[i] = (String)iter.nextElement();
        return ar;
    }
	
    void loadSoundBank (File soundBankFile) throws Exception {
		System.out.println (soundBankFile);
		
		AUNode theSubNode = theGraph.addSubGraph ();
		AUGraph theSubGraph = theGraph.getSubGraph (theSubNode);
		
		DLSGraph sub = new DLSGraph (theSubGraph, theSubNode, soundBankFile);
		
		theGraph.connectSubGraph (theSubNode);
		musicSynth = sub.getMusicDevice();

		myMIDIProcessor.reset();
		myMIDIInPort.connectSource ((MIDIEndpoint)midiSources.get (currentMIDISrc));
 	}

    private MusicDevice musicSynth;
	MusicDevice getMusicDevice () {
		return musicSynth;
	}

	void cleanUp() {
		try {
			if (logger != null)
				logger.cleanUp();
			theGraph.cleanUp();
		} catch (CAException e) {
			e.printStackTrace();
		}
	}
}
