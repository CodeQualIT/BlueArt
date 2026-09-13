package com.github.cc007.blueart.components

import io.github.cc007.kolostyles.dsl.kolo
import io.github.cc007.kolostyles.dsl.layout.display.flex
import io.github.cc007.kolostyles.dsl.layout.offset.top
import io.github.cc007.kolostyles.dsl.layout.sticky
import io.github.cc007.kolostyles.dsl.layout.z
import io.github.cc007.kolostyles.dsl.spacing.m
import io.github.cc007.kolostyles.dsl.spacing.px
import io.github.cc007.kolostyles.dsl.spacing.py
import kotlinx.html.*

fun BODY.topBanner(csrfToken: String?) {
    header(classes = "top-banner") {
        kolo { flex; sticky; top(0); z(10); px(3); py(5) }
        div(classes = "brand") {
            h1 {
                kolo { m(0) }
                +"BlueArt"
            }
        }
        form(action = "/logout", method = FormMethod.post, classes = "logout-form") {
            kolo { m(0) }
            csrfToken?.let {
                input(type = InputType.hidden, name = "_csrf") {
                    value = csrfToken
                }
            }
            submitInput(classes = "logout-button") {
                kolo { px(4); py(2) }
                value = "Logout"
            }
        }
    }
}