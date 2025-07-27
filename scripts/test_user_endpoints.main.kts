@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("org.yaml:snakeyaml:2.2")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.UUID

// --- Data classes for JSON parsing ---
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null
)

@Serializable
data class UserVO(
    val id: String,
    val displayName: String,
    val email: String,
    val role: String
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
        if (response.statusCode() !in 200..299 && response.statusCode() != 404) {
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
    val json = Json { ignoreUnknownKeys = true }
    val baseUrl = "http://localhost:$port/api"

    // --- 1. Test GET /api/users and get a valid user ID ---
    println("--- Testing GET /api/users ---")
    val usersRequest = HttpRequest.newBuilder()
        .uri(URI.create("$baseUrl/users?page=0&size=5"))
        .GET()
        .build()
    val usersResponse = sendRequest(client, usersRequest)
    
    val validUserId = if (usersResponse.statusCode() == 200) {
        try {
            val apiResponse = json.decodeFromString<ApiResponse<List<UserVO>>>(usersResponse.body())
            apiResponse.data?.firstOrNull()?.id
        } catch (e: Exception) {
            println("Failed to parse users response: ${e.message}")
            null
        }
    } else {
        null
    }

    if (validUserId == null) {
        println("\nCould not retrieve a valid user ID from /api/users. Skipping detailed user test.")
    } else {
        // --- 2. Test GET /api/users/{id} with the retrieved valid ID ---
        println("\n--- Testing GET /api/users/$validUserId ---")
        val userRequest = HttpRequest.newBuilder()
            .uri(URI.create("$baseUrl/users/$validUserId"))
            .GET()
            .build()
        sendRequest(client, userRequest)
    }

    // --- 3. Test GET /api/users/{id} (Not Found) ---
    println("\n--- Testing GET /api/users/{random-uuid} (Not Found) ---")
    val nonExistentUserId = UUID.randomUUID().toString()
    val notFoundRequest = HttpRequest.newBuilder()
        .uri(URI.create("$baseUrl/users/$nonExistentUserId"))
        .GET()
        .build()
    sendRequest(client, notFoundRequest)

    println("\n--- User endpoint tests finished. ---")
}

main()
