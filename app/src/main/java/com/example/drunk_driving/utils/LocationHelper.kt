package com.example.drunk_driving.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object LocationHelper {
    // 檢查是否有位置權限
    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 取得當前位置
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) {
            return null
        }

        return try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }

    // 將座標轉換為地址
    suspend fun getAddressFromLocation(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                suspendCoroutine { continuation ->
                    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                        addresses.firstOrNull()?.getAddressLine(0)?.let {
                            continuation.resume(it)
                        } ?: continuation.resume("找不到地址")
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    addresses[0].getAddressLine(0) ?: "找不到地址"
                } else {
                    "找不到地址"
                }
            }
        } catch (e: Exception) {
            "位置解析錯誤"
        }
    }

    // 取得當前位置和地址的完整資訊
    suspend fun getCurrentLocationInfo(context: Context): LocationInfo {
        val location = getCurrentLocation(context)

        return if (location != null) {
            val address = getAddressFromLocation(context, location.latitude, location.longitude)
            LocationInfo(
                latitude = location.latitude,
                longitude = location.longitude,
                address = address,
                hasLocation = true
            )
        } else {
            LocationInfo(
                latitude = 0.0,
                longitude = 0.0,
                address = if (hasLocationPermission(context)) "無法取得位置" else "需要位置權限",
                hasLocation = false
            )
        }
    }

    // 位置資訊資料類
    data class LocationInfo(
        val latitude: Double,
        val longitude: Double,
        val address: String,
        val hasLocation: Boolean
    )
}