package com.ln.whereismyphone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ln.service.MyService;

/**
 * Created by luong on 11/04/2015.
 */
public class LoginActivity extends ActionBarActivity {

    Button login;
    EditText password;
    TextView textView;
    SharedPreferences sharedPreferences;
    String pass;
    SharedPreferences.Editor editor;
    boolean createPass = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        login = (Button) findViewById(R.id.btn_pass);
        password = (EditText) findViewById(R.id.edit_password);
        textView = (TextView) findViewById(R.id.txt_password);
        sharedPreferences = getSharedPreferences(Helper.SHAREPREFERENCE, 4);
        pass = sharedPreferences.getString(Helper.PASSWORD, "");
        editor = sharedPreferences.edit();

        if(pass.length() > 0){
            textView.setText("Enter Password");
        }else{
            createPass = true;
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editPass = password.getText().toString();
                Log.d("editPass", editPass);
                if(pass.length() > 0){
                    if(editPass.equals(pass)){
                        exit();
                    }else{
                        if(createPass){
                            textView.setText("Create new password");
                            pass = "";
                        }else{
                            Toast.makeText(LoginActivity.this, "wrong password", Toast.LENGTH_LONG).show();
                        }
                        password.setText("");

                    }
                }else{
                    password.setText("");
                    textView.setText("Confirm password");
                    pass = editPass;
                }
            }
        });

    }

    public void exit(){
        if(createPass){
            editor.putString(Helper.PASSWORD,pass);
            editor.commit();
        }

        String security = sharedPreferences.getString(Helper.SECURITYPASSWORD, "");
        Intent intent;
        if(security.length() > 0){
            intent = new Intent(this, MainActivity.class);
            Intent service = new Intent(LoginActivity.this, MyService.class);
            stopService(service);

        }else{
            intent = new Intent(this, SecurityInfoActivity.class);
        }

        startActivity(intent);
        this.finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
}