package org.enterprisedlt.fabric.service.node.page

import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import org.enterprisedlt.fabric.service.node.{BootstrapMode, Context, Initial, JoinMode}
import org.scalajs.dom.html.Div

import scala.scalajs.js

/**
 * @author Alexey Polubelov
 */
object Boot {

    case class BootstrapSettings(
        networkName: String = "test_net",
        // --
        blockMaxMessageCount: Int = 150,
        blockAbsoluteMaxBytes: Int = 103809024,
        blockPreferredMaxBytes: Int = 524288,
        blockBatchTimeOut: String = "1s",
        // --
        raftTickInterval: String = "500ms",
        raftElectionTick: Int = 10,
        raftHeartbeatTick: Int = 1,
        raftMaxInflightBlocks: Int = 5,
        raftSnapshotIntervalSize: Int = 20971520,
        // --
        osn1Port: Int = 7001,
        osn2Port: Int = 7002,
        osn3Port: Int = 7003,
        // --
        peer0Port: Int = 7010
    )

    private val component = ScalaComponent.builder[Unit]("BootstrapMode")
      .initialState(BootstrapSettings())
      .renderBackend[Backend]
      .build

    class Backend(val $: BackendScope[Unit, BootstrapSettings]) {

        def goInit: Callback = Callback {
            Context.State.update(_ => Initial)
        }

        def goBootProgress: Callback = Callback() // TODO

        def render(s: BootstrapSettings): VdomTagOf[Div] =
            <.div(^.className := "card aut-form-card",
                <.div(^.className := "card-header text-white bg-primary",
                    <.h1("Bootstrap new network")
                ),
                <.div(^.className := "card-body aut-form-card",
                    //                    <form>
                    <.h5("Network settings:"),
                    <.span(<.br()),
                    <.div(^.className := "form-group row",
                        <.label(^.className := "col-sm-2 col-form-label", "Network name"),
                        <.div(^.className := "col-sm-10",
                                <.input(^.`type` := "text", ^.`className` := "form-control", //TODO: //TODO:value.bind="bootstrapSettings.networkName">)
                                )
                        )
                    ),
                    <.h5("Ordering nodes:"),
                    <.span(<.br()),
                    <.div(^.className := "form-group row",
                        <.label(^.className := "col-sm-2 col-form-label", "OSN1 port"),
                        <.div(^.className := "col-sm-10",
                                <.input(^.`type` := "text", ^.className := "form-control", //TODO://TODO:value.bind="bootstrapSettings.network.orderingNodes[0].port">
                                )
                        )
                    ),
                    <.div(^.className := "form-group row",
                        <.label(^.className := "col-sm-2 col-form-label", "OSN2 port"),
                        <.div(^.className := "col-sm-10",

                                <.input(^.`type` := "text", ^.className := "form-control" //TODO:value.bind="bootstrapSettings.network.orderingNodes[1].port">
                                )
                        )
                    ),
                    <.div(^.className := "form-group row",
                        <.label(^.className := "col-sm-2 col-form-label", "OSN3 port"),
                        <.div(^.className := "col-sm-10",
                                <.input(^.`type` := "text", ^.className := "form-control" //TODO:value.bind="bootstrapSettings.network.orderingNodes[2].port">
                                )
                        )
                    ),
                    <.h5("Peer nodes:"),
                    <.span(<.br()),
                    <.div(^.className := "form-group row",
                        <.label(^.className := "col-sm-2 col-form-label", "Peer1 port"),
                        <.div(^.className := "col-sm-10",

                                <.input(^.`type` := "text", ^.className := "form-control" //TODO:value.bind="bootstrapSettings.network.peerNodes[0].port">
                                )
                        )
                    ),
                    <.h5("Block settings:"),
                    <.span(<.br()),
                    <.div(^.className := "form-group row",
                        <.label(^.className := "col-sm-2 col-form-label", "Batch timeout"),
                        <.div(^.className := "col-sm-10",
                                <.input(^.`type` := "text", ^.className := "form-control" //TODO:value.bind="bootstrapSettings.block.batchTimeOut">
                                )
                        )
                    ),
                    <.div(^.className := "form-group row",
                        <.label(^.className := "col-sm-2 col-form-label", "Max messages count"),
                        <.div(^.className := "col-sm-10",
                                <.input(^.`type` := "text", ^.className := "form-control" //TODO:value.bind="bootstrapSettings.block.maxMessageCount">
                                )
                        )
                    ),
                    <.div(^.className := "form-group row",
                        <.label(^.className := "col-sm-2 col-form-label", "Absolute max bytes"),
                        <.div(^.className := "col-sm-10",
                                <.input(^.`type` := "text", ^.className := "form-control" //TODO:value.bind="bootstrapSettings.block.absoluteMaxBytes">
                                )
                        )
                    ),
                    <.div(^.className := "form-group row",
                        <.label(^.className := "col-sm-2 col-form-label", "Preferred max bytes"),
                        <.div(^.className := "col-sm-10",
                                <.input(^.`type` := "text", ^.className := "form-control" //TODO:value.bind="bootstrapSettings.block.preferredMaxBytes">
                                )
                        )
                    ),
                    <.hr(),
                    <.h5("Raft settings:"),
                    <.span(<.br()),
                    <.div(^.className := "form-group row",
                        <.label(^.className := "col-sm-2 col-form-label", "Tick interval"),
                        <.div(^.className := "col-sm-10",
                                <.input(^.`type` := "text", ^.className := "form-control" //TODO:value.bind="bootstrapSettings.raftSettings.tickInterval">
                                )
                        )
                    ),
                    <.div(^.className := "form-group row",
                        <.label(^.className := "col-sm-2 col-form-label", "Election tick"),
                        <.div(^.className := "col-sm-10",
                                <.input(^.`type` := "text", ^.className := "form-control" //TODO:value.bind="bootstrapSettings.raftSettings.electionTick">
                                )
                        )
                    ),
                    <.div(^.className := "form-group row",
                        <.label(^.className := "col-sm-2 col-form-label", "Heartbeat tick"),
                        <.div(^.className := "col-sm-10",
                                <.input(^.`type` := "text", ^.className := "form-control" //TODO:value.bind="bootstrapSettings.raftSettings.heartbeatTick">
                                )
                        )
                    ),
                    <.div(^.className := "form-group row",
                        <.label(^.className := "col-sm-2 col-form-label", "Max inflight blocks"),
                        <.div(^.className := "col-sm-10",
                                <.input(^.`type` := "text", ^.className := "form-control" //TODO:value.bind="bootstrapSettings.raftSettings.maxInflightBlocks">
                                )
                        )
                    ),
                    <.div(^.className := "form-group row",
                        <.label(^.className := "col-sm-2 col-form-label", "Snapshot interval size"),
                        <.div(^.className := "col-sm-10",
                                <.input(^.`type` := "text", ^.className := "form-control" //TODO:value.bind="bootstrapSettings.raftSettings.snapshotIntervalSize">
                                )
                        )
                    )
                ),
                <.hr(),
                <.div(^.className := "form-group mt-1",
                    <.button(^.`type` := "button", ^.className := "btn btn-outline-secondary", ^.onClick --> goInit, "Back"),
                    <.button(^.`type` := "button", ^.className := "btn btn-outline-success float-right", ^.onClick --> goBootProgress, "Bootstrap")
                )
                //                                                                                                        </form>
            )
    }

    def apply(): Unmounted[Unit, BootstrapSettings, Backend] = component()
}
