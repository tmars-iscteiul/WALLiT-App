package com.example.wallit_app.networking;

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

    ;

    private final int messageID;

    ServiceMessages(int messageID) {
        this.messageID = messageID;
    }

    public int getMessageID() {
        return messageID;
    }

    public String getMessageString() {
        return String.valueOf(messageID);
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