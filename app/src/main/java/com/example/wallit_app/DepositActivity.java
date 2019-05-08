package com.example.wallit_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.example.wallit_app.networking.ServiceMessages;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DepositActivity extends ToolBarActivity {

    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        et = findViewById(R.id.depositValue);
        et.setText("0");
    }

    // Called when the user presses the deposit button
    public void buttonSendDepositData(View view)  {
        String value = et.getText().toString();
        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
        Date date = new Date();
        String finalData = "[" + ft.format(date) + "] Client wants to deposit: " + value + "€.";
        redirectDataToServer(finalData);
        progressDialog.setMessage("Depositing...");
        progressDialog.show();
        // TODO: Add a timeout to the progress dialog.
    }

    @Override
    protected void handleAck(ServiceMessages ackCode)   {
        // Do something if needed (most likely not)
        switch(ackCode) {
            case MSG_ACK_POSITIVE:
                handlePositiveAck();
                break;
            case MSG_ACK_NEGATIVE:
                handleNegativeAck();
                break;
            case MSG_OFFLINE_ACK:
                handleOfflineAck();
                break;
            default:
                super.handleAck(ackCode);
                break;
        }
    }

    @Override
    protected void handleDataAck(ServiceMessages ackCode, String data)  {
        // Do something if needed (most likely not)

    }

    private void handlePositiveAck()    {
        progressDialog.hide();
        showMessageDialog("Deposit operation successful.");
    }

    private void handleNegativeAck()    {
        progressDialog.hide();
        showMessageDialog("Couldn't deposit.");
    }

}
