package wallit_app.activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wallit_app.R;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import wallit_app.utilities.ServiceMessages;

public class WithdrawActivity extends ToolBarActivity {

    private EditText et;
    private GifImageView loadingAnimation;
    private Button withdrawButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw_main);

        toolbar = findViewById(R.id.toolbar);
        setupToolbar();

        currentBalance = getIntent().getDoubleExtra(BindingActivity.USER_BALANCE, -1.0);

        loadingAnimation = findViewById(R.id.withdraw_button_animation);
        GifDrawable gifDrawable = (GifDrawable) loadingAnimation.getDrawable();
        gifDrawable.setLoopCount(0);
        withdrawButton = findViewById(R.id.withdraw_button);

        et = findViewById(R.id.current_balance);
        et.setInputType(InputType.TYPE_NULL);
        et.setKeyListener(null);
        et.setText("" + currentBalance);

        et = findViewById(R.id.withdraw_value);
    }

    // Called when the user presses the withdraw button
    public void buttonSendWithdrawData(View view)  {
        if(et.getText().toString().isEmpty())   {
            showMessageDialog("Withdraw value cannot be empty.");
            return;
        }
        loadingAnimation.setVisibility(View.VISIBLE);
        withdrawButton.setVisibility(View.INVISIBLE);
        redirectDataToServer(ServiceMessages.REQUEST_WITHDRAW.getMessageString() + "," + et.getText().toString(), ServiceMessages.REQUEST_WITHDRAW);
        // TODO: Add a timeout to the progress dialog.
    }

    @Override
    protected void handlePositiveAck()    {
        withdrawButton.setVisibility(View.VISIBLE);
        loadingAnimation.setVisibility(View.INVISIBLE);
        showMessageDialog("Deposit operation successful.");
    }

    @Override
    protected void handleNegativeAck()    {
        withdrawButton.setVisibility(View.VISIBLE);
        loadingAnimation.setVisibility(View.INVISIBLE);
        showMessageDialog("Couldn't withdraw.");
    }
}
