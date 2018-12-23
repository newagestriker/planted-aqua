package com.newage.plantedaqua;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class TankItemsAlbumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        ArrayList<String> tankUri=new ArrayList <>();

        RecyclerView album=findViewById(R.id.AlbumGridRecyclerView);
        RecyclerView.LayoutManager layoutManager= new GridLayoutManager(this,3);
        RecyclerView.Adapter adapter=new RecyclerViewAlbum(tankUri, this, new RecyclerViewAlbum.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });
    }
}
