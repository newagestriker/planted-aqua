package com.newage.aquapets.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.newage.aquapets.R;
import com.newage.aquapets.adapters.RecyclerViewAlbum;

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
