package com.apex.sdk.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.callback.MenuCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.enumeration.QueryWatchInfoType;
import com.apex.bluetooth.model.EABleMenuPage;
import com.apex.bluetooth.utils.LogUtils;
import com.apex.sdk.R;
import com.apex.sdk.dialog.WaitingDialog;
import com.apex.sdk.model.MenuInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MenuActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;
    private WaitingDialog waitingDialog;
    private List<MenuInfo> menuList;
    @BindView(R.id.tool)
    Toolbar toolbar;
    @BindView(R.id.show_menu_list)
    RecyclerView listView;
    @BindView(R.id.submit)
    AppCompatButton submitButton;
    String[] allpage;
    private AdapterMenu adapterMenu;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x40) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MenuActivity.this);
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                adapterMenu = new AdapterMenu();
                listView.setLayoutManager(linearLayoutManager);
                listView.setAdapter(adapterMenu);
                TouchHelp touchHelp = new TouchHelp();
                ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelp);
                touchHelper.attachToRecyclerView(listView);

            } else if (msg.what == 0x41) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(MenuActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x42) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }

            } else if (msg.what == 0x43) {
                if (waitingDialog != null) {
                    if (waitingDialog.isShowing()) {
                        waitingDialog.dismiss();
                    }
                }
                Toast.makeText(MenuActivity.this, getString(R.string.modification_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        unbinder = ButterKnife.bind(this);
        toolbar.setNavigationIcon(R.mipmap.exit_page);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (menuList == null) {
            menuList = new ArrayList<>();
        }
        EABleConnectState state = EABleManager.getInstance().getDeviceConnectState();
        if (state == EABleConnectState.STATE_CONNECTED) {
            if (waitingDialog == null) {
                waitingDialog = new WaitingDialog(MenuActivity.this);
            }
            // waitingDialog.show();
            EABleManager.getInstance().queryWatchInfo(QueryWatchInfoType.home_page, new MenuCallback() {
                @Override
                public void menuInfo(EABleMenuPage eaBleHomePage) {
                    LogUtils.i(TAG, "将结果传回到页面");
                    if (eaBleHomePage != null) {
                        List<EABleMenuPage.MenuType> typeList = eaBleHomePage.getTypeList();
                        List<EABleMenuPage.MenuType> allSupportList = eaBleHomePage.getAllSupportList();
                        if (allSupportList != null && !allSupportList.isEmpty()) {
                            Log.e(TAG, "所有支持的页面个数:" + allSupportList.size());
                            allpage = new String[allSupportList.size()];
                            for (int i = 0; i < allSupportList.size(); i++) {
                                if (allSupportList.get(i) == EABleMenuPage.MenuType.page_heart_rate) {
                                    allpage[i] = getString(R.string.heart_Rate);
                                } else if (allSupportList.get(i) == EABleMenuPage.MenuType.page_pressure) {
                                    allpage[i] = getString(R.string.stress);

                                } else if (allSupportList.get(i) == EABleMenuPage.MenuType.page_weather) {
                                    allpage[i] = getString(R.string.weather);

                                } else if (allSupportList.get(i) == EABleMenuPage.MenuType.page_music) {
                                    allpage[i] = getString(R.string.music);

                                } else if (allSupportList.get(i) == EABleMenuPage.MenuType.page_breath) {
                                    allpage[i] = getString(R.string.breathe);

                                } else if (allSupportList.get(i) == EABleMenuPage.MenuType.page_sleep) {
                                    allpage[i] = getString(R.string.sleep);
                                } else if (allSupportList.get(i) == EABleMenuPage.MenuType.page_menstrual_cycle) {
                                    allpage[i] = getString(R.string.menstrualCycle);
                                } else {

                                }
                            }
                        }
                        if (typeList != null && !typeList.isEmpty()) {

                            if (menuList != null) {
                                menuList.clear();
                            }
                            for (int i = 0; i < typeList.size(); i++) {
                                EABleMenuPage.MenuType menuType = typeList.get(i);
                                MenuInfo menuInfo = new MenuInfo();
                                menuInfo.setType(1);
                                menuInfo.setOrder(i);

                                if (menuType == EABleMenuPage.MenuType.page_heart_rate) {
                                    menuInfo.setMenuTitle(getString(R.string.heart_Rate));
                                    menuList.add(menuInfo);
                                } else if (menuType == EABleMenuPage.MenuType.page_pressure) {
                                    menuInfo.setMenuTitle(getString(R.string.stress));
                                    menuList.add(menuInfo);
                                } else if (menuType == EABleMenuPage.MenuType.page_weather) {
                                    menuInfo.setMenuTitle(getString(R.string.weather));
                                    menuList.add(menuInfo);
                                } else if (menuType == EABleMenuPage.MenuType.page_music) {
                                    menuInfo.setMenuTitle(getString(R.string.music));
                                    menuList.add(menuInfo);
                                } else if (menuType == EABleMenuPage.MenuType.page_breath) {
                                    menuInfo.setMenuTitle(getString(R.string.breathe));
                                    menuList.add(menuInfo);
                                } else if (menuType == EABleMenuPage.MenuType.page_sleep) {
                                    menuInfo.setMenuTitle(getString(R.string.sleep));
                                    menuList.add(menuInfo);
                                } else if (menuType == EABleMenuPage.MenuType.page_menstrual_cycle) {
                                    menuInfo.setMenuTitle(getString(R.string.menstrualCycle));
                                    menuList.add(menuInfo);
                                } else {

                                }
                            }


                        }
                    }
                    getHiddenPage();
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x40);
                    }

                }


                @Override
                public void mutualFail(int errorCode) {
                    Log.e(TAG, "获取菜单失败:" + errorCode);
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(0x41);
                    }
                }
            });
        }
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED) {
                    EABleMenuPage homePage = new EABleMenuPage();
                    List<EABleMenuPage.MenuType> typeList = new ArrayList<>();
                    if (menuList != null && !menuList.isEmpty()) {
                        for (int i = 0; i < menuList.size(); i++) {
                            MenuInfo menuInfo = menuList.get(i);
                            if (menuInfo.getType() == 2) {
                                break;
                            } else {
                                String title = menuInfo.getMenuTitle();
                                if (title.equalsIgnoreCase(getString(R.string.breathe))) {
                                    typeList.add(EABleMenuPage.MenuType.page_breath);
                                } else if (title.equalsIgnoreCase(getString(R.string.heart_Rate))) {
                                    typeList.add(EABleMenuPage.MenuType.page_heart_rate);
                                } else if (title.equalsIgnoreCase(getString(R.string.stress))) {
                                    typeList.add(EABleMenuPage.MenuType.page_pressure);
                                } else if (title.equalsIgnoreCase(getString(R.string.weather))) {
                                    typeList.add(EABleMenuPage.MenuType.page_weather);
                                } else if (title.equalsIgnoreCase(getString(R.string.music))) {
                                    typeList.add(EABleMenuPage.MenuType.page_music);
                                } else if (title.equalsIgnoreCase(getString(R.string.sleep))) {
                                    typeList.add(EABleMenuPage.MenuType.page_sleep);
                                } else if (title.equalsIgnoreCase(getString(R.string.menstrualCycle))) {
                                    typeList.add(EABleMenuPage.MenuType.page_menstrual_cycle);
                                } else {

                                }
                            }
                        }

                    }
                    if (typeList == null || typeList.isEmpty()) {
                        typeList.add(EABleMenuPage.MenuType.page_null);
                    }
                    homePage.setTypeList(typeList);
                    EABleManager.getInstance().setMenuPage(homePage, new GeneralCallback() {
                        @Override
                        public void result(boolean success) {
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x42);
                            }
                        }

                        @Override
                        public void mutualFail(int errorCode) {
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(0x43);
                            }
                        }
                    });

                }
            }
        });

    }

    private void getHiddenPage() {
        if (menuList != null && !menuList.isEmpty()) {
            Log.e(TAG, "所有菜单全部显示:列表数据长度" + menuList.size() + "所有支持的显示长度:" + allpage.length);
            if (menuList.size() == allpage.length) {
                MenuInfo menuInfo = new MenuInfo();
                menuInfo.setType(2);
                menuInfo.setOrder(menuList.size());
                menuInfo.setMenuTitle(getString(R.string.Hided));
                menuList.add(menuInfo);
                return;
            }
            for (int i = 0; i < allpage.length; i++) {
                boolean isExit = false;
                for (int j = 0; j < menuList.size(); j++) {
                    if (allpage[i].equalsIgnoreCase(menuList.get(j).getMenuTitle())) {
                        isExit = true;
                        break;
                    }
                }
                if (!isExit) {
                    boolean exitHidden = false;
                    for (int j = 0; j < menuList.size(); j++) {
                        if (menuList.get(j).getMenuTitle().equalsIgnoreCase(getString(R.string.Hided))) {
                            exitHidden = true;
                            break;
                        }
                    }
                    if (!exitHidden) {
                        MenuInfo menuInfo = new MenuInfo();
                        menuInfo.setType(2);
                        menuInfo.setOrder(menuList.size());
                        menuInfo.setMenuTitle(getString(R.string.Hided));
                        menuList.add(menuInfo);
                    }
                    MenuInfo menuInfo = new MenuInfo();
                    menuInfo.setType(1);
                    menuInfo.setOrder(menuList.size());
                    menuInfo.setMenuTitle(allpage[i]);
                    menuList.add(menuInfo);
                }
            }
        } else {
            Log.e(TAG, "全部是隐藏界面");
            for (int i = 0; i < allpage.length; i++) {
                MenuInfo menuInfo = new MenuInfo();
                menuInfo.setType(1);
                menuInfo.setOrder(i);
                menuList.add(menuInfo);
                if (allpage[i].equalsIgnoreCase(getString(R.string.heart_Rate))) {
                    menuInfo.setMenuTitle(getString(R.string.heart_Rate));
                } else if (allpage[i].equalsIgnoreCase(getString(R.string.stress))) {
                    menuInfo.setMenuTitle(getString(R.string.stress));
                } else if (allpage[i].equalsIgnoreCase(getString(R.string.weather))) {
                    menuInfo.setMenuTitle(getString(R.string.weather));
                } else if (allpage[i].equalsIgnoreCase(getString(R.string.music))) {
                    menuInfo.setMenuTitle(getString(R.string.music));
                } else if (allpage[i].equalsIgnoreCase(getString(R.string.breathe))) {
                    menuInfo.setMenuTitle(getString(R.string.breathe));
                } else if (allpage[i].equalsIgnoreCase(getString(R.string.sleep))) {
                    menuInfo.setMenuTitle(getString(R.string.sleep));
                } else if (allpage[i].equalsIgnoreCase(getString(R.string.menstrualCycle))) {
                    menuInfo.setMenuTitle(getString(R.string.menstrualCycle));
                }
            }
            MenuInfo menuInfo = new MenuInfo();
            menuInfo.setType(2);
            menuInfo.setOrder(0);
            menuInfo.setMenuTitle(getString(R.string.Hided));
            menuList.add(0, menuInfo);

        }

    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (waitingDialog != null) {
            waitingDialog.dismiss();
            waitingDialog = null;
        }

        super.onDestroy();
    }


    class AdapterMenu extends RecyclerView.Adapter<AdapterMenu.ViewHold> {
        @NonNull
        @Override
        public ViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v;
            if (viewType == 1) {
                v = LayoutInflater.from(MenuActivity.this).inflate(R.layout.adapter_menu_set, parent, false);
            } else {
                v = LayoutInflater.from(MenuActivity.this).inflate(R.layout.adapter_menu_group, parent, false);
            }
            return new ViewHold(v);

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHold holder, int position) {
            if (menuList == null || menuList.isEmpty()) {
                return;
            }
            holder.nameText.setText(menuList.get(position).getMenuTitle());

        }

        @Override
        public int getItemCount() {
            return menuList == null ? 0 : menuList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return menuList.get(position).getType();
        }

        class ViewHold extends RecyclerView.ViewHolder {
            @BindView(R.id.menu_name)
            TextView nameText;

            public ViewHold(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        public void onItemMove(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            int fromPosition = source.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            if (fromPosition < menuList.size() && toPosition < menuList.size()) {
                Collections.swap(menuList, fromPosition, toPosition);
                notifyItemMoved(fromPosition, toPosition);
            }
        }

        public MenuInfo getData(int position) {
            if (menuList != null) {
                return menuList.get(position);
            }
            return null;
        }

        public void onItemDissmiss(RecyclerView.ViewHolder source) {

        }

        public void onItemSelect(RecyclerView.ViewHolder source) {
            //当拖拽选中时放大选中的view
            //  source.itemView.setScaleX(1.2f);
            //  source.itemView.setScaleY(1.2f);
        }

        public void onItemClear(RecyclerView.ViewHolder source) {
            //  source.itemView.setScaleX(1.0f);
            //  source.itemView.setScaleY(1.0f);
        }
    }

    public class TouchHelp extends ItemTouchHelper.Callback {
        private final String TAG = this.getClass().getSimpleName();
        private int viewWidth;

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

            if (adapterMenu != null) {
                MenuInfo menuInfo = adapterMenu.getData(viewHolder.getAdapterPosition());
                if (menuInfo != null) {
                    if (menuInfo.getType() == 2) {
                        return makeMovementFlags(0, 0);
                    }
                }
            }
            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            adapterMenu.onItemMove(viewHolder, target);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                adapterMenu.onItemSelect(viewHolder);


            }

        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            if (!recyclerView.isComputingLayout()) {
                adapterMenu.onItemClear(viewHolder);
            }

        }


        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return 1.5f;
        }

        @Override
        public float getSwipeEscapeVelocity(float defaultValue) {
            return defaultValue * 100;
        }

    }
}
