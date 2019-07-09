package com.tvz.karlokovac.ednevnik

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
import com.tvz.karlokovac.ednevnik.R.id.studentsubject_classLabel
import com.tvz.karlokovac.ednevnik.model.Grade
import com.tvz.karlokovac.ednevnik.model.RowType
import com.tvz.karlokovac.ednevnik.model.StudentSubject
import com.tvz.karlokovac.ednevnik.model.StudentSubjectRow
import com.tvz.karlokovac.ednevnik.retrofit.retrofitSinglton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_student_subject_details.*
import kotlinx.android.synthetic.main.app_bar_student_subject_details.*
import kotlinx.android.synthetic.main.content_student_subject_details.*

class StudentSubjectDetailsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var studentId: Long = 0
    var subjectId: Long = 0
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

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        retrofitSinglton.api.getStudentSubjectByStudentIdAndSubjectId(retrofitSinglton.jwtToken, studentId, subjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> handleStudentSubjectResponse(result) },
                        { error -> handleStudenSubjectError(error) })

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun handleStudentSubjectResponse(result: StudentSubject) {
        studentSubject = result;
        setLabels();
        setUpGradeList(result.grades)
        Toast.makeText(applicationContext, "Date: " + result.grades.get(0).dateEarned, Toast.LENGTH_SHORT).show();
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
        rowList.add(StudentSubjectRow(RowType.HEADER, null, "OCJENE"))
        for (grade in gradeList){
            rowList.add(StudentSubjectRow(RowType.ITEM, grade, null))
        }

        return rowList
    }

    private fun handleStudenSubjectError(error: Throwable) {
        println(error.message)
    }

    private fun setLabels() {
        studentsubject_classLabel.setText("Razred")
        studentsubject_classText.setText(studentSubject.className)
        studentsubject_studentLabel.setText("Student")
        studentsubject_studentText.setText(studentSubject.studentName)
        studentsubject_subjectLabel.setText("Predmet")
        studentsubject_subjectText.setText(studentSubject.subjectName)
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

    companion object {
        const val ARG_STUDENT_ID = "student_id"
        const val ARG_SUBJECT_ID = "subject_id"
    }
}
