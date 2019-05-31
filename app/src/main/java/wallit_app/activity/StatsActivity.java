package wallit_app.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wallit_app.R;

import wallit_app.data.MovementEntryChunk;
import wallit_app.utilities.Formatter;
import wallit_app.utilities.ServiceMessages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class StatsActivity extends ToolBarActivity {

    private Resources res;
    private ArrayList<MovementEntryChunk> dataChunkPages;
    private int currentPageDisplay;
    private TextView currentPageDisplayTextView;
    private ImageView pageImageDisplay;

    private boolean hasPreviousPage;
    private boolean hasNextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_main);

        toolbar = findViewById(R.id.toolbar);
        setupToolbar();

        currentPageDisplayTextView = findViewById(R.id.pageText);
        pageImageDisplay = findViewById(R.id.pageSystemImage);
        username = getIntent().getStringExtra(LOGIN_USER);

        res = getResources();
    }

    @Override
    protected void runAfterConnectedToService()    {
        // TODO username isn't set here, transfer from previous intent (just like the host)
        redirectDataToServer(ServiceMessages.REQUEST_MOVEMENT_HISTORY.getMessageString() + "," + username, ServiceMessages.REQUEST_MOVEMENT_HISTORY);
        progressDialog.setMessage("Downloading movement history...");
        progressDialog.show();
    }

    @Override
    protected void handleAck(ServiceMessages ackCode, Object rawData) {
        if(ackCode == ServiceMessages.MSG_ACK_USER_DATA)    {
            dataChunkPages = (ArrayList<MovementEntryChunk>)rawData;
            currentPageDisplay = 0;
            insertDataOnTableFromPage(currentPageDisplay);
            updateCurrentBalanceValue();
            progressDialog.hide();
        }   else
            super.handleAck(ackCode, rawData);
    }

    // Called when user clicks on the next page button
    public void buttonShowNextPage(View view)    {
        if(hasNextPage)    {
            currentPageDisplay++;
            insertDataOnTableFromPage(currentPageDisplay);
        }
    }

    // Called when user clicks on the previous page button
    public void buttonShowPreviousPage(View view)    {
        if(hasPreviousPage)    {
            currentPageDisplay--;
            insertDataOnTableFromPage(currentPageDisplay);
        }
    }

    // Updates the table to display  data from a specific page in the dataChunkPages list
    private void insertDataOnTableFromPage(int pageNumber)  {
        for(int i = 0; i < dataChunkPages.get(pageNumber).getMovementEntryList().size(); i++)    {
            boolean isDeposit = dataChunkPages.get(pageNumber).getMovementEntryList().get(i).getAmount() > 0;
            insertDataOnCell(
                    "date" + (i+1),
                    new SimpleDateFormat("yyyy-MM-dd").format(dataChunkPages.get(pageNumber).getMovementEntryList().get(i).getDate()),
                    isDeposit
            );
            insertDataOnCell(
                    "amount" + (i+1),
                    Formatter.doubleToEuroString(dataChunkPages.get(pageNumber).getMovementEntryList().get(i).getAmount()),
                    isDeposit
            );
            insertDataOnCell(
                    "balance" + (i+1),
                    Formatter.doubleToEuroString(dataChunkPages.get(pageNumber).getMovementEntryList().get(i).getBalance()),
                    isDeposit
            );
        }
        // If there's rows left, clear them from previous inserted entries
        for(int i = dataChunkPages.get(pageNumber).getMovementEntryList().size(); i < 9; i++)    {
            clearTableRow(i);
        }
        updatePageSystemDisplay();
    }

    // Updates visual display of the paging system, including the display text (visual indication weather or not there's a next/previous page.
    private void updatePageSystemDisplay()    {
        hasPreviousPage = (currentPageDisplay - 1) >= 0;
        hasNextPage = (currentPageDisplay + 1) < dataChunkPages.size();
        currentPageDisplayTextView.setText("Page " + (currentPageDisplay+1) + " / " + dataChunkPages.size());
        if(hasPreviousPage) {
            if(hasNextPage)
                pageImageDisplay.setImageDrawable(getResources().getDrawable(R.drawable.stats_page_on_on));
            else
                pageImageDisplay.setImageDrawable(getResources().getDrawable(R.drawable.stats_page_on_off));
        }   else    {
            if(hasNextPage)
                pageImageDisplay.setImageDrawable(getResources().getDrawable(R.drawable.stats_page_off_on));
            else
                pageImageDisplay.setImageDrawable(getResources().getDrawable(R.drawable.stats_page_off_off));
        }
    }

    private void updateCurrentBalanceValue()    {
        TextView tv = findViewById(R.id.current_balance_value);
        tv.setText(Formatter.doubleToEuroString(dataChunkPages.get(0).getMovementEntryList().get(dataChunkPages.get(0).getMovementEntryList().size()-1).getBalance()));
    }

    //Inserts data on a specific cell (by String)
    private void insertDataOnCell(String cellName, String data, boolean isDeposit) {
        TextView tv = findViewById(res.getIdentifier(cellName, "id", getApplicationContext().getPackageName()));
        tv.setText(data);
        if(isDeposit)
            tv.setTextColor(getResources().getColor(R.color.colorDepositText));
        else
            tv.setTextColor(getResources().getColor(R.color.colorWithdrawText));
    }

    // Removes all text from a specific table row
    private void clearTableRow(int rowNumber)   {
        insertDataOnCell("date" + (rowNumber+1), "", false);
        insertDataOnCell("amount" + (rowNumber+1), "", false);
        insertDataOnCell("balance" + (rowNumber+1), "", false);
    }

}
