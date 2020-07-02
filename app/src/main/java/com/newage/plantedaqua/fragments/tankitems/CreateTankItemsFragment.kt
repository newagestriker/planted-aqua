package com.newage.plantedaqua.fragments.tankitems

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.newage.plantedaqua.BuildConfig
import com.newage.plantedaqua.R
import com.newage.plantedaqua.databinding.FragmentCreateTankItemsBinding
import com.newage.plantedaqua.viewmodels.TankItemListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*

class CreateTankItemsFragment : Fragment(), View.OnClickListener {


    private lateinit var binding: FragmentCreateTankItemsBinding
    private lateinit var tankpicUri : Uri

   private val REQ_CODE_TO_SELECT_ITEM_IMAGE = 61
   private val REQ_CODE_FOR_ITEM_BUTTON_VISIBILITY = 62
   private val REQUEST_CAMERA = 23
   private val SELECT_FILE = 31
    private var alertDialog : DialogInterface? = null
    private var arePermissionAvailable = false
    override fun onClick(v: View?) {
        binding.apply {

            when(v?.id){

                R.id.removeStartDateFromCreateItems->{
                    tankItemListViewModel.getEachTankItem().itemPurchaseDate = ""
                    tankItemListViewModel.resetEachTankItemLiveDataValue()
                }

                R.id.removeEndDateFromCreateItems->{
                    tankItemListViewModel.getEachTankItem().itemExpiryDate = ""
                    tankItemListViewModel.resetEachTankItemLiveDataValue()
                }

                R.id.AcCalendar ->{
                    selectDate(R.id.AcDateInput)
                }

                R.id.ExpCalendar ->{
                    selectDate(R.id.ExDateInput)
                }
            }

        }
    }

    private val tankItemListViewModel: TankItemListViewModel by sharedViewModel()


    private interface PermissionGranted {
        fun onPermissionsAvailable()
        fun onPermissionsNotAvailable()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        Timber.d("OnCreate Called for CreateTankItems")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_tank_items, container, false)

        binding.lifecycleOwner = this

        binding.boundDetailedTankItem = tankItemListViewModel

        Timber.d("Category : ${binding.boundDetailedTankItem!!.getEachTankItem().itemCategory}")



        binding.apply {
            checkPermissions(object : PermissionGranted {
                override fun onPermissionsAvailable() {
                    grantItemImagePermissionCard.visibility = View.GONE
                    arePermissionAvailable = true

                }

                override fun onPermissionsNotAvailable() {
                    grantItemImagePermissionCard.visibility = View.VISIBLE
                    arePermissionAvailable = false
                }
            })



            grantItemImagePermissionButton.setOnClickListener { requestForPermissions(REQ_CODE_FOR_ITEM_BUTTON_VISIBILITY) }
            loadImage.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissions(object : PermissionGranted {
                        override fun onPermissionsAvailable() {
                            grantItemImagePermissionCard.visibility = View.GONE
                            selectImage()
                        }

                        override fun onPermissionsNotAvailable() {
                            requestForPermissions(REQ_CODE_TO_SELECT_ITEM_IMAGE)
                        }
                    })
                } else {
                    selectImage()
                }
            }


            AcCalendar.setOnClickListener(this@CreateTankItemsFragment)


            ExpCalendar.setOnClickListener(this@CreateTankItemsFragment)

            removeStartDateFromCreateItems.setOnClickListener(this@CreateTankItemsFragment)
           removeEndDateFromCreateItems.setOnClickListener(this@CreateTankItemsFragment)
        }

        tankItemListViewModel.getEachTankItemLiveData().observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            binding.invalidateAll()

        })


        tankItemListViewModel.getDisplayImageUri().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(arePermissionAvailable) {

                it?.loadImageIntoImageView()
            }
        })

        return binding.root
    }



    private fun Uri.loadImageIntoImageView() {
        if(this.toString().isNotBlank()) {
            Glide.with(requireContext())
                    .load(this)
                    .apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.drawable.aquarium2))
                    .into(binding.EqImage)
        }
    }


    private fun selectDate(idName: Int) {
        val newCalendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(), OnDateSetListener { _, year, month, dayOfMonth ->
            val dt = year.toString() + "-" + convertToString(month + 1) + "-" + convertToString(dayOfMonth)
            if (idName == R.id.AcDateInput) {
                tankItemListViewModel.getEachTankItem().itemPurchaseDate = dt
                tankItemListViewModel.resetEachTankItemLiveDataValue()

            } else {
                tankItemListViewModel.getEachTankItem().itemExpiryDate = dt
                tankItemListViewModel.resetEachTankItemLiveDataValue()

            }
        }, newCalendar[Calendar.YEAR], newCalendar[Calendar.MONTH], newCalendar[Calendar.DAY_OF_MONTH])
        datePickerDialog.show()
    }

    private fun convertToString(num: Int): String {
        return if (num < 10) "0$num" else num.toString()
    }

    private var imageFileName : String = ""

    fun selectImage() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        imageFileName = tankItemListViewModel.category + "_" + timeStamp + "_Pic.jpg"
        val items = arrayOf<CharSequence>("Camera", "Gallery", "Cancel")
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            val resolver: ContentResolver = requireContext().contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            tankpicUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
            Timber.d("initial URI Q : $tankpicUri")
            tankItemListViewModel.setNewFile(File(tankpicUri.path!!))

        }
        else {
            val imagesFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "JPT_images")
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs()
            }
            if (imagesFolder.isDirectory) {
                tankItemListViewModel.setNewFile(File(imagesFolder,imageFileName))
                tankpicUri = if (Build.VERSION.SDK_INT >= 24) {
                    FileProvider.getUriForFile(requireContext().applicationContext,
                            BuildConfig.APPLICATION_ID + ".provider",
                            tankItemListViewModel.getNewFile()!!)
                } else {
                    Uri.fromFile(tankItemListViewModel.getNewFile())
                }

                Timber.d("initial URI <Q : $tankpicUri")

                // System.out.println("Uri : "+tankpicUri.toString());
            }


        }
        tankItemListViewModel.setNewUri(tankpicUri)
        Timber.d("Result stored Uri Start Intent : ${tankItemListViewModel.getNewUri()}")


        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Get Image from")
        builder.setItems(items) { dialog, which ->
            alertDialog = dialog
            if (items[which] == "Camera") {
                val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                i.putExtra(MediaStore.EXTRA_OUTPUT, tankpicUri)
                startActivityForResult(i,REQUEST_CAMERA)
            } else if (items[which] == "Gallery") {


                val i = Intent(Intent.ACTION_PICK)
                i.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")

                    startActivityForResult(Intent.createChooser(i, "Select Image Using"), SELECT_FILE)

            } else {
                tankItemListViewModel.setNewUri(null)
                dialog.dismiss()
            }
        }
        builder.show()


    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        /* if(data==null){
            System.out.println("Null data");
        }*/
       // var fileCreated: Boolean
        if (resultCode == Activity.RESULT_OK) {

            tankItemListViewModel.setImageFile(tankItemListViewModel.getNewFile()!!) // Checking whether image file exists is done inside this function itself in viewModel

            if (requestCode == SELECT_FILE) {

                try {

                    Timber.d("Uri from gallery : ${data!!.data}")
                    tankItemListViewModel.setDisplayImageUri(data.data!!)
                    tankItemListViewModel.setTankPicUriFromGallery(data.data)
                    CoroutineScope(Dispatchers.IO).launch {
                        tankItemListViewModel.deleteImageFileIfExists()
                   }

                } catch (e: Exception) {
                    val msg = if (TextUtils.isEmpty(e.message)) getString(R.string.unknown_error) else e.message!!
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                    Timber.d(msg)
                }

            }
            else if(requestCode == REQUEST_CAMERA){
                tankItemListViewModel.setTankPicUriFromGallery(null)

                tankItemListViewModel.apply {
                    getNewUri()?.let {

                        setDisplayImageUri(it)
                    }
                }
            }

            Timber.d("Result stored  Uri on Activity result : ${tankItemListViewModel.getNewUri()}")


            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q) {
                val scanFileIntent = Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, tankItemListViewModel.getNewUri())
                requireActivity().sendBroadcast(scanFileIntent)
            }

        }
    }



    //region PERMISSIONS
    private val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

    private fun checkPermissions(permissionGranted: PermissionGranted) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext().applicationContext, permissions[2]) == PackageManager.PERMISSION_GRANTED) {
                permissionGranted.onPermissionsAvailable()
            } else {
                permissionGranted.onPermissionsNotAvailable()
            }
        }
    }

    private fun requestForPermissions(req_code: Int) {
       requestPermissions( permissions, req_code)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

            if (requestCode == REQ_CODE_TO_SELECT_ITEM_IMAGE) {
                selectImage()
            }
            binding.grantItemImagePermissionCard.visibility = View.GONE
            arePermissionAvailable = true
          //  tankItemListViewModel.getNewUri()?.loadImageIntoImageView()

        } else {
            Toast.makeText(requireContext(), resources.getString(R.string.PermRationale), Toast.LENGTH_LONG).show()
            binding.grantItemImagePermissionCard.visibility = View.VISIBLE
            arePermissionAvailable = false
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    //endregion


    override fun onDestroy() {
        alertDialog?.dismiss()
        super.onDestroy()
    }




}