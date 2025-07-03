package edu.jimei.janus.infrastructure.oss

import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing
@EnableConfigurationProperties(OssProperties::class)
class OssClientConfig {

    @Bean
    fun ossClient(properties: OssProperties): OSS {
        return OSSClientBuilder().build(
            properties.endpoint,
            properties.accessKey,
            properties.secretKey
        )
    }
} 