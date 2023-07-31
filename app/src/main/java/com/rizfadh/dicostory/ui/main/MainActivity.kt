package com.rizfadh.dicostory.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import com.rizfadh.dicostory.R
import com.rizfadh.dicostory.ui.authentication.AuthActivity
import com.rizfadh.dicostory.ui.maps.MapsActivity
import com.rizfadh.dicostory.utils.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModelFactory: ViewModelFactory
    private val mainViewModel: MainViewModel by viewModels { viewModelFactory }
    private lateinit var userToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        viewModelFactory = ViewModelFactory.getInstance(this)

        mainViewModel.getUserToken().observe(this) {
            if (it != null) {
                supportActionBar?.show()
                supportActionBar?.title = getString(R.string.home)
                userToken = it
                if (savedInstanceState == null) showHomeUi()
            } else toAuthActivity()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> mainViewModel.deleteUserToken()
            R.id.action_localization -> startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            R.id.action_maps -> {
                if (::userToken.isInitialized) {
                    val mapsIntent = Intent(this, MapsActivity::class.java).apply {
                        putExtra(EXTRA_TOKEN, userToken)
                    }
                    startActivity(mapsIntent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toAuthActivity() {
        val authIntent = Intent(this, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(authIntent)
        overridePendingTransition(0, 0)
    }

    private fun showHomeUi() {
        val homeFragment = HomeFragment().apply {
            arguments = bundleOf(EXTRA_TOKEN to userToken)
        }
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_main_container, homeFragment)
        }
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }

}