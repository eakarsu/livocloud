package tr.com.eno.livo.cloud.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import tr.com.eno.livo.cloud.delegate.UserDelegate;
import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.viewBean.SignUpBean;

@Controller
public class ApplicationController {
	
	@Autowired
	private UserDelegate userDelegate;
	
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public ModelAndView displayDashboard(HttpServletRequest request, HttpServletResponse response, SignUpBean signUpBean) {
		 
		ModelAndView model = new ModelAndView("dashboard");
		
		return model;
	}

	@RequestMapping(value = "/licensing", method = RequestMethod.GET)
	public ModelAndView displayLicensing(HttpServletRequest request, HttpServletResponse response, SignUpBean signUpBean) {

		User loggedInUser = (User) request.getSession().getAttribute("loggedInUser");
		ModelAndView model = new ModelAndView("licensing");
		model.addObject("user", loggedInUser);
		return model;
	}
 
}
