package com.example.wallit_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ToolBarActivity extends BindingActivity {

    protected AlertDialog.Builder logoutDialog;

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
        //Back button on top left (Do we want it or no?)
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
            logoutDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void handlePositiveAck()    {
    }

    protected  void handleNegativeAck() {
    }
}
