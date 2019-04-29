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
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Logging in...");
        progressDialog.setIndeterminate(true);

        // TODO Replace this with Async tasks for socket connection (Staying like this is a really bad practice)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public void loginUser(View view) {
        EditText editText = findViewById(R.id.username);
        String username = editText.getText().toString();
        redirectDataToServer("User wants to login: " + username);
        progressDialog.show();
        // TODO: Add a timeout to the progress dialog.
        // TODO Replace the simple YES-NO ACK system to message ack system (so we know why it failed, etc...)
    }

    protected void handlePositiveAck()    {
        // Positive login confirmation
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(LOGIN_USER, username);
        progressDialog.hide();
        startActivity(intent);
    }

    protected void handleNegativeAck()    {
        progressDialog.hide();
        showMessageDialog("Couldn't login.");
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

    // TODO Create the dialog in the onCreate method, and change the text and only show it in this method
    public void showMessageDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text)
                .setCancelable(false)
                .setPositiveButton("OK :(", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showExitingDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
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
