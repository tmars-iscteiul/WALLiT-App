package wallit_app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;

import com.example.wallit_app.R;
import wallit_app.utilities.ServiceMessages;

/**
 * The main screen of the application, where the user will be able to access the private area, by providing his/her login credentials.
 * @author skner
 */
public class LoginActivity extends BindingActivity {

    private AlertDialog.Builder exitingDialog;
    private EditText usernameInputField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        usernameInputField = findViewById(R.id.username);

        loginOnBind = true;

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                launchConnectionOptionsDialog();
                return true;
            }
        });

        exitingDialog = new AlertDialog.Builder(this);
        exitingDialog.setTitle("Exit Confirmation");
        exitingDialog.setMessage("Are you sure you want to leave the application?");
        exitingDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        exitingDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Allows the service to communicate freely in the current LAN
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    /**
     * Called by pressing the LOGIN button in LoginActivity
     */
    public void buttonLoginUser(View view) {
        if(usernameInputField.getText().toString().isEmpty())   {
            showMessageDialog("Username cannot be empty.");
            return;
        }
        EditText passwordInputField = findViewById(R.id.password);
        if(passwordInputField.getText().toString().isEmpty())   {
            showMessageDialog("Password cannot be empty.");
            return;
        }
        username = usernameInputField.getText().toString();
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        bindToNetworkingService();
        // After it binds, it will send a message to the networking service telling the connection handler to login the user on the server due to the loginOnBind variable.
        // It's important to follow this sequence and only login after the activity is bound to the service, because the service takes time to establish a connection to the server.
        // If the activity attempts to login here, the bound and server connection isn't completed yet.
        // Thus we wait for the binding to be completed, and only then we can be sure that the connection is online.
    }

    /**
     * Launch the options dialog, updates the host variable with the defined text
     */
    public void launchConnectionOptionsDialog()    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connection settings");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(host);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                host = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void handleAck(ServiceMessages ackCode, Object rawData)   {
        switch(ackCode) {
            case MSG_ACK_POSITIVE:
                handlePositiveAck((double)rawData);
                break;
            case MSG_ACK_NEGATIVE:
                handleNegativeAck();
                break;
            case MSG_OFFLINE_ACK:
                // Simulates a positive ack, so the user can "login", but the application knows it's in offline mode, so it won't allow the user to contact the server
                handlePositiveAck(-1.0);
                break;
            case MSG_CONNECTION_TIMEOUT:
                unbindToNetworkingService();
                progressDialog.hide();
                showMessageDialog("Connection to server couldn't be established.");
                break;
            default:
                break;
        }
    }

    /**
     * Called when activity receives a positive ACK from the Networking Service
     * @param balance The balance sent by the server, after receiving the positive login ACK, that will be sent to {@link HomeActivity} to display that balance on screen.
     */
    private void handlePositiveAck(double balance)    {
        userLoggedIn = true;
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(BindingActivity.LOGIN_USER, username);
        intent.putExtra(BindingActivity.CONNECTION_HOST, host);
        intent.putExtra(BindingActivity.USER_BALANCE, balance);
        progressDialog.hide();
        startActivity(intent);
    }

    /**
     * Called when activity receives a negative ACK from the Networking Service
     */
    private void handleNegativeAck()    {
        unbindToNetworkingService();
        progressDialog.hide();
        showMessageDialog("Login failed. Please, try again.");
    }

    @Override
    protected void runAfterConnectedToService()    {}

    /**
     * Overrides the back button's function, to make it ask to exit the app
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            exitingDialog.show();
        }
        return true;
    }

}
