package com.magistor8.weather.view.main

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.magistor8.weather.R
import com.magistor8.weather.databinding.FragmentMainBinding
import com.magistor8.weather.domain_model.City
import com.magistor8.weather.domain_model.Weather
import com.magistor8.weather.permissions.LOCATION
import com.magistor8.weather.permissions.Permissions
import com.magistor8.weather.utils.showSnackBar
import com.magistor8.weather.view.details.DetailsFragment
import com.magistor8.weather.view_model.AppState
import com.magistor8.weather.view_model.MainViewModel
import java.io.IOException
import java.lang.IllegalArgumentException

private const val IS_WORLD_KEY = "LIST_OF_TOWNS_KEY"
private const val REFRESH_PERIOD = 60000L
private const val MINIMAL_DISTANCE = 100f

class MainFragment: Fragment() {

    private var _binding:FragmentMainBinding? = null
    private val binding:FragmentMainBinding
    get(){
        return _binding!!
    }
    private lateinit var permissions: Permissions

    private var data: AppState = AppState.Null
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    private val listener by lazy {
        object : OnItemViewClickListener {
        override fun onItemViewClick(weather: Weather) {
            activity?.supportFragmentManager?.apply {
                this.let {
                    Bundle().apply {
                        putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
                        beginTransaction()
                            .replace(R.id.container, DetailsFragment.newInstance(this))
                            .addToBackStack("")
                            .commit()
                        }
                    }
                }
            }
        }
    }

    private val onLocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            context?.let {
                getAddressAsync(it, location)
            }
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private val adapter = MainFragmentAdapter()
    private var isDataSetRus: Boolean = true

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        adapter.setListener(listener)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainFragmentRecyclerView.adapter = adapter
        permissions = Permissions(requireActivity(), this)
        binding.mainFragmentFAB.setOnClickListener { changeWeatherDataSet() }
        binding.mainFragmentFABLocation.setOnClickListener {
            //Спрашиваем разрешение
            permissions.checkPermission(LOCATION) { getLocation() }
//            if (checkPermission(LOCATION)) {
//                getLocation()
//            }
        }
        val observer = Observer<AppState> {
            data = it
            renderData(data)
        }
        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
        when (data) {
            is AppState.Null -> {
                activity?.let {
                    val isDataSetWorld: Boolean = it.getPreferences(Context.MODE_PRIVATE).getBoolean(IS_WORLD_KEY, false)
                    if (isDataSetWorld) {
                        changeWeatherDataSet()
                    } else {
                        viewModel.getWeatherFromLocalSourceRus()
                    }
                }
            }
            else -> renderData(data)
        }
    }


//    private fun checkPermission(permissionType: String) : Boolean {
//        context?.let {
//            when {
//                ContextCompat.checkSelfPermission(it, permissionType) ==
//                        PackageManager.PERMISSION_GRANTED -> {
//                    //Доступ есть
//                    return true
//                }
//                //Опционально: если нужно пояснение перед запросом разрешений
//                shouldShowRequestPermissionRationale(permissionType) -> {
//                    alertDialog(it, permissionType)
//                }
//                else -> {
//                    //Запрашиваем разрешение
//                    when(permissionType) {
//                        LOCATION -> regResLocation.launch(LOCATION)
//                    }
//                }
//            }
//        }
//        return false
//    }

//    private fun alertDialog(it: Context, permissionType: String) {
//        with(AlertDialog.Builder(it)) {
//            when (permissionType) {
//                LOCATION -> {
//                    this.setTitle("Доступ к геолокации")
//                        .setMessage("Для работы приложения необходим доступ к вашему местоположению")
//                        .setPositiveButton("Предоставить доступ") { _, _ ->
//                            regResLocation.launch(LOCATION)
//                        }
//                        .setNegativeButton("Не надо") { dialog, _ -> dialog.dismiss() }
//                        .create()
//                        .show()
//                }
//            }
//        }
//    }

    private fun showDialog(title: String, message: String) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

//    private val regResLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
//        if (result) {
//            getLocation()
//        } else {
//            alertDialog(requireContext(), LOCATION)
//        }
//    }

    private fun getLocation() {
        activity?.let { context ->
            if (ContextCompat.checkSelfPermission(
                    context,
                    LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Получить менеджер геолокаций
                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val provider = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    provider?.let {
                        // Будем получать геоположение через каждые 60 секунд или каждые 100 метров
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            REFRESH_PERIOD,
                            MINIMAL_DISTANCE,
                            onLocationListener
                        )
                    }
                } else {
                    val location =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location == null) {
                        showDialog(
                            getString(R.string.dialog_title_gps_turned_off),
                            getString(R.string.dialog_message_last_location_unknown)
                        )
                    } else {
                        getAddressAsync(context, location)
                        showDialog(
                            getString(R.string.dialog_title_gps_turned_off),
                            getString(R.string.dialog_message_last_known_location)
                        )
                    }
                }
            } else {
                permissions.alertDialog(context, LOCATION)
            }
        }
    }

    private fun getAddressAsync(
        context: Context,
        location: Location
    ) {
        val geoCoder = Geocoder(context)
        Thread {
            try {
                val addresses = geoCoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
                binding.mainFragmentFAB.post {
                    showAddressDialog(addresses[0].getAddressLine(0), location)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun showAddressDialog(address: String, location: Location) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_address_title))
                .setMessage(address)
                .setPositiveButton(getString(R.string.dialog_address_get_weather)) { _, _ ->
                    openDetailsFragment(
                        Weather(
                            City(
                                address,
                                location.latitude,
                                location.longitude
                            )
                        )
                    )
                }
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun openDetailsFragment(
        weather: Weather
    ) {
        activity?.supportFragmentManager?.apply {
            beginTransaction()
                .add(
                    R.id.container,
                    DetailsFragment.newInstance(Bundle().apply {
                        putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
                    })
                )
                .addToBackStack("")
                .commitAllowingStateLoss()
        }
    }

    private fun changeWeatherDataSet() {
        if (isDataSetRus) {
            viewModel.getWeatherFromLocalSourceWorld()
            binding.mainFragmentFAB.setImageResource(R.drawable.world)
        } else {
            viewModel.getWeatherFromLocalSourceRus()
            binding.mainFragmentFAB.setImageResource(R.drawable.rus)
        }
        //Сохраняем настройки
        saveListOfTowns()

        isDataSetRus = !isDataSetRus
    }

    private fun saveListOfTowns() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.let {
            it.putBoolean(IS_WORLD_KEY, isDataSetRus)
            it.apply()
        }
    }

    override fun onDestroyView() {
        _binding = null
        adapter.removeListener()
        super.onDestroyView()
    }

    private fun renderData(appState: AppState) {
        with(binding) {
            val loadingLayout = includedLoadingLayout.root
            when (appState) {
                is AppState.Success -> {
                    loadingLayout.visibility = View.GONE
                    adapter.setWeather(appState.weatherData)
                    if (!isDataSetRus) mainFragmentFAB.setImageResource(R.drawable.world)
                }
                is AppState.Loading -> {
                    loadingLayout.visibility = View.VISIBLE
                }
                is AppState.Error -> {
                    loadingLayout.visibility = View.GONE
                    mainFragmentRecyclerView.visibility = View.GONE
                    mainFragmentFAB.visibility = View.GONE
                    mainFragmentRootView.showSnackBar(
                        getString(R.string.Error),
                        getString(R.string.Reload),
                        { reloadData() }
                    )
                }
                is AppState.Null -> renderData(AppState.Error(IllegalArgumentException()))
            }
        }
    }

    private fun reloadData() {
        if (isDataSetRus) viewModel.getWeatherFromLocalSourceRus() else
            viewModel.getWeatherFromLocalSourceWorld()
        binding.mainFragmentRecyclerView.visibility = View.VISIBLE
        binding.mainFragmentFAB.visibility = View.VISIBLE
    }

}

interface OnItemViewClickListener {
    fun onItemViewClick(weather: Weather)
}
