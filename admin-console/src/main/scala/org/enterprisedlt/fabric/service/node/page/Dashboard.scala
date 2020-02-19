package org.enterprisedlt.fabric.service.node.page

import java.nio.charset.StandardCharsets

import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import org.enterprisedlt.fabric.service.node.connect.ServiceNodeRemote
import org.enterprisedlt.fabric.service.node.util.Tags._
import org.scalajs.dom
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.{Blob, FileReader, HTMLLinkElement, URL}

import scala.scalajs.js
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author Alexey Polubelov
 */
object Dashboard {

    case class State()

    private val component = ScalaComponent.builder[Unit]("Dashboard")
      .initialState(State())
      .renderBackend[Backend]
      .build

    class Backend(val $: BackendScope[Unit, State]) {

        def createInvite: Callback = Callback {
            ServiceNodeRemote.createInvite.map { invite =>
                doDownload(invite, "invite.json")
            }
        }

        def render(s: State): VdomTagOf[Div] =
            <.div(
                Tabs(
                    ("general", "General",
                      <.div(
                          <.button(^.tpe := "button", ^.className := "btn btn-outline-secondary", ^.onClick --> createInvite, "Create invite")
                      )
                    ),
                    ("users", "Users",
                      <.div(

                      )
                    ),
                    ("contracts", "Contracts",
                      <.div(

                      )
                    )
                )
            )

        def doDownload(content: String, name: String): Unit  = {
            val blob = new Blob(js.Array(content))
            val dataUrl = URL.createObjectURL(blob)
            val anchor = dom.document.createElement("a").asInstanceOf[HTMLLinkElement]
            anchor.href = dataUrl
            anchor.setAttribute("download", name)
            anchor.innerHTML = "Downloading..."
            anchor.style.display = "none"
            dom.document.body.appendChild(anchor)
            js.timers.setTimeout(1) {
                anchor.click()
                dom.document.body.removeChild(anchor)
                js.timers.setTimeout(500) {
                    URL.revokeObjectURL(anchor.href)
                }
            }
        }
    }

    def apply(): Unmounted[Unit, State, Backend] = component()
}
