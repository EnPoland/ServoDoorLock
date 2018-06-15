package com.en.po.servodoorlock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private final String DEVICE_ADDRESS = "98:D3:51:F5:AE:B3"; //MAC Address of Bluetooth Module
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    boolean connected = false;
    String command;

    ImageButton  bluetooth_connect_btn;
    EditText pass;
    ImageButton lock_state_btn;
    ImageButton open_settings;
    static SharedPreferences sharedPreferences;
    final static String passwordHash = "hash";




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            sharedPreferences = getSharedPreferences(passwordHash,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(passwordHash,"2a48a68eed639829a4e2dfe4f44a");
            editor.apply();
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();


        //passwords.add(0,sharedPreferences);

        pass = (EditText) findViewById(R.id.editPass);
        lock_state_btn = (ImageButton) findViewById(R.id.lock_state_btn);
        bluetooth_connect_btn = (ImageButton) findViewById(R.id.bluetooth_connect_btn);
        open_settings = (ImageButton) findViewById(R.id.settings_btn);



        bluetooth_connect_btn.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v){

               if(BTinit())
               {
                   BTconnect(1);
                   command = "3";
                   try
                   {
                       outputStream.write(command.getBytes());
                   }
                   catch (IOException e)
                   {
                       e.printStackTrace();
                   }

               }
           }
        });

        lock_state_btn.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v){

            if(!connected)
            {
                Toast.makeText(getApplicationContext(), R.string.establish, Toast.LENGTH_SHORT).show();
            }
            else
            {
            if (pass.getText().toString().length()==0) {
                Toast.makeText(getApplicationContext(), R.string.enter_pass, Toast.LENGTH_SHORT).show();
            }
            else {
                try {

                    if (PasswordCheck.checkPass(pass.getText().toString(),MainActivity.this)) {
                        command = "1";
                        Toast.makeText(getApplicationContext(), "good", Toast.LENGTH_SHORT).show();
                        try {
                            outputStream.write(command.getBytes()); // Sends the number 1 to the Arduino. For a detailed look at how the resulting command is handled, please see the Arduino Source Code
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else Toast.makeText(getApplicationContext(), R.string.wrong_pass, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            }
           }
        });

        open_settings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (connected) BTconnect(0);

                bluetooth_connect_btn.setImageResource(R.drawable.bluetooth_not);
                Toast.makeText(getApplicationContext(), R.string.pass_change_request, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), PasswordActivity.class);
                startActivity(intent);
            }
        });

    }

    //Initializes bluetooth module
    public boolean BTinit()
    {
        boolean found = false;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null) //Checks if the device supports bluetooth
        {
            Toast.makeText(getApplicationContext(), R.string.doesnt_support, Toast.LENGTH_SHORT).show();
        }

        if(!bluetoothAdapter.isEnabled()) //Checks if bluetooth is enabled. If not, the program will ask permission from the user to enable it
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter,0);

            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if(bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Toast.makeText(getApplicationContext(), R.string.please_pair, Toast.LENGTH_SHORT).show();
        }
        else
        {
            for(BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public boolean BTconnect(int code)  {
        if (code == 1) {
            try {
                socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
                socket.connect();

                Toast.makeText(getApplicationContext(),
                        R.string.succes, Toast.LENGTH_LONG).show();
                bluetooth_connect_btn.setImageResource(R.drawable.bluetooth_yes);
                pass.setVisibility(View.VISIBLE);
                connected = true;
            } catch (IOException e) {
                e.printStackTrace();
                connected = false;
            }

            if (connected) {
                try {
                    outputStream = socket.getOutputStream(); //gets the output stream of the socket
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        }else {
            try {
                socket.close();
                connected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return connected;

    }




    @Override
    protected void onStart()
    {
        super.onStart();
    }
}
