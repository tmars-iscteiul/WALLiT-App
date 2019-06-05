package wallit_app.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A data class, that will hold information sent by the java server to the android client, serving as a response message structure.
 * <p>
 * Always contains a String, that represents the ACK message type, this can be from a simple positive or negative ACK, or a data containing ACK.
 * Depending on the ACK, it will contain a data object, either a {@link MovementEntryChunk}, a {@link FundInfoEntryChunk} or the last recorded user balance.
 * @see FundInfoEntry
 * @see MovementEntry
 *
 * @author skner
 *
 */
public class AckMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ackMessageType;
    private ArrayList<MovementEntryChunk> movementEntryChunkList;
    private ArrayList<FundInfoEntryChunk> fundInfoChunkList;
    private double lastRecordedBalance;

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
        lastRecordedBalance = -1;
    }

    public AckMessage(String ackMessageType, double lastRecordedBalance)	{
        this.ackMessageType = ackMessageType;
        this.lastRecordedBalance = lastRecordedBalance;
        this.movementEntryChunkList = null;
        this.fundInfoChunkList = null;
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

    public double getLastRecordedBalance()	{
        return lastRecordedBalance;
    }
}