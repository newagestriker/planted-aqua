package com.newage.plantedaqua.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;
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
import com.newage.plantedaqua.adapters.RecyclerAdapterPicsInfo;
import com.newage.plantedaqua.helpers.TankDBHelper;
import com.newage.plantedaqua.helpers.TinyDB;
import com.newage.plantedaqua.models.GalleryInfo;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import com.facebook.ads.*;



public class A1Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<TankProgressDetails> tanklabels=new ArrayList <>();
    private TankProgressDetails tpd,temp;
    private RecyclerView.Adapter adapter;
    private int TANK_DETAILS_CREATION=1;
    private int TANK_DETAILS_MODIFICATION=2;
    private boolean clicked=false;
    private ArrayList<String> TagList=new ArrayList <>();
    private RelativeLayout relativeLayout;
    private TankDBHelper tankDBHelper;
    private Snackbar snackbar;
    private View headerview;
    private static final int RC_SIGN_IN = 47 ;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ProgressDialog progressDialog;
    private View userGSignIn;
    private DrawerLayout drawer;
    private TextView instructionText;
    private DatabaseReference devRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        TinyDB rebootRequired = new TinyDB(this);


        int currentVersionCode = BuildConfig.VERSION_CODE;
        int storedVersionCode = rebootRequired.getInt("STORED_VERSION_CODE");
        if(storedVersionCode <25){
            modifyDBs();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (storedVersionCode > 0 && storedVersionCode != currentVersionCode) {
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

        loadBannerAd();

//        ImageView imageView = findViewById(R.id.testImage);
//        imageView.setOnClickListener(v -> {
//            startActivity(new Intent(this,TestActivity.class));
//        });

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
        /*TextView profilename = (TextView) headerview.findViewById(R.id.prof_username);
        profilename.setText("your name");*/


        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.PleaseWait));
        userGSignIn = headerview.findViewById(R.id.sign_in_button);
        userGSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                progressDialog.show();

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

        insertTankRow();

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


        // ATTENTION: This was auto-generated to handle app links.
        /*Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();*/
    }

    private void modifyDBs() {

        TankDBHelper tankDBHelper = TankDBHelper.newInstance(getApplicationContext());
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
        } else if (id == R.id.nav_algae) {

            Intent i3 = new Intent(this, AlgaeActivity.class);
            startActivity(i3);
        }else if (id == R.id.nav_privacy) {

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

        else if (id == R.id.nav_wallet) {

            Intent iExpense = new Intent(this, ExpenseActivity.class);
            startActivity(iExpense);
        }

        else if (id == R.id.nav_macro_calc) {

            Intent iExpense = new Intent(this, MacroNutrientTableActivity.class);
            startActivity(iExpense);
        }

        else if (id == R.id.nav_micro_calc) {

            Intent iExpense = new Intent(this, MicroNutrientTableActivity.class);
            startActivity(iExpense);
        }

        else if (id == R.id.nav_converter) {

            Intent iExpense = new Intent(this, ConverterActivity.class);
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
                            progressDialog.dismiss();
                            SignInUpdateUI();
                            drawer.closeDrawer(GravityCompat.START);


                        } else {
                            // If sign in fails, display a message to the user.
                            try {
                                throw task.getException();
                            }catch(Exception e) {
                                //Log.e(TAG, e.getMessage());
                            }
                            Toast.makeText(A1Activity.this, "Authentication Failed.",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            SignOutUpdateUI();
                            drawer.closeDrawer(GravityCompat.START);

                        }

                        // ...
                    }
                });
    }

    private void setInstructionVisibility() {

        if (tanklabels.isEmpty()){
            instructionText.setVisibility(View.VISIBLE);
        }
        else
            instructionText.setVisibility(View.GONE);
    }

    private void insertTankRow(){

        tankDBHelper =TankDBHelper.newInstance(this);
        SQLiteDatabase DB = tankDBHelper.getWritableDatabase();
        Cursor c=tankDBHelper.getData(DB);


        while(c.moveToNext()){

            tpd=new TankProgressDetails();
            tpd.setImagedate(c.getString(10));
            tpd.setImageuri(c.getString(3));
            tpd.setText1(c.getString(2));
            tpd.setText2(c.getString(4));
            tpd.setTag(c.getString(1));
            tanklabels.add(tpd);

        }
        c.close();

        setInstructionVisibility();

        relativeLayout=findViewById(R.id.mainLayout);

        RecyclerView startScreenRecyclerView = findViewById(R.id.startScreenRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        startScreenRecyclerView.setLayoutManager(layoutManager);
        adapter=new RecyclerAdapterPicsInfo(tanklabels, this,R.layout.each_tank_layout, new RecyclerAdapterPicsInfo.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, String uri) {



               /* Intent i = new Intent(view.getContext(),OptionsActivity.class);
                i.putExtra("AquariumID",tanklabels.get(position).getTag());
                // System.out.println("Aquarium ID :" + tanklabels.get(position).getTag());
                startActivity(i);*/

                if(view.getTag().equals(1)){

                    Intent intent=new Intent(A1Activity.this,CreateTankActivity.class);
                    intent.putExtra("mode","modification");
                    intent.putExtra("Position",position);
                    startActivityForResult(intent,TANK_DETAILS_MODIFICATION);

                }
                else if(view.getTag().equals(2)){

                    Intent intent = new Intent(view.getContext(), OptionsActivity.class);
                    intent.putExtra("AquariumID",tanklabels.get(position).getTag());
                    // System.out.println("Aquarium ID :" + tanklabels.get(position).getTag());
                    startActivity(intent);

                }

                else if(view.getTag().equals(3)) {

                    final Cursor cursor = tankDBHelper.getDataCondition("AquariumID",tanklabels.get(position).getTag());
                    if (cursor.moveToFirst()) {

                        View infoView = getLayoutInflater().inflate(R.layout.tank_info_layout, null);
                        setAlldata(cursor, infoView);
                        AlertDialog.Builder builder = new AlertDialog.Builder(A1Activity.this);
                        builder.setCancelable(false)
                                .setView(infoView)
                                .setNeutralButton(getResources().getText(R.string.OK), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();


                    }
                    cursor.close();
                }



            }

            @Override
            public void onLongClick(View view, int position) {

                Intent intent=new Intent(A1Activity.this,CreateTankActivity.class);
                intent.putExtra("mode","modification");
                intent.putExtra("Position",position);
                startActivityForResult(intent,TANK_DETAILS_MODIFICATION);

            }
        });
        startScreenRecyclerView.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //swiped position
                clicked=false;
                if (direction == ItemTouchHelper.LEFT||direction == ItemTouchHelper.RIGHT) { //swipe left
                    temp=new TankProgressDetails();
                    temp=tanklabels.get(position);
                    TagList.add(temp.getTag());
                    tanklabels.remove(position);
                    adapter.notifyItemRemoved(position);
                    setInstructionVisibility();
                    snackbar = Snackbar
                            .make(relativeLayout, "Record deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    tanklabels.add(position,temp);
                                    clicked=true;
                                    TagList.remove(TagList.size()-1);
                                    adapter.notifyItemInserted(position);
                                    setInstructionVisibility();
                                    snackbar.dismiss();
                                }
                            }).addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int dismissType) {
                                    super.onDismissed(snackbar, dismissType);
                                    String Tag;

                                    if((dismissType == DISMISS_EVENT_TIMEOUT ||dismissType == DISMISS_EVENT_ACTION|| dismissType == DISMISS_EVENT_SWIPE
                                            || dismissType == DISMISS_EVENT_CONSECUTIVE || dismissType == DISMISS_EVENT_MANUAL)&& !clicked) {
                                        for(int i=0;i<TagList.size();i++) {
                                            Tag=TagList.get(i);
                                            getApplicationContext().deleteDatabase(Tag);
                                            tankDBHelper.deleteItem("AquariumID", Tag);
                                            tankDBHelper.deleteItemReco(Tag);
                                            tankDBHelper.deleteItemLight(Tag);
                                            NutrientDbHelper nutrientDbHelper = NutrientDbHelper.newInstance(A1Activity.this,Tag);
                                            ExpenseDBHelper expenseDBHelper = ExpenseDBHelper.getInstance(A1Activity.this);
                                            expenseDBHelper.deleteExpense("AquariumID",Tag);
                                            nutrientDbHelper.deleteNutrientTables();
                                            TagList.remove(i);
                                           // System.out.println("Tag : "+Tag);
                                        }
                                    }
                                }});

                    snackbar.show();

                }


            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(startScreenRecyclerView);


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
            } catch (ApiException e) {
                Toast.makeText(A1Activity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
            }
        }

        if(resultCode== Activity.RESULT_OK ) {
            if(requestCode==TANK_DETAILS_CREATION) {
                tpd = new TankProgressDetails();
                tpd.setImagedate(data.getStringExtra("StartupDate"));
                tpd.setImageuri(data.getStringExtra("ImageUri"));
                tpd.setText1(data.getStringExtra("Aquaname"));
                tpd.setText2(data.getStringExtra("Aquatype"));
                tpd.setTag(data.getStringExtra("Tag"));
                tanklabels.add(tpd);


                adapter.notifyItemInserted(tanklabels.size() - 1);
                setInstructionVisibility();
            }
            if(requestCode==TANK_DETAILS_MODIFICATION){

                int position=data.getIntExtra("Position",-1);
                tanklabels.get(position).setImagedate(data.getStringExtra("StartupDate"));
                //System.out.println("Data : "+data.getStringExtra("StartupDate"));
                tanklabels.get(position).setImageuri(data.getStringExtra("ImageUri"));
                tanklabels.get(position).setText1(data.getStringExtra("Aquaname"));
                tanklabels.get(position).setText2(data.getStringExtra("Aquatype"));
                tanklabels.get(position).setTag(data.getStringExtra("Tag"));
                adapter.notifyItemChanged(position);
                setInstructionVisibility();

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


    public void setAlldata(Cursor c,View view){

        TinyDB settingsDB = new TinyDB(getApplicationContext());



        TextView AquaName=view.findViewById(R.id.AquariumNameText);
        AquaName.setText(c.getString(2));


        TextView Price=view.findViewById(R.id.AquariumPriceText);
        Price.setText(c.getString(5));

        TextView LightType=view.findViewById(R.id.AquariumLightTypeText);
        LightType.setText(c.getString(13));

        TextView Wattage=view.findViewById(R.id.AquariumWattageText);
        Wattage.setText(c.getString(14));


        TextView StartupDate=view.findViewById(R.id.AquariumAcDateText);
        StartupDate.setText(c.getString(10));


        TextView DismantleDate=view.findViewById(R.id.AquariumDisDateText);
        DismantleDate.setText(c.getString(11));


        TextView LumensPerWatt=view.findViewById(R.id.AquariumLumensText);
        LumensPerWatt.setText(c.getString(15));

        TextView Currency=view.findViewById(R.id.AquariumCurrencyText);
        Currency.setText(settingsDB.getString("DefaultCurrencySymbol"));

        TextView Volume=view.findViewById(R.id.AquariumVolumeText);
        Volume.setText(c.getString(7));

        TextView VolumeMetric=view.findViewById(R.id.AquariumMetricText);
        VolumeMetric.setText(c.getString(8));

        TextView AquariumType=view.findViewById(R.id.AquariumTypeText);
        AquariumType.setText(c.getString(4));

        TextView CurrentStatus=view.findViewById(R.id.AquariumStatusText);
        CurrentStatus.setText(c.getString(9));


        TextView CO2=view.findViewById(R.id.AquariumCo2Text);
        CO2.setText(c.getString(12));




        ImageView tankImage = view.findViewById(R.id.TankImageInfo);

        String s=c.getString(3);
        if(!s.isEmpty()) {

            Uri tankpicUri;


            tankpicUri = Uri.parse(s);
            Glide.with(this)
                    .load(tankpicUri)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.drawable.aquarium2))
                    .into(tankImage);

        }


    }
    RecyclerView userTankImagesRecyclerView;
    ArrayList<GalleryInfo> galleryInfoArrayList = new ArrayList<>();
    private void loadUserTankImages(){

        DatabaseReference  galleryItemRef = FirebaseDatabase.getInstance().getReference("GI");

        Query galleryItemRefQuery = galleryItemRef.orderByChild("rating");



        userTankImagesRecyclerView = findViewById(R.id.ShowcaseTankRecyclerView);
        RecyclerView.Adapter showcaseAdapter = new ShowcaseRecyclerAdapter<>(galleryInfoArrayList, R.layout.showcase_recycler_view_item, (view, pos) -> {
            Intent iUsersGallery = new Intent(A1Activity.this, UsersGalleryActivity.class);
            iUsersGallery.putExtra("UserID", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
            iUsersGallery.putExtra("Position",pos);
            startActivity(iUsersGallery);
        });
        userTankImagesRecyclerView.setAdapter(showcaseAdapter);

        galleryItemRefQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                GalleryInfo galleryInfo = new GalleryInfo();
                galleryInfo = dataSnapshot.getValue(GalleryInfo.class);
                galleryInfoArrayList.add(galleryInfo);
                showcaseAdapter.notifyItemInserted(galleryInfoArrayList.size()-1);
                if(galleryInfoArrayList.size()==1){
                    startCarousel();
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

                Toast.makeText(A1Activity.this,"Database Error Occurred. Please check your Internet Connection",Toast.LENGTH_LONG).show();

            }
        });



    }

    private AdView adView;


    private void loadBannerAd() {
         // Instantiate an AdView object.
        // NOTE: The placement ID from the Facebook Monetization Manager identifies your App.
        // To get test ads, add IMG_16_9_APP_INSTALL# to your placement id. Remove this when your app is ready to serve real ads.

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
        super.onResume();
            if (taskCancelled && galleryInfoArrayList.size()>0) {

                startCarousel();

            }


        }

}

