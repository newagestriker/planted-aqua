package com.newage.plantedaqua.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import com.google.android.material.snackbar.Snackbar;
import com.newage.plantedaqua.helpers.ExpenseDBHelper;
import com.newage.plantedaqua.helpers.MyDbHelper;
import com.newage.plantedaqua.R;
import com.newage.plantedaqua.adapters.RecyclerAdapterTankItems;
import com.newage.plantedaqua.models.TankItems;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


import java.util.ArrayList;

public class TankItemListActivity extends AppCompatActivity {

    private RecyclerView itemsRecyclerView;
    private RecyclerAdapterTankItems adapterTankItems;
    private RecyclerView.LayoutManager layoutManager;
    private int ITEM_DETAILS_CREATION=45;
    private int ITEMS_DETAILS_MODIFICATION=43;
    ArrayList<TankItems> tankItems=new ArrayList <>();
    TankItems tankItems1=new TankItems();
    private String aquariumID;
    private String category;
    boolean clicked=false;
    private ArrayList<String> TagList=new ArrayList <>();
    private TankItems temp;
    private Snackbar snackbar;
    LinearLayout tankItemsLinear;
    MyDbHelper myDbHelper;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       if(item.getItemId()==R.id.addDetails){
            Intent intent=new Intent(this, CreateTankItemsActivity.class);
            intent.putExtra("mode","creation");
            intent.putExtra("AquariumID",aquariumID);
            intent.putExtra("ItemCategory",category);
            startActivityForResult(intent,ITEM_DETAILS_CREATION);

        }
        if(item.getItemId()==R.id.Help){
            showItemsHelpDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showItemsHelpDialog() {

        ViewGroup viewGroup = findViewById(android.R.id.content);

        View view = getLayoutInflater().inflate(R.layout.items_help_dialog_view,viewGroup,false);
        AlertDialog.Builder helpDialogBuilder = new AlertDialog.Builder(this);
        helpDialogBuilder.setView(view);


        final Dialog helpDialog = helpDialogBuilder.create();
        helpDialog.show();

        Button okButton = view.findViewById(R.id.help_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            helpDialog.dismiss();
                                        }
                                    });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tank_item_list);

        String type;

        aquariumID=getIntent().getStringExtra("AquariumID");
        category=getIntent().getStringExtra("ItemCategory");
        String aquaName=getIntent().getStringExtra("AquariumName");

        switch (category){
            case "E":
                type=getResources().getString(R.string.Eq);
                break;
            case "Fr":
                type=getResources().getString(R.string.Fauna);
                break;
            case "Fl":
                type=getResources().getString(R.string.Flora);
                break;
            default:
                type="";
                break;

        }

        getSupportActionBar().setTitle(type+" : " +aquaName);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        myDbHelper=MyDbHelper.newInstance(this,aquariumID);
        Cursor c=myDbHelper.getDataTICondition("I_Category",category);
        switch (category) {
            case "E": {
                while (c.moveToNext()) {

                    tankItems1 = new TankItems();
                    tankItems1.setTag(c.getString(0));
                    tankItems1.setTxt1(c.getString(1));
                    tankItems1.setTxt2(Integer.toString(c.getInt(6)));
                    tankItems1.setTxt3(c.getString(7));
                    tankItems1.setTxt4(c.getString(8));
                    tankItems1.setQuickNote(c.getString(13));
                    tankItems1.setItemUri(c.getString(3));
                    tankItems.add(tankItems1);

                }
                break;
            }
            default:{
                while (c.moveToNext()) {

                    tankItems1 = new TankItems();
                    tankItems1.setTag(c.getString(0));
                    tankItems1.setTxt1(c.getString(1));
                    tankItems1.setTxt2(Integer.toString(c.getInt(6)));
                    tankItems1.setTxt3(c.getString(13));
                    tankItems1.setTxt4(c.getString(7));
                    tankItems1.setQuickNote(c.getString(12));
                    tankItems1.setItemUri(c.getString(3));
                    tankItems.add(tankItems1);

                }

            }
        }
        c.close();

        tankItemsLinear=findViewById(R.id.TankItemsLinear);

        itemsRecyclerView = findViewById(R.id.ItemsRecyclerView);
        layoutManager=new LinearLayoutManager(this);
        itemsRecyclerView.setLayoutManager(layoutManager);
        adapterTankItems=new RecyclerAdapterTankItems(tankItems, category,this, new RecyclerAdapterTankItems.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, String uri) {

                quickNote(position);
            }

            @Override
            public void onLongClick(View view, int position) {

                Intent intent=new Intent(TankItemListActivity.this,CreateTankItemsActivity.class);
                intent.putExtra("mode","modification");
                intent.putExtra("ItemID",tankItems.get(position).getTag());
                intent.putExtra("AquariumID",aquariumID);
                intent.putExtra("ItemCategory",category);
                intent.putExtra("position",position);
                startActivityForResult(intent,ITEMS_DETAILS_MODIFICATION);

            }
        });
        itemsRecyclerView.setAdapter(adapterTankItems);


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
                    temp=new TankItems();
                    temp=tankItems.get(position);
                    TagList.add(temp.getTag());
                    tankItems.remove(position);
                    adapterTankItems.notifyItemRemoved(position);
                    snackbar = Snackbar
                            .make(tankItemsLinear, "Record deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    tankItems.add(position,temp);
                                    clicked=true;
                                    TagList.remove(TagList.size()-1);
                                    adapterTankItems.notifyItemInserted(position);
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
                                            myDbHelper.deleteItemTI("I_ID", Tag);
                                            ExpenseDBHelper expenseDBHelper = ExpenseDBHelper.getInstance(TankItemListActivity.this);
                                            expenseDBHelper.deleteExpense("ItemID",Tag);
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
        itemTouchHelper.attachToRecyclerView(itemsRecyclerView);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK ) {
            if(requestCode==ITEM_DETAILS_CREATION) {
                tankItems1 = new TankItems();
                tankItems1.setItemUri(data.getStringExtra("ImageUri"));
                tankItems1.setTxt1(data.getStringExtra("Txt1"));
                tankItems1.setTxt2(data.getStringExtra("Txt2"));
                tankItems1.setTxt3(data.getStringExtra("Txt3"));
                tankItems1.setTxt4(data.getStringExtra("Txt4"));
                tankItems1.setTag(data.getStringExtra("Tag"));
                tankItems1.setQuickNote(data.getStringExtra("QuickNote"));
                tankItems.add(tankItems1);


                adapterTankItems.notifyItemInserted(tankItems.size() - 1);
            }
            if(requestCode==ITEMS_DETAILS_MODIFICATION){

                int position=data.getIntExtra("Position",-1);
                tankItems.get(position).setItemUri(data.getStringExtra("ImageUri"));
                tankItems.get(position).setTxt1(data.getStringExtra("Txt1"));
                tankItems.get(position).setTxt2(data.getStringExtra("Txt2"));
                tankItems.get(position).setTxt3(data.getStringExtra("Txt3"));
                tankItems.get(position).setTxt4(data.getStringExtra("Txt4"));
                tankItems.get(position).setTag(data.getStringExtra("Tag"));
                tankItems.get(position).setQuickNote(data.getStringExtra("QuickNote"));
                adapterTankItems.notifyItemChanged(position);

                //System.out.println(tankItems.get(position).getTxt3());

            }
        }


    }

    private void quickNote(final int position){

        String preNote;
        final String id=tankItems.get(position).getTag();

        final View view=getLayoutInflater().inflate(R.layout.quicknote_dialog,null);
        final EditText quickNote=view.findViewById(R.id.enterQuickNote);
        quickNote.setCursorVisible(false);
        quickNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quickNote.setCursorVisible(true);
            }
        });
        Cursor c=myDbHelper.getDataTICondition("I_ID",id);
        c.moveToNext();
        preNote=c.getString(13);
        c.close();
        quickNote.setText(preNote);

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(view)
                .setTitle(R.string.QN)
                .setIcon(R.drawable.plantedaqua)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String qNote=quickNote.getText().toString();
                        tankItems.get(position).setQuickNote(qNote);
                        adapterTankItems.notifyItemChanged(position);
                        myDbHelper.updateItemTISingleItem(id,"I_Remarks",qNote);

                    }
                })
                .setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();



    }
}
