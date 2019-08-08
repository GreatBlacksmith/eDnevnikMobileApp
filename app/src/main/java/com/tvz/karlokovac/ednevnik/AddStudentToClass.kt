package com.tvz.karlokovac.ednevnik

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Spinner
import com.tvz.karlokovac.ednevnik.adapters.StudentSpinnerAdapter
import com.tvz.karlokovac.ednevnik.adapters.SubjectSpinnerAdapter
import com.tvz.karlokovac.ednevnik.model.AClass
import com.tvz.karlokovac.ednevnik.model.Student
import com.tvz.karlokovac.ednevnik.model.Subject
import com.tvz.karlokovac.ednevnik.retrofit.retrofitSinglton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_student_to_class.*
import kotlinx.android.synthetic.main.content_add_student_to_class.*
import kotlinx.android.synthetic.main.content_add_subject_to_student.*

class AddStudentToClass : AppCompatActivity() {

    lateinit var student_spinner: Spinner;
    var classId: Long = 0;
    var className: String = "-";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student_to_class)
        setSupportActionBar(toolbar)

        classId = intent.getLongExtra(AddStudentToClass.ARG_CLASS_ID, 0L);

        student_spinner = add_student_to_class_spinner;

        retrofitSinglton.api.getClassById(retrofitSinglton.jwtToken, classId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> handleClassResponse(result) },
                        { error -> handleClassError(error) })

        add_student_to_class_class.text = className

        retrofitSinglton.api.getAvailableStudentsForClass(retrofitSinglton.jwtToken, classId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> handleStudentSubjectResponse(result) },
                        { error -> handleStudenSubjectError(error) })

        add_student_to_class_button.setOnClickListener() {
            val selectedItem = student_spinner.selectedItem as Student


            retrofitSinglton.api.addStudentToClass(retrofitSinglton.jwtToken, classId, selectedItem.studentId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result -> handleAddSubjectResponse(result) },
                            { error -> handleAddSubjectError(error) })
        }

    }
    fun handleClassResponse(aClass: AClass){
        add_student_to_class_class.text = aClass.name
    }

    fun handleClassError(error: Throwable) {
        println(error.message)
    }

    fun handleStudentSubjectResponse(result: List<Student>) {

        var spinnerAdapter = StudentSpinnerAdapter(applicationContext!!, result);

        student_spinner?.adapter = spinnerAdapter;
    }

    fun handleStudenSubjectError(error: Throwable) {
        println(error.message)
    }

    fun handleAddSubjectResponse(result: Boolean) {

        val intent = Intent(applicationContext, ClassDetailActivity::class.java).apply {
            putExtra(StudentSubjectDetailsActivity.ARG_CLASS_ID, classId)
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
            putExtra(StudentSubjectDetailsActivity.ARG_CLASS_ID, classId)
        }
        setResult(RESULT_CANCELED, intent);
        finish();
    }


    companion object {
        const val ARG_CLASS_ID = "class_id"
    }

}
