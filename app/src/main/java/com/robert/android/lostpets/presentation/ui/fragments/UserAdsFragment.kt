package com.robert.android.lostpets.presentation.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.robert.android.lostpets.R
import com.robert.android.lostpets.domain.executor.impl.ThreadExecutor
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.network.ServiceGenerator
import com.robert.android.lostpets.network.service.AdService
import com.robert.android.lostpets.presentation.presenters.UserAdsPresenter
import com.robert.android.lostpets.presentation.presenters.impl.UserAdsPresenterImpl
import com.robert.android.lostpets.presentation.ui.activities.AdDetailActivity
import com.robert.android.lostpets.presentation.ui.adapters.AdsListAdapter
import com.robert.android.lostpets.presentation.ui.fragments.base.AbstractFragment
import com.robert.android.lostpets.presentation.ui.utils.SnackbarUtil
import com.robert.android.lostpets.threading.MainThreadImpl
import kotlinx.android.synthetic.main.fragment_user_ads.*
import kotlinx.android.synthetic.main.progress_bar.*

/**
 * Fragment que extiende la clase AbstractFragment e implementa la interfaz del callback del
 * presenter UserAdsPresenter. Es el controlador que se encarga de manejar la vista correspondiente
 * a los anuncios de mascotas perdidas publicados por un determinado usuario.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.ui.fragments.base.AbstractFragment
 * @see com.robert.android.lostpets.presentation.presenters.UserAdsPresenter.View
 */
class UserAdsFragment: AbstractFragment(), UserAdsPresenter.View {

    companion object {
        private const val USER = "UserAdsFragment::User"

        /**
         * Método que instancia el fragment para la vista asociada a los anuncios de mascotas
         * perdidas publicados por un determinado usuario.
         *
         * @param user el usuario que se encuentra autenticado en la aplicación.
         * @return el fragment creado e instanciado para la vista correspondiente.
         */
        fun newInstance(user: User): Fragment {
            val fragment = UserAdsFragment()
            val bundle = Bundle()
            bundle.putParcelable(USER, user)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var mUserAdsPresenter: UserAdsPresenter
    private lateinit var mUser: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_user_ads, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadExtras()
        init()
    }

    override fun onResume() {
        super.onResume()
        mUserAdsPresenter.getUserAds()
        mUserAdsPresenter.resume()
    }

    override fun onPause() {
        super.onPause()
        mUserAdsPresenter.pause()
    }

    override fun onStop() {
        super.onStop()
        mUserAdsPresenter.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mUserAdsPresenter.destroy()
    }

    override fun showUserAds(ads: List<Ad>) {
        userAdsSwipeRefreshLayout.isRefreshing = false
        userAdsRecyclerView.layoutManager = LinearLayoutManager(context)
        userAdsRecyclerView.adapter = AdsListAdapter(ads, mUser, fragmentManager) {
            val intent = Intent(context, AdDetailActivity::class.java)
            intent.putExtra(AdDetailActivity.USER, mUser)
            intent.putExtra(AdDetailActivity.AD, it)
            startActivity(intent)
        }
    }

    override fun onUserAdsEmpty() {
        SnackbarUtil.makeLong(fragmentUserAdsLayout, R.string.msg_no_user_ads)
    }

    override fun onUserDisabledAds() {
        SnackbarUtil.makeLong(fragmentUserAdsLayout, R.string.msg_disabled_user_ads)
    }

    override fun noInternetConnection() {
        SnackbarUtil.makeShort(fragmentUserAdsLayout, R.string.msg_no_internet_connection)
    }

    override fun serviceNotAvailable() {
        SnackbarUtil.makeLong(fragmentUserAdsLayout, R.string.msg_service_not_avabile)
    }

    private fun loadExtras() {
        with(arguments!!) {
            mUser = getParcelable(USER)!!
        }
    }

    private fun init() {
        (activity as AppCompatActivity).supportActionBar!!
                .setTitle(R.string.user_ads_fragment_toolbar_title)

        setProgressBarLayout(progressBarConstraintLayout)
        addViewsToHide(userAdsRecyclerView)

        mUserAdsPresenter = UserAdsPresenterImpl(ThreadExecutor.instance, MainThreadImpl.instance,
                this, context, ServiceGenerator.createService(context, AdService::class.java),
                mUser)

        userAdsSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        userAdsSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorSecondaryText)
        userAdsSwipeRefreshLayout.setOnRefreshListener { mUserAdsPresenter.getUserAds() }
    }
}
