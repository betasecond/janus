package edu.jimei.janus.application.service

import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service

@Service
@ConditionalOnBean(VectorStore::class)
class EmbeddingService(private val vectorStore: VectorStore) {

    fun embedAndStore(message: String) {
        val document = Document(message)
        vectorStore.add(listOf(document))
    }




} 