package vp.togedo.service.impl

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.service.ImageService
import java.nio.file.Files
import java.nio.file.Path
import java.util.*


@Service
class ImageServiceImpl(
    @Value("\${SERVER.NAME}")
    private val serverAddress: String,
    @Value("\${IMAGE.PATH}")
    private val imagePath: String
): ImageService {

    override fun saveImage(filePart: FilePart): Mono<String> {
        val fileName =
            "${UUID.randomUUID()}.${filePart.filename().split(".").last()}"

        return filePart
            .transferTo(Path.of("$imagePath/$fileName"))
            .then(Mono.fromCallable { "$serverAddress/image/$fileName" })

    }

    override fun deleteImage(fileName: String) {
        Files.deleteIfExists(Path.of("$imagePath/$fileName"))
    }
}