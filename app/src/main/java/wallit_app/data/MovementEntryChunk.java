package wallit_app.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A list of {@link MovementEntry}'s. This list will be restricted to 9 elements only, to facilitate, optimize and speed-up read operations on the android client.
 * @author skner
 *
 */
public class MovementEntryChunk implements Serializable	{

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

    public ArrayList<MovementEntry> getMovementEntryList()  {
        return movementEntryList;
    }
}

