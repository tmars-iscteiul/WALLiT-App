package wallit_app.data;
import java.io.Serializable;
import java.util.ArrayList;

public class AckMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ackMessageType;
    private ArrayList<MovementEntryChunk> movementEntryChunkList;
    private ArrayList<FundInfoEntryChunk> fundInfoChunkList;

    @SuppressWarnings("unchecked")
    public AckMessage(String ackMessageType, Object objectList) {
        this.ackMessageType = ackMessageType;
        if(ackMessageType == "MSG_ACK_FUND_DATA")	{
            this.movementEntryChunkList = null;
            this.fundInfoChunkList = (ArrayList<FundInfoEntryChunk>)objectList;
        }
        if(ackMessageType == "MSG_ACK_USER_DATA") {
            this.movementEntryChunkList = (ArrayList<MovementEntryChunk>)objectList;
            this.fundInfoChunkList = null;
        }
    }

    public String getAckMessageType()   {
        return ackMessageType;
    }

    public ArrayList<MovementEntryChunk> getMovementEntryChunkList() {
        return movementEntryChunkList;
    }

    public ArrayList<FundInfoEntryChunk> getFundInfoList() {
        return fundInfoChunkList;
    }
}


