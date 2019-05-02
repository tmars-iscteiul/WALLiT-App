package com.example.wallit_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.wallit_app.networking.ServiceMessages;

public class ToolBarActivity extends BindingActivity {

    protected AlertDialog.Builder logoutDialog;
    private AlertDialog.Builder connectionTimeoutDialogBuilder;
    private AlertDialog connectionTimeoutDialog;

    @Override
    protected void onStart() {
        super.onStart();
        bindToNetworkingService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        logoutDialog = new AlertDialog.Builder(this);
        logoutDialog.setTitle("Logout Confirmation");
        logoutDialog.setMessage("Are you sure you want to logout from your account?");
        logoutDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logoutUser(intent);
                    }
                });
        logoutDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        connectionTimeoutDialogBuilder = new AlertDialog.Builder(this);
        connectionTimeoutDialogBuilder.setMessage("Server connection timed out. Logging out...")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logoutUser(null);
                    }
                });
        connectionTimeoutDialog = connectionTimeoutDialogBuilder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Back button on top left (Do we want it or no?)
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // TODO: Opening settings activity unbinds every activity to the service, shutting it down. Fix this, for now we can't access the settings activity (nothing works there anyways)
        //if (id == R.id.action_settings) {
        //    startActivity(new Intent(this, SettingsActivity.class));
        //}
        if (id == R.id.action_logout)   {
            logoutDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void handleAck(ServiceMessages ackCode)   {
        switch(ackCode) {
            case MSG_CONNECTION_TIMEOUT:
                handleTimeoutAck();
                break;
            default:
                break;
        }
    }

    @Override
    protected void handleDataAck(ServiceMessages ackCode, String data)  {
        // Do something if needed (most likely not)
    }

    private void handleTimeoutAck()    {
        // Timeout login confirmation
        unbindToNetworkingService();
        progressDialog.hide();
        connectionTimeoutDialog.show();
    }

    private void logoutUser(Intent intent)   {
        if(intent == null)  {
            intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        userLoggedIn = false;
        startActivity(intent);
    }
}
