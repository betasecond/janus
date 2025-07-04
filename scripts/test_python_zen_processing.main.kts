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
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.system.exitProcess

// --- Configuration ---
val UPLOADER_ID = "388e9514-8840-4528-a28a-f5b2b2796d88" // Hardcoded test user ID
// 使用一个更小的公共文本文件URL
val DOCUMENT_URL = "https://svn.python.org/projects/peps/trunk/pep-0020.txt"
val TEMP_FILE_NAME = "temp_zen_of_python.md" // 更新临时文件名
val POLLING_TIMEOUT_SECONDS = 60 // 恢复超时时间，因为文件很小
// --- End Configuration ---

/**
 * 从 application.yml 或 application-dev.yml 文件中读取服务器端口。
 * 如果找不到文件或读取失败，则返回默认端口 9901。
 */
fun getServerPort(): Int {
    val yaml = Yaml()
    val configFile = listOf(File("../src/main/resources/application-dev.yml"), File("../src/main/resources/application.yaml"))
        .firstOrNull { it.exists() } ?: return 9901.also { println("警告: 未找到 application.yml。使用默认端口 9901。") }

    return try {
        FileInputStream(configFile).use {
            val config = yaml.load<Map<String, Any>>(it)
            (config["server"] as? Map<*, *>)?.get("port") as? Int ?: 9901
        }
    } catch (e: Exception) {
        9901.also { println("读取配置文件时出错: ${e.message}。使用默认端口 9901。") }
    }
}

/**
 * 从指定的URL下载文件并保存到本地临时路径。
 */
fun downloadFileFromUrl(client: HttpClient, url: String, destination: Path) {
    println("--- 0. 从URL下载文件 ---")
    try {
        val request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofInputStream())
        if (response.statusCode() == 200) {
            Files.copy(response.body(), destination, StandardCopyOption.REPLACE_EXISTING)
            println("文件已成功下载到: $destination")
        } else {
            println("错误: 下载文件失败。状态码: ${response.statusCode()}")
            exitProcess(1)
        }
    } catch (e: Exception) {
        println("错误: 下载文件时发生异常: ${e.message}")
        exitProcess(1)
    }
}

fun main() {
    val port = getServerPort()
    val client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()
    val tempFilePath = Paths.get(TEMP_FILE_NAME)

    // 在程序退出时注册一个钩子来删除临时文件
    Runtime.getRuntime().addShutdownHook(Thread {
        try {
            Files.deleteIfExists(tempFilePath)
            println("\n临时文件 $TEMP_FILE_NAME 已清理。")
        } catch (e: Exception) {
            // ignore
        }
    })

    // 0. 下载文件
    downloadFileFromUrl(client, DOCUMENT_URL, tempFilePath)
    val testFile = tempFilePath.toFile()

    if (!testFile.exists()) {
        println("错误: 临时文件未成功创建。")
        exitProcess(1)
    }

    println("\n--- 1. 提交文件处理请求 ---")
    // 1a. 上传文件
    val boundary = "Boundary-${UUID.randomUUID()}"
    val uploadRequest = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:$port/api/storage/upload?uploaderId=$UPLOADER_ID"))
        .header("Content-Type", "multipart/form-data; boundary=$boundary")
        .POST(ofMimeMultipartData(mapOf("file" to testFile.toPath()), boundary)).build()

    val uploadResponse = sendRequest(client, uploadRequest)
    val storageObjectId = extractJsonValue(uploadResponse, "id")

    if (storageObjectId == null) {
        println("错误: 未能从上传响应中获取 ID。测试终止。")
        exitProcess(1)
    }
    println("文件 ${testFile.name} 上传成功, ID: $storageObjectId")

    // 1b. 触发嵌入
    val embedRequest = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:$port/api/storage/$storageObjectId/embed"))
        .POST(HttpRequest.BodyPublishers.noBody()).build()
    sendRequest(client, embedRequest)

    println("\n--- 2. 文件处理请求已提交，开始轮询检查状态 ---")
    val startTime = System.currentTimeMillis()
    var isCompleted = false

    while (!isCompleted) {
        if (System.currentTimeMillis() - startTime > POLLING_TIMEOUT_SECONDS * 1000) {
            println("\n错误: 轮询超时 (${POLLING_TIMEOUT_SECONDS}秒)。")
            break
        }

        val detailsRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:$port/api/storage/$storageObjectId")).GET().build()
        val statusResponse = sendRequest(client, detailsRequest, verbose = false)
        val status = extractJsonValue(statusResponse, "embeddingStatus")

        print("\r当前状态: $status ") // 使用 \r 实现单行刷新

        if (status == "COMPLETED") {
            isCompleted = true
            println("\n  - ID $storageObjectId: 状态已完成 (COMPLETED)")
        } else if (status == "FAILED") {
            isCompleted = true
            println("\n  - ID $storageObjectId: 状态为 FAILED。请检查服务日志。")
        }

        if (!isCompleted) Thread.sleep(5000) // 等待5秒再轮询
    }

    println("\n--- 3. 最终结果 ---")
    val status = extractJsonValue(sendRequest(client, HttpRequest.newBuilder().uri(URI.create("http://localhost:$port/api/storage/$storageObjectId")).GET().build(), false), "embeddingStatus")
    if (status == "COMPLETED") {
        println("测试成功。文件处理完毕。")
    } else {
        println("测试失败。最终状态为 $status。")
        exitProcess(1)
    }
}

/**
 * 发送HTTP请求并返回响应体。
 */
fun sendRequest(client: HttpClient, request: HttpRequest, verbose: Boolean = true): String {
    return try {
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (verbose) {
            println("  请求 ${request.method()} ${request.uri()} -> 状态码: ${response.statusCode()}")
            if (response.body().isNotBlank()) println("  响应体: ${response.body().take(200)}...")
        }
        if (response.statusCode() >= 400) {
            if (verbose) println("  请求失败，请检查服务日志。")
        }
        response.body()
    } catch (e: Exception) {
        if (verbose) println("  请求时发生错误: ${e.message}。")
        ""
    }
}


fun extractJsonValue(json: String, key: String): String? {
    return "\"$key\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(json)?.groupValues?.get(1)
}

/**
 * 创建一个 `multipart/form-data` 请求体。
 */
fun ofMimeMultipartData(data: Map<String, Any>, boundary: String): HttpRequest.BodyPublisher {
    val byteArrays = ArrayList<ByteArray>()
    val separator = "--$boundary\r\nContent-Disposition: form-data; name=".toByteArray()
    val charset = Charsets.UTF_8

    for ((key, value) in data) {
        byteArrays.add(separator)
        if (value is Path) {
            val mimeType = Files.probeContentType(value) ?: "application/octet-stream"
            byteArrays.add("\"$key\"; filename=\"${value.fileName}\"\r\nContent-Type: $mimeType\r\n\r\n".toByteArray(charset))
            byteArrays.add(Files.readAllBytes(value))
            byteArrays.add("\r\n".toByteArray(charset))
        } else {
            byteArrays.add("\"$key\"\r\n\r\n$value\r\n".toByteArray(charset))
        }
    }
    byteArrays.add("--$boundary--".toByteArray(charset))
    return HttpRequest.BodyPublishers.ofByteArrays(byteArrays)
}


main()