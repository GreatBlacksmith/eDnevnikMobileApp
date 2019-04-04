package com.tvz.karlokovac.ednevnik

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.tvz.karlokovac.ednevnik.model.AClass
import com.tvz.karlokovac.ednevnik.retrofit.retrofitSinglton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_subject_list.*
import kotlinx.android.synthetic.main.subject_list_content.view.*
import kotlinx.android.synthetic.main.subject_list.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ClassDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ClassListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    var classes: List<AClass> = emptyList()
    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.mipmap.ic_launcher_round)
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Handle navigation view item clicks here.
            when (menuItem.itemId) {

                R.id.nav_camera -> {
                    Toast.makeText(this, "Profile", Toast.LENGTH_LONG).show()
                }
                R.id.nav_gallery -> {
                    Toast.makeText(this, "Wallet", Toast.LENGTH_LONG).show()
                }
                R.id.nav_slideshow -> {
                    Toast.makeText(this, "Offer", Toast.LENGTH_LONG).show()
                }
                R.id.nav_manage -> {
                    Toast.makeText(this, "Setting", Toast.LENGTH_LONG).show()
                }
            }
            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }

        retrofitSinglton.jwtToken = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                .getString(getString(R.string.preference_token_key), "Test brate!");
        retrofitSinglton.api.getClasses(retrofitSinglton.jwtToken).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { result -> handleResposne(result)},
                        { error -> handleError(error) })

        if (subject_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        //setupRecyclerView(subject_list)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleResposne(response: List<AClass>){
        classes = response
        setupRecyclerView(subject_list)
    }

    private fun handleError(error: Throwable){
        println(error.message)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, classes, twoPane)
    }

    class SimpleItemRecyclerViewAdapter(private val parentActivity: ClassListActivity,
                                        private val values: List<AClass>,
                                        private val twoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as AClass
                if (twoPane) {
                    val fragment = ClassDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(ClassDetailFragment.ARG_ITEM_ID, item.name)
                            putLong(ClassDetailFragment.ARG_CLASS_ID, item.classId)
                            //putString("jwt", retrofitSinglton.jwtToken)
                        }
                    }
                    parentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.subject_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, ClassDetailActivity::class.java).apply {
                        putExtra(ClassDetailFragment.ARG_ITEM_ID, item.name)
                        putExtra(ClassDetailFragment.ARG_CLASS_ID, item.classId)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.subject_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = item.classId.toString()
            holder.contentView.text = item.name

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.id_text
            val contentView: TextView = view.content
        }
    }
}
