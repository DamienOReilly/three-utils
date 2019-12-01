package org.damienoreilly.threeutils.model

import com.squareup.moshi.JsonClass

sealed class ApiResponse
sealed class ApiError : ApiResponse()
data class UnknownError(val error: String) : ApiError()

// My3 models
@JsonClass(generateAdapter = true)
data class Login(val username: String,
                 val password: String)

@JsonClass(generateAdapter = true)
data class My3Token(val token: TokenInfo?,
                    val profile: Profile?) : ApiResponse()

@JsonClass(generateAdapter = true)
data class Profile(val customerType: String?,
                   val ctnList: List<Ctn>?,
                   val ban: Any?,
                   val name: String?)

@JsonClass(generateAdapter = true)
data class Ctn(val ctn: String?,
               val accountType: String?,
               val isBusinessAccount: Boolean?,
               val mbbAccount: Boolean?)

@JsonClass(generateAdapter = true)
data class TokenInfo(val accessToken: String?,
                     val refreshToken: String?)

@JsonClass(generateAdapter = true)
data class My3Error(val errorCode: String?,
                    val errorDescription: String?,
                    val errorNumber: Int?,
                    val defaultUserMessage: String?) : ApiError() {
    override fun toString(): String {
        return errorDescription ?: "Unspecified error."
    }
}


@JsonClass(generateAdapter = true)
data class UsageDetails(val plan: String?,
                        val wheels: List<Any>?,
                        val bars: List<Any>?,
                        val text: List<Usage>?) : ApiResponse()

@JsonClass(generateAdapter = true)
data class Usage(val name: String,
                 val expiryText: String,
                 val remaining: String)

@JsonClass(generateAdapter = true)
data class Balance(val balance: String?) : ApiResponse()


// 3Plus models
@JsonClass(generateAdapter = true)
data class ThreePlusToken(val access_token: String,
                          val token_type: String,
                          val expires_in: Long,
                          val scope: String,
                          val jti: String) : ApiResponse()

@JsonClass(generateAdapter = true)
data class Competitions(val id: Int,
                        val category: String?,
                        val categorySecondary: String?,
                        val title: String?,
                        val subtitle: String?,
                        val type: String?,
                        val index: Int?,
                        val order: Int?,
                        val urlBannerImageLarge: String?,
                        val urlBannerImageLargeApp: String?,
                        val urlBannerImageMedium: String?,
                        val urlBannerImageMediumApp: String?,
                        val urlBannerImageSmall: String?,
                        val urlBannerImageSmallApp: String?,
                        val urlSpecialEventImage: String?,
                        val redirectionUrl: String?,
                        val distance: Any?,
                        val help: String?,
                        val name: String,
                        val supplierName: String?,
                        val qrCode: Boolean?,
                        val urlName: String?,
                        val countdownOffer: Boolean?,
                        val endDate: String?,
                        val maxNbOfOffer: Int?,
                        val remaining: Int?) : ApiResponse()

@JsonClass(generateAdapter = true)
data class EnterCompetition(
        val fromWeb: Boolean = true,
        val offerQuantity: Int = 1,
        val sendSMS: Boolean = true,
        val offerName: String) : ApiResponse()

@JsonClass(generateAdapter = true)
data class CompetitionEntered(
        val voucher: String?,
        val expirationDate: String?,
        val partnerName: String?,
        val purchaseDate: String?,
        val voucherRedeemdate: String?) : ApiResponse()

@JsonClass(generateAdapter = true)
data class ThreePlusRequestError(val error: String,
                                 val error_description: String) : ApiError() {
    override fun toString(): String {
        return error_description
    }
}

@JsonClass(generateAdapter = true)
data class ThreePlusFatalError(val timestamp: Long,
                               val status: Int,
                               val error: String,
                               val exception: String,
                               val message: String,
                               val path: String) : ApiError() {
    override fun toString(): String {
        return "$error - $message"
    }
}

@JsonClass(generateAdapter = true)
data class ThreePlusCompetitionEnteringError(
        val message: String,
        val status: Int) : ApiError() {
    override fun toString(): String {
        return message
    }
}
