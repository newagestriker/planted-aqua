package com.newage.plantedaqua.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.DatabaseUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.newage.plantedaqua.BuildConfig
import com.newage.plantedaqua.R
import com.newage.plantedaqua.dbhelpers.ExpenseDBHelper
import com.newage.plantedaqua.dbhelpers.TankDBHelper
import com.newage.plantedaqua.helpers.TinyDB
import com.newage.plantedaqua.models.TanksDetails
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*

class CreateTankActivity : AppCompatActivity() {
    private val REQUEST_CAMERA = 4
    private val SELECT_FILE = 5
    private var tankImage: ImageView? = null
    private var tankpicUri: Uri? = null
    private var image: File? = null
    private var aquaname = ""
    private var aquatype = ""
    private var startupdate = "00-00-0000"
    private var ID = ""
    private var s = ""
    private var dt = ""
    private var position = 0
    private var price: String? = null
    private var currency: String? = null
    private var volume: String? = null
    private var volumemetric: String? = null
    private var currentstatus: String? = null
    private var dismantledate: String? = null
    private var co2: String? = null
    private var lighttype: String? = null
    private var wattage: String? = null
    private var lumensperwatt: String? = null
    private var linearLayout: LinearLayout? = null
    var newImageCreated = false
    private var settingsDB: TinyDB? = null
    private var lightZone : String? = null
    private var requestPermissionCard: CardView? = null
    private var spotsProgressDialog: AlertDialog? = null
    private var removeStartDate: ImageView? = null
    private var removeEndDate: ImageView? = null
    private val REQ_CODE_TO_SELECT_IMAGE = 1
    private val REQ_CODE_FOR_BUTTON_VISIBILITY = 2
    fun removeDate(view: View) {
        if (view.tag.toString() == "start") {
            StartupDate!!.setText("")
            removeStartDate!!.visibility = View.GONE
        } else {
            DismantleDate!!.setText("")
            removeEndDate!!.visibility = View.GONE
        }
    }

    private interface PermissionGranted {
        fun onPermissionsAvailable()
        fun onPermissionsNotAvailable()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_tank_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val TANK_NAME_PRESENT: Boolean
        if (item.itemId == R.id.Save) {
            if (tankpicUri != null) {
                s = tankpicUri.toString()
            }
            val i = intent
            val mode = i.getStringExtra("mode")
            TANK_NAME_PRESENT = allData
            if (TANK_NAME_PRESENT) {
                spotsProgressDialog!!.show()
                val tankDBHelper = TankDBHelper.newInstance(this)
                val DB = tankDBHelper.writableDatabase
                val expenseDBHelper = ExpenseDBHelper.getInstance(this)
                val numericPrice = price!!.replace(",", ".").toFloat()
                val tanksDetails = TanksDetails()
                tanksDetails.tankID = ID
                tanksDetails.tankName = aquaname
                tanksDetails.tankType = aquatype
                tanksDetails.tankPrice = price!!
                tanksDetails.currency = currency!!
                tanksDetails.tankVolume = volume!!
                tanksDetails.tankVolumeMetric = volumemetric!!
                tanksDetails.tankStatus = currentstatus!!
                tanksDetails.tankStartDate = startupdate
                tanksDetails.tankEndDate = dismantledate!!
                tanksDetails.tankCO2Supply = co2!!
                tanksDetails.tankPicUri = s
                if (mode == "creation") {
                    val rowid = DatabaseUtils.queryNumEntries(DB, "TankDetails")
                    tankDBHelper.addData(DB, rowid, ID, aquaname, s, aquatype, price, currency, volume, volumemetric, currentstatus, startupdate, dismantledate, co2, lighttype, wattage, lumensperwatt, "")
                    expenseDBHelper.addDataExpense(expenseDBHelper.writableDatabase, ID, aquaname, aquaname, dy, mnth, yr, startupdate, 0L, 1, numericPrice, numericPrice, "", ID)
                }
                if (mode == "modification") {
                    dy = startupdate.split("-".toRegex()).toTypedArray()[2].toInt()
                    mnth = startupdate.split("-".toRegex()).toTypedArray()[1].toInt()
                    yr = startupdate.split("-".toRegex()).toTypedArray()[0].toInt()
                    tankDBHelper.updateItem(DB, ID, aquaname, s, aquatype, price, currency, volume, volumemetric, currentstatus, startupdate, dismantledate, co2, lighttype, wattage, lumensperwatt, "")
                    if (expenseDBHelper.checkExists(ID)) {
                        expenseDBHelper.updateDataExpense(expenseDBHelper.writableDatabase, ID, aquaname, aquaname, dy, mnth, yr, startupdate, 0L, 1, numericPrice, numericPrice, "", ID)
                    } else {
                        expenseDBHelper.addDataExpense(expenseDBHelper.writableDatabase, ID, aquaname, aquaname, dy, mnth, yr, startupdate, 0L, 1, numericPrice, numericPrice, "", ID)
                    }
                    expenseDBHelper.updateExpenseItem("AquariumID", ID, "TankName", aquaname)
                }
                checkCO2level()
                val intent = Intent()
                intent.putExtra("Aquaname", aquaname)
                intent.putExtra("Aquatype", aquatype)
                intent.putExtra("ImageUri", s)
                intent.putExtra("StartupDate", startupdate)
                intent.putExtra("Tag", ID)
                intent.putExtra("Position", position)
                intent.putExtra("TankItemsObject", tanksDetails)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tank_details)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.TankData)
        tankImage = findViewById(R.id.TankImage)
        linearLayout = findViewById(R.id.LinearTankDetails)
        val requestPermissionButton = findViewById<Button>(R.id.requestPermissionButton)
        requestPermissionCard = findViewById(R.id.requestPermissionCard)
        removeStartDate = findViewById(R.id.removeStartDate)
        removeEndDate = findViewById(R.id.removeEndDate)
        StartupDate = findViewById(R.id.StartupDateInput)
        DismantleDate = findViewById(R.id.DismantleDateInput)
        checkPermissions(object : PermissionGranted {
            override fun onPermissionsAvailable() {
                requestPermissionCard!!.visibility = View.GONE
            }

            override fun onPermissionsNotAvailable() {
                requestPermissionCard!!.visibility = View.VISIBLE
            }
        })
        requestPermissionButton.setOnClickListener { v: View? -> requestPermissions(REQ_CODE_FOR_BUTTON_VISIBILITY) }
        settingsDB = TinyDB(applicationContext)
        val intent = intent
        val mode = intent.getStringExtra("mode")
        if (mode == "creation") {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.getDefault()).format(Date())
            val rnd = Random().nextInt(10000)
            ID = timeStamp + "_" + rnd
        }
        position = intent.getIntExtra("Position", -1)
        if (position >= 0) {
            val tankDBHelper = TankDBHelper.newInstance(this)
            val DB = tankDBHelper.writableDatabase
            val c = tankDBHelper.getData(DB)
            if (c.count > 0) {
                c.moveToPosition(position)
                setAlldata(c)
            }
            c.close()
        }
        spotsProgressDialog = SpotsDialog.Builder()
                .setContext(this)
                .setTheme(R.style.ProgressDotsStyle)
                .build()
        var calendarIcon = findViewById<ImageView>(R.id.startupCalendar)
        calendarIcon.setOnClickListener { selectDate(R.id.StartupDateInput) }
        calendarIcon = findViewById(R.id.dismantleCalendar)
        calendarIcon.setOnClickListener { selectDate(R.id.DismantleDateInput) }
        val loadImage = findViewById<ImageView>(R.id.loadImage)
        loadImage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPermissions(object : PermissionGranted {
                    override fun onPermissionsAvailable() {
                        requestPermissionCard!!.visibility = View.GONE
                        selectImage()
                    }

                    override fun onPermissionsNotAvailable() {
                        requestPermissions(REQ_CODE_TO_SELECT_IMAGE)
                    }
                })
            } else {
                selectImage()
            }
        }
        val Currency = findViewById<TextView>(R.id.AquariumCurrencyInput)
        Currency.text = settingsDB!!.getString("DefaultCurrencySymbol")
    }

    private var StartupDate: EditText? = null
    private var DismantleDate: EditText? = null
    private val allData: Boolean
        private get() {
            val AquaName = findViewById<EditText>(R.id.AquariumNameInput)
            return if (!AquaName.text.toString().isEmpty()) {
                aquaname = AquaName.text.toString()
                startupdate = if (TextUtils.isEmpty(StartupDate!!.text.toString())) "0000-00-00" else StartupDate!!.text.toString()
                dismantledate = DismantleDate!!.text.toString()
                val Price = findViewById<EditText>(R.id.AquariumPriceInput)
                price = if (TextUtils.isEmpty(Price.text.toString())) "0.0" else Price.text.toString()
                val LightType = findViewById<EditText>(R.id.AquariumLightInput)
                lighttype = LightType.text.toString()
                val Wattage = findViewById<EditText>(R.id.AquariumWattageInput)
                wattage = Wattage.text.toString()
                val LumensPerWatt = findViewById<EditText>(R.id.LumensPerWatt)
                lumensperwatt = LumensPerWatt.text.toString()
                val Currency = findViewById<TextView>(R.id.AquariumCurrencyInput)
                currency = Currency.text.toString()
                val Volume = findViewById<EditText>(R.id.AquariumVolumeInput)
                volume = if (TextUtils.isEmpty(Volume.text.toString())) "0" else Volume.text.toString()
                val AquariumType = findViewById<RadioGroup>(R.id.AquariumTypeGroup)
                var selectedradiobutton = findViewById<RadioButton>(AquariumType.checkedRadioButtonId)
                aquatype = selectedradiobutton.text.toString()
                val CurrentStatus = findViewById<RadioGroup>(R.id.AquariumUseGroup)
                selectedradiobutton = findViewById(CurrentStatus.checkedRadioButtonId)
                currentstatus = selectedradiobutton.text.toString()
                val CO2 = findViewById<RadioGroup>(R.id.AquariumCO2Group)
                selectedradiobutton = findViewById(CO2.checkedRadioButtonId)
                co2 = selectedradiobutton.text.toString()
                val VolumeMetric = findViewById<Spinner>(R.id.AquariumVolMetricSpinner)
                volumemetric = VolumeMetric.selectedItem.toString()
                true
            } else {
                Snackbar.make(linearLayout!!, resources.getString(R.string.TankNameMust), Snackbar.LENGTH_LONG).show()
                false
            }
        }

    private fun setAlldata(c: Cursor) {
        val AquaName = findViewById<EditText>(R.id.AquariumNameInput)
        AquaName.setText(c.getString(2))
        val Price = findViewById<EditText>(R.id.AquariumPriceInput)
        Price.setText(c.getString(5))
        val LightType = findViewById<EditText>(R.id.AquariumLightInput)
        LightType.setText(c.getString(13))
        val Wattage = findViewById<EditText>(R.id.AquariumWattageInput)
        Wattage.setText(c.getString(14))
        val StartupDate = findViewById<EditText>(R.id.StartupDateInput)
        StartupDate.setText(c.getString(10))
        if (c.getString(10) == "") {
            removeStartDate!!.visibility = View.GONE
        } else {
            removeStartDate!!.visibility = View.VISIBLE
        }
        val DismantleDate = findViewById<EditText>(R.id.DismantleDateInput)
        DismantleDate.setText(c.getString(11))
        if (c.getString(11) == "") {
            removeEndDate!!.visibility = View.GONE
        } else {
            removeEndDate!!.visibility = View.VISIBLE
        }
        val LumensPerWatt = findViewById<EditText>(R.id.LumensPerWatt)
        LumensPerWatt.setText(c.getString(15))
        val Currency = findViewById<TextView>(R.id.AquariumCurrencyInput)
        Currency.text = settingsDB!!.getString("DefaultCurrencySymbol")
        val Volume = findViewById<EditText>(R.id.AquariumVolumeInput)
        Volume.setText(c.getString(7))
        val AquariumType = findViewById<RadioGroup>(R.id.AquariumTypeGroup)
        for (i in 0 until AquariumType.childCount) {
            if (c.getString(4) == (AquariumType.getChildAt(i) as RadioButton).text.toString()) {
                (AquariumType.getChildAt(i) as RadioButton).isChecked = true
                break
            }
        }
        val CurrentStatus = findViewById<RadioGroup>(R.id.AquariumUseGroup)
        for (i in 0 until CurrentStatus.childCount) {
            if (c.getString(9) == (CurrentStatus.getChildAt(i) as RadioButton).text.toString()) {
                (CurrentStatus.getChildAt(i) as RadioButton).isChecked = true
                break
            }
        }
        val CO2 = findViewById<RadioGroup>(R.id.AquariumCO2Group)
        for (i in 0 until CO2.childCount) {
            if (c.getString(12) == (CO2.getChildAt(i) as RadioButton).text.toString()) {
                (CO2.getChildAt(i) as RadioButton).isChecked = true
                break
            }
        }
        val volumemetric = arrayOf("Litre", "US Gallon", "UK Gallon")
        var pos = 0
        for (i in 0..2) {
            if (c.getString(8) == volumemetric[i]) {
                pos = i
                break
            }
        }
        val VolumeMetric = findViewById<Spinner>(R.id.AquariumVolMetricSpinner)
        VolumeMetric.setSelection(pos)
        ID = c.getString(1)
        s = c.getString(3)
        tankImage = findViewById(R.id.TankImage)
        s = c.getString(3)
        loadImageIntoImageView()
    }

    private fun loadImageIntoImageView() {
        tankpicUri = Uri.parse(s)
        Glide.with(this)
                .load(tankpicUri)
                .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.drawable.aquarium2))
                .into(tankImage!!)
        tankImage!!.isFocusable = true
        tankImage!!.isClickable = true
    }

    var dy = 0
    var mnth = 0
    var yr = 0
    private fun selectDate(idName: Int) {
        val newCalendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this@CreateTankActivity, OnDateSetListener { view, year, month, dayOfMonth ->
            dt = convertToString(year) + "-" + convertToString(month + 1) + "-" + convertToString(dayOfMonth)
            val dateText1 = findViewById<EditText>(idName)
            dateText1.setText(dt)
            if (idName == R.id.StartupDateInput) {
                dy = dayOfMonth
                mnth = month + 1
                yr = year
                removeStartDate!!.visibility = View.VISIBLE
            } else {
                removeEndDate!!.visibility = View.VISIBLE
            }
        }, newCalendar[Calendar.YEAR], newCalendar[Calendar.MONTH], newCalendar[Calendar.DAY_OF_MONTH])
        datePickerDialog.show()
    }

    private fun convertToString(num: Int): String {
        return if (num < 10) "0$num" else Integer.toString(num)
    }

    private fun selectImage() {
        val filedeleted: Boolean
        val items = arrayOf<CharSequence>("Camera", "Gallery", "Cancel")
        val timeStamp = SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.getDefault()).format(Date())
        val rnd = Random().nextInt(10000)
        val tempID = timeStamp + "_" + rnd
        val imageFileName = "TankNo_" + tempID + "_Pic.jpg"
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
            val resolver: ContentResolver = contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            tankpicUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
        }

        else {
            val imagesFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "JPT_images")
            if (!imagesFolder.exists()) {
                val dirCreated = imagesFolder.mkdirs()
            }
            if (imagesFolder.isDirectory) {

                image = File(imagesFolder, imageFileName)
                tankpicUri = if (Build.VERSION.SDK_INT >= 24) {
                    FileProvider.getUriForFile(this@CreateTankActivity,
                            BuildConfig.APPLICATION_ID + ".provider",
                            image!!)
                } else {
                    Uri.fromFile(image)
                }
                if (image!!.exists()) filedeleted = image!!.delete()
                Timber.d("Create Tank Uri%s", tankpicUri.toString())
            }
        }
        val builder = androidx.appcompat.app.AlertDialog.Builder(this@CreateTankActivity)
        builder.setTitle("Get Image from")
        builder.setItems(items) { dialog, which ->
            if (items[which] == "Camera") {
                val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                i.putExtra(MediaStore.EXTRA_OUTPUT, tankpicUri)
                startActivityForResult(i, REQUEST_CAMERA)
            } else if (items[which] == "Gallery") {
                val i = Intent(Intent.ACTION_PICK)
                i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                startActivityForResult(Intent.createChooser(i, "Select Image Using"), SELECT_FILE)
            } else {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fileCreated: Boolean
        if (resultCode == Activity.RESULT_OK) {
            newImageCreated = true
            if (requestCode == REQUEST_CAMERA) {
                Glide.with(this)
                        .load(tankpicUri)
                        .apply(RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(R.drawable.aquarium2))
                        .into(tankImage!!)
            }
            if (requestCode == SELECT_FILE) {
                try {
                    fileCreated = image!!.createNewFile()
                    if (fileCreated) {
                        try {
                            image!!.createNewFile()
                            copyFileWithUri(data!!.data!!,tankpicUri!!)
                        } catch (e: Exception) {
                            val msg = if (TextUtils.isEmpty(e.message)) getString(R.string.cloud_storage_error) else e.message!!
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    val msg = if (TextUtils.isEmpty(e.message)) resources.getString(R.string.unknown_error) else e.message!!
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                }

            }
            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q) {
                val scanFileIntent = Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, tankpicUri)
                sendBroadcast(scanFileIntent)
            }
        }

    }

    var permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    private fun checkPermissions(permissionGranted: PermissionGranted) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this.applicationContext, permissions[0]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.applicationContext, permissions[1]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.applicationContext, permissions[2]) == PackageManager.PERMISSION_GRANTED) {
                permissionGranted.onPermissionsAvailable()
            } else {
                permissionGranted.onPermissionsNotAvailable()
            }
        }
    }

    private fun requestPermissions(req_code: Int) {
        ActivityCompat.requestPermissions(this@CreateTankActivity, permissions, req_code)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQ_CODE_TO_SELECT_IMAGE) {
                selectImage()
            }
            loadImageIntoImageView()
            requestPermissionCard!!.visibility = View.GONE
        } else {
            Toast.makeText(this, resources.getString(R.string.PermRationale), Toast.LENGTH_LONG).show()
            requestPermissionCard!!.visibility = View.VISIBLE
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Throws(IOException::class)
    private fun copyFile(sourceFile: File, destFile: File?) {
        if (!sourceFile.exists()) {
            return
        }
        val source: FileChannel? = FileInputStream(sourceFile).channel
        val destination: FileChannel = FileOutputStream(destFile).channel
        if (source != null) {
            destination.transferFrom(source, 0, source.size())
        }
        source?.close()
        destination.close()
    }

    private fun getRealPathFromURI(contentUri: Uri?): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = contentResolver.query(contentUri!!, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor.close()
        return res
    }

    private fun checkCO2level() {
        var title = ""
        var visibility = "1"
        var recoString = ""
        val tankDBHelper = TankDBHelper.newInstance(this)
        val c = tankDBHelper.getDataCondition("AquariumID", ID)
        c?.let {


            if (it.moveToFirst()) {
                lightZone = it.getString(16)
            }
            it.close()
            if (!TextUtils.isEmpty(lightZone)) {
                if (lightZone != "Insufficient Data") {
                    if ((lightZone == "Dark" || lightZone == "Low") && co2 != "Air") {
                        recoString = "Your tank is receiving LOW light. There is probably no need for additional CO2 supplementation"
                        title = "INFO"
                    } else if (lightZone == "Medium" && co2 == "Air") {
                        recoString = "Your tank is receiving MEDIUM light. As such CO2 supplementation though not entirely necessary can prove beneficial. Opt for Liquid CO2 or at least DIY if not Pressurized "
                        title = "INFO"
                    } else if (lightZone == "Medium High" && (co2 == "Air" || co2 == "Liquid")) {
                        recoString = "Your tank is receiving MEDIUM to HIGH light. As such gaseous CO2 injection is an absolute necessity else it will led to algae growth. At least DIY if not pressurized is recommended "
                        title = "ALERT"
                    } else if ((lightZone == "High" || lightZone == "Very High") && co2 != "Pressurized") {
                        recoString = "Your tank is receiving HIGH light. As such pressurized CO2 injection is an absolute necessity else it will led to algae growth"
                        title = "ALERT"
                    } else {
                        visibility = "0"
                    }
                }
                val timeStamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val day = SimpleDateFormat("EE", Locale.getDefault()).format(Date())
                val cr = tankDBHelper.getDataRecoCondition(tankDBHelper.writableDatabase, ID)
                if (cr != null) {
                    if (cr.moveToFirst()) {
                        tankDBHelper.updateDataReco(1, ID, day, timeStamp, title, recoString, visibility, aquaname)
                    } else {
                        tankDBHelper.addDataReco(1, ID, day, timeStamp, title, recoString, visibility, aquaname)
                    }
                    cr.close()
                }
            }
        }
    }

    private fun copyFileWithUri(sourceUri: Uri, destUri: Uri) {

        val imageFileCopyJob = SupervisorJob()
        CoroutineScope(Dispatchers.IO + imageFileCopyJob).launch {
            applicationContext.contentResolver.openOutputStream(destUri).use { oStream -> applicationContext.contentResolver.openInputStream(sourceUri)?.use { iStream -> oStream!!.write(iStream.readBytes()) } }
            withContext(Dispatchers.Main){
            Glide.with(this@CreateTankActivity)
                    .load(tankpicUri)
                    .apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.drawable.aquarium2))
                    .into(tankImage!!)
                    }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (newImageCreated) {
            image!!.delete()
        }
    }

    override fun onStop() {
        if (spotsProgressDialog != null) {
            spotsProgressDialog!!.dismiss()
        }
        super.onStop()
    }
}