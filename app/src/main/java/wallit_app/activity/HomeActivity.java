package wallit_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.wallit_app.R;


public class HomeActivity extends ToolBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        host = intent.getStringExtra(CONNECTION_HOST);
        this.username = intent.getStringExtra(LoginActivity.LOGIN_USER);
        TextView textView = findViewById(R.id.welcome_text);
        textView.setText("Welcome " + username + "!");

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
        startActivity(new Intent(this, DepositActivity.class));
    }

    public void buttonGoToWithdrawActivity(View view)   {
        startActivity(new Intent(this, WithdrawActivity.class));
    }

    public void buttonGoToFundInfoActivity(View view)   {
        startActivity(new Intent(this, FundInfoActivity.class));
    }

    public void buttonGoToStatsActivity(View view)   {
        startActivity(new Intent(this, StatsActivity.class));
    }

}
