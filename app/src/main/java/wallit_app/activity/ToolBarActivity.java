package wallit_app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.wallit_app.R;
import wallit_app.utilities.ServiceMessages;

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

        logoutDialog = new AlertDialog.Builder(this);
        logoutDialog.setTitle("Logout Confirmation");
        logoutDialog.setMessage("Are you sure you want to logout from your account?");
        logoutDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logoutUser();
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
                        logoutUser();
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
        if (item.getItemId() == R.id.action_logout)
            logoutDialog.show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void handleAck(ServiceMessages ackCode)   {
        switch(ackCode) {
            case MSG_CONNECTION_TIMEOUT:
                connectionTimeoutDialog.show();
                break;
            case MSG_OFFLINE_ACK:
                handleOfflineAck();
                break;
            default:
                break;
        }
    }

    @Override
    protected void handleDataAck(ServiceMessages ackCode, Object data)  {
        // Do something if needed (most likely not)
    }

    @Override
    protected void runAfterConnectedToService()    {
    }

    private void logoutUser()   {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        userLoggedIn = false;
        startActivity(intent);
    }
}
