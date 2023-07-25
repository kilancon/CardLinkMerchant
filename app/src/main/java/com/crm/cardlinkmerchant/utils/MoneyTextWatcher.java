package com.crm.cardlinkmerchant.utils;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class MoneyTextWatcher implements TextWatcher {
    private final String TAG = "MoneyTextWatcher";
    private final WeakReference<EditText> editTextWeakReference;

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public MoneyTextWatcher(EditText editText) {
        this.editTextWeakReference = new WeakReference<>(editText);
    }

    public void afterTextChanged(Editable editable) {
        EditText editText = (EditText) this.editTextWeakReference.get();
        if (editText != null) {
            String obj = editable.toString();
            if (!obj.isEmpty()) {
                editText.removeTextChangedListener(this);
                if (editable.length() > 1 && !obj.contains(NumberFormat.getCurrencyInstance(Locale.GERMANY).getCurrency().getSymbol())) {
                    //Debug.LogInfo("MoneyTextWatcher", "clear 1 digit");
                    String replaceAll = editable.toString().replaceAll("\\s", "");
                    int length = replaceAll.length();
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(replaceAll);
                    //Debug.LogInfo("MoneyTextWatcher", "charCount: " + length);
                    if (length > 2) {
                        spannableStringBuilder.delete(length - 1, length);
                    }
                    String obj2 = spannableStringBuilder.toString();
                    //Debug.LogInfo("MoneyTextWatcher", "new string: " + obj2);
                    //Debug.LogInfo("MoneyTextWatcher", "new string length: " + obj2.length());
                    obj = obj2;
                }
                BigDecimal divide = new BigDecimal(obj.replaceAll(String.format("[%s,.]", new Object[]{NumberFormat.getCurrencyInstance(new Locale("el", "GR")).getCurrency().getSymbol()}), "").replaceAll("\\s", "")).setScale(2, 3).divide(new BigDecimal(100), 3);
                NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(Locale.US);
                currencyInstance.setCurrency(Currency.getInstance("EUR"));
                currencyInstance.setMaximumFractionDigits(2);
                String replace = currencyInstance.format(divide).replace(".", "!").replace(",", ".").replace("!", ",");
                editText.setText(replace);
                editText.setSelection(replace.length());
                editText.addTextChangedListener(this);
            }
        }
    }
    static public String customFormat(double value ) {
        //Locale locale = new Locale("el", "GR");
        Locale locale = new Locale("en", "ENG");
        String pattern = "###,###.##";
        DecimalFormat decimalFormat = (DecimalFormat)NumberFormat
                .getNumberInstance(locale);

        decimalFormat.applyPattern(pattern);
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);
        return decimalFormat.format(value);

    }
}
