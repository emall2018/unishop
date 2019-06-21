package com.google.cloud.android.speech;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by World of UI/UX on 01/04/2019.
 */

public class FilterActivity extends AppCompatActivity {

    private ArrayList<Food> foodOrderListModelClasses;
    private RecyclerView recyclerView;
    private FilterRestaurantAdapter bAdapter;

    private String txt[]={
            "pottery","watches","More..."};

    Dialog slideDialog;
    ImageView filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        openDialog();

        filter = findViewById(R.id.filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }

    private void openDialog() {

        slideDialog = new Dialog(FilterActivity.this, R.style.CustomDialogAnimation);
        slideDialog.setContentView(R.layout.filter_popup);

        slideDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Setting dialogview
        Window window = slideDialog.getWindow();
        //  window.setGravity(Gravity.BOTTOM);

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        slideDialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
        layoutParams.copyFrom(slideDialog.getWindow().getAttributes());

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.60);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = height;
        layoutParams.gravity = Gravity.BOTTOM;

        recyclerView = slideDialog.findViewById(R.id.filterrestaurant);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FilterActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        foodOrderListModelClasses = new ArrayList<>();

        for (int i = 0; i < txt.length; i++) {
            Food beanClassForRecyclerView_contacts = new Food(txt[i]);
            foodOrderListModelClasses.add(beanClassForRecyclerView_contacts);
        }
        bAdapter = new FilterRestaurantAdapter(FilterActivity.this,foodOrderListModelClasses);
        recyclerView.setAdapter(bAdapter);

        slideDialog.getWindow().setAttributes(layoutParams);
        slideDialog.setCancelable(true);
        slideDialog.setCanceledOnTouchOutside(true);
        slideDialog.show();
    }
}
