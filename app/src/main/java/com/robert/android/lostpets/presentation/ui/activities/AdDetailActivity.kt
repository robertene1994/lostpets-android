package com.robert.android.lostpets.presentation.ui.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.InputFilter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MarkerOptions
import com.robert.android.lostpets.R
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.PetStatus
import com.robert.android.lostpets.domain.model.types.Sex
import com.robert.android.lostpets.network.ServiceGenerator
import com.robert.android.lostpets.presentation.ui.activities.base.AbstractActivity
import com.robert.android.lostpets.presentation.ui.adapters.AdsListAdapter
import com.robert.android.lostpets.presentation.ui.fragments.ScrollableMapFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_ad_detail.*
import kotlinx.android.synthetic.main.app_bar.*
import org.jetbrains.anko.intentFor

/**
 * Activity que extiende la clase AbstractActivity. Es el controlador que se encarga de manejar la
 * vista correspondiente al detalle de los anuncios de mascotas perdidas.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.ui.activities.base.AbstractActivity
 */
class AdDetailActivity : AbstractActivity(), OnMapReadyCallback,
        ScrollableMapFragment.OnTouchListener {

    companion object {
        const val USER = "AdDetailActivity::User"
        const val AD = "AdDetailActivity::Ad"
        const val CHAT_REQUEST_CODE = 1841
        const val CAMERA_POSITION_ZOOM = 15F
    }

    private lateinit var mUser: User
    private lateinit var mAd: Ad
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_detail)
        loadExtras()
        init()
    }

    override fun onMapReady(map: GoogleMap?) {
        mMap = map as GoogleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.setAllGesturesEnabled(true)

        val markerTitle = String.format(
                getString(R.string.map_marker_title_ad_detail), mAd.pet.name)

        val lastSpottedCoords = com.google.android.gms.maps.model.LatLng(
                mAd.lastSpottedCoords.latitude, mAd.lastSpottedCoords.longitude)
        mMap.addMarker(MarkerOptions().position(lastSpottedCoords)
                .title(markerTitle)).showInfoWindow()

        val cameraPosition = CameraPosition.Builder()
                .target(lastSpottedCoords).zoom(CAMERA_POSITION_ZOOM).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onTouch() {
        adDetailScrollView.requestDisallowInterceptTouchEvent(true)
    }

    private fun loadExtras() {
        with(intent) {
            mUser = getParcelableExtra(USER)
            mAd = getParcelableExtra(AD)
        }
    }

    private fun init() {
        toolbar.setTitle(R.string.ad_detail_activity_toolbar_title)
        setSupportActionBar(toolbar)

        val imgUrl = "${ServiceGenerator.API_BASE_URL}${mAd.photo}"
        Picasso.get().load(imgUrl).into(updateAdPetImageView)

        petStatusEditText.filters = petStatusEditText.filters + InputFilter.AllCaps()
        if (mAd.petStatus == PetStatus.FOUND) {
            petStatusEditText.setText(getString(R.string.ad_pet_status_found))
            petStatusEditText.setTextColor(
                    ContextCompat.getColor(this, R.color.colorSuccess))
            feelingPetStatusImageView.setImageResource(R.drawable.ic_smile)
        } else {
            petStatusEditText.setText(getString(R.string.ad_pet_status_lost))
            petStatusEditText.setTextColor(
                    ContextCompat.getColor(this, R.color.colorDanger))
            feelingPetStatusImageView.setImageResource(R.drawable.ic_sad)
        }

        codeEditText.setText(mAd.code)
        updateAdObservationsEditText.setText(mAd.observations)
        adDateEditText.setText(AdsListAdapter.formatAdLongDate(mAd.date))

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapFragment) as ScrollableMapFragment
        mapFragment.setOnTouchListener(this)
        mapFragment.getMapAsync(this)

        adRewardEditText.setText(
                String.format(getString(R.string.ad_reward), mAd.reward))

        adPetNameEditText.setText(mAd.pet.name)
        updateAdPetTypeEditText.setText(mAd.pet.type)
        updateAdPetRaceEditText.setText(mAd.pet.race)

        sexEditText.setText(
                if (mAd.pet.sex == Sex.MALE) getString(R.string.ad_detail_pet_sex_male)
                else getString(R.string.ad_detail_pet_sex_female))

        updateAdPetColourEditText.setText(mAd.pet.colour)
        updateAdPetMicrochipIdEditText.setText(mAd.pet.microchipId)

        firstNameEditText.setText(mAd.user.firstName)
        lastNameEditText.setText(mAd.user.lastName)
        emailEditText.setText(mAd.user.email)
        phoneEditText.setText(mAd.user.phone)

        sendEmailButton.isEnabled = mUser != mAd.user
        callButton.isEnabled = mUser != mAd.user
        chatButton.isEnabled = mUser != mAd.user

        sendEmailButton.setOnClickListener { onClickEmail() }
        callButton.setOnClickListener { onClickPhone() }
        chatButton.setOnClickListener { onClickChat() }
    }

    private fun onClickEmail() {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${mAd.user.email}"))
        if (intent.resolveActivity(packageManager) != null)
            startActivity(intent)
    }

    private fun onClickPhone() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${mAd.user.phone}"))
        if (intent.resolveActivity(packageManager) != null)
            startActivity(intent)
    }

    private fun onClickChat() {
        val intent = intentFor<MainActivity>(
                MainActivity.CHAT_FROM_OUTSIDE to MainActivity.CHAT_FROM_OUTSIDE,
                MainActivity.USER_TO_CHAT to mAd.user)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
