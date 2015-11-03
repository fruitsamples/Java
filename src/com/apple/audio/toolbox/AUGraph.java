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
//  AUConstants.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.toolbox;

import com.apple.audio.*;
import com.apple.audio.units.*;
import com.apple.audio.util.*;
import com.apple.component.*;
import com.apple.audio.jdirect.Accessor;

/**
 * The AUGraph APIs are responsible for representing the description of a
 * set of AudioUnit components, as well as the audio connections between
 * their inputs and outputs.  This representation may be saved/restored persistently
 * and "instantiated" (open() then initialize()) by opening all of the AudioUnits, and making the physical
 * connections between them stored in the representation.  Thus the AUGraph is
 * a description of the various AudioUnits and their connections, but also may
 * manage the actual instantiated AudioUnits if initialize() when called.
 * The AUGraph, in essence, is a complete description of an audio signal processing
 * network.
 *<P>
 * The AUGraph may be introspected, in order to get complete information about all
 * of the AudioUnits in the graph.  The various nodes (AUNode) in the AUGraph 
 * representing AudioUnits may be added or removed, and the connections between
 * them modified.
 *<P>
 * An AUNode representing an AudioUnit component is created by specifying a
 * ComponentDescription record (from the Component Manager), as well as
 * optional "class" data, which is passed to the AudioUnit when it is opened.
 * This "class" data is in an arbitrary format, and may differ depending on the
 * particular AudioUnit.  In general, the data will be used by the AudioUnit
 * to configure itself when it is opened (in object-oriented terms, it corresponds
 * to constructor arguments).  In addition, certain AudioUnits may provide their
 * own class data when they are closed, allowing their current state to be saved
 * for the next time they are instantiated.  This provides a general mechanism
 * for persistence.
 * <P>
 * <PRE>
	AUGraph.open()			AudioUnits are open but not initialized (no resource allocation occurs here)
	AUGraph.initialize()			AudioUnitInitialize() is called on each open AudioUnit (get ready to render)
	AUGraph.start()					...Start() is called on the "head" node(s) of the AUGraph	(now rendering starts)
	AUGraph.stop()					...Stop() is called on the "head" node(s) of the AUGraph	(rendering is stopped)
	AUGraph.uninitialize()		AudioUnitUninitialize() is called on each open AudioUnit
	AUGraph.close()			all AudioUnits are closed
 * </PRE>
 */
public final class AUGraph extends CAObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}

	private static final Object syncObject = new Object();

	private static final int firstArg4Ptr = JNIToolbox.malloc (4);
	private static final int secondArg4Ptr = JNIToolbox.malloc (4);
	private static final int thirdArg4Ptr = JNIToolbox.malloc (4);
	private static final int fourthArg4Ptr = JNIToolbox.malloc (4);
//_________________________ STATIC METHODS
 	/**
 	 * Create an AUGraph
 	 * <BR><BR><b>CoreAudio::NewAUGraph</b><BR><BR>
 	 */
	public AUGraph () throws CAException {
		super (allocate());
	}
	
	AUGraph (int ptr, Object owner) {
		super (ptr, owner);
	}
		
	private static int allocate () throws CAException {
		synchronized (syncObject) {
			int res = NewAUGraph (firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
	}
	
//_________________________ INSTANCE METHODS
	private ATDispatcher dispatcher;
    
//_________________________ INSTANCE METHODS   
    /**
	 * Creates a new node in the graph, which wil be an instance of the Component
	 * (typically an AudioUnit) that is described by the specified description.
 	 * <BR><BR><b>CoreAudio::AUGraphNewNode</b><BR><BR>
 	 * @param inDescription the component description
 	 * @return the new AUNode
 	 */
	public AUNode newNode (ComponentDescription inDescription) throws CAException {
		int ptr = 0;
		synchronized (syncObject) {
			int res = AUGraphNewNode(_ID(), inDescription._ID(), 0, 0, firstArg4Ptr);
			CAException.checkError(res);
			ptr = Accessor.getIntFromPointer (firstArg4Ptr, 0);
 		}
		return AUNode.getNode (ptr);
	}

	/**
	 * Creates a new node in the graph, which wil be an instance of the Component
	 * (typically an AudioUnit) that is described by the specified description.
	 * The ComponentInstance at that node will be instantiated with the supplied class data.
 	 * <BR><BR><b>CoreAudio::AUGraphNewNode</b><BR><BR>
 	 * @param inDescription the component description
 	 * @param classData data that is specific for this kind of node
 	 * @return the new AUNode
 	 */
	public AUNode newNode (ComponentDescription inDescription, CAMemoryObject classData) throws CAException {
		int ptr = 0;
		synchronized (syncObject) {
			int res = AUGraphNewNode(_ID(), 
								inDescription._ID(), 
								classData.getSize(), 
								CAObject.ID(classData), 
								firstArg4Ptr);
			CAException.checkError(res);
			ptr = Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
		return AUNode.getNode (ptr);
	}

	/**
	 * Removes a node from the graph.
 	 * <BR><BR><b>CoreAudio::AUGraphRemoveNode</b><BR><BR>
 	 * @param inNode the AUNode to remove
 	 */
	public void removeNode (AUNode inNode) throws CAException {
 		synchronized (syncObject) {		//maintain state of our internal vector so synch
            int res = AUGraphRemoveNode (_ID(), CAObject.ID(inNode));
            CAException.checkError(res);
        }
	}

	/**
	 * Returns the number of nodes in the graph.
 	 * <BR><BR><b>CoreAudio::AUGraphGetNodeCount</b><BR><BR>
 	 * @return the new AUNode
 	 */
	public int getNodeCount () throws CAException {
		synchronized (syncObject) {
			int res = AUGraphGetNodeCount (_ID(), firstArg4Ptr);
			CAException.checkError(res);
			return Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
	}

	/**
	 * Returns the node at the specified index0.
 	 * <BR><BR><b>CoreAudio::AUGraphGetIndNode</b><BR><BR>
 	 * @param inIndex0 the index
 	 * @return the AUNode at the index
 	 */
	public AUNode getIndNode (int inIndex0) throws CAException {
		int ptr = 0;
		synchronized (syncObject) {
			int res = AUGraphGetIndNode(_ID(), inIndex0, firstArg4Ptr);
			CAException.checkError(res);
			ptr = Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
		return AUNode.getNode (ptr);
    }

	/**
	 * Clear the connections between the AUGraph's nodes.
 	 * <BR><BR><b>CoreAudio::AUGraphClearConnections</b><BR><BR>
 	 */
	public void clearConnections () throws CAException {
		int res = AUGraphClearConnections (_ID());
		CAException.checkError(res);
	}

	/**
	 * Instantiate the graph from the representation (opens all AudioUnits ).
	 * You should then call initialize to initialize the AudioUnits.
 	 * <BR><BR><b>CoreAudio::AUGraphOpen</b><BR><BR>
 	 */
	public void open () throws CAException {
		int res = AUGraphOpen (_ID());
		CAException.checkError(res);
	}
	 
	/**
	 * Destroy the built graph (leaving only the representation -- closes all AudioUnits)
 	 * <BR><BR><b>CoreAudio::AUGraphClose</b><BR><BR>
 	 */
	public void close () throws CAException {
		int res = AUGraphClose (_ID());
		CAException.checkError(res);
	}
	
	/**
	 * Return's true if the graph is open.
 	 * <BR><BR><b>CoreAudio::AUGraphIsOpen</b><BR><BR>
 	 * @return a boolean
 	 */
	public boolean isOpen () throws CAException {
		synchronized (syncObject) {
			int res = AUGraphIsOpen (_ID(), firstArg4Ptr);
			CAException.checkError(res);
			return Accessor.getByteFromPointer (firstArg4Ptr, 0) != 0;
		}
	}
	
	/**
	 * Once you've opened the Audio Units in a graph, you should then initialize them.
 	 * <BR><BR><b>CoreAudio::AUGraphInitialize</b><BR><BR>
 	 */
	public void initialize () throws CAException {
		int res = AUGraphInitialize (_ID());
		CAException.checkError(res);
	}
	 
	/**
	 * Uninitialize the AudioUnits (calls AudioUnitUninitialize() on each) without closing the components
 	 * <BR><BR><b>CoreAudio::AUGraphUninitialize</b><BR><BR>
 	 */
	public void uninitialize () throws CAException {
		int res = AUGraphUninitialize (_ID());
		CAException.checkError(res);
	}
	
	/**
	 * Return's true if the graph is initialized.
 	 * <BR><BR><b>CoreAudio::AUGraphIsInitialized</b><BR><BR>
 	 * @return a boolean
 	 */
	public boolean isInitialized () throws CAException {
		synchronized (syncObject) {
			int res = AUGraphIsInitialized (_ID(), firstArg4Ptr);
			CAException.checkError(res);
			return Accessor.getByteFromPointer (firstArg4Ptr, 0) != 0;
		}
	}
	
	/**
	 * Starts the graph - it begins to render at this point.
	 * You should then call initialize to initialize the AudioUnits.
 	 * <BR><BR><b>CoreAudio::AUGraphStart</b><BR><BR>
 	 */
	public void start () throws CAException {
		int res = AUGraphStart (_ID());
		CAException.checkError(res);
	}
	 
	/**
	 * Stops the graph - it will no longer be rendering at this point.
 	 * <BR><BR><b>CoreAudio::AUGraphStop</b><BR><BR>
 	 */
	public void stop () throws CAException {
		int res = AUGraphStop (_ID());
		CAException.checkError(res);
	}
	
	/**
	 * Return's true if the graph is running (it has been started).
 	 * <BR><BR><b>CoreAudio::AUGraphIsRunning</b><BR><BR>
 	 * @return a boolean
 	 */
	public boolean isRunning () throws CAException {
		synchronized (syncObject) {
			int res = AUGraphIsRunning (_ID(), firstArg4Ptr);
			CAException.checkError(res);
			return Accessor.getByteFromPointer (firstArg4Ptr, 0) != 0;
		}
	}

	/** 
	 * Retrieves the ComponentDescription from this node, that completely describes
	 * the type of audio unit that the node would create.
 	 * <BR><BR><b>CoreAudio::AUGraphGetNodeInfo</b><BR><BR>
 	 * @param inNode the node from which information is requested
 	 * @return the node's class data
 	 */
	public ComponentDescription getNodeInfo_ComponentDescription (AUNode inNode) throws CAException {
		ComponentDescription desc = new ComponentDescription();
		int res = AUGraphGetNodeInfo(_ID(), 
									CAObject.ID(inNode), 
									desc._ID(),		// pass in NULL if not interested
									0,	// pass in NULL if not interested
									0,			// pass in NULL if not interested
									0);//  0 if component not loaded (graph is not wired)
		CAException.checkError(res);
		return desc;
	}

	/** 
	 * Retrieves the class data for the specified node. Class Data can be used to
	 * reinstantiate a graph's state from one session to another. 
 	 * <BR><BR><b>CoreAudio::AUGraphGetNodeInfo</b><BR><BR>
 	 * @param inNode the node from which information is requested
 	 * @return the node's class data
 	 */
	public CAMemoryObject getNodeInfo_ClassData (AUNode inNode) throws CAException {
		int ptr = 0;
		int ptr2 = 0;
		synchronized (syncObject) {
			int res = AUGraphGetNodeInfo(_ID(), 
										CAObject.ID(inNode), 
										0,		// pass in NULL if not interested
										firstArg4Ptr,	// pass in NULL if not interested
										secondArg4Ptr,	// pass in NULL if not interested
										0);//  0 if component not loaded (graph is not wired)
			CAException.checkError(res);
			
			ptr = Accessor.getIntFromPointer (secondArg4Ptr, 0);
			ptr2 = Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
		return (ptr == 0 
						? null
						: JNIToolbox.newCAMemoryObject (
									ptr, 
									ptr2, 
									this));
	}
	
	/** 
	 * Retrieves the AudioUnit that was created by the AUGraph at the specified node.
	 * If the AUGraph is not opened when this call is made, it will return null as 
	 * the AudioUnit is not created until the graph is opened.
 	 * <BR><BR><b>CoreAudio::AUGraphGetNodeInfo</b><BR><BR>
 	 * @param inNode the node from which information is requested
 	 * @return the AudioUnit at that node or null
 	 */
	public AudioUnit getNodeInfo_AudioUnit (AUNode inNode) throws CAException {
		int ptr = 0;
		synchronized (syncObject) {
			int res = AUGraphGetNodeInfo(_ID(), 
										CAObject.ID(inNode), 
										0,		// pass in NULL if not interested
										0,	// pass in NULL if not interested
										0,			// pass in NULL if not interested
										firstArg4Ptr);//  0 if component not loaded (graph is not wired)
			CAException.checkError(res);
			ptr = Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
		return (ptr == 0
					? null
					: JNIToolbox.findClassForComponentInstance (ptr, this));
	}
	
	/**
	 * Call this after issuing a series of "edits" on the AUGraph with calls such as connectNodeInput()
	 * or disconnectNodeInput() to actually perform these actions.
	 * The call will be synchronous if synchronousFlag is true, meaning it will block until the changes are incorporated
	 * into the graph. If synchronousFlag is false, then AUGraphUpdate() will return immediately will return
	 * "true" if the changes were already made (no more changes to make) or "false" if there are changes still
	 * outstanding - in which case the application must call update() again to have those changes applied.
 	 * <BR><BR><b>CoreAudio::AUGraphUpdate</b><BR><BR>
	 * @param synchronousFlag
	 * @return true if graph has no outstanding updates to perform. If false, updates are still pending, and the
	 * application should call update() again (until true is returned).
	 */
	public boolean update (boolean synchronousFlag) throws CAException {
		if (synchronousFlag) {
			int res = AUGraphUpdate(_ID(), 0);
			CAException.checkError(res);
			return true;
		} else {
			synchronized (syncObject) {
				int res = AUGraphUpdate(_ID(), firstArg4Ptr);
				CAException.checkError(res);
				return Accessor.getByteFromPointer (firstArg4Ptr, 0) != 0;
			}
		}
	}

	/**
	 * This allows an application to receive notifications both before and after an AUGraph performs
	 * a render slice.
	 * <BR><BR><b>CoreAudio::AUGraphSetRenderNotification</b><BR><BR>
	 * @param inCallback the execute method of this interface will be called before and after rendering occurs.
	 */
	public void setRenderNotification (AUGraphRenderNotification callback) throws CAException {
		if (callback != null) {
			if (dispatcher != null)
				removeRenderNotification();
			dispatcher = new ATDispatcher (this, callback);	
			int res = AUGraphSetRenderNotification (_ID(), dispatcher.ID(), 0);
			if (res != 0) {
				dispatcher.cleanup();
				dispatcher = null;
				CAException.checkError (res);
			}
		} else
			removeRenderNotification();
	}
	
	/**
	 * Removes the render callback of this AUGraph.
	 * <BR><BR><b>CoreAudio::AUGraphSetRenderNotification</b><BR>
	 */
	public void removeRenderNotification () throws CAException {
		int res = AUGraphSetRenderNotification(_ID(), 0, 0);
		if (dispatcher != null)
			dispatcher.cleanup();
		dispatcher = null;
		CAException.checkError (res);
	}

	/** 
	 * Connect a node's output to a node's input.
 	 * <BR><BR><b>CoreAudio::AUGraphConnectNodeInput</b><BR><BR>
 	 * @param inSourceNode the source node
 	 * @param inSourceOutputNumber the source node's output bus number
 	 * @param inDestNode the destination node
 	 * @param inDestInputNumber the destinations node's input bus numer
 	 * @see boolean update(boolean)
 	 */
	public void connectNodeInput (AUNode inSourceNode, int inSourceOutputNumber, AUNode inDestNode, int inDestInputNumber) throws CAException {
		int res = AUGraphConnectNodeInput(_ID(),
										CAObject.ID(inSourceNode),
										inSourceOutputNumber,
										CAObject.ID(inDestNode),
										inDestInputNumber);
		CAException.checkError(res);
	}
	
	/** 
	 * Disconnect a node's output to a node's input.
 	 * <BR><BR><b>CoreAudio::AUGraphDisconnectNodeInput</b><BR><BR>
 	 * @param inDestNode the destination node
 	 * @param inDestInputNumber the destinations node's input bus numer
 	 * @see boolean update(boolean)
 	 */
	public void disconnectNodeInput (AUNode inDestNode, int inDestInputNumber) throws CAException {
		int res = AUGraphDisconnectNodeInput(_ID(),
										CAObject.ID(inDestNode),
										inDestInputNumber);
		CAException.checkError(res);
	}

	/**
	 * Returns the number of connections in the graph.
	 * <BR><BR><b>CoreAudio::AUGraphGetNumberOfConnections</b><BR>
	 */
	public int getNumberOfConnections () throws CAException {
		synchronized (syncObject) {
			int res = AUGraphGetNumberOfConnections (_ID(), firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
	}
	
	/**
	 * Gets information about a particular connection between two nodes in an AUGraph.
	 * <BR><BR><b>CoreAudio::AUGraphGetConnectionInfo</b><BR>
	 * @param inConnectionIndex the index of the connections from which to get information
	 * @return information about that connection
	 */
	public AUConnectionInfo getConnectionInfo (int inConnectionIndex) throws CAException {
		int ptr1 = 0;
		int ptr2 = 0;
		int ptr3 = 0;
		int ptr4 = 0;
		synchronized (syncObject) {
			int res = AUGraphGetConnectionInfo (_ID(),
                                                inConnectionIndex,
                                                firstArg4Ptr,
												secondArg4Ptr,
                                                thirdArg4Ptr,
                                                fourthArg4Ptr);
			CAException.checkError (res);
			ptr1 = Accessor.getIntFromPointer (firstArg4Ptr, 0);
			ptr2 = Accessor.getIntFromPointer (secondArg4Ptr, 0);
			ptr3 = Accessor.getIntFromPointer (thirdArg4Ptr, 0);
			ptr4 = Accessor.getIntFromPointer (fourthArg4Ptr, 0);
			
		}
		return new AUConnectionInfo (
						AUNode.getNode (ptr1), //source node
						ptr2,	//source output
						AUNode.getNode (ptr3), //dest node
						ptr4, //dest input
						this);
	}
	
	/**
	 * Gets the source node's connection information for the specified index.
	 * <BR><BR><b>CoreAudio::AUGraphGetConnectionInfo</b><BR>
	 * @param inConnectionIndex the index of the connections from which to get information
	 * @return the Source Node for that index
	 */
	public AUNode getConnectionInfo_SourceNode (int inConnectionIndex) throws CAException {
		int ptr = 0;
		synchronized (syncObject) {
			int res = AUGraphGetConnectionInfo (_ID(),
                                                inConnectionIndex,
                                                firstArg4Ptr,
                                                0,
                                                0,
                                                0);
			CAException.checkError (res);
			ptr = Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
		return AUNode.getNode (ptr);
	}

	/**
	 * Gets the source node's connection information for the specified index.
	 * <BR><BR><b>CoreAudio::AUGraphGetConnectionInfo</b><BR>
	 * @param inConnectionIndex the index of the connections from which to get information
	 * @return the Source Node's OutputNumber for that index
	 */
	public int getConnectionInfo_SourceOutput (int inConnectionIndex) throws CAException {
		synchronized (syncObject) {
			int res = AUGraphGetConnectionInfo (_ID(),
                                                inConnectionIndex,
												0,
                                                firstArg4Ptr,
                                                0,
                                                0);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
	}

	/**
	 * Gets the destination node's connection information for the specified index.
	 * <BR><BR><b>CoreAudio::AUGraphGetConnectionInfo</b><BR>
	 * @param inConnectionIndex the index of the connections from which to get information
	 * @return the Destination Node for that index
	 */
	public AUNode getConnectionInfo_DestinationNode (int inConnectionIndex) throws CAException {
		int ptr = 0;
		synchronized (syncObject) {
			int res = AUGraphGetConnectionInfo (_ID(),
                                                inConnectionIndex,
												0,
												0,
                                                firstArg4Ptr,
                                                0);
			CAException.checkError (res);
			ptr = Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
		return AUNode.getNode (ptr);
	}

	/**
	 * Gets the destination node's connection information for the specified index.
	 * <BR><BR><b>CoreAudio::AUGraphGetConnectionInfo</b><BR>
	 * @param inConnectionIndex the index of the connections from which to get information
	 * @return the Destination Node's input number for that index
	 */
	public int getConnectionInfo_DestinationInput (int inConnectionIndex) throws CAException {
		synchronized (syncObject) {
			int res = AUGraphGetConnectionInfo (_ID(),
                                                inConnectionIndex,
												0,
												0,
												0,
                                                firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
	}

	/**
	 * Get's the graph's CPU load for its last render process.
	 * <BR><BR><b>CoreAudio::AUGraphGetCPULoad</b><BR>
	 */
	public float getCPULoad () throws CAException {
		synchronized (syncObject) {
			int res = AUGraphGetCPULoad (_ID(), firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getFloatFromPointer (firstArg4Ptr, 0);
		}
	}
	
	/**
	 * Add a graph to the current graph. This makes the incoming graph
	 * a sub graph of this graph. The call returns the node that is
	 * associated with this graph.
	 * <BR><BR><b>CoreAudio::AUGraphNewNodeSubGraph</b><BR>
	 */
	public AUNode newNode_SubGraph () throws CAException {
		int ptr = 0;
		synchronized (syncObject) {
			int res = AUGraphNewNodeSubGraph (_ID(),
									firstArg4Ptr);
			CAException.checkError (res);
			ptr = Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
		return AUNode.getNode (ptr);
	}
	
	/**
	 * Get's the sub-graph for this node.
	 * <BR><BR><b>CoreAudio::AUGraphGetNodeInfoSubGraph</b><BR>
	 */
	public AUGraph getNodeInfo_SubGraph (AUNode inNode) throws CAException {
		synchronized (syncObject) {
			int res = AUGraphGetNodeInfoSubGraph (_ID(),
									CAObject.ID(inNode),
									firstArg4Ptr);
			CAException.checkError (res);
			return new AUGraph (Accessor.getIntFromPointer (firstArg4Ptr, 0), this);
		}
	}
								
	/**
	 * This returns true if the specified node is a subgraph
	 * Returns false if the specified node is not a subgraph
	 * <BR><BR><b>CoreAudio::AUGraphIsNodeSubGraph</b><BR>
	 */
	public boolean isNodeSubGraph (AUNode inNode) throws CAException {
		synchronized (syncObject) {
			int res = AUGraphIsNodeSubGraph (_ID(), 
								CAObject.ID(inNode),
								firstArg4Ptr);
			CAException.checkError (res);
			return (Accessor.getByteFromPointer (firstArg4Ptr, 0) != 0);
		}
	}
	
//_ NATIVE METHODS
	private static native int NewAUGraph (int outGraphPtr);
	private static native int AUGraphNewNode(int inGraph, int inDescription, int inClassDataLength,int inClassDataPtr,int outNodePtr);
	private static native int AUGraphRemoveNode (int inGraph, int inNode);
	private static native int AUGraphGetNodeCount(int inGraph, int outNumberOfNodesPtr);
	private static native int AUGraphGetIndNode(int inGraph, int inIndex, int outNodePtr);
	private static native int AUGraphClearConnections (int inGraph);
	private static native int AUGraphGetNodeInfo(int inGraph, 
										int inNode, 
										int outDescriptionPtr,		// pass in NULL if not interested
										int outClassDataLengthPtr,	// pass in NULL if not interested
										int outClassDataPtrPtr,			// pass in NULL if not interested
										int outAudioUnitPtr);//  0 if component not loaded (graph is not wired)
	private static native int AUGraphConnectNodeInput(int inGraph,
										int inSourceNode,
										int inSourceOutputNumber,
										int inDestNode,
										int inDestInputNumber);
	private static native int AUGraphDisconnectNodeInput(int inGraph,
										int inDestNode,
										int inDestInputNumber);
	private static native int AUGraphUpdate(int inGraph, int outBooleanIsUpdatedPtr);
	private static native int AUGraphOpen (int inGraph );
	private static native int AUGraphClose(int inGraph );
	private static native int AUGraphInitialize (int inGraph );
	private static native int AUGraphUninitialize(int inGraph );
	private static native int AUGraphStart(int inGraph);
	private static native int AUGraphStop(int inGraph);
	private static native int AUGraphIsOpen(int inGraph, int outBoolIsOpenPtr);
	private static native int AUGraphIsInitialized(int inGraph, int outBoolIsInitializedPtr);
	private static native int AUGraphIsRunning(int inGraph, int outBoolIsRunningPtr);
	private static native int AUGraphSetRenderNotification (int inGraph, int inCallback, int inRefCon);
	private static native int AUGraphGetNumberOfConnections(int inGraph, int outNumberOfConnectionsPtr);
	private static native int AUGraphGetConnectionInfo (int inGraph,
                                                int inConnectionIndex,
                                                int outSourceNodePtr,
                                                int outSourceOutputNumberPtr,
                                                int outDestNodePtr,
                                                int outDestInputNumberPtr);
	private static native int AUGraphGetCPULoad (int inGraph, int outCPULoadFloatPtr);
	
	private static native int AUGraphNewNodeSubGraph (int	inGraph,
									int 	outNode);
								
	private static native int AUGraphGetNodeInfoSubGraph (int inGraph,
									int 	inNode,
									int 	outSubGraph);
								
// this returns true if the specified node is a subgraph
// it returns false if the specified node is not a subgraph
	private static native int AUGraphIsNodeSubGraph (int	inGraph,
								int			inNode,
								int			outFlag);
}

/*
 */
