package edu.jimei.janus.infrastructure.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule()) // Support ZonedDateTime
        objectMapper.registerModule(KotlinModule.Builder().build()) // Support Kotlin data classes
        // 移除activateDefaultTyping，避免JSON序列化时添加类型信息导致解析错误
        // objectMapper.activateDefaultTyping(
        //     objectMapper.polymorphicTypeValidator,
        //     ObjectMapper.DefaultTyping.NON_FINAL
        // )
        return objectMapper
    }

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory, objectMapper: ObjectMapper): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory

        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(objectMapper, Any::class.java)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = jackson2JsonRedisSerializer
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = jackson2JsonRedisSerializer

        template.afterPropertiesSet()
        return template
    }
}
