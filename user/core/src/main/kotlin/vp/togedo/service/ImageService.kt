package vp.togedo.service

import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult

interface ImageService {

    fun saveImage(filePart: FilePart): Mono<String>

    fun deleteImage(fileName: String)

    fun publishDeleteEvent(fileName: String): Mono<SenderResult<Void>>
}