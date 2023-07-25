package com.crm.cardlinkmerchant.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.activity.TransactionDetailActivity;
import com.crm.cardlinkmerchant.model.AccountResponse;
import com.crm.cardlinkmerchant.model.ContactResponse;
import com.crm.cardlinkmerchant.model.PurchaseContentResponse;

import static com.crm.cardlinkmerchant.utils.MoneyTextWatcher.customFormat;

import java.util.List;

public class TransactionManagementAdapter  extends RecyclerView.Adapter<TransactionManagementAdapter.TransactionHolder>{
    private List<PurchaseContentResponse> transactionList;
    private ViewGroup viewGroup;
    private Context context;
    public TransactionManagementAdapter(Context context, List<PurchaseContentResponse> transactions){
        this.context = context;
        this.transactionList = transactions;
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
        PurchaseContentResponse transaction = transactionList.get(position);

        AccountResponse account = transaction.getAccount();
        ContactResponse contact = transaction.getContact();
        if(account !=null){
            holder.account_number.setText(account.getNumber());
        }

        if(transaction.getState().equals("POSTED")){
            holder.status.setText(context.getString(R.string.posted));
            holder.status.setBackgroundColor(context.getResources().getColor(R.color.btActiveOffer));
            holder.bg.setCardBackgroundColor(context.getResources().getColor(R.color.bgActiveOffer));
        }
        else {
            holder.status.setText(context.getString(R.string.cancelled));
            holder.status.setBackgroundColor(context.getResources().getColor(R.color.btDeactiveOffer));
            holder.bg.setCardBackgroundColor(context.getResources().getColor(R.color.bgDeactiveOffer));
        }

        holder.amount.setText(context.getResources().getString(R.string.euro_symbol)+customFormat(transaction.getTotal_amount()));

        String date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(transaction.getCreated_date()*1000);
        holder.date.setText(date);

    }
    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void setTransactionList(List<PurchaseContentResponse> transactionList) {
        this.transactionList = transactionList;
        notifyItemChanged(transactionList.size()-1);
    }
    public List<PurchaseContentResponse> getTransactionList() {
        return transactionList;
    }

    public class TransactionHolder extends RecyclerView.ViewHolder {
        public TextView account_number;
        public TextView status;
        public TextView date;
        public TextView amount;
        public CardView bg;

        public TransactionHolder(@NonNull View itemView) {
            super(itemView);
            account_number = itemView.findViewById(R.id.tvItemTransactionAccountNumber);
            status = itemView.findViewById(R.id.tvItemTransactionStatus);
            date = itemView.findViewById(R.id.tvItemTransactionCreatedDate);
            amount = itemView.findViewById(R.id.tvItemTransactionAmount);
            bg = itemView.findViewById(R.id.cvItemTransaction);
        }
    }
}
