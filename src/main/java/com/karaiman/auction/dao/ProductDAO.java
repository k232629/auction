package com.karaiman.auction.dao;

import java.io.IOException;
import java.util.Date;
 
import javax.persistence.NoResultException;

import com.karaiman.auction.form.BidForm;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.karaiman.auction.entity.Product;
import com.karaiman.auction.form.ProductForm;
import com.karaiman.auction.model.ProductInfo;
import com.karaiman.auction.pagination.PaginationResult;
 
@Transactional
@Repository
public class ProductDAO {
 
    @Autowired
    private SessionFactory sessionFactory;
 
    public Product findProduct(String code) {
        try {
            String sql = "Select e from " + Product.class.getName() + " e Where e.code =:code ";
 
            Session session = this.sessionFactory.getCurrentSession();
            Query<Product> query = session.createQuery(sql, Product.class);
            query.setParameter("code", code);
            return (Product) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
 
    public ProductInfo findProductInfo(String code) {
        Product product = this.findProduct(code);
        if (product == null) {
            return null;
        }
        return new ProductInfo(product.getCode(), product.getName(), product.getPrice(), product.getBidderCode());
    }
 
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void save(ProductForm productForm) {
 
        Session session = this.sessionFactory.getCurrentSession();
        String code = productForm.getCode();
 
        Product product = null;
 
        boolean isNew = false;
        if (code != null) {
            product = this.findProduct(code);
        }
        if (product == null) {
            isNew = true;
            product = new Product();
            product.setCreateDate(new Date());
        }
        product.setCode(code);
        product.setName(productForm.getName());
        product.setPrice(productForm.getPrice());
 
        if (productForm.getFileData() != null) {
            byte[] image = null;
            try {
                image = productForm.getFileData().getBytes();
            } catch (IOException e) {
            }
            if (image != null && image.length > 0) {
                product.setImage(image);
            }
        }
        if (isNew) {
            session.persist(product);
        }
        // If error in DB, Exceptions will be thrown out immediately
        session.flush();
    }

    @Transactional
    public void saveBid(BidForm bidForm) {

        Session session = this.sessionFactory.getCurrentSession();
        String code = bidForm.getCode();

        Product product = this.findProduct(code);

        product.setCode(code);
        product.setBidderCode(bidForm.getBidderCode());
        product.setPrice(bidForm.getNewPrice());

        session.flush();
    }

    @Transactional
    public void delete(String code) {

        Session session = this.sessionFactory.getCurrentSession();

        Product product = new Product();

        product.setCode(code);

        session.delete(product);
    }
 
    public PaginationResult<ProductInfo> queryProducts(int page, int maxResult, int maxNavigationPage) {
        String sql = "Select new " + ProductInfo.class.getName() //
                + "(p.code, p.name, p.price, p.bidderCode) " + " from "//
                + Product.class.getName() + " p ";
        sql += " order by p.createDate desc ";
        //
        Session session = this.sessionFactory.getCurrentSession();
        Query<ProductInfo> query = session.createQuery(sql, ProductInfo.class);

        return new PaginationResult<ProductInfo>(query, page, maxResult, maxNavigationPage);
    }
 
}