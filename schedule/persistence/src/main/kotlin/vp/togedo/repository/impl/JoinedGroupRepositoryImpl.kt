package vp.togedo.repository.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import vp.togedo.model.documents.joinedGroup.JoinedGroupDocument
import vp.togedo.redis.repository.JoinedGroupRedisRepository
import vp.togedo.repository.JoinedGroupRepository
import vp.togedo.repository.mongo.JoinedGroupMongoRepository

@Repository
class JoinedGroupRepositoryImpl(
    private val joinedGroupMongoRepository: JoinedGroupMongoRepository,
    private val joinedGroupRedisRepository: JoinedGroupRedisRepository
): JoinedGroupRepository {


    override fun findById(id: ObjectId): Mono<JoinedGroupDocument> {
        return joinedGroupRedisRepository.findById(id)
            .switchIfEmpty(
                Mono.defer{joinedGroupMongoRepository.findById(id)}
            )
            .publishOn(Schedulers.boundedElastic())
            .doOnSuccess {
                if (it != null)
                    joinedGroupRedisRepository.save(it).subscribe()
            }
    }

    override fun save(joinedGroupDocument: JoinedGroupDocument): Mono<JoinedGroupDocument> {
        return joinedGroupMongoRepository.save(joinedGroupDocument)
            .publishOn(Schedulers.boundedElastic())
            .doOnSuccess {
                joinedGroupRedisRepository.save(it).subscribe()
            }
    }
}