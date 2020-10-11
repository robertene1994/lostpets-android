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
import com.robert.android.lostpets.presentation.presenters.AdsPresenter
import com.robert.android.lostpets.presentation.presenters.impl.AdsPresenterImpl
import com.robert.android.lostpets.presentation.ui.activities.AdDetailActivity
import com.robert.android.lostpets.presentation.ui.adapters.AdsListAdapter
import com.robert.android.lostpets.presentation.ui.fragments.base.AbstractFragment
import com.robert.android.lostpets.presentation.ui.utils.SnackbarUtil
import com.robert.android.lostpets.threading.MainThreadImpl
import kotlinx.android.synthetic.main.fragment_ads.*
import kotlinx.android.synthetic.main.progress_bar.*

/**
 * Fragment que extiende la clase AbstractFragment e implementa la interfaz del callback del
 * presenter AdsPresenter. Es el controlador que se encarga de manejar la vista correspondiente a
 * todos los anuncios de mascotas perdidas del sistema.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.ui.fragments.base.AbstractFragment
 * @see com.robert.android.lostpets.presentation.presenters.AdsPresenter.View
 */
class AdsFragment : AbstractFragment(), AdsPresenter.View {

    companion object {
        private const val USER = "AdsFragment::User"

        /**
         * Método que instancia el fragment para la vista asociada a todos los anuncios de
         * mascotas perdidas del sistema.
         *
         * @param user el usuario que se encuentra autenticado en la aplicación.
         * @return el fragment creado e instanciado para la vista correspondiente.
         */
        fun newInstance(user: User): Fragment {
            val fragment = AdsFragment()
            val bundle = Bundle()
            bundle.putParcelable(USER, user)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var mAdsPresenter: AdsPresenter
    private lateinit var mUser: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ads, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadExtras()
        init()
    }

    override fun onResume() {
        super.onResume()
        mAdsPresenter.getAds()
        mAdsPresenter.resume()
    }

    override fun onPause() {
        super.onPause()
        mAdsPresenter.pause()
    }

    override fun onStop() {
        super.onStop()
        mAdsPresenter.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdsPresenter.destroy()
    }

    override fun showAds(ads: List<Ad>) {
        adsSwipeRefreshLayout.isRefreshing = false
        adsRecyclerView.layoutManager = LinearLayoutManager(context)
        adsRecyclerView.adapter = AdsListAdapter(ads, mUser, fragmentManager) {
            val intent = Intent(context, AdDetailActivity::class.java)
            intent.putExtra(AdDetailActivity.USER, mUser)
            intent.putExtra(AdDetailActivity.AD, it)
            startActivityForResult(intent, AdDetailActivity.CHAT_REQUEST_CODE)
        }
    }

    override fun onAdsEmpty() {
        SnackbarUtil.makeLong(fragmentAdsLayout, R.string.msg_no_ads)
    }

    override fun noInternetConnection() {
        SnackbarUtil.makeShort(fragmentAdsLayout, R.string.msg_no_internet_connection)
    }

    override fun serviceNotAvailable() {
        SnackbarUtil.makeLong(fragmentAdsLayout, R.string.msg_service_not_avabile)
    }

    private fun loadExtras() {
        with(arguments!!) {
            mUser = getParcelable(USER)!!
        }
    }

    private fun init() {
        (activity as AppCompatActivity).supportActionBar!!
                .setTitle(R.string.ads_fragment_toolbar_title)

        setProgressBarLayout(progressBarConstraintLayout)
        addViewsToHide(adsRecyclerView)

        mAdsPresenter = AdsPresenterImpl(ThreadExecutor.instance, MainThreadImpl.instance,
                this, context, ServiceGenerator.createService(context, AdService::class.java))

        adsSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        adsSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorSecondaryText)
        adsSwipeRefreshLayout.setOnRefreshListener { mAdsPresenter.getAds() }
    }
}
