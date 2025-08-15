package com.client.findjob.data.source.remote

import com.client.findjob.service.ApiService

class HomeRemoteImpl (private val apiService: ApiService) : MovieDataSource.Remote {
    override suspend fun getMovies(): BaseResponse<List<Movie>> {
        return apiService.getTopRateMovies(apiKey = BuildConfig.API_KEY)
    }

    override suspend fun getMovieDetail(movieId: Int): Movie {
        return apiService.getMovieDetails(movieId = movieId, apiKey = BuildConfig.API_KEY)
    }
}