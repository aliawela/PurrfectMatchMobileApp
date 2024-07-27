package com.example.purrfectmatchmobileapp;

import android.content.Context;
import android.util.DisplayMetrics;

public class ColumnCalculator {

    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // Forexample
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // Rounding
        return noOfColumns;
    }
}