package com.queuedesk;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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


    //constructors
    public Ticket(String title,String description ,String requester,Priority priority){
        this.title=title;
        this.description= description;
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

    @Override
    public int compareTo(Ticket other){
        if(this.priority!=other.priority){
            return other.priority.compareTo(this.priority);
        }
        return other.createdAt.compareTo(this.createdAt);
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

    public Status getStatus(){
        return status;
    }

    public LocalDateTime getCreatedAT(){
        return createdAt;
    }

    public String getAssignee(){
        return assignee;
    }

    public Set<String> getTags(){
        return tags;
    }




    //setters
    public void setDescription(String description){
        this.description=description;
    }
    public void setStatus(Status status){
        this.status=status;
    }
    public void setPriority(Priority priority){
        this.priority=priority;
    }
    public void setAssignee(String assignee){
        this.assignee=assignee;
    }

}