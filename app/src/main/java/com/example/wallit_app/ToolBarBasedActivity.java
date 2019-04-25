package com.example.wallit_app;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.wallit_app.networking.NetworkingService;


public abstract class ToolBarBasedActivity extends AppCompatActivity {

    protected NetworkingService nService;
    protected boolean serviceBound = false;
    protected String username = "null";

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
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, NetworkingService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        System.out.println("Networking service intent binded!");
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        serviceBound = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Back button on top left
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        if (id == R.id.action_logout)   {
            showLogoutDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showLogoutDialog() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ToolBarBasedActivity.this);
        alertDialog.setTitle("Logout Confirmation");
        alertDialog.setMessage("Are you sure you want to logout from your account?");
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        nService.sendDataToHandler("User wants to logout: " + username);
                        startActivity(intent);
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
