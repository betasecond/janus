package edu.jimei.janus.controller.vo

import org.springframework.ai.embedding.EmbeddingResponse

data class EmbeddingVO(
    val embedding: EmbeddingResponse
) 