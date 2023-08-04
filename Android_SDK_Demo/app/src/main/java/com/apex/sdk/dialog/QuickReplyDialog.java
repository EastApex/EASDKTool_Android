package com.apex.sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apex.sdk.R;

import java.util.ArrayList;
import java.util.List;

public class QuickReplyDialog extends Dialog {
    RecyclerView recyclerView;
    List<String> contentList;
    ItemAdapter itemAdapter;
    AppCompatButton button;

    public QuickReplyDialog(@NonNull Context context) {
        super(context,R.style.waiteDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_quick_reply, null, false);
        setContentView(dialogView);
        button = dialogView.findViewById(R.id.button);
        recyclerView = dialogView.findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        contentList = new ArrayList<>();
        itemAdapter = new ItemAdapter();
        recyclerView.setAdapter(itemAdapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.AdapterHold> {


        @NonNull
        @Override
        public AdapterHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_txt, parent, false);
            return new AdapterHold(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterHold holder, int position) {
            if (contentList == null || contentList.isEmpty()) {
                return;
            }
            holder.item.setText(contentList.get(position));
        }

        @Override
        public int getItemCount() {
            return contentList == null ? 0 : contentList.size();
        }

        class AdapterHold extends RecyclerView.ViewHolder {
            TextView item;

            public AdapterHold(@NonNull View itemView) {
                super(itemView);
                item = itemView.findViewById(R.id.item_txt);
            }
        }
    }

    public void addContent(String msg) {
        if (TextUtils.isEmpty(msg) || "".equalsIgnoreCase(msg)) {
            return;
        }
        if (contentList == null) {
            contentList = new ArrayList<>();
        }
        if (contentList.isEmpty()) {
            contentList.add(msg);
        } else {
            contentList.add(0, msg);
        }
        if (itemAdapter != null) {
            itemAdapter.notifyDataSetChanged();
        }

    }

    public void destroy() {
        if (recyclerView != null) {
            recyclerView.clearAnimation();
            recyclerView.removeAllViews();
            recyclerView.setAdapter(null);
            recyclerView = null;
        }
        itemAdapter = null;
        if (contentList != null) {
            contentList.clear();
            contentList = null;
        }
    }
}
