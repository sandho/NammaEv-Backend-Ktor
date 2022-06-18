package com.example.plugins

import com.example.db.DBClient
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.locations.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting() {

    install(StatusPages) {
        exception<AuthenticationException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<AuthorizationException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden)
        }
    }

    install(Locations) {}

    routing {
        get("/") {
            call.respondText("Working fine...")
        }
        get("/user") {
            call.respond(
                User(
                    "1",
                    "I'm the User",
                    "NammaEv Pro",
                    60.0,
                    2,
                    54.0,
                    40.0,
                    40.3
                )
            )
        }
        get("/station") {
            call.respond(
                mutableListOf(
                    Station(1, LatLng(0.0, 0.0), STATION_TYPE.HOME, true),
                    Station(2, LatLng(0.0, 0.0), STATION_TYPE.POWER, true),
                    Station(3, LatLng(0.0, 0.0), STATION_TYPE.REPAIR, false)
                )
            )
        }
        post("/station/review") {
            val review = call.receive<RatingReview>()

            call.respond(review)
        }
        post("/station/report") {
            val report = call.receive<ReportStation>()

            call.respond(report)
        }
        post("check") {
            val dbClient = DBClient()

            dbClient.create(
                User(
                    "1",
                    "I'm the User",
                    "NammaEv Pro",
                    60.0,
                    2,
                    54.0,
                    40.0,
                    40.3
                )
            ).let {
                user ->
                call.respond("stored successfully")
                call.respond(HttpStatusCode.Created)
            } ?: call.respond(HttpStatusCode.BadRequest)
        }
    }
}

@kotlinx.serialization.Serializable
data class RatingReview(
    var id: Int,
    var rating: Double,
    var comments: String,
    var stationID: Int
)

@kotlinx.serialization.Serializable
data class ReportStation(
    var id: Int
)

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

@kotlinx.serialization.Serializable
data class User(
    val id: String? = null,
    var name: String,
    var vehicle: String,
    var health: Double,
    var lastChargingStation: Int,
    var range: Double,
    var lastCharging: Double,
    var rideStateFromLastCharge: Double
)

@kotlinx.serialization.Serializable
data class Station(
    var id: Int,
    var location: LatLng,
    var type: STATION_TYPE,
    var availability: Boolean
)

@kotlinx.serialization.Serializable
data class LatLng(
    var lat: Double,
    var lng: Double
)

enum class STATION_TYPE {
    HOME,
    POWER,
    REPAIR
}