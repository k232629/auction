package com.karaiman.shoppingcart.controller;


import java.util.List;
 
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.karaiman.shoppingcart.dao.ProductDAO;
import com.karaiman.shoppingcart.entity.Product;
import com.karaiman.shoppingcart.form.ProductForm;
import com.karaiman.shoppingcart.form.ProductFormValidator;
import com.karaiman.shoppingcart.pagination.PaginationResult;
 
@Controller
@Transactional
public class AdminController {
 
   @Autowired
   private ProductDAO productDAO;
 
   @Autowired
   private ProductFormValidator productFormValidator;
 
   @InitBinder
   public void myInitBinder(WebDataBinder dataBinder) {
      Object target = dataBinder.getTarget();
      if (target == null) {
         return;
      }
      System.out.println("Target=" + target);
 
      if (target.getClass() == ProductForm.class) {
         dataBinder.setValidator(productFormValidator);
      }
   }
 
   // GET: Show Login Page
   @RequestMapping(value = { "/admin/login" }, method = RequestMethod.GET)
   public String login(Model model) {
 
      return "login";
   }
   
   @RequestMapping(value = { "/admin/accountInfo" }, method = RequestMethod.GET)
   public String accountInfo(Model model) {
 
      UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      System.out.println(userDetails.getPassword());
      System.out.println(userDetails.getUsername());
      System.out.println(userDetails.isEnabled());
 
      model.addAttribute("userDetails", userDetails);
      return "accountInfo";
   }
 
   // GET: Show product.
   @RequestMapping(value = { "/admin/product" }, method = RequestMethod.GET)
   public String product(Model model, @RequestParam(value = "code", defaultValue = "") String code) {
      ProductForm productForm = null;
 
      if (code != null && code.length() > 0) {
         Product product = productDAO.findProduct(code);
         if (product != null) {
            productForm = new ProductForm(product);
         }
      }
      if (productForm == null) {
         productForm = new ProductForm();
         productForm.setNewProduct(true);
      }
      model.addAttribute("productForm", productForm);
      return "product";
   }

   // GET: Show product.
   @RequestMapping(value = { "/admin/deleteProduct" }, method = RequestMethod.GET)
   public String deleteProduct(Model model, @RequestParam(value = "code", defaultValue = "") String code) {

      productDAO.delete(code);
      return "redirect:/productList";
   }
 
   // POST: Save product
   @RequestMapping(value = { "/admin/product" }, method = RequestMethod.POST)
   public String productSave(Model model, //
         @ModelAttribute("productForm") @Validated ProductForm productForm, //
         BindingResult result, //
         final RedirectAttributes redirectAttributes) {
 
      if (result.hasErrors()) {
         return "product";
      }
      try {
         productDAO.save(productForm);
      } catch (Exception e) {
         Throwable rootCause = ExceptionUtils.getRootCause(e);
         String message = rootCause.getMessage();
         model.addAttribute("errorMessage", message);
         // Show product form.
         return "product";
      }
 
      return "redirect:/productList";
   }
 
}