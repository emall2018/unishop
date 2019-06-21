package com.google.cloud.android.speech;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.cloud.android.speech.model.collection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment {

int load=0;
    String[] country = { "English", "French", "Arabic", "Spanish", "German","Turkish"};
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private String mToken;
    private String mEmail;
    TextView Name;
    private View view;
    private Switch sw1;
    TextView Language;
    private  String Subtitles;
    private String mlanguage;
    private  String mname;
private ImageView Home;
 private   Spinner spin;

    private ArrayList<collection> collectionList;
    private RecyclerView recyclerViewCollection;
    private CollectionAdapter collectionAdapter;

    private Integer image[] = {R.drawable.butt , R.drawable.ss, R.drawable.dd,
            R.drawable.bb};
    private String title[] = {"Winter","Summer","T-Shirts","Formal Shirts","Sunglasses"};
    private String description[] = {"20 Wardrobes","96 Wardrobes","125 Wardrobes ","99 Wardrobes","439 Wardrobes"};
    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_list, container, false);
        // Inflate the layout for this fragment
        recyclerViewCollection = view.findViewById(R.id.RecyclerViewCollection);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewCollection.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCollection.setItemAnimator(new DefaultItemAnimator());

        collectionList = new ArrayList<>();

        for (int i = 0; i < image.length; i++) {
            collection collection = new collection(title[i],description[i],image[i]);
            collectionList.add(collection);
        }
        collectionAdapter = new CollectionAdapter(getContext(),collectionList);
        recyclerViewCollection.setAdapter(collectionAdapter);
        mSubscriptions = new CompositeSubscription();
        sw1 = (Switch) view.findViewById(R.id.switch3);
        sw1.setOnCheckedChangeListener (null);
        spin = (Spinner) view.findViewById(R.id.spinner);
        initSharedPreferences();
        Home= view.findViewById(R.id.Home);
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), Welcomeee.class);
                startActivity(i);
            }
        });
        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (sw1.isChecked()) {
                    Subtitles = sw1.getTextOn().toString();

                }
                else
                    Subtitles = sw1.getTextOff().toString();
                mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                mToken = mSharedPreferences.getString(Constants.TOKEN,"");
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(Constants.Subtitles, Subtitles);



                editor.apply();
                User user = new User();
                user.setSubtitles(Subtitles);

                resetSettings(user);

            }

        });





return view;
    }

    private void resetSettings(User user) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");

        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");




            mSubscriptions.add(NetworkUtil.getRetrofit(mToken).ChangeSettings(mEmail, user)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponse,this::handleError));


    }
    private void handleResponse(Response response) {


        Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();



    }

    private void handleError(Throwable error) {



        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Network Error !", Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initSharedPreferences() {

        final TextView Email = (TextView) view.findViewById(R.id.Email);
        final TextView Name = (TextView) view.findViewById(R.id.tv_name);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
        mlanguage = mSharedPreferences.getString(Constants.Language,"");
Subtitles=mSharedPreferences.getString(Constants.Subtitles,"");
        Toast.makeText(getContext(), mlanguage, LENGTH_SHORT).show();

if ("ON".equals(Subtitles))
{
    sw1.setChecked(true);

}
else {
    sw1.setChecked(false);

}
        String[] list = ArrayUtils.removeElement(country, mlanguage);
System.out.println("list" +list);


       int index= Arrays.asList(country).indexOf(mlanguage);
        //Getting the instance of Spinner and applying OnItemSelectedListener on it
System.out.println("index" +index);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                load=load+1;

                if (load != 1)

                {
                    User user = new User();
                    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(Constants.Language, country[position]);
                    user.setLanguage(country[position]);
                    resetSettings(user);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        mname = mSharedPreferences.getString(Constants.username,"");
        Email.setText(mEmail);
        Name.setText(mname);
        spin.setSelection(index);
    }

    private void showSnackBarMessage(String message) {

        Toast.makeText(getContext(), message, LENGTH_SHORT).show();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }



}

