package edu.jimei.janus.infrastructure.oss

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oss")
data class OssProperties(
    val endpoint: String,
    val accessKey: String,
    val secretKey: String,
    val bucketName: String
) 