@file:Suppress("PackageDirectoryMismatch")

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.mustache.Mustache
import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import main.kotlin.TopicService
import main.kotlin.TopicServiceImpl
import mu.KotlinLogging
import org.koin.core.context.startKoin
import org.koin.dsl.module
import producer.buildProducer

private val logger = KotlinLogging.logger {}

val topicAppModule = module {
    single<TopicService> { TopicServiceImpl() }
}

fun main(args: Array<String>) {
    startKoin {
        modules(listOf(topicAppModule))
    }
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    moduleWithDependencies(TopicServiceImpl())
}

fun Application.moduleWithDependencies(topicService: TopicService) {

    val config = environment.config.config("ktor")
    logger.debug { "ENV = ${config.property("environment").getString()}" }

    install(StatusPages) {
        when {
            isDev -> {
                this.exception<Throwable> { e ->
                    call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
                    throw e
                }
            }

            isTest -> {
                this.exception<Throwable> { e ->
                    call.response.status(HttpStatusCode.InternalServerError)
                    throw e
                }
            }

            isProd -> {
                this.exception<Throwable> { e ->
                    call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
                    throw e
                }
            }
        }
    }

    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }

    val producer = buildProducer<ByteArray, ByteArray>(environment)

    topicService.setApiEndpoint(config.property("restapi.endpoint").getString())

    install(Routing) {
        if (isDev) trace {
            application.log.trace(it.buildText())
        }
        eventsUI(topicService, producer)
        staticResources()
    }

}

val Application.envKind get() = environment.config.property("ktor.environment").getString()
val Application.isDev get() = envKind == "dev"
val Application.isTest get() = envKind == "test"
val Application.isProd get() = envKind != "dev" && envKind != "test"
