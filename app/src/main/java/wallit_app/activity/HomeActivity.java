package wallit_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallit_app.R;

import wallit_app.utilities.Formatter;

public class HomeActivity extends ToolBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        toolbar = findViewById(R.id.toolbar);
        setupToolbar();

        Intent intent = getIntent();
        host = intent.getStringExtra(BindingActivity.CONNECTION_HOST);
        this.username = intent.getStringExtra(BindingActivity.LOGIN_USER);
        getSupportActionBar().setTitle("Welcome " + username);
        double currentBalanceAux = intent.getDoubleExtra(BindingActivity.USER_BALANCE, 0);
        // TODO Add a way to change this every time user deposits a value.
        TextView tv = findViewById(R.id.current_value_text);
        if(currentBalanceAux == -1.0)
            tv.setText("You currently have " + "null" + " in the WALLiT Fund.");
        else    {
            currentBalance = currentBalanceAux;
            tv.setText("You currently have " + Formatter.doubleToEuroString(currentBalance) + " in the WALLiT Fund.");
        }

        if(!host.equals("offline"))
            Toast.makeText(this, "Connected to server.", Toast.LENGTH_SHORT).show();
        else
            showMessageDialog("You are using the application in OFFLINE mode.");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        //Changes 'back' button action
        if(keyCode==KeyEvent.KEYCODE_BACK)
            logoutDialog.show();
        return true;
    }


    public void buttonGoToDepositActivity(View view)    {
        Intent intent = new Intent(this, DepositActivity.class);
        intent.putExtra(USER_BALANCE, currentBalance);
        startActivity(intent);
    }

    public void buttonGoToWithdrawActivity(View view)   {
        Intent intent = new Intent(this, WithdrawActivity.class);
        intent.putExtra(USER_BALANCE, currentBalance);
        startActivity(intent);
    }

    public void buttonGoToFundInfoActivity(View view)   {
        startActivity(new Intent(this, FundInfoActivity.class));
    }

    public void buttonGoToStatsActivity(View view)   {
        startActivity(new Intent(this, StatsActivity.class));
    }

}
