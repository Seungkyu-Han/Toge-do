package vp.togedo.service.impl

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import vp.togedo.service.ImageService
import java.nio.file.Files
import java.nio.file.Path
import java.util.*


@Service
class ImageServiceImpl(
    @Value("\${IMAGE.SERVER_NAME}")
    private val imageServerName: String,
    @Value("\${IMAGE.PATH}")
    private val imagePath: String,
    private val reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>
): ImageService {

    private val deleteImageEventTopic = "IMAGE_DELETE_IMAGE_TOPIC"

    override fun saveImage(filePart: FilePart): Mono<String> {
        val fileName =
            "${UUID.randomUUID()}.${filePart.filename().split(".").last()}"

        return filePart
            .transferTo(Path.of("$imagePath/$fileName"))
            .then(Mono.fromCallable { "$imageServerName/image/$fileName" })

    }

    override fun deleteImage(fileName: String) {
        Files.deleteIfExists(Path.of("$imagePath/$fileName"))
    }

    override fun publishDeleteEvent(fileName: String): Mono<SenderResult<Void>> =
        reactiveKafkaProducerTemplate.send(
            deleteImageEventTopic, fileName
        )

}