package com.apex.sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apex.sdk.R;
import com.contrarywind.adapter.WheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;

import java.util.Arrays;
import java.util.List;

public class OnlineDialDialog extends Dialog {
    private List<String> dialList;
    private SelectListener selectListener;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;

    public void setSelectListener(SelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public OnlineDialDialog(@NonNull Context context, List<String> list) {
        super(context, R.style.waiteDialog);
        dialList = list;
    }

    public interface SelectListener {
        void selectDial(String dial);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_dial, null, false);
        setContentView(dialogView);
        recyclerView = dialogView.findViewById(R.id.dial_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        itemAdapter = new ItemAdapter();
        recyclerView.setAdapter(itemAdapter);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        //p.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.3
        p.width = (int) (d.getWidth() * 0.85); // 宽度设置为屏幕的0.85
        dialogWindow.setAttributes(p);

    }

    class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {
        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_dial_item, parent, false);
            return new ItemHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            if (dialList == null || dialList.isEmpty()) {
                return;
            }
            holder.textView.setText(dialList.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (selectListener != null) {
                        selectListener.selectDial(dialList.get(position));
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return dialList == null ? 0 : dialList.size();
        }

        class ItemHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ItemHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.dial_item);
            }
        }
    }

    public void destroyDialog() {
        dialList = null;
        selectListener = null;
        if (recyclerView != null) {
            recyclerView.setLayoutManager(null);
            recyclerView.setAdapter(null);
            recyclerView.clearAnimation();
            recyclerView.removeAllViews();
            recyclerView = null;
        }
        itemAdapter = null;
    }

}
