package com.karaiman.auction.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.karaiman.auction.entity.Account;
import com.karaiman.auction.form.RegistrationForm;
 
@Transactional
@Repository
public class AccountDAO {
 
    @Autowired
    private SessionFactory sessionFactory;
 
    public Account findAccount(String userName) {
        Session session = this.sessionFactory.getCurrentSession();
        return session.find(Account.class, userName);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void save(RegistrationForm regForm) {
 
        Session session = this.sessionFactory.getCurrentSession();
        String userName = regForm.getUserName();
        Account account = null;
 
        boolean isNew = false;
        if (userName != null) {
            account = this.findAccount(userName);
        }
        if (account == null) {
            isNew = true;
            account = new Account();
        }
        account.setUserName(userName);
        account.setEncrytedPassword(regForm.getPassword());
 
        if (isNew) {
            session.persist(account);
        }
        // If error in DB, Exceptions will be thrown out immediately
        session.flush();
    }
}