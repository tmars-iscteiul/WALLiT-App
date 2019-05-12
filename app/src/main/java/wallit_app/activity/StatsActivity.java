package wallit_app.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.wallit_app.R;

import wallit_app.data.MovementEntry;
import wallit_app.data.MovementEntryChunk;
import wallit_app.utilities.ServiceMessages;

import java.util.ArrayList;

public class StatsActivity extends ToolBarActivity {

    private Resources res;
    private ArrayList<MovementEntryChunk> dataChunkPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        res = getResources();
    }

    @Override
    protected void runAfterConnectedToService()    {
        // TODO username isn't set here, transfer from previous intent (just like the host)
        redirectDataToServer("REQUEST_MOVEMENT_HISTORY," + username);
        progressDialog.setMessage("Downloading movement history...");
        progressDialog.show();
    }

    @Override
    protected void handleDataAck(ServiceMessages ackCode, Object rawData) {
        ArrayList<MovementEntryChunk> movementEntryChunkList = new ArrayList<>((ArrayList<MovementEntryChunk>)rawData);
        // TODO Add a loop to fill in the table with all the received information
        TextView tv = (TextView)findViewById(res.getIdentifier("date1", "id", getApplicationContext().getPackageName()));
        tv.setText(movementEntryChunkList.get(0).getMovementEntry(0).getDate());
    }

    // TODO Add an ACK with data received so it can place it in the table
    // This is gonna take long

    /*
    If deposit row: change font color to #6699CC
    If withdraw row: change font color to #676767
    Date, Amount, Balance.

    int id = res.getIdentifier("titleText", "id", getContext().getPackageName());

    TODO: Add page system, so user can see entire history of movements. (9 entries per page)
     */

}
