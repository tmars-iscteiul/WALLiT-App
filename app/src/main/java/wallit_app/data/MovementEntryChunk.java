package wallit_app.data;

import java.io.Serializable;
import java.util.ArrayList;

public class MovementEntryChunk implements Serializable	{

    /* Each chunk is composed of 9 entries (to match the fixed table's display lines)
     * Each entry has 3 values: Date, Amount and Balance
     * This data chunk is delivered by the server being serializable. This class will also be present in the android app
     * Server sends an ArrayList<FundInfoDataChunk> to the app after being requested.
     *
     * Data needs to be stored in memory after it's requested to the server (so it doesn't request every time the StatsActivity is launched)
     *
     */

    /**
     *
     */
    private static final long serialVersionUID = 3L;
    private ArrayList<MovementEntry> movementEntryList;

    public MovementEntryChunk()  {
        movementEntryList = new ArrayList<>();
    }

    // Returns true if entry was added
    // Returns false if list is full (9 entries already)
    public boolean addMovementEntry(MovementEntry entry)	{
        if(movementEntryList.size() == 9)	{
            return false;
        }	else	{
            movementEntryList.add(entry);
            return true;
        }
    }

    public void setMovementEntryList(ArrayList<MovementEntry> list)  {
        movementEntryList = list;
    }

    // If we need the ArrayList itself, replace this with its getter
    public MovementEntry getMovementEntry(int index)  {
        return movementEntryList.get(index);
    }
}
