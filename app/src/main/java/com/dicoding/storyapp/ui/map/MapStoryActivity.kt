package com.dicoding.storyapp.ui.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityMapStoryBinding
import com.dicoding.storyapp.helper.SettingsPreferences
import com.dicoding.storyapp.helper.ViewModelFactory
import com.dicoding.storyapp.model.ListStoryItem
import com.dicoding.storyapp.ui.detail.StoryDetailActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MapStoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapStoryBinding
    private lateinit var mapStoryViewModel: MapStoryViewModel
    private var originStoryLatLng : LatLng? = null
    private var list = ArrayList<ListStoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        val pref = SettingsPreferences.getInstance(dataStore)
        mapStoryViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application, pref)).get(
            MapStoryViewModel::class.java
        )

        mapStoryViewModel.getSessionToken().observe(this){
            mapStoryViewModel.getStoryWithLocation(it)
        }

        mapStoryViewModel.listStory.observe(this){
            list.clear()
            list.addAll(it)
            mapFragment.getMapAsync(this)
        }

        mapStoryViewModel.isLoading.observe(this){
            showLoading(it)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        originStoryLatLng = LatLng(list[0].lat, list[0].lon)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originStoryLatLng!!, 15f))

        for(storyItem in list){
            val latLng = LatLng(storyItem.lat, storyItem.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(storyItem.name)
                    .snippet(storyItem.description)
            )
        }

        mMap.setOnInfoWindowClickListener { marker ->
            val intent = Intent(this@MapStoryActivity, StoryDetailActivity::class.java)
            intent.putExtra(StoryDetailActivity.EXTRA_STORY, findMarkerData(marker))
            startActivity(intent)
        }

        getMyLocation()
        setMapStyle()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun findMarkerData(marker: Marker) : ListStoryItem{
        return list.get(list.indexOf(list.find{
            it.name == marker.title && it.description == marker.snippet
                    && it.lat == marker.position.latitude && it.lon == marker.position.longitude
        }))
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun showLoading(status: Boolean){
        binding.progressBarMap.visibility = if(status) View.VISIBLE else View.GONE
    }

    companion object{
        private const val TAG = "MapStoryActivity"
    }
}