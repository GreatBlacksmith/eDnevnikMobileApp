package com.tvz.karlokovac.ednevnik

import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.tvz.karlokovac.ednevnik.model.AClass
import com.tvz.karlokovac.ednevnik.retrofit.retrofitSinglton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    var classes: List<AClass> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout_homeactivity, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout_homeactivity.addDrawerListener(toggle)
        toggle.syncState()
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        val token = sharedPref.getString(getString(R.string.preference_token_key), "Test brate!")
        retrofitSinglton.api.getClasses(token).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { result -> handleResposne(result)},
                        { error -> handleError(error) })

       // nav_view.setNavigationItemSelectedListener(this)
    }

    private fun handleResposne(response: List<AClass>){
        classes = response
    }

    private fun handleError(error: Throwable){
        println(error.message)
    }

    override fun onBackPressed() {
        if (drawer_layout_homeactivity.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_homeactivity.closeDrawer(GravityCompat.START)
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

        drawer_layout_homeactivity.closeDrawer(GravityCompat.START)
        return true
    }
}
