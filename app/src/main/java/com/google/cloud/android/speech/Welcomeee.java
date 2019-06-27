package com.google.cloud.android.speech;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.ChatView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.GsonFactory;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIEvent;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.widget.Toast.LENGTH_SHORT;

public class Welcomeee extends AppCompatActivity implements   View.OnClickListener, SignalingClient.Callback {
    private FrameLayout MainFragment;
    private UserListFragment UserListFragment;
    private AccountFragment AccountFragment;
    private GroupFragment GroupFragment;
    private TextView mTextMessage;
    private CompositeSubscription mSubscriptions;
    public static final String TAG = Welcomeee.class.getName();
    private Gson gson = GsonFactory.getGson();

    private AIDataService aiDataService;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    private ChatView chatView;
    private Users myAccount;
    private Users UniSpeech;
    private String mToken;
    private String mEmail;



    String  mlanguage;

    private String username;
    private SharedPreferences mSharedPreferences;
 //   private String selectedTargetLanguage = "";

    MediaPlayer mp;

    public void popup (String username) {
        runOnUiThread(() -> {

                    try {
                        builder = new AlertDialog.Builder(Welcomeee.this, R.style.AlertDialogCustom);
                        View view;
                        view = LayoutInflater.from(Welcomeee.this).inflate(R.layout.outputcall, null);
                        TextView calleeName = view.findViewById(R.id.callee);

                        calleeName.setText("calling " + username + "...");

                        builder.setPositiveButton("End", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(Welcomeee.this, "ِCall canceled", Toast.LENGTH_SHORT).show();
                            }
                        });

                        builder.setView(view);
                      dialog=  builder.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }





    @Override
    public void onDestroy() {
        super.onDestroy();
        SignalingClient.get().destroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        MainFragment= (FrameLayout)findViewById(R.id.mainFrame);

        UserListFragment = new UserListFragment();
        AccountFragment  = new AccountFragment();
        GroupFragment= new GroupFragment();
        initSharedPreferences();
       loadProfile();
        initChatView();
        //Language, Dialogflow Client access token
        final LanguageConfig config = new LanguageConfig("1f127244af234e2a8ec83da0d716c110 ", "57ea648328dc4c039713f8bec24009ce");
        initService(config);
        sendRequest("hi");
    }


    @Override
    public void onClick(View v) {

        //new message
        final Message message = new Message.Builder()
                .setUser(myAccount)
                .setRightMessage(true)
                .setMessageText(chatView.getInputText())
                .hideIcon(false)
                .build();
        //Set to chat view
        chatView.send(message);
        sendRequest(chatView.getInputText());
        //Reset edit text
        chatView.setInputText("");
    }

    /*
    * AIRequest should have query OR event
    */
    private void sendRequest(String text) {
        Log.d(TAG, text);
        final String queryString = String.valueOf(text);
        final String eventString = null;
        final String contextString = null;

        if (TextUtils.isEmpty(queryString) && TextUtils.isEmpty(eventString)) {
            onError(new AIError(getString(R.string.non_empty_query)));
            return;
        }

        new Welcomeee.AiTask().execute(queryString, eventString, contextString);
    }

    public class AiTask extends AsyncTask<String, Void, AIResponse> {
        private AIError aiError;

        @Override
        protected AIResponse doInBackground(final String... params) {
            final AIRequest request = new AIRequest();
            String query = params[0];
            String event = params[1];
            String context = params[2];

            if (!TextUtils.isEmpty(query)) {
                request.setQuery(query);
            }

            if (!TextUtils.isEmpty(event)) {
                request.setEvent(new AIEvent(event));
            }

            RequestExtras requestExtras = null;
            if (!TextUtils.isEmpty(context)) {
                final List<AIContext> contexts = Collections.singletonList(new AIContext(context));
                requestExtras = new RequestExtras(contexts, null);
            }

            try {
                return aiDataService.request(request, requestExtras);
            } catch (final AIServiceException e) {
                aiError = new AIError(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final AIResponse response) {
            if (response != null) {
                onResult(response);
            } else {
                onError(aiError);
            }
        }
    }


    private void onResult(final AIResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Variables
                gson.toJson(response);
                final Status status = response.getStatus();
                final Result result = response.getResult();
                final String speech = result.getFulfillment().getSpeech();
                final Metadata metadata = result.getMetadata();
                final HashMap<String, JsonElement> params = result.getParameters();

                // Logging
                Log.d(TAG, "onResult");
                Log.i(TAG, "Received success response");
                Log.i(TAG, "Status code: " + status.getCode());
                Log.i(TAG, "Status type: " + status.getErrorType());
                Log.i(TAG, "Resolved query: " + result.getResolvedQuery());
                Log.i(TAG, "Action: " + result.getAction());
                Log.i(TAG, "Speech: " + speech);

                if (metadata != null) {
                    Log.i(TAG, "Intent id: " + metadata.getIntentId());
                    Log.i(TAG, "Intent name: " + metadata.getIntentName());
                }

                if (params != null && !params.isEmpty()) {
                    Log.i(TAG, "Parameters: ");
                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                        Log.i(TAG, String.format("%s: %s",
                                entry.getKey(), entry.getValue().toString()));
                    }
                }


                //Update view to bot says
                final Message receivedMessage = new Message.Builder()
                        .setUser(UniSpeech)
                        .setRightMessage(false)
                        .setMessageText(speech)
                        .build();
                chatView.receive(receivedMessage);
                if (speech.indexOf("Start Calling") !=-1)
                {
                    String callee= speech.substring(speech.lastIndexOf("Start Calling ") + 14);
                    call(callee);

                }
                if (speech.indexOf("Loading...") !=-1)
                {

setFragment(UserListFragment);



                }
                if (speech.indexOf("Searching for") !=-1)
                {
                    String category= speech.substring(speech.lastIndexOf("Searching for ") + 14);



                    Intent intent = new Intent(getApplicationContext(), Profilelists.class);

                    intent.putExtra( "category", category);

                    startActivity(intent);

                }
           /*     if (speech.indexOf("ok...") !=-1)
                {     Bundle arguments = new Bundle();
                     arguments.putString("request", "answer");
                      arguments.putString("username","wafa" );
                    arguments.putString("pair", "ahmed");
                           AccountFragment.setArguments(arguments);
                       android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                      ft.replace(R.id.mainFrame, AccountFragment);
                       ft.commit();



                }*/


            }
        });
    }
    private  void   call(String callee) {
        popup(callee);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    username= mSharedPreferences.getString(Constants.username,"");
        mlanguage= mSharedPreferences.getString(Constants.Language,"");

        Toast.makeText(Welcomeee.this, mlanguage, Toast.LENGTH_SHORT).show();
     SignalingClient.get().initCall(username, callee,mlanguage);

}
    private void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, error.toString());
            }
        });
    }

    private void initChatView() {

        int myId = 0;
        Bitmap icon1 = BitmapFactory.decodeResource(getResources(), R.drawable.chattbot);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
        String myName = "You";
        myAccount = new Users(myId, myName, icon);

        int botId = 1;
        String botName = "UniShop";
        UniSpeech = new Users(botId, botName, icon1);

        chatView = findViewById(R.id.chat_view);
        chatView.setRightBubbleColor(ContextCompat.getColor(this, R.color.primary));
        chatView.setLeftBubbleColor(ContextCompat.getColor(this, R.color.aluminum));
        chatView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        chatView.setSendButtonColor(ContextCompat.getColor(this, R.color.primary));
        chatView.setSendIcon(R.drawable.ic_action_send);

        chatView.setRightMessageTextColor(ContextCompat.getColor(this, R.color.white));
        chatView.setLeftMessageTextColor(ContextCompat.getColor(this, R.color.white));
        chatView.setUsernameTextColor(ContextCompat.getColor(this, R.color.primary));
        chatView.setSendTimeTextColor(ContextCompat.getColor(this, R.color.primary));
        chatView.setDateSeparatorColor(ContextCompat.getColor(this, R.color.primary));
        chatView.setInputTextHint("new message...");

        chatView.setMessageMarginTop(5);
        chatView.setMessageMarginBottom(5);
        chatView.setOnClickSendButtonListener(this);
    }

    private void initService(final LanguageConfig languageConfig) {
        final AIConfiguration.SupportedLanguages lang =
                AIConfiguration.SupportedLanguages.fromLanguageTag(languageConfig.getLanguageCode());
        final AIConfiguration config = new AIConfiguration(languageConfig.getAccessToken(),
                lang,
                AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(this, config);
    }

    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");

        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
    }
    private void loadProfile() {

        try{
            mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getProfile(mEmail)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponse,this::handleError));}
        catch(Exception e)
        {e.printStackTrace();}
    }

    private void handleResponse(User user) {



        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(Constants.username, user.getName());
        editor.putString(Constants.Language, user.getLanguage());

        editor.apply();

       SignalingClient.get().init(this, user.getName());

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
    private void    setFragment(android.support.v4.app.Fragment fragment){
        android.support.v4.app.FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();






    }

    @Override
    public void onCallReceived(JSONObject data) {

        runOnUiThread(() -> {
            String  fromsocketId = data.optString("from");
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                mp = MediaPlayer.create(getApplicationContext(), notification);
                mp.start();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Welcomeee.this, R.style.AlertDialogCustom);
                View view;
                view = LayoutInflater.from(Welcomeee.this).inflate(R.layout.inputcall, null);
                TextView calleeName = view.findViewById(R.id.callee);

                calleeName.setText("Call from " + fromsocketId);
                mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

               mlanguage= mSharedPreferences.getString(Constants.Language,"");
                builder1.setPositiveButton("Answer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Welcomeee.this, "Call accepted", Toast.LENGTH_SHORT).show();
                        SignalingClient.get().AcceptCall(data.optString("to"), data.optString("from") , mlanguage );
                        mp.stop();
                        Intent intent = new Intent(getApplicationContext(), Callactivity.class);

                        intent.putExtra( "request", "answer");
                        intent.putExtra( "username",data.optString("to"));
                        intent.putExtra( "pair", data.optString("from"));
                        intent.putExtra( "mlanguage", mlanguage);
                        intent.putExtra( "TargetLanguage", data.optString("Language"));

                        startActivity(intent);

                    }
                });
                builder1.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Welcomeee.this, "Call canceled", Toast.LENGTH_SHORT).show();
                        SignalingClient.get().CancelCall(data.optString("to"), data.optString("from") , mlanguage );
                        mp.stop();
                    }
                });
                builder1.setView(view);
               builder1.show();


            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }


    @Override
    public void onCallAcepted(JSONObject data) {
       String fromsocketId = data.optString("from");
        String username= data.optString("to");
      Bundle arguments = new Bundle();
        arguments.putString("request", "offer");
        arguments.putString("pair", fromsocketId);
        arguments.putString("username", username);

        Intent intent = new Intent(getApplicationContext(), Callactivity.class);

        intent.putExtra( "request", "offer");
        intent.putExtra( "username",username);
        intent.putExtra( "pair", fromsocketId);
        intent.putExtra( "mlanguage", mlanguage);
        intent.putExtra( "TargetLanguage", data.optString("Language"));
        dialog.dismiss();
        startActivity(intent);

    }
    @Override
    public void onOfferReceived(JSONObject data) {


    }
    @Override
    public void onAnswerReceived(JSONObject data) {

    }
    @Override
    public void onIceCandidateReceived(JSONObject data) {
        Toast.makeText(getApplication(), "Error!", LENGTH_SHORT).show();
    }
    @Override
    public  void onMessageReceived(JSONObject data) {}
    @Override
public void onCallCanceled(JSONObject data){
    dialog.dismiss();
}
    @Override
    public  void onEndCall(JSONObject data){}
}


