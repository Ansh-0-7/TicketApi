package com.Api.ticket.cache;

import com.Api.ticket.model.TicketEntity;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketCache {
    private static final String TICKET_ID_TO_TICKET_MAP = "TICKET_ID_TO_TICKET_MAP";
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
}
