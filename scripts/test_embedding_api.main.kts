@file:DependsOn("org.yaml:snakeyaml:2.2")

import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun getServerPort(): Int {
    val yaml = Yaml()
    val configFiles = listOf(
        File("../src/main/resources/application-dev.yml"),
        File("../src/main/resources/application.yaml")
    )

    val configFile = configFiles.firstOrNull { it.exists() }

    if (configFile == null) {
        println("警告: 未找到 application.yml 或 application-dev.yml。使用默认端口 9901。")
        return 9901
    }

    println("从 ${configFile.name} 读取配置...")

    return try {
        FileInputStream(configFile).use { inputStream ->
            val config = yaml.load<Map<String, Any>>(inputStream)
            val serverConfig = config["server"] as? Map<*, *>
            serverConfig?.get("port") as? Int ?: 9901
        }
    } catch (e: Exception) {
        println("读取配置文件时出错: ${e.message}。使用默认端口 9901。")
        9901
    }
}

fun main() {
    val port = getServerPort()
    val client = HttpClient.newBuilder().build()

    // --- 测试 POST /ai/embedding ---
    println("\n--- 1. 测试 POST /ai/embedding ---")
    val embedMessage = "Hello from Kotlin Script, just embedding."
    val embedPayload = """{"message": "$embedMessage"}"""
    val embedRequest = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:$port/ai/embedding"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(embedPayload))
        .build()

    println("向 ${embedRequest.uri()} 发送请求...")
    sendRequest(client, embedRequest)

    // --- 测试 POST /ai/embedding/store ---
    println("\n--- 2. 测试 POST /ai/embedding/store ---")
    val storeMessage = "Hello from Kotlin Script, please store me."
    val storePayload = """{"message": "$storeMessage"}"""
    val storeRequest = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:$port/ai/embedding/store"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(storePayload))
        .build()

    println("向 ${storeRequest.uri()} 发送请求...")
    sendRequest(client, storeRequest)
}

fun sendRequest(client: HttpClient, request: HttpRequest) {
    try {
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println("响应状态码: ${response.statusCode()}")
        println("响应体: ${response.body()}")
    } catch (e: Exception) {
        println("请求时发生错误: ${e.message}。")
        println("请确保 Janus 应用正在 http://localhost:${request.uri().port} 上运行")
    }
}

main() 