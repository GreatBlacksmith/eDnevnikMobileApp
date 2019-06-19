package com.tvz.karlokovac.ednevnik.model

class Subject (var subjectId:Long, var name: String, var description: String, var subjectTypeId: Long, var classTypesId: List<Long>) {
}