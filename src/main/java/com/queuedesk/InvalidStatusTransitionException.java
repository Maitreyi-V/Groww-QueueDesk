package com.queuedesk;

public class InvalidStatusTransitionException extends RuntimeException{
    InvalidStatusTransitionException(String msg){
        super(msg);
    }
}
