package com.example.drunk_driving.repository

import android.util.Log
import com.example.drunk_driving.model.CaseData
import com.example.drunk_driving.model.Location
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await

object CaseRepository {
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val casesCollection by lazy { firestore.collection("Cases") }
    private val counterCollection by lazy { firestore.collection("counters") }

    // 建立新的案件文件
    suspend fun createCase(
        reporterId: String,
        videoUrl: String,
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        address: String = "待定位",
        classification: String = ""
    ): Result<String> {
        return try {
            // 生成遞增的案件 ID
            val caseId = getNextCaseId()

            val caseData = CaseData(
                caseId = caseId,
                reporterId = reporterId,
                time = Timestamp.now(),
                location = Location(
                    geo = GeoPoint(latitude, longitude),
                    address = address
                ),
                classification = classification,
                status = "待處理", // 初始狀態
                videoUrl = videoUrl,
                createdAt = Timestamp.now()
            )

            casesCollection.document(caseId).set(caseData).await()
            Log.d("CaseRepository", "案件建立成功: $caseId")
            Result.success(caseId)

        } catch (e: Exception) {
            Log.e("CaseRepository", "建立案件失敗", e)
            Result.failure(e)
        }
    }

    // 產生遞增的案件編號
    private suspend fun getNextCaseId(): String {
        val counterDocRef = counterCollection.document("caseCounter")

        return firestore.runTransaction { transaction ->
            val counterDoc = transaction.get(counterDocRef)

            val currentNumber = if (counterDoc.exists()) {
                counterDoc.getLong("count") ?: 0L
            } else {
                0L
            }

            val nextNumber = currentNumber + 1

            // 更新計數器
            transaction.set(counterDocRef, mapOf("count" to nextNumber))

            // 返回格式化的案件編號
            formatCaseId(nextNumber)
        }.await()
    }

    // 格式化案件編號為 C00001 格式
    private fun formatCaseId(number: Long): String {
        return "C${number.toString().padStart(5, '0')}"
    }

    // 初始化計數器
    suspend fun initializeCaseCounter(startFrom: Long = 0): Result<Unit> {
        return try {
            counterCollection.document("caseCounter")
                .set(mapOf("count" to startFrom))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CaseRepository", "初始化計數器失敗", e)
            Result.failure(e)
        }
    }
}