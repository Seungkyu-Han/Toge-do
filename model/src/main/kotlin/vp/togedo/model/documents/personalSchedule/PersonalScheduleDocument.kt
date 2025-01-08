package vp.togedo.model.documents.personalSchedule

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import vp.togedo.model.exception.personalSchedule.*

@Document(collection = "personal_schedules")
data class PersonalScheduleDocument(
    @Id
    @JsonProperty("id")
    val id: ObjectId,

    @JsonProperty("fixedSchedules")
    val fixedSchedules: MutableList<PersonalScheduleElement> = mutableListOf(),

    @JsonProperty("flexibleSchedules")
    val flexibleSchedules: MutableList<PersonalScheduleElement> = mutableListOf()
){
    /**
     * 고정 스케줄에서 아이디를 기준으로 해당 일정을 삭제
     * @param personalScheduleElementId 삭제할 고정 스케줄의 object id
     * @return 고정 스케줄이 삭제된 personal schedule document
     * @throws NotFoundPersonaScheduleException 해당 스케줄이 존재하지 않음
     */
    fun deleteFixedPersonalScheduleElementById(personalScheduleElementId: ObjectId): PersonalScheduleDocument{
        if(!fixedSchedules.removeIf { it.id == personalScheduleElementId })
            throw NotFoundPersonaScheduleException()
        return this
    }

    /**
     * 유동 스케줄에서 아이디를 기준으로 해당 일정을 삭제
     * @param personalScheduleElementId 삭제할 유동 스케줄의 object id
     * @return 유동 스케줄이 삭제된 personal schedule document
     * @throws NotFoundPersonaScheduleException 해당 스케줄이 존재하지 않음
     */
    fun deleteFlexiblePersonalScheduleElementById(personalScheduleElementId: ObjectId): PersonalScheduleDocument{
        if(!flexibleSchedules.removeIf { it.id == personalScheduleElementId })
            throw NotFoundPersonaScheduleException()
        return this
    }

    /**
     * 고정 스케줄에서 해당 스케줄을 변경
     * @param personalScheduleElement 변경하고 싶은 고정 일정
     * @return 변경된 personal schedule document
     * @throws NotFoundPersonaScheduleException 해당 스케줄이 존재하지 않음
     * @throws ConflictPersonalScheduleException 스케줄의 시간이 충돌
     * @throws PersonalScheduleTimeIsNotRangeException 유효한 시간 범위가 아님
     * @throws PersonalScheduleEndTimeBeforeStartTimeException 종료 시간이 시작 시간보다 앞에 있음
     */
    fun modifyFixedPersonalScheduleElement(personalScheduleElement: PersonalScheduleElement): PersonalScheduleDocument{

        val index = findFixedPersonalScheduleIndexById(
            fixedPersonalScheduleId = personalScheduleElement.id)

        val originalFixedPersonalScheduleElement = fixedSchedules[index]

        fixedSchedules.removeAt(index)

        return try{
            addFixedPersonalScheduleElement(personalScheduleElement)
        }catch(e: PersonalScheduleException){
            fixedSchedules.add(index, originalFixedPersonalScheduleElement)
            throw e
        }
    }

    /**
     * 유동 스케줄에서 해당 스케줄을 변경
     * @param personalScheduleElement 변경하고 싶은 유동 일정
     * @return 변경된 personal schedule document
     * @throws NotFoundPersonaScheduleException 해당 스케줄이 존재하지 않음
     * @throws ConflictPersonalScheduleException 스케줄의 시간이 충돌
     * @throws PersonalScheduleTimeIsNotRangeException 유효한 시간 범위가 아님
     * @throws PersonalScheduleEndTimeBeforeStartTimeException 종료 시간이 시작 시간보다 앞에 있음
     */
    fun modifyFlexiblePersonalScheduleElement(personalScheduleElement: PersonalScheduleElement): PersonalScheduleDocument{

        val index = findFlexiblePersonalScheduleIndexById(
            flexiblePersonalScheduleId = personalScheduleElement.id)

        val originalFlexiblePersonalScheduleElement = flexibleSchedules[index]

        flexibleSchedules.removeAt(index)

        return try{
            addFlexiblePersonalScheduleElement(personalScheduleElement)
        }catch(e: PersonalScheduleException){
            flexibleSchedules.add(index, originalFlexiblePersonalScheduleElement)
            throw e
        }
    }

    /**
     * 고정 스케줄에 새로운 고정 일정을 추가
     * @param personalScheduleElement 추가하고 싶은 고정 일정
     * @return 추가된 personal schedule document
     * @throws ConflictPersonalScheduleException 스케줄의 시간이 충돌
     * @throws PersonalScheduleTimeIsNotRangeException 유효한 시간 범위가 아님
     * @throws PersonalScheduleEndTimeBeforeStartTimeException 종료 시간이 시작 시간보다 앞에 있음
     */
    fun addFixedPersonalScheduleElement(personalScheduleElement: PersonalScheduleElement): PersonalScheduleDocument {
        personalScheduleElement.isValidTimeForFixedSchedule()

        val sortedIndex = getSortedIndex(
            personalScheduleElement = personalScheduleElement,
            personalScheduleEnum = PersonalScheduleEnum.FIXED_PERSONAL_SCHEDULE
        )

        fixedSchedules.add(sortedIndex, personalScheduleElement)
        return this
    }

    /**
     * 유동 스케줄에 새로운 유동 일정을 추가
     * @param personalScheduleElement 추가하고 싶은 유동 일정
     * @return 추가된 personal schedule document
     * @throws ConflictPersonalScheduleException 스케줄의 시간이 충돌
     * @throws PersonalScheduleTimeIsNotRangeException 유효한 시간 범위가 아님
     * @throws PersonalScheduleEndTimeBeforeStartTimeException 종료 시간이 시작 시간보다 앞에 있음
     */
    fun addFlexiblePersonalScheduleElement(personalScheduleElement: PersonalScheduleElement): PersonalScheduleDocument {

        personalScheduleElement.isValidTimeForFlexibleSchedule()

        val sortedIndex = getSortedIndex(
            personalScheduleElement = personalScheduleElement,
            personalScheduleEnum = PersonalScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE)

        flexibleSchedules.add(sortedIndex, personalScheduleElement)

        return this
    }

    /**
     * 정렬된 스케줄 배열에 해당 스케줄의 인덱스를 탐색
     * @param personalScheduleElement 탐색하고 싶은 스케줄 요소
     * @param personalScheduleEnum 탐색하고 싶은 스케줄 배열
     * @return 삽입될 스케줄의 인덱스
     * @throws ConflictPersonalScheduleException 스케줄의 시간이 충돌
     */
    fun getSortedIndex(personalScheduleElement: PersonalScheduleElement, personalScheduleEnum: PersonalScheduleEnum): Int {
        val schedules:MutableList<PersonalScheduleElement> = when(personalScheduleEnum){
            PersonalScheduleEnum.FIXED_PERSONAL_SCHEDULE -> fixedSchedules
            PersonalScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE -> flexibleSchedules
        }

        val index = schedules.binarySearch(0){
            it.startTime.compareTo(personalScheduleElement.startTime)
        }

        if(index >= 0)
            throw ConflictPersonalScheduleException()

        val sortedIndex = (index + 1) * -1

        //앞의 종료 시간과 해당 스케줄의 시작 시간을 비교
        if(sortedIndex > 0)
            if(schedules[sortedIndex - 1].endTime >= personalScheduleElement.startTime)
                throw ConflictPersonalScheduleException()

        //뒤의 시작 시간과 해당 스케줄의 종료 시간을 비교
        if(sortedIndex < schedules.size)
            if(schedules[sortedIndex].startTime <= personalScheduleElement.endTime)
                throw ConflictPersonalScheduleException()

        return sortedIndex
    }

    /**
     * 해당 개인 고정 일정의 인덱스를 아이디를 기준으로 탐색
     * @param fixedPersonalScheduleId 개인 고정 일정의 object id
     * @return 해당 개인 고정 일정의 인덱스
     * @throws NotFoundPersonaScheduleException 해당 고정 일정을 찾을 수 없음
     */
    fun findFixedPersonalScheduleIndexById(fixedPersonalScheduleId: ObjectId): Int{
        val index = fixedSchedules.indexOfFirst{it.id == fixedPersonalScheduleId}
        if(index < 0)
            throw NotFoundPersonaScheduleException()
        return index
    }

    /**
     * 해당 개인 유동 일정의 인덱스를 아이디를 기준으로 탐색
     * @param flexiblePersonalScheduleId 개인 유동 일정의 object id
     * @return 해당 개인 유동 일정의 인덱스
     * @throws NotFoundPersonaScheduleException 해당 유동 일정을 찾을 수 없음
     */
    fun findFlexiblePersonalScheduleIndexById(flexiblePersonalScheduleId: ObjectId): Int{
        val index = flexibleSchedules.indexOfFirst{it.id == flexiblePersonalScheduleId}
        if(index < 0)
            throw NotFoundPersonaScheduleException()
        return index
    }
}
