package wallit_app.activity;

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

public class DepositActivity extends ToolBarActivity {

    private EditText et;
    private GifImageView loadingAnimation;
    private Button depositButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit_main);

        toolbar = findViewById(R.id.toolbar);
        setupToolbar();

        currentBalance = getIntent().getDoubleExtra(BindingActivity.USER_BALANCE, -1.0);

        loadingAnimation = findViewById(R.id.deposit_button_animation);
        GifDrawable gifDrawable = (GifDrawable) loadingAnimation.getDrawable();
        gifDrawable.setLoopCount(0);
        depositButton = findViewById(R.id.deposit_button);

        et = findViewById(R.id.current_balance);
        et.setInputType(InputType.TYPE_NULL);
        et.setKeyListener(null);
        et.setText(Formatter.doubleToEuroString(currentBalance));

        et = findViewById(R.id.deposit_value);
    }

    // Called when the user presses the deposit button
    public void buttonSendDepositData(View view)  {
        // TODO Make sure can't send empty value to server
        if(et.getText().toString().isEmpty())   {
            showMessageDialog("Deposit value cannot be empty.");
            return;
        }
        depositButton.setVisibility(View.INVISIBLE);
        loadingAnimation.setVisibility(View.VISIBLE);
        redirectDataToServer(ServiceMessages.REQUEST_DEPOSIT.getMessageString() + "," + et.getText().toString(), ServiceMessages.REQUEST_DEPOSIT);
        // TODO: Add a timeout to the progress dialog.
    }

    @Override
    protected void handlePositiveAck()    {
        depositButton.setVisibility(View.VISIBLE);
        loadingAnimation.setVisibility(View.INVISIBLE);
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

}
