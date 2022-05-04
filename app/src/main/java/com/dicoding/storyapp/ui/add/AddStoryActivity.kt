package com.dicoding.storyapp.ui.add

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.helper.*
import com.dicoding.storyapp.helper.CameraUtils.reduceFileImage
import com.dicoding.storyapp.helper.CameraUtils.rotateBitmap
import com.dicoding.storyapp.helper.CameraUtils.uriToFile
import com.dicoding.storyapp.ui.story.ListStoryActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var getFile : File? = null
    private lateinit var addStoryViewModel: AddStoryViewModel
    private var userToken : String = ""
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private var currentLocation : Location? = null
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, "No permission granted.", Toast.LENGTH_SHORT).show()
                finish()
            }
            attemptRetrieveLocation()
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val pref = SettingsPreferences.getInstance(dataStore)
        addStoryViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application, pref)).get(
            AddStoryViewModel::class.java
        )

        //Retrieve login session token, needed when submitting a new story
        addStoryViewModel.getSessionToken().observe(this){ sessionData ->
            if(sessionData.trim() != ""){
                userToken = sessionData
            }
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        addStoryViewModel.isLoading.observe(this){
            showLoading(it)
        }

        addStoryViewModel.uploadResponse.observe(this){ uploadResponse ->
            if(uploadResponse.error){
                Toast.makeText(this, uploadResponse.message, Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "New Story uploaded successfully.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AddStoryActivity, ListStoryActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.btnCameraX.setOnClickListener{ startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnSubmitStory.setOnClickListener { submitStory() }

        attemptRetrieveLocation()
    }

    //Attempt to retrieve location if permission is granted
    private fun attemptRetrieveLocation(){
        if(checkLocationPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkLocationPermission(Manifest.permission.ACCESS_COARSE_LOCATION)){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                    val locationString : String = "${location.latitude}, ${location.longitude}"
                    binding.tvLocation.text = locationString
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun checkLocationPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraX() {
        val cameraXIntent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(cameraXIntent)
    }

    private fun startGallery() {
        val galleryIntent = Intent()
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.type = "image/*"
        val chooser = Intent.createChooser(galleryIntent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun submitStory() {
        if(getFile != null){
            val file = reduceFileImage(getFile as File)
            val description = binding.etTextBox.text.toString().trim().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)
            val lat = currentLocation?.latitude.toString().toRequestBody("text/plain".toMediaType())
            val lon = currentLocation?.longitude.toString().toRequestBody("text/plain".toMediaType())
            addStoryViewModel.uploadStory(userToken, description, lat, lon, imageMultipart)
        }else{
            Toast.makeText(this, "Please upload an image first.", Toast.LENGTH_SHORT).show()
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getFile = myFile
            val result = rotateBitmap(BitmapFactory.decodeFile(myFile.path), isBackCamera)
            binding.previewImageView.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.progressBarUpload.visibility = if(isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}