package com.example.philipgo.servodoorlock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class PasswordActivity extends AppCompatActivity {

    ImageButton sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
         sendBtn = (ImageButton) findViewById(R.id.sendPass);


         sendBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 finish();
             }
         });

    }
}
