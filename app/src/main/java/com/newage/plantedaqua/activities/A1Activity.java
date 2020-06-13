package com.newage.plantedaqua.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;


import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Handler;
import android.text.TextUtils;

import android.text.method.LinkMovementMethod;

import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.viewpager.widget.ViewPager;


import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.codemybrainsout.ratingdialog.RatingDialog;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.newage.plantedaqua.BuildConfig;

import com.newage.plantedaqua.adapters.ShowcaseRecyclerAdapter;
import com.newage.plantedaqua.helpers.ExpenseDBHelper;
import com.newage.plantedaqua.helpers.MyDbHelper;
import com.newage.plantedaqua.helpers.NutrientDbHelper;
import com.newage.plantedaqua.R;

import com.newage.plantedaqua.helpers.TankDBHelper;

import com.newage.plantedaqua.helpers.TanksPlaceholderFragment;
import com.newage.plantedaqua.helpers.TanksSectionsPagerAdapter;
import com.newage.plantedaqua.helpers.TinyDB;
import com.newage.plantedaqua.models.GalleryInfo;

import com.newage.plantedaqua.models.TanksDetails;
import com.onesignal.OneSignal;


import java.util.ArrayList;
import java.util.Calendar;

import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import com.facebook.ads.*;

import dmax.dialog.SpotsDialog;

import static java.lang.Thread.sleep;


public class A1Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int TANK_DETAILS_CREATION=1;
    private int TANK_DETAILS_MODIFICATION=2;

    private TankDBHelper tankDBHelper;
    private View headerview;
    private static final int RC_SIGN_IN = 47 ;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private View userGSignIn;
    private DrawerLayout drawer;
    private TextView instructionText;
    private DatabaseReference devRef;
    private AlertDialog spotsProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        TinyDB rebootRequired = new TinyDB(this);

        tankDBHelper = TankDBHelper.newInstance(this);
        int currentVersionCode = BuildConfig.VERSION_CODE;
        int storedVersionCode = rebootRequired.getInt("STORED_VERSION_CODE");
        if(storedVersionCode <25){
            modifyDBs();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (storedVersionCode > 0 && storedVersionCode != currentVersionCode) {
            setAlarmForNewVersion();
            builder.setTitle(getResources().getString(R.string.Attention))
                    .setMessage(getResources().getString(R.string.AppUpdated))
                    .setNeutralButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

        }
        rebootRequired.putInt("STORED_VERSION_CODE", currentVersionCode);

        setContentView(R.layout.activity_a1);
        userTankImagesRecyclerView = findViewById(R.id.ShowcaseTankRecyclerView);


        loadBannerAd();


        instructionText = findViewById(R.id.InstructionText);



        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(user!=null){
            loadUserTankImages();
        }


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        devRef = FirebaseDatabase.getInstance().getReference("Dev");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.StartTitle);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerview = navigationView.getHeaderView(0);

            spotsProgressDialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setTheme(R.style.ProgressDotsStyle)
                    .build();
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setMessage(getResources().getString(R.string.PleaseWait));
        userGSignIn = headerview.findViewById(R.id.sign_in_button);
        userGSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
               // progressDialog.show();
                spotsProgressDialog.show();

            }
        });

        TextView logout = headerview.findViewById(R.id.Logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FirebaseDatabase.getInstance().getReference("UI").child(user.getUid()).removeValue();
               FirebaseDatabase.getInstance().getReference("UL").child(user.getUid()).removeValue();
                mAuth.signOut();
                SignOutUpdateUI();
                drawer.closeDrawer(GravityCompat.START);
                Toast.makeText(A1Activity.this, getResources().getString(R.string.Loggedout), Toast.LENGTH_SHORT).show();

            }
        });

       // insertTankRow();

        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .session(7)
                .build();

        ratingDialog.show();

        setAlarmOn1stDay();

        if(mAuth.getCurrentUser()!=null) {

            if(mAuth.getCurrentUser().getEmail()!=null) {

                if (mAuth.getCurrentUser().getEmail().equals("skramiz@gmail.com") || mAuth.getCurrentUser().getEmail().equals("newagestriker@gmail.com")) {

                    writeDeveloperMessage();
                }



            }

            OneSignal.sendTag("User_ID", mAuth.getCurrentUser().getUid());


        }



        showDeveloperMessage();



    }



    public void shortCutClick(View view){

        switch (view.getTag().toString()){
            case  "wallet" : {
                Intent iExpense = new Intent(this, ExpenseActivity.class);
                startActivity(iExpense);
                break;
            }
            case  "macro" : {
                Intent iMacro = new Intent(this, MacroNutrientTableActivity.class);
                startActivity(iMacro);
                break;
            }
            case  "converter" : {
                Intent iConverter = new Intent(this, ConverterActivity.class);
                startActivity(iConverter);
                break;
            }
            case  "plantDB" : {
                Intent iPlantDB = new Intent(this, PlantDatabaseActivity.class);
                startActivity(iPlantDB);
                break;
            }
            default : {
                Intent i3 = new Intent(this, AlgaeActivity.class);
                startActivity(i3);
                break;
            }
        }

    }

    private void modifyDBs() {

        ExpenseDBHelper expenseDBHelper = ExpenseDBHelper.getInstance(getApplicationContext());
        MyDbHelper myDbHelper;
        float numericPrice;
        String aquaName;
        String aquaID;

        String TID;
        String[] dtparts;
        int dy;
        int mnth;
        int year;
        String finaldt;
        int numericQuantity;

        Cursor cTanks = tankDBHelper.getData(tankDBHelper.getWritableDatabase());
        Cursor cMyDB;
        if(cTanks!=null){
            if(cTanks.moveToFirst()){

                do{
                    aquaID = cTanks.getString(1);
                    aquaName = cTanks.getString(2);
                    if(TextUtils.isEmpty(cTanks.getString(10))){
                        dy = 0;
                        mnth = 0;
                        year = 0;

                    }else {
                        dtparts = cTanks.getString(10).split("/");
                        dy = Integer.parseInt(TextUtils.isEmpty(dtparts[0])?"0":dtparts[0]);
                        mnth = Integer.parseInt(TextUtils.isEmpty(dtparts[1])?"0":dtparts[1]);
                        year = Integer.parseInt(TextUtils.isEmpty(dtparts[2])?"0":dtparts[2]);
                    }
                    finaldt = formatDate(year)+"-"+formatDate(mnth)+"-"+formatDate(dy);
                    tankDBHelper.updateSingleItem("AquariumID",aquaID,"StartupDate",finaldt);
                    numericPrice = Float.parseFloat(TextUtils.isEmpty(cTanks.getString(5))?"0.0":cTanks.getString(5).replace(",","."));
                    expenseDBHelper.addDataExpense(expenseDBHelper.getWritableDatabase(),aquaID,aquaName, cTanks.getString(2),dy,mnth,year,finaldt,0L,1,numericPrice,numericPrice,"",aquaID);
                    myDbHelper = MyDbHelper.newInstance(getApplicationContext(),aquaID);
                    cMyDB = myDbHelper.getDataTI(myDbHelper.getWritableDatabase());


                    if(cMyDB!=null){

                        if(cMyDB.moveToFirst()){
                            do{
                                TID = cMyDB.getString(0);
                                if(TextUtils.isEmpty(cMyDB.getString(7))){
                                    dy = 0;
                                    mnth = 0;
                                    year = 0;

                                } else {
                                    dtparts = cMyDB.getString(7).split("/");
                                    dy = Integer.parseInt(dtparts[0]);
                                    mnth = Integer.parseInt(dtparts[1]);
                                    year = Integer.parseInt(dtparts[2]);
                                }
                                finaldt = formatDate(year)+"-"+formatDate(mnth)+"-"+formatDate(dy);
                                numericQuantity = Integer.parseInt(cMyDB.getString(6));
                                numericPrice =Float.parseFloat(TextUtils.isEmpty(cMyDB.getString(5))?"0.0":cMyDB.getString(5).replace(",","."));
                                myDbHelper.updateItemTISingleItem(TID,"I_BuyDate",finaldt);
                                expenseDBHelper.addDataExpense(expenseDBHelper.getWritableDatabase(),TID,aquaName,cMyDB.getString(1),dy,mnth,year,finaldt,0L,numericQuantity,numericPrice,numericPrice*numericQuantity,"",aquaID);



                            }while (cMyDB.moveToNext());

                        }
                        cMyDB.close();

                    }





                }while (cTanks.moveToNext());
            }
            cTanks.close();
        }





    }

    private String formatDate(int num){

        return num<10?"0"+num:Integer.toString(num);

    }

    private void writeDeveloperMessage() {


        LinearLayout dev_msg_layout = findViewById(R.id.AddDevMsgLayout);
        dev_msg_layout.setVisibility(View.VISIBLE);
        final EditText dev_msg_editText = findViewById(R.id.addDevMessage);
        ImageView updateDevMessage = findViewById(R.id.updateDevMessage);


        updateDevMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String msg = TextUtils.isEmpty(dev_msg_editText.getText().toString())?"":dev_msg_editText.getText().toString();

                String timeStamp = Long.toString(Calendar.getInstance().getTimeInMillis());

                devRef.child("M").setValue(msg);
                devRef.child("T").setValue(timeStamp);

                Toast.makeText(A1Activity.this,"Message Updated",Toast.LENGTH_SHORT).show();


            }
        });



    }

    private String storedMsgFlag;
    private String downloadedMsgFlag;

    private void showDeveloperMessage() {

        final TextView dev_msg = findViewById(R.id.showDevMessage);
        final TinyDB tinyDB = new TinyDB(this);


        final LinearLayout show_dev_message_layout = findViewById(R.id.DevMsgLayout);


        ImageView close_dev_msg = findViewById(R.id.closeDevMessage);

        storedMsgFlag = tinyDB.getString("STORED_DEV_MSG_FLAG");
        downloadedMsgFlag = tinyDB.getString("DOWNLOADED_DEV_MSG_FLAG");




        if(storedMsgFlag.equals(downloadedMsgFlag)){

            show_dev_message_layout.setVisibility(View.GONE);

        }
        else{
            show_dev_message_layout.setVisibility(View.VISIBLE);
        }





        devRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.child("M").getValue()!=null && dataSnapshot.child("T").getValue()!=null ) {

                    dev_msg.setText(dataSnapshot.child("M").getValue().toString());
                    downloadedMsgFlag = dataSnapshot.child("T").getValue().toString();
                    tinyDB.putString("DOWNLOADED_DEV_MSG_FLAG",downloadedMsgFlag);

                    if(storedMsgFlag.equals(downloadedMsgFlag)){

                        show_dev_message_layout.setVisibility(View.GONE);

                    }
                    else{
                        show_dev_message_layout.setVisibility(View.VISIBLE);
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        close_dev_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_dev_message_layout.setVisibility(View.GONE);

                tinyDB.putString("STORED_DEV_MSG_FLAG",downloadedMsgFlag);
            }
        });




    }


    TinyDB userAcceptance;
    @Override
    protected void onStart() {
        super.onStart();
        checkConsent();
        setDefaultSymbol();
        if(mAuth.getCurrentUser()!=null) {

            SignInUpdateUI();
        }
        else{

            SignOutUpdateUI();

        }

        loadViewPagerTankDetails();

    }

    private void setDefaultSymbol(){
        TinyDB settingsDB = new TinyDB(this.getApplicationContext());
        if(settingsDB.getString("DefaultCurrencySymbol").isEmpty()){
            settingsDB.putString("DefaultCurrencySymbol","₹");
        }

    }

    private void checkConsent() {

        boolean UserAccepted;
        userAcceptance = new TinyDB(this);

        UserAccepted = userAcceptance.getBoolean("UserAccepted");
        if (!UserAccepted) {


            View consentView = getLayoutInflater().inflate(R.layout.consent_layout, null);
            TextView textView = consentView.findViewById(R.id.PPLink);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            AlertDialog.Builder consentDialog = new AlertDialog.Builder(this);
            consentDialog.setIcon(R.drawable.plantedaqua)
                    .setCancelable(false)
                    .setView(consentView)
                    .setNegativeButton(getResources().getString(R.string.Decline), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setPositiveButton(getResources().getString(R.string.Agree), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            userAcceptance.putBoolean("UserAccepted", true);
                            dialog.dismiss();

                        }
                    })
                    .create().show();
        }



    }






    private void SignInUpdateUI(){

       // OneSignal.setSubscription(true);

        userGSignIn.setVisibility(View.GONE);
        (headerview.findViewById(R.id.UserName)).setVisibility(View.VISIBLE);
        (headerview.findViewById(R.id.NavEmailView)).setVisibility(View.VISIBLE);
        ( headerview.findViewById(R.id.Logout)).setVisibility(View.VISIBLE);

        if(user.getPhotoUrl()!=null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.drawable.profle))
                    .into((ImageView) headerview.findViewById(R.id.NavImageView));
        }

        ((TextView) headerview.findViewById(R.id.UserName)).setText(user.getDisplayName());
        ((TextView) headerview.findViewById(R.id.NavEmailView)).setText(user.getEmail());


    }

    private void SignOutUpdateUI() {

        //OneSignal.setSubscription(false);

        Glide.with(this)
                .load(R.drawable.profile2)
                .into((ImageView) headerview.findViewById(R.id.NavImageView));

        userGSignIn.setVisibility(View.VISIBLE);
        (headerview.findViewById(R.id.UserName)).setVisibility(View.GONE);
        (headerview.findViewById(R.id.NavEmailView)).setVisibility(View.GONE);
        (headerview.findViewById(R.id.Logout)).setVisibility(View.GONE);
        userTankImagesRecyclerView.setVisibility(View.GONE);
    }



    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.a1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(item.getItemId()==R.id.addDetails){
            Intent intent=new Intent(this,CreateTankActivity.class);
            intent.putExtra("mode","creation");
            startActivityForResult(intent,TANK_DETAILS_CREATION);

        }
        if(item.getItemId()==R.id.Help){
            showItemsHelpDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showItemsHelpDialog() {

        ViewGroup viewGroup = findViewById(android.R.id.content);

        View view = getLayoutInflater().inflate(R.layout.items_help_dialog_view_2,viewGroup,false);
        AlertDialog.Builder helpDialogBuilder = new AlertDialog.Builder(this);
        helpDialogBuilder.setView(view);


        final Dialog helpDialog = helpDialogBuilder.create();
        helpDialog.show();

        Button okButton = view.findViewById(R.id.help_ok_button2);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpDialog.dismiss();
            }
        });

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_nearby) {
            startMapsActivty();
        }
        else if (id == R.id.nav_privacy) {

            Intent i4 = new Intent(this, ConditionsActivity.class);
            i4.putExtra("URL","https://plantedaquaapp.blogspot.com/p/privacy-policy.html");
            startActivity(i4);
        }
        else if (id == R.id.nav_open_source_license) {

            Intent i4 = new Intent(this, ConditionsActivity.class);
            i4.putExtra("URL","https://plantedaquaapp.blogspot.com/p/copyright-information.html");
            startActivity(i4);
        }else if (id == R.id.nav_disclaimer) {

            Intent i4 = new Intent(this, ConditionsActivity.class);
            i4.putExtra("URL","https://plantedaquaapp.blogspot.com/p/disclaimer.html");
            startActivity(i4);
        }
        else if (id == R.id.nav_help) {

            Intent i4 = new Intent(this, ConditionsActivity.class);
            i4.putExtra("URL","https://plantedaquaapp.blogspot.com/2018/09/get-started-with-planted-aqua.html");
            startActivity(i4);
        }
        else if (id == R.id.nav_icons8) {

            Intent i4 = new Intent(this, ConditionsActivity.class);
            i4.putExtra("URL","https://icons8.com/");
            startActivity(i4);
        }
        else if (id == R.id.nav_seller) {

            startShopActivty();


        }
        else if (id == R.id.nav_settings) {

            Intent iSettings = new Intent(this, SettingsActivity.class);
            startActivity(iSettings);
        }

        else if (id == R.id.nav_users_gallery) {

            if (mAuth.getCurrentUser() != null) {

                Intent iUsersGallery = new Intent(this, UsersGalleryActivity.class);

                iUsersGallery.putExtra("UserID", mAuth.getCurrentUser().getUid());
                startActivity(iUsersGallery);
            }
            else
            {
                Toast.makeText(this,"Sorry! You must be logged in to use this feature",Toast.LENGTH_SHORT).show();
            }



        }


        else if (id == R.id.nav_micro_calc) {

            Intent iExpense = new Intent(this, MicroNutrientTableActivity.class);
            startActivity(iExpense);
        }



        else if (id == R.id.nav_chatbox) {

            if (mAuth.getCurrentUser() != null) {

                Intent iChatBox = new Intent(this, ChatBoxActivity.class);

                iChatBox.putExtra("UserID", mAuth.getCurrentUser().getUid());
                startActivity(iChatBox);
            }
            else
            {
                Toast.makeText(this,"Sorry! You must be logged in to use this feature",Toast.LENGTH_SHORT).show();
            }



        }
        else if(id == R.id.nav_my_chats){

            Intent iChats = new Intent(this, ChatUsersActivity.class);
            startActivity(iChats);

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startMapsActivty(){
        if (mAuth.getCurrentUser() == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.Attention))
                    .setMessage(getResources().getString(R.string.NotLoggedInMsg))
                    .setNeutralButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();



        } else {
            // String userID = "ma";
            validateAndProceed();




        }
    }

    private void startShopActivty(){
        if (mAuth.getCurrentUser() == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.Attention))
                    .setMessage(getResources().getString(R.string.NotLoggedInMsg))
                    .setNeutralButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();



        } else {
            // String userID = "ma";
            Intent iShop = new Intent(this, SellerActivity.class);
            iShop.putExtra("User", "Own");
            startActivity(iShop);




        }
    }
    String userType = "";
    private void validateAndProceed() {

        final View checkView=LayoutInflater.from(this).inflate(R.layout.remember_layout,null);
        final TinyDB settingsDB=new TinyDB(this.getApplicationContext());

            final AlertDialog.Builder messageDialog = new AlertDialog.Builder(this);
            messageDialog.setMessage(R.string.SellerShopMsg)
                    .setCancelable(false)
                    .setIcon(R.drawable.attention)
                    .setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent in = new Intent(A1Activity.this, MapsActivity.class);
                            in.putExtra("UserType", settingsDB.getString("UserType"));
                            startActivity(in);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

       // settingsDB.putBoolean("RememberSorH",false);
        if(settingsDB.getBoolean("RememberSorH")) {
            if (settingsDB.getString("UserType").equals("Seller")) {
                messageDialog.create().show();
            } else {
                Intent in = new Intent(A1Activity.this, MapsActivity.class);
                in.putExtra("UserType", settingsDB.getString("UserType"));
                startActivity(in);
            }
        }

        else {

            final CharSequence items[] = {getResources().getString(R.string.Hobbyist), getResources().getString(R.string.Seller), getResources().getString(R.string.Cancel)};

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.HorS))
                    .setView(checkView)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (items[i].equals(getResources().getString(R.string.Cancel))) {

                                dialogInterface.dismiss();
                            } else {
                                userType = items[i].toString();
                                Boolean rememberChecked = false;
                                CheckBox checkBox = checkView.findViewById(R.id.RememberCheckbox);
                                if (checkBox.isChecked())
                                    rememberChecked = true;
                                settingsDB.putBoolean("RememberSorH", rememberChecked);
                                settingsDB.putString("UserType", userType);
                                if(userType.equals("Seller")) {
                                    messageDialog.create().show();
                                }else {
                                    Intent in = new Intent(A1Activity.this, MapsActivity.class);
                                    in.putExtra("UserType", userType);
                                    startActivity(in);
                                }
                            }
                        }
                    }).create().show();
        }
    }



    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(A1Activity.this,"User Signed in",Toast.LENGTH_SHORT).show();
                            user = mAuth.getCurrentUser();
                           // progressDialog.dismiss();
                            spotsProgressDialog.dismiss();
                            SignInUpdateUI();
                            drawer.closeDrawer(GravityCompat.START);


                        } else {
                            // If sign in fails, display a message to the user.
                            try {
                                throw Objects.requireNonNull(task.getException());
                            }catch(Exception e) {
                                //Log.e(TAG, e.getMessage());
                            }
                            Toast.makeText(A1Activity.this, "Authentication Failed.",Toast.LENGTH_SHORT).show();
                            spotsProgressDialog.dismiss();
                           // progressDialog.dismiss();
                            SignOutUpdateUI();
                            drawer.closeDrawer(GravityCompat.START);

                        }

                        // ...
                    }
                });
    }

    private void setInstructionVisibility() {

        if (tanksDetailsArrayList.isEmpty()){
            instructionText.setVisibility(View.VISIBLE);
        }
        else
            instructionText.setVisibility(View.GONE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                loadUserTankImages();
            } catch (ApiException e) {
                Toast.makeText(A1Activity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                spotsProgressDialog.dismiss();
               // progressDialog.cancel();
            }
        }

        if(resultCode== Activity.RESULT_OK ) {
            if(requestCode==TANK_DETAILS_CREATION) {

                setInstructionVisibility();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        tanksSectionsPagerAdapter.notifyDataSetChanged();
                        viewPager2.setCurrentItem(tanksDetailsArrayList.size()-1);
                        currentFragmentInstance = (TanksPlaceholderFragment) tanksSectionsPagerAdapter.getItem(tanksDetailsArrayList.size()-1);

                            currentFragmentInstance.updateRecoRecyclerView();


                    }
                });

            }
          else if(requestCode==TANK_DETAILS_MODIFICATION){

                int position=data.getIntExtra("Position",-1);
                setInstructionVisibility();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        tanksSectionsPagerAdapter.notifyDataSetChanged();
                        viewPager2.setCurrentItem(position);
                        currentFragmentInstance = (TanksPlaceholderFragment) tanksSectionsPagerAdapter.getItem(position);

                            currentFragmentInstance.updateRecoRecyclerView();

                    }
                });




            }

          else if (requestCode == LIGHT_ACTIVITY_REQUEST_CODE) {
                int position=data.getIntExtra("Position",-1);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        tanksSectionsPagerAdapter.notifyDataSetChanged();
                        viewPager2.setCurrentItem(position);
                        currentFragmentInstance = (TanksPlaceholderFragment) tanksSectionsPagerAdapter.getItem(position);
                        currentFragmentInstance.updateRecoRecyclerView();


                    }
                });
            }
        }


    }



    private void setAlarmOn1stDay() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(

                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                1,
                9,
                0,
                0
        );

        Calendar calendar_everday = Calendar.getInstance();
        calendar_everday.set(
                calendar_everday.get(Calendar.YEAR),
                calendar_everday.get(Calendar.MONTH),
                calendar_everday.get(Calendar.DAY_OF_MONTH),
                10,
                0,
                0

        );

        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.MONTH, 1);
        }

        if(calendar_everday.getTimeInMillis() < System.currentTimeMillis()) {
            calendar_everday.add(Calendar.DAY_OF_MONTH,1);
        }

        defaultAlarms(calendar,1,"MONTHLY_EXPENSE");
        defaultAlarms(calendar_everday,2,"EVERYDAY");


    }

    private void defaultAlarms(Calendar calendar,int reqCode, String alarmType){

        long alarmInMillis;

        alarmInMillis = calendar.getTimeInMillis();
        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this.getApplicationContext(), RamizAlarm.class);
        intent.putExtra("AT",alarmType);

        //Log.i("Default_Alarm",Long.toString(alarmInMillis));

        PendingIntent pi = PendingIntent.getBroadcast(this, reqCode, intent, PendingIntent.FLAG_NO_CREATE);
        if(pi!=null){
            //System.out.println("not null");
            //System.out.println("Ramiz: Pending intent exists ");
            pi = PendingIntent.getBroadcast(this, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            assert alarmManager != null;
            alarmManager.cancel(pi);

        }

        pi = PendingIntent.getBroadcast(this.getApplicationContext(), reqCode, intent, 0);
        if (Build.VERSION.SDK_INT >= 23) {
            assert alarmManager != null;
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmInMillis, pi);
        }
        else if (Build.VERSION.SDK_INT >= 19) {
            assert alarmManager != null;
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmInMillis, pi);
        } else {
            assert alarmManager != null;
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmInMillis, pi);
        }

    }

    RecyclerView userTankImagesRecyclerView;
    ArrayList<GalleryInfo> galleryInfoArrayList = new ArrayList<>();
    private void loadUserTankImages(){

        if(timerTask==null) {
            DatabaseReference galleryItemRef = FirebaseDatabase.getInstance().getReference("GI");

            Query galleryItemRefQuery = galleryItemRef.orderByChild("rating");




            ShowcaseRecyclerAdapter<GalleryInfo> showcaseAdapter = new ShowcaseRecyclerAdapter<>(galleryInfoArrayList, R.layout.showcase_recycler_view_item, (view, pos) -> {
                Intent iUsersGallery = new Intent(A1Activity.this, UsersGalleryActivity.class);
                iUsersGallery.putExtra("UserID", mAuth.getCurrentUser().getUid());
                iUsersGallery.putExtra("Position", pos);
                startActivity(iUsersGallery);
            });
            userTankImagesRecyclerView.setAdapter(showcaseAdapter);

            galleryItemRefQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    GalleryInfo galleryInfo = new GalleryInfo();
                    galleryInfo = dataSnapshot.getValue(GalleryInfo.class);
                    galleryInfoArrayList.add(galleryInfo);
                    showcaseAdapter.notifyItemInserted(galleryInfoArrayList.size() - 1);
                    if (galleryInfoArrayList.size() == 1) {
                        userTankImagesRecyclerView.setVisibility(View.VISIBLE);
                        startCarousel();
                    }
                    if (galleryInfoArrayList.size() == 0) {
                        userTankImagesRecyclerView.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(A1Activity.this, "Database Error Occurred. Please check your Internet Connection", Toast.LENGTH_LONG).show();

                }
            });
        }

        userTankImagesRecyclerView.setVisibility(View.VISIBLE);

    }

    private AdView adView;


    private void loadBannerAd() {


        adView = new AdView(this, "200298157916823_200419604571345", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        AdView.AdViewLoadConfig loadAdConfig = adView.buildLoadAdConfig()
                .withAdListener(adListener)
                .build();

        adView.loadAd(loadAdConfig);


    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private int pos = 0;
    private Timer timer = new Timer();
    private Boolean taskCancelled = false;
    private TimerTask timerTask;

    @Override
    protected void onPause() {
        super.onPause();
        tinyDB.putInt("A1_VIEWPAGER_POSITION",viewPager2.getCurrentItem());
        timer.cancel();
        if(timerTask!=null) {
            taskCancelled = timerTask.cancel();
        }
    }

    private void startCarousel(){

        timerTask = new TimerTask() {

            @Override
            public void run() {
                if (pos == galleryInfoArrayList.size() - 1) {
                    pos = 0;
                }
                A1Activity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userTankImagesRecyclerView.scrollToPosition(pos);
                        pos++;
                    }
                });


            }

        };


        timer = new Timer();


        timer.scheduleAtFixedRate(
                timerTask, 1000L, 4000L
        );



    }

    @Override
    protected void onResume() {

        viewPager2.setCurrentItem(tinyDB.getInt("A1_VIEWPAGER_POSITION"));
        super.onResume();
            if (taskCancelled && galleryInfoArrayList.size()>0) {

                startCarousel();

            }


        }

    private void setAlarmForNewVersion(){

        MyDbHelper alarmdbhelper1;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        SQLiteDatabase db1 = tankDBHelper.getWritableDatabase();
        String AquariumName;
        PendingIntent pi;
        String storedTimeInMillis;
        Cursor c1 = tankDBHelper.getData(db1);
        while (c1.moveToNext()) {

            AquariumName = c1.getString(2);
            alarmdbhelper1 = MyDbHelper.newInstance(this, c1.getString(1));
            SQLiteDatabase db2 = alarmdbhelper1.getWritableDatabase();

            Cursor c = alarmdbhelper1.getData(db2);


            while (c.moveToNext()) {
                Calendar calendar = Calendar.getInstance();
                storedTimeInMillis = c.getString(2);


                calendar.set(

                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        c.getInt(5),
                        c.getInt(6),
                        0
                );


                calendar.set(Calendar.DAY_OF_WEEK, c.getInt(4));
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 7);
                }

                alarmdbhelper1.updateTimeInMillis(db2, Long.toString(calendar.getTimeInMillis()), storedTimeInMillis);



                Intent inn = new Intent(this, RamizAlarm.class);
                inn.putExtra("AT", c.getString(1));
                inn.putExtra("AlarmName", c.getString(0));
                inn.putExtra("NotifyType", c.getString(7));
                inn.putExtra("AquariumID", c1.getString(1));
                inn.putExtra("KEY_TRIGGER_TIME", calendar.getTimeInMillis());
                inn.putExtra("KEY_INTENT_ID", c.getInt(3));
                inn.putExtra("AquariumName", AquariumName);

                pi = PendingIntent.getBroadcast(this, c.getInt(3), inn, PendingIntent.FLAG_UPDATE_CURRENT);
                if (Build.VERSION.SDK_INT >= 23) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                } else if (Build.VERSION.SDK_INT >= 19) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                }
            }
            c.close();


        }
        c1.close();

    }


    //region EACH TANK IN VIEWPAGER

    private ArrayList<TanksDetails> tanksDetailsArrayList;
    private TanksSectionsPagerAdapter tanksSectionsPagerAdapter;
    private ViewPager viewPager2;
    private TinyDB tinyDB;
    private ExpenseDBHelper expenseDBHelper;
    TanksPlaceholderFragment currentFragmentInstance;
    private void loadViewPagerTankDetails(){
        tinyDB = new TinyDB(getApplicationContext());
        tanksDetailsArrayList = new ArrayList<>();
        expenseDBHelper = ExpenseDBHelper.getInstance(A1Activity.this.getApplicationContext());
        TanksDetails tanksDetails = new TanksDetails();
        Cursor c;
        float sum = 0f;
        Cursor cursor = tankDBHelper.getData(tankDBHelper.getReadableDatabase());
        if(cursor!=null){
            if(cursor.moveToFirst()){
                do{
                    tanksDetails = new TanksDetails();
                    tanksDetails.setCurrency(tinyDB.getString("DefaultCurrencySymbol"));
                    tanksDetails.setTankPrice(cursor.getString(5));
                    tanksDetails.setTankVolume(cursor.getString(7));
                    tanksDetails.setTankVolumeMetric(cursor.getString(8));
                    tanksDetails.setTankID(cursor.getString(1));
                    tanksDetails.setTankName(cursor.getString(2));
                    tanksDetails.setTankPicUri(cursor.getString(3));
                    tanksDetails.setTankType(cursor.getString(4));
                    tanksDetails.setTankStatus(cursor.getString(9));
                    tanksDetails.setTankStartDate(cursor.getString(10));
                    tanksDetails.setTankEndDate(cursor.getString(11));
                    tanksDetails.setTankCO2Supply(cursor.getString(12));
                    tanksDetails.setTankLightRegion(cursor.getString(16)==null?"Dark":cursor.getString(16));
                    tanksDetails.setMicroDosageText(cursor.getString(20)==null?"":cursor.getString(20));
                    tanksDetails.setMacroDosageText(cursor.getString(21)==null?"":cursor.getString(21));


                    //Get Tank Expense
                    sum = 0f;
                    c = expenseDBHelper.getExpensePerGroupWithGroupValue("TankName", "", "", tanksDetails.getTankName());
                    if (c != null) {
                        if (c.moveToFirst()) {
                            do {
                                sum += c.getFloat(1);
                            } while (c.moveToNext());
                        }
                        c.close();
                    }
                    tanksDetails.setCumExpenses(String.format(Locale.getDefault(),"%.2f",sum));
                    tanksDetailsArrayList.add(tanksDetails);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }




        setInstructionVisibility();
        viewPager2 = findViewById(R.id.TanksViewPager);
        tanksSectionsPagerAdapter = new TanksSectionsPagerAdapter(tanksDetailsArrayList,getSupportFragmentManager());
        viewPager2.setAdapter(tanksSectionsPagerAdapter);






    }



    //Adding Click Events to Icons in Each Tank Card
    public void onTankDashBoardItemsClicked(View view){

        switch(view.getId()){
            case R.id.deleteTank : removeTankFromPager(view.getTag().toString());
            break;
            case R.id.imageNutrientOption : navigateToSelectedOption(new Intent(this,DosingGraphsActivity.class),viewPager2.getCurrentItem(),"");
                break;
            case R.id.imageFloraOption: navigateToSelectedOption(new Intent(this,TankItemListActivity.class),viewPager2.getCurrentItem(),"Fl");
                break;
            case R.id.imageFaunaOption: navigateToSelectedOption(new Intent(this,TankItemListActivity.class),viewPager2.getCurrentItem(),"Fr");
                break;
            case R.id.imageTanksEquipmentOption: navigateToSelectedOption(new Intent(this,TankItemListActivity.class),viewPager2.getCurrentItem(),"E");
                break;
            case R.id.imageLogOptions: navigateToSelectedOption(new Intent(this,LogsActivity.class),viewPager2.getCurrentItem(),"");
                break;
            case R.id.imageTaskOptions: navigateToSelectedOption(new Intent(this,TasksActivity.class),viewPager2.getCurrentItem(),"");
                break;
            case R.id.imageLightOption: navigateToSelectedOption(new Intent(this,LightCalcActivity.class),viewPager2.getCurrentItem(),"Light");
                break;
            case R.id.imageMacroOption:
            case R.id.SetMacroValue:
                navigateToSelectedOption(new Intent(this,MacroNutrientTableActivity.class),viewPager2.getCurrentItem(),"");
                break;
            case R.id.imageMicroOption:
            case R.id.SetMicroValue:
                navigateToSelectedOption(new Intent(this,MicroNutrientTableActivity.class),viewPager2.getCurrentItem(),"");
                break;
            case R.id.imageTPAOptions: navigateToSelectedOption(new Intent(this,TankProgressActivity.class),viewPager2.getCurrentItem(),"");
                break;


            default : editTankDetails();
            break;
        }



    }

    private void editTankDetails(){
        Intent intent=new Intent(A1Activity.this,CreateTankActivity.class);
        intent.putExtra("mode","modification");
        intent.putExtra("Position",viewPager2.getCurrentItem());
        startActivityForResult(intent,TANK_DETAILS_MODIFICATION);
    }


    private void removeTankFromPager(String aquariumID){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Delete Tank")
                .setMessage("This action will delete your tank and all relevant data. Do you wish to continue?")
                .setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                       // Log.i("AQUARIUMID",aquariumID);
                        getApplication().deleteDatabase(aquariumID);
                        tankDBHelper.deleteItem("AquariumID", aquariumID);
                        tankDBHelper.deleteItemReco(aquariumID);
                        tankDBHelper.deleteItemLight(aquariumID);
                        NutrientDbHelper nutrientDbHelper = NutrientDbHelper.newInstance(A1Activity.this.getApplicationContext(), aquariumID);
                        expenseDBHelper.deleteExpense("AquariumID", aquariumID);
                        nutrientDbHelper.deleteNutrientTables();
                      // Log.i("POSITION",Integer.toString(viewPager2.getCurrentItem()));
                        tanksDetailsArrayList.remove(viewPager2.getCurrentItem());
                        tanksSectionsPagerAdapter.notifyDataSetChanged();
                        setInstructionVisibility();
                       // Log.i("SIZE",Integer.toString(tanksDetailsArrayList.size()));
                    }
                })
                .create()
                .show();
    }

    private int LIGHT_ACTIVITY_REQUEST_CODE = 81;

    private void navigateToSelectedOption(Intent intent,int position, String itemCategory){


        intent.putExtra("AquariumID",tanksDetailsArrayList.get(position).getTankID());
        intent.putExtra("AquariumName",tanksDetailsArrayList.get(position).getTankName());
        intent.putExtra("ItemCategory",itemCategory);
        intent.putExtra("Position",position);

        if(itemCategory.equals("Light")){
            startActivityForResult(intent,LIGHT_ACTIVITY_REQUEST_CODE);
        }
        else{
            startActivity(intent);
        }



    }
    //endregion




}

