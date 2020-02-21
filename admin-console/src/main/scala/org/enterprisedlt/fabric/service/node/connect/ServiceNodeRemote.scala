package org.enterprisedlt.fabric.service.node.connect

import org.enterprisedlt.fabric.service.node.model.{BootstrapOptions, FabricServiceState, JoinOptions, JoinRequest}
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * @author Alexey Polubelov
  */
object ServiceNodeRemote {

    def getOrganisationFullName: Future[String] = {
        Ajax
          .get("/service/organization-full-name")
          .map(_.responseText)
          .map(r => upickle.default.read[String](r))
    }

    def getServiceState: Future[FabricServiceState] = {
        Ajax
          .get("/service/state")
          .map(_.responseText)
          .map(r => upickle.default.read[FabricServiceState](r))
    }

    def executeBootstrap(bootstrapOptions: BootstrapOptions): Future[Unit] = {
        val json = upickle.default.write(bootstrapOptions)
        Ajax
          .post("/admin/bootstrap", json)
          .map { _ => () }
    }

    def executeJoin(joinOptions: JoinOptions): Future[Unit] = {
        val json = upickle.default.write(joinOptions)
        Ajax
          .post("/admin/request-join", json)
          .map { _ => () }
    }

    def createInvite: Future[String] = {
        Ajax
          .get("/admin/create-invite")
          .map(_.responseText)
    }

    def joinNetwork(joinRequest: JoinRequest): Future[Unit] = {
        val json = upickle.default.write(joinRequest)
        Ajax.post("/join-network", json)
          .map(_ => ())
    }



//    def createContract(createContractRequest: CreateContractRequest): Future[Unit] = {
//        val json = upickle.default.write(createContractRequest)
//        Ajax.post("/admin/create-contract", json)
//          .map(_ => ())
//
//    }

}
