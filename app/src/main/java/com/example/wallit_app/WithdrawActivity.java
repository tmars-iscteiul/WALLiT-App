package com.example.wallit_app;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WithdrawActivity extends ToolBarActivity {

    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        et = findViewById(R.id.withdrawValue);
        et.setText("0");
    }

    public void sendWithdrawData(View view)  {
        String value = et.getText().toString();
        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
        Date date = new Date();
        String finalData = "[" + ft.format(date) + "] Client wants to withdraw: " + value + "€.";
        redirectDataToServer(finalData);
        progressDialog.setMessage("Withdrawing...");
        progressDialog.show();
        // TODO: Add a timeout to the progress dialog.
    }

    @Override
    protected void handlePositiveAck()    {
        // Positive login confirmation
        progressDialog.hide();
        showMessageDialog("Deposit operation successful.");
    }

    @Override
    protected void handleNegativeAck()    {
        progressDialog.hide();
        showMessageDialog("Couldn't deposit.");
    }
}
