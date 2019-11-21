package com.spaceballs.fraudcalldetector;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

public class DisclaimerDialog extends DialogFragment {

    public interface IDisclaimerDialogHandler {
        void onAgree(Dialog dialog, boolean buttonChecked);
        void onDisagree(Dialog dialog);
    }

    View dialogView;
    IDisclaimerDialogHandler resultHandler;

    public static DisclaimerDialog createDialog(IDisclaimerDialogHandler resultHandler) {
        DisclaimerDialog dialog = new DisclaimerDialog();
        dialog.resultHandler = resultHandler;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.disclaimer, null);

        dialogView.findViewById(R.id.agreeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO handle agree here
                CheckBox rememberChoiceCB = dialogView.findViewById(R.id.rememberCheckBox);
                boolean rememberChoice = rememberChoiceCB.isChecked();
                resultHandler.onAgree(getDialog(), rememberChoice);
            }
        });

        dialogView.findViewById(R.id.disagreeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultHandler.onDisagree(getDialog());
            }
        });

        // set background to transparent
        final Drawable bg = new ColorDrawable(Color.BLACK);
        bg.setAlpha(130);

        dialog.getWindow().setBackgroundDrawable(bg);
        dialog.getWindow().setContentView(dialogView);

        return dialog;
    }
}
