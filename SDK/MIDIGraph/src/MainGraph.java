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
        File:			MainGraph.java
        
        Description:	the "main" graph that you add sub-graphs to
        
        Author:			William Stewart
*/

/* 
 */

import com.apple.component.*;
import com.apple.audio.*;
import com.apple.audio.hardware.*;
import com.apple.audio.toolbox.*;
import com.apple.audio.units.*;
import com.apple.audio.util.*;

public class MainGraph 
{
	public MainGraph () throws CAException {
		createGraph();
	}
	
	private AUGraph myAUGraph;
	private AUNode mixer;
	private AudioUnit mixerUnit;
	private int whichBus = 0;
	
	public AUNode addSubGraph () throws CAException {
		return myAUGraph.newNode_SubGraph();
	}
	
	public AUGraph getSubGraph (AUNode inNode) throws CAException {
		return myAUGraph.getNodeInfo_SubGraph (inNode);
	}
	
	public void connectSubGraph (AUNode inSub) throws CAException {
			//there are 64 input buses for the mixer
		myAUGraph.connectNodeInput (inSub, 0, mixer, (whichBus++ % 64));
		while (myAUGraph.update(false) == false) {
			Thread.yield();
		}
	}
	
	public AUGraph getMainGraph () { return myAUGraph; }
	
	public AUNode getMixer () { return mixer; }
	
	private synchronized void createGraph () throws CAException 
	{
		System.out.println ("Creating Graph:" + myAUGraph);
        
		myAUGraph = new AUGraph();
			
		ComponentDescription cd = new ComponentDescription (AUConstants.kAudioUnitComponentType,
													AUConstants.kAudioUnitSubType_Mixer,
													AUConstants.kAudioUnitID_StereoMixer,
													0, 
													0);
		mixer = myAUGraph.newNode (cd);
		
		cd.setSubType (AUConstants.kAudioUnitSubType_Output);
		cd.setManufacturer (AUConstants.kAudioUnitID_DefaultOutput);
		AUNode output = myAUGraph.newNode (cd);
			
		myAUGraph.open();				

		mixerUnit = myAUGraph.getNodeInfo_AudioUnit (mixer);

		AudioDeviceOutputUnit outputUnit = (AudioDeviceOutputUnit)myAUGraph.getNodeInfo_AudioUnit (output);
		AudioDevice device = outputUnit.getCurrentDevice();
		CAMemoryObject bufferSize = new CAMemoryObject (4, false);
		device.getOutputProperty(0, AHConstants.kAudioDevicePropertyBufferFrameSize, bufferSize);
		System.out.println ("Buffer Size (before):" + bufferSize.getIntAt(0));
		
		bufferSize.setIntAt (0, 128); 
		device.setOutputProperty(null, 
					0, 		
					AHConstants.kAudioDevicePropertyBufferFrameSize, 
					bufferSize);
					
		bufferSize.setIntAt (0, 0);
		device.getOutputProperty(0, AHConstants.kAudioDevicePropertyBufferFrameSize, bufferSize);
		System.out.println ("Buffer Size (after):" + bufferSize.getIntAt(0) + " frames");
		
		myAUGraph.connectNodeInput (mixer, 0, output, 0);

		myAUGraph.initialize();
		
		// just start it up, even though we don't have a synth yet
		myAUGraph.start();
	}
	
	void cleanUp() throws CAException {
		myAUGraph.stop();
		myAUGraph.uninitialize();
		myAUGraph.close();
	}
}
