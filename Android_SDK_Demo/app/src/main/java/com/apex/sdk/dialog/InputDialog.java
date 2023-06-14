package com.apex.sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.apex.sdk.R;

public class InputDialog extends Dialog {
    private EditText userNick;
    private AppCompatButton cancel, submit;
    private InputDataListener inputDataListener;

    public InputDialog(@NonNull Context context) {
        super(context, R.style.waiteDialog);
    }

    public void setInputDataListener(InputDataListener inputDataListener) {
        this.inputDataListener = inputDataListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_input, null, false);
        setContentView(dialogView);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        userNick = dialogView.findViewById(R.id.user_nick);
        cancel = dialogView.findViewById(R.id.cancel);
        submit = dialogView.findViewById(R.id.submit);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = userNick.getText().toString();
                if (TextUtils.isEmpty(data)) {
                    return;
                }
                if (inputDataListener != null) {
                    inputDataListener.inputData(data);
                }
                dismiss();
            }
        });

    }

    public interface InputDataListener {
        void inputData(String data);
    }
}
