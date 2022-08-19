package com.example.happyplaceapp

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.happyplaceapp.databinding.ActivityAddHappyPlaceBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivityAddHappyPlaceBinding? = null
    private var cal = Calendar.getInstance()
    private var mRegisterEntity: registerEntity? = null

    private var saveImageToInternalStorage: Uri? = null

    private lateinit var dateSetListener : DatePickerDialog.OnDateSetListener
    private var latitude = 0.0
    private var longitude = 0.0

    companion object{
        private const val IMAGE_DIRECTORY = "Happy Places"
    }

      private var  galleryImageResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
                result->
            if (result.resultCode == RESULT_OK && result.data != null){

                 val contentUri = result.data?.data

                    val clickedImageBitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)

                    saveImageToInternalStorage  = saveImageToInternalStorage(clickedImageBitmap)


                    binding?.ivPlaceImage?.setImageBitmap(clickedImageBitmap)

            }
        }


    private var  cameraCaptureImageResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result->
        if (result.resultCode == RESULT_OK && result.data != null){

            val thumbNail: Bitmap = result.data!!.extras!!.get("data") as Bitmap
            saveImageToInternalStorage  = saveImageToInternalStorage(thumbNail)
            Log.e("Saved Image", "Path :: $saveImageToInternalStorage")

            binding?.ivPlaceImage?.setImageBitmap(thumbNail)

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarAddPlace)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarAddPlace?.setNavigationOnClickListener{
            onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener{
            _, year, month, dayofmonth->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayofmonth)
            updateDateInView()
        }
        updateDateInView()

        //Update
        val dao = (application as RegisterApp).db.registerDao()

        updatePutDataPlace(dao)

        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date->{
                DatePickerDialog(this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.tv_add_image-> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select photo from Gallery",
                    "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems){ _, which->

                    when(which){
                        0 ->choosePhotoFromGallery()
                        1 ->capturePhotoUsingCamera()
                    }
                }
                pictureDialog.show()
            }
            R.id.btn_save-> {


                        val dao = (application as RegisterApp).db.registerDao()
                        addRecord(dao)
                        //Finished Activity
                        finish()

            }
        }
    }
    private fun updatePutDataPlace(registerDao: registerDao) {

        mRegisterEntity = intent.getSerializableExtra("data") as? registerEntity

        if (mRegisterEntity != null) {

            setSupportActionBar(binding?.toolbarAddPlace)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "Edit Place Details"

            binding?.toolbarAddPlace?.setNavigationOnClickListener {
                onBackPressed()
            }

            saveImageToInternalStorage = Uri.parse(mRegisterEntity!!.image)

            binding?.ivPlaceImage?.setImageURI( saveImageToInternalStorage)
            binding?.etTitle?.setText(mRegisterEntity!!.title)
            binding?.etDescription?.setText(mRegisterEntity!!.description)
            binding?.etDate?.setText(mRegisterEntity!!.date)
            binding?.etLocation?.setText(mRegisterEntity!!.location)
            latitude = mRegisterEntity!!.latitude
            longitude = mRegisterEntity!!.longitude

            binding?.btnSave?.text = "UPDATE"

            binding?.btnSave?.setOnClickListener {

                val titleR = binding?.etTitle?.text.toString()
                val descriptionR = binding?.etDescription?.text.toString()
                val dateR = binding?.etDate?.text.toString()
                val locationR = binding?.etLocation?.text.toString()

                if (titleR.isNotEmpty() && descriptionR.isNotEmpty() && dateR.isNotEmpty()
                    && locationR.isNotEmpty()){
                    lifecycleScope.launch {

                        registerDao.update(registerEntity(mRegisterEntity!!.id, mRegisterEntity!!.title ,
                            saveImageToInternalStorage.toString(),
                            mRegisterEntity!!.description, mRegisterEntity!!.date, mRegisterEntity!!.location, longitude = longitude, latitude = latitude))
                        Toast.makeText(applicationContext, "Record Updated",
                            Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(applicationContext, "Please enter everything correctly",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun addRecord(registerDao: registerDao){


        val title = binding?.etTitle?.text.toString()
        val description = binding?.etDescription?.text.toString()
        val date = binding?.etDate?.text.toString()
        val location = binding?.etLocation?.text.toString()


        if (title.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty()
                                                 && location.isNotEmpty()){
            lifecycleScope.launch {

                registerDao.insert(registerEntity(title = title, image = saveImageToInternalStorage.toString(), description =
                description, date = date, location = location, longitude = longitude, latitude = latitude))
                Toast.makeText(applicationContext, "Record Saved",
                    Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(applicationContext, "Email and Name cannot be blank",
                Toast.LENGTH_LONG).show()
        }

    }



    private fun choosePhotoFromGallery() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){

                        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        galleryImageResultLauncher.launch(pickIntent)


                }else{
                    showRationalDialogForPermissions()
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions:
                                    MutableList<PermissionRequest> , token: PermissionToken)
            {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }
    private fun capturePhotoUsingCamera() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraCaptureImageResultLauncher.launch(intent)

                }else{
                    showRationalDialogForPermissions()
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions:
                                MutableList<PermissionRequest> , token: PermissionToken)
            {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }

       private fun saveImageToInternalStorage(bitmap: Bitmap): Uri? {

        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }


    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this).setMessage("It looks like you have turned off the access permission," +
                "You need to go setting and enable it.")
            .setPositiveButton("Go to Setting"){
                _,_->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel"){dialog, _->
                dialog.dismiss()
        }
    }

    private fun updateDateInView(){
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding?.etDate?.setText(sdf.format(cal.time).toString())
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}