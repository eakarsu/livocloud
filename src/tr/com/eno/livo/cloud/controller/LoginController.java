package tr.com.eno.livo.cloud.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import tr.com.eno.livo.cloud.delegate.UserDelegate;
import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.viewBean.LoginBean;

@Controller
public class LoginController {
	
	@Autowired
	private UserDelegate userDelegate;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView displayLogin(HttpServletRequest request, HttpServletResponse response, LoginBean loginBean) {

		request.getSession().setAttribute("userMail", loginBean.getUsername());

		ModelAndView model = new ModelAndView("login");
		// LoginBean loginBean = new LoginBean();
		model.addObject("loginBean", loginBean);

		return model;
	}
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView executeLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@ModelAttribute("loginBean") LoginBean loginBean) {
		ModelAndView model = null;

		try {
			User isValidUser = userDelegate.isValidUser(loginBean.getUsername(), loginBean.getPassword(), 3);
			if (isValidUser.getUserId() != 0) {
				System.out.println("User Login Successful");
				
				request.getSession().setAttribute("loggedInUser", isValidUser);
				
				model = new ModelAndView("dashboard");
			 
				System.out.println("User set Session : " + loginBean.getUsername());
			} else {
				model = new ModelAndView("login");
				request.setAttribute("message", "Invalid credentials!!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}
}
