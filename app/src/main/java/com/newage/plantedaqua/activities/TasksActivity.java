package com.newage.plantedaqua.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.newage.plantedaqua.helpers.MyDbHelper;
import com.newage.plantedaqua.R;
import com.newage.plantedaqua.adapters.RecyclerAdapter;
import com.newage.plantedaqua.helpers.TankDBHelper;
import com.newage.plantedaqua.models.TaskItems;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;


public class TasksActivity extends AppCompatActivity {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    TabLayout tabLayout;
    static CoordinatorLayout coordinatorLayout;
    private static final int  ALARM_ACTIVITY = 33;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        String aquaName=getIntent().getStringExtra("AquariumName");

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.Task)+" : "+aquaName);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        coordinatorLayout=findViewById(R.id.main_content);
        /**
         * The {@link PagerAdapter} that will provide
         * fragments for each of the sections. We use a
         * {@link FragmentPagerAdapter} derivative, which will keep every
         * loaded fragment in memory. If this becomes too memory intensive, it
         * may be best to switch to a
         * {@link FragmentStatePagerAdapter}.
         */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        RecyclerView recyclerView;
        RecyclerView.Adapter adapter;
        RecyclerView.LayoutManager layoutManager;
        private ArrayList<TaskItems> taskItems=new ArrayList <>();
        private TaskItems ti=new TaskItems();

        private int SET_ALARM=29;

        @Nullable
        @Override
        public View getView() {
            return super.getView();
            
        }

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args=new Bundle();
            args.putInt(ARG_SECTION_NUMBER,sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
           View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
           Bundle args=getArguments();
            assert args != null;
            int position=args.getInt(ARG_SECTION_NUMBER);
           String AlarmType;
            switch (position){
                case 0: AlarmType=getResources().getString(R.string.page_text_1);
                    break;
                case 1: AlarmType=getResources().getString(R.string.page_text_2);
                    break;
                case 2: AlarmType=getResources().getString(R.string.page_text_3);
                    break;
                case 3: AlarmType=getResources().getString(R.string.page_text_4);
                    break;
                case 4: AlarmType=getResources().getString(R.string.page_text_5);
                    break;
                case 5: AlarmType=getResources().getString(R.string.page_text_6);
                    break;
                default:AlarmType="";
            }

            PageSelected(AlarmType,rootView);
           return rootView;
        }
        private void PageSelected(final String AlarmType, View rootView){

            recyclerView= rootView.findViewById(R.id.menuRecyclerView);
            layoutManager=new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            taskItems=new ArrayList <>();


            Bundle bundle=getActivity().getIntent().getExtras();

            final String aquaID= bundle.getString("AquariumID");




            MyDbHelper mydbhelper= MyDbHelper.newInstance(getActivity(),aquaID);

            SQLiteDatabase db=mydbhelper.getWritableDatabase();
            Cursor c=mydbhelper.getDataANCondition(db,"Category",AlarmType);


            if(c.moveToFirst()) {
                do {
                    ti=new TaskItems();
                    ti.setAlarmName(c.getString(0));
                    ti.setAlarmTime(c.getString(5));
                    ti.setAlarmDays(c.getString(4));
                    taskItems.add(ti);
                } while (c.moveToNext());

            }
            c.close();

            adapter=new RecyclerAdapter(taskItems, aquaID, AlarmType, new RecyclerAdapter.OnItemClickListener() {
                @Override
                public void onClick(View view, int position) {

                    Context ctx=view.getContext();


                    if (view.getTag()!=null) {

                        delete(position,ctx,AlarmType,aquaID);

                    } else {

                        Intent i1 = new Intent(ctx, SetAlarmActivity.class);
                        i1.putExtra("AquariumID", aquaID);
                        i1.putExtra("AlarmType",AlarmType);
                        i1.putExtra("AlarmName",taskItems.get(position).getAlarmName());
                        i1.putExtra("Position",position);
                        startActivityForResult(i1,SET_ALARM);
                    }

                }
            });

            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            FloatingActionButton floatingActionButton=rootView.findViewById(R.id.addDosageSchedule);
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater=LayoutInflater.from(getActivity());
                    final View view= inflater.inflate(R.layout.enternutrientdialog,null);
                    builder.setView(view)
                            .setMessage("What task Items alarm you want to set")
                            .setTitle("Action")
                            .setIcon(R.drawable.plantedaqua)
                            .setNeutralButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    addAlarmButton(view,aquaID,AlarmType);

                                }
                            });
                    builder.show();

                }
            });
        }
        public void delete(int pos,Context ctx,String AlarmType,String aquariumID){

            String tsk=taskItems.get(pos).getAlarmName();

            taskItems.remove(pos);
            adapter.notifyItemRemoved(pos);
            // notifyItemChanged(pos,nutrient.size());

            MyDbHelper alarmdbhelper = MyDbHelper.newInstance(ctx,aquariumID);


            Cursor c= alarmdbhelper.getData2Condition("Alarm_Name",tsk,"Alarm_Type",AlarmType);

            while(c.moveToNext()) {

                AlarmManager alarmManager=(AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                Intent inn= new Intent(ctx, RamizAlarm.class);
                PendingIntent pi = PendingIntent.getBroadcast(ctx, c.getInt(3), inn, PendingIntent.FLAG_NO_CREATE);
                if(pi==null){
                    //System.out.println("Ramiz: No such pending intent exists");
                }
                else{
                    // System.out.println("Ramiz: Pending intent exists;");
                    pi = PendingIntent.getBroadcast(ctx, c.getInt(3), inn, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pi);

                }
            }
            c.close();

            //region RESET DEFAULT ALARM BITS
            TankDBHelper tankDBHelper=TankDBHelper.newInstance(ctx);
            Cursor cursor=tankDBHelper.getDataCondition("AquariumID",aquariumID);
            cursor.moveToFirst();
            String defaultAlarm = cursor.getString(22);
            cursor.close();

            int defaultAlarmInt = Integer.parseInt(defaultAlarm);


                if (AlarmType.equals("Dosing")) {
                    if (tsk.equals("Macro")) {

                        defaultAlarmInt -=100;

                    } else if (tsk.equals("Micro")) {

                        defaultAlarmInt -=10;
                    }
                }
                if (AlarmType.equals("Water Change") && tsk.equals("Weekly")) {

                    defaultAlarmInt -= 1;
                }


                tankDBHelper.updateSingleItem("AquariumID",aquariumID,"DefaultAlarm",Integer.toString(defaultAlarmInt));
            //endregion


            //region DELETE TABLE
            alarmdbhelper.deleteItem("Alarm_Name",tsk,"Alarm_Type",AlarmType);
            alarmdbhelper.deleteItemAN("AlarmName",tsk,"Category",AlarmType);
            //endregion



        }

        private void addAlarmButton(View view,final String aquariumID,final String AlarmType){

            boolean itemExist=false;
            final EditText editText = view.findViewById(R.id.enterNutrient);

            if(!editText.getText().toString().isEmpty()) {

                for(int i=0;i<taskItems.size();i++)
                {
                    if(taskItems.get(i).getAlarmName().equals(editText.getText().toString())){
                        itemExist=true;
                        Snackbar.make(coordinatorLayout,getResources().getString(R.string.ItemExist),Snackbar.LENGTH_LONG).show();
                        break;
                    }
                }

                if(!itemExist) {

                    Runnable runnable=new Runnable() {
                        @Override
                        public void run() {
                            int Slot;
                            String noInfoText = getResources().getString(R.string.NoInfo);
                            MyDbHelper mydbhelper;
                            SQLiteDatabase db;
                            Cursor c;
                            boolean equal=true;

                            TankDBHelper tankDBHelper=TankDBHelper.newInstance(getActivity());
                            SQLiteDatabase TDb=tankDBHelper.getWritableDatabase();
                            Cursor cT=tankDBHelper.getData(TDb);
                            Slot=3;
                            ArrayList<Integer> tableSlots=new ArrayList <>();
                            while (cT.moveToNext()) {
                                mydbhelper = MyDbHelper.newInstance(getActivity(), cT.getString(1));
                                db = mydbhelper.getWritableDatabase();
                                c = mydbhelper.getDataAN(db);
                                while (c.moveToNext()) {
                                    tableSlots.add(c.getInt(3));

                                }
                                c.close();
                            }

                            cT.close();
                            while(equal && tableSlots.size()>0){
                                for(int i=0;i<tableSlots.size();i++){
                                    //System.out.println("Table Slots : "+tableSlots.get(i)+" Check Slot : "+ Slot);
                                    if(tableSlots.get(i)==Slot){
                                       // System.out.println("Equality found so break!!");
                                        Slot++;
                                        equal=true;
                                        break;
                                    }
                                    equal=false;
                                }
                            }

                           // System.out.println("Chosen Slot "+Slot);
                            mydbhelper=MyDbHelper.newInstance(getActivity(), aquariumID);
                            db=mydbhelper.getWritableDatabase();
                            mydbhelper.addDataAN(db, editText.getText().toString(), AlarmType, noInfoText,Slot,"","");
                            Intent alarmIntent = new Intent(getActivity(),SetAlarmActivity.class);
                            alarmIntent.putExtra("AlarmType",AlarmType);
                            alarmIntent.putExtra("AquariumID",aquariumID);
                            alarmIntent.putExtra("AlarmName",editText.getText().toString());
                            alarmIntent.putExtra("Position",taskItems.size()-1);
                            startActivityForResult(alarmIntent,ALARM_ACTIVITY);


                        }
                    };
                    AsyncTask.execute(runnable);

                    ti=new TaskItems();
                    ti.setAlarmName(editText.getText().toString());
                    taskItems.add(ti);

                }
            }

        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(resultCode==RESULT_OK) {
                int position = data.getIntExtra("Position", -1);
               // System.out.println("Posiiton "+position);
                taskItems.get(position).setAlarmDays( data.getStringExtra("AlarmDays"));
                taskItems.get(position).setAlarmTime( data.getStringExtra("AlarmDate"));
                adapter.notifyItemChanged(position);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Context context;
         SectionsPagerAdapter(Context context,FragmentManager fm) {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.context=context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return PlaceholderFragment.newInstance(position);

        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 6;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0:
                    return context.getString(R.string.page_text_1);
                case 1:
                    return context.getString(R.string.page_text_2);
                case 2:
                    return context.getString(R.string.page_text_3);
                case 3:
                    return context.getString(R.string.page_text_4);
                case 4:
                    return context.getString(R.string.page_text_5);
                case 5:
                    return context.getString(R.string.page_text_6);
                    default:
                        return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

    }
}
