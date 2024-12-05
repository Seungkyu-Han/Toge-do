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
    @Value("\${IMAGE.PATH}")
    private val imagePath: String,
): ImageService {

    override fun saveImage(filePart: FilePart): Mono<String> {
        println(filePart.filename())
        val path = Path.of("$imagePath/${UUID.randomUUID()}.${filePart.filename().split(".").last()}")

        return filePart
            .transferTo(path)
            .then(Mono.fromCallable { path.toString() })
    }

    override fun deleteImage(filePath: String) {
        Files.deleteIfExists(Path.of("$imagePath/${UUID.randomUUID()}"))
    }
}