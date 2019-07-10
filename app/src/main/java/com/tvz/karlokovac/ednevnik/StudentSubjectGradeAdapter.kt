package com.tvz.karlokovac.ednevnik

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tvz.karlokovac.ednevnik.model.RowType
import com.tvz.karlokovac.ednevnik.model.StudentSubjectRow
import com.tvz.karlokovac.ednevnik.viewholders.DefaultViewHolder

class StudentSubjectGradeAdapter(private var gradeList: ArrayList<StudentSubjectRow>) : RecyclerView.Adapter<DefaultViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val inflatedView = when (viewType) {
            RowType.ITEM.ordinal -> layoutInflater.inflate(R.layout.student_subject_row_item, parent, false)
            else -> layoutInflater.inflate(R.layout.header_item, parent, false)
        }
        return DefaultViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: DefaultViewHolder, position: Int) {
        val itemRow: StudentSubjectRow = gradeList[position]
        if (itemRow.type == RowType.ITEM) {
            val grade = itemRow.grade

            grade?.dateEarned?.let { holder.setText(R.id.stud_sub_row_date, it) }
            grade?.type?.let { holder.setText(R.id.stud_sub_row_type, it) }
            grade?.grade?.let { holder.setText(R.id.stud_sub_row_grade, it.toString()) }
        } else {
            itemRow.header?.get(0)?.let { holder.setText(R.id.header_type, it) }
            itemRow.header?.get(1)?.let { holder.setText(R.id.header_date, it) }
            itemRow.header?.get(2)?.let { holder.setText(R.id.header_grade, it) }
        }
    }

    override fun getItemCount(): Int {
        return gradeList.size
    }

    override fun getItemViewType(position: Int) = gradeList[position].type.ordinal
}


