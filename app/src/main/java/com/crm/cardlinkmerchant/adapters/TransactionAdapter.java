package com.crm.cardlinkmerchant.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.activity.OfferDetailActivity;
import com.crm.cardlinkmerchant.activity.TransactionDetailActivity;
import com.crm.cardlinkmerchant.model.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionHolder> {
    private List<Transaction> transactions;
    private ViewGroup viewGroup;
    private Context context;
    public TransactionAdapter(Context context, List<Transaction> transactions){
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_transaction, parent, false);
        TransactionHolder viewHolder = new TransactionHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        String currency = context.getString(R.string.euro_symbol);
        holder.account_number.setText(transaction.getAccountNumber());
        holder.created_date.setText(transaction.getCreatedDate());
        holder.amount.setText(currency + transaction.getAmount());

        if(transaction.getStatus()){
            holder.status.setText("POSTED");
            holder.status.setBackgroundColor(context.getResources().getColor(R.color.btActiveOffer));
            holder.bg.setCardBackgroundColor(context.getResources().getColor(R.color.bgActiveOffer));
        }
        else {
            holder.status.setText("CANCELLED");
            holder.status.setBackgroundColor(context.getResources().getColor(R.color.btDeactiveOffer));
            holder.bg.setCardBackgroundColor(context.getResources().getColor(R.color.bgDeactiveOffer));
        }
        holder.bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactions = transactionList;
        notifyItemChanged(transactionList.size()-1);
    }
    public List<Transaction> getTransactionList() {
        return transactions;
    }

    public class TransactionHolder extends RecyclerView.ViewHolder {
        public TextView account_number;
        public TextView created_date;
        public TextView status;
        public TextView amount;
        public CardView bg;

        public TransactionHolder(@NonNull View itemView) {
            super(itemView);
            account_number = itemView.findViewById(R.id.tvItemTransactionAccountNumber);
            created_date = itemView.findViewById(R.id.tvItemTransactionCreatedDate);
            status = itemView.findViewById(R.id.tvItemTransactionStatus);
            amount = itemView.findViewById(R.id.tvItemTransactionAmount);
            bg = itemView.findViewById(R.id.cvItemTransaction);
        }

    }
}

