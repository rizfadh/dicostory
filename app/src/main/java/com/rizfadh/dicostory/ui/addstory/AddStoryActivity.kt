package com.rizfadh.dicostory.ui.addstory

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rizfadh.dicostory.R
import com.rizfadh.dicostory.databinding.ActivityAddStoryBinding
import com.rizfadh.dicostory.ui.main.MainActivity
import com.rizfadh.dicostory.utils.*
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.EasyPermissions.hasPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private var _binding: ActivityAddStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ViewModelFactory
    private val addStoryViewModel: AddStoryViewModel by viewModels { viewModelFactory }

    private lateinit var userToken: String

    private var pictureFile: File? = null

    private lateinit var fusedLocation: FusedLocationProviderClient

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.CAMERA_RESULT) {

            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra(CameraActivity.EXTRA_PICTURE, File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra(CameraActivity.EXTRA_PICTURE)
            } as? File

            val isBackCamera =
                it.data?.getBooleanExtra(CameraActivity.EXTRA_ROTATION, true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                pictureFile = file
                binding.ivAddStoryImagePreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImage = it.data?.data as Uri
            selectedImage.let { uri ->
                pictureFile = uriToFile(uri, this)
                binding.ivAddStoryImagePreview.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelFactory = ViewModelFactory.getInstance(this)
        intent.getStringExtra(MainActivity.EXTRA_TOKEN)?.let {
            userToken = it
        }
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        supportActionBar?.title = getString(R.string.add_story)

        if (!hasPermission()) requestPermissions()

        addStoryViewModel.uploadResult.observe(this) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                    isButtonClickable(false)
                }

                is Result.Success -> {
                    showLoading(false)
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.success))
                        .setMessage(getString(R.string.upload_success))
                        .setPositiveButton("Ok") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setOnDismissListener {
                            finish()
                        }
                        .create()
                        .show()
                }

                is Result.Error -> {
                    showLoading(false)
                    alert(this, getString(R.string.error), it.error)
                    isButtonClickable(true)
                }

                else -> {}
            }
        }

        binding.apply {
            btnAddCamera.setOnClickListener { startCameraActivity() }
            btnAddGallery.setOnClickListener { openGallery() }
            buttonAdd.setOnClickListener { uploadStory(pictureFile) }
        }
    }

    private fun hasPermission() = hasPermissions(
        this, CAMERA_PERMISSION, FINE_LOCATION_PERMISSION, COARSE_LOCATION_PERMISSION
    )

    private fun requestPermissions() {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.permission_required),
            PERMISSION_REQUEST_CODE,
            CAMERA_PERMISSION, FINE_LOCATION_PERMISSION, COARSE_LOCATION_PERMISSION
        )
    }

    private fun startCameraActivity() {
        cameraLauncher.launch(Intent(this, CameraActivity::class.java))
    }

    private fun openGallery() {
        val intentGallery = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = Intent.createChooser(intentGallery, getString(R.string.choose_picture))
        galleryLauncher.launch(chooser)
    }

    @SuppressLint("MissingPermission")
    private fun uploadStory(picture: File?) {
        val storyDescription = binding.edAddDescription.text.toString().trim()
        when {
            storyDescription.isEmpty() -> binding.edAddDescription.error =
                getString(R.string.required)

            picture == null -> alert(
                this,
                getString(R.string.error),
                getString(R.string.picture_empty)
            )

            else -> {
                val token = "Bearer $userToken"
                val image = reduceFileImage(picture)
                val description = storyDescription.toRequestBody("text/plain".toMediaType())
                val requestImageFile = image.asRequestBody("image/jpeg".toMediaType())
                val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    image.name,
                    requestImageFile
                )
                if (binding.cbLocation.isChecked) {
                    fusedLocation.lastLocation.addOnSuccessListener {
                        it?.let {
                            val lat = it.latitude.toFloat().toString().toRequestBody("text/plain".toMediaType())
                            val lon = it.longitude.toFloat().toString().toRequestBody("text/plain".toMediaType())
                            addStoryViewModel.addStory(token, description, imageMultiPart, lat, lon)
                        }
                    }
                } else addStoryViewModel.addStory(token, description, imageMultiPart)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isButtonClickable(isClickable: Boolean) {
        binding.apply {
            btnAddCamera.isClickable = isClickable
            btnAddGallery.isClickable = isClickable
            buttonAdd.isClickable = isClickable
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        alert(this, getString(R.string.success), getString(R.string.permissions_success))
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this).build().show()
        } else requestPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 10
        const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        const val FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        const val COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
    }
}