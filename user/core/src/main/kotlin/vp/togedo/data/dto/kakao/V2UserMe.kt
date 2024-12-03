package vp.togedo.data.dto.kakao

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class V2UserMe(
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("has_signed_up")
    val hasSignedUp: Boolean?,

    @JsonProperty("connected_ad")
    val connectedAd: LocalDateTime?,

    @JsonProperty("synched_at")
    val synchedAt: LocalDateTime?,

    @JsonProperty("properties")
    val properties: Any?,

    @JsonProperty("kakao_account")
    val kakaoAccount: KakaoAccount?,

    @JsonProperty("for_partner")
    val forPartner: Partner?,
)

data class KakaoAccount(
    @JsonProperty("profile_needs_agreement")
    val profileNeedsAgreement: Boolean?,

    @JsonProperty("profile_nickname_needs_agreement")
    val profileNicknameNeedsAgreement: Boolean?,

    @JsonProperty("profile_image_needs_agreement")
    val profileImageNeedsAgreement: Boolean?,

    @JsonProperty("profile")
    val profile: Profile?,

    @JsonProperty("name_needs_agreement")
    val nameNeedsAgreement: Boolean?,

    @JsonProperty("name")
    val name: String?,

    @JsonProperty("email_needs_agreement")
    val emailNeedsAgreement: Boolean?,

    @JsonProperty("is_email_valid")
    val isEmailValid: Boolean?,

    @JsonProperty("is_email_verified")
    val isEmailVerified: Boolean?,

    @JsonProperty("email")
    val email: String?,

    @JsonProperty("age_range_needs_agreement")
    val ageRangeNeedsAgreement: Boolean?,

    @JsonProperty("age_range")
    val ageRange: String?,

    @JsonProperty("birthyear_needs_agreement")
    val birthyearNeedsAgreement: Boolean?,

    @JsonProperty("birthyear")
    val birthyear: String?,

    @JsonProperty("birthday_needs_agreement")
    val birthdayNeedsAgreement: Boolean?,

    @JsonProperty("birthday")
    val birthday: String?,

    @JsonProperty("birthday_type")
    val birthdayType: String?,

    @JsonProperty("gender_needs_agreement")
    val genderNeedsAgreement: Boolean?,

    @JsonProperty("gender")
    val gender: String?,

    @JsonProperty("phone_number_needs_agreement")
    val phoneNumberNeedsAgreement: Boolean?,

    @JsonProperty("phone_number")
    val phoneNumber: String?,

    @JsonProperty("ci_needs_agreement")
    val ciNeedsAgreement: Boolean?,

    @JsonProperty("ci")
    val ci: String?,

    @JsonProperty("ci_authenticated_at")
    val ciAuthenticatedAt: LocalDateTime?,
)

data class Profile(
    @JsonProperty("nickname")
    val nickname: String?,

    @JsonProperty("thumbnail_image_url")
    val thumbnailImageUrl: String?,

    @JsonProperty("profile_image_url")
    val profileImageUrl: String?,

    @JsonProperty("is_default_image")
    val isDefaultImage: Boolean?,

    @JsonProperty("is_default_nickname")
    val isDefaultNickname: Boolean?,
)

data class Partner(
    @JsonProperty("uuid")
    val uuid: String?,
)