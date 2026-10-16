package com.queuedesk;

import javax.swing.*;
import java.time.LocalDateTime;

public class Ticket {
    private int id;
    private String title;
    private String description;
    private String requester;
    private LocalDateTime createdAt;
    private Priority priority;
    private Status status ;

    private static int nextID=1;

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

    public static int totalCreated(){
        return nextID-1;
    }
    public double ageInHours(){
        return java.time.Duration.between(createdAt,LocalDateTime.now()).toHours();
    }

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

}