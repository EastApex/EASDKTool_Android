package com.apex.sdk.utils;

import android.content.Context;
import android.util.TypedValue;

import androidx.annotation.NonNull;

public class SizeTransform {
    public static float dp2px(float dp, @NonNull Context mContext) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
    }
}
