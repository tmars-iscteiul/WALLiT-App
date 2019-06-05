package wallit_app.data;

import java.io.Serializable;
import java.util.ArrayList;

import wallit_app.utilities.TimeScaleType;

/**
 * A list of {@link FundInfoEntry}'s, that contains an associated {@link TimeScaleType} to it.
 * @author skner
 *
 */
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

