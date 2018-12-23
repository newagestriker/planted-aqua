package com.newage.plantedaqua;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

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

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class A1Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<TankProgressDetails> tanklabels=new ArrayList <>();
    TankProgressDetails tpd,temp;
    RecyclerView startScreenRecyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    int TANK_DETAILS_CREATION=1;
    int TANK_DETAILS_MODIFICATION=2;
    boolean clicked=false;
    ArrayList<String> TagList=new ArrayList <>();
    LinearLayout linearLayout;
    TankDBHelper tankDBHelper;
    SQLiteDatabase DB;
    Snackbar snackbar;
    View headerview;
    private static final int RC_SIGN_IN =47 ;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ProgressDialog progressDialog;
    View userGSignIn;
    TextView logout;
    DrawerLayout drawer;
    TinyDB rebootRequired;
    int currentVersionCode;
    int storedVersionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rebootRequired = new TinyDB(this);


        currentVersionCode = BuildConfig.VERSION_CODE;
        storedVersionCode = rebootRequired.getInt("STORED_VERSION_CODE");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (storedVersionCode < currentVersionCode) {
            builder.setTitle(getResources().getString(R.string.Attention))
                    .setMessage(getResources().getString(R.string.AppUpdated))
                    .setNeutralButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            rebootRequired.putInt("STORED_VERSION_CODE", currentVersionCode);
                            dialog.dismiss();
                        }
                    }).create().show();

        }

        setContentView(R.layout.activity_a1);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


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

        logout = headerview.findViewById(R.id.Logout);
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


        // ATTENTION: This was auto-generated to handle app links.
        /*Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();*/
    }

    TinyDB userAcceptance;
    @Override
    protected void onStart() {
        super.onStart();
        checkConsent();
        if(mAuth.getCurrentUser()!=null) {

            SignInUpdateUI();
        }
        else{

            SignOutUpdateUI();

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

        Glide.with(this)
                .load(user.getPhotoUrl())
                .into((ImageView) headerview.findViewById(R.id.NavImageView));

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
        return super.onOptionsItemSelected(item);
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
        else if (id == R.id.nav_seller) {

            startShopActivty();


        }
        else if (id == R.id.nav_settings) {

            Intent iSettings = new Intent(this, SettingsActivity.class);
            startActivity(iSettings);
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

            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
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

        final String TAG="ramiz";

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

    public void insertTankRow(){

        tankDBHelper =TankDBHelper.newInstance(this);
        DB = tankDBHelper.getWritableDatabase();
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

        linearLayout=findViewById(R.id.mainLayout);

        startScreenRecyclerView=findViewById(R.id.startScreenRecyclerView);
        layoutManager=new LinearLayoutManager(this);
        startScreenRecyclerView.setLayoutManager(layoutManager);
        adapter=new RecyclerAdapterPicsInfo(tanklabels, this, new RecyclerAdapterPicsInfo.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, String uri) {



                Intent i = new Intent(view.getContext(),OptionsActivity.class);
                i.putExtra("AquariumID",tanklabels.get(position).getTag());
                // System.out.println("Aquarium ID :" + tanklabels.get(position).getTag());
                startActivity(i);

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
                    snackbar = Snackbar
                            .make(linearLayout, "Record deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    tanklabels.add(position,temp);
                                    clicked=true;
                                    TagList.remove(TagList.size()-1);
                                    adapter.notifyItemInserted(position);
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

            }
        }


    }
}

