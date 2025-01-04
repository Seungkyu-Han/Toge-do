package vp.togedo.service.impl

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.service.S3Service

@Service
class S3ServiceImpl(
    private val amazonS3Client: AmazonS3Client,
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String
): S3Service {

    override fun postImage(fileName: String, image: FilePart): Mono<String> {
        val fileExtension = image.filename().split(".").last()

        val fileUrl = "https://${bucket}.s3-ap-northeast-2.amazonaws.com/toge-do/${fileName}.$fileExtension"

        val objectMetadata = ObjectMetadata()
        objectMetadata.contentType = image.headers().contentType?.type
        objectMetadata.contentLength = image.headers().contentLength

        return DataBufferUtils.join(image.content())
            .map(DataBuffer::asInputStream)
            .doOnNext{
                amazonS3Client.putObject(bucket, "toge-do/$fileName.${fileExtension}", it, objectMetadata)
            }.map{
                fileUrl
            }


    }

    override fun deleteImage(fileName: String) {
        amazonS3Client.deleteObject(bucket, "toge-do/${fileName}")
    }
}