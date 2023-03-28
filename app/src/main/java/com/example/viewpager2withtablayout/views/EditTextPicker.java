package com.example.viewpager2withtablayout.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.example.viewpager2withtablayout.R;

import timber.log.Timber;


public class EditTextPicker extends ClearableEditText implements TextWatcher {

    private final String mask;
    static final String TAG = EditTextPicker.class.getName();
    private boolean maskCheckFlag = true;

    public EditTextPicker(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        this.ImplementListeners();

        final TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.EditTextPicker,
                0, 0
        );

        try {
            // For mask
            this.mask = a.getString(R.styleable.EditTextPicker_mask);
            if (this.mask != null) {
                if (!this.mask.trim().isEmpty()) {
                    this.maskingEditText(this.mask);
                }
            } else
                this.removeTextChangedListener(this);

            //For type -> range and equal
            int type = a.getInteger(R.styleable.EditTextPicker_type, 0);
            if (type == 1) {

                float minvalue = a.getFloat(R.styleable.EditTextPicker_minValue, -1);
                float maxvalue = a.getFloat(R.styleable.EditTextPicker_maxValue, -1);

                if (minvalue == -1)
                    throw new RuntimeException("Min value not provided");
                if (maxvalue == -1)
                    throw new RuntimeException("Max value not provided");

            } else if (type == 2) {
                String defaultValue = a.getString(R.styleable.EditTextPicker_defaultValue);

                if (defaultValue == null)
                    throw new RuntimeException("Default value not provided");
            }

        } catch (final Exception e) {
            Timber.e(e, "TextPicker: ");
            throw e;
        } finally {
            a.recycle();
        }
    }

    private void ImplementListeners() {
        addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
    }

    @Override
    public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
        if (this.mask == null) return;
        this.maskCheckFlag = i2 != 0;
    }

    @Override
    public void afterTextChanged(final Editable editable) {
        if (this.mask == null) return;
        if (!this.maskCheckFlag) return;
        final StringBuilder txt = TextUtils.editTextLoopToNextChar(this.mask, editable.length() - 1);
        if (txt.length() == 0) return;
        //Input Filter work
        final InputFilter[] filters = editable.getFilters(); //get filter
        editable.setFilters(new InputFilter[]{}); //reset filter
        editable.insert(editable.length() - 1, txt);
        editable.setFilters(filters); //restore filter
    }

    // call for maskingEditText
    private void maskingEditText(final String mask) {
        setFilters(TextUtils.setLengthEditText(mask)); //Setting length
    }
}
