package vp.togedo.model.documents.user

import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import vp.togedo.model.exception.user.*
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

    @Nested
    inner class AddFriend{
        private val friendId: ObjectId = ObjectId.get()

        @Test
        @DisplayName("해당 유저를 친구로 추가")
        fun addFriendReturnSuccess(){
            //given

            //when
            user.addFriend(userId = friendId)

            //then
            Assertions.assertTrue(user.friends.contains(friendId))
        }

        @Test
        @DisplayName("본인을 친구로 추가")
        fun addFriendToMeReturnException(){
            //given

            //when && then
            Assertions.assertThrows(CantRequestToMeException::class.java){user.addFriend(userId = user.id)}
            Assertions.assertFalse(user.friends.contains(user.id))
        }

        @Test
        @DisplayName("이미 친구를 친구로 추가")
        fun addFriendAlreadyFriendReturnException(){
            //given
            user.friends.add(element = friendId)

            //when && then
            Assertions.assertThrows(AlreadyFriendException::class.java){user.addFriend(userId = friendId)}
            Assertions.assertTrue(user.friends.contains(friendId))
        }
    }

    @Nested
    inner class ApproveFriendRequest{
        private val friendId:ObjectId = ObjectId.get()

        @Test
        @DisplayName("친구 요청을 보낸 사용자의 친구 요청을 승인")
        fun approveFriendRequestToSendFriendRequestReturnSuccess(){
            //given
            user.friendRequests.add(element = friendId)

            //when && then
            user.approveFriendRequest(userId = friendId)

            //then
            Assertions.assertTrue(user.friends.contains(friendId))
            Assertions.assertFalse(user.friendRequests.contains(friendId))
        }

        @Test
        @DisplayName("승인 하는 유저가 본인인 경우")
        fun approveFriendRequestToMeReturnException(){
            //given

            //when && then
            Assertions.assertThrows(CantRequestToMeException::class.java){user.approveFriendRequest(userId = user.id)}

            Assertions.assertFalse(user.friends.contains(user.id))
        }

        @Test
        @DisplayName("승인하는 유저가 이미 친구인 경우")
        fun approveFriendRequestToAlreadyFriendReturnException(){
            //given
            user.friends.add(element = friendId)

            //when && then
            Assertions.assertThrows(AlreadyFriendException::class.java){user.approveFriendRequest(userId = friendId)}

            Assertions.assertTrue(user.friends.contains(friendId))
        }

        @Test
        @DisplayName("승인하는 유저가 친구 요청을 보내지 않은 경우")
        fun approveFriendRequestToNotSendRequestReturnException(){
            //given

            //when && then
            Assertions.assertThrows(FriendRequestNotSentException::class.java){user.approveFriendRequest(userId = friendId)}

            Assertions.assertFalse(user.friends.contains(friendId))
        }
    }

    @Nested
    inner class RemoveFriend{
        private val friendId: ObjectId = ObjectId.get()

        @Test
        @DisplayName("친구 목록에서 친구를 삭제")
        fun removeFriendFromFriendListReturnSuccess(){
            //given
            user.friends.add(element = friendId)

            //when
            user.removeFriend(userId = friendId)

            //then
            Assertions.assertFalse(user.friends.contains(friendId))
        }

        @Test
        @DisplayName("본인을 친구 목록에서 삭제")
        fun removeFriendToMeReturnException(){
            //given

            //when
            Assertions.assertThrows(CantRequestToMeException::class.java){user.removeFriend(userId = user.id)}

            //then
            Assertions.assertFalse(user.friends.contains(user.id))
        }

        @Test
        @DisplayName("친구 아닌 유저를 친구 목록에서 삭제")
        fun removeFriendNotFriendReturnException(){
            //given

            //when
            Assertions.assertThrows(NotFriendException::class.java){user.removeFriend(userId = friendId)}

            //then
            Assertions.assertFalse(user.friends.contains(friendId))
        }
    }

    @Nested
    inner class RemoveFriendRequest{
        private val friendId: ObjectId = ObjectId.get()

        @Test
        @DisplayName("친구 요청을 보낸 유저의 친구 요청을 삭제")
        fun removeFriendRequestReturnSuccess(){
            //given
            user.friendRequests.add(element = friendId)

            //when
            user.removeFriendRequest(userId = friendId)

            //then
            Assertions.assertFalse(user.friendRequests.contains(friendId))
        }

        @Test
        @DisplayName("본인을 친구 요청 목록에서 삭제")
        fun removeFriendRequestToMeReturnException(){
            //given

            //when
            Assertions.assertThrows(CantRequestToMeException::class.java){user.removeFriendRequest(userId = user.id)}

            //then
            Assertions.assertFalse(user.friendRequests.contains(user.id))
        }

        @Test
        @DisplayName("친구 요청을 보내지 않은 유저를 삭제")
        fun removeFriendRequestToNotSendRequestReturnException(){
            //given

            //when
            Assertions.assertThrows(FriendRequestNotSentException::class.java){user.removeFriendRequest(userId = friendId)}

            //then
            Assertions.assertFalse(user.friendRequests.contains(friendId))
        }
    }

    @Nested
    inner class UpdateUserInfo{

        @Test
        @DisplayName("사용자의 이름과 이메일을 동시에 수정")
        fun updateUserNameAndEmailReturnSuccess(){
            //given
            val newName = UUID.randomUUID().toString()
            val newEmail = UUID.randomUUID().toString()

            //when
            user.updateUserInfo(name = newName, email = newEmail)

            //then
            Assertions.assertEquals(newName, user.name)
            Assertions.assertEquals(newEmail, user.email)
        }

        @Test
        @DisplayName("사용자의 이름만 수정")
        fun updateUserNameReturnSuccess(){
            //given
            val newName = UUID.randomUUID().toString()

            //when
            user.updateUserInfo(name = newName, email = user.email)

            //then
            Assertions.assertEquals(newName, user.name)
        }

        @Test
        @DisplayName("사용자의 이메일만 수정")
        fun updateUserEmailReturnSuccess(){
            //given
            val newEmail = UUID.randomUUID().toString()

            //when
            user.updateUserInfo(name = user.name, email = newEmail)

            //then
            Assertions.assertEquals(newEmail, user.email)
        }
    }

    @Nested
    inner class UpdateUserProfileImageUrl{

        @Test
        @DisplayName("해당 유저의 프로필 이미지를 새로운 이미지로 변경")
        fun updateUserProfileImageUrlToNewImageUrlReturnSuccess(){
            //given
            val newProfileImageUrl = UUID.randomUUID().toString()

            //when
            user.updateUserProfileImageUrl(newProfileImageUrl)

            //then
            Assertions.assertEquals(newProfileImageUrl, user.profileImageUrl)
        }

        @Test
        @DisplayName("해당 유저의 프로필 이미지를 삭제")
        fun updateUserProfileImageToNullReturnSuccess(){
            //given

            //when
            user.updateUserProfileImageUrl(null)

            //then
            Assertions.assertNull(user.profileImageUrl)
        }
    }

}