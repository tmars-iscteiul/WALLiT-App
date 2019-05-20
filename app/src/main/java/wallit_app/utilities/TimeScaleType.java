package wallit_app.utilities;

import java.io.Serializable;

public enum TimeScaleType implements Serializable	{
    ONE_MONTH(30), // TODO A month, is it 30 or 31. 29 or 28!?!?!?!?
    THREE_MONTHS(90), // TODO 90? 93!?!?!? OH NO
    SIX_MONTHS(27), // 180/7 (weekly, but it's approximately...) How to handle this?
    ONE_YEAR(52), // 360/7 (same as above...)
    FIVE_YEARS(60), // We're trying to have it every month (1st of each month). This one works well actually.
    ;

    // Number of entries per perspective. This is useful to guarantee consistency between how many entries should be inserted per time scale on each chunk.
    private int entriesPerScale;

    TimeScaleType(int entriesPerScale) {
        this.entriesPerScale = entriesPerScale;
    }

    public int getEntriesPerScale()	{
        return entriesPerScale;
    }
}
