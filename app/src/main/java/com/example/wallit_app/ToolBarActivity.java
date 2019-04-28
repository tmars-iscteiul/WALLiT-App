package com.example.wallit_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class ToolBarActivity extends BindingActivity {


    protected void handlePositiveAck()    {
    }

    protected  void handleNegativeAck() {
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Logout Confirmation");
        alertDialog.setMessage("Are you sure you want to logout from your account?");
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        redirectDataToServer("User wants to logout: " + username);
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
