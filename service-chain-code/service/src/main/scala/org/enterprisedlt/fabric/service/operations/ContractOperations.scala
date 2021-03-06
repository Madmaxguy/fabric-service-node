package org.enterprisedlt.fabric.service.operations

import org.enterprisedlt.fabric.contract.OperationContext
import org.enterprisedlt.fabric.service.Main
import org.enterprisedlt.fabric.service.model.{CollectionsHelper, Contract, Organization, UpgradeContract}
import org.enterprisedlt.spec.{ContractOperation, ContractResult}
import org.enterprisedlt.spec._

import scala.util.Try


/**
 * @author Andrew Pudovikov
 */
trait ContractOperations {
    self: Main.type =>

    @ContractOperation(OperationType.Invoke)
    def createContract(contract: Contract): ContractResult[Unit] =
        getOwnOrganization
          .flatMap { founderOrg =>
              for {
                  _ <- Option(contract.name).filter(_.nonEmpty).toRight("Contract name must be non empty")
                  _ <- Option(contract.chainCodeName).filter(_.nonEmpty).toRight("Chaincode name must be non empty")
                  _ <- Option(contract.chainCodeVersion).filter(_.nonEmpty).toRight("Chaincode version must be non empty")
                  participantsList <- Option(contract.participants).filter(_.nonEmpty).toRight("Participants list must be non empty")
              } yield {
                  participantsList
                    .filter(e => e != founderOrg.name)
                    .foreach { orgFromList =>
                        OperationContext.store.get[Organization](orgFromList)
                          .map { participant =>
                              //                              logger.debug(s"putting in to coll ${CollectionsHelper.collectionNameFor(founderOrg, participant)} ${contract}")
                              OperationContext.privateStore(
                                  CollectionsHelper.collectionNameFor(founderOrg, participant))
                                .put(contract.name, contract.copy(founder = founderOrg.name,
                                    timestamp = OperationContext.transaction.timestamp.getEpochSecond))
                          }
                    }
              }

          }

    @ContractOperation(OperationType.Invoke)
    def upgradeContract(upgradeContract: UpgradeContract): ContractResult[Unit] =
        getOwnOrganization
          .flatMap { founderOrg =>
              for {
                  _ <- Option(upgradeContract.name).filter(_.nonEmpty).toRight("Contract name must be non empty")
                  _ <- Option(upgradeContract.chainCodeName).filter(_.nonEmpty).toRight("Chaincode name must be non empty")
                  _ <- Option(upgradeContract.chainCodeVersion).filter(_.nonEmpty).toRight("Chaincode version must be non empty")
                  participantsList <- Option(upgradeContract.participants).filter(_.nonEmpty).toRight("Participants list must be non empty")
              } yield {
                  participantsList
                    .filter(e => e != founderOrg.name)
                    .foreach { orgFromList =>
                        OperationContext.store.get[Organization](orgFromList)
                          .map { participant =>
                              OperationContext.privateStore(
                                  CollectionsHelper.collectionNameFor(founderOrg, participant))
                                .put[UpgradeContract](upgradeContract.name, upgradeContract.copy(founder = founderOrg.name,
                                    timestamp = OperationContext.transaction.timestamp.getEpochSecond))
                          }
                    }
              }

          }


    @ContractOperation(OperationType.Query)
    def listContracts: ContractResult[Array[Contract]] = Try {
        CollectionsHelper
          .collectionsFromOrganizations(
              OperationContext.store
                .list[Organization]
                .map(_.value.mspId))
          .filter(e => e.endsWith(s"-${OperationContext.clientIdentity.mspId}") || e.startsWith(s"${OperationContext.clientIdentity.mspId}-"))
          .map(OperationContext.privateStore)
          .flatMap { store =>
              store.list[Contract].map(_.value)
          }.toArray
    }


    @ContractOperation(OperationType.Query)
    def getContract(name: String, founder: String): ContractResult[Contract] =
        getOwnOrganization
          .flatMap { org =>
              for {
                  contractFounderOrg <- OperationContext.store.get[Organization](founder).toRight(s"Contract founder organization $founder isn't registered")
                  contractNameValue <- Option(name).filter(_.nonEmpty).toRight("Contract name must be non empty")
                  r <- OperationContext.privateStore(
                      CollectionsHelper.collectionNameFor(org, contractFounderOrg))
                    .get[Contract](contractNameValue)
                    .toRight(s"No contract with name $contractNameValue from ${contractFounderOrg.name} org")
              } yield r
          }

    @ContractOperation(OperationType.Invoke)
    def delContract(name: String, founder: String): ContractResult[Unit] =
        getOwnOrganization
          .flatMap { org =>
              for {
                  contractFounderOrg <- OperationContext.store.get[Organization](founder).toRight(s"Contract founder organization $founder isn't registered")
                  contractNameValue <- Option(name).filter(_.nonEmpty).toRight("Contract name must be non empty")
              } yield
                  OperationContext
                    .privateStore(
                        CollectionsHelper.collectionNameFor(org, contractFounderOrg))
                    .del[Contract](contractNameValue)
          }
}
