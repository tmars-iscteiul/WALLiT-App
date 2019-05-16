package wallit_app.utilities;

// This will serve as the project's messaging dictionary, where every class can understand each other by having a global unique communication code stored and updated in one single place
public enum ServiceMessages {
    MSG_UNKNOWN(-1),
    MSG_UNBIND(0),
    MSG_BIND(1),
    MSG_ACK_POSITIVE(2),
    MSG_ACK_NEGATIVE(3),
    MSG_SEND_DATA(4),
    MSG_LOGIN(5),
    MSG_TERMINATE_SERVICE(6),
    MSG_CONNECTION_TIMEOUT(7),
    MSG_OFFLINE_ACK(8),
    REQUEST_LOGIN(9),
    REQUEST_MOVEMENT_HISTORY(10),
    REQUEST_DEPOSIT(11),
    REQUEST_WITHDRAW(12),
    REQUEST_FUND_INFO(13),
    ;

    private final int messageID;

    ServiceMessages(int messageID) {
        this.messageID = messageID;
    }

    public int getMessageID() {
        return messageID;
    }

    public String getMessageString() {
        return toString();
    }

    static public ServiceMessages getMessageByID(int id)   {
        for (ServiceMessages sm : ServiceMessages.values()) {
            if (sm.getMessageID() == id) {
                return sm;
            }
        }
        return MSG_UNKNOWN;
    }

    static public ServiceMessages getMessageByString(String string) {
        for (ServiceMessages sm : ServiceMessages.values()) {
            if (sm.toString().equals(string)) {
                return sm;
            }
        }
        return MSG_UNKNOWN;
    }
}