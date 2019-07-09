package com.tvz.karlokovac.ednevnik.model

class StudentSubject constructor(var studentId: Long,
                                 var studentName: String,
                                 var subjectId: Long,
                                 var subjectName: String,
                                 var className: String,
                                 var grades: List<Grade>,
                                 var average: Double) {
}