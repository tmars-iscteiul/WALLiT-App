package com.example.wallit_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

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

    public void sendDepositData(View view)  {
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
    protected void handlePositiveAck()    {
        // Positive login confirmation
        progressDialog.hide();
        showMessageDialog("Withdraw operation successful.");
    }

    @Override
    protected void handleNegativeAck()    {
        progressDialog.hide();
        showMessageDialog("Couldn't withdraw.");
    }

}
