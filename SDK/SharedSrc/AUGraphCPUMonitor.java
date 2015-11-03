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
        File:			AUGraphCPUMonitor.java
        
        Description:	Monitors the CPU usage of an AUGraph displaying the usage in a java.awt.Panel
        
        Author:			William Stewart
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import com.apple.audio.toolbox.*;

public class AUGraphCPUMonitor {
	private static int whichFile;

	private AUGraph myAUGraph;
	private Thread logThread = null;
	private boolean doLogging = true;
	private File logFile;
	private FileWriter logWriter;
	private int sleepTime = 400;
	private boolean loggingToFile = false;
	private Canvas metre;
	private Checkbox cbox;
	private float currentLoad;
	private String logFileName;
	
	public void logGraph (AUGraph graph, String fileName, int sleep) {
		myAUGraph = graph;
		logFileName = fileName;
		this.sleepTime = sleep;
		
		logThread = new Thread(new Runnable () {
			public void run () {
				try {
					Thread.sleep (2000);
					for (int i = 0; doLogging; i++) {
						float load = myAUGraph.getCPULoad();
						if (loggingToFile)
							logToFile (load, i);
						updateUIMetre (load);
						Thread.sleep (sleepTime);
					}
				} catch (Exception io) {
					io.printStackTrace();
				}
			}
		});
		logThread.start();
	}
		
	public void cleanUp() {
		try {
			doLogging = false;
			if (loggingToFile) {
				logWriter.flush();
				logWriter.close();
			}
        } catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Panel createUIPanel () {
		Panel p = new Panel ();
		p.setLayout (new BorderLayout());
		
		Label l = new Label ("Current CPU Load of AUGraph");
		p.add (l, "North");
		
		metre = new Canvas () {
			Dimension d = new Dimension (100, 8);
			public Dimension getPreferredSize () {
				return d;
			}
			
			public void paint (Graphics g) {
				Dimension dim = getSize();
				g.setColor (Color.gray);
				g.fillRect (0, 0, dim.width, d.height);
				g.setColor (Color.green);
				g.fillRect (0, 0, (int)(dim.width * currentLoad), d.height);
			}
		};
		
		p.add (metre, "Center");
		
		cbox = new Checkbox ("Log to File");
		cbox.addItemListener (new ItemListener () {
			public void itemStateChanged( ItemEvent event ) {
				if (cbox.getState())
					startFileLogging();
				else
					stopFileLogging();
			}
		});
		p.add (cbox, "South");
		
		p.add (new Canvas () {
			Dimension d = new Dimension (4, 8);
			public Dimension getPreferredSize () {
				return d;
			}
		}, "East");
		p.add (new Canvas () {
			Dimension d = new Dimension (4, 8);
			public Dimension getPreferredSize () {
				return d;
			}
		}, "West");
		
		return p;
	}
	
	void updateUIMetre (float load) {
		currentLoad = load;
		metre.repaint();
	}
	
	void logToFile (float load, int index) throws IOException {
		String str = Float.toString (load);
		logWriter.write (str, 0, str.length());
		logWriter.write ('\n');
		if ((index & 0xFF) == 0xFF)
			logWriter.flush();
	}

	private void startFileLogging () {
		try {
			logFile = new File (System.getProperty ("user.home") + "/" + logFileName + "_Log_" + whichFile++ + ".txt");
			logWriter = new FileWriter (logFile);
			String str = "Log File for AUGraph\n";
			logWriter.write (str, 0, str.length());
			logWriter.flush();
			loggingToFile = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private void stopFileLogging () {
		try {
			loggingToFile = false;
			logWriter.flush();
			logWriter.close();
			logWriter = null;
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
}
