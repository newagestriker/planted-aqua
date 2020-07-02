package com.newage.plantedaqua.activities

import android.app.*
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.codemybrainsout.ratingdialog.RatingDialog
import com.facebook.ads.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.newage.plantedaqua.BuildConfig
import com.newage.plantedaqua.R
import com.newage.plantedaqua.activities.A1Activity
import com.newage.plantedaqua.adapters.ShowcaseRecyclerAdapter
import com.newage.plantedaqua.dbhelpers.ExpenseDBHelper
import com.newage.plantedaqua.dbhelpers.MyDbHelper
import com.newage.plantedaqua.dbhelpers.NutrientDbHelper
import com.newage.plantedaqua.dbhelpers.TankDBHelper
import com.newage.plantedaqua.helpers.TanksPlaceholderFragment
import com.newage.plantedaqua.helpers.TanksSectionsPagerAdapter
import com.newage.plantedaqua.helpers.TinyDB
import com.newage.plantedaqua.models.GalleryInfo
import com.newage.plantedaqua.models.TanksDetails
import com.newage.plantedaqua.viewmodels.A1ViewModel
import com.onesignal.OneSignal
import dmax.dialog.SpotsDialog
import org.koin.androidx.viewmodel.compat.ScopeCompat.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class A1Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TANK_DETAILS_CREATION = 1
    private val TANK_DETAILS_MODIFICATION = 2
    private val SETTINGS_REQUEST_CODE = 30
    private var tankDBHelper: TankDBHelper? = null
    private var headerview: View? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var userGSignIn: View? = null
    private var drawer: DrawerLayout? = null
    private var instructionText: TextView? = null
    private var devRef: DatabaseReference? = null
    private var spotsProgressDialog: android.app.AlertDialog? = null
    private val a1ViewModel : A1ViewModel by viewModel()
    private var tinyDB : TinyDB? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rebootRequired = TinyDB(this)
        tankDBHelper = TankDBHelper.newInstance(this)
        tinyDB = TinyDB(applicationContext)
        val currentVersionCode = BuildConfig.VERSION_CODE
        val storedVersionCode = rebootRequired.getInt("STORED_VERSION_CODE")
        if (storedVersionCode < 25) {
            modifyDBs()
        }
        val builder = android.app.AlertDialog.Builder(this)
        setAlarmForNewVersion()
        if (storedVersionCode > 0 && storedVersionCode != currentVersionCode) {
            builder.setTitle(resources.getString(R.string.Attention))
                    .setMessage(resources.getString(R.string.AppUpdated))
                    .setNeutralButton(resources.getString(R.string.OK)) { dialog, which -> dialog.dismiss() }.create().show()
        }
        rebootRequired.putInt("STORED_VERSION_CODE", currentVersionCode)
        setContentView(R.layout.activity_a1)
        userTankImagesRecyclerView = findViewById(R.id.ShowcaseTankRecyclerView)
        loadBannerAd()
        instructionText = findViewById(R.id.InstructionText)
        mAuth = FirebaseAuth.getInstance()
        user = mAuth!!.currentUser
        if (user != null) {
            loadUserTankImages()
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        devRef = FirebaseDatabase.getInstance().getReference("Dev")
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.StartTitle)
        drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer!!.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        headerview = navigationView.getHeaderView(0)
        spotsProgressDialog = SpotsDialog.Builder()
                .setContext(this)
                .setTheme(R.style.ProgressDotsStyle)
                .build()
        //        progressDialog = new ProgressDialog(this);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setMessage(getResources().getString(R.string.PleaseWait));
        userGSignIn = headerview!!.findViewById(R.id.sign_in_button)
        userGSignIn!!.setOnClickListener(View.OnClickListener {
            signIn()
            // progressDialog.show();
            spotsProgressDialog!!.show()
        })
        val logout = headerview!!.findViewById<TextView>(R.id.Logout)
        logout.setOnClickListener {
            FirebaseDatabase.getInstance().getReference("UI").child(user!!.uid).removeValue()
            FirebaseDatabase.getInstance().getReference("UL").child(user!!.uid).removeValue()
            mAuth!!.signOut()
            SignOutUpdateUI()
            drawer!!.closeDrawer(GravityCompat.START)
            Toast.makeText(this@A1Activity, resources.getString(R.string.Loggedout), Toast.LENGTH_SHORT).show()
        }

        // insertTankRow();
        val ratingDialog = RatingDialog.Builder(this)
                .session(7)
                .build()
        ratingDialog.show()
        setAlarmOn1stDay()
        if (mAuth!!.currentUser != null) {
            if (mAuth!!.currentUser!!.email != null) {
                if (mAuth!!.currentUser!!.email == "skramiz@gmail.com" || mAuth!!.currentUser!!.email == "newagestriker@gmail.com") {
                    writeDeveloperMessage()
                }
            }
            OneSignal.sendTag("User_ID", mAuth!!.currentUser!!.uid)
        }
        showDeveloperMessage()

        a1ViewModel.getTankDetailArrayListLiveData().observe(this, androidx.lifecycle.Observer {
            if (it.isNullOrEmpty()) {
                instructionText!!.visibility = View.VISIBLE
            } else instructionText!!.visibility = View.GONE
        })
    }

    fun shortCutClick(view: View) {
        when (view.tag.toString()) {
            "wallet" -> {
                val iExpense = Intent(this, ExpenseActivity::class.java)
                startActivity(iExpense)
            }
            "macro" -> {
                val iMacro = Intent(this, MacroNutrientTableActivity::class.java)
                startActivity(iMacro)
            }
            "converter" -> {
                val iConverter = Intent(this, ConverterActivity::class.java)
                startActivity(iConverter)
            }
            "plantDB" -> {
                val iPlantDB = Intent(this, PlantDatabaseActivity::class.java)
                startActivity(iPlantDB)
            }
            else -> {
                val i3 = Intent(this, AlgaeActivity::class.java)
                startActivity(i3)
            }
        }
    }

    private fun modifyDBs() {
        val expenseDBHelper = ExpenseDBHelper.getInstance(applicationContext)
        var myDbHelper: MyDbHelper
        var numericPrice: Float
        var aquaName: String
        var aquaID: String
        var TID: String
        var dtparts: Array<String>
        var dy: Int
        var mnth: Int
        var year: Int
        var finaldt: String
        var numericQuantity: Int
        val cTanks = tankDBHelper!!.getData(tankDBHelper!!.writableDatabase)
        var cMyDB: Cursor?
        if (cTanks != null) {
            if (cTanks.moveToFirst()) {
                do {
                    aquaID = cTanks.getString(1)
                    aquaName = cTanks.getString(2)
                    if (TextUtils.isEmpty(cTanks.getString(10))) {
                        dy = 0
                        mnth = 0
                        year = 0
                    } else {
                        dtparts = cTanks.getString(10).split("/".toRegex()).toTypedArray()
                        dy = if (TextUtils.isEmpty(dtparts[0])) 0 else dtparts[0].toInt()
                        mnth = if (TextUtils.isEmpty(dtparts[1])) 0 else dtparts[1].toInt()
                        year = if (TextUtils.isEmpty(dtparts[2])) 0 else dtparts[2].toInt()
                    }
                    finaldt = formatDate(year) + "-" + formatDate(mnth) + "-" + formatDate(dy)
                    tankDBHelper!!.updateSingleItem("AquariumID", aquaID, "StartupDate", finaldt)
                    numericPrice = if (TextUtils.isEmpty(cTanks.getString(5))) 0.0f else cTanks.getString(5).replace(",", ".").toFloat()
                    expenseDBHelper.addDataExpense(expenseDBHelper.writableDatabase, aquaID, aquaName, cTanks.getString(2), dy, mnth, year, finaldt, 0L, 1, numericPrice, numericPrice, "", aquaID)
                    myDbHelper = MyDbHelper.newInstance(applicationContext, aquaID)
                    cMyDB = myDbHelper.getDataTI(myDbHelper.writableDatabase)
                    if (cMyDB != null) {
                        if (cMyDB.moveToFirst()) {
                            do {
                                TID = cMyDB.getString(0)
                                if (TextUtils.isEmpty(cMyDB.getString(7))) {
                                    dy = 0
                                    mnth = 0
                                    year = 0
                                } else {
                                    dtparts = cMyDB.getString(7).split("/".toRegex()).toTypedArray()
                                    dy = dtparts[0].toInt()
                                    mnth = dtparts[1].toInt()
                                    year = dtparts[2].toInt()
                                }
                                finaldt = formatDate(year) + "-" + formatDate(mnth) + "-" + formatDate(dy)
                                numericQuantity = cMyDB.getString(6).toInt()
                                numericPrice = if (TextUtils.isEmpty(cMyDB.getString(5))) 0.0f else cMyDB.getString(5).replace(",", ".").toFloat()
                                myDbHelper.updateItemTISingleItem(TID, "I_BuyDate", finaldt)
                                expenseDBHelper.addDataExpense(expenseDBHelper.writableDatabase, TID, aquaName, cMyDB.getString(1), dy, mnth, year, finaldt, 0L, numericQuantity, numericPrice, numericPrice * numericQuantity, "", aquaID)
                            } while (cMyDB.moveToNext())
                        }
                        cMyDB.close()
                    }
                } while (cTanks.moveToNext())
            }
            cTanks.close()
        }
    }

    private fun formatDate(num: Int): String {
        return if (num < 10) "0$num" else Integer.toString(num)
    }

    private fun writeDeveloperMessage() {
        val dev_msg_layout = findViewById<LinearLayout>(R.id.AddDevMsgLayout)
        dev_msg_layout.visibility = View.VISIBLE
        val dev_msg_editText = findViewById<EditText>(R.id.addDevMessage)
        val updateDevMessage = findViewById<ImageView>(R.id.updateDevMessage)
        updateDevMessage.setOnClickListener {
            val msg = if (TextUtils.isEmpty(dev_msg_editText.text.toString())) "" else dev_msg_editText.text.toString()
            val timeStamp = java.lang.Long.toString(Calendar.getInstance().timeInMillis)
            devRef!!.child("M").setValue(msg)
            devRef!!.child("T").setValue(timeStamp)
            Toast.makeText(this@A1Activity, "Message Updated", Toast.LENGTH_SHORT).show()
        }
    }

    private var storedMsgFlag: String? = null
    private var downloadedMsgFlag: String? = null
    private fun showDeveloperMessage() {
        val dev_msg = findViewById<TextView>(R.id.showDevMessage)

        val show_dev_message_layout = findViewById<LinearLayout>(R.id.DevMsgLayout)
        val close_dev_msg = findViewById<ImageView>(R.id.closeDevMessage)
        storedMsgFlag = tinyDB!!.getString("STORED_DEV_MSG_FLAG")
        downloadedMsgFlag = tinyDB!!.getString("DOWNLOADED_DEV_MSG_FLAG")
        if (storedMsgFlag == downloadedMsgFlag) {
            show_dev_message_layout.visibility = View.GONE
        } else {
            show_dev_message_layout.visibility = View.VISIBLE
        }
        devRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("M").value != null && dataSnapshot.child("T").value != null) {
                    dev_msg.text = dataSnapshot.child("M").value.toString()
                    downloadedMsgFlag = dataSnapshot.child("T").value.toString()
                    tinyDB!!.putString("DOWNLOADED_DEV_MSG_FLAG", downloadedMsgFlag)
                    if (storedMsgFlag == downloadedMsgFlag) {
                        show_dev_message_layout.visibility = View.GONE
                    } else {
                        show_dev_message_layout.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        close_dev_msg.setOnClickListener {
            show_dev_message_layout.visibility = View.GONE
            tinyDB!!.putString("STORED_DEV_MSG_FLAG", downloadedMsgFlag)
        }
    }

    var userAcceptance: TinyDB? = null
    override fun onStart() {
        super.onStart()
        checkConsent()
        setDefaultSymbol()
        if (mAuth!!.currentUser != null) {
            SignInUpdateUI()
        } else {
            SignOutUpdateUI()
        }
        loadViewPagerTankDetails()
    }

    private fun setDefaultSymbol() {
        val settingsDB = TinyDB(this.applicationContext)
        if (settingsDB.getString("DefaultCurrencySymbol").isEmpty()) {
            settingsDB.putString("DefaultCurrencySymbol", "₹")
        }
    }

    private fun checkConsent() {
        val UserAccepted: Boolean
        userAcceptance = TinyDB(this)
        UserAccepted = userAcceptance!!.getBoolean("UserAccepted")
        if (!UserAccepted) {
            val consentView = layoutInflater.inflate(R.layout.consent_layout, null)
            val textView = consentView.findViewById<TextView>(R.id.PPLink)
            textView.movementMethod = LinkMovementMethod.getInstance()
            val consentDialog = android.app.AlertDialog.Builder(this)
            consentDialog.setIcon(R.drawable.plantedaqua)
                    .setCancelable(false)
                    .setView(consentView)
                    .setNegativeButton(resources.getString(R.string.Decline)) { _, _ -> finish() }
                    .setPositiveButton(resources.getString(R.string.Agree)) { dialog, _ ->
                        userAcceptance!!.putBoolean("UserAccepted", true)
                        dialog.dismiss()
                    }
                    .create().show()
        }
    }

    private fun SignInUpdateUI() {

        // OneSignal.setSubscription(true);
        userGSignIn!!.visibility = View.GONE
        headerview!!.findViewById<View>(R.id.UserName).visibility = View.VISIBLE
        headerview!!.findViewById<View>(R.id.NavEmailView).visibility = View.VISIBLE
        headerview!!.findViewById<View>(R.id.Logout).visibility = View.VISIBLE
        if (user!!.photoUrl != null) {
            Glide.with(this)
                    .load(user!!.photoUrl)
                    .apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.drawable.profle))
                    .into((headerview!!.findViewById<View>(R.id.NavImageView) as ImageView))
        }
        (headerview!!.findViewById<View>(R.id.UserName) as TextView).text = user!!.displayName
        (headerview!!.findViewById<View>(R.id.NavEmailView) as TextView).text = user!!.email
    }

    private fun SignOutUpdateUI() {

        //OneSignal.setSubscription(false);
        Glide.with(this)
                .load(R.drawable.profile2)
                .into((headerview!!.findViewById<View>(R.id.NavImageView) as ImageView))
        userGSignIn!!.visibility = View.VISIBLE
        headerview!!.findViewById<View>(R.id.UserName).visibility = View.GONE
        headerview!!.findViewById<View>(R.id.NavEmailView).visibility = View.GONE
        headerview!!.findViewById<View>(R.id.Logout).visibility = View.GONE
        userTankImagesRecyclerView!!.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.a1, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.itemId == R.id.addDetails) {
            val intent = Intent(this, CreateTankActivity::class.java)
            intent.putExtra("mode", "creation")
            startActivityForResult(intent, TANK_DETAILS_CREATION)
        }
        if (item.itemId == R.id.Help) {
            showItemsHelpDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showItemsHelpDialog() {
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val view = layoutInflater.inflate(R.layout.items_help_dialog_view_2, viewGroup, false)
        val helpDialogBuilder = android.app.AlertDialog.Builder(this)
        helpDialogBuilder.setView(view)
        val helpDialog: Dialog = helpDialogBuilder.create()
        helpDialog.show()
        val okButton = view.findViewById<Button>(R.id.help_ok_button2)
        okButton.setOnClickListener { helpDialog.dismiss() }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.nav_nearby) {
            startMapsActivty()
        } else if (id == R.id.nav_privacy) {
            val i4 = Intent(this, ConditionsActivity::class.java)
            i4.putExtra("URL", "https://plantedaquaapp.blogspot.com/p/privacy-policy.html")
            startActivity(i4)
        } else if (id == R.id.nav_open_source_license) {
            val i4 = Intent(this, ConditionsActivity::class.java)
            i4.putExtra("URL", "https://plantedaquaapp.blogspot.com/p/copyright-information.html")
            startActivity(i4)
        } else if (id == R.id.nav_disclaimer) {
            val i4 = Intent(this, ConditionsActivity::class.java)
            i4.putExtra("URL", "https://plantedaquaapp.blogspot.com/p/disclaimer.html")
            startActivity(i4)
        } else if (id == R.id.nav_help) {
            val i4 = Intent(this, ConditionsActivity::class.java)
            i4.putExtra("URL", "https://plantedaquaapp.blogspot.com/2018/09/get-started-with-planted-aqua.html")
            startActivity(i4)
        } else if (id == R.id.nav_icons8) {
            val i4 = Intent(this, ConditionsActivity::class.java)
            i4.putExtra("URL", "https://icons8.com/")
            startActivity(i4)
        } else if (id == R.id.nav_seller) {
            startShopActivty()
        } else if (id == R.id.nav_settings) {
            val iSettings = Intent(this, SettingsActivity::class.java)
            startActivityForResult(iSettings,SETTINGS_REQUEST_CODE)
        } else if (id == R.id.nav_users_gallery) {
            if (mAuth!!.currentUser != null) {
                val iUsersGallery = Intent(this, UsersGalleryActivity::class.java)
                iUsersGallery.putExtra("UserID", mAuth!!.currentUser!!.uid)
                startActivity(iUsersGallery)
            } else {
                Toast.makeText(this, "Sorry! You must be logged in to use this feature", Toast.LENGTH_SHORT).show()
            }
        } else if (id == R.id.nav_micro_calc) {
            val iExpense = Intent(this, MicroNutrientTableActivity::class.java)
            startActivity(iExpense)
        } else if (id == R.id.nav_chatbox) {
            if (mAuth!!.currentUser != null) {
                val iChatBox = Intent(this, ChatBoxActivity::class.java)
                iChatBox.putExtra("UserID", mAuth!!.currentUser!!.uid)
                startActivity(iChatBox)
            } else {
                Toast.makeText(this, "Sorry! You must be logged in to use this feature", Toast.LENGTH_SHORT).show()
            }
        } else if (id == R.id.nav_my_chats) {
            val iChats = Intent(this, ChatUsersActivity::class.java)
            startActivity(iChats)
        }
        drawer!!.closeDrawer(GravityCompat.START)
        return true
    }

    private fun startMapsActivty() {
        if (mAuth!!.currentUser == null) {
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle(resources.getString(R.string.Attention))
                    .setMessage(resources.getString(R.string.NotLoggedInMsg))
                    .setNeutralButton(resources.getString(R.string.OK)) { dialogInterface, i -> dialogInterface.dismiss() }.create().show()
        } else {
            // String userID = "ma";
            validateAndProceed()
        }
    }

    private fun startShopActivty() {
        if (mAuth!!.currentUser == null) {
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle(resources.getString(R.string.Attention))
                    .setMessage(resources.getString(R.string.NotLoggedInMsg))
                    .setNeutralButton(resources.getString(R.string.OK)) { dialogInterface, i -> dialogInterface.dismiss() }.create().show()
        } else {
            // String userID = "ma";
            val iShop = Intent(this, SellerActivity::class.java)
            iShop.putExtra("User", "Own")
            startActivity(iShop)
        }
    }

    var userType = ""
    private fun validateAndProceed() {
        val checkView = LayoutInflater.from(this).inflate(R.layout.remember_layout, null)
        val settingsDB = TinyDB(this.applicationContext)
        val messageDialog = android.app.AlertDialog.Builder(this)
        messageDialog.setMessage(R.string.SellerShopMsg)
                .setCancelable(false)
                .setIcon(R.drawable.attention)
                .setPositiveButton(resources.getString(R.string.OK)) { dialog, which ->
                    val `in` = Intent(this@A1Activity, MapsActivity::class.java)
                    `in`.putExtra("UserType", settingsDB.getString("UserType"))
                    startActivity(`in`)
                }
                .setNegativeButton(resources.getString(R.string.Cancel)) { dialog, which -> dialog.dismiss() }

        // settingsDB.putBoolean("RememberSorH",false);
        if (settingsDB.getBoolean("RememberSorH")) {
            if (settingsDB.getString("UserType") == "Seller") {
                messageDialog.create().show()
            } else {
                val `in` = Intent(this@A1Activity, MapsActivity::class.java)
                `in`.putExtra("UserType", settingsDB.getString("UserType"))
                startActivity(`in`)
            }
        } else {
            val items = arrayOf<CharSequence>(resources.getString(R.string.Hobbyist), resources.getString(R.string.Seller), resources.getString(R.string.Cancel))
            val builder = AlertDialog.Builder(this)
            builder.setTitle(resources.getString(R.string.HorS))
                    .setView(checkView)
                    .setItems(items) { dialogInterface, i ->
                        if (items[i] == resources.getString(R.string.Cancel)) {
                            dialogInterface.dismiss()
                        } else {
                            userType = items[i].toString()
                            var rememberChecked = false
                            val checkBox = checkView.findViewById<CheckBox>(R.id.RememberCheckbox)
                            if (checkBox.isChecked) rememberChecked = true
                            settingsDB.putBoolean("RememberSorH", rememberChecked)
                            settingsDB.putString("UserType", userType)
                            if (userType == "Seller") {
                                messageDialog.create().show()
                            } else {
                                val `in` = Intent(this@A1Activity, MapsActivity::class.java)
                                `in`.putExtra("UserType", userType)
                                startActivity(`in`)
                            }
                        }
                    }.create().show()
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct!!.idToken, null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(this@A1Activity, "User Signed in", Toast.LENGTH_SHORT).show()
                        user = mAuth!!.currentUser
                        // progressDialog.dismiss();
                        spotsProgressDialog!!.dismiss()
                        SignInUpdateUI()
                        drawer!!.closeDrawer(GravityCompat.START)
                    } else {
                        // If sign in fails, display a message to the user.
                        try {
                            throw Objects.requireNonNull(task.exception)!!
                        } catch (e: Exception) {
                            //Log.e(TAG, e.getMessage());
                        }
                        Toast.makeText(this@A1Activity, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                        spotsProgressDialog!!.dismiss()
                        // progressDialog.dismiss();
                        SignOutUpdateUI()
                        drawer!!.closeDrawer(GravityCompat.START)
                    }

                    // ...
                }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
                loadUserTankImages()
            } catch (e: ApiException) {
                Toast.makeText(this@A1Activity, e.message, Toast.LENGTH_SHORT).show()
                spotsProgressDialog!!.dismiss()
                // progressDialog.cancel();
            }
        }
        if (resultCode == Activity.RESULT_OK) {


            if (requestCode == TANK_DETAILS_CREATION) {
                val tanksDetails = data!!.getParcelableExtra<TanksDetails>("TankItemsObject")
                a1ViewModel.getTankDetailsArrayList()!!.add(tanksDetails!!)
                Handler().post {

                    a1ViewModel.calcCumExpense(a1ViewModel.getTankDetailsArrayList()!!.size - 1)
                    tanksSectionsPagerAdapter!!.notifyDataSetChanged()
                    viewPager2!!.currentItem = a1ViewModel.getTankDetailsArrayList()!!.size - 1
                }
            } else if (requestCode == TANK_DETAILS_MODIFICATION) {
                val tanksDetails = data!!.getParcelableExtra<TanksDetails>("TankItemsObject")
                val position = data.getIntExtra("Position",-1)
                a1ViewModel.getTankDetailsArrayList()!![position] = tanksDetails!!
                a1ViewModel.calcCumExpense(position)
                Handler().post { tanksSectionsPagerAdapter!!.notifyDataSetChanged() }
            } else if (requestCode == LIGHT_ACTIVITY_REQUEST_CODE) {
                Handler().post { tanksSectionsPagerAdapter!!.notifyDataSetChanged() }
            }
            else if(requestCode == SETTINGS_REQUEST_CODE)
                {
                    a1ViewModel.setDefaultCurrency()
                    Handler().post { tanksSectionsPagerAdapter!!.notifyDataSetChanged() }
                }

        }
    }

    private fun setAlarmOn1stDay() {
        val calendar = Calendar.getInstance()
        calendar[calendar[Calendar.YEAR], calendar[Calendar.MONTH], 1, 9, 0] = 0
        val calendar_everday = Calendar.getInstance()
        calendar_everday[calendar_everday[Calendar.YEAR], calendar_everday[Calendar.MONTH], calendar_everday[Calendar.DAY_OF_MONTH], 10, 0] = 0
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.MONTH, 1)
        }
        if (calendar_everday.timeInMillis < System.currentTimeMillis()) {
            calendar_everday.add(Calendar.DAY_OF_MONTH, 1)
        }
        defaultAlarms(calendar, 1, "MONTHLY_EXPENSE")
        defaultAlarms(calendar_everday, 2, "EVERYDAY")
    }

    private fun defaultAlarms(calendar: Calendar, reqCode: Int, alarmType: String) {
        val alarmInMillis: Long
        alarmInMillis = calendar.timeInMillis
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this.applicationContext, RamizAlarm::class.java)
        intent.putExtra("AT", alarmType)

        //Log.i("Default_Alarm",Long.toString(alarmInMillis));
        var pi = PendingIntent.getBroadcast(this, reqCode, intent, PendingIntent.FLAG_NO_CREATE)
        if (pi != null) {
            //System.out.println("not null");
            //System.out.println("Ramiz: Pending intent exists ");
            pi = PendingIntent.getBroadcast(this, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            assert(alarmManager != null)
            alarmManager.cancel(pi)
        }
        pi = PendingIntent.getBroadcast(this.applicationContext, reqCode, intent, 0)
        if (Build.VERSION.SDK_INT >= 23) {
            assert(alarmManager != null)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmInMillis, pi)
        } else if (Build.VERSION.SDK_INT >= 19) {
            assert(alarmManager != null)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmInMillis, pi)
        } else {
            assert(alarmManager != null)
            alarmManager[AlarmManager.RTC_WAKEUP, alarmInMillis] = pi
        }
    }

    var userTankImagesRecyclerView: RecyclerView? = null
    var galleryInfoArrayList = ArrayList<GalleryInfo>()
    private fun loadUserTankImages() {
        if (timerTask == null) {
            val galleryItemRef = FirebaseDatabase.getInstance().getReference("GI")
            val galleryItemRefQuery = galleryItemRef.orderByChild("rating")
            val showcaseAdapter = ShowcaseRecyclerAdapter<GalleryInfo>(R.layout.showcase_recycler_view_item, object : ShowcaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(view: View?, pos: Int) {
                    val iUsersGallery = Intent(this@A1Activity, UsersGalleryActivity::class.java)
                    iUsersGallery.putExtra("UserID", mAuth!!.currentUser!!.uid)
                    iUsersGallery.putExtra("Position", pos)
                    startActivity(iUsersGallery)
                }

                override fun onItemLongClick(view: View?, pos: Int) {}
            })
            showcaseAdapter.submitList(galleryInfoArrayList)
            userTankImagesRecyclerView!!.adapter = showcaseAdapter
            galleryItemRefQuery.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    var galleryInfo = GalleryInfo()
                    galleryInfo = dataSnapshot.getValue(GalleryInfo::class.java)!!
                    galleryInfoArrayList.add(galleryInfo)
                    showcaseAdapter.notifyItemInserted(galleryInfoArrayList.size - 1)
                    if (galleryInfoArrayList.size == 1) {
                        userTankImagesRecyclerView!!.visibility = View.VISIBLE
                        startCarousel()
                    }
                    if (galleryInfoArrayList.size == 0) {
                        userTankImagesRecyclerView!!.visibility = View.GONE
                    }
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@A1Activity, "Database Error Occurred. Please check your Internet Connection", Toast.LENGTH_LONG).show()
                }
            })
        }
        userTankImagesRecyclerView!!.visibility = View.VISIBLE
    }

    private var adView: AdView? = null
    private fun loadBannerAd() {
        adView = AdView(this, "200298157916823_200419604571345", AdSize.BANNER_HEIGHT_50)

        // Find the Ad Container
        val adContainer = findViewById<View>(R.id.banner_container) as LinearLayout

        // Add the ad view to your activity layout
        adContainer.addView(adView)
        val adListener: AdListener = object : AdListener {
            override fun onError(ad: Ad, adError: AdError) {}
            override fun onAdLoaded(ad: Ad) {}
            override fun onAdClicked(ad: Ad) {}
            override fun onLoggingImpression(ad: Ad) {}
        }
        val loadAdConfig = adView!!.buildLoadAdConfig()
                .withAdListener(adListener)
                .build()
        adView!!.loadAd(loadAdConfig)
    }

    override fun onDestroy() {
        if (adView != null) {
            adView!!.destroy()
        }
        super.onDestroy()
    }

    private var pos = 0
    private var timer = Timer()
    private var taskCancelled = false
    private var timerTask: TimerTask? = null
    override fun onPause() {
        super.onPause()
        tinyDB!!.putInt("A1_VIEWPAGER_POSITION", viewPager2!!.currentItem)
        timer.cancel()
        if (timerTask != null) {
            taskCancelled = timerTask!!.cancel()
        }
    }

    private fun startCarousel() {
        timerTask = object : TimerTask() {
            override fun run() {
                if (pos == galleryInfoArrayList.size - 1) {
                    pos = 0
                }
                runOnUiThread {
                    userTankImagesRecyclerView!!.scrollToPosition(pos)
                    pos++
                }
            }
        }
        timer = Timer()
        timer.scheduleAtFixedRate(
                timerTask, 1000L, 4000L
        )
    }

    override fun onResume() {
        viewPager2!!.currentItem = tinyDB!!.getInt("A1_VIEWPAGER_POSITION")

        if (taskCancelled && galleryInfoArrayList.size > 0) {
            startCarousel()
        }
        super.onResume()
    }

    private fun setAlarmForNewVersion() {
        var alarmdbhelper1: MyDbHelper
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val db1 = tankDBHelper!!.writableDatabase
        var AquariumName: String
        var pi: PendingIntent?
        var storedTimeInMillis: String
        val c1 = tankDBHelper!!.getData(db1)
        while (c1.moveToNext()) {
            AquariumName = c1.getString(2)
            alarmdbhelper1 = MyDbHelper.newInstance(this, c1.getString(1))
            val db2 = alarmdbhelper1.writableDatabase
            val c = alarmdbhelper1.getData(db2)
            while (c.moveToNext()) {
                val calendar = Calendar.getInstance()
                storedTimeInMillis = c.getString(2)
                calendar[calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH], c.getInt(5), c.getInt(6)] = 0
                calendar[Calendar.DAY_OF_WEEK] = c.getInt(4)
                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 7)
                }
                alarmdbhelper1.updateTimeInMillis(db2, java.lang.Long.toString(calendar.timeInMillis), storedTimeInMillis)
                val inn = Intent(this, RamizAlarm::class.java)
                inn.putExtra("AT", c.getString(1))
                inn.putExtra("AlarmName", c.getString(0))
                inn.putExtra("NotifyType", c.getString(7))
                inn.putExtra("AquariumID", c1.getString(1))
                inn.putExtra("KEY_TRIGGER_TIME", calendar.timeInMillis)
                inn.putExtra("KEY_INTENT_ID", c.getInt(3))
                inn.putExtra("AquariumName", AquariumName)
                pi = PendingIntent.getBroadcast(this, c.getInt(3), inn, PendingIntent.FLAG_UPDATE_CURRENT)
                if (Build.VERSION.SDK_INT >= 23) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)
                } else if (Build.VERSION.SDK_INT >= 19) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)
                } else {
                    alarmManager[AlarmManager.RTC_WAKEUP, calendar.timeInMillis] = pi
                }
            }
            c.close()
        }
        c1.close()
    }

    //region EACH TANK IN VIEWPAGER
    private var tanksSectionsPagerAdapter: TanksSectionsPagerAdapter? = null
    private var viewPager2: ViewPager? = null
    private var expenseDBHelper: ExpenseDBHelper? = null
    private fun loadViewPagerTankDetails() {
        viewPager2 = findViewById(R.id.TanksViewPager)
        tanksSectionsPagerAdapter = TanksSectionsPagerAdapter(a1ViewModel.getTankDetailsArrayList()!!.size, supportFragmentManager)
        viewPager2!!.adapter = tanksSectionsPagerAdapter
    }

    //Adding Click Events to Icons in Each Tank Card
    fun onTankDashBoardItemsClicked(view: View) {
        when (view.id) {
            R.id.deleteTank -> removeTankFromPager(view.tag.toString())
            R.id.imageNutrientOption -> navigateToSelectedOption(Intent(this, DosingGraphsActivity::class.java), viewPager2!!.currentItem, "")
            R.id.imageFloraOption -> navigateToSelectedOption(Intent(this, TankItemsActivity::class.java), viewPager2!!.currentItem, "Fl")
            R.id.imageFaunaOption -> navigateToSelectedOption(Intent(this, TankItemsActivity::class.java), viewPager2!!.currentItem, "Fr")
            R.id.imageTanksEquipmentOption -> navigateToSelectedOption(Intent(this, TankItemsActivity::class.java), viewPager2!!.currentItem, "E")
            R.id.imageLogOptions -> navigateToSelectedOption(Intent(this, LogsActivity::class.java), viewPager2!!.currentItem, "")
            R.id.imageTaskOptions -> navigateToSelectedOption(Intent(this, TasksActivity::class.java), viewPager2!!.currentItem, "")
            R.id.imageLightOption -> navigateToSelectedOption(Intent(this, LightCalcActivity::class.java), viewPager2!!.currentItem, "Light")
            R.id.imageMacroOption, R.id.SetMacroValue -> navigateToSelectedOption(Intent(this, MacroNutrientTableActivity::class.java), viewPager2!!.currentItem, "")
            R.id.imageMicroOption, R.id.SetMicroValue -> navigateToSelectedOption(Intent(this, MicroNutrientTableActivity::class.java), viewPager2!!.currentItem, "")
            R.id.imageTPAOptions -> navigateToSelectedOption(Intent(this, TankProgressActivity::class.java), viewPager2!!.currentItem, "")
            else -> editTankDetails()
        }
    }

    private fun editTankDetails() {
        val intent = Intent(this@A1Activity, CreateTankActivity::class.java)
        intent.putExtra("mode", "modification")
        intent.putExtra("Position", viewPager2!!.currentItem)
        startActivityForResult(intent, TANK_DETAILS_MODIFICATION)
    }

    private fun removeTankFromPager(aquariumID: String) {
        val alertDialogBuilder = android.app.AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Delete Tank " + a1ViewModel.getTankDetailsArrayList()!![viewPager2!!.currentItem].tankName)
                .setMessage("This action will delete your tank and all relevant data. Do you wish to continue?")
                .setNegativeButton(resources.getString(R.string.Cancel)) { dialog, which -> dialog.dismiss() }
                .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                    // Log.i("AQUARIUMID",aquariumID);
                   a1ViewModel.deleteTankDataFromDatabase(aquariumID)


                    // Log.i("POSITION",Integer.toString(viewPager2.getCurrentItem()));
                    a1ViewModel.getTankDetailsArrayList()!!.removeAt(viewPager2!!.currentItem)
                    tanksSectionsPagerAdapter!!.notifyDataSetChanged()
                    // Log.i("SIZE",Integer.toString(a1ViewModel.getTankDetailsArrayList().size()));
                }
                .create()
                .show()
    }

    private val LIGHT_ACTIVITY_REQUEST_CODE = 81
    private fun navigateToSelectedOption(intent: Intent, position: Int, itemCategory: String) {
        intent.putExtra("AquariumID", a1ViewModel.getTankDetailsArrayList()!![position].tankID)
        intent.putExtra("AquariumName", a1ViewModel.getTankDetailsArrayList()!![position].tankName)
        intent.putExtra("ItemCategory", itemCategory)
        intent.putExtra("Position", position)
        if (itemCategory == "Light") {
            startActivityForResult(intent, LIGHT_ACTIVITY_REQUEST_CODE)
        } else {
            startActivity(intent)
        }
    } //endregion

    companion object {
        private const val RC_SIGN_IN = 47
    }
}