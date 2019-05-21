package wallit_app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.wallit_app.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import wallit_app.data.FundInfoEntry;
import wallit_app.data.FundInfoEntryChunk;
import wallit_app.utilities.ServiceMessages;

// Using this library for the graph view: https://github.com/jjoe64/GraphView
public class FundInfoActivity extends ToolBarActivity {

    private ImageView timeScaleImage;

    private ArrayList<FundInfoEntryChunk> fundInfoEntries;
    private int currentTimeScale;
    private GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fundinfo_main);

        toolbar = findViewById(R.id.toolbar);
        setupToolbar();

        timeScaleImage = findViewById(R.id.timeScaleImage);
        graph = findViewById(R.id.graph);
        currentTimeScale = 0;
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
        // TODO Add time scales chunks data class and change this to handle it instead of just one graph
        // TODO Implement time scales changing display on graph
        fundInfoEntries = new ArrayList<>((ArrayList<FundInfoEntryChunk>)rawData);
        updateGraphData();
        updateScaleImage();
        progressDialog.hide();
    }

    private void updateGraphData()   {
        DataPoint[] dp = new DataPoint[fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().size()];
        for(int i = 0; i < dp.length; i++)  {
            // TODO Date isn't set here properly. Find out how and replace it once the fundinfo data is being delivered from the files
            // TODO Add page-like system to change time scales
            dp[i] = new DataPoint(i, fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().get(i).getValue());
        }
        setupSeries(dp);
    }

    private void setupSeries(DataPoint[] dp)    {
        // Adds the background first
        graph.removeAllSeries();
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

        // Adjusts viewport to include all data
        // TODO Get the highest point and add a 5th more Y size on the viewport
        graph.getViewport().setMaxX(dp.length);
    }

    private void setupGraph()   {
        // Locks the viewport to manual bounds
        // TODO Find out how to see dates instead of numbers on the X axis
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(20);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(1500);

        // Removes grid lines
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
    }

    private void selectTimeScale(int scale) {
        currentTimeScale = scale;
        updateGraphData();
        updateScaleImage();
    }

    private void updateScaleImage() {
        // TODO Optimize this
        switch(currentTimeScale)    {
            case 0:
                timeScaleImage.setImageDrawable(getResources().getDrawable(R.drawable.fund_timescale_selection1));
                break;
            case 1:
                timeScaleImage.setImageDrawable(getResources().getDrawable(R.drawable.fund_timescale_selection2));
                break;
            case 2:
                timeScaleImage.setImageDrawable(getResources().getDrawable(R.drawable.fund_timescale_selection3));
                break;
            case 3:
                timeScaleImage.setImageDrawable(getResources().getDrawable(R.drawable.fund_timescale_selection4));
                break;
            case 4:
                timeScaleImage.setImageDrawable(getResources().getDrawable(R.drawable.fund_timescale_selection5));
                break;
        }
    }

    public void selectScale5Button(View view)    {
        selectTimeScale(4);
    }
    public void selectScale4Button(View view)    {
        selectTimeScale(3);
    }
    public void selectScale3Button(View view)    {
        selectTimeScale(2);
    }
    public void selectScale2Button(View view)    {
        selectTimeScale(1);
    }
    public void selectScale1Button(View view)    {
        selectTimeScale(0);
    }
}
