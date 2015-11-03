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
        File:			MIDIProcessor.java
        
        Description:	the "main" graph that you add sub-graphs to
        
        Author:			William Stewart
*/

/* 
 */
import com.apple.audio.midi.*;
import com.apple.audio.util.*;
import com.apple.audio.CAException;
import com.apple.audio.units.MusicDevice;
import java.io.PrintStream;

public class MIDIProcessor implements MIDIReadProc
{
    private static final boolean debugPrint = false;
    private MIDIGraph player;
    private int transposeValue;
    private int useChannel;
    private boolean inSysEx;
    int patch;
    int volume;

    MIDIProcessor(MIDIGraph grapher)
    {
        inSysEx = false;
        volume = 127;
        player = grapher;
    }

    public void execute(MIDIInputPort port, MIDIEndpoint endpoint, MIDIPacketList list)
    {
        for (int i = 0; i < list.numPackets(); i++)
        {
            MIDIPacket packet = list.getPacket(i);
            processMIDIEvent(port, endpoint, packet.getData());
        }
    }

    synchronized void processMIDIEvent(MIDIInputPort port, MIDIEndpoint endpoint, MIDIData data)
    {
        try
        {
            int startOffset = data.getMIDIDataOffset();
            int numMIDIdata = startOffset + data.getMIDIDataLength();
            for (int i = startOffset; i < numMIDIdata; )
            {
                int commandByte = data.getUByteAt(i);
                int command = commandByte & 240;
                if (command == 240 || inSysEx)
                {
                    int j;
                    inSysEx = true;
                    for (j = i; j < numMIDIdata && data.getUByteAt(j) < 128; ++j) 
						;
                    if (j == numMIDIdata)
                        return;
                    inSysEx = false;
                    if (data.getUByteAt(j) == 247)
                        i = j + 1;
                    else
                        i = j;
                }
                else if (command == 144 || command == 128)
                {
                    doNote(144, data.getByteAt(i + 1), (command == 128) ? 0 : data.getByteAt(i + 2));
                    i += 3;
                }
                else if (command == 192)
                {
                    setPatchNumber(data.getByteAt(i + 1));
                    i += 2;
                }
                else
                {
                    int j;
                    for (j = i; ++j < numMIDIdata && data.getUByteAt(j) < 128; ) /* null body */ ;
                    if (command < 240)
                    {
                        if (j - i == 2)
                            doMIDI(command + useChannel, data.getByteAt(i + 1));
                        else if (j - i == 3)
                            doMIDI(command + useChannel, data.getByteAt(i + 1), data.getByteAt(i + 2));
                        else
                        {
                            System.out.println("Error in MIDI parsing:i=" + i + ",j=" + j);
                            for (int k = startOffset; k < numMIDIdata; k++)
                                System.out.print("0x:" + Integer.toHexString(CAUtils.UByte2Int(data.getByteAt(k))) + ",");
                            System.out.println("");
                        }
                    }
                    i = j;
                }
            }
        }
        catch (Exception cae)
        {
            cae.printStackTrace();
        }
    }

    private void doNote(int command, int data1, int data2)
        throws CAException
    {
        if (data2 > 0)
            data2 = 127;
        player.getMusicDevice().sendMIDIEvent(command + useChannel, data1 + transposeValue, data2, 0);
    }

    private void doMIDI(int command, int data1, int data2)
        throws CAException
    {
        player.getMusicDevice().sendMIDIEvent(command + useChannel, data1, data2, 0);
    }

    private void doMIDI(int command, int data1)
        throws CAException
    {
        player.getMusicDevice().sendMIDIEvent(command + useChannel, data1, 0, 0);
    }

    void reset()
    {
        setPatchNumber(patch);
        setVolume(volume);
    }

    void setPatchNumber(int patch)
    {
        try
        {
            player.getMusicDevice().sendMIDIEvent(192 + useChannel, patch, 0, 0);
            this.patch = patch;
        }
        catch (CAException e)
        {
            e.printStackTrace();
        }
    }

    void setVolume(int vol)
    {
        try
        {
            player.getMusicDevice().sendMIDIEvent(176 + useChannel, 7, vol, 0);
            volume = vol;
        }
        catch (CAException e)
        {
            e.printStackTrace();
        }
    }

    void setTransposeValue(int trans)
    {
        transposeValue = trans;
    }

    void setChannel(int chan)
    {
        useChannel = chan;
        setPatchNumber(patch);
    }
}
