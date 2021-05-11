package com.stv.msgservice.ui.conversation.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

public class OptionItemView extends CheckedTextView {

    public OptionItemView(Context context) {
        this(context, null, 0);
    }

    public OptionItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptionItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    public OptionItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {

    }
}
