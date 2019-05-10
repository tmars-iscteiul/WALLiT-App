package com.example.wallit_app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.app.AlertDialog;

import com.example.wallit_app.R;
import com.example.wallit_app.networking.ServiceMessages;

public class LoginActivity extends BindingActivity {

    public static final String LOGIN_USER = "com.example.wallit_app.LOGINUSER";
    private AlertDialog.Builder exitingDialog;
    private EditText usernameInputField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        usernameInputField = findViewById(R.id.username);

        loginOnBind = true;

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

        // TODO Replace this with Async tasks for socket connection (Staying like this is a really bad practice)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    // Called by pressing the LOGIN button in LoginActivity
    public void buttonLoginUser(View view) {
        username = usernameInputField.getText().toString();
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        bindToNetworkingService();
        // After it binds, it will send a message to the networking service telling the connection handler to login the user on the server due to the loginOnBind variable.
        // It's important to follow this sequence and only login after the activity is bound to the service, because the service takes time to establish a connection to the server.
        // If the activity attempts to login here, the bound and server connection isn't completed yet.
        // Thus we wait for the binding to be completed, and only then we can be sure that the connection is online.
        // TODO: Add a timeout to the progress dialog.
    }

    // Launch the options dialog, updates the host variable with the defined text
    // TODO Add a host type check (to counter invalid hosts inserted by the user)
    public void launchConnectionOptionsDialog(View view)    {
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
    protected void handleAck(ServiceMessages ackCode)   {
        switch(ackCode) {
            case MSG_ACK_POSITIVE:
                handlePositiveAck();
                break;
            case MSG_ACK_NEGATIVE:
                handleNegativeAck();
                break;
            case MSG_CONNECTION_TIMEOUT:
                handleTimeoutAck();
                break;
            case MSG_OFFLINE_ACK:
                handlePositiveAck();
                break;
            default:
                break;
        }
    }

    @Override
    protected void handleDataAck(ServiceMessages ackCode, String data)  {
        // Do something if needed (most likely not)
    }

    private void handlePositiveAck()    {
        // Positive login confirmation
        userLoggedIn = true;
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(LOGIN_USER, username);
        intent.putExtra(CONNECTION_HOST, host);
        progressDialog.hide();
        startActivity(intent);
    }

    private void handleNegativeAck()    {
        // Negative login confirmation
        unbindToNetworkingService();
        progressDialog.hide();
        showMessageDialog("Login failed. Please, try again.");
    }

    private void handleTimeoutAck()    {
        // Timeout login confirmation
        unbindToNetworkingService();
        progressDialog.hide();
        showMessageDialog("Couldn't connect to the server. Try again later.");
    }

    // Overrides the back button's function, to make it ask to exit the app
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        //Changes 'back' button action
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            exitingDialog.show();
        }
        return true;
    }

}
