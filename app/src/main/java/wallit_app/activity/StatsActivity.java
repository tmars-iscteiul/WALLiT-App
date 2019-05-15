package wallit_app.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.wallit_app.R;

import wallit_app.data.MovementEntryChunk;
import wallit_app.utilities.ServiceMessages;

import java.util.ArrayList;

public class StatsActivity extends ToolBarActivity {

    private Resources res;
    private ArrayList<MovementEntryChunk> dataChunkPages;
    private int currentPageDisplay;
    private TextView currentPageDisplayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO There's a bug in offline mode, when the user exits from stats activity to the home activity, it shows the offline dialog for some reason. Find out why, how and fix it
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentPageDisplayTextView = findViewById(R.id.pageText);

        res = getResources();
    }

    @Override
    protected void runAfterConnectedToService()    {
        // TODO username isn't set here, transfer from previous intent (just like the host)
        redirectDataToServer("REQUEST_MOVEMENT_HISTORY", ServiceMessages.REQUEST_MOVEMENT_HISTORY);
        progressDialog.setMessage("Downloading movement history...");
        progressDialog.show();
    }

    @Override
    protected void handleAck(ServiceMessages ackCode)   {
        super.handleAck(ackCode);
    }

    @Override
    protected void handleDataAck(ServiceMessages ackCode, Object rawData) {
        dataChunkPages = (ArrayList<MovementEntryChunk>)rawData;
        currentPageDisplay = 0;
        insertDataOnTableFromPage(currentPageDisplay);
        progressDialog.hide();
    }

    public void showNextPage(View view)    {
        // If there's a next page
        if((currentPageDisplay + 1) < dataChunkPages.size())    {
            currentPageDisplay++;
            insertDataOnTableFromPage(currentPageDisplay);
        }   else    {
            System.out.println("There's no next page to display.");
        }
    }

    public void showPreviousPage(View view)    {
        // If there's a previous page
        if((currentPageDisplay - 1) >= 0)    {
            currentPageDisplay--;
            insertDataOnTableFromPage(currentPageDisplay);
        }   else    {
            System.out.println("There's no previous page to display.");
        }
    }

    private void insertDataOnTableFromPage(int pageNumber)  {
        int i = 8;
        // If there's rows left, clear them from previous inserted entries
        for(; i > dataChunkPages.get(pageNumber).getMovementEntryList().size() - 1; i--)    {
            clearTableRow(i);
        }
        for(; i >= 0; i--)    {
            System.out.println("Started: " + i + ", out of " + dataChunkPages.get(pageNumber).getMovementEntryList().size());
            System.out.println("Object: " + dataChunkPages.get(pageNumber).getMovementEntryList().get(i));
            boolean isDeposit = dataChunkPages.get(pageNumber).getMovementEntryList().get(i).getAmount() > 0;
            insertDataOnCell("date" + (i+1), dataChunkPages.get(pageNumber).getMovementEntryList().get(i).getDate(), isDeposit);
            insertDataOnCell("amount" + (i+1), dataChunkPages.get(pageNumber).getMovementEntryList().get(i).getAmount() + " €", isDeposit);
            insertDataOnCell("balance" + (i+1), dataChunkPages.get(pageNumber).getMovementEntryList().get(i).getBalance() + " € ", isDeposit);
        }
        currentPageDisplayTextView.setText("Page " + (pageNumber+1) + " / " + dataChunkPages.size());
    }

    private void insertDataOnCell(String cellName, String data, boolean isDeposit) {
        System.out.println("Setting: " + cellName + " to " + data);
        TextView tv = findViewById(res.getIdentifier(cellName, "id", getApplicationContext().getPackageName()));
        tv.setText(data);
        if(isDeposit)
            tv.setTextColor(getResources().getColor(R.color.colorPrimary));
        else
            tv.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void clearTableRow(int rowNumber)   {
        insertDataOnCell("date" + (rowNumber+1), "", false);
        insertDataOnCell("amount" + (rowNumber+1), "", false);
        insertDataOnCell("balance" + (rowNumber+1), "", false);
    }

}
