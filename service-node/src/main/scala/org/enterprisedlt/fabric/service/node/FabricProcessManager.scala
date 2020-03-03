package org.enterprisedlt.fabric.service.node

import org.enterprisedlt.fabric.service.node.model.ProcessManagerState

/**
  * @author Alexey Polubelov
  */
trait FabricProcessManager {

    def getProcessState(): Option[ProcessManagerState]

    def startOrderingNode(name: String): Either[String, String]

    def osnAwaitJoinedToRaft(name: String): Unit

    def osnAwaitJoinedToChannel(name: String, channelName: String): Unit

    def startPeerNode(name: String): Either[String, String]

    def peerAwaitForBlock(name: String, blockNumber: Long): Unit

    def terminateChainCode(peerName: String, chainCodeName: String, chainCodeVersion: String): Unit
}
