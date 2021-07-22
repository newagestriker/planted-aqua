package com.newage.plantedaqua.activities

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.newage.plantedaqua.BuildConfig
import com.newage.plantedaqua.R
import com.newage.plantedaqua.adapters.RecyclerAdapterSellerItems
import com.newage.plantedaqua.helpers.CloudNotificationHelper
import com.newage.plantedaqua.helpers.TinyDB
import com.newage.plantedaqua.models.SellerItemsDescription
import com.newage.plantedaqua.services.filewebservices.IFileWebService
import com.newage.plantedaqua.services.permissionservices.IActivityCompatPermissionService
import org.json.JSONArray
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.io.*
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*

class SellerActivity : AppCompatActivity() {
    private val REQUEST_CAMERA = 56
    private val SELECT_FILE = 89
    private var sellerItemsDescriptionArrayList: ArrayList<SellerItemsDescription>? = null
    private var mode: String? = null
    private var mAuth: FirebaseAuth? = null
    private var settingsDB: TinyDB? = null
    private lateinit var JSONStorageRef: StorageReference
    private lateinit var JSONFileUri: Uri
    private lateinit var permissions: Array<String>
    private lateinit var activity: Activity
    private var permissionRequestCode: Int = 145
    private val activityCompatPermissionService: IActivityCompatPermissionService by inject {
        parametersOf(permissions, activity, permissionRequestCode)
    }
    private val fileWebService: IFileWebService by inject {
        parametersOf(
            JSONStorageRef,
            JSONFileUri
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        settingsDB = TinyDB(this.applicationContext)
        User_ID = intent.getStringExtra("User")
        if (User_ID == "Own") {
            mode = "Own"
            SellerSpeciality!!.hint = resources.getString(R.string.Specialization)
            mAuth = FirebaseAuth.getInstance()
            User_ID = mAuth!!.currentUser!!.uid
            SellerName!!.text = mAuth!!.currentUser!!.displayName
            if (mAuth!!.currentUser!!.photoUrl != null) {
                if (!mAuth!!.currentUser!!.photoUrl.toString().isEmpty()) {
                    sellerPhoto!!.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(mAuth!!.currentUser!!.photoUrl)
                        .error(R.drawable.profle)
                        .into(sellerPhoto!!)
                }
            } else {
                sellerPhoto!!.visibility = View.GONE
            }
            menuInflater.inflate(R.menu.seller_menu, menu)
        } else {
            mode = "Other"
            SellerName!!.text = intent.getStringExtra("DN")
            SellerSpeciality!!.isFocusable = false
            SellerSpeciality!!.isClickable = false
            SellerContact!!.isFocusable = false
            SellerContact!!.isClickable = false
            if (!TextUtils.isEmpty(intent.getStringExtra("PU"))) {
                sellerPhoto!!.visibility = View.VISIBLE
                Glide.with(this)
                    .load(Uri.parse(intent.getStringExtra("PU")))
                    .error(R.drawable.profle)
                    .into(sellerPhoto!!)
            } else {
                sellerPhoto!!.visibility = View.GONE
            }
        }
        SellerRef = FirebaseDatabase.getInstance().getReference("UI")
            .child(User_ID!!) // Define Seller Ref only after the User_ID is identified
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyPermissions()
        } else {
            fromDatabase
        }
        return super.onCreateOptionsMenu(menu)
    }

    var itemImage: ImageView? = null
    var sellerItemName: EditText? = null
    var sellerItemDes: EditText? = null
    var sellerItemQuan: EditText? = null
    var sellerItemPrice: EditText? = null
    var sellerItemAvail: EditText? = null
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.seller_items_layout, null)
        itemImage = view.findViewById(R.id.ItemImage)
        sellerItemName = view.findViewById(R.id.SellerItemName)
        sellerItemDes = view.findViewById(R.id.SellerItemDes)
        sellerItemQuan = view.findViewById(R.id.SellerItemQuan)
        sellerItemPrice = view.findViewById(R.id.SellerItemPrice)
        sellerItemAvail = view.findViewById(R.id.SellerItemAvail)
        itemImage?.setImageDrawable(resources.getDrawable(R.drawable.noimage))
        itemImage?.setOnClickListener(View.OnClickListener { selectImage() })
        builder.setView(view)
            .setTitle(resources.getString(R.string.AddItems))
            .setPositiveButton(resources.getString(R.string.OK)) { dialog, which -> allData }
            .setNegativeButton(resources.getString(R.string.Cancel)) { dialog, which -> dialog.cancel() }
            .setCancelable(false).create()
        builder.show()
        return super.onOptionsItemSelected(item)
    }

    var sellerItemsDescription = SellerItemsDescription()
    var SellerRef: DatabaseReference? = null//Log.i("Image uri",u);

    //Log.i("Image uri","Success");
    //UPLOAD TO FIREBASE
    private val allData: Unit
        private get() {
            sellerItemsDescription = SellerItemsDescription()
            sellerItemsDescription.itemName = sellerItemName!!.text.toString()
            sellerItemsDescription.itemQuantity = sellerItemQuan!!.text.toString()
            sellerItemsDescription.itemAvailability = sellerItemAvail!!.text.toString()
            sellerItemsDescription.itemDescription = sellerItemDes!!.text.toString()
            sellerItemsDescription.itemPrice = sellerItemPrice!!.text.toString()
            SellerRef!!.child("SP").setValue(SellerSpeciality!!.text.toString())
            SellerRef!!.child("SC").setValue(SellerContact!!.text.toString())


            //UPLOAD TO FIREBASE
            if (image != null) {
                val timeStamp = SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.getDefault()).format(
                    Date()
                )
                val rnd = Random().nextInt(10000)
                tempImageID = timeStamp + "_" + rnd + ".jpg"
                val storageReference = FirebaseStorage.getInstance().getReference("Seller")
                val itemImages = storageReference.child("$User_ID/$tempImageID")
                progressDialog!!.show()
                itemImage!!.isDrawingCacheEnabled = true
                itemImage!!.buildDrawingCache()
                val bitmap = (itemImage!!.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val uploadTask = itemImages.putBytes(data)
                uploadTask.addOnFailureListener {
                    Toast.makeText(this@SellerActivity, "IMAGE UPLOAD ERROR!!", Toast.LENGTH_SHORT)
                        .show()
                    image!!.delete()
                    progressDialog!!.dismiss()
                }.addOnSuccessListener {
                    itemImages.downloadUrl.addOnSuccessListener { uri ->
                        val u = uri.toString()
                        //Log.i("Image uri",u);
                        //Log.i("Image uri","Success");
                        sellerItemsDescription.itemImage = u
                        sellerItemsDescription.itemImagePath = tempImageID
                        image!!.delete()
                        uploadSellerJSONFile()
                        progressDialog!!.dismiss()
                        SellerRef!!.child("IC").setValue("1")
                    }
                }
            } else {
                sellerItemsDescription.itemImage = ""
                sellerItemsDescription.itemImagePath = tempImageID
                uploadSellerJSONFile()
            }
        }
    private var User_ID: String? = ""
    private var progressDialog: ProgressDialog? = null
    private var SellerName: TextView? = null
    private var sellerPhoto: ImageView? = null
    private var SellerSpeciality: EditText? = null
    private var SellerContact: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar!!.title = resources.getString(R.string.Shop)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.setMessage(resources.getString(R.string.Uploading))
        SellerName = findViewById(R.id.SellerName)
        sellerPhoto = findViewById(R.id.SellerImage)
        SellerSpeciality = findViewById(R.id.SellerSpeciality)
        SellerContact = findViewById(R.id.SellerContact)
        SellerSpeciality?.setText(resources.getString(R.string.NoInfo))
        val StorageRef = FirebaseStorage.getInstance().getReference("Seller")
        JSONStorageRef = StorageRef.child("$User_ID/JSON1.JSON")
        JSONFileUri = Uri.EMPTY
    }

    var JSONtext: String? =
        null//Log.i("JSON DOWNLOAD FAIL",e.getMessage());//Log.i("DOWNLOAD ERROR JSON",e.getMessage());//Log.i("InsertPosition",Integer.toString(i));

    //populateList();
//Log.i("JSON TEXT", JSONtext);////Log.i("Snapshot",dataSnapshot.getValue(String.class));
    //Log.i("REALTIME","not null");
    private val fromDatabase: Unit
        private get() {
            if (SellerRef != null) {

                //Log.i("REALTIME","not null");
                SellerRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        ////Log.i("Snapshot",dataSnapshot.getValue(String.class));
                        SellerSpeciality!!.setText(
                            dataSnapshot.child("SP").getValue(String::class.java)
                        )
                        SellerContact!!.setText(
                            dataSnapshot.child("SC").getValue(String::class.java)
                        )
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(
                            this@SellerActivity,
                            databaseError.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
            sellerItemsDescriptionArrayList = ArrayList()
            val recyclerView = findViewById<RecyclerView>(R.id.SellerItemRecyclerView)
            adapter =
                RecyclerAdapterSellerItems(this, sellerItemsDescriptionArrayList, User_ID, mode)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            val tempJSON = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "temp.JSON"
            )
            try {
                if (tempJSON.exists()) tempJSON.delete()
                tempJSON.createNewFile()
                val StorageRef = FirebaseStorage.getInstance().getReference("Seller")
                val JSONStorageRef = StorageRef.child("$User_ID/JSON1.JSON")
                JSONStorageRef.getFile(tempJSON)
                    .addOnSuccessListener {
                        try {
                            if (tempJSON.length() != 0L) {
                                val scanner = Scanner(tempJSON)
                                JSONtext = if (scanner.useDelimiter("\\A")
                                        .hasNext()
                                ) scanner.next() else ""
                                scanner.close()
                                //Log.i("JSON TEXT", JSONtext);
                                val jsonArray = JSONArray(JSONtext)
                                val gson = Gson()
                                var jsonObject: JSONObject
                                for (i in 0 until jsonArray.length()) {
                                    jsonObject = jsonArray.getJSONObject(i)
                                    sellerItemsDescriptionArrayList!!.add(
                                        gson.fromJson(
                                            jsonObject.toString(),
                                            SellerItemsDescription::class.java
                                        )
                                    )
                                    //Log.i("InsertPosition",Integer.toString(i));
                                }
                                adapter?.notifyDataSetChanged()
                                SellerRef!!.child("IC")
                                    .setValue(if (sellerItemsDescriptionArrayList!!.isEmpty()) "0" else "1")
                                if (sellerItemsDescriptionArrayList!!.isEmpty()) {
                                    Toast.makeText(
                                        this@SellerActivity,
                                        "NO ITEMS LISTED",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                //populateList();
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@SellerActivity, e.message, Toast.LENGTH_LONG).show()
                            //Log.i("DOWNLOAD ERROR JSON",e.getMessage());
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@SellerActivity, "NO ITEMS LISTED", Toast.LENGTH_LONG)
                            .show()
                        //Log.i("JSON DOWNLOAD FAIL",e.getMessage());
                    }
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }
    var adapter: RecyclerView.Adapter<*>? = null
    var tempID = ""
    var tankpicUri: Uri? = null
    var image: File? = null
    fun selectImage() {
        val filedeleted: Boolean
        val items = arrayOf<CharSequence>("Camera", "Gallery", "Cancel")
        val imagesFolder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "JPT_images"
        )
        if (!imagesFolder.exists()) {
            val dirCreated = imagesFolder.mkdirs()
        }
        if (imagesFolder.isDirectory) {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.getDefault()).format(
                Date()
            )
            val rnd = Random().nextInt(10000)
            tempID = timeStamp + "_" + rnd
            image = File(imagesFolder, tempID + "_Pic.jpg")
            tankpicUri = if (Build.VERSION.SDK_INT >= 24) {
                FileProvider.getUriForFile(
                    this@SellerActivity,
                    BuildConfig.APPLICATION_ID + ".provider",
                    image!!
                )
            } else {
                Uri.fromFile(image)
            }
            if (image!!.exists()) filedeleted = image!!.delete()
        }
        val builder = AlertDialog.Builder(this@SellerActivity)
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

    var tempImageID = ""
    var newImageCreated = false
    var tankPicUriFromGallery: Uri? = null
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fileCreated: Boolean
        if (resultCode == RESULT_OK) {
            newImageCreated = true
            if (requestCode == REQUEST_CAMERA) {
                Glide.with(this)
                    .load(tankpicUri)
                    .apply(
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.drawable.aquarium2)
                    )
                    .into(itemImage!!)
            }
            if (requestCode == SELECT_FILE) {
                tankPicUriFromGallery = data!!.data
                try {
                    fileCreated = image!!.createNewFile()
                    if (fileCreated) {
                        try {
                            image!!.createNewFile()
                            copyFile(File(getRealPathFromURI(data.data)), image)
                        } catch (e: IOException) {
                            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
                Glide.with(this)
                    .load(tankPicUriFromGallery)
                    .apply(
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.drawable.aquarium2)
                    )
                    .into(itemImage!!)
            }
            val scanFileIntent = Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, tankpicUri
            )
            sendBroadcast(scanFileIntent)
        }


        /* Uri imageuri=data.getData();
                itemImage.setImageURI(imageuri);



                Intent i=new Intent(Intent.ACTION_VIEW);

                i.setDataAndType(selectImageUri, "image/ *");
                startActivity(i);*/
    }

    private fun onSuccess() {
        Log.i("SellerActivity", "Sucesss")
    }

    private fun onFailure() {
        Log.i("SellerActivity", "Failure")
    }

    private fun onError() {
        Log.i("SellerActivity", "Error")
    }

    private fun uploadSellerJSONFile1(){


    }
    private fun uploadSellerJSONFile() {
        //  fileWebService.fileUpload({ onSuccess() }, { onFailure() }) { onError()}
        Log.i("SellerActivity", "Inside Upload")
        sellerItemsDescriptionArrayList!!.add(sellerItemsDescription)
        adapter!!.notifyItemInserted(sellerItemsDescriptionArrayList!!.size - 1)
        val gson = Gson()
        val SellerItemsJSON: String
        val stringBuilder = StringBuilder()
        stringBuilder.append("[")
        var comma = ""
        for (SID in sellerItemsDescriptionArrayList!!) {
            stringBuilder.append(comma)
            stringBuilder.append(gson.toJson(SID))
            comma = ","
        }
        stringBuilder.append("]")
        SellerItemsJSON = stringBuilder.toString()
        val tempJSON: File
        try {
            tempJSON = File(
                getExternalFilesDir(null),
                "temp.JSON"
            )
            tempJSON.createNewFile()
            val output: Writer = BufferedWriter(FileWriter(tempJSON))
            output.write(SellerItemsJSON)
            output.close()


            JSONFileUri = if (Build.VERSION.SDK_INT >= 24) {
                FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", tempJSON)
            } else Uri.fromFile(tempJSON)
            //Log.i("URI",uri.toString());
            progressDialog!!.show()
            JSONStorageRef.putFile(JSONFileUri)
                .addOnSuccessListener {
                    tempJSON.delete()
                    progressDialog!!.dismiss()
                    msgAllUsers()
                    // adapter.notifyItemInserted((sellerItemsDescriptionArrayList.size()-1));
                    //adapter.notifyDataSetChanged();
                    //Log.i("InsertPosition",Integer.toString(sellerItemsDescriptionArrayList.size()-1));
                    Log.i("SellerActivity", "Upload success")
                }
                .addOnFailureListener {
                    Toast.makeText(this@SellerActivity, "UPLOAD ERROR!!", Toast.LENGTH_SHORT).show()
                    //Log.i("JSON upload error",e.getMessage());
                    tempJSON.delete()
                    progressDialog!!.dismiss()
                    deleteUploadedItems(tempImageID)
                    sellerItemsDescriptionArrayList!!.removeAt(sellerItemsDescriptionArrayList!!.size - 1)
                    adapter!!.notifyItemRemoved(sellerItemsDescriptionArrayList!!.size - 1)
                    Log.i("SellerActivity", "Upload fail")
                }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            Log.i("SellerActivity", e.message!!)
            //Log.i("TEMP FILE ERROR",e.getMessage());
        }

    }

    private fun deleteUploadedItems(fileID: String) {
        if (image != null) {
            val storageReference = FirebaseStorage.getInstance().getReference("Seller")
            val imageRef = storageReference.child("$User_ID/$fileID")
            imageRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(
                        this@SellerActivity,
                        "Item deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this@SellerActivity,
                        "Deletion Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun msgAllUsers() {
        val DN: String?
        val PU: String
        DN = if (mAuth!!.currentUser!!.displayName == null) {
            "An Unknown Seller"
        } else {
            mAuth!!.currentUser!!.displayName
        }
        PU = if (mAuth!!.currentUser!!.photoUrl == null) {
            ""
        } else {
            mAuth!!.currentUser!!.photoUrl.toString()
        }
        val cloudNotificationHelper = CloudNotificationHelper(this)
        cloudNotificationHelper.sendCloudNotification(
            "$DN has new items in shop",
            PU,
            CloudNotificationHelper.MsgType.allUsers,
            User_ID,
            "All",
            "New Items Alert",
            DN,
            "seller_update"
        )
    }

    private fun verifyPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    permissions[0]
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    permissions[1]
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    permissions[2]
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fromDatabase
                if (mode == "Own") {
                    SellerRef!!.child("UT").setValue(settingsDB!!.getString("UserType"))
                    helpSellers()
                }
            } else {
                ActivityCompat.requestPermissions(this@SellerActivity, permissions, 1)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            fromDatabase
        } else {
            Toast.makeText(this, resources.getString(R.string.PermRationale), Toast.LENGTH_LONG)
                .show()
            finish()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Throws(IOException::class)
    private fun copyFile(sourceFile: File, destFile: File?) {
        if (!sourceFile.exists()) {
            return
        }
        val source: FileChannel? = FileInputStream(sourceFile).channel
        val destination: FileChannel? = FileOutputStream(destFile).channel
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size())
        }
        source?.close()
        destination?.close()
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

    override fun onBackPressed() {
        super.onBackPressed()
        SellerRef!!.child("SP").setValue(SellerSpeciality!!.text.toString())
        SellerRef!!.child("SC").setValue(SellerContact!!.text.toString())
        if (newImageCreated) {
            image!!.delete()
        }
    }

    private fun helpSellers() {
        val cardFixSeller = findViewById<CardView>(R.id.card_seller_fix)
        val cardFixLocation = findViewById<CardView>(R.id.card_location_fix)
        val cardAddContact = findViewById<CardView>(R.id.card_contact_add)
        val makeSeller = findViewById<Button>(R.id.make_seller)
        val gotoMap = findViewById<Button>(R.id.go_to_Map)
        cardFixSeller.visibility =
            if (settingsDB!!.getString("UserType") == "Seller") View.GONE else View.VISIBLE
        SellerContact!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                SellerRef!!.child("SC").setValue(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
        makeSeller.setOnClickListener {
            SellerRef!!.child("UT").setValue("Seller")
            settingsDB!!.putString("UserType", "Seller")
        }
        gotoMap.setOnClickListener {
            val intent = Intent(this@SellerActivity, MapsActivity::class.java)
            intent.putExtra("UserType", settingsDB!!.getString("UserType"))
            startActivity(intent)
        }
        SellerRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("UT").value != null) cardFixSeller.visibility =
                    if (dataSnapshot.child("UT").value == "Seller") View.GONE else View.VISIBLE
                if (dataSnapshot.child("P").value != null) cardFixLocation.visibility =
                    if (dataSnapshot.child("P").value == false) View.VISIBLE else View.GONE
                if (dataSnapshot.child("SC").value != null) cardAddContact.visibility =
                    if (dataSnapshot.child("SC").value == "") View.VISIBLE else View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}