package com.example.wallit_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.wallit_app.networking.NetworkingService;

public class ToolBarActivity extends BindingActivity {

    protected AlertDialog.Builder alertDialogBuilder;
    protected AlertDialog alertDialog;
    protected AlertDialog.Builder logoutDialog;

    @Override
    protected void onStart() {
        super.onStart();
        bindToNetworkingService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alertDialogBuilder = new AlertDialog.Builder(this);
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        logoutDialog = new AlertDialog.Builder(this);
        logoutDialog.setTitle("Logout Confirmation");
        logoutDialog.setMessage("Are you sure you want to logout from your account?");
        logoutDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(intent);
                    }
                });
        logoutDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Back button on top left
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
        logoutDialog.show();
    }

    public void showMessageDialog(String text) {
         alertDialogBuilder.setMessage(text)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    protected void handlePositiveAck()    {
    }

    protected  void handleNegativeAck() {
    }
}
