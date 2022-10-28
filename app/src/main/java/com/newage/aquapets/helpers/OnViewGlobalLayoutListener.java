package com.newage.aquapets.helpers;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

public  class OnViewGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {


    private Context context;
    private int maxHeight;
    private View view;

    public OnViewGlobalLayoutListener(View view, int maxHeight, Context context) {
        this.context = context;
        this.view = view;
        this.maxHeight = dpToPx(maxHeight);
    }

    @Override
    public void onGlobalLayout() {
        if (view.getHeight() > maxHeight) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = maxHeight;
            view.setLayoutParams(params);
        }
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}