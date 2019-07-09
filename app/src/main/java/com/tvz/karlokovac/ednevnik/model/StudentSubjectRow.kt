package com.tvz.karlokovac.ednevnik.model

enum class RowType {
    ITEM,
    HEADER
}

data class StudentSubjectRow(var type: RowType, var grade: Grade?, var header: String?)