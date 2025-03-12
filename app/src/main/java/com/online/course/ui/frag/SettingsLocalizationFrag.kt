package com.online.course.ui.frag

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import com.google.gson.Gson
import com.online.course.R
import com.online.course.databinding.FragSettingsLocalizationBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.adapter.ItemSpinnerAdapter
import com.online.course.manager.db.AppDb
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.SettingsLocalizationPresenterImpl
import java.util.*


class SettingsLocalizationFrag : NetworkObserverFragment(), SettingsFrag.SaveCallback,
    AdapterView.OnItemSelectedListener, View.OnClickListener, TextWatcher {

    private lateinit var mBinding: FragSettingsLocalizationBinding
    private lateinit var mPresenter: Presenter.SettingsLocalizationPresenter
    private lateinit var mCountries: List<Region>
    private lateinit var mProvinces: List<Region>
    private lateinit var mCities: List<Region>
    private lateinit var mDistricts: List<Region>
    private lateinit var mTimeZones: List<String>
    private var mSelectedTimeZone: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragSettingsLocalizationBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun enableDisableBtn() {
        val selectedCountry = mBinding.settingsLocalizationCountrySpinner.selectedItem
        val selectedProvince = mBinding.settingsLocalizationProvinceSpinner.selectedItem
        val selectedCity = mBinding.settingsLocalizationCitySpinner.selectedItem
        val selectedDistrict = mBinding.settingsLocalizationDistrictSpinner.selectedItem

        val address = mBinding.settingsLocalizationAddressTv.text.toString()

        val enable = mSelectedTimeZone != null && selectedCountry != null &&
                selectedProvince != null && selectedCity != null && selectedDistrict != null &&
                address.isNotEmpty()

        (parentFragment as SettingsFrag).changeSaveBtnEnable(enable)
    }

    private fun init() {
        val user = App.loggedInUser!!

        mPresenter = SettingsLocalizationPresenterImpl(this)
        mPresenter.getTimeZones()
        mPresenter.getCountries()

        mBinding.settingsLocalizationCountrySpinner.onItemSelectedListener = this
        mBinding.settingsLocalizationProvinceSpinner.onItemSelectedListener = this
        mBinding.settingsLocalizationCitySpinner.onItemSelectedListener = this
        mBinding.settingsLocalizationDistrictSpinner.onItemSelectedListener = this

        user.countryId?.let {
            mPresenter.getProvinces(user.countryId!!)
        }

        user.provinceId?.let {
            mPresenter.getCities(user.provinceId!!)
        }

        user.cityId?.let {
            mPresenter.getDistricts(user.cityId!!)
        }

        mBinding.settingsLocalizationAddressTv.setText(user.address)
        mBinding.settingsLocalizationAddressTv.addTextChangedListener(this)
    }

    override fun onSaveClicked() {
        val selectedCountry = mBinding.settingsLocalizationCountrySpinner.selectedItem
        val selectedProvince = mBinding.settingsLocalizationProvinceSpinner.selectedItem
        val selectedCity = mBinding.settingsLocalizationCitySpinner.selectedItem
        val selectedDistrict = mBinding.settingsLocalizationDistrictSpinner.selectedItem

        if (mSelectedTimeZone == null || selectedCountry == null || selectedProvince == null ||
            selectedCity == null || selectedDistrict == null
        ) {
            return
        }

        val address = mBinding.settingsLocalizationAddressTv.text.toString()
        if (address.isEmpty()) {
            mBinding.settingsLocalizationAddressTv.error = ""
            return
        }

        val changeSettings = UserChangeLocalization()
        changeSettings.timeZone = mSelectedTimeZone!!
        changeSettings.countryId =
            mCountries[mBinding.settingsLocalizationCountrySpinner.selectedItemPosition].id
        changeSettings.provinceId =
            mProvinces[mBinding.settingsLocalizationProvinceSpinner.selectedItemPosition].id
        changeSettings.cityId =
            mCities[mBinding.settingsLocalizationCitySpinner.selectedItemPosition].id
        changeSettings.districtId =
            mDistricts[mBinding.settingsLocalizationDistrictSpinner.selectedItemPosition].id
        changeSettings.address = address

        mPresenter.changeProfileSettings(changeSettings)
    }

    override fun initTab() {
        (parentFragment as SettingsFrag).changeSaveBtnEnable(false)
        (parentFragment as SettingsFrag).changeSaveBtnVisibility(true)
    }

    fun onSettingsChanged(
        response: BaseResponse,
        changeSettings: UserChangeLocalization
    ) {
        if (context == null) return

        if (response.isSuccessful) {
            val loggedInUser = App.loggedInUser!!
            loggedInUser.timeZone = changeSettings.timeZone
            loggedInUser.countryId = changeSettings.countryId
            loggedInUser.provinceId = changeSettings.provinceId
            loggedInUser.cityId = changeSettings.cityId
            loggedInUser.districtId = changeSettings.districtId
            loggedInUser.address = changeSettings.address
            App.loggedInUser = loggedInUser
            App.saveToLocal(
                Gson().toJson(loggedInUser, UserProfile::class.java),
                requireContext(),
                AppDb.DataType.USER
            )

            ToastMaker.show(
                requireContext(),
                getString(R.string.success),
                response.message,
                ToastMaker.Type.SUCCESS
            )
        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun onTimeZonesReceived(timeZones: List<String>) {
            mSelectedTimeZone = App.loggedInUser!!.timeZone

        for (timeZone in timeZones) {
            if (mSelectedTimeZone == timeZone || timeZone.lowercase() == TimeZone.getDefault().id.lowercase()) {
                mBinding.settingsLocalizationTimeZoneBtn.text = timeZone
                mSelectedTimeZone = timeZone
                break
            }
        }

        mBinding.settingsLocalizationTimeZoneBtn.setOnClickListener(this)
        mTimeZones = timeZones

        enableDisableBtn()

//        val adapter = ItemSpinnerAdapter(requireContext(), timeZones)
//        mBinding.settingsLocalizationTimeZoneSpinner.adapter = adapter
//        mBinding.settingsLocalizationTimeZoneSpinner.setSelection(selected)
    }


    fun onCountriesReceived(countries: List<Region>) {
        mCountries = countries
        setAdapter(
            mBinding.settingsLocalizationCountrySpinner,
            countries,
            App.loggedInUser!!.countryId
        )
    }

    fun onProvincesReceived(provinces: List<Region>) {
        mProvinces = provinces
        setAdapter(
            mBinding.settingsLocalizationProvinceSpinner,
            provinces,
            App.loggedInUser!!.provinceId
        )
    }

    fun onCitiesReceived(cities: List<Region>) {
        mCities = cities
        setAdapter(mBinding.settingsLocalizationCitySpinner, cities, App.loggedInUser!!.cityId)
    }

    fun onDistrictsReceived(districts: List<Region>) {
        mDistricts = districts
        setAdapter(
            mBinding.settingsLocalizationDistrictSpinner,
            districts,
            App.loggedInUser!!.districtId
        )
    }

    private fun setAdapter(regionSpinner: Spinner, items: List<Region>, id: Int?) {
        val adapter = ItemSpinnerAdapter(requireContext(), items.map { it.name })
        regionSpinner.adapter = adapter

        if (id != null) {
            var selected = 0

            for ((index, item) in items.withIndex()) {
                if (item.id == id) {
                    selected = index
                    break
                }
            }

            regionSpinner.setSelection(selected)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val spinner = parent as Spinner
        val selectedItem = spinner.selectedItem

        when (parent.id) {
            R.id.settingsLocalizationCountrySpinner -> {
                selectedItem?.let {
                    mPresenter.getProvinces(mCountries[spinner.selectedItemPosition].id)
                }
            }

            R.id.settingsLocalizationProvinceSpinner -> {
                selectedItem?.let {
                    mPresenter.getCities(mProvinces[spinner.selectedItemPosition].id)
                }
            }

            R.id.settingsLocalizationCitySpinner -> {
                selectedItem?.let {
                    mPresenter.getDistricts(mCities[spinner.selectedItemPosition].id)
                }
            }
        }

        enableDisableBtn()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.settingsLocalizationTimeZoneBtn -> {
                if (this::mTimeZones.isInitialized) {
                    val bundle = Bundle()
                    bundle.putString(App.TITLE, getString(R.string.time_zone))

                    val dialog = SelectionDialog<SelectionItem>()
                    dialog.arguments = bundle
                    dialog.setOnItemSelected(
                        mTimeZones.map { SelectionItem(it) },
                        object : SelectionDialog.ItemSelection<SelectionItem> {
                            override fun onItemSelected(item: SelectionItem) {
                                enableDisableBtn()
                                mSelectedTimeZone = item.title
                                mBinding.settingsLocalizationTimeZoneBtn.text = item.title
                            }
                        })
                    dialog.show(childFragmentManager, null)
                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        enableDisableBtn()
    }
}