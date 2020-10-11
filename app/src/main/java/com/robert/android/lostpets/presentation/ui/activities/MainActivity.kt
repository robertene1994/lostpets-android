package com.robert.android.lostpets.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import com.robert.android.lostpets.R
import com.robert.android.lostpets.domain.executor.impl.ThreadExecutor
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.presentation.presenters.MainPresenter
import com.robert.android.lostpets.presentation.presenters.impl.MainPresenterImpl
import com.robert.android.lostpets.presentation.ui.activities.base.AbstractActivity
import com.robert.android.lostpets.presentation.ui.fragments.*
import com.robert.android.lostpets.presentation.ui.utils.SnackbarUtil
import com.robert.android.lostpets.storage.SessionRepositoryImpl
import com.robert.android.lostpets.threading.MainThreadImpl
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_navigation_drawer.*
import kotlinx.android.synthetic.main.nav_header_navigation_drawer.view.*
import org.jetbrains.anko.startActivity

/**
 * Activity que extiende la clase AbstractActivity e implementa la interfaz del callback del
 * presenter MainPresenter. Es el controlador que se encarga de manejar y coordinar todas las
 * demás vistas (fragments), basándose en el menú lateral proporciondo.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.ui.activities.base.AbstractActivity
 * @see com.robert.android.lostpets.presentation.presenters.MainPresenter.View
 */
class MainActivity : AbstractActivity(), MainPresenter.View, NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val NEW_AD = "MainActivity::NewAd"
        const val UPDATE_AD = "MainActivity::UpdateAd"
        const val CHAT_FROM_OUTSIDE = "MainActivity::ChatFromAdDetail"
        const val USER_TO_CHAT = "MainActivity::UserToChat"
    }

    private lateinit var mMainPresenter: MainPresenter
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadExtras()
        init()
    }

    override fun onResume() {
        super.onResume()
        mMainPresenter.resume()
    }

    override fun onPause() {
        super.onPause()
        mMainPresenter.pause()
    }

    override fun onStop() {
        super.onStop()
        mMainPresenter.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMainPresenter.destroy()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.adsNavItem -> loadFragment(AdsFragment.newInstance(mUser))
            R.id.userAdsNavItem -> loadFragment(UserAdsFragment.newInstance(mUser))
            R.id.addAdNavItem -> loadFragment(AddAdFragment.newInstance(mUser))
            R.id.userChatsNavItem -> loadFragment(UserChatsFragment.newInstance(mUser))
            R.id.settingsNavItem -> loadFragment(SettingsFragment.newInstance())
            R.id.logoutNavItem -> mMainPresenter.logOut()
        }
        item.isChecked = true
        drawerLayout.closeDrawers()
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers()
        else
            super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && data != null) {
            with(data) {
                if (requestCode == AddAdFragment.PICK_PET_IMAGE || requestCode == UpdateAdFragment.PICK_PET_IMAGE)
                    supportFragmentManager.findFragmentById(R.id.fragmentContent)!!
                            .onActivityResult(requestCode, resultCode, data)
                if (getStringExtra(CHAT_FROM_OUTSIDE) != null)
                    loadFragment(UserChatsFragment.newInstance(mUser, getParcelableExtra(USER_TO_CHAT)))
            }
        }
    }

    override fun onUserRetrieved(user: User) {
        mUser = user

        with(intent) {
            when {
                getStringExtra(CHAT_FROM_OUTSIDE) != null ->
                    loadFragment(UserChatsFragment
                            .newInstance(mUser, getParcelableExtra(USER_TO_CHAT)), false)
                else -> loadFragment(AdsFragment.newInstance(mUser), false)
            }
        }

        val header = navigationView.getHeaderView(0)
        header.loggedUserNameTextView.text =
                String.format(getString(R.string.nav_user_profile_info),
                        mUser.lastName, mUser.firstName)
        header.loggedUserEmailTextView.text = mUser.email
    }

    override fun onLogOut() {
        startActivity<LogInActivity>()
        finish()
    }

    override fun noInternetConnection() {
        SnackbarUtil.makeShort(drawerLayout, R.string.msg_no_internet_connection)
    }

    override fun serviceNotAvailable() {
        SnackbarUtil.makeLong(drawerLayout, R.string.msg_service_not_avabile)
    }

    private fun loadExtras() {
        with(intent) {
            when {
                getStringExtra(NEW_AD) != null ->
                    SnackbarUtil.makeShort(drawerLayout, R.string.msg_new_ad_added)
                getStringExtra(UPDATE_AD) != null ->
                    SnackbarUtil.makeShort(drawerLayout, R.string.msg_ad_updated)
            }
        }
    }

    private fun init() {
        toolbar.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string
                .navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.inflateHeaderView(R.layout.nav_header_navigation_drawer)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.adsNavItem)

        setProgressBarLayout(progressBarLayout)
        addViewsToHide(fragmentContent)

        mMainPresenter = MainPresenterImpl(ThreadExecutor.instance, MainThreadImpl.instance,
                this, getContext(), SessionRepositoryImpl(getContext()))
        mMainPresenter.getUser()
    }

    private fun loadFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager
                .beginTransaction().replace(fragmentContent.id, fragment)
        if (addToBackStack)
            transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
    }
}
