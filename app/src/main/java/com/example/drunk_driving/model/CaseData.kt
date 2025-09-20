package com.example.drunk_driving.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint


data class Location(
    val geo: GeoPoint = GeoPoint(0.0, 0.0),
    val address: String = ""
)

data class CaseData(
    val caseId: String = "",
    val reporterId: String = "",
    val time: Timestamp = Timestamp.now(),
    val location: Location = Location(),
    val classification: String = "",
    val status: String = "",
    val videoUrl: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
