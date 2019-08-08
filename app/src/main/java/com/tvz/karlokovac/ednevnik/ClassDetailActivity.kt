package com.tvz.karlokovac.ednevnik

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.tvz.karlokovac.ednevnik.model.AClass
import com.tvz.karlokovac.ednevnik.retrofit.retrofitSinglton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_class_detail.*

/**
 * An activity representing a single Subject detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [ClassListActivity].
 */
class ClassDetailActivity : AppCompatActivity() {

    var classId: Long = 0L;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_detail)
        setSupportActionBar(detail_toolbar)

//        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
//        val token = sharedPref.getString(getString(R.string.preference_token_key), "Test brate!")

        classId = intent.getLongExtra(ClassDetailFragment.ARG_CLASS_ID, 0L)

        retrofitSinglton.api.getClasses(retrofitSinglton.jwtToken).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { result -> handleResposne(savedInstanceState, result)},
                        { error -> handleError(error) })

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        class_details_add_student.setOnClickListener() { view ->

            val intent = Intent(applicationContext, AddStudentToClass::class.java).apply {
                putExtra(AddStudentToClass.ARG_CLASS_ID, classId)
            }

            startActivityForResult(intent, 1)
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
    }
    private fun handleResposne(savedInstanceState: Bundle?, response: List<AClass>){

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = ClassDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ClassDetailFragment.ARG_ITEM_ID, intent.getStringExtra(ClassDetailFragment.ARG_ITEM_ID))
                    putLong(ClassDetailFragment.ARG_CLASS_ID,
                            classId)
                }
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.subject_detail_container, fragment)
                    .commit()
        }
    }

    private fun handleError(error: Throwable){
        println(error.message)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    // This ID represents the Home or Up button. In the case of this
                    // activity, the Up button is shown. For
                    // more details, see the Navigation pattern on Android Design:
                    //
                    // http://developer.android.com/design/patterns/navigation.html#up-vs-back

                    navigateUpTo(Intent(this, ClassListActivity::class.java))
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}
