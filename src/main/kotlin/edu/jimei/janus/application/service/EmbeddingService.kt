package edu.jimei.janus.application.service

import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.ai.vectorstore.redis.RedisVectorStore
import org.springframework.stereotype.Service

@Service
class EmbeddingService(private val vectorStore: RedisVectorStore) {

    fun embedAndStore(message: String) {
        val document = Document(message)
        vectorStore.add(listOf(document))
    }

    fun findSimilar(message: String, topK: Int = 4): List<Document>? {
        return vectorStore.similaritySearch(message)
    }
} 