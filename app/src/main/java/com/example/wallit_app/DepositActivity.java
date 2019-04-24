package com.example.wallit_app;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wallit_app.networking.DataSending;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.Socket;

public class DepositActivity extends ToolBarBasedActivity {

    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        et = findViewById(R.id.depositValue);;
        et.setText("0");
    }


    public void sendDepositData(View view)  {
        String value = et.getText().toString();
        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
        Date date = new Date();
        String finalData = "[" + ft.format(date) + "] Client wants to deposit: " + value + "€.";
        System.out.println("Want to send: " + finalData);
        new DataSending(finalData).start();
    }

}
