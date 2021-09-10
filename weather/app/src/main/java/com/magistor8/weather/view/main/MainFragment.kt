package com.magistor8.weather.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.magistor8.weather.R
import com.magistor8.weather.databinding.FragmentMainBinding
import com.magistor8.weather.domain_model.Weather
import com.magistor8.weather.view.details.DetailFragment
import com.magistor8.weather.view_model.AppState
import com.magistor8.weather.view_model.MainViewModel

class MainFragment: Fragment() {

    private var _binding:FragmentMainBinding? = null
    private val binding:FragmentMainBinding
    get(){
        return _binding!!
    }

    private lateinit var viewModel: MainViewModel
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
        adapter.setListener(object : OnItemViewClickListener {
            override fun onItemViewClick(weather: Weather) {
                val manager = activity?.supportFragmentManager
                if (manager != null) {
                    val bundle = Bundle()
                    bundle.putParcelable(DetailFragment.BUNDLE_EXTRA, weather)
                    manager.beginTransaction()
                        .replace(R.id.container, DetailFragment.newInstance(bundle))
                        .addToBackStack("")
                        .commit()
                }
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainFragmentRecyclerView.adapter = adapter
        binding.mainFragmentFAB.setOnClickListener { changeWeatherDataSet() }
        val observer = Observer<AppState> { renderData(it) }
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
        viewModel.getWeatherFromLocalSourceRus()
    }

    private fun changeWeatherDataSet() {
        if (isDataSetRus) {
            viewModel.getWeatherFromLocalSourceWorld()
            binding.mainFragmentFAB.setImageResource(R.drawable.world)
        } else {
            viewModel.getWeatherFromLocalSourceRus()
            binding.mainFragmentFAB.setImageResource(R.drawable.rus)
        }
        isDataSetRus = !isDataSetRus
    }

    override fun onDestroyView() {
        _binding = null
        adapter.removeListener()
        super.onDestroyView()
    }

    private fun renderData(appState: AppState) {
        val loadingLayout = binding.mainFragmentLoadingLayout
        when (appState) {
            is AppState.Success -> {
                loadingLayout.visibility = View.GONE
                adapter.setWeather(appState.weatherData)
            }
            is AppState.Loading -> {
                loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                loadingLayout.visibility = View.GONE
                binding.mainFragmentRecyclerView.visibility = View.GONE
                binding.mainFragmentFAB.visibility = View.GONE
                Snackbar
                    .make(binding.root, getString(R.string.Error), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.Reload)) {
                        if (isDataSetRus) viewModel.getWeatherFromLocalSourceRus() else
                            viewModel.getWeatherFromLocalSourceWorld()
                        binding.mainFragmentRecyclerView.visibility = View.VISIBLE
                        binding.mainFragmentFAB.visibility = View.VISIBLE
                    }
                    .show()
            }
        }

    }


}

interface OnItemViewClickListener {
    fun onItemViewClick(weather: Weather)
}
