package com.karaiman.shoppingcart.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import com.karaiman.shoppingcart.form.BidForm;
import com.karaiman.shoppingcart.form.ProductForm;
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

import com.karaiman.shoppingcart.dao.AccountDAO;
import com.karaiman.shoppingcart.dao.ProductDAO;
import com.karaiman.shoppingcart.entity.Product;
import com.karaiman.shoppingcart.form.ProductFormValidator;
import com.karaiman.shoppingcart.form.RegistrationForm;
import com.karaiman.shoppingcart.model.ProductInfo;
import com.karaiman.shoppingcart.pagination.PaginationResult;

@Controller
@Transactional
public class MainController {

	@Autowired
	private ProductDAO productDAO;

	@Autowired
	private AccountDAO accountDAO;

	@Autowired
	private ProductFormValidator productFormValidator;

	@InitBinder
	public void myInitBinder(WebDataBinder dataBinder) {
		Object target = dataBinder.getTarget();
		if (target == null) {
			return;
		}
		System.out.println("Target=" + target);

		if (target.getClass() == ProductInfo.class) {

		} else if (target.getClass() == ProductForm.class) {
			dataBinder.setValidator(productFormValidator);
		}

	}

	@RequestMapping("/403")
	public String accessDenied() {
		return "/403";
	}

	// GET: Show Login Page
	   @RequestMapping(value = { "/registration" }, method = RequestMethod.GET)
	   public String register(Model model) {

	      return "/registration";
	   }

	@RequestMapping(value = { "/registration" }, method = RequestMethod.POST)
	   public String accountSave(Model model, //
	         @ModelAttribute("registrationForm") @Validated RegistrationForm regForm, //
	         BindingResult result, //
	         final RedirectAttributes redirectAttributes) {

	      if (result.hasErrors()) {
	         return "/registartion";
	      }
	      try {
	         accountDAO.save(regForm);
	      } catch (Exception e) {
	         Throwable rootCause = ExceptionUtils.getRootCause(e);
	         String message = rootCause.getMessage();
	         model.addAttribute("errorMessage", message);
	         // Show product form.
	         return "/registration";
	      }

	      return "redirect:/productList";
	   }

	@RequestMapping("/")
	public String home() {
		// return "index";
		return "login";
	}
	
	@RequestMapping("/home")
	public String home_page() {
		return "index";
	}
	

	// Product List
	@RequestMapping({ "/productList" })
	public String listProductHandler(Model model, //
			@RequestParam(value = "name", defaultValue = "") String likeName,
			@RequestParam(value = "page", defaultValue = "1") int page) {
		final int maxResult = 5;
		final int maxNavigationPage = 10;

		PaginationResult<ProductInfo> result = productDAO.queryProducts(page, //
				maxResult, maxNavigationPage, likeName);

		model.addAttribute("paginationProducts", result);
		return "productList";
	}

	@RequestMapping({ "/placeBid" })
	public String listProductHandler(HttpServletRequest request, Model model,
			@RequestParam(value = "code", defaultValue = "") String code,
			@RequestParam(value = "name", defaultValue = "") String likeName,
			@RequestParam(value = "page", defaultValue = "1") int page) {

		final int maxResult = 5;
		final int maxNavigationPage = 10;

		PaginationResult<ProductInfo> result = productDAO.queryProducts(page, //
				maxResult, maxNavigationPage, likeName);

		BidForm bidForm = new BidForm();

		model.addAttribute("codeToEdit", code);
		model.addAttribute("bidForm", bidForm);
		model.addAttribute("paginationProducts", result);

		return "productList";
	}

	@RequestMapping(value = { "/placeBidSubmit" }, method = RequestMethod.POST)
	public String listProductHandler(HttpServletRequest request, Model model,
									 @ModelAttribute("bidForm") BidForm bidForm,
									 BindingResult result) {

		if((bidForm.getNewPrice() - bidForm.getPrice()) > 1) {
			productDAO.saveBid(bidForm);
		}

		return "redirect:/productList";
	}

	// GET: Show product.
	   @RequestMapping(value = { "/product" }, method = RequestMethod.GET)
	   public String product(Model model, @RequestParam(value = "code", defaultValue = "") String code) {

		   UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		   ProductForm productForm = null;

		   Product product = productDAO.findProduct(userDetails.getUsername());
		   if (product != null) {
		   		productForm = new ProductForm(product);
		   }
		   if (productForm == null) {
		   		productForm = new ProductForm();
	         	productForm.setNewProduct(true);
		   }

		   model.addAttribute("productForm", productForm);
		   return "/product";
	   }

	   // POST: Save product
	   @RequestMapping(value = { "/product" }, method = RequestMethod.POST)
	   public String productSave(Model model, //
	         @ModelAttribute("productForm") @Validated ProductForm productForm, //
	         BindingResult result, //
	         final RedirectAttributes redirectAttributes) {

	      if (result.hasErrors()) {
	         return "/product";
	      }
	      try {
	         productDAO.save(productForm);
	      } catch (Exception e) {
	         Throwable rootCause = ExceptionUtils.getRootCause(e);
	         String message = rootCause.getMessage();
	         model.addAttribute("errorMessage", message);
	         // Show product form.
	         return "/product";
	      }

	      return "redirect:/productList";
	   }

	@RequestMapping(value = { "/productImage" }, method = RequestMethod.GET)
	public void productImage(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("code") String code) throws IOException {
		Product product = null;
		if (code != null) {
			product = this.productDAO.findProduct(code);
		}
		if (product != null && product.getImage() != null) {
			response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
			response.getOutputStream().write(product.getImage());
		}
		response.getOutputStream().close();
	}

}
