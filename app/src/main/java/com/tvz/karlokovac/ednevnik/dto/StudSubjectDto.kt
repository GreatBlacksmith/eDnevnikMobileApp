package com.tvz.karlokovac.ednevnik.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class StudSubjectDto constructor(
        var studentName: String,
        var studentId: Long,
        var subjectName: String,
        var subjectId: Long,
        var className: String): Parcelable {
}