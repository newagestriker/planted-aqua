package com.newage.plantedaqua.activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.newage.plantedaqua.helpers.ConnectionDetector;
import com.newage.plantedaqua.models.GalleryInfo;
import com.newage.plantedaqua.R;
import com.newage.plantedaqua.adapters.UsersGalleryRecyclerAdapter;

import java.util.ArrayList;

public class UsersGalleryActivity extends AppCompatActivity {

    private RecyclerView usersGalleryRecyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<GalleryInfo> galleryInfoArrayList;
    private DatabaseReference galleryItemRef;
    private GalleryInfo temp = new GalleryInfo();
    private String User_ID="";
    private RelativeLayout progressBarRelativeLayout;
    private ProgressBar galleryItemLoadingProgressBar;
    private int UPDATE_ITEM_CODE = 27;
    private FirebaseAuth mAuth;
    private int pos;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mAuth.getCurrentUser()!=null)
            getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.addDetails){


                Intent intent = new Intent(this, GalleryActivity.class);
                intent.putExtra("UserID", User_ID);
                intent.putExtra("mode", "create");
                startActivity(intent);


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setTitle("Users Gallery");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_gallery);
        ConnectionDetector connectionDetector = new ConnectionDetector(this,"google.com");
        if (!connectionDetector.isInternetAvailable())
            Toast.makeText(this,"No internet connection found",Toast.LENGTH_LONG).show();
        mAuth = FirebaseAuth.getInstance();
        progressBarRelativeLayout = findViewById(R.id.ProgressBarRelativeLayout);
        galleryItemLoadingProgressBar = findViewById(R.id.GalleryItemsLoadingProgessBar);
        User_ID = getIntent().getStringExtra("UserID");
        pos = getIntent().getIntExtra("Position",-1);

        galleryItemRef = FirebaseDatabase.getInstance().getReference("GI");

        galleryInfoArrayList = new ArrayList<>();

        usersGalleryRecyclerView = findViewById(R.id.UsersGalleryRecyclerView);
        adapter = new UsersGalleryRecyclerAdapter(this, galleryInfoArrayList,User_ID, new UsersGalleryRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {


                if (galleryInfoArrayList.get(position).getFirebaseUserID().equals(User_ID)) {
                    Intent intent = new Intent(UsersGalleryActivity.this, GalleryActivity.class);
                    intent.putExtra("mode", "edit");
                    intent.putExtra("UserID", galleryInfoArrayList.get(position).getUserID());
                    startActivityForResult(intent, UPDATE_ITEM_CODE);
                }
            }
        });

        usersGalleryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersGalleryRecyclerView.setAdapter(adapter);

        getDataFromRealtimeDatabase();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == UPDATE_ITEM_CODE){

            Intent reloadIntent = new Intent(this,UsersGalleryActivity.class);
            reloadIntent.putExtra("UserID",User_ID);

            finish();



            startActivity(reloadIntent);
            //Toast.makeText(this,"RESULT OK",Toast.LENGTH_SHORT).show();

        }

    }

    private void getDataFromRealtimeDatabase() {


        Query galleryItemRefQuery = galleryItemRef.orderByChild("rating");



        galleryItemRefQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                temp = new GalleryInfo();
                temp = dataSnapshot.getValue(GalleryInfo.class);
                galleryInfoArrayList.add(temp);
                galleryItemLoadingProgressBar.setVisibility(View.GONE);
                progressBarRelativeLayout.setVisibility(View.GONE);
                adapter.notifyItemInserted(galleryInfoArrayList.size()-1);

                if(galleryInfoArrayList.size()-1==pos && pos!=-1){
                    usersGalleryRecyclerView.scrollToPosition(pos);
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

                Toast.makeText(UsersGalleryActivity.this,"Database Error Occurred. Please check your Internet Connection",Toast.LENGTH_LONG).show();

            }
        });



    }
}
