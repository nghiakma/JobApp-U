package com.example.jobapp_u.model

import android.os.Parcelable
import com.example.jobapp_u.model.Academic
import com.example.jobapp_u.model.Address
import com.example.jobapp_u.model.Details
import kotlinx.parcelize.Parcelize

@Parcelize
data class Student(
    var uid : String? = null,
    var details: Details? = null,
    var address: Address? = null,
    var academic: Academic? = null,
):Parcelable
