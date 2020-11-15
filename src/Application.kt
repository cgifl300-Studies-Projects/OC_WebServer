package com.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val cours = arrayOf (Course(1, "Cours numéro 1", active = true),
        Course(2, "Cours numéro 2", active = false),
        Course(3, "Cours numéro 3", active = false))

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        get("/") {
            call.respondText("Welcome to OpenClassrooms brand new server !", contentType = ContentType.Text.Plain)
        }

        get("/course/top") {
            // Afficher une réponse Json correspondant au meilleur cours sorti par OpenClassrooms
            call.respond(mapOf("id" to cours[0].id,
                    "title" to cours[0].title,
                    "complexity" to cours[0].complexity,
                    "active" to cours[0].active))
        }

        get("/course/{id}") {
            // Afficher les informations d'un cours. Pour la démo, vous devrez gérer uniquement les cours possédant
            // l'identifiant 1, 2 et 3. Vous renverrez un message d'erreur si d'autres identifiants sont renseignés.
            val id = call.parameters["id"].toString()
            var idvalue = 0
            try {
                idvalue = id.toInt()
            }
            catch (e: NumberFormatException)
            { idvalue = 0 }

            if ((idvalue > 3) or (idvalue < 1)) {
                call.respondText("$id course is not avaible", contentType = ContentType.Text.Plain)
            } else {
                call.respond(mapOf("id" to cours[idvalue - 1].id,
                        "title" to cours[idvalue - 1].title,
                        "complexity" to cours[idvalue - 1].complexity,
                        "active" to cours[idvalue - 1].active))
            }
        }

        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }

        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.red
                }
                p {
                    fontSize = 2.em
                }
                rule("p.myclass") {
                    color = Color.blue
                }
            }
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}