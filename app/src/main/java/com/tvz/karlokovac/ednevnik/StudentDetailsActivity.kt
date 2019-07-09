package com.tvz.karlokovac.ednevnik

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Toast
import com.tvz.karlokovac.ednevnik.model.Student
import com.tvz.karlokovac.ednevnik.model.StudentSubject
import com.tvz.karlokovac.ednevnik.model.Subject
import com.tvz.karlokovac.ednevnik.retrofit.retrofitSinglton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_student_details.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_student_details.*
import kotlinx.android.synthetic.main.student_list_for_class_content.view.*
import kotlinx.android.synthetic.main.subject_list_for_student_content.view.*

class StudentDetailsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    var subjects: List<Subject> = emptyList()
    var student: Student? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_details)
        setSupportActionBar(toolbar)

        val studentId = intent.getLongExtra(ARG_STUDENT_ID, 0L)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout_student_details_activity, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout_student_details_activity.addDrawerListener(toggle)
        toggle.syncState()

        retrofitSinglton.api.getSubjectsForStudent(retrofitSinglton.jwtToken, studentId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { result -> handleSubjectsForStudentResposne(result)},
                        { error -> handleStudentError(error) })

        retrofitSinglton.api.getStudentById(retrofitSinglton.jwtToken, studentId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { result -> handleStudentResposne(result)},
                        { error -> handleStudentError(error) })
       // nav_view.setNavigationItemSelectedListener(this)
    }

    private fun handleStudentResposne(response: Student){
        student = response
        student_details_name_textview.text = response.name
        student_details_lastname_textview.text = response.lastName
    }

    private fun handleStudentError(error: Throwable){
        println(error.message)
    }

    private fun handleSubjectsForStudentResposne(response: List<Subject>){
        subjects = response
        student_details_subject_list.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = SubjectsAdapter(response) { subject: Subject -> subjectClicked(subject) }
        }
    }

    private fun subjectClicked(subject: Subject){

        val intent = Intent(applicationContext, StudentSubjectDetailsActivity::class.java).apply {
            putExtra(StudentSubjectDetailsActivity.ARG_STUDENT_ID, student!!.studentId)
            putExtra(StudentSubjectDetailsActivity.ARG_SUBJECT_ID, subject.subjectId)
        }

        startActivity(intent)

//        //TODO add handlers
//        retrofitSinglton.api.getStudentSubjectByStudentIdAndSubjectId(retrofitSinglton.jwtToken, student?.studentId!!, subject.subjectId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe (
//                        { result -> handleStudentSubjectResponse(result)},
//                        { error -> handleSubjectsForStudentError(error) })
    }

//    private fun handleStudentSubjectResponse(result : StudentSubject){
//        Toast.makeText(applicationContext, "Average: " + result.average, Toast.LENGTH_SHORT).show();
//    }
//
//    private fun handleSubjectsForStudentError(error: Throwable){
//        println(error.message)
//
//    }

    override fun onBackPressed() {
        if (drawer_layout_student_details_activity.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_student_details_activity.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
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

        drawer_layout_student_details_activity.closeDrawer(GravityCompat.START)
        return true
    }


    class SubjectsAdapter(var studentSubjects: List<Subject>, val clickListener: (Subject) -> Unit) : RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.subject_list_for_student_content,
                    parent, false)
            return SubjectViewHolder(view)
        }

        override fun getItemCount() = studentSubjects.size

        override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
            holder.bind(studentSubjects[position], clickListener)
        }

        inner class SubjectViewHolder constructor(val subjectView: View) : RecyclerView.ViewHolder(subjectView) {
            fun bind(subject: Subject, clickListener: (Subject) -> Unit) {
                subjectView.subject_list_for_student_name.text = subject.name
                subjectView.subject_list_for_student_decription.text = subject.description
                subjectView.setOnClickListener{ clickListener(subject) }
            }
        }
    }

    companion object {
        const val ARG_STUDENT_ID = "student_id"
    }
}
