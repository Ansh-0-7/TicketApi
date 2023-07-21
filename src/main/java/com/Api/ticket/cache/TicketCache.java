package com.Api.ticket.cache;

import com.Api.ticket.model.TicketEntity;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component

public class TicketCache {
    private static final String TICKET_ID_TO_TICKET_MAP = "TICKET_ID_TO_TICKET_MAP";
    private static final String PAGE_NUMBER_TO_TICKET_MAP = "PAGE_NUMBER_TO_TICKET_MAP";
    @Autowired
    RedissonClient redissonClient;

    public TicketEntity getTicketByTicketId(int ticketId) {
        RMap<Integer, TicketEntity> map = redissonClient.getMap(TICKET_ID_TO_TICKET_MAP);
        return map.getOrDefault(ticketId, null);
    }

    public void putTicketToTicketIdTicketMap(TicketEntity ticketEntity) {
        RMap<Integer, TicketEntity> map = redissonClient.getMap(TICKET_ID_TO_TICKET_MAP);
        if (map != null) {
            map.put(ticketEntity.getId(), ticketEntity);
        }
    }

    public List<TicketEntity> getAllTickets(int pageNo) {
        RMap<String, List<TicketEntity>> map = redissonClient.getMap(PAGE_NUMBER_TO_TICKET_MAP);
        List<TicketEntity> ticketData = new ArrayList<>();
        if (map.containsKey(String.valueOf(pageNo)))
            ticketData = map.get(String.valueOf(pageNo));
        return ticketData;
    }

    public void ticketListToCache(int pageNo, List<TicketEntity> ticketList) {
        RMap<String, List<TicketEntity>> map = redissonClient.getMap(PAGE_NUMBER_TO_TICKET_MAP);
        map.put(String.valueOf(pageNo),ticketList);
    }
}
