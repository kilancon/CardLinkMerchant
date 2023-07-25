package com.crm.cardlinkmerchant.utils;

public class Constants {
    public static final int TIMEOUT = 5000;

    public static class CrmConfig {



        public static String TOKEN = "";
        public static String REFRESH_TOKEN = "";
        //public static String APP_ID ="69bfb7cc-2cb8-4d6c-b1df-8a71e8f8e380";
        public static String APP_ID ="com.crm.app.v5.uat.cardlinkrewardsmerchantpos";
        public static String SERVICE_URL = "https://sandbox.crm.com/backoffice/v1/";
        public static final String API_KEY = "be656e39-29a7-45fc-b037-3440a0783204"; //sandbox


//        public static String APP_ID ="com.crm.app.v5.cardlinkrewardsmerchantpos";
//        public static String SERVICE_URL = "https://app.crm.com/backoffice/v1/";
//        public static final String API_KEY = "8740261b-6c99-4a23-93ec-2e0f34a11ac6";// production

        public static final String INTEGRATION_ID_SEND_EMAIL= "fdec3220-ec14-4a8f-ac88-3f216443548f";

        public static final String REGISTER_PORTAL_URL= "https://cardlinkportaluat.crm.com/";
        public static final String TERM_AND_CONDITION_MERCHANT_URL= "https://cardlink.gr/en/terms-of-pos-supply";
        public static final String PRIVACY_POLICY_MERCHANT_URL= "https://cardlink.gr/en/gdpr";
        public static final String TERM_AND_CONDITION_CUSTOMER_URL= "https://6b9e-113-190-233-53.ap.ngrok.io/term-conditions-customer";

        public static final String SERVICE_URL_MAKE_TOPUP = "topups";

        public static final String SERVICE_URL_GET_APP_CONFIG = "applications";
        public static final String SERVICE_URL_FORGOT_PASSWORD = "users/forgot_password";
        public static final String SERVICE_URL_AUTHENTICATE = "users/authenticate";
        public static final String SERVICE_URL_ORGANISATIONS = "organisations";
        public static final String SERVICE_URL_ORGANISATION_CONFIG = "applications?platform_app_id="+APP_ID;
        public static final String SERVICE_URL_GET_REWARD_OFFERS = "reward_offers";
        public static final String SERVICE_URL_GET_OFFER_DETAIL = "reward_offers/{id}/awards";
        public static final String SERVICE_URL_CHANGE_OFFER_STATE = "reward_offers/{id}/life_cycle_state";
        public static final String SERVICE_URL_CHANGE_OFFER_AWARDS= "reward_offers/{id}/awards";
        public static final String SERVICE_URL_CONTACT = "contacts";
        public static final String SERVICE_URL_SIGN_OUT = "users/{id}/sign_out";
        public static final String SERVICE_URL_LOAD_PURCHASES = "purchases?";//What is the API to call ???
        public static final String SERVICE_URL_GET_ORGANISATION = "organisations/{id}/network";
        public static final String SERVICE_URL_GET_CUSTOMER = "contacts";
        public static final String SERVICE_URL_MAKE_PURCHASE = "purchases";
        public static final String SERVICE_URL_CANCEL_PURCHASE = "purchases/{id}/cancel";//What is the API to call ???
        public static final String SERVICE_URL_CLASSIFICATIONS = "customer_event_classifications?";//What is the API to call ???
        public static final String SERVICE_URL_GET_CONTACT_OTP = "contacts/{id}/otp";
        public static final String SERVICE_URL_VERIFY_OTP = "contacts/{id}/validate-otp";
        public static final String SERVICE_URL_SIGN_UP_ORGANISATION = "contacts/{id}/organisations";
        public static final String SERVICE_URL_REFRESH_TOKEN = "users/refresh";
        public static final String SERVICE_URL_CREATE_COMMUNICATION = "communications";
    }
    public static class SdkConfig {
        public static final String EMAIL_SENDER = "giftcard@cardlink.gr";
        public static final String EMAIL_SENDER_PASS = "zjjjvjgqhmnyzgqy";
        public static final int DEEPLINK_TOPUP_CODE = 100;
        public static final int DEEPLINK_CANCEL_TOPUP_CODE = 102;
        public static final String POS_APP_TOPUP_OPENLINK = "gr.cardlink.possible.activities.transaction.TransactionActivity";
        public static final String TOPUP_SALE = "SALE";
        public static final String TOPUP_VOID = "VOID";
        public static final String TOPUP_REVERSAL = "REVERSAL";

        //        public static final String SERVICE_TERMINAL_INFO_URL = "https://1282e84a6df0.ngrok.io/gpsapi/smartnav"; //VVVVVVV testtttttttt
        public static final String SERVICE_SEND_SMS_URL = "https://api.eu-gb.apiconnect.appdomain.cloud/cardlink-possible/api/v1/sms";
        public static final String SERVICE_SEND_SMS_CLIENT_ID = "02c0925c-44a2-4cc9-a36e-f596d8ff4ae0";
        public static final String SERVICE_SEND_SMS_SECRET = "xU1jT1oO8nP3mV8aE3wT0wH5hK2pP4iL6qJ8qG5tX1fA4rT7mF";
    }
}
