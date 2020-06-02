package com.newage.plantedaqua.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newage.plantedaqua.models.ExpenseItems;
import com.newage.plantedaqua.R;

import java.util.ArrayList;
import java.util.Locale;

public class ExpenseTableRecyclerView extends RecyclerView.Adapter<ExpenseTableRecyclerView.ExpenseRecyclerViewHolder> {


        private ArrayList<ExpenseItems> arrayList;

        Context context;
        private OnExpenseItemClickListener onExpenseItemClickListener;



    public ExpenseTableRecyclerView(Context context,ArrayList<ExpenseItems> arrayList, OnExpenseItemClickListener onExpenseItemClickListener){
            this.arrayList=arrayList;
            this.onExpenseItemClickListener=onExpenseItemClickListener;
            this.context = context;

        }

        @NonNull
        @Override
        public ExpenseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_table_items, parent, false);

            return new ExpenseRecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExpenseRecyclerViewHolder holder, int position) {

            holder.expensePrice.setText(String.format(Locale.getDefault(),"%.2f",arrayList.get(position).getExpenseTotalPrice()));
            holder.expenseQuantity.setText(String.format(Locale.getDefault(),"%d",arrayList.get(position).getExpenseQuantity()));
            holder.expenseTankName.setText(arrayList.get(position).getExpenseTankName());
            holder.expenseItemName.setText(arrayList.get(position).getExpenseItemName());
            holder.expenseDate.setText(arrayList.get(position).getExpenseDate());
            holder.expensePricePerQuantity.setText(String.format(Locale.getDefault(),"%.2f",arrayList.get(position).getExpensePrice()));

            if(arrayList.get(position).isShowDeleteButton() && arrayList.get(position).getCategory().equals("EXTRA")){

                holder.deleteExpenseButton.setVisibility(View.VISIBLE);
                holder.mainLayoutPerRow.setBackground(ContextCompat.getDrawable(context, R.drawable.fillbox));
                holder.mainLayoutPerRow.setPadding(5,5,5,5);

            }
            else
            {
                holder.deleteExpenseButton.setVisibility(View.GONE);

                holder.mainLayoutPerRow.setBackground(null);
                holder.mainLayoutPerRow.setPadding(0,0,0,0);

            }


        }

        public interface OnExpenseItemClickListener{

            void onClick(View view, int position);

        }




        @Override
        public int getItemCount() {
            return arrayList.size();

        }
        public class ExpenseRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView expenseDate;
            TextView expenseItemName;
            TextView expenseTankName;
            TextView expenseQuantity;
            TextView expensePrice;
            TextView expensePricePerQuantity;
            ImageView deleteExpenseButton;
            ConstraintLayout mainLayoutPerRow;



            ExpenseRecyclerViewHolder(View view){
                super(view);
               expenseDate = view.findViewById(R.id.ExpenseDate);
               expenseItemName = view.findViewById(R.id.ExpenseItemName);
               expenseTankName = view.findViewById(R.id.ExpenseTankName);
               expenseQuantity = view.findViewById(R.id.ExpenseQuantity);
               expensePrice = view.findViewById(R.id.ExpensePrice);
               expensePricePerQuantity = view.findViewById(R.id.ExpensePricePerUnit);
                mainLayoutPerRow = view.findViewById(R.id.eachExpenseRowMainLayout);
               deleteExpenseButton = view.findViewById(R.id.DeleteExpenseItemButton);
               deleteExpenseButton.setOnClickListener(this);
               view.setOnClickListener(this);


            }

            @Override
            public void onClick(View view) {

                onExpenseItemClickListener.onClick(view,getLayoutPosition());


            }
        }


}
