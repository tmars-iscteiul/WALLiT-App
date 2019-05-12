package wallit_app.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.wallit_app.R;

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
        insertDataOnTable((ArrayList<MovementEntryChunk>)rawData);
        progressDialog.hide();
    }

    private void insertDataOnTable(ArrayList<MovementEntryChunk> movementEntryChunkList)  {
        for(int i = 0; i<movementEntryChunkList.size(); i++)    {
            boolean isDeposit = movementEntryChunkList.get(0).getMovementEntry(i).getAmount() > 0;
            insertDataOnCell("date" + (i+1), movementEntryChunkList.get(0).getMovementEntry(i).getDate(), isDeposit);
            insertDataOnCell("amount" + (i+1), movementEntryChunkList.get(0).getMovementEntry(i).getAmount() + " €", isDeposit);
            insertDataOnCell("balance" + (i+1), movementEntryChunkList.get(0).getMovementEntry(i).getBalance() + " € ", isDeposit);
        }
        System.out.println("Inserted movement history data to table");
        // TODO Add page system, so user can see entire history of movements. (9 entries per page) (RIGHT NOW, IT CRASHED THE APP IF 10 LINES ARE TRANSMITTED)
        // TODO Add data to cache, memory or keep activity alive, so we don't request it to the server every time.
    }

    private void insertDataOnCell(String cellName, String data, boolean isDeposit) {
        TextView tv = findViewById(res.getIdentifier(cellName, "id", getApplicationContext().getPackageName()));
        tv.setText(data);
        if(isDeposit)
            tv.setTextColor(getResources().getColor(R.color.colorPrimary));
        else
            tv.setTextColor(getResources().getColor(R.color.colorAccent));
    }

}
