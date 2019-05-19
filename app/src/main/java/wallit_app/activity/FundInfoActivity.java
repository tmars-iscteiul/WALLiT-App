package wallit_app.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.wallit_app.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import wallit_app.data.FundInfoEntry;
import wallit_app.utilities.ServiceMessages;

// Using this library for the graph view: https://github.com/jjoe64/GraphView
public class FundInfoActivity extends ToolBarActivity {

    private ArrayList<FundInfoEntry> fundInfoEntries;
    private GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fundinfo_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        graph = findViewById(R.id.graph);
        fundInfoEntries = new ArrayList<>();
    }

    @Override
    protected void runAfterConnectedToService()    {
        // TODO username isn't set here, transfer from previous intent (just like the host)
        redirectDataToServer(ServiceMessages.REQUEST_FUND_INFO.getMessageString(), ServiceMessages.REQUEST_MOVEMENT_HISTORY);
        progressDialog.setMessage("Downloading fund information...");
        progressDialog.show();
    }

    @Override
    protected void handleAck(ServiceMessages ackCode)   {
        switch(ackCode) {
            // This is here because the server isn't prepared to handle fund info requests. Simply sends a negative ACK.
            // Once the data struct class is implemented, replace this with proper server communication
            case MSG_ACK_NEGATIVE:
                progressDialog.hide();
                showMessageDialog("Getting fund information from server not yet implemented.");
                break;
            default:
                break;
        }
    }

    @Override
    protected void handleDataAck(ServiceMessages ackCode, Object rawData) {
        // TODO Add local object variable to store fund information received from the server. TBD what class it is and how it's constructed
        fundInfoEntries = new ArrayList<>((ArrayList<FundInfoEntry>)rawData);
        updateGraphData();
        progressDialog.hide();
    }

    private void updateGraphData()   {
        DataPoint[] dp = new DataPoint[fundInfoEntries.size()];
        for(int i = 0; i < dp.length; i++)  {
            // TODO Date isn't set here properly. Find out how and replace it once the fundinfo data is being delivered from the files
            dp[i] = new DataPoint(i, fundInfoEntries.get(i).getValue());
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);
        graph.addSeries(series);
    }
}
