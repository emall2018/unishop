package com.google.cloud.android.speech;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by World of UI/UX on 01/04/2019.
 */

public class BookOrderActivity extends AppCompatActivity {

    TextView title;
    private ArrayList<BookOrder> bookOrderModelClasses;
    private RecyclerView recyclerView;
    private ProfileAdapter bAdapter;

    private Integer image[] = {R.drawable.ss,R.drawable.dd,R.drawable.bb,R.drawable.haha,
    R.drawable.aaaa,R.drawable.ggg};
    private String txt[]={ "candle holder","bowl","jug","cup","ashtray","plates"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order);


        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(BookOrderActivity.this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        bookOrderModelClasses = new ArrayList<>();

        for (int i = 0; i < image.length; i++) {
            BookOrder beanClassForRecyclerView_contacts = new BookOrder(image[i],txt[i]);
            bookOrderModelClasses.add(beanClassForRecyclerView_contacts);
        }
        bAdapter = new ProfileAdapter(BookOrderActivity.this,bookOrderModelClasses);
        recyclerView.setAdapter(bAdapter);
    }
}
