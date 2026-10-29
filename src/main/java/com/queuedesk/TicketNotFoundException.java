package com.queuedesk;

public class TicketNotFoundException extends RuntimeException{
    public TicketNotFoundException(String msg){
        super(msg);
    }
}
