package wallit_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wallit_app.R;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import wallit_app.utilities.Formatter;
import wallit_app.utilities.ServiceMessages;

public class DepositActivity extends ToolBarActivity {

    private EditText currentBalanceTextView;
    private EditText depositValueInput;
    private GifImageView loadingAnimation;
    private Button depositButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit_main);

        toolbar = findViewById(R.id.toolbar);
        setupToolbar();

        currentBalance = getIntent().getDoubleExtra(BindingActivity.USER_BALANCE, -1.0);
        username = getIntent().getStringExtra(LOGIN_USER);

        loadingAnimation = findViewById(R.id.deposit_button_animation);
        GifDrawable gifDrawable = (GifDrawable) loadingAnimation.getDrawable();
        gifDrawable.setLoopCount(0);
        depositButton = findViewById(R.id.deposit_button);

        currentBalanceTextView = findViewById(R.id.current_balance);
        currentBalanceTextView.setInputType(InputType.TYPE_NULL);
        currentBalanceTextView.setKeyListener(null);
        currentBalanceTextView.setText(Formatter.doubleToEuroString(currentBalance));

        depositValueInput = findViewById(R.id.deposit_value);
    }

    // Called when the user presses the deposit button
    public void buttonSendDepositData(View view)  {
        if(depositValueInput.getText().toString().isEmpty())   {
            showMessageDialog("Deposit value cannot be empty.");
            return;
        }
        if(Double.parseDouble(depositValueInput.getText().toString()) <= 0)   {
            showMessageDialog("Deposit value needs to be above 0.");
            return;
        }
        depositButton.setVisibility(View.INVISIBLE);
        loadingAnimation.setVisibility(View.VISIBLE);
        redirectDataToServer(ServiceMessages.REQUEST_DEPOSIT.getMessageString() + "," + username + ","  + depositValueInput.getText().toString(), ServiceMessages.REQUEST_DEPOSIT);
        // TODO: Update balance on HomeActivity and DepositActivity
    }

    @Override
    protected void handlePositiveAck()    {
        depositButton.setVisibility(View.VISIBLE);
        loadingAnimation.setVisibility(View.INVISIBLE);
        currentBalance += Double.parseDouble(depositValueInput.getText().toString());
        currentBalanceTextView.setText(Formatter.doubleToEuroString(currentBalance));
        showMessageDialog("Deposit operation successful.");
    }

    @Override
    protected void handleNegativeAck()    {
        depositButton.setVisibility(View.VISIBLE);
        loadingAnimation.setVisibility(View.INVISIBLE);
        showMessageDialog("Couldn't deposit.");
    }

    @Override
    protected void handleOfflineAck()    {
        depositButton.setVisibility(View.VISIBLE);
        loadingAnimation.setVisibility(View.INVISIBLE);
        super.handleOfflineAck();
    }

    @Override
    public void onBackPressed() {
        System.out.println("Pressed back;");
        Intent intent = new Intent();
        intent.putExtra(USER_BALANCE, ""+currentBalance);
        setResult(RESULT_OK, intent);
        finish();
    }

}
