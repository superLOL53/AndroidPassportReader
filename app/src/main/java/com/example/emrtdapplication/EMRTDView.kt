package com.example.emrtdapplication

import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.emrtdapplication.fragments.LDS1Fragment
import com.google.android.material.navigation.NavigationView

/**
 * TODO: Write docs
 */
class EMRTDView() : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var actionBarDrawerToggle : ActionBarDrawerToggle? = null
    private var drawerLayout : DrawerLayout? = null
    private var lds1ViewLayout : LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.emrtd_view)

        lds1ViewLayout = findViewById(R.id.lds1layout)
        drawerLayout = findViewById(R.id.drawerLayout)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.open,R.string.close
        )

        drawerLayout!!.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.lds1)

        findViewById<NavigationView>(R.id.nvView).setNavigationItemSelectedListener(this)

        val fragment = LDS1Fragment()
        supportFragmentManager.commit { setReorderingAllowed(true)
                                        replace(R.id.nav_host_fragment, fragment)}
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        var fragment : Fragment? = null
        when (p0.itemId) {
            R.id.nav_lds1 -> {
                fragment = LDS1Fragment()
                supportActionBar?.setTitle(R.string.lds1)
            }
            R.id.nav_travel -> {
                fragment = Fragment(R.layout.travel_records)
                supportActionBar?.setTitle(R.string.travel_records)
            }
            R.id.nav_visa -> {
                fragment = Fragment(R.layout.visa_records)
                supportActionBar?.setTitle(R.string.visa_records)
            }
            R.id.nav_biometrics -> {
                fragment = Fragment(R.layout.additional_biometrics)
                supportActionBar?.setTitle(R.string.additional_biometrics)
            }
        }
        if (fragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit()
        }
        drawerLayout!!.closeDrawers()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle != null && actionBarDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }
}