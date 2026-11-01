package com.queuedesk.io;

import com.queuedesk.exception.InvalidTicketDataException;
import com.queuedesk.model.Ticket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class TicketJsonStore {
    private static final ObjectMapper MAPPER= new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.FIELD,JsonAutoDetect.Visibility.ANY);


    public static void save(List<Ticket> tickets, Path file) throws InvalidTicketDataException{
        try{
            MAPPER.writerFor(new TypeReference<List<Ticket>>(){})
                    .writeValue(file.toFile(),tickets);
        }catch (IOException e){
            throw new InvalidTicketDataException("Could not save the tickets to "+file ,e);
        }
    }

    public static List<Ticket> load(Path file) throws InvalidTicketDataException{
        try{
            List<Ticket> tickets= MAPPER.readValue(file.toFile(), new TypeReference<List<Ticket>>() {
            });
            for(Ticket ticket : tickets){
                Ticket.ensureNextIdAfter(ticket.getID());
            }
            return tickets;
        }catch(IOException e){
            throw new InvalidTicketDataException("Could not load the tickets from the file",e);
        }
    }
}
