package wallit_app.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wallit_app.R;

import wallit_app.data.MovementEntryChunk;
import wallit_app.utilities.Formatter;
import wallit_app.utilities.ServiceMessages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Contains the user's movement history table and other indicators received from the java server, such as the current Balance.
 * @author skner
 */
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

    /**
     * Called when user clicks on the next page button.
     * @param view Android view from button.
     */
    public void buttonShowNextPage(View view)    {
        if(hasNextPage)    {
            currentPageDisplay++;
            insertDataOnTableFromPage(currentPageDisplay);
        }
    }

    /**
     * Called when user clicks on the previous page button.
     * @param view Android view from button.
     */
    public void buttonShowPreviousPage(View view)    {
        if(hasPreviousPage)    {
            currentPageDisplay--;
            insertDataOnTableFromPage(currentPageDisplay);
        }
    }

    /**
     * Updates the table to display  data from a specific page in the dataChunkPages list.
     * @param pageNumber Page number to extract the data from the {@link StatsActivity#dataChunkPages} list.
     */
    private void insertDataOnTableFromPage(int pageNumber)  {
        // Inserts data from the dataChunkPages to each row's cell
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

    /**
     * Updates visual display of the paging system, including the display text (visual indication weather or not there's a next/previous page.
     */
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

    /**
     * Updates current user balance value display.
     */
    private void updateCurrentBalanceValue()    {
        TextView tv = findViewById(R.id.current_balance_value);
        if(!dataChunkPages.get(0).getMovementEntryList().isEmpty())
            tv.setText(Formatter.doubleToEuroString(dataChunkPages.get(0).getMovementEntryList().get(0).getBalance()));
        else
            tv.setText(Formatter.doubleToEuroString(0.0));
    }

    /**
     * Inserts data on a specific cell (by String).
     * @param cellName The name of the cell that the current Activity's resources will find and assign to TextView tv.
     * @param data The data string that will be set in the TextView tv.
     * @param isDeposit Will serve to distinguish from deposit and withdraw operations, to allow the Activity to format the line accordingly.
     */
    private void insertDataOnCell(String cellName, String data, boolean isDeposit) {
        TextView tv = findViewById(res.getIdentifier(cellName, "id", getApplicationContext().getPackageName()));
        tv.setText(data);
        if(isDeposit)
            tv.setTextColor(getResources().getColor(R.color.colorDepositText));
        else
            tv.setTextColor(getResources().getColor(R.color.colorWithdrawText));
    }

    /**
     * Removes all text from a specific table row
     * @param rowNumber The row to remove all text from
     */
    private void clearTableRow(int rowNumber)   {
        insertDataOnCell("date" + (rowNumber+1), "", false);
        insertDataOnCell("amount" + (rowNumber+1), "", false);
        insertDataOnCell("balance" + (rowNumber+1), "", false);
    }

}
