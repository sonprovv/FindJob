package com.client.findjob.data.repository

import com.client.findjob.data.model.CheckNotiMes
import com.client.findjob.service.RetrofitClient
import retrofit2.Response

class NotiMesRepo {
    suspend fun checkFirebase(): Response<CheckNotiMes> {
        return RetrofitClient.apiService.checkFirebase()
    }
}