package com.tvz.karlokovac.ednevnik

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tvz.karlokovac.ednevnik.model.Student
import com.tvz.karlokovac.ednevnik.retrofit.retrofitSinglton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_class_detail.*
import kotlinx.android.synthetic.main.student_list_for_class_content.view.*
import kotlinx.android.synthetic.main.class_detail.view.*

/**
 * A fragment representing a single Subject detail screen.
 * This fragment is either contained in a [ClassListActivity]
 * in two-pane mode (on tablets) or a [ClassDetailActivity]
 * on handsets.
 */
class ClassDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var item: String? = null
    private var classId: Long = 0
    private var students: List<Student>? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                item = it.getString(ARG_ITEM_ID)
                classId = it.getLong(ARG_CLASS_ID)
                activity?.toolbar_layout?.title = item
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.class_detail, container, false)

        retrofitSinglton.api.getStudentsForClass(retrofitSinglton.jwtToken, classId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> handleResposne(result, rootView) },
                        { error -> handleError(error) })

        return rootView
    }

    private fun handleResposne(response: List<Student>, rootView: View) {
        students = response
//        rootView.linLayout1.subject_detail.text = response[0].name
//        rootView.linLayout1.subject_detail.text = item
        rootView.linLayout1.student_list_for_class.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = StudentsAdapter(response) { student : Student -> studentClicked(student) }
        }
    }

    private fun studentClicked(student: Student){

        val intent = Intent(context, StudentDetailsActivity::class.java).apply {
            putExtra(StudentDetailsActivity.ARG_STUDENT_ID, student.studentId)
        }

        startActivity(intent)
    }

    private fun handleError(error: Throwable) {
        println(error.message)
    }

    class StudentsAdapter(var myStudents: List<Student>, val clickListener: (Student) -> Unit) : RecyclerView.Adapter<StudentsAdapter.StudentViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.student_list_for_class_content,
                    parent, false)
            return StudentViewHolder(view)
        }

        override fun getItemCount() = myStudents.size

        override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
            holder.bind(myStudents[position], clickListener)
        }

        inner class StudentViewHolder constructor(val studentView: View) : RecyclerView.ViewHolder(studentView) {
            fun bind(student: Student, clickListener: (Student) -> Unit) {
                studentView.student_list_for_class_name.text = student.name
                studentView.student_list_for_class_lastname.text = student.lastName
                studentView.setOnClickListener{ clickListener(student) }
            }
        }
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
        const val ARG_CLASS_ID = "class_id"
    }
}
