package com.en.po.servodoorlock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

public class PasswordActivity extends AppCompatActivity {

    ImageButton sendBtn;
    EditText oldPass;
    EditText newPass;
    EditText secNewPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        sendBtn = (ImageButton) findViewById(R.id.sendPass);
        oldPass = (EditText) findViewById(R.id.enter_old_pass);
        newPass = (EditText) findViewById(R.id.enter_new_pass);
        secNewPass = (EditText) findViewById(R.id.sec_enter_new_pass);
        oldPass.requestFocus();


         sendBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                if(((oldPass.getText().toString().length()==0))||
                        (newPass.getText().toString().length()==0)||
                        (secNewPass.getText().toString().length()==0)){
                    Toast.makeText(getApplicationContext(), R.string.fill_in_fields, Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        if (!PasswordCheck.checkPass(oldPass.getText().toString(),getApplicationContext())){
                            Toast.makeText(getApplicationContext(), R.string.wrong_pass, Toast.LENGTH_SHORT).show();
                        }else {
                            if (!(newPass.getText().toString().equals(secNewPass.getText().toString()))){
                                Toast.makeText(getApplicationContext(), R.string.pass_dont_match, Toast.LENGTH_SHORT).show();
                            }else {

                                String newPassStringHash = PasswordCheck.cryptWithMD5(newPass.getText().toString());
                                MainActivity.sharedPreferences = getSharedPreferences(MainActivity.passwordHash,MODE_PRIVATE);
                                SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
                                editor.putString(MainActivity.passwordHash,newPassStringHash);
                                editor.apply();

//
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }finish();


             }
         });

    }
}
