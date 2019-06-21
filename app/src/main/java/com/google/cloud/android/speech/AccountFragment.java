package com.google.cloud.android.speech;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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

import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_SHORT;

public class AccountFragment extends Fragment  {
    private static final String TAG = "SellActivity";
    String[] category = { "pottery", "Dresses",  "Electronic Devices"};
    Bitmap bitmap;

    private ProgressDialog dialog = null;

    private static int RESULT_LOAD_IMAGE = 1;
    ProgressDialog dialog1 = null;
    View view;
    private Uri filePath;
    Button upload;
    String path;
    private String Email;
    private  String Token;
    private SharedPreferences mSharedPreferences;
    /**
     * Spinners
     */
    Spinner categorySpinner;

    String selectedCategory;
    private ProgressDialog pDialog;
    ArrayList<String> arrayList = new ArrayList<String>();
    ImageView thumbnailPic;
    TextView tv_productName;
    TextView tv_productPrice;
    TextView tv_productDescription;
    ImageView imageView;
Button camera;
    String name;
    String email;
    RetrofitInterface   apiService;

    private CompositeSubscription mSubscriptions;
    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_account, container, false);
        tv_productName = (TextView) view.findViewById(R.id.name);
        tv_productPrice = (TextView) view.findViewById(R.id.price);
        tv_productDescription = (TextView) view.findViewById(R.id.description);
        categorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
      //  subCategorySpinner = (Spinner) view.findViewById(R.id.subcategorySpinner);
        imageView = view.findViewById(R.id.thumbnailPic);
        camera = view.findViewById(R.id.cBtn);
        upload = view.findViewById(R.id.uploadBtn);

        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,category);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(aa);


        mSubscriptions = new CompositeSubscription();
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveProduct();

            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickedButton();

            }
        });



        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 selectedCategory = (String) parent.getSelectedItem();


                //sendSubCatRequest(selectedCategory);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Log.d("Lifecycle", "onCreate()");

        return view;
    }


    public void clickedButton() {
        Log.d("Lifecycle", "clickedButton() called");
        thumbnailPic = (ImageView) view.findViewById(R.id.thumbnailPic);
        if (thumbnailPic != null)
            callCamera();

    }

    //work of camera begins
    public void callCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            path = getPathFromURI(data.getData());

                filePath = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                    imageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getContext().getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
private  void      SaveProduct()
{  mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    Token = mSharedPreferences.getString(Constants.TOKEN,"");
    Email = mSharedPreferences.getString(Constants.EMAIL,"");
    String name =  tv_productName .getText().toString();
    String Price = tv_productPrice.getText().toString();
    String Description = tv_productDescription.getText().toString();

    showSnackBarMessage(selectedCategory);
        Product product = new Product();
        product.setpName(name);
        product.setCategory(selectedCategory);
        product.setpSNo(Description);
        product.setpPrice(Price);

    registerProcess(Email,Token, product);





}
    private void registerProcess(String Email, String Token, Product product) {
        System.out.println("mailll"+ Email);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Token = mSharedPreferences.getString(Constants.TOKEN,"");

        Email = mSharedPreferences.getString(Constants.EMAIL,"");



            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            Token = mSharedPreferences.getString(Constants.TOKEN,"");

            Email = mSharedPreferences.getString(Constants.EMAIL,"");



/*       File  file = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("name", "filename", requestBody);
System.out.println("khra" +part);

        mSubscriptions.add(NetworkUtil.getRetrofit(Token).UploadImage(Email,part )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
*/
        mSubscriptions.add(NetworkUtil.getRetrofit(Token).UploadProduct(Email, product )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponse, this::handleError));


     }
    private void handleResponse(Response response) {
        showSnackBarMessage("Product successfully added !");
        Fragment fragment = new UserListFragment();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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

        Toast.makeText(getContext(), message, LENGTH_SHORT).show();



    }
    }



