package wallit_app.data;

import java.io.Serializable;
import java.util.ArrayList;

public class AckMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ackMessageType;
    private ArrayList<MovementEntryChunk> movementEntryChunkList;
    private ArrayList<FundInfoEntry> fundInfoList;

    public AckMessage(String ackMessageType, ArrayList<MovementEntryChunk> movementEntryChunkList) {
        this.ackMessageType = ackMessageType;
        this.movementEntryChunkList = movementEntryChunkList;
        this.fundInfoList = null;
    }

    public AckMessage(ArrayList<FundInfoEntry> fundInfoList, String ackMessageType) {
        this.ackMessageType = ackMessageType;
        this.fundInfoList = fundInfoList;
        this.movementEntryChunkList = null;
    }

    public String getAckMessageType()   {
        return ackMessageType;
    }

    public ArrayList<MovementEntryChunk> getMovementEntryChunkList() {
        return movementEntryChunkList;
    }

    public ArrayList<FundInfoEntry> getFundInfoList() {
        return fundInfoList;
    }
}


