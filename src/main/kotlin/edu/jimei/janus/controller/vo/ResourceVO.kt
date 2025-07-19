package edu.jimei.janus.controller.vo

import edu.jimei.janus.domain.storage.StorageObject
import java.time.LocalDate
import java.util.*

// Corresponds to mockResources in the frontend
data class ResourceVO(
    val id: String, // Frontend uses string 'res001', but backend has UUID. We will convert.
    val title: String,
    val type: String, // e.g., 'document', 'assessment'. We will derive this from contentType or use a default.
    val subject: String, // This will be mocked for now.
    val uploader: String,
    val uploadDate: LocalDate, // Frontend has 'YYYY-MM-DD', backend has LocalDateTime. We'll convert.
    val thumbnail: String // This will be mocked for now.
)

fun StorageObject.toResourceVo(): ResourceVO {
    // A simple mapping from file extension to resource type.
    val type = when (this.contentType) {
        "application/pdf" -> "assessment"
        "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> "document"
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "document"
        "video/mp4" -> "video"
        else -> "document"
    }

    // Placeholder for thumbnail logic
    val thumbnail = when (type) {
        "video" -> "https://images.unsplash.com/photo-1579468118864-1b9ea3c0db4a?w=400&h=200&fit=crop"
        "assessment" -> "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=400&h=200&fit=crop"
        else -> "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=400&h=200&fit=crop"
    }

    return ResourceVO(
        id = "res-${this.id.toString().substring(0, 4)}", // Mocking the 'res001' style id
        title = this.originalFilename,
        type = type,
        subject = "General", // Mocked value
        uploader = this.uploader?.displayName ?: "Unknown Uploader",
        uploadDate = this.createdAt!!.toLocalDate(),
        thumbnail = thumbnail // Mocked value
    )
} 