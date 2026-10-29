package com.queuedesk;

import java.util.*;

public class Repository <T extends Ticket>{
    private List<T> items = new ArrayList<>();

    //methods
    public void add(T item){
        items.add(item);
    }

    public List<T> all(){
        return Collections.unmodifiableList(items);
    }

    public T findById(int id){
        for(T item : items){
            if(item.getID()==id){
                return item;
            }
        }
        throw new TicketNotFoundException("Ticket with ID " + id + " not found");
    }

    public Map<String,List<T>> groupByAssignee(){
        Map<String,List<T>> groupMap= new HashMap<>();
        for(T item : items){
            String key = item.getAssignee()==null ? "Unassigned" : item.getAssignee();
            if(!groupMap.containsKey(key)){
                groupMap.put(key,new ArrayList<>());
            }
            groupMap.get(key).add(item);
        }
        return groupMap;
    }


}
