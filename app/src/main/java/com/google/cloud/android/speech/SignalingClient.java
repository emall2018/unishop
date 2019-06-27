package com.google.cloud.android.speech;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.net.URISyntaxException;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by chao on 2019/1/30.
 */

public class SignalingClient {

    private  String username ;
     public String Clientid;
    private static SignalingClient instance;
    private SignalingClient(){}
    public static SignalingClient get() {
        if(instance == null) {
            synchronized (SignalingClient.class) {
                if(instance == null) {
                    instance = new SignalingClient();
                }
            }
        }
        return instance;
    }

    private Socket socket;
    private String room = "OldPlace";
    private Callback callback;



    public void init(Callback callback, String username) {
        this.callback = callback;
        System.out.println("wafa");
        username= username;
        try {


            socket = IO.socket("https://translateappletalk.herokuapp.com/");
       //     socket = IO.socket("https://translateappletalk.herokuapp.com/");
            socket.connect();

            socket.emit("username", username);


            socket.on("joined", args -> {
                Log.e("chao", "joined:" + socket.id());

            });
            socket.on("log", args -> {
                Log.e("chao", "log call " + Arrays.toString(args));
            });
            socket.on("bye", args -> {
                Log.e("chao", "bye " + args[0]);

            });
            socket.on("message", args -> {
                Log.e("chao", "message " + Arrays.toString(args));
                Object arg = args[0];
                if(arg instanceof String) {

                } else if(arg instanceof JSONObject) {
                    JSONObject data = (JSONObject) arg;
                    String type = data.optString("type");
                    if("offer".equals(type)) {


                        callback.onOfferReceived(data);
                    } else if("answer".equals(type)) {
                        callback.onAnswerReceived(data);
                    } else if("candidate".equals(type)) {
                        callback.onIceCandidateReceived(data);
                    }
                    else if("initcall".equals(type)) {
                        callback.onCallReceived(data);
                    }
                    else if("acceptcall".equals(type)) {
                        callback.onCallAcepted(data);
                    } if("chat".equals(type)) {


                        callback.onMessageReceived(data);
                    }
                    if("endCall".equals(type)) {


                        callback.onEndCall(data);
                    }
                    else if("CancelCall".equals(type)) {
                        callback.onCallCanceled(data);
                    }
                }
            });
        }
         catch (URISyntaxException e) {
            e.printStackTrace();
            System.out.println("error");
        }
    }

    public void destroy() {

        socket.disconnect();
        socket.close();
        instance = null;
    }




    public  void chat(String username, String callee, String message)
    {
        JSONObject jo = new JSONObject();
        try {
            jo.put("type", "chat");
            jo.put("message", message);
            jo.put("from", username);
            jo.put("to", callee);

            socket.emit("message", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

public  void AcceptCall(String username, String caller,String Language)
{
    JSONObject jo = new JSONObject();
    try {
        jo.put("type", "acceptcall");

        jo.put("from", username);
        jo.put("to", caller);
        jo.put("Language", Language);
        socket.emit("message", jo);

    } catch (JSONException e) {
        e.printStackTrace();
    }

}
    public  void CancelCall(String username, String caller,String Language)
    {
        JSONObject jo = new JSONObject();
        try {
            jo.put("type", "CancelCall");

            jo.put("from", username);
            jo.put("to", caller);
            jo.put("Language", Language);
            socket.emit("message", jo);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void sendIceCandidate(IceCandidate iceCandidate, String to, String username) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("type", "candidate");
            jo.put("label", iceCandidate.sdpMLineIndex);
            jo.put("id", iceCandidate.sdpMid);
            jo.put("candidate", iceCandidate.sdp);
            jo.put("from",   username);
            jo.put("to", to);


            socket.emit("message", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendSessionDescription(SessionDescription sdp, String to, String username, String Language) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("type", sdp.type.canonicalForm());
            jo.put("sdp", sdp.description);
            jo.put("from", username);
            jo.put("to", to);
            jo.put("Language", Language);
            socket.emit("message", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
public  void initCall(String username, String callee, String Language)
{
    JSONObject jo = new JSONObject();
    try {
        jo.put("type", "initcall");

        jo.put("from", username);
        jo.put("to", callee);
        jo.put("Language", Language);
        socket.emit("message", jo);
    } catch (JSONException e) {
        e.printStackTrace();
    }

}

    public  void endCall(String username, String callee)
    {
        JSONObject jo = new JSONObject();
        try {
            jo.put("type", "endCall");

            jo.put("from", username);
            jo.put("to", callee);

            socket.emit("message", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public interface Callback {

        void onMessageReceived (JSONObject data);
        void onCallReceived(JSONObject data);
void onCallAcepted(JSONObject data);
       void onOfferReceived(JSONObject data);
      void onAnswerReceived(JSONObject data);
       void onIceCandidateReceived(JSONObject data);
       void onCallCanceled(JSONObject data);
       void onEndCall(JSONObject data);
    }

}
