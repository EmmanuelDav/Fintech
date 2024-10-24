package com.iyke.onlinebanking.model

import android.os.Parcelable

class CardDetails(
    var cardLabel: String,
    var cardName: String,
    var cardNumber: String,
    var cardDate: String,
    var cardCVC: String,
    var zipCode: String,
    var city: String
)