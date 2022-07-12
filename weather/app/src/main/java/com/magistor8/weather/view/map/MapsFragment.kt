package com.magistor8.weather.view.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.magistor8.weather.R
import com.magistor8.weather.databinding.FragmentMapsBinding
import kotlinx.android.synthetic.main.fragment_maps.*
import java.io.IOException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter

import com.google.android.gms.maps.model.LatLng
import com.magistor8.weather.domain_model.WeatherDTO
import com.magistor8.weather.permissions.Permissions
import com.magistor8.weather.repository.DetailsRepository
import com.magistor8.weather.repository.DetailsRepositoryImpl
import com.magistor8.weather.repository.RemoteDataSource
import com.magistor8.weather.utils.condition
import com.magistor8.weather.utils.convertDtoToModel
import com.magistor8.weather.permissions.LOCATION
import com.magistor8.weather.permissions.PermissionListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding: FragmentMapsBinding get() = _binding!!
    private lateinit var tempMarker: Marker
    private val detailsRepositoryImpl: DetailsRepository = DetailsRepositoryImpl(RemoteDataSource())

    private lateinit var permissions: Permissions
    private lateinit var map: GoogleMap
    private val markers: ArrayList<Marker> = arrayListOf()

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        val initialPlace = LatLng(52.52000659999999, 13.404953999999975)
        googleMap.addMarker(
            MarkerOptions().position(initialPlace).title(getString(R.string.marker_start))
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(initialPlace))
        activateMyLocation()
        googleMap.setOnMapLongClickListener { latLng ->
            getAddressAsync(latLng)
        }
        map.setInfoWindowAdapter(object : InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                val v: View = layoutInflater.inflate(R.layout.infowindowlayout, null)
                val tv1 = v.findViewById<TextView>(R.id.textView1)
                val tv2 = v.findViewById<TextView>(R.id.textView2)
                tv1.text = tempMarker.title
                tv2.text = tempMarker.snippet
                return v
            }
        })

        //маркер лисенер
        map.setOnMarkerClickListener { marker ->
            tempMarker = marker
            detailsRepositoryImpl.getWeatherDetailsFromServer(marker.position.latitude, marker.position.longitude, markerCallBack)
            true
        }
    }

    private fun activateMyLocation() {
        context?.let {
            val isPermissionGranted =
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED
            map.isMyLocationEnabled = isPermissionGranted
            map.uiSettings.isMyLocationButtonEnabled = isPermissionGranted
        }
        //Спрашиваем разрешение
        permissions.checkPermission(LOCATION)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        permissions = Permissions(requireActivity(), this)
        initSearchByAddress()
    }

    private val markerCallBack = object :
        Callback<WeatherDTO> {

        override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {
            val serverResponse: WeatherDTO? = response.body()
            if (response.isSuccessful && serverResponse != null) {
                val weather = convertDtoToModel(serverResponse)
                tempMarker.snippet = String.format("Темпиратура: %d, %s, ветер %s м/с, влажность %s ",
                    weather.temperature, condition(requireContext(), weather.condition), weather.wind, weather.humidity)
                tempMarker.showInfoWindow()
            }
        }

        override fun onFailure(call: Call<WeatherDTO>, t: Throwable) {
            tempMarker.hideInfoWindow()
        }
    }

    private fun getAddressAsync(location: LatLng) {
        context?.let {
            val geoCoder = Geocoder(it)
            Thread {
                try {
                    val addresses =
                        geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    textAddress.post { textAddress.text = addresses[0].getAddressLine(0) }
                    Handler(Looper.getMainLooper()).post {
                        addMarkerToArray(location, addresses[0].getAddressLine(0))
                        drawLine()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    private fun addMarkerToArray(location: LatLng, title: String) {
        val marker = setMarker(location, title)
        markers.add(marker)
    }

    private fun setMarker(
        location: LatLng,
        searchText: String,
    ): Marker {
        val height = 100
        val width = 100
        val b = BitmapFactory.decodeResource(resources, R.drawable.pin)
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
        val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker)
        val marker = MarkerOptions()
            .position(location)
            .title(searchText)
            .icon(smallMarkerIcon)
        return map.addMarker(marker)!!
    }


    private fun drawLine() {
        val last: Int = markers.size - 1
        if (last >= 1) {
            val previous: LatLng = markers[last - 1].position
            val current: LatLng = markers[last].position
            map.addPolyline(
                PolylineOptions()
                    .add(previous, current)
                    .color(Color.RED)
                    .width(5f)
            )
        }
    }

    private fun initSearchByAddress() {
        binding.buttonSearch.setOnClickListener {
            val geoCoder = Geocoder(it.context)
            val searchText = searchAddress.text.toString()
            Thread {
                try {
                    val addresses = geoCoder.getFromLocationName(searchText, 1)
                    if (addresses.size > 0) {
                        goToAddress(addresses, it, searchText)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    private fun goToAddress(
        addresses: MutableList<Address>,
        view: View,
        searchText: String
    ) {
        val location = LatLng(
            addresses[0].latitude,
            addresses[0].longitude
        )
        view.post {
            setMarker(location, searchText)
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    15f
                )
            )
        }
    }
}