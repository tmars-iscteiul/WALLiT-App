package wallit_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wallit_app.R;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import wallit_app.utilities.Formatter;
import wallit_app.utilities.ServiceMessages;

public class WithdrawActivity extends ToolBarActivity {

    private EditText currentBalanceTextView;
    private EditText withdrawValueInput;
    private GifImageView loadingAnimation;
    private Button withdrawButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw_main);

        toolbar = findViewById(R.id.toolbar);
        setupToolbar();

        currentBalance = getIntent().getDoubleExtra(BindingActivity.USER_BALANCE, -1.0);
        username = getIntent().getStringExtra(LOGIN_USER);

        loadingAnimation = findViewById(R.id.withdraw_button_animation);
        GifDrawable gifDrawable = (GifDrawable) loadingAnimation.getDrawable();
        gifDrawable.setLoopCount(0);
        withdrawButton = findViewById(R.id.withdraw_button);

        currentBalanceTextView = findViewById(R.id.current_balance);
        currentBalanceTextView.setInputType(InputType.TYPE_NULL);
        currentBalanceTextView.setKeyListener(null);
        currentBalanceTextView.setText(Formatter.doubleToEuroString(currentBalance));

        withdrawValueInput = findViewById(R.id.withdraw_value);
    }

    // Called when the user presses the withdraw button
    public void buttonSendWithdrawData(View view)  {
        if(withdrawValueInput.getText().toString().isEmpty())   {
            showMessageDialog("Withdraw value cannot be empty.");
            return;
        }
        loadingAnimation.setVisibility(View.VISIBLE);
        withdrawButton.setVisibility(View.INVISIBLE);
        redirectDataToServer(ServiceMessages.REQUEST_WITHDRAW.getMessageString() + "," + username + "," + withdrawValueInput.getText().toString(), ServiceMessages.REQUEST_WITHDRAW);
        // TODO: Update balance on HomeActivity and WithdrawActivity
    }

    @Override
    protected void handlePositiveAck()    {
        withdrawButton.setVisibility(View.VISIBLE);
        loadingAnimation.setVisibility(View.INVISIBLE);
        currentBalance -= Double.parseDouble(withdrawValueInput.getText().toString());
        currentBalanceTextView.setText(Formatter.doubleToEuroString(currentBalance));
        showMessageDialog("Withdraw operation successful.");
    }

    @Override
    protected void handleNegativeAck()    {
        withdrawButton.setVisibility(View.VISIBLE);
        loadingAnimation.setVisibility(View.INVISIBLE);
        showMessageDialog("Couldn't withdraw.");
    }

    @Override
    protected void handleOfflineAck()    {
        withdrawButton.setVisibility(View.VISIBLE);
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
