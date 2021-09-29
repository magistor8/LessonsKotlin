package com.magistor8.weather.view.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.magistor8.weather.R
import com.magistor8.weather.databinding.FragmentMainBinding
import com.magistor8.weather.domain_model.Weather
import com.magistor8.weather.utils.showSnackBar
import com.magistor8.weather.view.details.DetailsFragment
import com.magistor8.weather.view_model.AppState
import com.magistor8.weather.view_model.MainViewModel
import java.lang.IllegalArgumentException

private const val IS_WORLD_KEY = "LIST_OF_TOWNS_KEY"

class MainFragment: Fragment() {

    private var _binding:FragmentMainBinding? = null
    private val binding:FragmentMainBinding
    get(){
        return _binding!!
    }
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
        binding.mainFragmentFAB.setOnClickListener { changeWeatherDataSet() }
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
