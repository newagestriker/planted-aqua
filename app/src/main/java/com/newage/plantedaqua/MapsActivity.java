package com.newage.plantedaqua;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import android.support.v7.widget.CardView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.onesignal.OneSignal;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

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
    private TinyDB LocationStatusInput;
    DatabaseReference currentUserDB;
    CardView userInfoCard;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        userInfoCard=findViewById(R.id.userInfoCard);

        mAuth=FirebaseAuth.getInstance();
        setPermanent=findViewById(R.id.SetPermanent);
        userID=mAuth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.PleaseWait));
        progressDialog.show();
        LocationStatusInput=new TinyDB(this);
        SET_PERMANENT=LocationStatusInput.getBoolean("SET_PERMANENT");
        storageReference= FirebaseStorage.getInstance().getReference("images");
        String userType=getIntent().getStringExtra("UserType");
        setUserdata(userType);


        if(mAuth.getCurrentUser().getEmail()!=null)
            current_user_email=mAuth.getCurrentUser().getEmail();
        if(mAuth.getCurrentUser().getDisplayName()!=null)
            current_user_dn=mAuth.getCurrentUser().getDisplayName();
        OneSignal.sendTag("User_ID", current_user_email);
        OneSignal.sendTag("User_Type", userType);


        //userID="bapi";

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
    String msg="",current_user_email="",current_user_dn="";
    EditText UserMsg;
    private void createAndSendRequest(){
        View view=getLayoutInflater().inflate(R.layout.connect_layout,null);
        UserMsg=view.findViewById(R.id.userMessage);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.Send), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        msg="Message from "+current_user_dn+" : "+ UserMsg.getText().toString();
                        sendRequest();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void sendRequest(){

        final String photoUrl;
        if(mAuth.getCurrentUser().getPhotoUrl()!=null)
            photoUrl=mAuth.getCurrentUser().getPhotoUrl().toString();
        else
            photoUrl="na";
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);


                    //This is a Simple Logic to Send Notification different Device Programmatically....

                    try {
                        String jsonResponse;
                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic ZjY1NWJjMmYtYzEyZS00ZTZjLWIwOTktYjA1OGM1YzZlYjNh");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"75961582-4d0f-4c61-bd6e-7fd712577fbc\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"msg\": \""+msg+"\",\"PU\": \""+photoUrl+"\",\"send_email\":\""+current_user_email+"\"},"
                                + "\"contents\": {\"en\": \""+msg+"\"}"
                                + "}";


                       // System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        //System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        //System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        Toast.makeText(MapsActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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



      /*  StorageReference dpReference;
        if(mAuth.getCurrentUser().getDisplayName()!=null && mAuth.getCurrentUser().getPhotoUrl()!=null ) {
            dpReference = storageReference.child(userID + "/" + mAuth.getCurrentUser().getDisplayName() + ".jgp");
            dpReference.putFile(Uri.parse(mAuth.getCurrentUser().getPhotoUrl().toString())).addOnSuccessListener(new OnSuccessListener <UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(MapsActivity.this,"Success",Toast.LENGTH_SHORT).show();

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MapsActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
        }*/
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        // //Log.i("Map Ready","I'm in mapready function");
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
                    LocationStatusInput.putBoolean("SET_PERMANENT",SET_PERMANENT);
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
                    LocationStatusInput.putBoolean("SET_PERMANENT",SET_PERMANENT);
                }

            }
        });

        requestContactDetails();


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
    private void setMarkerBitmap(LatLng userLoc,String UT,String DN,String key){

          for (Marker markerIt : markerList) {

              ////Log.i("Loop","inside for");
               if (markerIt.getTag().toString().equals(key)) {
                   if (UT.equals(getResources().getString(R.string.Hobbyist)))
                       markerIt.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.hobbyist));
                   else
                       markerIt.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.seller2));
                   markerIt.setTitle(DN);
                   return;
               }

           }

            Marker mUserMarker;

            if (UT.equals(getResources().getString(R.string.Hobbyist)))

                mUserMarker = (mMap.addMarker(new MarkerOptions().position(userLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.hobbyist))));
            else
                mUserMarker = (mMap.addMarker(new MarkerOptions().position(userLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.seller2))));
            mUserMarker.setTag(key);
            mUserMarker.setTitle(DN);
            markerList.add(mUserMarker);
    }

   HashMap<String,DatabaseReference> referenceHashMap=new HashMap <>();
    private void readDBData(final String key, final LatLng userLoc){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UI").child(key);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String UT=(dataSnapshot.child("UT").getValue(String.class));
                String DN=(dataSnapshot.child("DN").getValue(String.class));

                if(UT!=null){
                    setMarkerBitmap(userLoc,UT,DN,key);
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
    private void displayUserInfo(final String key){

        final TextView dn=findViewById(R.id.DN);
        final ImageView dp=findViewById(R.id.DP);

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


