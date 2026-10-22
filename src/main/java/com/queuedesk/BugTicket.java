package com.queuedesk;

public class BugTicket extends Ticket implements Escalatable{
    private Severity severity ;
    private String stepsToReproduce;

    private int escalationLevel;
    private boolean escalatedState;

    //constructor
    public BugTicket(String title, String description,String requester, Priority priority, Severity severity, String stepsToReproduce){
        super(title,description,requester,priority);
        this.severity=severity;
        this.stepsToReproduce=stepsToReproduce;
    }

    //method

    //from abstract method
    @Override
    public double estimateEffortHours() {
        switch(severity){
            case CRITICAL:
                return 8.0;
            case HIGH:
                return 6.0;
            case MEDIUM:
                return 4.0;
            case LOW:
                return 2.0;
            default :
                return 2.0;
        }
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


    //getters
    public Severity getSeverity(){
        return severity;
    }
    public String getStepsToReproduce(){
        return stepsToReproduce;
    }
}
