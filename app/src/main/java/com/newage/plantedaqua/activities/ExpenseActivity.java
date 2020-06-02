package com.newage.plantedaqua.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.graphics.Color;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.newage.plantedaqua.helpers.ExpenseDBHelper;
import com.newage.plantedaqua.models.ExpenseItems;
import com.newage.plantedaqua.adapters.ExpenseTableRecyclerView;
import com.newage.plantedaqua.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class ExpenseActivity extends AppCompatActivity {


    private ExpenseItems expenseItems;
    private ArrayList<ExpenseItems> expenseItemsArrayList;
    private RecyclerView expenseRecyclerView;
    private float expense;
    private TextView dateInput;
    private EditText tankNameInput;
    private EditText expenseItemInput;
    private EditText itemQuantityInput;
    private EditText itemPriceInput;
    private TextView totalExpense;
    private ExpenseDBHelper expenseDBHelper;
    private String startDate="",endDate="";
    private boolean startDateSet =false;
    private boolean endDateSet = false;
    TextView showStartDate,showEndDate;




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.expense_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        if (item.getItemId() == R.id.AddExpense) {

            refresh = true;

            View view = getLayoutInflater().inflate(R.layout.add_expense_dialog,null);

            ImageView calendarImage = view.findViewById(R.id.PickDateExpenseDialog);
            dateInput = view.findViewById(R.id.ExpenseDateInput);
            expenseItemInput = view.findViewById(R.id.ExpenseItemNameInput);
            tankNameInput= view.findViewById(R.id.ExpenseTankNameInput);
            itemQuantityInput = view.findViewById(R.id.ExpenseQuantityInput);
            itemPriceInput = view.findViewById(R.id.ExpensePricePerUnitInput);

            calendarImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickDate();
                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view)
                    .setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    })
                    .setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            refreshAdapterBeforeAdding();

                            getExpenseInput();

                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();



        }

        else{

            AlertDialog.Builder clearExpenseAlert = new AlertDialog.Builder(this);
            clearExpenseAlert.setTitle(getString(R.string.Attention))
                    .setMessage(getString(R.string.delete_all_expense))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            expenseDBHelper.deleteAllExpense();
                            expenseItemsArrayList.clear();
                            barChart.invalidate();
                            adapter.notifyDataSetChanged();
                            totalExpense.setText(getResources().getString(R.string._000));

                        }
                    })
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();


        }

        return super.onOptionsItemSelected(item);

    }

    private void getExpenseInput(){


        //TankDBHelper tankDBHelper = TankDBHelper.newInstance(this.getApplicationContext());


        String aquaname = tankNameInput.getText().toString();

        //String messageText="";
        String ID ="";

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.getDefault()).format(new Date());
            int rnd = new Random().nextInt(10000);
            ID = timeStamp + "_" + rnd;



        String itemName = expenseItemInput.getText().toString();
        int quantity = Integer.parseInt(TextUtils.isEmpty(itemQuantityInput.getText().toString())?"1":itemQuantityInput.getText().toString());
        float numericPrice = Float.parseFloat(TextUtils.isEmpty(itemPriceInput.getText().toString())?"0.0":itemPriceInput.getText().toString().replace(",","."));

        expenseItems = new ExpenseItems();
        expenseItems.setExpenseTotalPrice(quantity*numericPrice);
        expenseItems.setExpensePrice(numericPrice);
        expenseItems.setExpenseItemName(itemName);
        expenseItems.setExpenseTankName(aquaname);
        expenseItems.setExpenseQuantity(quantity);
        expenseItems.setExpenseYear(yr);
        expenseItems.setExpenseMonth(mnth);
        expenseItems.setExpenseDay(dy);
        expenseItems.setExpenseDate(dt);
        expenseItems.setItemID(ID);




        expenseDBHelper.addDataExpense(expenseDBHelper.getWritableDatabase(),ID,aquaname,itemName,dy,mnth,yr,dt,0L,quantity,numericPrice,quantity * numericPrice,"EXTRA",ID);
        expense = expenseDBHelper.getExpenseperMonth(-1);
        totalExpense.setText(String.format(Locale.getDefault(),"%.2f",expense));
        expenseItemsArrayList.add(expenseItems);
        adapter.notifyItemInserted(expenseItemsArrayList.size()-1);
        barChart.invalidate();
    }

    private int dy=0,mnth = 0,yr = 0;
    private String dt="0000-00-00";

    private void pickDate(){

        final Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(ExpenseActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dt = formatDate(year)+"-"+formatDate(month+1)+"-"+formatDate(dayOfMonth);

                dateInput.setText(dt);


                    dy = dayOfMonth;
                    mnth = month+1;
                    yr = year;


            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private String formatDate(int datePart){

        return datePart<10?"0"+datePart:Integer.toString(datePart);
    }

    private String dateString;

    private String pickDate(final TextView setDateTextView,final Boolean strtDate){

        final Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(ExpenseActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateString = formatDate(year)+"-"+formatDate(month+1)+"-"+formatDate(dayOfMonth);

                if(strtDate){

                    startDate=dateString;
                    startDateSet = true;
                }
                else{

                    endDate=dateString;
                    endDateSet = true;
                }

                if(startDateSet&&endDateSet){filterBydate.setVisibility(View.VISIBLE);}


                setDateTextView.setText(dateString);
                setDateTextView.setVisibility(View.VISIBLE);




            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();



        return dateString;

    }
    private ImageView filterBydate,clearFilters;
    private String monthFromIntent;

    private RecyclerView.Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        clearFilters = findViewById(R.id.ClearFilters);

        totalExpense = findViewById(R.id.TotalExpense);

        expenseDBHelper = ExpenseDBHelper.getInstance(this.getApplicationContext());

        ImageView pickStartDateImage = findViewById(R.id.SelectStartDate);
        ImageView pickEndDateImage = findViewById(R.id.SelectEndDate);
        filterBydate = findViewById(R.id.FilterByDate);
        filterBydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilters.setVisibility(View.VISIBLE);
                showFilteredValues();

            }


        });


        showStartDate = findViewById(R.id.ShowStartDate);
        showEndDate = findViewById(R.id.ShowEndDate);

        clearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                refreshAdapterBeforeAdding();
            }
        });


        pickStartDateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickDate(showStartDate,true);


            }
        });

        pickEndDateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickDate(showEndDate,false);


            }
        });


        expenseRecyclerView = findViewById(R.id.ExpenseRecyclerView);

        setInitData();

        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ExpenseTableRecyclerView(expenseItemsArrayList, new ExpenseTableRecyclerView.OnExpenseItemClickListener() {
            @Override
            public void onClick(View view, int position) {



                if(view.getId()==R.id.DeleteExpenseItemButton){
                    expenseDBHelper.deleteExpense("ItemID",expenseItemsArrayList.get(position).getItemID());
                    expenseItemsArrayList.remove(position);
                    adapter.notifyItemRemoved(position);
                    barChart.invalidate();
                    expense = expenseDBHelper.getExpenseperMonth(-1);
                    totalExpense.setText(String.format(Locale.getDefault(),"%.2f",expense));
                }
                else{
                    showDeleteButton=!showDeleteButton;
                    refreshAdapterBeforeAdding();
                }

            }
        });

        expenseRecyclerView.setAdapter(adapter);

        displayCharts();

    }

    private void showFilteredValues() {

        Cursor c = expenseDBHelper.getFilteredByDataExpense(startDate,endDate);

        expenseItemsArrayList.clear();
        adapter.notifyDataSetChanged();
        ExpenseItems expenseItem;


        if(c!=null){


            if(c.moveToFirst()){

                do{
                    //expense_table(id integer primary key autoincrement,AquariumID text,TankName text,ItemName text,Day integer,Month integer,Year integer,ShownDate text,TimeInMIllis integer,Quantity integer,Price real,TotalPrice real,AdditionalInfo text)
                    expenseItem = new ExpenseItems();
                    expenseItem.setItemID(c.getString(1));
                    expenseItem.setExpenseTankName(c.getString(2));
                    expenseItem.setExpenseItemName(c.getString(3));
                    expenseItem.setExpenseDate(c.getString(7));
                    expenseItem.setExpenseDay(c.getInt(4));
                    expenseItem.setExpenseMonth(c.getInt(5));
                    expenseItem.setExpenseYear(c.getInt(6));
                    expenseItem.setExpenseQuantity(c.getInt(9));
                    expenseItem.setExpensePrice(c.getFloat(10));
                    expenseItem.setExpenseTotalPrice(c.getFloat(11));
                    expenseItem.setShowDeleteButton(showDeleteButton);



                    expenseItemsArrayList.add(expenseItem);
                    adapter.notifyItemInserted(expenseItemsArrayList.size()-1);

                }while (c.moveToNext());
            }
            c.close();

            expense=expenseDBHelper.getFilteredNetExpense(startDate,endDate);
            totalExpense.setText(String.format(Locale.getDefault(),"%.2f",expense));
            c.close();


        }

        barChart.invalidate();






    }

    private boolean refresh = false;
    private boolean showDeleteButton= false;

    private void setInitData(){

        int month;

        if(refresh){
            month=-1;
        }
        else {
            month = getIntent().getIntExtra("PREVIOUS_MONTH", -1);
        }
        Cursor c;

        if(month!=-1) {
            c = expenseDBHelper.getExpenseDataPerMonth(month);
        }
        else{

            c = expenseDBHelper.getDataExpense(expenseDBHelper.getReadableDatabase());
        }

        startDateSet = false;
        endDateSet = false;
        filterBydate.setVisibility(View.GONE);
        showEndDate.setVisibility(View.GONE);
        showStartDate.setVisibility(View.GONE);
        clearFilters.setVisibility(View.GONE);



        expenseItemsArrayList = new ArrayList<>();
        ExpenseItems expenseItem;

        if(c!=null){

            if(c.moveToFirst()){
                do{
                    //expense_table(id integer primary key autoincrement,AquariumID text,TankName text,ItemName text,Day integer,Month integer,Year integer,ShownDate text,TimeInMIllis integer,Quantity integer,Price real,TotalPrice real,AdditionalInfo text)
                    expenseItem = new ExpenseItems();
                    expenseItem.setItemID(c.getString(1));
                    expenseItem.setExpenseTankName(c.getString(2));
                    expenseItem.setExpenseItemName(c.getString(3));
                    expenseItem.setExpenseDate(c.getString(7));
                    expenseItem.setExpenseDay(c.getInt(4));
                    expenseItem.setExpenseMonth(c.getInt(5));
                    expenseItem.setExpenseYear(c.getInt(6));
                    expenseItem.setExpenseQuantity(c.getInt(9));
                    expenseItem.setExpensePrice(c.getFloat(10));
                    expenseItem.setExpenseTotalPrice(c.getFloat(11));
                    expenseItem.setCategory(c.getString(12));
                    expenseItem.setShowDeleteButton(showDeleteButton);

                    expenseItemsArrayList.add(expenseItem);


                }while (c.moveToNext());
            }

            expense = expenseDBHelper.getExpenseperMonth(month);
            totalExpense.setText(String.format(Locale.getDefault(),"%.2f",expense));
            c.close();
        }


        
    }


    private void refreshAdapterBeforeAdding(){


        startDateSet = false;
        endDateSet = false;
        filterBydate.setVisibility(View.GONE);
        showEndDate.setVisibility(View.GONE);
        showStartDate.setVisibility(View.GONE);
        clearFilters.setVisibility(View.GONE);



        expenseItemsArrayList.clear();
        adapter.notifyDataSetChanged();
        Cursor c = expenseDBHelper.getDataExpense(expenseDBHelper.getReadableDatabase());
        ExpenseItems expenseItem;

        if(c!=null){

            if(c.moveToFirst()){
                do{
                    //expense_table(id integer primary key autoincrement,AquariumID text,TankName text,ItemName text,Day integer,Month integer,Year integer,ShownDate text,TimeInMIllis integer,Quantity integer,Price real,TotalPrice real,AdditionalInfo text)
                    expenseItem = new ExpenseItems();
                    expenseItem.setItemID(c.getString(1));
                    expenseItem.setExpenseTankName(c.getString(2));
                    expenseItem.setExpenseItemName(c.getString(3));
                    expenseItem.setExpenseDate(c.getString(7));
                    expenseItem.setExpenseDay(c.getInt(4));
                    expenseItem.setExpenseMonth(c.getInt(5));
                    expenseItem.setExpenseYear(c.getInt(6));
                    expenseItem.setExpenseQuantity(c.getInt(9));
                    expenseItem.setExpensePrice(c.getFloat(10));
                    expenseItem.setExpenseTotalPrice(c.getFloat(11));
                    expenseItem.setCategory(c.getString(12));
                    expenseItem.setShowDeleteButton(showDeleteButton);

                    expenseItemsArrayList.add(expenseItem);
                    adapter.notifyItemInserted(expenseItemsArrayList.size()-1);


                }while (c.moveToNext());
            }

            expense = expenseDBHelper.getExpenseperMonth(-1);
            totalExpense.setText(String.format(Locale.getDefault(),"%.2f",expense));
            c.close();
        }



    }

    // DISPLAY CHARTS
    private BarChart barChart;
    private BarData barData;

    private void displayCharts(){
        barChart = findViewById(R.id.ExpenseBarChart);
        barChart.setFitBars(true);
        barChart.setVisibleXRangeMaximum(20);

        ArrayList<BarEntry> yVals = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        int i=0;
        for (ExpenseItems expenseItem: expenseItemsArrayList) {

            yVals.add(new BarEntry(i++,(float) expenseItem.getExpenseTotalPrice()));
            xVals.add(expenseItem.getExpenseDate());
        }

        BarDataSet barDataSet = new BarDataSet(yVals,"Amount Spent");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setDrawValues(true);

        barData = new BarData(barDataSet);

        formatXAxis(barChart,xVals);
        barChart.setData(barData);
        barChart.invalidate();
        barChart.animateY(500);

    }

    private void formatXAxis(BarChart barChart,ArrayList<String> xVals){

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(90);

        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return xVals.get(index);
            }
        });

    }









}
