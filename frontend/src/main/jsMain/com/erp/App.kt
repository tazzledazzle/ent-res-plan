package com.erp

import react.*
import react.dom.client.createRoot
import kotlinx.browser.document
import kotlinx.browser.window
import com.erp.api.ERPClient
import com.erp.components.*
import react.router.dom.*

fun main() {
    val container = document.createElement("div")
    document.body!!.appendChild(container)

    val client = ERPClient()

    createRoot(container)
        .render(
            BrowserRouter {
                App(client)
            }
        )
}
