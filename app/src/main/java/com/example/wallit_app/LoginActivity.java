package com.example.wallit_app;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.app.AlertDialog;

import com.example.wallit_app.networking.NetworkingService;

public class LoginActivity extends AppCompatActivity {

    public static final String LOGIN_USER = "com.example.wallit_app.LOGINUSER";
    private NetworkingService nService;
    private boolean serviceBound = false;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            NetworkingService.LocalBinder binder = (NetworkingService.LocalBinder)service;
            nService = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        // TODO Replace this with Async tasks for socket connection (Staying like this is a really bad practice)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, NetworkingService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        System.out.println("Networking service intent booted!");
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        nService.terminateConnection();
        serviceBound = false;
    }

    public void loginUser(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        EditText editText = findViewById(R.id.username);
        String username = editText.getText().toString();
        intent.putExtra(LOGIN_USER, username);
        nService.sendDataToHandler("User wants to login: " + username);
        startActivity(intent);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        //Changes 'back' button action
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            showExitingDialog();
        }
        return true;
    }

    public void showExitingDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle("Exit Confirmation");
        alertDialog.setMessage("Are you sure you want to leave the application?");
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
}
