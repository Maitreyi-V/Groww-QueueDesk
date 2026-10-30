package com.queuedesk;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include =JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value=BugTicket.class,name = "bug"),
        @JsonSubTypes.Type(value=FeatureRequestTicket.class,name="feature"),
        @JsonSubTypes.Type(value=AccessRequestTicket.class,name="access")
})
public abstract class Ticket implements Comparable<Ticket>{
    private int id;
    private String title;
    private String description;
    private String requester;
    private LocalDateTime createdAt;
    private Priority priority;
    private Status status ;

    private static int nextID=1;

    private String assignee;
    private Set<String> tags= new HashSet<>();


    //for Jackson only
    protected Ticket(){}

    //constructors
    public Ticket(String title,String description ,String requester,Priority priority){
        this.title=title;
        setDescription(description);
        this.requester=requester;
        this.priority=priority;
        this.createdAt=LocalDateTime.now();
        status=Status.OPEN;
        this.id=nextID++;
    }
    public Ticket(String title, String requester){
        this(title,"No Description" , requester,Priority.LOW);
    }

    //methods
    public static int totalCreated(){
        return nextID-1;
    }
    public double ageInHours(){
        return java.time.Duration.between(createdAt,LocalDateTime.now()).toHours();
    }

    public void addTag(String tag){
        tags.add(tag);
    }



    public void transitionTo(Status newStatus){
        // Rule 1: Cannot change status if already CLOSED
        if(this.status==Status.CLOSED ){
            throw new InvalidStatusTransitionException("Cannot change status of a closed/cancelled ticket");
        }
        // Rule 2: Cannot jump directly from OPEN to CLOSED
        if(this.status==Status.OPEN && newStatus==Status.CLOSED){
            throw new InvalidStatusTransitionException("Ticket must be IN_PROGRESS or RESOLVED before it can be CLOSED");
        }
        // Apply the valid transition
        this.status=newStatus;
    }
    @Override
    public int compareTo(Ticket other){
        if(this.priority!=other.priority){
            return other.priority.compareTo(this.priority);
        }
        return this.createdAt.compareTo(other.createdAt);
    }

    @Override
    public String toString(){
        return String.format("# %s | %s | %s | %s" ,id,title,priority,status);
    }

    public String shortDescription(int maxlength){
        if(description.length()>maxlength){
            return description.substring(0,maxlength-3)+"...";
        }else{
            return description;
        }
    }

    //abstract
    public abstract double estimateEffortHours();

    //getters
    public int getID(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String getRequester(){
        return requester;
    }

    public Priority getPriority(){
        return priority;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public String getAssignee(){
        return assignee;
    }

    public Set<String> getTags(){
        return Collections.unmodifiableSet(tags);
    }




    //setters
    public void setDescription(String description){
        if(description==null || description.isBlank() ){
            throw new IllegalArgumentException("Decription cannot be blank");
        }
        this.description=description.trim();
    }
    public void setPriority(Priority priority){
        this.priority=priority;
    }
    public void setAssignee(String assignee){
        this.assignee=assignee;
    }

}