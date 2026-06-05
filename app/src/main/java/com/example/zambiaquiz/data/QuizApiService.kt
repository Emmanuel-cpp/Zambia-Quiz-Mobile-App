package com.example.zambiaquiz.data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface QuizApiService {

    @GET("questions.json")
    suspend fun getQuestions(): Response<ResponseBody>
}