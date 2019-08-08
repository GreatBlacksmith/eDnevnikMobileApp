package com.tvz.karlokovac.ednevnik

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Spinner
import com.tvz.karlokovac.ednevnik.adapters.SubjectSpinnerAdapter
import com.tvz.karlokovac.ednevnik.dto.StudSubjectDto
import com.tvz.karlokovac.ednevnik.model.AClass
import com.tvz.karlokovac.ednevnik.model.Subject
import com.tvz.karlokovac.ednevnik.retrofit.retrofitSinglton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_add_subject_to_student2.*
import kotlinx.android.synthetic.main.content_add_subject_to_student.*

class AddSubjectToStudent : AppCompatActivity() {

    lateinit var subject_spinner: Spinner

    lateinit var studSubjectDto: StudSubjectDto
    var className: String = "test";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subject_to_student2)
        setSupportActionBar(toolbar)

        studSubjectDto = intent.getParcelableExtra(GradeInputActivity.ARG_STUD_SUBJECT)

        subject_spinner = add_subject_to_stud_spinner
        add_subject_to_stud_student.text = studSubjectDto.studentName
        add_subject_to_stud_class.text = studSubjectDto.className

        retrofitSinglton.api.getClassById(retrofitSinglton.jwtToken, studSubjectDto.classId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> handleClassResponse(result) },
                        { error -> handleClassError(error) })


        add_subject_to_stud_button.setOnClickListener {
            val selectedItem = subject_spinner.selectedItem as Subject


            retrofitSinglton.api.addSubjectToStudent(retrofitSinglton.jwtToken, studSubjectDto.studentId, selectedItem.subjectId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> handleAddSubjectResponse(result) },
                            { error -> handleAddSubjectError(error) })
        }

        retrofitSinglton.api.getAvailableSubjectsForStudent(retrofitSinglton.jwtToken, studSubjectDto.studentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> handleStudentSubjectResponse(result) },
                        { error -> handleStudenSubjectError(error) })
    }

    fun handleStudentSubjectResponse(result: List<Subject>) {

        var spinnerAdapter = SubjectSpinnerAdapter(applicationContext!!, result);

        subject_spinner?.adapter = spinnerAdapter;

//        val intent = Intent(applicationContext, StudentSubjectDetailsActivity::class.java).apply {
//            putExtra(StudentSubjectDetailsActivity.ARG_STUDENT_ID, studSubjectDto.studentId)
//            putExtra(StudentSubjectDetailsActivity.ARG_SUBJECT_ID, studSubjectDto.subjectId)
//        }
//        setResult(RESULT_OK, intent);
//        finish();
    }

    fun handleStudenSubjectError(error: Throwable) {
        println(error.message)
    }

    fun handleClassResponse(aClass: AClass){
        className = aClass.name
//        add_subject_to_stud_class.text = aClass.name
    }

    fun handleClassError(error: Throwable) {
        println(error.message)
    }

    fun handleAddSubjectResponse(result: Boolean) {

        val intent = Intent(applicationContext, StudentDetailsActivity::class.java).apply {
            putExtra(StudentSubjectDetailsActivity.ARG_STUDENT_ID, studSubjectDto.studentId)
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    fun handleAddSubjectError(error: Throwable) {
        println(error.message)
        returnFromError()
    }

    override fun onBackPressed() {
        returnFromError()
    }

    fun returnFromError() {
        val intent = Intent(applicationContext, StudentDetailsActivity::class.java).apply {
            putExtra(StudentSubjectDetailsActivity.ARG_STUDENT_ID, studSubjectDto.studentId)
        }
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    companion object {
        const val ARG_STUD_SUBJECT = "stud_subject"
    }
}
