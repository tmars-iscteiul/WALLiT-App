package wallit_app.data;

import java.io.Serializable;
import java.util.ArrayList;

public class AckMessage implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String ackMessageType;
    private ArrayList<MovementEntryChunk> movementEntryList;

    public AckMessage(String ackMessageType, ArrayList<MovementEntryChunk> movementEntryList) {
        this.ackMessageType = ackMessageType;
        this.movementEntryList = movementEntryList;
    }

    public String getAckMessageType()   {
        return ackMessageType;
    }

    public ArrayList<MovementEntryChunk> getMovementEntryList() {
        return movementEntryList;
    }

}

