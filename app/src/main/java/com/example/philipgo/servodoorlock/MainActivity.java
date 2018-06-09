package com.example.philipgo.servodoorlock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Set;
import java.util.UUID;
import android.os.Handler;


public class MainActivity extends AppCompatActivity {

    private final String DEVICE_ADDRESS = "98:D3:51:F5:AE:B3"; //MAC Address of Bluetooth Module
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothDevice device;
    private BluetoothSocket socket;



    private OutputStream outputStream;
    private InputStream inputStream;


    Thread thread;
    byte buffer[];

    boolean stopThread;
    boolean connected = false;
    String command;

    ImageButton  bluetooth_connect_btn;
    EditText pass;
    ImageButton lock_state_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pass = (EditText) findViewById(R.id.editPass);
        lock_state_btn = (ImageButton) findViewById(R.id.lock_state_btn);
        bluetooth_connect_btn = (ImageButton) findViewById(R.id.bluetooth_connect_btn);



        bluetooth_connect_btn.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v){
            pass.setVisibility(View.VISIBLE);
               if(BTinit())
               {
                   BTconnect();


                   // The code below sends the number 3 to the Arduino asking it to send the current state of the door lock so the lock state icon can be updated accordingly

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
                    PasswordCheck check = new PasswordCheck();
                    if (check.checkPass(pass.getText().toString(),MainActivity.this)) {
                        command = "1";
                        Toast.makeText(getApplicationContext(), "good", Toast.LENGTH_SHORT).show();
                        try {
                            outputStream.write(command.getBytes()); // Sends the number 1 to the Arduino. For a detailed look at how the resulting command is handled, please see the Arduino Source Code
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else Toast.makeText(getApplicationContext(), R.string.wrong_pass, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            }
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

    public boolean BTconnect()
    {

        try
        {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
            socket.connect();

            Toast.makeText(getApplicationContext(),
                    R.string.succes, Toast.LENGTH_LONG).show();
            connected = true;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            connected = false;
        }

        if(connected)
        {
            try
            {
                outputStream = socket.getOutputStream(); //gets the output stream of the socket
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                inputStream = socket.getInputStream(); //gets the input stream of the socket
            }
            catch (IOException e)
            {
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
