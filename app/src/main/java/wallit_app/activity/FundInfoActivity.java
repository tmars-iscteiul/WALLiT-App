package wallit_app.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.wallit_app.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
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

        toolbar = findViewById(R.id.toolbar);
        setupToolbar();

        graph = findViewById(R.id.graph);
        setupGraph();
        fundInfoEntries = new ArrayList<>();
    }

    @Override
    protected void runAfterConnectedToService()    {
        // TODO username isn't set here, transfer from previous intent (just like the host)
        redirectDataToServer(ServiceMessages.REQUEST_FUND_INFO.getMessageString(), ServiceMessages.REQUEST_FUND_INFO);
        progressDialog.setMessage("Downloading fund information...");
        progressDialog.show();
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
        setupSeries(dp);
    }

    private void setupSeries(DataPoint[] dp)    {
        // Adds the background first
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);
        series.setThickness(0);
        series.setDrawBackground(true);
        series.setBackgroundColor(getResources().getColor(R.color.colorGraphBackground));
        graph.addSeries(series);
        // Adds the lines second (so the lines stay on top of the background)
        series = new LineGraphSeries<>(dp);
        series.setColor(getResources().getColor(R.color.colorPrimary));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(4);
        series.setThickness(2);
        series.setDrawBackground(false);
        graph.addSeries(series);

    }

    private void setupGraph()   {
        // Locks the viewport to manual bounds
        // TODO Change later to match the imported data series
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(20);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(1500);

        // Removes grid lines
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);

    }
}
