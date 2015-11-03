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
//  MIDIOutputPort.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Jon Summers
//
package com.apple.audio.midi;

import com.apple.audio.util.*;

public final class MIDIConstants {
	
	private MIDIConstants() {}
    /**
        discussion:		device/entity/endpoint property, string
        
            Devices, entities, and endpoints may all have names.  The recommended way
            to display an endpoint's name is to ask for the endpoint name, and display
            only that name if it is unique.  If it is non-unique, prepend the device
            name.
            
            A setup editor may allow the user to set the names of both driver-owned 
            and external devices.
    */
	public final static CAFString kMIDIPropertyName = new CAFString ("name");
    /**
        discussion:		device/endpoint property, string
        
            Drivers should set this property on their devices.
            Setup editors may allow the user to set this property on external devices.
            Creators of virtual endpoints may set this property on their endpoints.
    */
	public final static CAFString kMIDIPropertyManufacturer = new CAFString("manufacturer");
    /**
        discussion:		device/endpoint property, string
    
            Drivers should set this property on their devices.
            Setup editors may allow the user to set this property on external devices.
            Creators of virtual endpoints may set this property on their endpoints.
    */
	public final static CAFString kMIDIPropertyModel = new CAFString("model");
    /**
        discussion:		devices, entities, endpoints all have unique ID's, integer
        
            The system assigns unique ID's to all objects.  Creators of virtual
            endpoints may set this property on their endpoints, though doing so
            may fail if the chosen ID is not unique.
    */
	public final static CAFString kMIDIPropertyUniqueID = new CAFString("uniqueID");
    /**
        discussion:		device/entity property, integer
        
            The entity's system-exclusive ID, in user-visible form
            Drivers may set this property on their devices or entities.
            Setup editors may allow the user to set this property on
            external devices.
    */
	public final static CAFString kMIDIPropertyDeviceID = new CAFString("deviceID");
    /**
        discussion:		endpoint property, integer
    
            The value is a bitmap of channels on which the object receives, (1<<0)=ch 1...(1<<15)=ch 16.
            Drivers may set this property on their entities or endpoints.
            
            Setup editors may allow the user to set this property on external endpoints.
            Virtual destination may set this property on their endpoints.
    */
	public final static CAFString kMIDIPropertyReceiveChannels = new CAFString("receiveChannels");
    /**
        discussion:		device/entity/endpoint property, integer
    
            Set by the owning driver; should not be touched by other clients.
            maximum bytes/second of sysex messages sent to it
            (default is 3125, as with MIDI 1.0)
    */
	public final static CAFString kMIDIPropertyMaxSysExSpeed = new CAFString("maxSysExSpeed");
    /**
        discussion:		device/entity/endpoint property, integer
    
            Set by the owning driver; should not be touched by other clients.
            If it is > 0, then it is a recommendation of how many microseconds 
            in advance clients should schedule output.  Clients should treat this
            value as a minimum.  For devices with a > 0 advance schedule time, 
            drivers will receive outgoing messages to the device at the time they 
            are sent by the client, via MIDISend, and the driver is responsible 
            for scheduling events to be played at the right times according to their
            timestamps.
            
            As of CoreMIDI 1.3, this property may also be set on virtual destinations
            (but only the creator of the destination should do so).
            When a client sends to a virtual destination with an advance schedule time
            of 0, the virtual destination receives its messages at their scheduled
            delivery time.  If a virtual destination has a non-zero advance schedule time,
            it receives timestamped messages as soon as they are sent, and must do its
            own scheduling of the events.
    */
	public final static CAFString kMIDIPropertyAdvanceScheduleTimeMuSec = new CAFString("scheduleAheadMuSec");
    /**
        discussion:		device/entity/endpoint property, string
    
            Name of the driver that owns a device.
            Set by the owning driver, on the device; should not be touched by other clients.
            Property is inherited from the device by its entities and endpoints.
    
            New in CoreMIDI 1.1.
    */
	public final static CAFString kMIDIPropertyDriverOwner = new CAFString ("driver");
    /**
        discussion:		entity/endpoint property, integer
    
            0 if there are external MIDI connectors, 1 if not.
            New in CoreMIDI 1.1.
    */
	public final static CAFString kMIDIPropertyIsEmbeddedEntity = new CAFString ("embedded");
    /**
        discussion:		device/entity/endpoint property, integer or CFDataRef
    
            UniqueID of an external device/entity/endpoint attached to this one
            (strongly recommended that it be an endpoint).
            This is for the use of a setup editor UI; not currently used internally.
            A driver-owned entity or endpoint has this property to refer
            to an external MIDI device that is connected to it.
            
            The property is non-existant or 0 if there is no connection.
    
            New in CoreMIDI 1.1.
            
            Beginning with CoreMIDI 1.3, this property may be a CFDataRef containing
            an array of big-endian SInt32's, to allow specifying that a driver object connects
            to multiple external objects (via MIDI thru-ing or splitting).
            
            This property may also exist for external devices/entities/endpoints,
            in which case it signifies a MIDI Thru connection to another external
            device/entity/endpoint (again, strongly recommended that it be an endpoint).
    */
	public final static CAFString kMIDIPropertyConnectionUniqueID = new CAFString ("connUniqueID");
    /**
        discussion:		device/entity/endpoint property, integer
    
            1 = device is offline (is temporarily absent), 0 = present
            Set by the owning driver, on the device; should not be touched by other clients.
            Property is inherited from the device by its entities and endpoints.
    
            New for CoreMIDI 1.1.
    */
	public final static CAFString kMIDIPropertyOffline = new CAFString ("offline");
    /**
        discussion:		device/entity/endpoint property, integer
    
            1 = endpoint is private, hidden from other clients.
            May be set on a device or entity, but they will still appear in the API; only
            affects whether the owned endpoints are hidden.
    
            New for CoreMIDI 1.3.
    */
	public final static CAFString kMIDIPropertyPrivate = new CAFString ("private");
    /**
        discussion:		entity/endpoint property, integer
    
            1 if the endpoint broadcasts messages to all of the other endpoints
            in the device, 0 if not.  Set by the owning driver; should not be touched
            by other clients.
    
            New for CoreMIDI 1.3.
    */
	public final static CAFString kMIDIPropertyIsBroadcast = new CAFString ("broadcast");
    /**
        discussion:		device/entity/endpoint property, CFData containing AliasHandle
    
            An alias to the device's current factory patch name file.
    
            Added in CoreMIDI 1.1.  DEPRECATED as of CoreMIDI 1.3.
            Use kMIDIPropertyNameConfiguration instead.
    */
	public final static CAFString kMIDIPropertyFactoryPatchNameFile = new CAFString ("factoryPatchFile");
    /**
        discussion:		device/entity/endpoint property, CFData containing AliasHandle
    
            An alias to the device's current user patch name file.
    
            Added in CoreMIDI 1.1.  DEPRECATED as of CoreMIDI 1.3.
            Use kMIDIPropertyNameConfiguration instead.
    */
	public final static CAFString kMIDIPropertyUserPatchNameFile = new CAFString ("userPatchFile");
    /**
        discussion:		device/entity/endpoint property, CFDictionary
    
                        This specifies the device's current patch, note and control
                        name values using the MIDINameDocument XML format.  This
                        specification requires the use of higher-level, OS-specific constructs
                        outside of the specification, to fully define the current
                        names for a device.
                        
                        The MIDINameConfiguration property is implementated as a CFDictionary:
                        
                        key "master" maps to a CFDataRef containing an AliasHandle
                        referring to the device's master name document.
                        
                        key "banks" maps to a CFDictionaryRef.  This dictionary's keys
                        are CFStringRef names of patchBank elements in the master document,
                        and its values are each a CFDictionaryRef: key "file" maps to a CFDataRef
                        containing an AliasHandle to a document containing patches
                        that override those in the master document, and key "patchNameList"
                        maps to a CFStringRef which is the name of the patchNameList
                        element in the overriding document.
                        
                        key "currentModes" maps to a 16-element CFArrayRef, each element
                        of which is a CFStringRef of the name of the current mode for
                        each of the 16 MIDI channels.
                        
                        Clients setting this property must take particular care to preserve dictionary
                        values other than the ones they are interested in changing, and
                        to properly structure the dictionary.
                        
                        New for CoreMIDI 1.3.
    */
	public final static CAFString kMIDIPropertyNameConfiguration = new CAFString ("nameConfiguration");
    
	public final static CAFString kMIDIDriverPropertyUsesSerial = new CAFString ("MIDIDriverUsesSerial");
    /**
        discussion:		device property, CFStringRef which is a full POSIX path to a device
                        or external device's icon, stored in any standard graphic file format,
                        preferably .icns but JPEG, GIF, PNG and TIFF are all acceptable.  (See CFURL
                        for functions to convert between POSIX paths and other ways
                        of specifying files.)
                        
                        Drivers should set the icon on the devices they add.
                        
                        A studio setup editor should allow the user to choose icons
                        for external devices.
                        
                        New for CoreMIDI 1.3.
    */
	public final static CAFString kMIDIPropertyImage = new CAFString ("image");
    
    /**
        discussion:		endpoint property, integer
    
            The value is a bitmap of channels on which the object transmits, (1<<0)=ch 1...(1<<15)=ch 16
            New for CoreMIDI 1.3.
    */
	public final static CAFString kMIDIPropertyTransmitChannels = new CAFString ("transmitChannels");
    /**
        discussion:		device/entity/endpoint property, integer, returns the
                        driver version API of the owning driver (only for driver-
                        owned devices).  Drivers need not set this property;
                        applications should not write to it.
                        
                        New for CoreMIDI 1.3.
    */
    public final static CAFString kMIDIPropertyDriverVersion = new CAFString ("driverVersion");
    
	public final static CAFString kMIDIPropertySupportsGeneralMIDI = new CAFString ("supports General MIDI");
	public final static CAFString kMIDIPropertySupportsMMC = new CAFString ("supports MMC");
    /** external device property, integer to indicate whether the external device is capable of routing its MIDI Ins to its MIDI Outs*/
	public final static CAFString kMIDIPropertyCanRoute = new CAFString ("can route");
    
	public final static CAFString kMIDIPropertyReceivesClock = new CAFString ("receives clock");
	public final static CAFString kMIDIPropertyReceivesMTC = new CAFString ("receives MTC");
	public final static CAFString kMIDIPropertyReceivesNotes = new CAFString ("receives notes");
	public final static CAFString kMIDIPropertyReceivesProgramChanges = new CAFString ("receives program changes");
	public final static CAFString kMIDIPropertyReceivesBankSelectMSB = new CAFString ("receives bank select MSB");
	public final static CAFString kMIDIPropertyReceivesBankSelectLSB = new CAFString ("receives bank select LSB");
    
	public final static CAFString kMIDIPropertyTransmitsClock = new CAFString ("transmits clock");
	public final static CAFString kMIDIPropertyTransmitsMTC = new CAFString ("transmits MTC");
	public final static CAFString kMIDIPropertyTransmitsNotes = new CAFString ("transmits notes");
	public final static CAFString kMIDIPropertyTransmitsProgramChanges = new CAFString ("transmits program changes");
	public final static CAFString kMIDIPropertyTransmitsBankSelectMSB = new CAFString ("transmits bank select MSB");
	public final static CAFString kMIDIPropertyTransmitsBankSelectLSB = new CAFString ("transmits bank select LSB");
    
	public final static CAFString kMIDIPropertyPanDisruptsStereo = new CAFString ("pan disrupts stereo");
	public final static CAFString kMIDIPropertyIsSampler = new CAFString ("is sampler");
	public final static CAFString kMIDIPropertyIsDrumMachine = new CAFString ("is drum machine");
	public final static CAFString kMIDIPropertyIsMixer = new CAFString ("is mixer");
	public final static CAFString kMIDIPropertyIsEffectUnit = new CAFString ("is effect unit");
    
	/** This is the message ID passed in the notification proc that the MIDISetup has changed*/
	public static final int
	    kMIDIMsgSetupChanged        = 1,
        kMIDIMsgObjectAdded			= 2,
        kMIDIMsgObjectRemoved		= 3,
        kMIDIMsgPropertyChanged		= 4;
        
}

/*
 */
