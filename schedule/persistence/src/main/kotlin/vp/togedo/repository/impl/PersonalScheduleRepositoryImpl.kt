package vp.togedo.repository.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import vp.togedo.model.documents.personalSchedule.PersonalScheduleDocument
import vp.togedo.redis.repository.PersonalScheduleRedisRepository
import vp.togedo.repository.PersonalScheduleRepository
import vp.togedo.repository.mongo.PersonalScheduleMongoRepository

@Repository
class PersonalScheduleRepositoryImpl(
    private val personalScheduleMongoRepository: PersonalScheduleMongoRepository,
    private val personalScheduleRedisRepository: PersonalScheduleRedisRepository
): PersonalScheduleRepository {


    override fun findByUserId(userId: ObjectId): Mono<PersonalScheduleDocument> {
        return personalScheduleRedisRepository.findById(userId)
            .switchIfEmpty(
                Mono.defer{personalScheduleMongoRepository.findById(userId) }
            )
            .publishOn(Schedulers.boundedElastic())
            .doOnSuccess {
                if (it != null)
                    personalScheduleRedisRepository.save(it).subscribe()
            }
    }

    override fun save(personalScheduleDocument: PersonalScheduleDocument): Mono<PersonalScheduleDocument> {
        return personalScheduleMongoRepository.save(personalScheduleDocument)
            .publishOn(Schedulers.boundedElastic())
            .doOnSuccess {
                personalScheduleRedisRepository.save(it).subscribe()
            }
    }
}