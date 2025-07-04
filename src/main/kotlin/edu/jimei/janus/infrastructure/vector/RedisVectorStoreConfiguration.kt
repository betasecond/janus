package edu.jimei.janus.infrastructure.vector

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.TokenCountBatchingStrategy
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.ai.vectorstore.redis.RedisVectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.JedisPooled


@Configuration
class RedisVectorStoreConfiguration {
    @Bean
    fun vectorStore(jedisPooled: JedisPooled, embeddingModel: EmbeddingModel): VectorStore {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
            .indexName("custom-index") // Optional: defaults to "spring-ai-index"
            .prefix("custom-prefix") // Optional: defaults to "embedding:"
            .metadataFields( // Optional: define metadata fields for filtering
                RedisVectorStore.MetadataField.tag("country"),
                RedisVectorStore.MetadataField.numeric("year")
            )
            .initializeSchema(true) // Optional: defaults to false
            .batchingStrategy(TokenCountBatchingStrategy()) // Optional: defaults to TokenCountBatchingStrategy
            .build()
    }

    @Bean
    fun jedisPooled(
        @Value("\${spring.data.redis.host}") redisHost: String,
        @Value("\${spring.data.redis.port}") redisPort: Int,
        @Value("\${spring.data.redis.password}") redisPassword: String
    ): JedisPooled {
        return JedisPooled(redisHost, redisPort, "default", redisPassword)
    }
}