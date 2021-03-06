package com.tvz.karlokovac.ednevnik

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.tvz.karlokovac.ednevnik.GradeInputActivity.Companion.ARG_STUD_SUBJECT
import com.tvz.karlokovac.ednevnik.R.id.studentsubject_classLabel
import com.tvz.karlokovac.ednevnik.dto.StudSubjectDto
import com.tvz.karlokovac.ednevnik.retrofit.retrofitSinglton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_student_subject_details.*
import kotlinx.android.synthetic.main.app_bar_student_subject_details.*
import kotlinx.android.synthetic.main.content_student_subject_details.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import android.R.attr.data
import android.app.Activity
import com.tvz.karlokovac.ednevnik.model.*


class StudentSubjectDetailsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var studentId: Long = 0
    var subjectId: Long = 0
    var classId: Long = 0
    var className: String = "-"
    var gradeList: RecyclerView? = null
    lateinit var studentSubject: StudentSubject;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_subject_details)
        setSupportActionBar(toolbar)

        gradeList = stud_sub_list;
        gradeList?.visibility = View.GONE
        gradeList?.layoutManager = LinearLayoutManager(this)

        studentId = intent.getLongExtra(ARG_STUDENT_ID, 0L)
        subjectId = intent.getLongExtra(ARG_SUBJECT_ID, 0L)
        classId = intent.getLongExtra(ARG_CLASS_ID, 0L)

        retrofitSinglton.api.getClassById(retrofitSinglton.jwtToken, classId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> handleClassResponse(result) },
                        { error -> handleClassError(error) })

        fab.setOnClickListener { view ->

            var studSubjectDto = StudSubjectDto(
                    studentSubject.studentName,
                    studentSubject.studentId,
                    studentSubject.subjectName,
                    studentSubject.subjectId,
                    0L,
                    studentSubject.className
                    )

            val intent = Intent(applicationContext, GradeInputActivity::class.java).apply {
                putExtra(ARG_STUD_SUBJECT, studSubjectDto)
            }

            startActivityForResult(intent, 1)
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        getDataFromServer()

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun getDataFromServer(){
        retrofitSinglton.api.getStudentSubjectByStudentIdAndSubjectId(retrofitSinglton.jwtToken, studentId, subjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> handleStudentSubjectResponse(result) },
                        { error -> handleStudentSubjectError(error) })
    }

    private fun handleStudentSubjectResponse(result: StudentSubject) {
        studentSubject = result;
        setLabels();
        setUpGradeList(result.grades)
    }

    private fun setUpGradeList(grades: List<Grade>) {
        val gradeRows = mapResponseToRow(grades)
        val adapter = StudentSubjectGradeAdapter(gradeRows)
        gradeList?.adapter = adapter
        gradeList?.scrollToPosition(0)
        gradeList?.visibility = View.VISIBLE

    }


    private fun mapResponseToRow(gradeList: List<Grade>): ArrayList<StudentSubjectRow> {
        var rowList : ArrayList<StudentSubjectRow> = arrayListOf()
        rowList.add(StudentSubjectRow(RowType.HEADER, null, listOf(getString(R.string.header_type), getString(R.string.header_date), getString(R.string.header_grade))))
        for (grade in gradeList){
            val localDateTime = LocalDateTime.parse(grade.dateEarned)
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            grade.dateEarned = formatter.format(localDateTime)

            rowList.add(StudentSubjectRow(RowType.ITEM, grade, null))
        }

        return rowList
    }

    private fun handleStudentSubjectError(error: Throwable) {
        println(error.message)
    }

    private fun setLabels() {
        studentsubject_classText.setText(className)
        studentsubject_studentText.setText(studentSubject.studentName)
        studentsubject_subjectText.setText(studentSubject.subjectName)
        studentsubject_average.setText(studentSubject.average.toString())
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.student_subject_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun handleClassError(error: Throwable) {
        println(error.message)
    }

    fun handleClassResponse(aClass: AClass){
        className = aClass.name
//        add_subject_to_stud_class.text = studSubjectDto.className
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent){
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                studentId = data.getLongExtra(ARG_STUDENT_ID, 0L)
                subjectId = data.getLongExtra(ARG_SUBJECT_ID, 0L)
                getDataFromServer()
            }
        }
    }

    companion object {
        const val ARG_STUDENT_ID = "student_id"
        const val ARG_SUBJECT_ID = "subject_id"
        const val ARG_CLASS_ID = "class_id"
    }
}
