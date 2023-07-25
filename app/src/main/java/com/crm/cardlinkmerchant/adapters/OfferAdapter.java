package com.crm.cardlinkmerchant.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.activity.MainActivity;
import com.crm.cardlinkmerchant.activity.OfferDetailActivity;
import com.crm.cardlinkmerchant.activity.SwitchBusinessActivity;
import com.crm.cardlinkmerchant.model.Offer;
import com.crm.cardlinkmerchant.services.CrmServices;
import com.crm.cardlinkmerchant.services.ResultCode;
import com.crm.cardlinkmerchant.storage.Storage;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.CustomDialog;
import com.crm.cardlinkmerchant.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferHolder> {
    public interface OfferDetail{
        void onEdit(Offer offer);
    }
    private List<Offer> offers;
    private ViewGroup viewGroup;
    private Context context;
    private OfferDetail offerDetail;
    public OfferAdapter(Context context, List<Offer> offers, OfferDetail offerDetail){
        this.context = context;
        this.offers = offers;
        this.offerDetail = offerDetail;
    }

    @NonNull
    @Override
    public OfferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_offer, parent, false);
        OfferHolder viewHolder = new OfferHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OfferHolder holder, int position) {
        Offer offer = offers.get(position);

        holder.name.setText(offer.getName());
        holder.description.setText(offer.getShort_description());

        if(offer.getLife_cycle_state().equals("ACTIVE")){
            holder.status.setText(context.getResources().getString(R.string.active));
            holder.status.setBackgroundColor(context.getResources().getColor(R.color.btActiveOffer));
            holder.bg.setCardBackgroundColor(context.getResources().getColor(R.color.bgActiveOffer));
        }
        else {
            holder.status.setText(context.getResources().getString(R.string.deactive));
            holder.status.setBackgroundColor(context.getResources().getColor(R.color.btDeactiveOffer));
            holder.bg.setCardBackgroundColor(context.getResources().getColor(R.color.bgDeactiveOffer));
        }
        String pre = context.getResources().getString(R.string.offer_amount);
        List<Offer.Award> awards = offer.getAwards();

        if (awards.size() > 0){
            Offer.Award award = awards.get(0);
            double amount = award.getAmount();
            if(award.getAmount_type().equals("PERCENTAGE")){
                int amountInt = (int) amount;
                holder.amount.setText(pre + " " + amountInt + "%");
            }

            else if(award.getAmount_type().equals("FIXED")){
                String currency = award.getCurrency();
                String currencyStr = currency != null && !currency.isEmpty() ? currency : context.getResources().getString(R.string.euro_symbol);
                holder.amount.setText(pre + " " + currencyStr + StringUtils.customFormat(award.getAmount()));
            }

        }
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offerDetail.onEdit(offer);
            }
        });



    }
    @Override
    public int getItemCount() {
        return offers.size();
    }

    public class OfferHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView description;
        public TextView status;
        public TextView amount;
        public ImageButton edit;
        public CardView bg;


        public OfferHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvItemOfferName);
            description = itemView.findViewById(R.id.tvItemOfferDesc);
            status = itemView.findViewById(R.id.tvItemOfferStatus);
            amount = itemView.findViewById(R.id.tvItemOfferAmount);
            edit = itemView.findViewById(R.id.ibItemOfferMore);
            bg = itemView.findViewById(R.id.cvItemOffer);
        }

    }



}

