package com.crm.cardlinkmerchant.services;

import android.content.Context;
import android.util.Log;


import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.crm.cardlinkmerchant.model.Offer;
import com.crm.cardlinkmerchant.model.PurchaseResponse;
import com.crm.cardlinkmerchant.storage.Storage;
import com.crm.cardlinkmerchant.utils.AppUtils;
import com.crm.cardlinkmerchant.utils.Constants;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CrmServices {
    private static final String TAG = "CL_API_LOG";
    private Context context;
    public CrmServices(Context context){
        this.context = context;
        Constants.CrmConfig.TOKEN = Storage.getToken(context);
        Constants.CrmConfig.REFRESH_TOKEN = Storage.getRefreshToken(context);
    }
    public JSONObject getAppConfig() throws Exception {
        Log.d(TAG, "getAppConfig: APP_ID =" + Constants.CrmConfig.APP_ID);
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_GET_APP_CONFIG + "?platform_app_id=" + Constants.CrmConfig.APP_ID;
            result = ServiceUtils.sendGetRequestWithApiKey(url, context);
            Log.d(TAG, "getAppConfig result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else{
                    result.put("code", ResultCode.UNKNOWN_ERROR);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getAppConfig: " + Constants.CrmConfig.APP_ID + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }
    public JSONObject getOrganisationOutlet(String orgId) throws Exception {
        Log.d(TAG, "getOrganisationOutlet: orgId =" + orgId);
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_GET_ORGANISATION.replace("{id}", orgId);
            url = url + "?page=1&size=1000";
            result = ServiceUtils.sendGetRequestWithToken(url, context);
            Log.d(TAG, "getOrganisationOutlet result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else{
                    result.put("code", ResultCode.UNKNOWN_ERROR);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getOrganisationOutlet orgId = " + orgId + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }
    public JSONObject getCustomer(String customerValue, Boolean isCallWithToken) throws Exception {
        Log.d(TAG, "getCustomer: customerValue =" + customerValue);
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_GET_CUSTOMER + "?include_financials=true&search_value=" + customerValue;
            if (isCallWithToken){
                result = ServiceUtils.sendGetRequestWithToken(url, context);
            }
            else{
                result = ServiceUtils.sendGetRequestWithApiKey(url, context);
            }
            Log.d(TAG, "getCustomer result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else{
                    result.put("code", ResultCode.UNKNOWN_ERROR);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getCustomer customerValue = " + customerValue + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }
    public JSONObject makePurchase(String contactId, String outLetId, Double amount, String classification, Double requestAmount) throws Exception {
        Log.d(TAG, "makePurchase: contactId=" + contactId );
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_MAKE_PURCHASE;

            JSONObject body = new JSONObject();
            body.put("contact_id", contactId);
            body.put("reduction_method", "FRONT_END");
            body.put("organisation_id", outLetId);
            body.put("total_amount", amount);
            body.put("net_amount", amount);
            body.put("tax_amount", 0);

            if(requestAmount > 0){
                JSONObject spend_request = new JSONObject();
                spend_request.put("amount", requestAmount);
                spend_request.put("restrict_fully_covered", false);
                body.put("spend_request", spend_request);
            }

//            JSONObject classificationJS = new JSONObject();
//            classificationJS.put("name", classification);
//            body.put("classification", classificationJS);

            result = ServiceUtils.sendPostRequestWithToken(url, body, context);
            Log.d(TAG, "makePurchase result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else if(result.has("responseCode") && result.getInt("responseCode") == 429){
                    result.put("code", ResultCode.MANY_REQUESTS);
                }
                else{
                    result.put("code", ResultCode.UNKNOWN_ERROR);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "makePurchase: contactId" + contactId + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }

    public JSONObject makeTopup(String walletId, Double amount, String code, String date) throws Exception {
        Log.d(TAG, "makeTopup: walletId=" + walletId );
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_MAKE_TOPUP;

            JSONObject body = new JSONObject();
            body.put("wallet_id", walletId);
            body.put("payment_method", "CASH");
            body.put("code", code);
            body.put("topup_date", date);
            body.put("amount", amount);


            result = ServiceUtils.sendPostRequestWithToken(url, body, context);
            Log.d(TAG, "makePurchase result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else if(result.has("responseCode") && result.getInt("responseCode") == 429){
                    result.put("code", ResultCode.MANY_REQUESTS);
                }
                else{
                    result.put("code", ResultCode.UNKNOWN_ERROR);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "makeTop: walletId" + walletId + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }

    public JSONObject getOTPContact(String contactId, Boolean isCallWithToken) throws Exception {
        Log.d(TAG, "getOTPContact: contactId=" + contactId );
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_GET_CONTACT_OTP.replace("{id}", contactId);
            JSONObject body = new JSONObject();
            body.put("send_method", "SMS");
            if (isCallWithToken)
                result = ServiceUtils.sendPostRequestWithToken(url, body, context);
            else
                result = ServiceUtils.sendPostRequestWithApiKey(url, body, context);
            Log.d(TAG, "getOTPContact result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else if(result.has("responseCode") && result.getInt("responseCode") == 429){
                    result.put("code", ResultCode.MANY_REQUESTS);
                }
                else{
                    result.put("code", ResultCode.UNKNOWN_ERROR);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getOTPContact: " + contactId + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }
    public JSONObject verifyOTP(String contactId, String auth_otp, String otp, Boolean isCallWithToken) throws Exception {
        Log.d(TAG, "verifyOTP: contactId=" + contactId );
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_VERIFY_OTP.replace("{id}", contactId);
            JSONObject body = new JSONObject();
            body.put("auth_otp", auth_otp);
            body.put("code", otp);
            if(isCallWithToken){
                result = ServiceUtils.sendPostRequestWithToken(url, body, context);
            }
            else{
                result = ServiceUtils.sendPostRequestWithApiKey(url, body, context);
            }

            Log.d(TAG, "verifyOTP result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else if (result.has("error") && result.getString("error").equals("CRM.EXCEPTIONS.INVALIDLOGINEXCEPTION")){
                    result.put("code", ResultCode.INVALIDLOGINEXCEPTION);
                }
                else if (result.has("error") && result.getString("error").equals("COM.CRM.EXCEPTIONS.CONTACTUNIQUELYIDENTIFYEXCEPTION")){
                    result.put("code", ResultCode.INVALIDLOGINEXCEPTION);
                }
                else{
                    result.put("code", ResultCode.UNKNOWN_ERROR);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "verifyOTP: " + contactId + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }
    public JSONObject forgotPassword(String email) throws Exception {
        Log.d(TAG, "forgotPassword: email=" + email );
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_FORGOT_PASSWORD;

            JSONObject body = new JSONObject();
            body.put("email", email);

            result = ServiceUtils.sendPostRequest(url, body, context);
            Log.d(TAG, "forgotPassword result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else  if(result.has("responseCode") && result.getInt("responseCode") == 400){
                    result.put("code", ResultCode.INVALIDLOGINEXCEPTION);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "forgotPassword: " + email + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }

    public JSONObject login(String email, String password) throws Exception {
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_AUTHENTICATE;

            JSONObject body = new JSONObject();
            body.put("username", email);
            body.put("password", password);

            result = ServiceUtils.sendPostRequest(url, body, context);
            Log.d(TAG, "login result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else  if(result.has("responseCode") && result.getInt("responseCode") == 400){
                    result.put("code", ResultCode.INVALIDLOGINEXCEPTION);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "login: " + email + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }

    public JSONObject switchBusiness(String orgId) throws Exception {
        Log.d(TAG, "switch business: orgId=" + orgId );
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_ORGANISATIONS;
            JSONObject body = new JSONObject();

            result = ServiceUtils.sendPostRequestWithToken(url  + "/"+ orgId + "/switch", body, context);
            Log.d(TAG, "switchBusiness result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else{
                    result.put("code", ResultCode.UNKNOWN_ERROR);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "switchBusiness: " + orgId + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }
    public JSONObject getRewardOffers() throws Exception {
        Log.d(TAG, "getRewardOffers: " );
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_GET_REWARD_OFFERS;
            result = ServiceUtils.sendGetRequestWithToken(url , context);
            Log.d(TAG, "getRewardOffers result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                   // JSONArray content = result.getJSONArray("content");
                    //Log.d(TAG, "getRewardOffers content: " + content);
                    result.put("code", ResultCode.OK);
//                    List<Offer> offers = new ArrayList<>();
//                    if(content != null){
//                        for (int i=0; i< content.length() ; i++){
//                            ObjectMapper mapper = AppUtils.initMapper();
//                            JSONObject org = (JSONObject) content.get(i);
//                            Offer convertOfr = mapper.readValue(org.toString(), Offer.class);
//
//                            List<Offer.Award> awards = getRewardOffersDetail(convertOfr.getId(), convertOfr);
//                            AppUtils.console(context, TAG, "GetRewardOffersTask offer awards = "+awards.toString());
//                            convertOfr.setAwards(awards);
//                            AppUtils.console(context, TAG, "GetRewardOffersTask offer item = "+convertOfr.toString());
//                            offers.add(convertOfr);
//                        }
//                    }
                }
                else{
                    result.put("code", ResultCode.UNKNOWN_ERROR);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getRewardOffers, Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }
    public List<Offer.Award> getRewardOffersDetail(String id) throws Exception {
        Log.d(TAG, "getRewardOffersDetail id: " +id );
        List<Offer.Award> awardList = new ArrayList<>();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_GET_OFFER_DETAIL.replace("{id}", id);
            JSONObject result = ServiceUtils.sendGetRequestWithToken(url , context);
            Log.d(TAG, "getRewardOffersDetail result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    JSONArray awards = result.getJSONArray("awards");
                    AppUtils.console(context.getApplicationContext(), TAG, "getRewardOffersDetail awards: " + awards);

                    if(awards != null){
                        for (int i=0; i< awards.length() ; i++){
                            ObjectMapper mapper = AppUtils.initMapper();
                            JSONObject org = (JSONObject) awards.get(i);
                            Offer.Award convertOfr = mapper.readValue(org.toString(), Offer.Award.class);
                            AppUtils.console(context, TAG, "GetRewardOffersTask award item = "+convertOfr.toString());
                            awardList.add(convertOfr);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getRewardOffers, Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return awardList;
    }
    public JSONObject changeOfferState(String offerId, String updateState) throws Exception {
        Log.d(TAG, "changeOfferState: offerId=" + offerId );
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_CHANGE_OFFER_STATE.replace("{id}", offerId);

            JSONObject body = new JSONObject();
            body.put("life_cycle_state", updateState);

            result = ServiceUtils.sendPostRequestWithToken(url, body, context);
            Log.d(TAG, "changeOfferState result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else{
                    result.put("code", ResultCode.UNKNOWN_ERROR);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "changeOfferState: " + offerId + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }
    public JSONObject changeOfferAward(String offerId, String amountType, Double amount) throws Exception {
        Log.d(TAG, "changeOfferState: offerId=" + offerId );
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_CHANGE_OFFER_AWARDS.replace("{id}", offerId);

            JSONObject body = new JSONObject();
            JSONArray awards = new JSONArray();
            JSONObject award = new JSONObject();
            award.put("amount", amount);
            award.put("amount_type", amountType);
            awards.put(award);
            body.put("awards", awards);

            result = ServiceUtils.sendPostRequestWithToken(url, body, context);
            Log.d(TAG, "changeOfferState result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else{
                    result.put("code", ResultCode.UNKNOWN_ERROR);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "changeOfferState: " + offerId + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }
    public JSONObject getOrganisationConfig() throws Exception {
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_ORGANISATION_CONFIG;
            JSONObject body = new JSONObject();

            result = ServiceUtils.sendGetRequestWithToken(url , context);
            AppUtils.console(context, TAG, "getOrganisationConfig result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else  if(result.has("responseCode") && result.getInt("responseCode") == 400){
                    result.put("code", ResultCode.INVALIDLOGINEXCEPTION);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getOrganisationConfig Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }


    public JSONObject createContact(String first_name, String last_name, String contact_type, String country_code, String phone_type, String phone_number)
            throws Exception {
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_CONTACT;

            JSONObject phone = new JSONObject();
            phone.put("country_code", country_code);
            phone.put("phone_type", phone_type);
            phone.put("number", phone_number);
            phone.put("is_primary", true);
            JSONArray phones = new JSONArray();
            phones.put(phone);

            JSONObject body = new JSONObject();
            body.put("first_name", first_name);
            body.put("last_name", last_name);
            body.put("contact_type", contact_type);
            body.put("phones", phones);

            Log.d(TAG, "createContact body: " + body);

            result = ServiceUtils.sendPostRequestWithApiKey(url, body, context);
            Log.d(TAG, "createContact result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else if (result.has("error") && result.getString("error").equals("COM.CRM.EXCEPTIONS.CIMALREADYEXISTSFORANOTHERCONTACTEXCEPTION")){
                    result.put("code", ResultCode.READY_EXISTS_FOR_ANOTHER_CONTAC_TEXCEPTION);
                }
                else {
                    throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "createContact: Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }
    public JSONObject signUpOrganisation(String contactId) throws Exception {
        Log.d(TAG, "signUpOrganisation: contactId=" + contactId );
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_SIGN_UP_ORGANISATION.replace("{id}", contactId);
            JSONObject body = new JSONObject();
            body.put("organisation_id", Storage.getOrganisationSelected(context));
            body.put("action", "SIGNUP");
            body.put("service_acceptance", true);
            body.put("email_opt_out", false);
            body.put("sms_opt_out", false);
            body.put("consent_state", "ACCEPTED");
            result = ServiceUtils.sendPostRequestWithApiKey(url, body, context);
            Log.d(TAG, "signUpOrganisation result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else{
                    result.put("code", ResultCode.UNKNOWN_ERROR);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "signUpOrganisation: " + contactId + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }
    public JSONObject signOut() throws Exception {
        JSONObject result = new JSONObject();
        try {

            JWT parsedJWT = new JWT(Constants.CrmConfig.TOKEN);
            String account_id = parsedJWT.getSubject();

            String url = Constants.CrmConfig.SERVICE_URL_SIGN_OUT.replace("{id}", account_id);
            JSONObject body = new JSONObject();

            result = ServiceUtils.sendPostRequestWithToken(url , body, context);
            AppUtils.console(context, TAG, "signOut result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else  if(result.has("responseCode") && result.getInt("responseCode") == 400){
                    result.put("code", ResultCode.INVALIDLOGINEXCEPTION);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "signOut Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }

    //tạm thời truyền mỗi page sang
    public JSONObject getPurchases(int page, String classification_id, String customerId,
                                   String fromDate,
                                   String toDate) throws Exception {
        JSONObject result = new JSONObject();
        try {
            //RecipientResponse customer = getCustomer(cardNumber);
            String url = Constants.CrmConfig.SERVICE_URL_LOAD_PURCHASES;//+ "&from_date=1570423477&to_date=1622103439"
            if(customerId != null && !customerId.isEmpty())
                url = url+"contact_id="+customerId;
            if(fromDate != null && !fromDate.isEmpty())
                url = url+"&from_date="+fromDate;
            if(toDate != null && !toDate.isEmpty())
                url = url+"&to_date="+toDate;
            if (page > 0){
                url = url+"&page="+page;
            }
            if(!classification_id.isEmpty()){
                url = url + "&classification_id=" + classification_id;
            }

            url = url + "&size=20";
            Log.d(TAG, "getPurchases url: " + url);
            JSONObject response = ServiceUtils.sendGetRequestWithToken(url, context);
            Log.d(TAG, "getPurchases: " + response.toString());

            if(response != null) {
                ObjectMapper mapper = initMapper();
                PurchaseResponse purchaseResponse = mapper.readValue(response.toString(), PurchaseResponse.class);
                result.put("purchases", purchaseResponse);
                Log.d(TAG, "getPurchases: " + purchaseResponse.toString());
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getPurchases:Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
    }
    public JSONObject cancelPurchase(String purchaseId) throws Exception {
        JSONObject result = new JSONObject();
        try {
            Log.d(TAG, "cancelPurchase Id = "+ purchaseId);
            JSONObject response = ServiceUtils.sendPostRequestWithToken(Constants.CrmConfig.SERVICE_URL_CANCEL_PURCHASE.replace("{id}", purchaseId)
                    , new JSONObject(), this.context);
            Log.d(TAG, "cancelPurchase: " + response);
            if(response != null) {
                String resultId = response.getString("id");
                if(resultId.equals(purchaseId)){
                    result.put("code", "OK");
                }
                else{
                    result.put("code", "ERROR");
                }
            }
            else{
                result = new JSONObject();
                result.put("code", "ERROR");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "cancelPurchases: " + purchaseId + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }

    public JSONObject getClassifications(int page) throws Exception {
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_CLASSIFICATIONS;
            if (page > 0){
                url = url+"page="+page;
            }

            url = url + "&size=100";
            JSONObject body = new JSONObject();

            result = ServiceUtils.sendGetRequestWithToken(url , context);
            AppUtils.console(context, TAG, "getClassifications result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else  if(result.has("responseCode") && result.getInt("responseCode") == 400){
                    result.put("code", ResultCode.INVALIDLOGINEXCEPTION);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getClassifications Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }
    public JSONObject refreshToken() throws Exception {
        Log.d(TAG, "getAppConfig: APP_ID =" + Constants.CrmConfig.APP_ID);
        JSONObject result = new JSONObject();
        try {
            result = ServiceUtils.sendRefreshToken(context);
            Log.d(TAG, "sendRefreshToken result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else{
                    result.put("code", ResultCode.UNKNOWN_ERROR);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "sendRefreshToken: " + Constants.CrmConfig.APP_ID + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }

    public JSONObject sendCommunication(String subject,String content) throws Exception {
        JWT parsedJWT = new JWT(Constants.CrmConfig.TOKEN);
        String email = parsedJWT.getClaim("email").asString();
        JSONObject result = new JSONObject();
        try {
            String url = Constants.CrmConfig.SERVICE_URL_CREATE_COMMUNICATION;
            JSONObject body = new JSONObject();
            body.put("name", subject);
            body.put("integration_id", Constants.CrmConfig.INTEGRATION_ID_SEND_EMAIL);
            body.put("channel", "EMAIL");
            body.put("language", "EN");
            body.put("subject", subject);
            body.put("content", content);
            body.put("recipient",email);
            Log.d(TAG, "sendCommunication: body=" + body );
            result = ServiceUtils.sendPostRequestWithApiKey(url, body, context);
            System.out.println("AAAAAAAAA");System.out.println("AAAAAAAAA");
            System.out.println("AAAAAAAAA");System.out.println("AAAAAAAAA");

            Log.d(TAG, "sendCommunication result: " + result);
            if(result != null) {
                if(result.has("responseCode") && result.getInt("responseCode") == 200){
                    result.put("code", ResultCode.OK);
                }
                else if(result.has("responseCode") && result.getInt("responseCode") == 429){
                    result.put("code", ResultCode.MANY_REQUESTS);
                }
                else{
                    result.put("code", ResultCode.UNKNOWN_ERROR);
                }
            }
            else{
                result = new JSONObject();
                result.put("code", ResultCode.UNKNOWN_ERROR);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "sendCommunication: email" + email + ", Exception: " + e.getMessage());
            if(e.getMessage().equals(ResultCode.NOT_CONNECT_API.toString())){
                throw new Exception(ResultCode.NOT_CONNECT_API.toString());
            }
            else if(e.getMessage().equals(ResultCode.REQUEST_TIMEOUT.toString())){
                throw new Exception(ResultCode.REQUEST_TIMEOUT.toString());
            }
            else {
                throw new Exception(ResultCode.UNKNOWN_ERROR.toString());
            }
        }
        return result;
    }

    private static ObjectMapper initMapper() {
        ObjectMapper mapper = new ObjectMapper();

        //mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        return mapper;
    }
}
