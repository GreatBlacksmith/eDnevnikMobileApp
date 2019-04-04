package com.tvz.karlokovac.ednevnik.retrofit

import com.tvz.karlokovac.ednevnik.model.AClass
import com.tvz.karlokovac.ednevnik.model.LoginUser
import com.tvz.karlokovac.ednevnik.model.Student
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface EDnevnikApi {

    @POST("login")
    fun login(@Body loginUser: LoginUser): Observable<Response<Void>>

    @GET("teacher/classes")
    fun getClasses(@Header("Authorization") jwt : String): Observable<List<AClass>>

    @GET("class/{id}/students")
    fun getStudentsForClass(@Header("Authorization") jwt : String, @Path("id") classId: Long) : Observable<List<Student>>

}

