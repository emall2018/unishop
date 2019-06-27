package com.google.cloud.android.speech;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.language_translator.v3.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.developer_cloud.language_translator.v3.model.TranslationResult;
import com.ibm.watson.developer_cloud.service.security.IamOptions;

import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by ASUS on 17/05/2019.
 */

public  class Callactivity extends AppCompatActivity  implements MessageDialogFragment.Listener , SignalingClient.Callback {
    private final static String CAMERA = Manifest.permission.CAMERA;
    private final static String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    EglBase.Context eglBaseContext;
   String request;
   String TranText;
    PeerConnectionFactory peerConnectionFactory;
    SurfaceViewRenderer localView;
    MediaStream mediaStream;
    List<PeerConnection.IceServer> iceServers;
    String Translation;
    String Subtitles;
    private String username;
    private SharedPreferences mSharedPreferences;
    private String selectedTargetLanguage = "English";
    HashMap<String, PeerConnection> peerConnectionMap;
    SurfaceViewRenderer remoteView;
    private String mToken;
    private String mEmail;
    private  String mlanguage;
    private  String    TargetLanguage;
    private  String fromsocketId;
    private DataChannel datachannel;
     private TextToSpeech convertToSpeech;
     private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";
     private static final String STATE_RESULTS = "results";
     private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private SpeechService mSpeechService;
    private VoiceRecorder mVoiceRecorder;
    private LanguageTranslator translationService;
    private TextToSpeech textService;
    private Locale fortts;
    // Resource caches
    private int mColorHearing;
    private int mColorNotHearing;
    // View references
    private TextView mStatus;
    private TextView mText;
    private Callactivity.ResultAdapter mAdapter;
    private RecyclerView mRecyclerView;
    String socketId;
    String State="";
    String text;
    ImageButton Button;

    public void sendMessage( String message, String state) {




            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        TranslateOptions translateOptions = new TranslateOptions.Builder()
                                .addText(message)
                                .source(mlanguage)
                                .target(TargetLanguage)
                                .build();
                        System.out.println("fifitta" + mlanguage);
                        TranslationResult result = translationService.translate(translateOptions).execute();
                        String firstTranslation = result.getTranslations().get(0).getTranslationOutput();
                        String tosend = state + firstTranslation;
                        ByteBuffer data = stringToByteBuffer(tosend, Charset.defaultCharset());
                       datachannel.send(new DataChannel.Buffer(data, false));
                       // SignalingClient.get().chat(username, socketId,firstTranslation);
                        System.out.println("fifitta" + firstTranslation);
                       //  Toast.makeText(Callactivity.this, "sent", LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    private ByteBuffer stringToByteBuffer(String msg, Charset charset) {
        return ByteBuffer.wrap(msg.getBytes(charset));
    }
    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
            showStatus(true);
            if (mSpeechService != null) {
                mSpeechService.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (mSpeechService != null) {
                mSpeechService.recognize(data, size);
            }
        }

        @Override
        public void onVoiceEnd() {
            showStatus(false);
            if (mSpeechService != null) {
                mSpeechService.finishRecognizing();
            }
        }

    };


    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
            mStatus.setVisibility(View.VISIBLE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }

    };

    private LanguageTranslator initLanguageTranslatorService() {
        IamOptions options = new IamOptions.Builder()
                .apiKey(getString(R.string.language_translator_apikey))
                .build();
        LanguageTranslator service = new LanguageTranslator("2018-05-01", options);
        service.setEndPoint(getString(R.string.language_translator_url));
        return service;
    }



    ///1!
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_activity);
        socketId = getIntent().getStringExtra("pair");

        request = getIntent().getStringExtra("request");
        username = getIntent().getStringExtra("username");
        Toast.makeText(Callactivity.this, "beginning of speech recognition ...", LENGTH_SHORT).show();
        String bb = getIntent().getStringExtra("mlanguage");
        mlanguage = SupportedLanguages.fromLabel(bb);

       // mlanguage="French";
        String aa=getIntent().getStringExtra("TargetLanguage");
        TargetLanguage =SupportedLanguages.fromLabel(aa );
        // Disconnecting..
        Button = findViewById(R.id.disconnect);
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                disconnect();

            }
        });

      //  TargetLanguage= "English";
        fortts=SupportedLanguages.totts(bb );
        SignalingClient.get().init(this, username);

        remoteView = findViewById(R.id.fullscreen_video_view);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");

        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Translation = mSharedPreferences.getString(Constants.Subtitles,"");
if (Translation.equals("ON") && ! mlanguage.equalsIgnoreCase(TargetLanguage))
{
    CardView subtitlees= findViewById(R.id.card);
    subtitlees.setVisibility(View.VISIBLE);
}
       // Toast.makeText(Callactivity.this, Translation, LENGTH_SHORT).show();
        translationService = initLanguageTranslatorService();
        ///////////////////////////////////

        final Resources resources = getResources();
        final Resources.Theme theme = getTheme();
        mColorHearing = ResourcesCompat.getColor(resources, R.color.status_hearing, theme);
        mColorNotHearing = ResourcesCompat.getColor(resources, R.color.status_not_hearing, theme);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        mStatus = (TextView) findViewById(R.id.status);
        mText = (TextView) findViewById(R.id.text);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ArrayList<String> results = savedInstanceState == null ? null :
                savedInstanceState.getStringArrayList(STATE_RESULTS);
        mAdapter = new Callactivity.ResultAdapter(results);
        mRecyclerView.setAdapter(mAdapter);
        //////////////////////////////////////////////////////////////
        if(ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] {CAMERA, RECORD_AUDIO }, 100);
        else


            start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && grantResults.length > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                start();
            }
            else
                showPermissionMessageDialog();
            Toast.makeText(this, "Please check your permissions...", LENGTH_SHORT).show();
        }
    }

    private void disconnect()
    {
        peerConnectionMap.clear();

        peerConnectionFactory= null;
        SignalingClient.get().endCall(username, socketId);
        SignalingClient.get().destroy();
        localView.clearImage();
        localView = null;
        remoteView = null;
        eglBaseContext = null;
        remoteView.init(eglBaseContext, null);
        localView.init(eglBaseContext, null);
        mediaStream = null;
        Intent intent = new Intent(getApplicationContext(), Welcomeee.class);


        startActivity(intent);
        finish();
    }

    private void start() {
        peerConnectionMap = new HashMap<>();
        iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());
        PeerConnection.IceServer turnpeerIceServer = PeerConnection.IceServer.builder("turn:192.168.220.130")
                .setUsername("wafakallel")
                .setPassword("123456")
                .createIceServer();
        iceServers.add(turnpeerIceServer);

        eglBaseContext = EglBase.create().getEglBaseContext();

        // create PeerConnectionFactory
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions
                .builder(this)
                .createInitializationOptions());
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        DefaultVideoEncoderFactory defaultVideoEncoderFactory =
                new DefaultVideoEncoderFactory(eglBaseContext, true, true);
        DefaultVideoDecoderFactory defaultVideoDecoderFactory =
                new DefaultVideoDecoderFactory(eglBaseContext);
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory();

        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext);
        // create VideoCapturer
        VideoCapturer videoCapturer = createCameraCapturer(true);
        VideoSource videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), videoSource.getCapturerObserver());
        videoCapturer.startCapture(480, 640, 30);

        localView = findViewById(R.id.pip_video_view);

        remoteView .setMirror(false);
        remoteView.init(eglBaseContext, null);
        // create VideoTrack
        localView.setMirror(true);
        localView.init(eglBaseContext, null);
        remoteView.setZOrderOnTop(false);
        localView.setZOrderOnTop(true);
        localView.setZOrderMediaOverlay(true);

        VideoTrack videoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);
//        // display in localView
        videoTrack.addSink(localView);
        ///////////// AudioTrack????*/////////////////////
        MediaConstraints constraints = new MediaConstraints();
        mediaStream = peerConnectionFactory.createLocalMediaStream("mediaStream");
        if (mlanguage.equalsIgnoreCase(TargetLanguage)) {
            AudioSource audioSource = peerConnectionFactory.createAudioSource(constraints);
             AudioTrack localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);
            //////***********************
            mediaStream.addTrack(localAudioTrack);}
            mediaStream.addTrack(videoTrack);

            if ("offer".equalsIgnoreCase(request)) {
                call(socketId);
            }

    }
    private void  call(String socketId){

        PeerConnection peerConnection = getOrCreatePeerConnection(socketId);
        peerConnection.createOffer(new SdpAdapter("createOfferSdp:" + socketId) {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);
                peerConnection.setLocalDescription(new SdpAdapter("setLocalSdp:" + socketId), sessionDescription);
                SignalingClient.get().sendSessionDescription(sessionDescription, socketId, username, TargetLanguage);
            }
        }, new MediaConstraints());
    }




    private synchronized PeerConnection getOrCreatePeerConnection(String socketId) {
        PeerConnection peerConnection = peerConnectionMap.get(socketId);
        if(peerConnection != null) {
            return peerConnection;
        }
        peerConnection = peerConnectionFactory.createPeerConnection(iceServers, new PeerConnectionAdapter("PC:" + socketId) {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                SignalingClient.get().sendIceCandidate(iceCandidate, socketId, username);
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);

                runOnUiThread(() -> {
                    remoteVideoTrack.addSink(remoteView);
                });
            }
            @Override
            public void onDataChannel(DataChannel dataChannel) {


                dataChannel.registerObserver(new DataChannelAdapter() {
                    @Override
                    public void onBufferedAmountChange(long l) {

                    }
                    private String TAG="Data";
                    @Override
                    public void onStateChange() {
                        Log.d(TAG, "onStateChange: remote data channel state: " + dataChannel.state().toString());
                    }

                    @Override
                    public void onMessage(DataChannel.Buffer buffer) {
                        Log.d(TAG, "onMessage: got message");

                        String message = byteBufferToString(buffer.data, Charset.defaultCharset());
char Statee=   message.charAt(0);
String msgg=  message.substring(1);
System.out.println(msgg);
                        runOnUiThread(new Runnable() {
                            public void run() {

                                 System.out.println("yaRabbi"+ TranText);

                                mText.setText(msgg);



                        }});
if (Statee == 'T'){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    convertToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                                        @Override
                                        public void onInit(int status) {
                                            if (status != TextToSpeech.ERROR) {
                                                convertToSpeech.setLanguage(fortts);
                                                convertToSpeech.speak(msgg, TextToSpeech.QUEUE_FLUSH, null, null);
                                            }
                                        }
                                    });

                                    /*SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                                            .text(message)
                                            .voice(SynthesizeOptions.Voice.EN_US_LISAVOICE)
                                            .accept(SynthesizeOptions.Accept.AUDIO_WAV)
                                            .build();
                                    player.playStream(textService.synthesize(synthesizeOptions).execute());*/

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                    }
                    private String byteBufferToString(ByteBuffer buffer, Charset charset) {
                        byte[] bytes;
                        if (buffer.hasArray()) {
                            bytes = buffer.array();
                        } else {
                            bytes = new byte[buffer.remaining()];
                            buffer.get(bytes);
                        }
                        return new String(bytes, charset);
                    }
                });

            }



        });

        DataChannel . Init init = new   DataChannel.Init ( ) ;
        datachannel= peerConnection.createDataChannel ( "dataChannel" , init ) ;
        peerConnection.addStream(mediaStream);
        peerConnectionMap.put(socketId, peerConnection);
        return peerConnection;
    }

    @Override
    public void onOfferReceived(JSONObject data) {


            fromsocketId = data.optString("from");



                                PeerConnection peerConnection = getOrCreatePeerConnection(fromsocketId);

                                peerConnection.setRemoteDescription(new SdpAdapter("setRemoteSdp:" + fromsocketId),
                                        new SessionDescription(SessionDescription.Type.OFFER, data.optString("sdp")));
                                peerConnection.createAnswer(new SdpAdapter("localAnswerSdp") {

                                    @Override
                                    public void onCreateSuccess(SessionDescription sdp) {

                                        super.onCreateSuccess(sdp);
                                        peerConnectionMap.get(fromsocketId).setLocalDescription(new SdpAdapter("setLocalSdp:" + fromsocketId), sdp);
                                        SignalingClient.get().sendSessionDescription(sdp, fromsocketId, username, mlanguage);
                                    }
                                }, new MediaConstraints());



                        }






    @Override
    public void onAnswerReceived(JSONObject data) {
        String socketId = data.optString("from");
        selectedTargetLanguage= data.optString("language");
        PeerConnection peerConnection = getOrCreatePeerConnection(socketId);
        peerConnection.setRemoteDescription(new SdpAdapter("setRemoteSdp:" + socketId),
                new SessionDescription(SessionDescription.Type.ANSWER, data.optString("sdp")));
    }
    @Override
    public void onIceCandidateReceived(JSONObject data) {
        String socketId = data.optString("from");
        selectedTargetLanguage= data.optString("language");

        PeerConnection peerConnection = getOrCreatePeerConnection(socketId);
        peerConnection.addIceCandidate(new IceCandidate(
                data.optString("id"),
                data.optInt("label"),
                data.optString("candidate")
        ));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
        SignalingClient.get().destroy();
    }
    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(Callactivity.this,mlanguage+TargetLanguage  , LENGTH_SHORT).show();
      if (!mlanguage.equalsIgnoreCase(TargetLanguage)) {
            // Prepare Cloud Speech API
             Toast.makeText(Callactivity.this, "begining of speech recognition ...", LENGTH_SHORT).show();
            bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);

            // Start listening to voices
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecorder();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                showPermissionMessageDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_AUDIO_PERMISSION);
            }
        }
   }
   private VideoCapturer createCameraCapturer(boolean isFront) {
       Camera1Enumerator enumerator = new Camera1Enumerator(false);
       final String[] deviceNames = enumerator.getDeviceNames();

       // First, try to find front facing camera
       for (String deviceName : deviceNames) {
           if (isFront ? enumerator.isFrontFacing(deviceName) : enumerator.isBackFacing(deviceName)) {
               VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

               if (videoCapturer != null) {
                   return videoCapturer;
               }
           }
       }

       return null;
   }
    @Override
    protected void onStop() {
        // Stop listening to voice
        if (!mlanguage.equalsIgnoreCase(TargetLanguage)) {
            stopVoiceRecorder();

            // Stop Cloud Speech API
            mSpeechService.removeListener(mSpeechServiceListener);
            unbindService(mServiceConnection);
            mSpeechService = null;

            super.onStop();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            outState.putStringArrayList(STATE_RESULTS, mAdapter.getResults());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_file:
                mSpeechService.recognizeInputStream(getResources().openRawResource(R.raw.audio));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }



    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }

    private void showPermissionMessageDialog() {
        MessageDialogFragment
                .newInstance(getString(R.string.permission_message))
                .show(getSupportFragmentManager(), FRAGMENT_MESSAGE_DIALOG);
    }

    private void showStatus(final boolean hearingVoice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatus.setTextColor(hearingVoice ? mColorHearing : mColorNotHearing);
            }
        });
    }

    @Override
    public void onMessageDialogDismissed() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private final SpeechService.Listener mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                    }
                    if (mText != null && !TextUtils.isEmpty(text)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                              //      mText.setText(null);
                                  //  mAdapter.addResult(text);
                                //    mRecyclerView.smoothScrollToPosition(0);
                                 //   String msg= "T"+ text;
                                   sendMessage(text,"T" );
                                   // System.out.println(msg);
                                  //  sendMessage(text);
                                } else {
                               //   mText.setText(text);
                                 //   sendMessage(text);
                                //   String msg= "F"+ text;
                                   // System.out.println(msg);
                                    sendMessage(text ,"F");
                                }
                            }
                        });
                    }
                }
            };

    private static class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_result, parent, false));
            text = (TextView) itemView.findViewById(R.id.text);
        }

    }

    private static class ResultAdapter extends RecyclerView.Adapter<Callactivity.ViewHolder> {

        private final ArrayList<String> mResults = new ArrayList<>();

        ResultAdapter(ArrayList<String> results) {
            if (results != null) {
                mResults.addAll(results);
            }
        }

        @Override
        public Callactivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Callactivity.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(Callactivity.ViewHolder holder, int position) {
            holder.text.setText(mResults.get(position));
        }

        @Override
        public int getItemCount() {
            return mResults.size();
        }

        void addResult(String result) {
            mResults.add(0, result);
            notifyItemInserted(0);
        }

        public ArrayList<String> getResults() {
            return mResults;
        }

    }
    @Override
    public void onMessageReceived(JSONObject data) {

      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String msg = data.optString("message");
                    convertToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                convertToSpeech.setLanguage(Locale.ENGLISH);
                                convertToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();;*/
    }
    @Override
    public void onCallReceived(JSONObject data) {}
    @Override
    public void onCallAcepted(JSONObject data) {


    }
    @Override
    public  void onCallCanceled(JSONObject data){}
    @Override
    public  void onEndCall(JSONObject data){
        disconnect();
    }
}
