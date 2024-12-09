package vp.togedo.service.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.UserRepository
import vp.togedo.document.UserDocument
import vp.togedo.service.FriendService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.UserException

@Service
class FriendServiceImpl(
    private val userRepository: UserRepository
): FriendService {

    /**
     * 친구 목록에 있는 ID를 이용해 사용자를 검색해오는 메서드
     * @param friends 친구의 id 리스트
     * @return 유저 정보 Flux
     */
    override fun getUserByFriends(friends: Set<ObjectId>): Flux<UserDocument> {
        return userRepository.findAllById(friends)
    }

    /**
     * 해당 사용자에게 친구 요청을 보내는 메서드
     * @param userId 요청을 보내는 사용자의 id
     * @param friendId 친구 요청을 받는 사용자의 id
     * @return 친구 요청을 받은 사용자의 user document
     */
    override fun requestFriend(userId: ObjectId, friendId: ObjectId): Mono<UserDocument> {
        return userRepository.findById(friendId)
            .flatMap{
                it.friendRequests.add(userId)
                userRepository.save(it)
            }.switchIfEmpty(
                Mono.error(UserException(ErrorCode.USER_NOT_FOUND))
            )
    }
}