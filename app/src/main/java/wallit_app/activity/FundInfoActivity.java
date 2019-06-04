package wallit_app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wallit_app.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import wallit_app.data.FundInfoEntryChunk;
import wallit_app.utilities.Formatter;
import wallit_app.utilities.ServiceMessages;

// Using this library for the graph view: https://github.com/jjoe64/GraphView
public class FundInfoActivity extends ToolBarActivity {

    private ImageView timeScaleImage;
    private ImageView dataPointSelectionImage;
    private GraphView graph;
    private TextView selectedDate;
    private TextView selectedValue;

    private ArrayList<FundInfoEntryChunk> fundInfoEntries;
    private LineGraphSeries<DataPoint> highlightedDataPoint;
    private int currentTimeScale;
    private int currentDataPoint;

    private boolean hasPreviousDataPoint;
    private boolean hasNextDataPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fundinfo_main);

        toolbar = findViewById(R.id.toolbar);
        setupToolbar();

        username = getIntent().getStringExtra(LOGIN_USER);
        timeScaleImage = findViewById(R.id.pageSystemImage);
        dataPointSelectionImage = findViewById(R.id.datapoint_selection_image);
        selectedDate = findViewById(R.id.datapoint_selection_date);
        selectedValue = findViewById(R.id.datapoint_selection_value);
        graph = findViewById(R.id.graph);

        currentTimeScale = 0;
        currentDataPoint = 0;
        setupGraph(0, 20, 0, 1500);
        fundInfoEntries = new ArrayList<>();
    }

    @Override
    protected void runAfterConnectedToService()    {
        redirectDataToServer(ServiceMessages.REQUEST_FUND_INFO.getMessageString(), ServiceMessages.REQUEST_FUND_INFO);
        progressDialog.setMessage("Downloading fund information...");
        progressDialog.show();
    }

    @Override
    protected void handleAck(ServiceMessages ackCode, Object rawData) {
        if(ackCode == ServiceMessages.MSG_ACK_FUND_DATA)    {
            fundInfoEntries = new ArrayList<>((ArrayList<FundInfoEntryChunk>)rawData);
            updateGraphData();
            updateScaleImage();
            updateCurrentWallitValue();
            progressDialog.hide();
        }   else
            super.handleAck(ackCode, rawData);
    }

    // Called to update the data to be displayed on the graph view, based on the current selected time scale
    private void updateGraphData()   {
        DataPoint[] dp = new DataPoint[fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().size()];
        for(int i = 0; i < dp.length; i++)  {
            dp[i] = new DataPoint(fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().get(i).getDate().getTime(), fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().get(i).getValue());
        }
        setupSeries(dp);
        highlightDataPoint(fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().size()-1);
    }

    // Adds the data (dp) to be displayed in the graph, draws the lines and data points, including the background visuals.
    // viewportLimit is Y axis limit to the data, which is the highest value on the dp array (calculated previously on the updateGraphData() method)
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
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                // Tapping on the object causes it to crash ConcurrentModificationException. This is a bug with the framework GraphView and couldn't be resolved by us.
                // So, we decided to exclude this functionality from the final MVP until an update to the framework is released with a fix.
                //highlightDataPoint(getDataPointIndex(dataPoint));
            }
        });
        graph.addSeries(series);

        setupGraph(0, series.getHighestValueY(), dp[0].getX(), dp[dp.length-1].getX());
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
        graph.getViewport().setMaxY(maxY+(maxY/5));
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(minX);
        graph.getViewport().setMaxX(maxX);
        graph.getGridLabelRenderer().setNumHorizontalLabels(2);

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

    // Called to display the selected data point's information on the screen after the user selects it
    private void highlightDataPoint(int index)    {
        currentDataPoint = index;
        selectedDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().get(index).getDate()));
        selectedValue.setText(Formatter.doubleToEuroString(fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().get(index).getValue()));
        if(highlightedDataPoint != null) {
            // Remove the old highlighted point
            graph.removeSeries(highlightedDataPoint);
        }
        highlightedDataPoint = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().get(index).getDate(), fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().get(index).getValue()),
        });
        highlightedDataPoint.setColor(getResources().getColor(R.color.colorAccent));
        highlightedDataPoint.setDrawDataPoints(true);
        highlightedDataPoint.setDataPointsRadius(10);
        graph.addSeries(highlightedDataPoint);
        updateDataPointSelectionDisplay();
    }

    // On a given data point, returns the index of the fundInfoEntries array-list, assuming it belongs to the current time scale being displayed
    private int getDataPointIndex(DataPointInterface dp)   {
        for(int i = 0; i<fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().size(); i++)  {
            if((long)dp.getX() == fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().get(i).getDate().getTime())
                return i;
        }
        return -1;
    }

    // Updates the visual display for the data point selection image
    private void updateDataPointSelectionDisplay()    {
        hasPreviousDataPoint = (currentDataPoint - 1) >= 0;
        hasNextDataPoint = (currentDataPoint + 1) < fundInfoEntries.get(currentTimeScale).getFundInfoEntryList().size();
        if(hasPreviousDataPoint) {
            if(hasNextDataPoint)
                dataPointSelectionImage.setImageDrawable(getResources().getDrawable(R.drawable.datapoint_selection_on_on));
            else
                dataPointSelectionImage.setImageDrawable(getResources().getDrawable(R.drawable.datapoint_selection_on_off));
        }   else    {
            if(hasNextDataPoint)
                dataPointSelectionImage.setImageDrawable(getResources().getDrawable(R.drawable.datapoint_selection_off_on));
            else
                dataPointSelectionImage.setImageDrawable(getResources().getDrawable(R.drawable.datapoint_selection_off_off));
        }
    }

    // Updates current fund total value display
    private void updateCurrentWallitValue() {
        TextView tv = findViewById(R.id.current_wallit_value);
        tv.setText(Formatter.doubleToEuroString(fundInfoEntries.get(0).getFundInfoEntryList().get(fundInfoEntries.get(0).getFundInfoEntryList().size()-1).getValue()));
    }

    // Called when user clicks on the left button in the data point selection display panel
    public void displayLeftDataPoint(View view) {
        if(hasPreviousDataPoint)    {
            highlightDataPoint(currentDataPoint-1);
        }
    }

    // Called when user clicks on the right button in the data point selection display panel
    public void displayRightDataPoint(View view)    {
        if(hasNextDataPoint)    {
            highlightDataPoint(currentDataPoint+1);
        }
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
