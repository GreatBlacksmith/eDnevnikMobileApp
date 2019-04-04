package com.tvz.karlokovac.ednevnik.retrofit

import com.tvz.karlokovac.ednevnik.constants.GlobalConstants
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object retrofitSinglton {

    val api = Retrofit.Builder()
            .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create())
            .addConverterFactory(
                    GsonConverterFactory.create())
            .baseUrl(GlobalConstants.baseServerUrl)
            .build()
            .create(EDnevnikApi::class.java)

    var jwtToken = "";
}