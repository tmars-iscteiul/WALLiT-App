package wallit_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallit_app.R;

import wallit_app.utilities.Formatter;

/**
 * The home activity is the main screen the user will see after he logs in to the app. From here, he can choose to go to any other main screen.
 * @see DepositActivity
 * @see WithdrawActivity
 * @see FundInfoActivity
 * @see StatsActivity
 * @author skner
 */
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

        if(host.equals("offline"))
            showMessageDialog("You are using the application in OFFLINE mode.");
    }

    /**
     * Sent after deposit/withdraw activities are done, updating the displayed balance if needed.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                currentBalance = Double.parseDouble(data.getStringExtra(USER_BALANCE));
                topMessageView.setText("You currently have " + Formatter.doubleToEuroString(currentBalance) + " in the WALLiT Fund.");
            }
        }
    }

    /**
     * Changes 'back' button action.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)   {

        if(keyCode==KeyEvent.KEYCODE_BACK)
            logoutDialog.show();
        return true;
    }

    /**
     * Called when the user presses the deposit button.
     */
    public void buttonGoToDepositActivity(View view)    {
        Intent intent = new Intent(this, DepositActivity.class);
        intent.putExtra(USER_BALANCE, currentBalance);
        intent.putExtra(LOGIN_USER, username);
        startActivityForResult(intent, 1);
    }

    /**
     * Called when the user presses the withdraw button.
     */
    public void buttonGoToWithdrawActivity(View view)   {
        Intent intent = new Intent(this, WithdrawActivity.class);
        intent.putExtra(USER_BALANCE, currentBalance);
        intent.putExtra(LOGIN_USER, username);
        startActivityForResult(intent, 1);
    }

    /**
     * Called when the user presses the fund info button.
     */
    public void buttonGoToFundInfoActivity(View view)   {
        Intent intent = new Intent(this, FundInfoActivity.class);
        intent.putExtra(LOGIN_USER, username);
        startActivity(intent);
    }

    //

    /**
     * Called when the user presses the stats button.
     */
    public void buttonGoToStatsActivity(View view)   {
        Intent intent = new Intent(this, StatsActivity.class);
        intent.putExtra(LOGIN_USER, username);
        startActivity(intent);
    }

}
