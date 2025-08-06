@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("org.yaml:snakeyaml:2.2")
@file:DependsOn("com.google.code.gson:gson:2.10.1")

import com.google.gson.Gson
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.ZonedDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

// --- Data class for JSON serialization (using Gson, no @Serializable needed) ---
data class TrackingDto(
    val eventName: String,
    val eventTime: String,
    val page: String,
    val target: String,
    val userId: Long?,
    val properties: Map<String, String>?
)

// --- Helper Functions ---
fun getServerPort(): Int {
    val yaml = Yaml()
    // Adjusted path for scripts directory
    val configFiles = listOf(
        File("./src/main/resources/application-dev.yml"),
        File("./src/main/resources/application.yaml")
    )

    val configFile = configFiles.firstOrNull { it.exists() }

    if (configFile == null) {
        println("Warning: application.yml or application-dev.yml not found. Using default port 9901.")
        return 9901
    }

    println("Reading configuration from ${configFile.name}...")

    return try {
        FileInputStream(configFile).use { inputStream ->
            val config = yaml.load<Map<String, Any>>(inputStream)
            val serverConfig = config["server"] as? Map<*, *>
            serverConfig?.get("port") as? Int ?: 9901
        }
    } catch (e: Exception) {
        println("Error reading config file: ${e.message}. Using default port 9901.")
        9901
    }
}

fun sendRequest(client: HttpClient, request: HttpRequest): HttpResponse<String> {
    try {
        println("Sending request to ${request.uri()}...")
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println("Status: ${response.statusCode()}")
        println("Response Body: ${response.body()}")
        if (response.statusCode() !in 200..299) {
             println("Request failed with status code ${response.statusCode()}")
        }
        return response
    } catch (e: Exception) {
        println("An error occurred during the request: ${e.message}")
        println("Please ensure the Janus application is running at http://localhost:${request.uri().port}")
        throw e
    }
}

// --- Main Execution Logic ---
fun main() {
    val port = getServerPort()
    val client = HttpClient.newBuilder().build()
    val gson = Gson()
    val trackingUrl = "http://localhost:$port/api/v1/track"

    // --- 1. Test POST /api/v1/track ---
    println("\n--- Testing POST /api/v1/track ---")

    val trackingEvent = TrackingDto(
        eventName = "script_test_run",
        eventTime = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT),
        page = "/scripts/test_tracking_endpoint.main.kts",
        target = "main_execution",
        userId = 999L, // Test user ID
        properties = mapOf("framework" to "kotlin-script", "test-uuid" to UUID.randomUUID().toString())
    )

    val jsonBody = gson.toJson(trackingEvent)
    println("Request Body: $jsonBody")

    val trackRequest = HttpRequest.newBuilder()
        .uri(URI.create(trackingUrl))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
        .build()

    val response = sendRequest(client, trackRequest)
    if (response.statusCode() == 200) {
        println("--> Tracking event sent successfully!")
    } else {
        println("--> Failed to send tracking event.")
    }

    println("\n--- Tracking endpoint test finished. ---")
}

main()
