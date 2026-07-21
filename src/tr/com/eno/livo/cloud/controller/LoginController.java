package tr.com.eno.livo.cloud.controller;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.service.AccountRegistrationService;
import tr.com.eno.livo.cloud.utility.AppConstants;
import tr.com.eno.livo.cloud.viewBean.LoginBean;

@Controller
public class LoginController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private AccountRegistrationService accountRegistrationService;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView displayLogin() {
		ModelAndView model = new ModelAndView("login");
		model.addObject("loginBean", new LoginBean());
		return model;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView executeLogin(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("loginBean") LoginBean loginBean) {
		try {
			User user = accountRegistrationService.authenticate(loginBean.getUsername(), loginBean.getPassword(),
					AppConstants.UserState.VERIFIED_ACTIVE.value);
			if (user == null) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return invalidLogin(loginBean);
			}
			request.changeSessionId();
			request.getSession().setAttribute("loggedInUser", user);
			return new ModelAndView("dashboard");
		} catch (IllegalArgumentException ex) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return invalidLogin(loginBean);
		} catch (SQLException ex) {
			LOGGER.error("Login database lookup failed", ex);
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			ModelAndView model = new ModelAndView("login");
			model.addObject("loginBean", loginBean);
			model.addObject("message", "Login is temporarily unavailable. Please try again.");
			return model;
		}
	}

	private ModelAndView invalidLogin(LoginBean loginBean) {
		loginBean.setPassword(null);
		ModelAndView model = new ModelAndView("login");
		model.addObject("loginBean", loginBean);
		model.addObject("message", "Invalid email or password.");
		return model;
	}
}
