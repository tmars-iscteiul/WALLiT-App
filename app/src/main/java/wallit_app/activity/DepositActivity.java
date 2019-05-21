package wallit_app.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.example.wallit_app.R;
import wallit_app.utilities.ServiceMessages;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DepositActivity extends ToolBarActivity {

    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit_main);

        toolbar = findViewById(R.id.toolbar);
        setupToolbar();

        et = findViewById(R.id.depositValue);
        et.setText("0");
    }

    // Called when the user presses the deposit button
    public void buttonSendDepositData(View view)  {
        if(et.getText().toString().isEmpty())   {
            showMessageDialog("Deposit value cannot be empty.");
            return;
        }
        redirectDataToServer(ServiceMessages.REQUEST_DEPOSIT.getMessageString() + "," + et.getText().toString(), ServiceMessages.REQUEST_DEPOSIT);
        progressDialog.setMessage("Depositing...");
        progressDialog.show();
        // TODO: Add a timeout to the progress dialog.
    }

    @Override
    protected void handleAck(ServiceMessages ackCode)   {
        // Do something if needed (most likely not)
        switch(ackCode) {

            default:
                super.handleAck(ackCode);
                break;
        }
    }

    @Override
    protected void handlePositiveAck()    {
        progressDialog.hide();
        showMessageDialog("Deposit operation successful.");
    }

    @Override
    protected void handleNegativeAck()    {
        progressDialog.hide();
        showMessageDialog("Couldn't deposit.");
    }

}
