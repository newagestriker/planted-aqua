package com.newage.aquapets.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.newage.aquapets.helpers.ConnectionDetector;
import com.newage.aquapets.R;
import com.newage.aquapets.room.ChatUsers;
import com.newage.aquapets.room.SingleChatDB;
import com.onesignal.OneSignal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener,GoogleMap.OnMapLongClickListener,GoogleMap.OnMapClickListener {
    private GoogleMap mMap;
    private LatLng centrePosition;
    private Marker marker;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseAuth mAuth;
    private String userID;
    private DatabaseReference allUserDB;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private ProgressDialog progressDialog;
    private LocationCallback locationCallback;
    private Button setPermanent;
    boolean ZoomOnce=false;
    private Boolean SET_PERMANENT;
    private DatabaseReference currentUserDB;
    private CardView userInfoCard;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            userInfoCard = findViewById(R.id.userInfoCard);


            mAuth = FirebaseAuth.getInstance();
            setPermanent = findViewById(R.id.SetPermanent);
            userID = mAuth.getCurrentUser().getUid();
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getResources().getString(R.string.PleaseWait));
            progressDialog.show();
            storageReference = FirebaseStorage.getInstance().getReference("images");
            String userType = getIntent().getStringExtra("UserType");
            setUserdata(userType);


            if (mAuth.getCurrentUser().getEmail() != null)
                current_user_email = mAuth.getCurrentUser().getEmail();
            if (mAuth.getCurrentUser().getDisplayName() != null)
                current_user_dn = mAuth.getCurrentUser().getDisplayName();
            OneSignal.sendTag("User_ID", mAuth.getCurrentUser().getUid());
            OneSignal.sendTag("User_Type", userType);


            //userID="bapi";
        }

    }
    //JSONObject tags = new JSONObject();
    String tags;
    private void requestContactDetails() {
        Button requestContact=findViewById(R.id.Connect);
        requestContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndSendRequest();
            }
        });
    }
    String current_user_email="",current_user_dn="";
   // EditText UserMsg;
    private void createAndSendRequest(){

        storeReceiverUserDetails();

        Intent intent = new Intent(this,SingleChatActivity.class);
        intent.putExtra("from_user", to_user);
        startActivity(intent);

    }
    private void storeReceiverUserDetails(){

        String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        ChatUsers chatUsers = new ChatUsers();
        chatUsers.setChatUserID(to_user);
        chatUsers.setChatUserName(DN);
        chatUsers.setChatUserPhotoURL(PU);
        chatUsers.setChatUserLastMessageTime(timeStamp);

        SingleChatDB singleChatDB = SingleChatDB.Companion.getInstance(this);



        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                singleChatDB.getSingleChatDao().insertChatUser(chatUsers);
            }
        };

        AsyncTask.execute(runnable);

    }




    private void setUserdata(String userType){


        currentUserDB = FirebaseDatabase.getInstance().getReference("UI").child(userID);
        currentUserDB.child("UT").setValue(userType);
        if(mAuth.getCurrentUser().getDisplayName()!=null)
            currentUserDB.child("DN").setValue(mAuth.getCurrentUser().getDisplayName());
        currentUserDB.child("Em").setValue(mAuth.getCurrentUser().getEmail());
        currentUserDB.child("PN").setValue(mAuth.getCurrentUser().getPhoneNumber());
        if(mAuth.getCurrentUser().getPhotoUrl()!=null)
            currentUserDB.child("PU").setValue(mAuth.getCurrentUser().getPhotoUrl().toString());




    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        // //Log.i("Map Ready","I'm in mapready function");
        ConnectionDetector connectionDetector = new ConnectionDetector(this,"google.com");
        if (!connectionDetector.isInternetAvailable())
            Toast.makeText(this,"No internet connection found",Toast.LENGTH_LONG).show();
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);

        centrePosition=mMap.getCameraPosition().target;
        centerMarker = mMap.addMarker(new MarkerOptions().position(centrePosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocationmarker)));
        allUserDB = FirebaseDatabase.getInstance().getReference("UL");
        geoFire = new GeoFire(allUserDB);
        geoQuery=geoFire.queryAtLocation(new GeoLocation(centrePosition.latitude,centrePosition.longitude),1000);
        progressDialog.cancel();

        getAllUsersLocation();
        currentUserDB.child("P").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SET_PERMANENT = dataSnapshot.getValue(boolean.class);
                if(SET_PERMANENT==null)
                    SET_PERMANENT=false;
                setPermanentLocation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        requestContactDetails();


    }

    private void setPermanentLocation(){

        if(SET_PERMANENT) {
            setPermanent.setText(getResources().getString(R.string.LOCATIONFIXED));
            //Log.i("User_ID",userID);
            geoFire.getLocation(userID, new com.firebase.geofire.LocationCallback() {
                @Override
                public void onLocationResult(String key, GeoLocation location) {

                    LatLng latLng = new LatLng(location.latitude,location.longitude);

                    marker=mMap.addMarker(new MarkerOptions().position(latLng).title(getResources().getString(R.string.CurrentLoc)).icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocationmarker)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                    ZoomOnce=true;
                    getMapCentre();
                    centerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.centremarker));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MapsActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });
        }
        else{
            setPermanent.setText(getResources().getString(R.string.SETPERMANENT));
            centerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.mylocationmarker));
            getCurrentLocation();

        }


        setPermanent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SET_PERMANENT){
                    setPermanent.setText(getResources().getString(R.string.SETPERMANENT));

                    if(mMap!=null) {
                        ZoomOnce=false;
                        centerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.mylocationmarker));
                        getCurrentLocation();

                    }
                    SET_PERMANENT=false;
                    currentUserDB.child("P").setValue(SET_PERMANENT);
                    if(marker!=null)
                        marker.remove();

                }
                else{

                    setPermanent.setText(getResources().getString(R.string.LOCATIONFIXED));
                    geoFire.setLocation(userID, new GeoLocation(centrePosition.latitude, centrePosition.longitude), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            LatLng latLng = new LatLng(centrePosition.latitude,centrePosition.longitude);
                            marker=mMap.addMarker(new MarkerOptions().position(latLng).title(getResources().getString(R.string.CurrentLoc)).icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocationmarker)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            if(error!=null)
                                Toast.makeText(MapsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (fusedLocationProviderClient != null) {
                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    }
                    centerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.centremarker));
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);

                    fusedLocationProviderClient = null;
                    locationCallback=null;
                    locationRequest = null;
                    SET_PERMANENT=true;
                    currentUserDB.child("P").setValue(SET_PERMANENT);
                }

            }
        });

    }


    Marker centerMarker;
    private void getMapCentre(){

        ////Log.i("Centre","I'm in mapcenter function");

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                userInfoCard.setVisibility(View.GONE);
                centrePosition=mMap.getCameraPosition().target;
                geoQuery.setCenter(new GeoLocation(centrePosition.latitude,centrePosition.longitude));

                if(centerMarker==null)
                    centerMarker = mMap.addMarker(new MarkerOptions().position(centrePosition));
                else
                    centerMarker.setPosition(centrePosition);

            }
        });
    }

    List<Marker> markerList=new ArrayList<>();

    private void getAllUsersLocation(){

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                for(Marker markerIt:markerList){
                    if(markerIt.getTag().toString().equals(key) || key.equals(userID))
                       return;
                }

                if(!key.equals(userID)) {
                    LatLng userLoc = new LatLng(location.latitude, location.longitude);
                    readDBData(key,userLoc);

                    /*Marker mUserMarker = setMarkerBitmap(userLoc,key);
                    //mUserMarker.setTitle(DN);
                    mUserMarker.setTag(key);
                    markerList.add(mUserMarker);*/

                    ////Log.i("Marker Entered","Entered");
                }
               // //Log.i("Reference size",Integer.toString(referenceHashMap.size()));
            }

            @Override
            public void onKeyExited(String key) {
               ////Log.i("Marker Exited","Exited");
                for(Marker markerIt:markerList){
                    if(markerIt.getTag().toString().equals(key)) {
                        markerList.remove(markerIt);
                        markerIt.remove();
                        referenceHashMap.remove(key);
                        return;
                    }
                }

               // //Log.i("Reference size",Integer.toString(referenceHashMap.size()));

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

                for(Marker markerIt:markerList){
                    if(markerIt.getTag().equals(key) && !key.equals(userID)) {
                        markerIt.setPosition(new LatLng(location.latitude,location.longitude));
                    }
                }


            }

            @Override
            public void onGeoQueryReady() {


            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

                Toast.makeText(MapsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void setMarkerBitmap(LatLng userLoc,String UT,String DN,String IC,String key){

        Marker mUserMarker;

        if (UT.equals(getResources().getString(R.string.Hobbyist))) {

            mUserMarker = (mMap.addMarker(new MarkerOptions().position(userLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.hobbyist))));
        }
        else {

            if(IC.equals("0")) {
                mUserMarker = (mMap.addMarker(new MarkerOptions().position(userLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.seller2))));
            }
            else{
                mUserMarker = (mMap.addMarker(new MarkerOptions().position(userLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.items_shop))));
            }
        }
        mUserMarker.setTag(key);
        mUserMarker.setTitle(DN);
        markerList.add(mUserMarker);

        /*  for (Marker markerIt : markerList) {

              ////Log.i("Loop","inside for");
               if (markerIt.getTag().toString().equals(key)) {
                   if (UT.equals(getResources().getString(R.string.Hobbyist)))
                       markerIt.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.hobbyist));
                   else {

                       if(IC.equals("0")) {
                           markerIt.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.seller2));
                       }
                       else{
                           markerIt.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.items_shop));
                       }
                   }

                   markerIt.setTitle(DN);
                   return;
               }

           }*/


    }

   HashMap<String,DatabaseReference> referenceHashMap=new HashMap <>();
    private void readDBData(final String key, final LatLng userLoc){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UI").child(key);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String UT=(dataSnapshot.child("UT").getValue(String.class));
                String DN=(dataSnapshot.child("DN").getValue(String.class));
                String IC = (dataSnapshot.child("IC").getValue(String.class));

                if(IC==null){
                    IC = "0";
                }
                if(UT!=null){
                    setMarkerBitmap(userLoc,UT,DN,IC,key);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        referenceHashMap.put(key,ref);

    }

    private void updateLoc(LocationResult locationResult) {


        lastLocation = locationResult.getLastLocation();
       // //Log.i("Latitude", Double.toString(lastLocation.getLatitude()));
       // //Log.i("Longitude", Double.toString(lastLocation.getLongitude()));
        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

       // //Log.i("Update","Updating UL");

        if (marker != null)
            marker.setPosition(latLng);
        else
             
             geoFire.setLocation(userID, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if(error!=null)
                    Toast.makeText(MapsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getCurrentLocation(){

        ////Log.i("Location update","i'm in LA function");
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        locationRequest=new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback=new LocationCallback(){

            @Override
        public void onLocationResult(LocationResult locationResult) {


            lastLocation = locationResult.getLastLocation();
            updateLoc(locationResult);
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            if(!ZoomOnce) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                ZoomOnce=true;
            }
            super.onLocationResult(locationResult);
        }
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED) {


                fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
                getMapCentre();



            } else {
                ActivityCompat.requestPermissions(MapsActivity.this, permissions, 31);
            }

        }else {

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            mMap.setMyLocationEnabled(true);
            getMapCentre();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
                getMapCentre();
            }
        }else {
            Toast.makeText(this,getResources().getString(R.string.MapsPermRationale),Toast.LENGTH_LONG).show();
            finish();
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void removeLocationApiAndGeoListener(){

        //if(LISTENER_ENTERED)
        geoQuery.removeAllListeners();
        markerList.clear();
        referenceHashMap.clear();

        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }

        fusedLocationProviderClient = null;
        locationCallback=null;
        locationRequest = null;



    }


   /* @Override
    public void onConfigurationChanged(Configuration newConfig) {

        CONFIG_CHANGED=true;


        super.onConfigurationChanged(newConfig);
    }*/

    @Override
    protected void onStop() {

       /* if(CONFIG_CHANGED){
            Toast.makeText(MapsActivity.this, "CONFIG CHANGED", Toast.LENGTH_SHORT).show();
            CONFIG_CHANGED=false;
        }else {*/
            removeLocationApiAndGeoListener();
          /* if (geoFire != null && !SET_PERMANENT) {
                geoFire.removeLocation(userID, new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null)
                            Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
                currentUserDB.removeValue();
            }*/
       // }

        super.onStop();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String key;
        if(marker.getTag()!=null) {
            userInfoCard.setVisibility(View.VISIBLE);
            key = marker.getTag().toString();
            displayUserInfo(key);
            //Toast.makeText(this,key,Toast.LENGTH_SHORT).show();
        }
        return  true;
    }
    String send_email;
    String DN="";
    String PU="";
    String UT="";
    String  to_user="";
    private void displayUserInfo(final String key){

        final TextView dn=findViewById(R.id.DN);
        final ImageView dp=findViewById(R.id.DP);
        final LinearLayout visitButtonParent = findViewById(R.id.visit_button_parent);

        to_user = key;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UI").child(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {

                    DN = (dataSnapshot.child("DN").getValue(String.class));

                    send_email = (dataSnapshot.child("Em").getValue(String.class));

                    if (DN != null) {

                        dn.setText(DN);

                    }
                    UT=(dataSnapshot.child("UT").getValue(String.class));
                    PU = (dataSnapshot.child("PU").getValue(String.class));
                    if (PU != null) {
                        Glide.with(MapsActivity.this)
                                .load(Uri.parse(PU))
                                .apply(new RequestOptions()
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .error(R.drawable.profle))
                                .into(dp);
                    } else {
                        userInfoCard.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    Toast.makeText(MapsActivity.this,getResources().getString(R.string.ViewRefreshed),Toast.LENGTH_LONG).show();
                    dn.setText("");
                    Glide.with(MapsActivity.this)
                            .load(R.drawable.profle)
                            .apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .error(R.drawable.profle))
                            .into(dp);
                    userInfoCard.setVisibility(View.GONE);
                }
                ImageButton visitShop=findViewById(R.id.VisitButton);
                if(UT.equals("Seller")) {
                    visitButtonParent.setVisibility(View.VISIBLE);
                    visitShop.setVisibility(View.VISIBLE);
                    visitShop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MapsActivity.this, SellerActivity.class);
                            intent.putExtra("User", key);
                            intent.putExtra("DN", DN);
                            intent.putExtra("PU", PU);
                            startActivity(intent);


                        }
                    });
                }else{
                    visitButtonParent.setVisibility(View.GONE);
                    visitShop.setVisibility(View.GONE);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MapsActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }

        });

    }

    @Override
    protected void onResume() {
        if(mMap!=null){

            getAllUsersLocation();
        }
        super.onResume();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        userInfoCard.setVisibility(View.GONE);

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        userInfoCard.setVisibility(View.GONE);
    }



}


