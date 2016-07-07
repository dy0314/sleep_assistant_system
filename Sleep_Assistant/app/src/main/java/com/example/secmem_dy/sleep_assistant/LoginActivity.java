package com.example.secmem_dy.sleep_assistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by SECMEM-DY on 2016-07-07.
 */

public class LoginActivity extends Activity {
    private EditText idInput, passwordInput;
    private CheckBox autoLogin;
    private Button loginButton;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check auto login
        setContentView(R.layout.login);
        idInput=(EditText)findViewById(R.id.idinput);
        passwordInput=(EditText)findViewById(R.id.passwordInput);
        loginButton=(Button)findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check id,pswd
                Intent intent = new Intent(getApplicationContext(),AlarmSettingActivity.class);
                startActivity(intent);
            }
        });
    }
}
