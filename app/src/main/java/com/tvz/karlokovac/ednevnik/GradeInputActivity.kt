package com.tvz.karlokovac.ednevnik

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Spinner
import com.tvz.karlokovac.ednevnik.StudentSubjectDetailsActivity.Companion.ARG_STUDENT_ID
import com.tvz.karlokovac.ednevnik.StudentSubjectDetailsActivity.Companion.ARG_SUBJECT_ID
import com.tvz.karlokovac.ednevnik.dto.StudSubjectDto
import com.tvz.karlokovac.ednevnik.dto.StudSubjectRequest
import com.tvz.karlokovac.ednevnik.model.StudentSubject
import com.tvz.karlokovac.ednevnik.retrofit.retrofitSinglton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_grade_input.*
import kotlinx.android.synthetic.main.content_grade_input.*

class GradeInputActivity : AppCompatActivity() {

    var grade_type_spinner: Spinner? = null
    var grade_spinner: Spinner? = null
    lateinit var add_grade_button : Button;
    lateinit var studSubjectDto : StudSubjectDto;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grade_input)
        setSupportActionBar(toolbar)


        studSubjectDto = intent.getParcelableExtra(ARG_STUD_SUBJECT)

        grade_input_student.setText(studSubjectDto.studentName)
        grade_input_subject.setText(studSubjectDto.subjectName)
        grade_input_class.setText(studSubjectDto.className)

        grade_type_spinner = grade_input_type_spinner
        grade_spinner = grade_input_spinner

        add_grade_button = grade_input_add_grade_button
        add_grade_button.setOnClickListener {
            callAddGrade();
        }

    }

    fun callAddGrade(){

        val studSubjectRequest = StudSubjectRequest(
                studSubjectDto.studentId,
                studSubjectDto.subjectId,
                grade_type_spinner?.selectedItem.toString(),
                grade_spinner?.selectedItem.toString())

        retrofitSinglton.api.addGradeToStudentSubject(retrofitSinglton.jwtToken, studSubjectRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> handleStudentSubjectResponse(result) },
                        { error -> handleStudenSubjectError(error) })
    }

    fun handleStudentSubjectResponse(result: StudentSubject){
        val intent = Intent(applicationContext, StudentSubjectDetailsActivity::class.java).apply {
            putExtra(ARG_STUDENT_ID, studSubjectDto.studentId)
            putExtra(ARG_SUBJECT_ID, studSubjectDto.subjectId)
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, StudentSubjectDetailsActivity::class.java).apply {
        }
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    fun handleStudenSubjectError(error: Throwable){

    }

    companion object {
        const val ARG_STUD_SUBJECT = "stud_subject"
    }
}
