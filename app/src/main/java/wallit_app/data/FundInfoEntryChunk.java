package wallit_app.data;

import java.io.Serializable;
import java.util.ArrayList;

import wallit_app.utilities.TimeScaleType;

public class FundInfoEntryChunk implements Serializable {

    private static final long serialVersionUID = 5L;

    private ArrayList<FundInfoEntry> fundInfoEntryList;
    private TimeScaleType timeScale;

    public FundInfoEntryChunk(ArrayList<FundInfoEntry> fundInfoEntryList, TimeScaleType timeScale) {
        this.fundInfoEntryList = fundInfoEntryList;
        this.timeScale = timeScale;
    }

    public ArrayList<FundInfoEntry> getFundInfoEntryList() {
        return fundInfoEntryList;
    }

    public TimeScaleType getTimeScale() {
        return timeScale;
    }
}

