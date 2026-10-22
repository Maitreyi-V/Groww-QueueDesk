package com.queuedesk;

public class AccessRequestTicket extends Ticket implements Escalatable{
    private String systemName;
    private String accessLevel;

    private int escalationLevel=0;
    private boolean escalatedState=false;


    //constructor
    public AccessRequestTicket(String title, String description,String requester, Priority priority,String systemName,String accessLevel ){
        super(title, description, requester, priority);
        this.systemName=systemName;
        this.accessLevel=accessLevel;
    }

    //from abstract class
    @Override
    public double estimateEffortHours(){
        return 1.0;
    }

    //from interface
    @Override
    public void escalate(){
        escalationLevel++;
        escalatedState=true;
    }

    @Override
    public boolean isEscalated(){
        return escalatedState;
    }

    @Override
    public int escalationLevel(){
        return escalationLevel;
    }

}
