package wallit_app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wallit_app.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

        timeScaleImage = findViewById(R.id.pageSystemImage);
        graph = findViewById(R.id.graph);
        currentTimeScale = 0;
        setupGraph(0, 20, 0, 1500);
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

    // Called to update the data to be displayed on the graph view, based on the current selected time scale
    private void updateGraphData()   {
        DataPoint[] dp = new DataPoint[fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().size()];
        double viewportLimit = 0;
        for(int i = 0; i < dp.length; i++)  {
            // TODO Date isn't set here properly. Find out how and replace it once the fundinfo data is being delivered from the files
            // TODO Add page-like system to change time scales
            dp[i] = new DataPoint(fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().get(i).getDate(), fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().get(i).getValue());
            if(dp[i].getY() > viewportLimit)
                viewportLimit = dp[i].getY();
        }
        setupSeries(dp, viewportLimit);
    }

    // Adds the data (dp) to be displayed in the graph, draws the lines and data points, including the background visuals.
    // viewportLimit is Y axis limit to the data, which is the highest value on the dp array (calculated previously on the updateGraphData() method)
    private void setupSeries(DataPoint[] dp, double viewportLimit)    {
        // TODO Remove duplicated code
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
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                // TODO Add a way to highlight the selected datapoint on the graph view
                displayDataPointToastMessage(dataPoint);
            }
        });
        graph.addSeries(series);

        setupGraph(0, viewportLimit, dp[0].getX(), dp[dp.length-1].getX());
    }

    // Configures the viewport, visuals and labels of the graph, taking as inputs the viewport bounds
    private void setupGraph(double minY, double maxY, double minX, double maxX)   {
        // Custom label formatter to show currency "EUR" and Date
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this) {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return super.formatLabel(value, isValueX);
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " €";
                }
            }
        });

        // Locks the viewport to manual bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(minY);
        graph.getViewport().setMaxY(maxY);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(minX);
        graph.getViewport().setMaxX(maxX);
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);

        // Removes grid lines
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
    }

    // Called by the button click listeners, to change and update the graph to the selected time scale
    private void selectTimeScale(int scale) {
        currentTimeScale = scale;
        updateGraphData();
        updateScaleImage();
    }

    // Updates the scale selection image under the graph view, based on the current selected time scale
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

    // Called to display the selected datapoint's information on the screen after the user selects it
    private void displayDataPointToastMessage(DataPointInterface dp)    {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date((long)dp.getX()));
        String value = new DecimalFormat("#.##").format(dp.getY());
        String message = "Value at " + date + " is " + value + " €.";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // All the buttons to display each time scale on the graph view
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
