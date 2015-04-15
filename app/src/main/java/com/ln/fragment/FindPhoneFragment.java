package com.ln.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.ln.whereismyphone.Helper;
import com.ln.whereismyphone.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class FindPhoneFragment extends Fragment implements View.OnClickListener{


    Button controller, connect;
    EditText editSecurity;
    TextView txtConnect, txtMessage;
    LinearLayout linearLayout, layoutController;
    String isConnectTo;


    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://128.199.137.159:3000/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    ArrayList<String> listOnlineName = new ArrayList<String>();
    SharedPreferences sharedPreferences;
    String mySecurityCode;
    String securityCode;


    public FindPhoneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(Helper.SHAREPREFERENCE, 4);
        mySecurityCode = sharedPreferences.getString(Helper.SECURITYPASSWORD, "");

        mSocket.on("login_successful", onLogin);
        mSocket.on("messageReceived", onMessageReceive);

        mSocket.on("login_error", onLoginError);

        mSocket.on("online", onOnline);

        mSocket.on("offline", onOffline);

        mSocket.connect();
        mSocket.emit("login", mySecurityCode);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_find_phone, container, false);
        controller = (Button) v.findViewById(R.id.controler);
        connect = (Button) v.findViewById(R.id.btn_connect);
        editSecurity = (EditText) v.findViewById(R.id.edit_security_connect);
        txtConnect = (TextView) v.findViewById(R.id.txt_connect);
        linearLayout = (LinearLayout) v.findViewById(R.id.layout_connect);
        txtMessage = (TextView) v.findViewById(R.id.txt_message);
        layoutController = (LinearLayout) v.findViewById(R.id.layout_controller);



        controller.setOnClickListener(this);
        connect.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == controller.getId()){
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.controller)
                    .items(R.array.list_controller)
                    .itemsCallbackSingleChoice(7, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
                            mSocket.emit("sendMessage", securityCode, text);
                            return true; // allow selection
                        }
                    })
                    .positiveText(R.string.choose)
                    .show();
        }

        if(view.getId() == connect.getId()){
            securityCode = editSecurity.getText().toString();
            if(securityCode.length() > 0){
                if(listOnlineName.contains(securityCode) && !mySecurityCode.equals(securityCode)){
                    txtConnect.setText("Connect to "+ securityCode);
                    linearLayout.setVisibility(View.GONE);
                    layoutController.setVisibility(View.VISIBLE);
                    isConnectTo = securityCode;
                }
                else{
                    Toast.makeText(getActivity(), "This name is not online", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getActivity(), "Cant find anything", Toast.LENGTH_LONG).show();
            }
        }
    }

    private Emitter.Listener onMessageReceive = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final String message = args[1].toString();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                      txtMessage.setText(message);
                }
            });
        }
    };

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray data = (JSONArray) args[0];

                    try {

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            String name = obj.getString("name");
                            listOnlineName.add(name);

                        }

                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

    private Emitter.Listener onLoginError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "login error", Toast.LENGTH_LONG).show();

                }
            });
        }
    };

    private Emitter.Listener onOnline = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String user = args[0].toString();
                    if(!listOnlineName.contains(user)){
                        listOnlineName.add(user);
                    }
           //         Toast.makeText(getActivity(), user + " online", Toast.LENGTH_LONG).show();

                }
            });
        }
    };

    private Emitter.Listener onOffline = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String user = args[0].toString();
                    if(listOnlineName.contains(user)){
                        listOnlineName.remove(user);
                    }

                    if(user.equals(isConnectTo)){
                        txtConnect.setText("Enter your security code of phone lost");
                        linearLayout.setVisibility(View.VISIBLE);
                        layoutController.setVisibility(View.GONE);
                        isConnectTo = "";
                        editSecurity.setText("");
                    }
              //      Toast.makeText(getActivity(), user + " offline", Toast.LENGTH_LONG).show();

                }
            });
        }
    };

}
