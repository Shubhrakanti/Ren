package com.ren;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Creates a button that has a sole purpose of showing a help menu.
 * Created by Alvin on 7/21/2016.
 */
public class HelpDialogButtonView extends Button implements View.OnClickListener {
    private final String TAG = "HelpDialogButtonView";
    AlertDialog helpAlertDialog;
    String dialogTitleStr, dialogMsgStr;
    int dialogTitleDrawableInt;
    Context context;

    public HelpDialogButtonView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.HelpDialogButtonView);

        setText("?");

        try {

            dialogTitleStr  = a.getString(R.styleable.HelpDialogButtonView_dialogTitle);
            dialogMsgStr    = a.getString(R.styleable.HelpDialogButtonView_dialogMsg);
            dialogTitleDrawableInt = a.getResourceId(R.styleable.HelpDialogButtonView_dialogDrawable, -1);
        } finally {
            a.recycle();
        }

        buildAlertDialog();
        setOnClickListener(this);
    }

    private void buildAlertDialog()
    {
           AlertDialog.Builder b = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        // Create custom title if drawable exists

        b.setTitle(dialogTitleStr);

        if(dialogTitleDrawableInt != -1)
            b.setIcon(dialogTitleDrawableInt);
        b.setMessage(dialogMsgStr);
        b.setPositiveButton("Ok", null);

        helpAlertDialog = b.create();
    }

    @Override
    public void onClick(View v)
    {
        helpAlertDialog.show();
    }
}
