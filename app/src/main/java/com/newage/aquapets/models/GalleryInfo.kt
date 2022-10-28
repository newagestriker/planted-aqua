package com.newage.aquapets.models

data class GalleryInfo(
        var facebookURL: String = "",
        var twitterURL: String = "",
        var instagramURL: String = "",
        var authorsName: String = "",
        var tech: String = "",
        var fts: String = "",
        var flora: String = "",
        var fauna: String = "",
        var description: String = "",
        var tankImageURL: String = "",
        var tankImageFileName: String = "",
        var userID: String = "",
        var websiteURL: String = "",
        var userphotoURL: String = "",
        var firebaseUserID: String = "",
        var email: String = "",
        var rating: Float = 0.0f,
        var yourRating: Float = 0.0f,
        var ratingCount: Int = 0
)
    {
    fun ratingInText(): String {
        return "$ratingCount Ratings"
    }

    fun avgRating(): Float {
        return if (ratingCount.toFloat() == 0.0f) 0.0f else rating / ratingCount
    }

}