package wallit_app.data;
import java.io.Serializable;
import java.util.ArrayList;

public class AckMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ackMessageType;
    private ArrayList<MovementEntryChunk> movementEntryChunkList;
    private ArrayList<FundInfoEntry> fundInfoList;

    public AckMessage(String ackMessageType, Object objectList) {
        this.ackMessageType = ackMessageType;
        if(ackMessageType == "MSG_ACK_FUND_DATA")	{
            this.movementEntryChunkList = null;
            this.fundInfoList = (ArrayList<FundInfoEntry>)objectList;
        }
        if(ackMessageType == "MSG_ACK_USER_DATA") {
            this.movementEntryChunkList = (ArrayList<MovementEntryChunk>)objectList;
            this.fundInfoList = null;
        }
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


