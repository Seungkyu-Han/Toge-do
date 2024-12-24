package vp.togedo.service.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.document.GroupDocument
import vp.togedo.repository.GroupRepository
import vp.togedo.repository.JoinedGroupRepository
import vp.togedo.service.GroupService

@Service
class GroupServiceImpl(
    private val groupRepository: GroupRepository,
    private val joinedGroupRepository: JoinedGroupRepository
): GroupService {

    override fun createGroup(name: String, members: List<ObjectId>): Mono<GroupDocument> {
        val group = GroupDocument(
            name = name,
            members = members.toMutableList()
        )
        return groupRepository.save(group)
    }
}