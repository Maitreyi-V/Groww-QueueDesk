package com.queuedesk;

import java.util.Comparator;

public class TicketComparators {
    // 1.Alphabetically by assignee name and then priority(descending)
    public static final Comparator<Ticket> BY_ASSIGNEE_THEN_PRIORITY = (t1,t2) ->{
        String a1=t1.getAssignee()==null ? "" : t1.getAssignee();
        String a2=t2.getAssignee()==null ? "" : t2.getAssignee();

        int assigneeComparision =a1.compareTo(a2);
        if(assigneeComparision !=0){
            return assigneeComparision;
        }
        return t2.getPriority().compareTo(t1.getPriority());
    };

    // 2.By status , then by Creation time
    public static final Comparator<Ticket> BY_STATUS_THEN_AGE = (t1,t2)->{
        int statusComparision=t1.getStatus().compareTo(t2.getStatus());
        if(statusComparision!=0){
            return statusComparision;
        }
        return t1.getCreatedAT().compareTo(t2.getCreatedAT());

    };

}
