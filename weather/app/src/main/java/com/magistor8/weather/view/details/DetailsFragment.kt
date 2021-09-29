package com.magistor8.weather.view.details


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.google.android.material.snackbar.Snackbar
import com.magistor8.weather.R
import com.magistor8.weather.databinding.FragmentDetailsBinding
import com.magistor8.weather.domain_model.Weather
import com.magistor8.weather.utils.condition
import com.magistor8.weather.utils.showSnackBar
import com.magistor8.weather.view_model.AppState
import com.magistor8.weather.view_model.DetailsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.details_feels_like.*
import kotlinx.android.synthetic.main.details_liq.*
import kotlinx.android.synthetic.main.details_wind.*
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this).get(DetailsViewModel::class.java)
    }

    private val localWeather: Weather by lazy {
        (arguments?.getParcelable(BUNDLE_EXTRA)) ?: Weather()
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
        viewModel.detailsLiveData.observe(viewLifecycleOwner, { renderData(it) })
        getWeather()
    }

    private fun getWeather() {
        viewModel.getWeatherFromRemoteSource(localWeather.city.lat, localWeather.city.lon)
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Error -> {
                binding.includedLoadingLayout.root.visibility = View.GONE
                binding.mainView.visibility = View.VISIBLE
                view?.showSnackBar(
                    getString(R.string.Error),
                    getString(R.string.Reload),
                    { getWeather() }
                )
            }
            is AppState.Loading -> {
                binding.includedLoadingLayout.root.visibility = View.VISIBLE
                binding.mainView.visibility = View.GONE
            }
            is AppState.SuccessDetails -> {
                binding.includedLoadingLayout.root.visibility = View.GONE
                binding.mainView.visibility = View.VISIBLE
                val weather = appState.weatherData
                //Показываем
                showWeather(weather)
                //Сохраняем в базу истории
                saveWeather(weather)
                Snackbar.make(binding.root, "Success", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun saveWeather(weather: Weather) {
        viewModel.saveCityToDB(
            Weather(
                localWeather.city,
                weather.temperature,
                weather.feelsLike,
                weather.condition,
                weather.icon,
                weather.wind,
                weather.humidity
            )
        )
    }

    private fun ImageView.loadUrl(url: String) {

        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadUrl.context)) }
            .build()

        val request = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(500)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }

    private fun showWeather(weather: Weather) {
        with(binding) {
            cityName.text = localWeather.city.name
            condition.text = condition(requireContext(), weather.condition)
            temp.text = weather.temperature.toString()
            wind.text = weather.wind.toString().plus(" м/с")
            humidity.text = weather.humidity.toString().plus("%")
            feels_like.text = weather.feelsLike.toString()
            binding.icon.loadUrl("https://yastatic.net/weather/i/icons/blueye/color/svg/${weather.icon}.svg")

            when(weather.season) {
                "winter" -> binding.root.setBackgroundResource(R.drawable.winter)
                "autumn" -> binding.root.setBackgroundResource(R.drawable.autumn)
                "summer" -> binding.root.setBackgroundResource(R.drawable.summer)
                "spring" -> binding.root.setBackgroundResource(R.drawable.spring)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val BUNDLE_EXTRA = "weather"

        fun newInstance(bundle: Bundle): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}