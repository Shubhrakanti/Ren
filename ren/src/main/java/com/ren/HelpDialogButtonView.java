package com.ren;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.content.ContextCompat;

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

        Log.e(TAG, "Being created");
        try {

            dialogTitleStr  = a.getString(R.styleable.HelpDialogButtonView_dialogTitle);
            dialogMsgStr    = a.getString(R.styleable.HelpDialogButtonView_dialogMsg);
            if( MainActivity.DEBUG) {Log.e(TAG, "Dialog Title: " + dialogTitleStr + " Msg: " + dialogMsgStr );}
            dialogTitleDrawableInt = a.getResourceId(R.styleable.HelpDialogButtonView_dialogDrawable, -1);
        } finally {
            a.recycle();
        }

        buildAlertDialog();
        setOnClickListener(this);
    }

    private void buildAlertDialog()
    {
        Log.e(TAG, "Dialog being built");
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
