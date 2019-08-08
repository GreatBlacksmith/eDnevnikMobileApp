package com.tvz.karlokovac.ednevnik.retrofit

import com.tvz.karlokovac.ednevnik.dto.StudSubjectRequest
import com.tvz.karlokovac.ednevnik.model.*
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

    @GET("class/{id}/available-students")
    fun getAvailableStudentsForClass(@Header("Authorization") jwt : String, @Path("id") classId: Long) : Observable<List<Student>>

    @FormUrlEncoded
    @POST("class/{id}/add-student")
    fun addStudentToClass(@Header("Authorization") jwt: String, @Path("id") classId: Long, @Field("studentId") studentId: Long) : Observable<Boolean>


    @GET("class/{id}")
    fun getClassById(@Header("Authorization") jwt : String, @Path("id") classId: Long) : Observable<AClass>

    @GET("student/{id}/subjects")
    fun getSubjectsForStudent(@Header("Authorization") jwt: String, @Path("id") studentId: Long?): Observable<List<Subject>>

    @GET("student/{id}/available-subjects")
    fun getAvailableSubjectsForStudent(@Header("Authorization") jwt: String, @Path("id") studentId: Long?): Observable<List<Subject>>

    @FormUrlEncoded
    @POST("student/{id}/add-subject")
    fun addSubjectToStudent(@Header("Authorization") jwt: String, @Path("id") studentId: Long, @Field("subjectId") subjectId: Long) : Observable<Boolean>

    @GET("student/{id}")
    fun getStudentById(@Header("Authorization") jwt : String, @Path("id") studentId: Long): Observable<Student>

    @GET("student-subject/{studentId}/{subjectId}")
    fun getStudentSubjectByStudentIdAndSubjectId(@Header("Authorization") jwt: String, @Path("studentId") studentId : Long,
                                                 @Path("subjectId") subjectId : Long): Observable<StudentSubject>

    @POST("student-subject")
    fun addGradeToStudentSubject(@Header("Authorization") jwt: String, @Body request : StudSubjectRequest): Observable<StudentSubject>
}

