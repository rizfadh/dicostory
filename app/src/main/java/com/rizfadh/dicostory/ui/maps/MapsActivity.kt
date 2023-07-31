package com.rizfadh.dicostory.ui.maps

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.rizfadh.dicostory.R
import com.rizfadh.dicostory.data.api.response.StoryResult
import com.rizfadh.dicostory.databinding.ActivityMapsBinding
import com.rizfadh.dicostory.ui.main.DetailActivity
import com.rizfadh.dicostory.ui.main.MainActivity
import com.rizfadh.dicostory.utils.Result
import com.rizfadh.dicostory.utils.ViewModelFactory
import com.rizfadh.dicostory.utils.alert
import com.rizfadh.dicostory.utils.dateFormat

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var _binding: ActivityMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModelFactory: ViewModelFactory
    private val mapsViewModel: MapsViewModel by viewModels { viewModelFactory }
    private lateinit var userToken: String
    private val markerData = mutableMapOf<Marker, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.stories_maps)

        viewModelFactory = ViewModelFactory.getInstance(this)
        intent.getStringExtra(MainActivity.EXTRA_TOKEN)?.let {
            userToken = it
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()

        mMap.uiSettings.apply {
            isZoomGesturesEnabled = true
            isCompassEnabled = true
            isZoomControlsEnabled = true
        }

        mMap.setOnInfoWindowClickListener {
            markerData[it]?.let { userToken ->
                toDetailActivity(userToken)
            }
        }

        val token = "Bearer $userToken"
        mapsViewModel.getStoriesMaps(token, 1, 30).observe(this) {
            when (it) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    addMarker(it.data)
                }
                is Result.Empty -> alert(
                    this, getString(R.string.error), getString(R.string.result_empty)
                )
                is Result.Error -> alert(this, getString(R.string.error), it.error)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun addMarker(stories: List<StoryResult>) {
        stories.forEach {
            val latLng = LatLng(it.lat as Double, it.lon as Double)
            val marker = mMap.addMarker(
                MarkerOptions().position(latLng).title(it.name).snippet(it.createdAt.dateFormat())
            ) as Marker
            markerData[marker] = it.id
        }

        val indonesia = LatLng(-2.548926, 118.0148634)
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(indonesia, 4f)
        )
    }

    private fun toDetailActivity(userId: String) {
        val detailIntent = Intent(this, DetailActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_TOKEN, userToken)
            putExtra(DetailActivity.USER_ID, userId)
        }
        startActivity(detailIntent)
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
            )
            if (!success) {
                alert(this, getString(R.string.error), getString(R.string.style_maps_error))
            }
        } catch (e: Resources.NotFoundException) {
            alert(this, getString(R.string.error), e.message.toString())
        }
    }
}