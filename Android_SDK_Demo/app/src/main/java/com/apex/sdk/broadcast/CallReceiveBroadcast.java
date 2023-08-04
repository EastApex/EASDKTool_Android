package com.apex.sdk.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.apex.bluetooth.callback.GeneralCallback;
import com.apex.bluetooth.core.EABleManager;
import com.apex.bluetooth.enumeration.EABleConnectState;
import com.apex.bluetooth.model.EABleSocialContact;

import java.util.Calendar;


public class CallReceiveBroadcast extends BroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();
    private static int lastetState = TelephonyManager.CALL_STATE_IDLE; // 最后的状态


    @Override
    public void onReceive(Context context, Intent intent) {
        // Log.e(TAG, "获取到来电广播");
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.e(TAG, "挂断电话");
                        boolean isMiss = false;
                        if (lastetState == TelephonyManager.CALL_STATE_RINGING) {
                            isMiss = true;
                        }
                        lastetState = TelephonyManager.CALL_STATE_IDLE;
                        if (EABleManager.getInstance().getDeviceConnectState() != EABleConnectState.STATE_CONNECTED) {
                            return;
                        }

                        EABleSocialContact eaBleSocialContact = new EABleSocialContact();
                        eaBleSocialContact.seteType(EABleSocialContact.SocialContactType.incomingcall);
                        eaBleSocialContact.setE_ops(EABleSocialContact.SocialContactOps.del);
                        EABleManager.getInstance().pushInfo2Watch(eaBleSocialContact, new GeneralCallback() {
                            @Override
                            public void result(boolean success,int reason) {
                                Log.e(TAG, "挂断发送成功");
                            }

                            @Override
                            public void mutualFail(int errorCode) {

                            }
                        });


                        //未接来电提醒
                        if (isMiss) {
                            EABleSocialContact eaBleSocialContact3 = new EABleSocialContact();
                            eaBleSocialContact3.seteType(EABleSocialContact.SocialContactType.missedcall);
                            eaBleSocialContact3.setE_ops(EABleSocialContact.SocialContactOps.add);
                            eaBleSocialContact3.setTitle(incomingNumber);
                            eaBleSocialContact3.setContent(incomingNumber);
                            eaBleSocialContact3.setDate(getNoticeTime(Calendar.getInstance().getTimeInMillis()));
                            if (EABleManager.getInstance().getDeviceConnectState() == EABleConnectState.STATE_CONNECTED && eaBleSocialContact3 != null) {
                                EABleManager.getInstance().pushInfo2Watch(eaBleSocialContact3, new GeneralCallback() {
                                    @Override
                                    public void result(boolean success,int reason) {

                                    }

                                    @Override
                                    public void mutualFail(int errorCode) {

                                    }
                                });

                            }
                        }


                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        lastetState = TelephonyManager.CALL_STATE_RINGING;

                        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        //     Log.e(TAG, "大于android9的时候直接使用通知不使用状态监听");
                        //     return;
                        // }
                        if (EABleManager.getInstance().getDeviceConnectState() != EABleConnectState.STATE_CONNECTED) {
                            return;
                        }
                        // String incomingNumber = intent.getStringExtra("incoming_number");
                        Log.e(TAG, "电话号码:" + incomingNumber);

                        //  Log.e(TAG, "已初始化");
                        //      Log.e(TAG, "蓝牙已连接上");
                        EABleSocialContact eaBleSocialContact2 = new EABleSocialContact();
                        eaBleSocialContact2.seteType(EABleSocialContact.SocialContactType.incomingcall);
                        eaBleSocialContact2.setE_ops(EABleSocialContact.SocialContactOps.add);
                        eaBleSocialContact2.setTitle(incomingNumber);
                        eaBleSocialContact2.setContent(incomingNumber);
                        eaBleSocialContact2.setDate(getNoticeTime(Calendar.getInstance().getTimeInMillis()));
                        if (eaBleSocialContact2 == null) {
                            return;
                        }
                        EABleManager.getInstance().pushInfo2Watch(eaBleSocialContact2, new GeneralCallback() {
                            @Override
                            public void result(boolean success,int reason) {
                                Log.e(TAG, "响铃发送成功");
                            }

                            @Override
                            public void mutualFail(int errorCode) {

                            }

                        });


                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.e(TAG, "接听");
                        lastetState = TelephonyManager.CALL_STATE_OFFHOOK;
                        if (EABleManager.getInstance().getDeviceConnectState() != EABleConnectState.STATE_CONNECTED) {
                            return;
                        }
                        EABleSocialContact eaBleSocialContact1 = new EABleSocialContact();
                        eaBleSocialContact1.seteType(EABleSocialContact.SocialContactType.incomingcall);
                        eaBleSocialContact1.setE_ops(EABleSocialContact.SocialContactOps.del);
                        EABleManager.getInstance().pushInfo2Watch(eaBleSocialContact1, new GeneralCallback() {
                            @Override
                            public void result(boolean success,int reason) {
                                Log.e(TAG, "接听发送成功");
                            }

                            @Override
                            public void mutualFail(int errorCode) {

                            }

                        });


                        break;
                }

            }
        }, PhoneStateListener.LISTEN_CALL_STATE);

        //  telephony.listen(new PhoneStateListener() {
        //      @Override
        //      public void onCallStateChanged(int state, String incomingNumber) {
        //          super.onCallStateChanged(state, incomingNumber);
        //          switch (state) {

        //          }
        //      }
        //  }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private String getNoticeTime(long noticeTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(noticeTime);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return calendar.get(Calendar.YEAR) + (month < 10 ? "0" + month : month + "") + (day < 10 ? "0" + day : day + "") + "T" + (hour < 10 ? "0" + hour : hour + "") + (minute < 10 ? "0" + minute : minute + "") + (second < 10 ? "0" + second : second + "");
    }

}
