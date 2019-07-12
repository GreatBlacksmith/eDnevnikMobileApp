package com.tvz.karlokovac.ednevnik.dto

class StudSubjectRequest constructor(
        var studentId: Long,
        var subjectId: Long,
        var gradeType: String,
        var grade: String
) {
}