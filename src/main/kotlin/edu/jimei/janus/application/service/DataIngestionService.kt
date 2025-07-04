package edu.jimei.janus.application.service

import edu.jimei.janus.domain.storage.StorageObject
import org.springframework.ai.reader.pdf.PagePdfDocumentReader
import org.springframework.stereotype.Service
import org.springframework.ai.reader.markdown.MarkdownDocumentReader
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.core.io.Resource
import java.io.File

@Service
class DataIngestionService(
    // 只依赖 VectorStore，不需要关心具体的嵌入模型是哪个
    private val vectorStore: VectorStore
) {
    fun ingest(resource: Resource, storageObject: StorageObject) {
        // 1. 根据文件类型选择合适的 DocumentReader
        val extension = File(storageObject.originalFilename).extension.lowercase()
        val documentReader = when (extension) {
            "pdf" -> PagePdfDocumentReader(resource)
            "md" -> MarkdownDocumentReader(resource, MarkdownDocumentReaderConfig.defaultConfig())
            else -> throw IllegalArgumentException("Unsupported file type: .$extension")
        }

        val rawDocuments = documentReader.get()
        val textSplitter = TokenTextSplitter()
        val chunkedDocuments = textSplitter.apply(rawDocuments)

        // 2. 提交给 VectorStore
        vectorStore.add(chunkedDocuments)
        println("Successfully ingested and embedded ${chunkedDocuments.size} chunks from ${resource.filename}.")
    }
}