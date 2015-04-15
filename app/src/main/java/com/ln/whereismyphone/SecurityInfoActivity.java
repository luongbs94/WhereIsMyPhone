package com.ln.whereismyphone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by luong on 11/04/2015.
 */
public class SecurityInfoActivity extends ActionBarActivity{

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    EditText phone, gmail, securityCode;
    Button save;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_layout);
        setTitle("Security Info");
        sharedPreferences = getSharedPreferences(Helper.SHAREPREFERENCE,4);
        editor = sharedPreferences.edit();

        save = (Button) findViewById(R.id.save_security);
        phone = (EditText) findViewById(R.id.phone);
        gmail = (EditText) findViewById(R.id.gmail);
        securityCode = (EditText) findViewById(R.id.security_code);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_phone = phone.getText().toString();
                String str_gmail = gmail.getText().toString();
                String str_secutity_code = securityCode.getText().toString();

                if(str_phone.length() > 0 && str_gmail.length() > 0 && str_secutity_code.length() > 0){
                    Toast.makeText(SecurityInfoActivity.this, "Save", Toast.LENGTH_LONG).show();
                    editor.putString(Helper.SECURITYPASSWORD, str_secutity_code);
                    editor.putString(Helper.PHONE,str_gmail);
                    editor.putString(Helper.GMAIL, str_phone);
                    editor.commit();
                    Intent intent = new Intent(SecurityInfoActivity.this, MainActivity.class);
                    startActivity(intent);
                    SecurityInfoActivity.this.finish();

                }else{
                    Toast.makeText(SecurityInfoActivity.this, "Please fill all info", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
