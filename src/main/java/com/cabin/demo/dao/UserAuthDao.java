package com.cabin.demo.dao;

import com.cabin.demo.entity.auth.AuthProvider;
import com.cabin.demo.entity.auth.UserAuth;
import org.hibernate.SessionFactory;

public class UserAuthDao extends BaseDAO<UserAuth> {

    public UserAuthDao(SessionFactory sessionFactory) {
        super(sessionFactory, UserAuth.class);
    }

    public UserAuth findByEmail(String email) {
        try (var session = getSessionFactory().openSession()) {
            return session.createQuery("FROM UserAuth WHERE email = :email", UserAuth.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }

    public UserAuth findByProviderAndProviderId(AuthProvider provider, String providerId) {
        try (var session = getSessionFactory().openSession()) {
            return session.createQuery("FROM UserAuth WHERE provider = :provider AND providerUserId = :providerId", UserAuth.class)
                    .setParameter("provider", provider)
                    .setParameter("providerId", providerId)
                    .uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }
}
