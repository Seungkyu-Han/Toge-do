package vp.togedo.service

import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono

interface S3Service {

    fun postImage(fileName: String, image: FilePart): Mono<String>
}