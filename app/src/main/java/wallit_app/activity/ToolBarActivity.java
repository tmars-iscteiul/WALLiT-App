package wallit_app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.wallit_app.R;
import wallit_app.utilities.ServiceMessages;

public class ToolBarActivity extends BindingActivity {

    protected Toolbar toolbar;
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout)
            logoutDialog.show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void handleAck(ServiceMessages ackCode)   {
        System.out.println("Handling ack " + ackCode.getMessageString());
        switch(ackCode) {
            case MSG_ACK_POSITIVE:
                handlePositiveAck();
                break;
            case MSG_ACK_NEGATIVE:
                handleNegativeAck();
                break;
            case MSG_CONNECTION_TIMEOUT:
                progressDialog.hide();
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

    // Called when activity receives a positive ACK from the Networking Service
    protected void handlePositiveAck()  {
    }

    // Called when activity receives a positive ACK from the Networking Service
    protected void handleNegativeAck()  {
    }

    @Override
    protected void runAfterConnectedToService()    {
    }

    // Sets up a fade transition when the user clicks the back button to return to the previous activity
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // Logs out user, by returning to the login activity, clearing all cache from the app
    private void logoutUser()   {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        userLoggedIn = false;
        startActivity(intent);
    }

    // Update's toolbar's visuals and functionalities
    protected void setupToolbar()   {
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
    }
}
