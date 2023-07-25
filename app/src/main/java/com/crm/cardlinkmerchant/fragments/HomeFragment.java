package com.crm.cardlinkmerchant.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.crm.cardlinkmerchant.activity.OffersActivity;
import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.activity.RegisterActivity;
import com.crm.cardlinkmerchant.activity.RegisterXinActivity;
import com.crm.cardlinkmerchant.activity.SelectPurchaseMethodActivity;
import com.crm.cardlinkmerchant.activity.SignUpActivity;
import com.crm.cardlinkmerchant.activity.Spend3Activity;
import com.crm.cardlinkmerchant.activity.TopupActivity;
import com.crm.cardlinkmerchant.activity.TransactionManagementActivity;
import com.crm.cardlinkmerchant.utils.ChosseDialog;
import com.crm.cardlinkmerchant.utils.CustomDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {
    Unbinder unbinder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        // bind view using butter knife
        unbinder = ButterKnife.bind(this, view);

        return view;

    }

    @OnClick({R.id.fragment_home_register_customer, R.id.fragment_home_transaction, R.id.fragment_home_create_purchase, R.id.fragment_home_offers, R.id.fragment_home_wallet_topup})
    public void onClickTopup(View view){
        Intent intent;
        switch (view.getId()){
            case R.id.fragment_home_register_customer:
                intent = new Intent(getActivity().getBaseContext(), RegisterXinActivity.class);
                startActivity(intent);
//                ChosseDialog customDialog = new ChosseDialog(getActivity(), new ChosseDialog.OKListener() {
//                    @Override
//                    public void onOk() {
//                        Intent intent = new Intent(getActivity().getBaseContext(), RegisterActivity.class);
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Intent intent = new Intent(getActivity().getBaseContext(), SignUpActivity.class);
//                        startActivity(intent);
//                    }
//                }, "",getString(R.string.new_customer_registration), getString(R.string.signup_and_verify), false);
//
//                customDialog.show();
                return;
            case R.id.fragment_home_create_purchase:
//                intent = new Intent(getActivity().getBaseContext(), Spend3Activity.class);
                intent = new Intent(getActivity().getBaseContext(), SelectPurchaseMethodActivity.class);
                startActivity(intent);
                return;
            case R.id.fragment_home_transaction:
                intent = new Intent(getActivity().getBaseContext(), TransactionManagementActivity.class);
                startActivity(intent);
                return;
            case R.id.fragment_home_offers:
                intent = new Intent(getActivity().getBaseContext(), OffersActivity.class);
                startActivity(intent);
                return;
            case R.id.fragment_home_wallet_topup:
                intent = new Intent(getActivity().getBaseContext(), TopupActivity.class);
                startActivity(intent);
                return;
            default:
                return;
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}