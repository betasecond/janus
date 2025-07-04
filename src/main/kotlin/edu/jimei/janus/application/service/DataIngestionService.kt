package edu.jimei.janus.application.service

import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.core.io.Resource
import org.springframework.ai.reader.pdf.PagePdfDocumentReader // 举例

@Service
class DataIngestionService(
    // 只依赖 VectorStore，不需要关心具体的嵌入模型是哪个
    private val vectorStore: VectorStore
) {
    fun ingestPdf(resource: Resource) {
        // 1. 读取和切分（处理文本）
        val documentReader = PagePdfDocumentReader(resource)
        val rawDocuments = documentReader.get()
        val textSplitter = TokenTextSplitter()
        val chunkedDocuments = textSplitter.apply(rawDocuments)

        // 2. 提交给 VectorStore
        // VectorStore 会在内部负责调用嵌入模型并存储
        vectorStore.add(chunkedDocuments)
        println("Successfully ingested and embedded ${chunkedDocuments.size} chunks from ${resource.filename}.")
    }
}