package com.crm.cardlinkmerchant.utils;
import com.crm.cardlinkmerchant.model.SendingSmsResult;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class StringUtils {
    static public String customFormat(double value ) {
        Locale locale = new Locale("en", "EN");
       // Locale locale = new Locale("el", "GR");
        String pattern = "###,###.##";
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat
                .getNumberInstance(locale);

        decimalFormat.applyPattern(pattern);
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);
        return decimalFormat.format(value);

    }
    static public String customFormatPriceForGr(double value ) {
        //Locale locale = new Locale("en", "EN");
         Locale locale = new Locale("el", "GR");
        String pattern = "###,###.##";
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat
                .getNumberInstance(locale);

        decimalFormat.applyPattern(pattern);
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);
        return decimalFormat.format(value);

    }

    public static SendingSmsResult getSendingSmsResponse(String response) {
        try {
            ObjectMapper mapper = initMapper();
            SendingSmsResult sendingSmsResponse = mapper.readValue(response, SendingSmsResult.class);
            return sendingSmsResponse;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
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
