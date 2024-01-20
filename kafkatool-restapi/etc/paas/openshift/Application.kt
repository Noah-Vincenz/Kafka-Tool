import com.fasterxml.jackson.databind.SerializationFeature
import consumer.buildConsumer
import domain.BackgroundJob
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.cio.CIO
import org.jetbrains.exposed.sql.Database
import org.koin.core.context.startKoin
import org.koin.dsl.module
import com.viartemev.ktor.flyway.FlywayFeature
import io.ktor.util.*
import mu.KotlinLogging
import service.*
import util.topicConfigs

val topicAppModule = module {
    single<EventService> { EventServiceImpl() }
    single<LogService> { LogServiceImpl() }
}

fun main(args: Array<String>) {
    startKoin {
        modules(listOf(topicAppModule))
    }
    embeddedServer(CIO, commandLineEnvironment(args)).start(true)
}

private val logger = KotlinLogging.logger {}


@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    moduleWithDependencies(EventServiceImpl(), LogServiceImpl())
}

@KtorExperimentalAPI
fun Application.moduleWithDependencies(eventService: EventService, logService: LogService) {

    val config = environment.config.config("ktor")
    logger.debug { "ENV = ${config.property("environment").getString()}" }

    install(BackgroundJob.BackgroundJobFeature) {
        name = "Kafka-Consumer-Job"
        job = buildConsumer<ByteArray, ByteArray>( // TODO: changed this to <String, ByteArray>
            environment,
            topicConfigs
        )
    }
    install(StatusPages) {
        exception<Throwable> { e ->
            call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
            throw e
        }
    }
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    val db = DatabaseFactory.createHikari()
    Database.connect(db)
    install(FlywayFeature) {
        dataSource = db
    }

    install(Routing) {
//        if (!testing) trace { application.log.trace(it.buildText()) }
        restApi(eventService, logService)
    }
}
