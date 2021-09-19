package com.magistor8.weather.view.details

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.magistor8.weather.R
import com.magistor8.weather.databinding.FragmentDetailsBinding
import com.magistor8.weather.domain_model.Weather
import com.magistor8.weather.domain_model.WeatherDTO
import com.magistor8.weather.view.main.showSnackBar

const val DETAILS_INTENT_FILTER = "DETAILS INTENT FILTER"
private const val TEMP_INVALID = -100
private const val FEELS_LIKE_INVALID = -100

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() {
            return _binding!!
        }

    private lateinit var weatherBundle: Weather
    private val loadResultsReceiver: BroadcastReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {

            val weatherDTO = intent.getParcelableExtra<WeatherDTO>(DETAILS_LOAD_RESULT_EXTRA)
            if (weatherDTO == null) {
                binding.mainView.showSnackBar(
                    when (intent.getStringExtra(DETAILS_LOAD_RESULT_EXTRA)) {
                        DETAILS_INTENT_EMPTY_EXTRA -> getString(R.string.NullIntent)
                        DETAILS_DATA_EMPTY_EXTRA -> getString(R.string.ServerWithNullParams)
                        DETAILS_RESPONSE_EMPTY_EXTRA -> getString(R.string.EmptyData)
                        DETAILS_REQUEST_ERROR_EXTRA -> intent.getStringExtra(DETAILS_REQUEST_ERROR_MESSAGE_EXTRA)?:getString(R.string.Error)
                        DETAILS_URL_MALFORMED_EXTRA -> getString(R.string.WrongURL)
                        else -> getString(R.string.Error)
                    },
                    getString(R.string.Reload),
                    { reloadData() }
                )
            } else { renderData(weatherDTO) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            LocalBroadcastManager.getInstance(it)
                .registerReceiver(loadResultsReceiver, IntentFilter(DETAILS_INTENT_FILTER))
        }
    }

    override fun onDestroy() {
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(loadResultsReceiver)
        }
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherBundle = arguments?.getParcelable(BUNDLE_EXTRA) ?: Weather()
        getWeather()
    }

    private fun reloadData() {
        getWeather()
    }

    private fun getWeather() {
        binding.mainView.visibility = View.GONE
        binding.loadingLayout.visibility = View.VISIBLE
        context?.let {
            it.startService(Intent(it, DetailsService::class.java).apply {
                putExtra(
                    LATITUDE_EXTRA,
                    weatherBundle.city.lat
                )
                putExtra(
                    LONGITUDE_EXTRA,
                    weatherBundle.city.lon
                )
            })
        }
    }

    private fun renderData(weatherDTO: WeatherDTO) {
        binding.mainView.visibility = View.VISIBLE
        binding.loadingLayout.visibility = View.GONE

        val fact = weatherDTO.fact
        val temp = fact!!.temp
        val feelsLike = fact.feels_like
        val condition = fact.condition
        if (temp == TEMP_INVALID || feelsLike == FEELS_LIKE_INVALID || condition == null) {
            binding.mainView.showSnackBar(
                getString(R.string.Error),
                getString(R.string.Reload),
                { reloadData() }
            )
        } else {
            with(binding) {
                mainView.visibility = View.VISIBLE
                loadingLayout.visibility = View.GONE
                val city = weatherBundle.city
                cityName.text = city.city
                cityCoordinates.text = String.format(
                    getString(R.string.city_coordinates),
                    city.lat.toString(),
                    city.lon.toString()
                )
                weatherCondition.text = fact.condition
                temperatureValue.text = fact.temp.toString()
                feelsLikeValue.text = fact.feels_like.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {

        const val BUNDLE_EXTRA = "weather"

        fun newInstance(bundle: Bundle): DetailFragment {
            val fragment = DetailFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}