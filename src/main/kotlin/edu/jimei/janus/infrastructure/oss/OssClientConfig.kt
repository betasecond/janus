package edu.jimei.janus.infrastructure.oss


import com.aliyun.oss.*
import com.aliyun.oss.common.auth.*
import com.aliyun.oss.common.comm.Protocol
import com.aliyun.oss.common.comm.SignVersion
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

        // 创建 ClientBuilderConfiguration 实例，用于配置 OSS 客户端参数
        val clientBuilderConfiguration = ClientBuilderConfiguration()

        // 设置签名算法版本为 V4
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4)

        // 设置使用 HTTPS 协议访问 OSS，保证传输安全性
        clientBuilderConfiguration.setProtocol(Protocol.HTTPS)

        val newEnvironmentVariableCredentialsProvider =
            CredentialsProviderFactory
                .newEnvironmentVariableCredentialsProvider()

        return OSSClientBuilder
            .create()
            .endpoint(properties.endpoint)
            .credentialsProvider(newEnvironmentVariableCredentialsProvider)
            .clientConfiguration(clientBuilderConfiguration)
            // TODO:后面改成配置文件读取
            .region("cn-fuzhou")
            .build()

    }
} 