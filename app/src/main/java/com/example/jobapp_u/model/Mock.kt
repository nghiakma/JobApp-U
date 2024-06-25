package com.example.jobapp_u.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mock(
    var uid: String = System.currentTimeMillis().toString(),
    var title: String = "",
    var duration: String = "",
    var mockQuestion: List<MockQuestion> = emptyList()
) : Parcelable