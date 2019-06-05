package wallit_app.utilities;

import java.io.Serializable;

/**
 * An enum that will hold each available TimeScaleType for the {@link wallit_app.data.FundInfoEntry} lists that are sent to the android client.
 * <p>
 * Each one has two different static and final values associated with it. The first is {@link TimeScaleType#entriesPerScale} and the second is {@link TimeScaleType#jumpsPerLine}
 * @see wallit_app.data.FundInfoEntryChunk
 * @author skner
 *
 */
public enum TimeScaleType implements Serializable	{
    ONE_MONTH(30, 1),
    THREE_MONTHS(30, 3),
    SIX_MONTHS(27, 7),
    ONE_YEAR(52, 7),
    FIVE_YEARS(60, 30),
    ;

    /** Number of entries per perspective. This is useful to guarantee consistency between how many entries should be inserted per time scale on each chunk.*/
    private int entriesPerScale;
    /** How many lines does it jump from the original data file. This will be used to determine the interval in between data entries in the timescaled list.*/
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
