package com.queuedesk.model;

public class FeatureRequestTicket extends Ticket{
    private int votes;
    private String businessJustification;

    //getters
    public int getVotes(){
        return votes;
    }

    public String getBusinessJustification(){
        return businessJustification;
    }

    //for jackson only
    private FeatureRequestTicket(){}

    //constructor
    public FeatureRequestTicket(String title, String description,String requester, Priority priority,int votes,String businessJustification ){
        super(title, description, requester, priority);
        this.votes=votes;
        this.businessJustification=businessJustification;
    }

    @Override
    public double estimateEffortHours(){
        return 10+ (votes*0.5);
    }
}
