@file:DependsOn("org.yaml:snakeyaml:2.2")

import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.random.Random

// --- Configuration ---
val UPLOADER_ID = "388e9514-8840-4528-a28a-f5b2b2796d88" // Hardcoded test user ID
val TEST_FILE_PATH = "../document/step1.md"
// --- End Configuration ---

fun getServerPort(): Int {
    val yaml = Yaml()
    val configFiles = listOf(
        File("../src/main/resources/application-dev.yml"),
        File("../src/main/resources/application.yaml")
    )

    val configFile = configFiles.firstOrNull { it.exists() } ?: return 9901.also {
        println("警告: 未找到 application.yml 或 application-dev.yml。使用默认端口 9901。")
    }

    println("从 ${configFile.name} 读取配置...")
    return try {
        FileInputStream(configFile).use {
            val config = yaml.load<Map<String, Any>>(it)
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
    val client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()
    val testFile = File(TEST_FILE_PATH)

    if (!testFile.exists()) {
        println("错误: 测试文件未找到 at $TEST_FILE_PATH")
        return
    }

    // --- 1. 上传文件 ---
    println("\n--- 1. 上传文件 ---")
    val boundary = "Boundary-${UUID.randomUUID()}"
    val uploadRequest = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:$port/api/storage/upload?uploaderId=$UPLOADER_ID"))
        .header("Content-Type", "multipart/form-data; boundary=$boundary")
        .POST(ofMimeMultipartData(mapOf("file" to testFile.toPath()), boundary))
        .build()

    println("向 ${uploadRequest.uri()} 发送上传请求...")
    val uploadResponse = sendRequest(client, uploadRequest)
    val storageObjectId = extractJsonValue(uploadResponse, "id")

    if (storageObjectId == null) {
        println("错误: 未能从上传响应中获取 storageObjectId。")
        return
    }
    println("文件上传成功，Storage Object ID: $storageObjectId")


    // --- 2. 查询文件详情 ---
    println("\n--- 2. 查询文件详情 ---")
    val detailsRequest = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:$port/api/storage/$storageObjectId"))
        .GET()
        .build()
    println("向 ${detailsRequest.uri()} 发送查询请求...")
    sendRequest(client, detailsRequest)


    // --- 3. 触发嵌入 ---
    println("\n--- 3. 触发嵌入 ---")
    val embedRequest = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:$port/api/storage/$storageObjectId/embed"))
        .POST(HttpRequest.BodyPublishers.noBody())
        .build()
    println("向 ${embedRequest.uri()} 发送嵌入请求...")
    sendRequest(client, embedRequest)


    // --- 4. 轮询检查状态 ---
    println("\n--- 4. 轮询检查嵌入状态 (最多等待30秒) ---")
    var status: String? = ""
    for (i in 1..10) {
        Thread.sleep(3000) // 等待3秒
        println("检查 #${i}...")
        val statusResponse = sendRequest(client, detailsRequest)
        status = extractJsonValue(statusResponse, "embeddingStatus")
        if (status == "COMPLETED") {
            println("成功! 文件嵌入状态为 COMPLETED。")
            break
        }
    }
    if (status != "COMPLETED") {
        println("警告: 文件嵌入未在30秒内完成。最后状态: $status")
    }

    println("\n测试脚本执行完毕。")
}

fun sendRequest(client: HttpClient, request: HttpRequest): String {
    try {
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println("响应状态码: ${response.statusCode()}")
        println("响应体: ${response.body()}")
        if (response.statusCode() >= 400) {
             println("请求失败，请检查服务日志。")
        }
        return response.body()
    } catch (e: Exception) {
        println("请求时发生错误: ${e.message}。")
        println("请确保 Janus 应用正在 http://localhost:${request.uri().port} 上运行。")
        return ""
    }
}

fun extractJsonValue(json: String, key: String): String? {
    // A simple regex for demonstration; a proper JSON library would be more robust.
    val regex = "\"$key\"\\s*:\\s*\"([^\"]+)\"".toRegex()
    return regex.find(json)?.groupValues?.get(1)
}

fun ofMimeMultipartData(data: Map<String, Any>, boundary: String): HttpRequest.BodyPublisher {
    val byteArrays = ArrayList<ByteArray>()
    val separator = "--$boundary\r\nContent-Disposition: form-data; name=".toByteArray()

    for ((key, value) in data) {
        byteArrays.add(separator)
        if (value is Path) {
            val
            path = value
            val mimeType = Files.probeContentType(path) ?: "application/octet-stream"
            byteArrays.add("\"$key\"; filename=\"${path.fileName}\"\r\nContent-Type: $mimeType\r\n\r\n".toByteArray())
            byteArrays.add(Files.readAllBytes(path))
            byteArrays.add("\r\n".toByteArray())
        } else {
            byteArrays.add("\"$key\"\r\n\r\n$value\r\n".toByteArray())
        }
    }
    byteArrays.add("--$boundary--".toByteArray())
    return HttpRequest.BodyPublishers.ofByteArrays(byteArrays)
}


main() 