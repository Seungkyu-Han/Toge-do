package vp.togedo.service

import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono

interface ImageService {

    fun saveImage(filePart: FilePart): Mono<String>

    fun deleteImage(fileName: String)

    fun publishDeleteEvent(fileName: String): Mono<Void>
}