package com.example.wallit_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.app.AlertDialog;

public class LoginActivity extends BindingActivity {

    public static final String LOGIN_USER = "com.example.wallit_app.LOGINUSER";
    private AlertDialog.Builder exitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

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
        EditText editText = findViewById(R.id.username);
        username = editText.getText().toString();
        bindToNetworkingService();
        // After it binds, it will send a message to the networking service telling the connection handler to login the user on the server due to the loginOnBind variable.
        // It's important to follow this sequence and only login after the activity is bound to the service, because the service takes time to establish a connection to the server.
        // If the activity attempts to login here, the bound and server connection isn't completed yet.
        // Thus we wait for the binding to be completed, and only then we can be sure that the connection is online.
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        // TODO: Add a timeout to the progress dialog.
        // TODO: Change the way the connection works by having a login: accepting, refusing, connection not found, etc... as ACKs from the service instead of a binary ACK.

    }


    @Override
    protected void handlePositiveAck()    {
        // Positive login confirmation
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(LOGIN_USER, username);
        progressDialog.hide();
        startActivity(intent);
    }

    @Override
    protected void handleNegativeAck()    {
        // Negative login confirmation
        progressDialog.hide();
        showMessageDialog("Couldn't login.");
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
