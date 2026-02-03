package com.example.emrtdapplication

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.emrtdapplication.fragments.AdditionalBiometricsFragment
import com.example.emrtdapplication.fragments.LDS1Fragment
import com.example.emrtdapplication.fragments.SettingsFragment
import com.example.emrtdapplication.fragments.TravelRecordsFragment
import com.example.emrtdapplication.fragments.VisaRecordFragment
import com.google.android.material.navigation.NavigationView

/**
 * Activity for navigating and displaying information read from the eMRTD
 *
 * @property actionBarDrawerToggle Listener for [drawerLayout]
 * @property drawerLayout Layout for navigating between different LDS application
 */
class EMRTDView() : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var actionBarDrawerToggle : ActionBarDrawerToggle? = null
    private var drawerLayout : DrawerLayout? = null
    private var currentFragment : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.emrtd_view)
        if (savedInstanceState != null) {
            currentFragment = savedInstanceState.getInt("currentFragment", 0)
            val title = savedInstanceState.getCharSequence("actionBarTitle")
            if (title != null) {
                supportActionBar?.title = title
            } else {
                supportActionBar?.setTitle(R.string.lds1)
            }
        } else {
            supportActionBar?.setTitle(R.string.lds1)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawerLayout = findViewById(R.id.drawerLayout)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.open,R.string.close
        )

        drawerLayout!!.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()

        val fragment = when(currentFragment) {
            0 -> LDS1Fragment()
            1 -> TravelRecordsFragment()
            2 -> VisaRecordFragment()
            3 -> AdditionalBiometricsFragment()
            else -> LDS1Fragment()
        }
        findViewById<NavigationView>(R.id.nvView).setNavigationItemSelectedListener(this)
        supportFragmentManager.commit { setReorderingAllowed(true)
                                        replace(R.id.nav_host_fragment, fragment)}

        onBackPressedDispatcher.addCallback(this) {
            EMRTD.reset()
            finish()
        }.isEnabled = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("currentFragment", currentFragment)
        outState.putCharSequence("actionBarTitle", supportActionBar?.title)
        super.onSaveInstanceState(outState)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        var fragment : Fragment? = null
        when (p0.itemId) {
            R.id.nav_lds1 -> {
                currentFragment = 0
                fragment = LDS1Fragment()
                supportActionBar?.setTitle(R.string.lds1)
            }
            R.id.nav_travel -> {
                currentFragment = 1
                fragment = TravelRecordsFragment()
                supportActionBar?.setTitle(R.string.travel_records)
            }
            R.id.nav_visa -> {
                currentFragment = 2
                fragment = VisaRecordFragment()
                supportActionBar?.setTitle(R.string.visa_records)
            }
            R.id.nav_biometrics -> {
                currentFragment = 3
                fragment = AdditionalBiometricsFragment()
                supportActionBar?.setTitle(R.string.additional_biometrics)
            }
            R.id.nav_settings -> {
                currentFragment = 4
                fragment = SettingsFragment()
                supportActionBar?.setTitle(R.string.settings)
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