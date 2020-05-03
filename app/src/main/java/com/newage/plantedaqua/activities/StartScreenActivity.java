package com.newage.plantedaqua.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.newage.plantedaqua.R;
import com.newage.plantedaqua.adapters.RecyclerAdapterPicsInfo;
import com.newage.plantedaqua.helpers.TankDBHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class StartScreenActivity extends AppCompatActivity {

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.addDetails){
           Intent intent=new Intent(this, CreateTankActivity.class);
           intent.putExtra("mode","creation");
           startActivityForResult(intent,TANK_DETAILS_CREATION);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        getSupportActionBar().setTitle(R.string.StartTitle);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        insertTankRow();


    }

    public void insertTankRow(){

        tankDBHelper = TankDBHelper.newInstance(this);
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
        adapter=new RecyclerAdapterPicsInfo(tanklabels, this,R.layout.tankprogressrow, new RecyclerAdapterPicsInfo.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, String uri) {



                Intent i = new Intent(view.getContext(),OptionsActivity.class);
                i.putExtra("AquariumID",tanklabels.get(position).getTag());
               // System.out.println("Aquarium ID :" + tanklabels.get(position).getTag());
                startActivity(i);

            }

            @Override
            public void onLongClick(View view, int position) {

                Intent intent=new Intent(StartScreenActivity.this,CreateTankActivity.class);
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
                                            //System.out.println("Tag : "+Tag);
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

        if(resultCode==Activity.RESULT_OK ) {
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
