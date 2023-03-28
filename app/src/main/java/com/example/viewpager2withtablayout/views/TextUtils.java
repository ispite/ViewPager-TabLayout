package com.example.viewpager2withtablayout.views;

import android.text.InputFilter;

final class TextUtils {

    // call in afterTextChanged event
    public static StringBuilder editTextLoopToNextChar(final String maskEdit, final int position) {

        final StringBuilder finalResult = new StringBuilder();
        for (int i = position; i < maskEdit.length(); i++) {
            if (maskEdit.charAt(i) != '#') {
                finalResult.append(maskEdit.charAt(i));
            } else
                break;
        }

        return finalResult;
    }

    // set length
    public static InputFilter[] setLengthEditText(final String maskText) {
        return new InputFilter[]{new InputFilter.LengthFilter(maskText.length())};
    }

}
