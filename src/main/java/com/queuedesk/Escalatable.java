package com.queuedesk;

public interface Escalatable {
    void escalate();
    boolean isEscalated();
    int escalationLevel();

    default String escalationBadge(){
        if(isEscalated()){
            return "This ticket has been escalated to level " + escalationLevel() ;
        }
        else {
            return "";
        }

    }

}
