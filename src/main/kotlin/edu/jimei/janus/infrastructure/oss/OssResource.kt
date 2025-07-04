package edu.jimei.janus.infrastructure.oss

import com.aliyun.oss.model.OSSObject
import org.springframework.core.io.AbstractResource
import java.io.FilterInputStream
import java.io.InputStream

/**
 * A custom Spring Resource that wraps an Alibaba Cloud OSSObject.
 * It ensures that when the input stream is closed, the underlying OSSObject is also closed,
 * preventing resource leaks.
 */
class OssResource(private val ossObject: OSSObject) : AbstractResource() {

    override fun getDescription(): String {
        return "OSS Resource [${ossObject.bucketName}/${ossObject.key}]"
    }

    override fun getInputStream(): InputStream {
        // Return a proxy stream that closes the OSSObject when it is closed.
        return object : FilterInputStream(ossObject.objectContent) {
            @Override
            override fun close() {
                try {
                    super.close()
                } finally {
                    ossObject.close()
                }
            }
        }
    }
} 