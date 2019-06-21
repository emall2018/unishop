package com.google.cloud.android.speech;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.widget.Toast.LENGTH_SHORT;

public class Profilelists extends AppCompatActivity {

    String category;
    EditText search;
    TextView title;
    private ArrayList<BookOrder> Profile;
    private RecyclerView recyclerView;
    private BookOrderAdapter bAdapter;
    private SharedPreferences mSharedPreferences;
    private Integer image[] = {R.drawable.avatar};
    private String txt[]={ "Ahmed"};
    private String mToken;
    private CompositeSubscription mSubscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilelists);
        category = getIntent().getStringExtra("category");
        search = findViewById(R.id.keyword);
        search.setText(category);


        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Profilelists.this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Profile = new ArrayList<>();

        for (int i = 0; i < image.length; i++) {
            BookOrder beanClassForRecyclerView_contacts = new BookOrder(image[i],txt[i]);
            Profile.add(beanClassForRecyclerView_contacts);
        }
        bAdapter = new BookOrderAdapter(Profilelists.this,Profile);
        recyclerView.setAdapter(bAdapter);
       // GetProfiles(category);
    }
    private void GetProfiles(String Category) {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");

System.out.println("bingo" + mToken);
        try{
            mSubscriptions.add(NetworkUtil.getRetrofit(mToken).searchByCategory(Category)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponse,this::handleError));}
        catch(Exception e)
        {e.printStackTrace();}
    }
    private void handleResponse(User user) {



        showSnackBarMessage(user.getName());

    }

    private void handleError(Throwable error) {



        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
        }
    }
    private void showSnackBarMessage(String message) {

        Toast.makeText(this, message, LENGTH_SHORT).show();

    }
}
