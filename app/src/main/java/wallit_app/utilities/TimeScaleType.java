package wallit_app.utilities;

import java.io.Serializable;

public enum TimeScaleType implements Serializable	{
    ONE_MONTH(30, 1),
    THREE_MONTHS(90, 1),
    SIX_MONTHS(27, 7),
    ONE_YEAR(52, 7),
    FIVE_YEARS(60, 30),
    ;

    // Number of entries per perspective. This is useful to guarantee consistency between how many entries should be inserted per time scale on each chunk.
    private int entriesPerScale;
    // How many lines does it jump from the original data file. This will be used to determine the interval in between data entries in the timescaled list.
    private int jumpsPerLine;

    TimeScaleType(int entriesPerScale, int jumpsPerLine) {
        this.entriesPerScale = entriesPerScale;
        this.jumpsPerLine = jumpsPerLine;
    }

    public int getEntriesPerScale()	{
        return entriesPerScale;
    }

    public int getJumpsPerLine()	{
        return jumpsPerLine;
    }

}
