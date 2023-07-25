package com.crm.cardlinkmerchant.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crm.cardlinkmerchant.R;
import com.crm.cardlinkmerchant.model.AppConfig;
import com.crm.cardlinkmerchant.model.Offer;
import com.crm.cardlinkmerchant.storage.Storage;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TermConditionFragment extends Fragment {
    Unbinder unbinder;
    private final String TAG = "TERM_CONDITION_FRG";

    @BindView(R.id.tvTermConditionContent)
    TextView tvTermConditionContent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_term_condition, container, false);

        unbinder = ButterKnife.bind(this, root);
        String htmlTemp = "Lorem ipsum dolor sit amet consectetur, adipisicing elit. Illum hic quasi quae, dolorem. Dignissimos, sed excepturi ad praesentium beatae labore dolores, ullam fugiat doloremque, dolorem ipsa mollitia earum unde ut.";
        String html = "";
        String url = null;
        String appConfigStr = Storage.getAppConfig(getContext());
        //AppUtils.console(getContext(),TAG, "appConfig="+ appConfigStr);

        ObjectMapper mapper = AppUtils.initMapper();

        try {
            AppConfig appConfig = mapper.readValue(appConfigStr.toString(), AppConfig.class);
            AppUtils.console(getContext(),TAG, "appConfig ="+ appConfig.toString());
            html = appConfig.getAbout_details().getTerms_conditions().getContent();
            url = appConfig.getAbout_details().getTerms_conditions().getUrl();
            AppUtils.console(getContext(),TAG, "html = "+ html);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            String stackTrace = Log.getStackTraceString(e);
            AppUtils.console(getContext(),TAG, "AppConfig JsonProcessingException e ="+ stackTrace);
        }
        if(url != null){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
            getActivity().onBackPressed();
        }
        else {
            html = html == null ? htmlTemp : html;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvTermConditionContent.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvTermConditionContent.setText(Html.fromHtml(html));
            }
        }
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}