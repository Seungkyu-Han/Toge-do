package vp.togedo.listener

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import vp.togedo.service.ImageService

@Component
class ImageEventListener(
    private val imageService: ImageService
) {

    @KafkaListener(topics = ["IMAGE_DELETE_TOPIC"], groupId = "seungkyu")
    fun deleteImageListener(fileName: String){
        imageService.deleteImage(fileName)
    }
}