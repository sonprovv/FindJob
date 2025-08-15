package com.client.findjob.service

import com.client.findjob.data.model.CheckNotiMes
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("check-firebase")
    suspend fun checkFirebase(): Response<CheckNotiMes>
}