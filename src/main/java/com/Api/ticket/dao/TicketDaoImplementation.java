package com.Api.ticket.dao;

import com.Api.ticket.config.HibernateConfiguration;
import com.Api.ticket.model.TicketEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class TicketDaoImplementation {
    HibernateConfiguration hibernateConfiguration;
    @Autowired
    SessionFactory sessionFactory;
    public final static org.slf4j.Logger logger= LoggerFactory.getLogger(TicketDaoImplementation.class);

    public List<TicketEntity> getAllTickets(int pageNo,int pageSize) {
        Session session = sessionFactory.openSession();
        try {
            int offset = (pageNo - 1) * pageSize;
            String hql = "FROM TicketEntity";
            Query query = session.createQuery(hql).setFirstResult(offset).setMaxResults(pageSize);
            return query.getResultList();
        }
        catch (Exception e){
            logger.info(e.getMessage());
            return null;
        }
        finally {
            session.close();
        }
    }


    public TicketEntity getTicketById(int id) {
        EntityManager entityManager = sessionFactory.createEntityManager();
        try {
            String hql="SELECT ticket FROM TicketEntity ticket WHERE ticket.id = :ID";
            TypedQuery<TicketEntity> query = entityManager.createQuery(hql, TicketEntity.class);
            query.setParameter("ID", id);
            List<TicketEntity> resultList = query.getResultList();
            if (resultList.isEmpty()) {
                return null;
            }
            return resultList.get(0);
        }
        catch (Exception e){
            logger.error(e.getMessage());
            throw new IllegalStateException("No ticket Found");
        }
        finally {
            entityManager.close();
        }
    }


    public TicketEntity addTicket(TicketEntity ticketEntity) {
        Session session = null;
        Transaction txn = null;
        try {
            session = sessionFactory.openSession();
            txn = session.beginTransaction();
            session.saveOrUpdate(ticketEntity);
            txn.commit();
            return ticketEntity;
        } catch (Exception e) {
            if (txn != null) {
                txn.rollback();
            }
            logger.error("Error : ", e);
            throw e;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Optional<TicketEntity> getTicketByCode(int ticketCode) {
        Session session = sessionFactory.openSession();
        try {
            Query query = session.createQuery("FROM TicketEntity WHERE ticketCode = :TICKET_CODE");
            query.setParameter("TICKET_CODE", ticketCode);
            List<TicketEntity> tickets = query.getResultList();
            return tickets.stream().findFirst();
        }
        catch (Exception e){
            logger.info(e.getMessage());
            return null;
        }
        finally {
            session.close();
        }
    }


    public List<TicketEntity> getTicketByStatus(String status) {
        Session session = sessionFactory.openSession();
        try {
            String hql="FROM TicketEntity WHERE status = :STATUS";
            TypedQuery<TicketEntity> query = session.createQuery(hql, TicketEntity.class);
            query.setParameter("STATUS", status);
            return query.getResultList();
        }
        catch (Exception e){
            logger.info(e.getMessage());
            return null;
        }
        finally {
            session.close();
        }
    }

}
