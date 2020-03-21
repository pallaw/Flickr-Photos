package com.pallaw.flickrphotos.data.model

import com.google.gson.annotations.SerializedName

data class Urls(
    @SerializedName("url")
    var url: List<Url> = listOf()
) {
    data class Url(
        @SerializedName("_content")
        var content: String = "",
        @SerializedName("type")
        var type: String = ""
    )
}