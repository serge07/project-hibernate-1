package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {


    private final SessionFactory sessionFactory;

    @Autowired
    public PlayerRepositoryDB(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;

    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {

        try (Session session = sessionFactory.openSession()) {
            String nativeQuery = "SELECT * FROM player LIMIT :pageSize OFFSET :offset";
            return session.createNativeQuery(nativeQuery, Player.class)
                    .setParameter("pageSize", pageSize)
                    .setParameter("offset", pageNumber * pageSize)
                    .getResultList();

        }
    }

    @Override
    public int getAllCount() {
        try(Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createNamedQuery("Player.countAll", Long.class);
            return query.getSingleResult().intValue();

        }
    }

    @Override
    public Player save(Player player) {

        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(player);
            session.getTransaction().commit();
            return player;

        }
    }

    @Override
    public Player update(Player player) {
        try(Session session=sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(player);
            session.getTransaction().commit();
            return player;

        }
    }

    @Override
    public Optional<Player> findById(long id) {

        try(Session session=sessionFactory.openSession()) {
            String nativeQuery = "SELECT * FROM player WHERE id = :id";
            return session.createNativeQuery(nativeQuery, Player.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst();

        }
    }

    @Override
    public void delete(Player player) {
        try(Session session=sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(player);
            session.getTransaction().commit();
        }

    }

    @PreDestroy
    public void beforeStop() {
         if(sessionFactory != null) sessionFactory.close(); //sessionFactory.close();
    }
}