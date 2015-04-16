package com.ln.whereismyphone;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by luong on 15/04/2015.
 */
public class ChatActivity extends ActionBarActivity {

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://128.199.137.159:3000/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    String guest;
    ListView listView;
    EditText message;
    Button send;
    ArrayList<String> listMessage = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Message");
        setContentView(R.layout.layout_chat);

        guest = getIntent().getExtras().getString("guest");

        listView = (ListView) findViewById(R.id.list_message);
        message = (EditText) findViewById(R.id.edt_chat_message);
        send = (Button) findViewById(R.id.btn_send_message);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtMessage = message.getText().toString();
                mSocket.emit("sendMessage", guest, txtMessage);
                listMessage.add("Me : " + txtMessage);
                message.setText("");
                adapter.notifyDataSetChanged();


            }
        });

        mSocket.on("messageReceived", onMessageReceive);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listMessage);
        listView.setAdapter(adapter);

    }

    private Emitter.Listener onMessageReceive = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final String txtMessage1 = args[1].toString();
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listMessage.add("Guest : " + txtMessage1);
                    adapter.notifyDataSetChanged();
                    if(txtMessage1.equals("END")){
                        ChatActivity.this.finish();
                    }
                }
            });
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.emit("sendMessage", guest, "END");
    }
}
