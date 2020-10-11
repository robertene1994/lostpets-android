package com.robert.android.lostpets.presentation.ui.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
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
import com.robert.android.lostpets.domain.model.types.AdStatus
import com.robert.android.lostpets.domain.model.types.Sex
import com.robert.android.lostpets.domain.model.types.PetStatus
import com.robert.android.lostpets.network.ServiceGenerator
import com.robert.android.lostpets.network.service.AdService
import com.robert.android.lostpets.presentation.presenters.AddAdPresenter
import com.robert.android.lostpets.presentation.presenters.impl.AddAdPresenterImpl
import com.robert.android.lostpets.presentation.ui.activities.MainActivity
import com.robert.android.lostpets.presentation.ui.adapters.AdsListAdapter
import com.robert.android.lostpets.presentation.ui.fragments.base.AbstractFragment
import com.robert.android.lostpets.presentation.ui.utils.KeyboardUtil
import com.robert.android.lostpets.presentation.ui.utils.SnackbarUtil
import com.robert.android.lostpets.threading.MainThreadImpl
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_ad.*
import kotlinx.android.synthetic.main.progress_bar.*
import java.io.File
import java.util.*

/**
 * Fragment que extiende la clase AbstractFragment e implementa la interfaz del callback del
 * presenter AddAdPresenter. Además, esta fragment implementa la interfaz
 * ScrollableMapFragment.OnTouchListener con el objetivo de interceptar los toques del mapa,
 * mientras el usuario desliza por los datos del anuncio que se publica. Es el controlador que se
 * encarga de manejar la vista correspondiente al proceso que permite publicar un nuevo anuncio de
 * mascota perdida.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.ui.fragments.base.AbstractFragment
 * @see com.robert.android.lostpets.presentation.presenters.AddAdPresenter.View
 * @see com.robert.android.lostpets.presentation.ui.fragments.ScrollableMapFragment.OnTouchListener
 */
class AddAdFragment : AbstractFragment(), AddAdPresenter.View, OnMapReadyCallback,
        ScrollableMapFragment.OnTouchListener, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, RadioGroup.OnCheckedChangeListener {

    companion object {
        private const val USER = "AddAdFragment::User"
        internal const val PICK_PET_IMAGE = 1100
        private const val REQUEST_LOCATION_PERMISSIONS = 1101
        private const val DIALOG_DATE_PICKER = "AddAdFragment:DialogDatePicker"
        private const val DIALOG_TIME_PICKER = "AddAdFragment:DialogTimePicker"
        private const val REQUEST_DATE_PICKER = 1102
        private const val REQUEST_TIME_PICKER = 1103

        /**
         * Método que instancia el fragment para la vista asociada al proceso que permite publicar
         * un nuevo anuncio de mascota perdida.
         *
         * @param user el usuario que se encuentra autenticado en la aplicación.
         * @return el fragment creado e instanciado para la vista correspondiente.
         */
        fun newInstance(user: User): Fragment {
            val fragment = AddAdFragment()
            val bundle = Bundle()
            bundle.putParcelable(USER, user)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var mAddAdPresenter: AddAdPresenter
    private lateinit var mUser: User
    private lateinit var mMap: GoogleMap
    private lateinit var mCalendar: Calendar
    private var mSex: Sex? = null
    private var mMarker: Marker? = null
    private var mFile: File? = null
    private var mUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_add_ad, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadExtras()
        init()
    }

    override fun onResume() {
        super.onResume()
        mAddAdPresenter.resume()
    }

    override fun onPause() {
        super.onPause()
        mAddAdPresenter.pause()
    }

    override fun onStop() {
        super.onStop()
        mAddAdPresenter.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAddAdPresenter.destroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PET_IMAGE && resultCode == RESULT_OK && data != null) {
            mUri = data.data
            Picasso.get().load(mUri).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE).resize(750, 0)
                    .centerInside().into(addAdPetImageView)
            mAddAdPresenter.processImage(data.data!!)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
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

        mMap.setOnMapLongClickListener {
            if (mMarker != null)
                mMarker!!.position = it
            else
                mMarker = mMap.addMarker(MarkerOptions().position(it)
                        .title(getString(R.string.map_marker_title_add_ad)).draggable(true))
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, month)
        mCalendar.set(Calendar.DAY_OF_MONTH, day)
        addAdDateEditText.setText(AdsListAdapter.formatAdShortDate(mCalendar.time))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        mCalendar.set(Calendar.MINUTE, minute)
        addAdTimeEditText.setText(AdsListAdapter.formatAdShortTime(mCalendar.time))
    }

    override fun onTouch() {
        addAdScrollView.requestDisallowInterceptTouchEvent(true)
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.addAdMaleSexRadioButton -> mSex = Sex.MALE
            R.id.addAdFemaleSexRadioButton -> mSex = Sex.FEMALE
        }
    }

    override fun onProcessedImage(file: File) {
        mFile = file
        SnackbarUtil.makeShort(fragmentAddAdLayout, R.string.msg_image_is_processed)
    }

    override fun onAdAdded(adAdded: Boolean) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(MainActivity.NEW_AD, MainActivity.NEW_AD)
        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun noInternetConnection() {
        SnackbarUtil.makeShort(fragmentAddAdLayout, R.string.msg_no_internet_connection)
    }

    override fun serviceNotAvailable() {
        SnackbarUtil.makeLong(fragmentAddAdLayout, R.string.msg_service_not_avabile)
    }

    private fun loadExtras() {
        with(arguments!!) {
            mUser = getParcelable(USER)!!
        }
    }

    private fun init() {
        (activity as AppCompatActivity).supportActionBar!!
                .setTitle(R.string.add_ad_fragment_toolbar_title)

        setProgressBarLayout(progressBarConstraintLayout)
        addViewsToHide(addAdScrollView)

        mCalendar = Calendar.getInstance()
        mAddAdPresenter = AddAdPresenterImpl(ThreadExecutor.instance, MainThreadImpl.instance,
                this, context, ServiceGenerator.createService(context, AdService::class.java))

        val mapFragment = childFragmentManager
                .findFragmentById(R.id.addAdMapFragment) as ScrollableMapFragment
        mapFragment.setOnTouchListener(this)
        mapFragment.getMapAsync(this)

        addAdMaleSexRadioButton.text = getString(R.string.add_ad_pet_sex_male)
        addAdFemaleSexRadioButton.text = getString(R.string.add_ad_pet_sex_female)
        addAdPetSexRadioGroup.setOnCheckedChangeListener(this)

        addAdPetImageView.setOnClickListener { onClickPetImage() }
        addAdSelectDateButton.setOnClickListener { onClickSelectDate() }
        addAdSelectTimeButton.setOnClickListener { onClickSelectTime() }
        adAddButton.setOnClickListener { onClickAddAd() }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSIONS)
        } else {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
            val locationManager
                    = context.getSystemService(LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

            val latLng =
                    com.google.android.gms.maps.model.LatLng(location.latitude, location.longitude)
            val cameraPosition = CameraPosition.Builder()
                    .target(latLng).zoom(14F).build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    private fun onClickPetImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        activity!!.startActivityForResult(
                Intent.createChooser(intent, getString(R.string.choose_pet_image)), PICK_PET_IMAGE)
    }

    private fun onClickSelectDate() {
        val datePickerFragment= DatePickerFragment.newInstance()
        datePickerFragment.setTargetFragment(this, REQUEST_DATE_PICKER)
        datePickerFragment.show(fragmentManager, DIALOG_DATE_PICKER)
    }

    private fun onClickSelectTime() {
        val timePickerFragment= TimePickerFragment.newInstance()
        timePickerFragment.setTargetFragment(this, REQUEST_TIME_PICKER)
        timePickerFragment.show(fragmentManager, DIALOG_TIME_PICKER)
    }

    private fun onClickAddAd() {
        KeyboardUtil.hideKeyboard(context, fragmentAddAdLayout)

        val reward = addAdRewardEditText.text.toString().trim()
        val date = addAdDateEditText.text.toString().trim()
        val time = addAdTimeEditText.text.toString().trim()
        val observations = addAdObservationsEditText.text.toString().trim()
        val name = addAdPetNameEditText.text.toString().trim()
        val type = addAdPetTypeEditText.text.toString().trim()
        val race = addAdPetRaceEditText.text.toString().trim()
        val colour = addAdPetColourEditText.text.toString().trim()
        val microchipId = addAdPetMicrochipIdEditText.text.toString().trim()

        val validImage = validateImage()
        val validReward = validateReward(reward)
        val validDate = validateDate(date)
        val validTime = validateTime(time)
        val validLastSpottedLocation = validateLastSpottedLocation()
        val validObservations = validateObservations(observations)
        val validName = validateName(name)
        val validType = validateType(type)
        val validRace = validateRace(race)
        val validSex = validateSex()
        val validColour = validateColour(colour)
        val validMicrochipId = validateMicrochipId(microchipId)

        if (validImage && validReward && validDate && validTime && validLastSpottedLocation &&
                validObservations && validName && validType && validRace && validSex &&
                validColour && validMicrochipId) {
            val pet = Pet(name, type, race, mSex!!, colour, microchipId)
            val ad = Ad(null, null, mCalendar.time,
                    AdStatus.ENABLED, PetStatus.LOST, reward.toDouble(),
                    LatLng(mMarker!!.position.latitude, mMarker!!.position.longitude),
                    pet, observations, null, mUser)
            mAddAdPresenter.addAd(ad, mFile!!)
        }
    }

    private fun validateImage(): Boolean {
        return when {
            mUri == null -> {
                SnackbarUtil.makeShort(fragmentAddAdLayout, R.string.msg_image_required)
                false
            }
            mFile == null -> {
                SnackbarUtil.makeLong(fragmentAddAdLayout, R.string.msg_image_is_processing)
                false
            }
            else -> true
        }
    }

    private fun validateReward(reward: String): Boolean {
        return when {
            reward.isEmpty() -> {
                addAdRewardTextInputLayout.error = getString(R.string.msg_reward_required)
                false
            }
            reward == "." -> {
                addAdRewardTextInputLayout.error = getString(R.string.msg_reward_invalid)
                false
            }
            else -> {
                addAdRewardTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateDate(date: String): Boolean {
        return when {
            date.isEmpty() -> {
                addAdDateTextInputLayout.error = getString(R.string.msg_date_required)
                false
            }
            else -> {
                addAdDateTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateTime(time: String): Boolean {
        return when {
            time.isEmpty() -> {
                addAdTimeTextInputLayout.error = getString(R.string.msg_time_required)
                false
            }
            mCalendar.time.after(Date()) -> {
                addAdTimeTextInputLayout.error = getString(R.string.msg_date_and_time_before)
                false
            }
            else -> {
                addAdTimeTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateLastSpottedLocation(): Boolean {
        return when (mMarker){
            null -> {
                SnackbarUtil.makeShort(fragmentAddAdLayout, R.string
                        .msg_last_spotted_location_required)
                false
            }
            else -> true
        }
    }

    private fun validateObservations(observations: String): Boolean {
        return when {
            observations.isEmpty() -> {
                addAdObservationsTextInputLayout.error = getString(R.string.msg_remarks_required)
                false
            }
            else -> {
                addAdObservationsTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateName(name: String): Boolean {
        return when {
            name.isEmpty() -> {
                addAdPetNameTextInputLayout.error = getString(R.string.msg_name_required)
                false
            }
            else -> {
                addAdPetNameTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateType(type: String): Boolean {
        return when {
            type.isEmpty() -> {
                addAdPetTypeTextInputLayout.error = getString(R.string.msg_type_required)
                false
            }
            else -> {
                addAdPetTypeTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateRace(race: String): Boolean {
        return when {
            race.isEmpty() -> {
                addAdPetRaceTextInputLayout.error = getString(R.string.msg_race_required)
                false
            }
            else -> {
                addAdPetRaceTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateSex(): Boolean {
        return when (mSex){
            null -> {
                SnackbarUtil.makeShort(fragmentAddAdLayout, R.string.msg_sex_required)
                false
            }
            else -> true
        }
    }

    private fun validateColour(colour: String): Boolean {
        return when {
            colour.isEmpty() -> {
                addAdPetColourTextInputLayout.error = getString(R.string.msg_colour_required)
                false
            }
            else -> {
                addAdPetColourTextInputLayout.error = null
                true
            }
        }
    }

    private fun validateMicrochipId(microchipId: String): Boolean {
        return when {
            microchipId.isEmpty() -> {
                addAdPetMicrochipIdTextInputLayout.error = getString(R.string.msg_microchip_id_required)
                false
            }
            else -> {
                addAdPetMicrochipIdTextInputLayout.error = null
                true
            }
        }
    }
}