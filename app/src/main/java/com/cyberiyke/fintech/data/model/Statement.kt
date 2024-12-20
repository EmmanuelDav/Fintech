package com.cyberiyke.fintech.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class Statement(
    val amount: String,
    val type: String,
    val client: String,
    val time: com.google.firebase.Timestamp,
    var message: String
): Parcelable