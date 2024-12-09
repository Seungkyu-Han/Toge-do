package vp.togedo.service.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import vp.togedo.UserRepository
import vp.togedo.document.UserDocument
import vp.togedo.service.FriendService

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
}