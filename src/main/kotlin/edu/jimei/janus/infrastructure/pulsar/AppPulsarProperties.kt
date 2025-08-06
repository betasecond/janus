package edu.jimei.janus.infrastructure.pulsar

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.pulsar")
data class AppPulsarProperties(
    val topics: Topics
) {
    data class Topics(
        val fileProcessing: String,
        val trackingEvents: String
    )
}
