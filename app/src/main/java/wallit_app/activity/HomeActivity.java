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

    private TextView topMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        toolbar = findViewById(R.id.toolbar);
        setupToolbar();

        Intent intent = getIntent();
        host = intent.getStringExtra(BindingActivity.CONNECTION_HOST);
        username = intent.getStringExtra(BindingActivity.LOGIN_USER);
        currentBalance = intent.getDoubleExtra(BindingActivity.USER_BALANCE, 0);
        getSupportActionBar().setTitle("Welcome " + username);
        topMessageView = findViewById(R.id.current_value_text);
        topMessageView.setText("You currently have " + Formatter.doubleToEuroString(currentBalance) + " in the WALLiT Fund.");

        if(!host.equals("offline"))
            Toast.makeText(this, "Connected to server.", Toast.LENGTH_SHORT).show();
        else
            showMessageDialog("You are using the application in OFFLINE mode.");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                currentBalance = Double.parseDouble(data.getStringExtra(USER_BALANCE));
                topMessageView.setText("You currently have " + Formatter.doubleToEuroString(currentBalance) + " in the WALLiT Fund.");
            }
        }
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
        intent.putExtra(LOGIN_USER, username);
        startActivityForResult(intent, 1);
    }

    public void buttonGoToWithdrawActivity(View view)   {
        Intent intent = new Intent(this, WithdrawActivity.class);
        intent.putExtra(USER_BALANCE, currentBalance);
        intent.putExtra(LOGIN_USER, username);
        startActivityForResult(intent, 1);
    }

    public void buttonGoToFundInfoActivity(View view)   {
        Intent intent = new Intent(this, FundInfoActivity.class);
        intent.putExtra(LOGIN_USER, username);
        startActivity(intent);
    }

    public void buttonGoToStatsActivity(View view)   {
        Intent intent = new Intent(this, StatsActivity.class);
        intent.putExtra(LOGIN_USER, username);
        startActivity(intent);
    }

}
