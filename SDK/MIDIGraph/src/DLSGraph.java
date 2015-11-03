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
        File:			DLSGraph.java
        
        Description:	Create a sub graph with a DLS unit in it
        
        Author:			William Stewart
*/

/* 
 */

import java.io.*;

import com.apple.component.*;
import com.apple.audio.*;
import com.apple.audio.toolbox.*;
import com.apple.audio.units.*;

public class DLSGraph {

	public DLSGraph (AUGraph inGraph, AUNode inNode, File inSoundBankFile) throws Exception {
		myAUGraph = inGraph;
		myGraphNode = inNode; //this is the node of the sub graph
		setupGraph();
		setSoundBank (inSoundBankFile);
		
	}
	
	private AUGraph myAUGraph;
	private AUNode myGraphNode;
	private MusicDevice myMusicDevice;
	
	public AUNode getNode () { return myGraphNode; }
	
	public AUGraph getGraph () { return myAUGraph; }
	
	public MusicDevice getMusicDevice () { return myMusicDevice; }
	
	private void setupGraph () throws CAException 
	{
		System.out.println ("Creating Graph:" + myAUGraph);
        			
		ComponentDescription cd = new ComponentDescription (AUConstants.kAudioUnitComponentType,
													AUConstants.kAudioUnitSubType_MusicDevice,
													AUConstants.kAudioUnitID_DLSSynth,
													0, 
													0);
		AUNode synth = myAUGraph.newNode (cd);
		
		cd.setSubType (AUConstants.kAudioUnitSubType_Output);
		cd.setManufacturer (AUConstants.kAudioUnitID_GenericOutput);
		AUNode output = myAUGraph.newNode (cd);
			
		myAUGraph.open();				
			
		myMusicDevice = (MusicDevice)myAUGraph.getNodeInfo_AudioUnit (synth);
				
		// this is the dry output of the synth
		myAUGraph.connectNodeInput (synth, 0, output, 0);
	}

	private void setSoundBank (File inFile) throws Exception {
		myMusicDevice.setProperty_SoundBank (inFile);
		myAUGraph.initialize();
	}
}	
