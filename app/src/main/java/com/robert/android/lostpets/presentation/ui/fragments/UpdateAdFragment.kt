package com.robert.android.lostpets.presentation.ui.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.RadioGroup
import android.widget.TimePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.robert.android.lostpets.R
import com.robert.android.lostpets.domain.executor.impl.ThreadExecutor
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.domain.model.LatLng
import com.robert.android.lostpets.domain.model.Pet
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.PetStatus
import com.robert.android.lostpets.domain.model.types.Sex
import com.robert.android.lostpets.network.ServiceGenerator
import com.robert.android.lostpets.network.service.AdService
import com.robert.android.lostpets.presentation.presenters.UpdateAdPresenter
import com.robert.android.lostpets.presentation.presenters.impl.UpdateAdPresenterImpl
import com.robert.android.lostpets.presentation.ui.activities.MainActivity
import com.robert.android.lostpets.presentation.ui.adapters.AdsListAdapter
import com.robert.android.lostpets.presentation.ui.fragments.base.AbstractFragment
import com.robert.android.lostpets.presentation.ui.utils.KeyboardUtil
import com.robert.android.lostpets.presentation.ui.utils.SnackbarUtil
import com.robert.android.lostpets.threading.MainThreadImpl
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.io.File
import java.util.Calendar
import java.util.Date
import kotlinx.android.synthetic.main.fragment_update_ad.*
import kotlinx.android.synthetic.main.progress_bar.*

/**
 * Fragment que extiende la clase AbstractFragment e implementa la interfaz del callback del
 * presenter UpdateAdPresenter. Además, esta fragment implementa la interfaz
 * ScrollableMapFragment.OnTouchListener con el objetivo de interceptar los toques del mapa,
 * mientras el usuario desliza por los datos del anuncio que se modifica. Es el controlador que se
 * encarga de manejar la vista correspondiente al proceso que permite modificar un anuncio
 * existente perteneciente a una mascota perdida.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.ui.fragments.base.AbstractFragment
 * @see com.robert.android.lostpets.presentation.presenters.UpdateAdPresenter.View
 * @see com.robert.android.lostpets.presentation.ui.fragments.ScrollableMapFragment.OnTouchListener
 */
class UpdateAdFragment : AbstractFragment(), UpdateAdPresenter.View, OnMapReadyCallback,
        ScrollableMapFragment.OnTouchListener, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, RadioGroup.OnCheckedChangeListener {

    companion object {
        private const val USER = "UpdateAdFragment::User"
        private const val AD = "UpdateAdFragment::Ad"
        internal const val PICK_PET_IMAGE = 1100
        private const val REQUEST_LOCATION_PERMISSIONS = 1101
        private const val DIALOG_DATE_PICKER = "UpdateAdFragment:DialogDatePicker"
        private const val DIALOG_TIME_PICKER = "UpdateAdFragment:DialogTimePicker"
        private const val REQUEST_DATE_PICKER = 1102
        private const val REQUEST_TIME_PICKER = 1103

        /**
         * Método que instancia el fragment para la vista asociada al proceso que permite modificar
         * un anuncio existente perteneciente a una mascota perdida.
         *
         * @param user el usuario que se encuentra autenticado en la aplicación.
         * @param ad el anuncio de la mascota perdida que se desea modificar.
         * @return el fragment creado e instanciado para la vista correspondiente.
         */
        fun newInstance(user: User, ad: Ad): Fragment {
            val fragment = UpdateAdFragment()
            val bundle = Bundle()
            bundle.putParcelable(USER, user)
            bundle.putParcelable(AD, ad)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var mUpdateAdPresenter: UpdateAdPresenter
    private lateinit var mUser: User
    private lateinit var mAd: Ad

    private lateinit var mMap: GoogleMap
    private lateinit var mCalendar: Calendar
    private var mPetStatus: PetStatus? = null
    private var mSex: Sex? = null
    private var mMarker: Marker? = null
    private var mFile: File? = null
    private var mUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_update_ad, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadExtras()
        init()
    }

    override fun onResume() {
        super.onResume()
        mUpdateAdPresenter.resume()
    }

    override fun onPause() {
        super.onPause()
        mUpdateAdPresenter.pause()
    }

    override fun onStop() {
        super.onStop()
        mUpdateAdPresenter.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mUpdateAdPresenter.destroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PET_IMAGE && resultCode == RESULT_OK && data != null) {
            mUri = data.data
            Picasso.get().load(mUri)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).resize(750, 0)
                    .centerInside().into(updateAdPetImageView)
            mUpdateAdPresenter.processImage(data.data!!)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermission()
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        mMap = map!!
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        checkLocationPermission()

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isIndoorLevelPickerEnabled = true
            isMapToolbarEnabled = true
            setAllGesturesEnabled(true)
        }

        val markerTitle = String.format(
                getString(R.string.map_marker_title_ad_detail), mAd.pet.name)

        val lastSpottedCoords = com.google.android.gms.maps.model.LatLng(
                mAd.lastSpottedCoords.latitude, mAd.lastSpottedCoords.longitude)
        mMarker = mMap.addMarker(MarkerOptions().position(lastSpottedCoords).title(markerTitle))
        mMarker!!.showInfoWindow()

        val cameraPosition = CameraPosition.Builder()
                .target(lastSpottedCoords).zoom(15F).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        mMap.setOnMapLongClickListener {
            if (mMarker != null)
                mMarker!!.position = it
            else
                mMarker = mMap.addMarker(MarkerOptions().position(it)
                        .title(getString(R.string.map_marker_title_update_ad)).draggable(true))
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, month)
        mCalendar.set(Calendar.DAY_OF_MONTH, day)
        updateAdDateEditText.setText(AdsListAdapter.formatAdShortDate(mCalendar.time))
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        mCalendar.set(Calendar.MINUTE, minute)
        updateAdTimeEditText.setText(AdsListAdapter.formatAdShortTime(mCalendar.time))
    }

    override fun onTouch() {
        updateAdScrollView.requestDisallowInterceptTouchEvent(true)
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (group!!.id) {
            R.id.updateAdPetStatusRadioGroup -> {
                when (checkedId) {
                    R.id.updateAdPetStatusFoundRadioButton -> mPetStatus = PetStatus.FOUND
                    R.id.updateAdPetStatusLostRadioButton -> mPetStatus = PetStatus.LOST
                }
            }
            R.id.updateAdPetSexRadioGroup -> {
                when (checkedId) {
                    R.id.updateAdMaleSexRadioButton -> mSex = Sex.MALE
                    R.id.updateAdFemaleSexRadioButton -> mSex = Sex.FEMALE
                }
            }
        }
    }

    override fun onProcessedImage(file: File) {
        mFile = file
        SnackbarUtil.makeShort(fragmentUpdateAdLayout, R.string.msg_image_is_processed)
    }

    override fun onAdUpdated(adUpdated: Boolean) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(MainActivity.UPDATE_AD, MainActivity.UPDATE_AD)
        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun noInternetConnection() {
        SnackbarUtil.makeShort(fragmentUpdateAdLayout, R.string.msg_no_internet_connection)
    }

    override fun serviceNotAvailable() {
        SnackbarUtil.makeLong(fragmentUpdateAdLayout, R.string.msg_service_not_avabile)
    }

    private fun loadExtras() {
        with(arguments!!) {
            mUser = getParcelable(USER)!!
            mAd = getParcelable(AD)!!
        }
    }

    private fun init() {
        (activity as AppCompatActivity).supportActionBar!!
                .setTitle(R.string.update_ad_fragment_toolbar_title)

        setProgressBarLayout(progressBarConstraintLayout)
        addViewsToHide(updateAdScrollView)

        KeyboardUtil.hideKeyboard(context, fragmentUpdateAdLayout)

        val imgUrl = "${ServiceGenerator.API_BASE_URL}${mAd.photo}"
        Picasso.get().load(imgUrl).into(updateAdPetImageView)

        mPetStatus = mAd.petStatus
        when (mPetStatus) {
            PetStatus.FOUND -> updateAdPetStatusFoundRadioButton.isChecked = true
            PetStatus.LOST -> updateAdPetStatusLostRadioButton.isChecked = true
        }

        updateAdRewardEditText.setText(mAd.reward.toString())
        updateAdDateEditText.setText(AdsListAdapter.formatAdShortDate(mAd.date))
        updateAdTimeEditText.setText(AdsListAdapter.formatAdShortTime(mAd.date))
        updateAdObservationsEditText.setText(mAd.observations)
        updateAdPetNameEditText.setText(mAd.pet.name)
        updateAdPetTypeEditText.setText(mAd.pet.type)
        updateAdPetRaceEditText.setText(mAd.pet.race)

        mSex = mAd.pet.sex
        when (mSex) {
            Sex.MALE -> updateAdMaleSexRadioButton.isChecked = true
            Sex.FEMALE -> updateAdFemaleSexRadioButton.isChecked = true
        }

        updateAdPetColourEditText.setText(mAd.pet.colour)
        updateAdPetMicrochipIdEditText.setText(mAd.pet.microchipId)

        mCalendar = Calendar.getInstance()
        mUpdateAdPresenter = UpdateAdPresenterImpl(ThreadExecutor.instance, MainThreadImpl.instance,
                this, context, ServiceGenerator.createService(context, AdService::class.java))

        val mapFragment = childFragmentManager
                .findFragmentById(R.id.updateAdMapFragment) as ScrollableMapFragment
        mapFragment.setOnTouchListener(this)
        mapFragment.getMapAsync(this)

        updateAdPetStatusFoundRadioButton.text = getString(R.string.update_ad_pet_status_found)
        updateAdPetStatusLostRadioButton.text = getString(R.string.update_ad_pet_status_lost)
        updateAdPetStatusRadioGroup.setOnCheckedChangeListener(this)

        updateAdMaleSexRadioButton.text = getString(R.string.update_ad_pet_sex_male)
        updateAdFemaleSexRadioButton.text = getString(R.string.update_ad_pet_sex_female)
        updateAdPetSexRadioGroup.setOnCheckedChangeListener(this)

        updateAdPetImageView.setOnClickListener { onClickPetImage() }
        updateAdSelectDateButton.setOnClickListener { onClickSelectDate() }
        updateAdSelectTimeButton.setOnClickListener { onClickSelectTime() }
        updateAdButton.setOnClickListener { onClickUpdateAd() }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSIONS)
        } else {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
        }
    }

    private fun onClickPetImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        activity!!.startActivityForResult(
                Intent.createChooser(intent, getString(R.string.choose_pet_image)), PICK_PET_IMAGE)
    }

    private fun onClickSelectDate() {
        val datePickerFragment = DatePickerFragment.newInstance()
        datePickerFragment.setTargetFragment(this, REQUEST_DATE_PICKER)
        datePickerFragment.show(fragmentManager, DIALOG_DATE_PICKER)
    }

    private fun onClickSelectTime() {
        val timePickerFragment = TimePickerFragment.newInstance()
        timePickerFragment.setTargetFragment(this, REQUEST_TIME_PICKER)
        timePickerFragment.show(fragmentManager, DIALOG_TIME_PICKER)
    }

    private fun onClickUpdateAd() {
        KeyboardUtil.hideKeyboard(context, fragmentUpdateAdLayout)

        val id = mAd.id
        val code = mAd.code
        val photo = mAd.photo
        val adStatus = mAd.adStatus
        val reward = updateAdRewardEditText.text.toString().trim()
        val date = updateAdDateEditText.text.toString().trim()
        val time = updateAdTimeEditText.text.toString().trim()
        val observations = updateAdObservationsEditText.text.toString().trim()
        val name = updateAdPetNameEditText.text.toString().trim()
        val type = updateAdPetTypeEditText.text.toString().trim()
        val race = updateAdPetRaceEditText.text.toString().trim()
        val colour = updateAdPetColourEditText.text.toString().trim()
        val microchipId = updateAdPetMicrochipIdEditText.text.toString().trim()

        val validImage = validateImage()
        val validPetStatus = validatePetStatus()
        val validReward = validateReward(reward)
        val validDate = validateDate(date)
        val validTime = validateTime(time)
        val validObservations = validateObservations(observations)
        val validLastSpottedLocation = validateLastSpottedLocation()
        val validName = validateName(name)
        val validType = validateType(type)
        val validRace = validateRace(race)
        val validSex = validateSex()
        val validColour = validateColour(colour)
        val validMicrochipId = validateMicrochipId(microchipId)

        if (validImage && validPetStatus && validReward && validDate && validTime &&
                validObservations && validLastSpottedLocation && validName && validType &&
                validRace && validSex && validColour && validMicrochipId) {
            val pet = Pet(name, type, race, mSex!!, colour, microchipId)
            val ad = Ad(id, code, mCalendar.time, adStatus, mPetStatus!!, reward.toDouble(),
                    LatLng(mMarker!!.position.latitude, mMarker!!.position.longitude), pet,
                    observations, photo, mUser)
            mUpdateAdPresenter.updateAd(ad, mFile)
        }
    }

    private fun validateImage(): Boolean {
        return when {
            mAd.photo == null && mUri == null -> {
                SnackbarUtil.makeShort(fragmentUpdateAdLayout, R.string.msg_image_required)
                false
            }
            mAd.photo == null && mFile == null -> {
                SnackbarUtil.makeLong(fragmentUpdateAdLayout, R.string.msg_image_is_processing)
                false
            }
            else -> true
        }
    }

    private fun validatePetStatus(): Boolean {
        return when (mPetStatus) {
            null -> {
                SnackbarUtil.makeShort(fragmentUpdateAdLayout, R.string.msg_status_required)
                false
            }
            else -> true
        }
    }

    private fun validateReward(reward: String): Boolean {
        return when {
            reward.isEmpty() -> {
                updateAdRewardTextInputLayout.error = getString(R.string.msg_reward_required)
                false
            }
            reward == "." -> {
                updateAdRewardTextInputLayout.error = getString(R.string.msg_reward_invalid)
                false
            }
            else -> {
                updateAdRewardTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateDate(date: String): Boolean {
        return when {
            date.isEmpty() -> {
                updateAdDateTextInputLayout.error = getString(R.string.msg_date_required)
                false
            }
            else -> {
                updateAdDateTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateTime(time: String): Boolean {
        return when {
            time.isEmpty() -> {
                updateAdTimeTextInputLayout.error = getString(R.string.msg_time_required)
                false
            }
            mCalendar.time.after(Date()) -> {
                updateAdTimeTextInputLayout.error = getString(R.string.msg_date_and_time_before)
                false
            }
            else -> {
                updateAdTimeTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateObservations(observations: String): Boolean {
        return when {
            observations.isEmpty() -> {
                updateAdObservationsTextInputLayout.error = getString(R.string.msg_remarks_required)
                false
            }
            else -> {
                updateAdObservationsTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateLastSpottedLocation(): Boolean {
        return when (mMarker) {
            null -> {
                SnackbarUtil.makeShort(fragmentUpdateAdLayout, R.string
                        .msg_last_spotted_location_required)
                false
            }
            else -> true
        }
    }

    private fun validateName(name: String): Boolean {
        return when {
            name.isEmpty() -> {
                updateAdPetNameTextInputLayout.error = getString(R.string.msg_name_required)
                false
            }
            else -> {
                updateAdPetNameTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateType(type: String): Boolean {
        return when {
            type.isEmpty() -> {
                updateAdPetTypeTextInputLayout.error = getString(R.string.msg_type_required)
                false
            }
            else -> {
                updateAdPetTypeTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateRace(race: String): Boolean {
        return when {
            race.isEmpty() -> {
                updateAdPetRaceTextInputLayout.error = getString(R.string.msg_race_required)
                false
            }
            else -> {
                updateAdPetRaceTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateSex(): Boolean {
        return when (mSex) {
            null -> {
                SnackbarUtil.makeShort(fragmentUpdateAdLayout, R.string.msg_sex_required)
                false
            }
            else -> true
        }
    }

    private fun validateColour(colour: String): Boolean {
        return when {
            colour.isEmpty() -> {
                updateAdPetColourTextInputLayout.error = getString(R.string.msg_colour_required)
                false
            }
            else -> {
                updateAdPetColourTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateMicrochipId(microchipId: String): Boolean {
        return when {
            microchipId.isEmpty() -> {
                updateAdPetMicrochipIdTextInputLayout.error = getString(R.string.msg_microchip_id_required)
                false
            }
            else -> {
                updateAdPetMicrochipIdTextInputLayout.error = null
                true
            }
        }
    }
}
