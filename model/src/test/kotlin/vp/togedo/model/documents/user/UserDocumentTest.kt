package vp.togedo.model.documents.user

import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import vp.togedo.model.exception.user.AlreadyFriendException
import vp.togedo.model.exception.user.AlreadyRequestFriendException
import vp.togedo.model.exception.user.CantRequestToMeException
import java.util.*

class UserDocumentTest{

    private lateinit var user: UserDocument

    @BeforeEach
    fun setUp() {
        user = UserDocument(
            oauth = Oauth(
                kakaoId = 0L,
                googleId = UUID.randomUUID().toString(),
            ),
            name = UUID.randomUUID().toString(),
            profileImageUrl = UUID.randomUUID().toString(),
            deviceToken = UUID.randomUUID().toString(),
        )
    }

    @Nested
    inner class AddFriendRequest{
        private val friendId: ObjectId = ObjectId.get()

        @Test
        @DisplayName("해당 사용자에게 정상적으로 친구 요청")
        fun addFriendRequestReturnSuccess(){
            //given

            //when
            user.addFriendRequest(userId = friendId)

            //then
            Assertions.assertTrue(user.friendRequests.contains(friendId))
        }

        @Test
        @DisplayName("친구 요청을 보낸 사용자가 본인인 경우")
        fun addFriendRequestToMeReturnException(){
            //given

            //when && then
            Assertions.assertThrows(CantRequestToMeException::class.java){user.addFriendRequest(userId = user.id)}
            Assertions.assertFalse(user.friendRequests.contains(user.id))
        }

        @Test
        @DisplayName("친구 요청을 보낸 사용자가 이미 친구인 경우")
        fun addFriendRequestToAlreadyFriendReturnException(){
            //given
            user.friends.add(element = friendId)

            //when && then
            Assertions.assertThrows(AlreadyFriendException::class.java){user.addFriendRequest(userId = friendId)}
            Assertions.assertFalse(user.friendRequests.contains(friendId))
        }

        @Test
        @DisplayName("친구 요청을 보낸 사용자가 이미 친구요청을 보낸 경우")
        fun addFriendRequestToAlreadyFriendRequestReturnException(){
            //given
            user.friendRequests.add(element = friendId)

            //when && then
            Assertions.assertThrows(AlreadyRequestFriendException::class.java){user.addFriendRequest(userId = friendId)}
            Assertions.assertTrue(user.friendRequests.contains(friendId))
        }

    }

}