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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import tr.com.eno.livo.cloud.delegate.UserDelegate;
import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.service.AccountRegistrationService;
import tr.com.eno.livo.cloud.service.AccountRegistrationService.AccountAlreadyExistsException;
import tr.com.eno.livo.cloud.service.AccountRegistrationService.InvalidVerificationTokenException;
import tr.com.eno.livo.cloud.service.AccountRegistrationService.RegistrationRateLimitException;
import tr.com.eno.livo.cloud.service.AccountRegistrationService.VerificationChallenge;
import tr.com.eno.livo.cloud.utility.AppConstants;
import tr.com.eno.livo.cloud.viewBean.SendEmailBean;
import tr.com.eno.livo.cloud.viewBean.SignUpBean;
import tr.com.eno.livo.cloud.viewBean.UserBean;

@Controller
public class SignUpController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SignUpController.class);

	@Autowired
	private AccountRegistrationService accountRegistrationService;

	@Autowired
	private UserDelegate userDelegate;

	@RequestMapping(value = "/signUp", method = RequestMethod.POST)
	public ModelAndView executeSignUp(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("signUpBean") SignUpBean signUpBean) {
		try {
			User user = accountRegistrationService.startRegistration(
					signUpBean.getName(), signUpBean.getEmail(), request.getRemoteAddr());
			request.getSession(true).setAttribute("loggedInUser", user);

			UserBean userBean = new UserBean();
			userBean.setName(user.getName());
			userBean.setEmail(user.getEmail());
			ModelAndView model = new ModelAndView("userInfo");
			model.addObject("userBean", userBean);
			model.addObject("name", user.getName());
			model.addObject("email", user.getEmail());
			return model;
		} catch (AccountAlreadyExistsException ex) {
			ModelAndView model = homeWithForm(signUpBean);
			model.addObject("isRegisteredEmail", true);
			model.addObject("userControlResponse", "An account already exists for that email address.");
			return model;
		} catch (RegistrationRateLimitException ex) {
			response.setStatus(429);
			ModelAndView model = homeWithForm(signUpBean);
			model.addObject("validationError", "Too many registration attempts. Try again in 15 minutes.");
			return model;
		} catch (IllegalArgumentException ex) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			ModelAndView model = homeWithForm(signUpBean);
			model.addObject("validationError", ex.getMessage());
			return model;
		} catch (SQLException ex) {
			LOGGER.error("Unable to start account registration", ex);
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			return error("Registration is temporarily unavailable. Please try again.");
		}
	}

	@RequestMapping(value = "/updateUserInfos", method = RequestMethod.POST)
	public ModelAndView updateUserInfos(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("userBean") UserBean userBean) {
		User sessionUser = sessionUser(request);
		if (sessionUser == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return new ModelAndView("redirect:/login");
		}

		try {
			VerificationChallenge challenge = accountRegistrationService.completeRegistration(
					sessionUser.getUserId(), userBean.getName(), userBean.getCompanyName(), userBean.getUserPassword());
			sessionUser.setName(userBean.getName().trim());
			sessionUser.setCompanyName(userBean.getCompanyName().trim());
			sessionUser.setAccountState(AppConstants.UserState.UNVERIFIED.value);

			boolean delivered = deliver(challenge);
			return verificationView(challenge.getEmail(), delivered);
		} catch (IllegalArgumentException | IllegalStateException ex) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			ModelAndView model = profileView(sessionUser);
			model.addObject("validationError", ex.getMessage());
			return model;
		} catch (SQLException ex) {
			LOGGER.error("Unable to complete account registration for user {}", sessionUser.getUserId(), ex);
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			return error("Account details could not be saved. Please try again.");
		}
	}

	@RequestMapping(value = "/verifyEmail", method = RequestMethod.GET)
	public ModelAndView verifyEmail(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("email") String email, @RequestParam("token") String token) {
		try {
			User user = accountRegistrationService.verify(email, token);
			request.getSession(true).setAttribute("loggedInUser", user);
			SignUpBean signUpBean = new SignUpBean();
			signUpBean.setEmail(user.getEmail());
			ModelAndView model = new ModelAndView("termsofuse");
			model.addObject("email", user.getEmail());
			model.addObject("signUpBean", signUpBean);
			return model;
		} catch (InvalidVerificationTokenException | IllegalArgumentException ex) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return error("This verification link is invalid, expired, or has already been used.");
		} catch (SQLException ex) {
			LOGGER.error("Unable to verify account email", ex);
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			return error("Verification is temporarily unavailable. Please try again.");
		}
	}

	@RequestMapping(value = "/resendEmail", method = RequestMethod.POST)
	public ModelAndView resendEmail(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("sendEmailBean") SendEmailBean ignored) {
		User sessionUser = sessionUser(request);
		if (sessionUser == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return new ModelAndView("redirect:/login");
		}

		try {
			VerificationChallenge challenge = accountRegistrationService.resendVerification(sessionUser.getUserId());
			return verificationView(challenge.getEmail(), deliver(challenge));
		} catch (IllegalStateException ex) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return error(ex.getMessage());
		} catch (SQLException ex) {
			LOGGER.error("Unable to reissue verification for user {}", sessionUser.getUserId(), ex);
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			return error("A new verification link could not be created. Please try again.");
		}
	}

	@RequestMapping(value = "/termsVerified", method = RequestMethod.POST)
	public ModelAndView termsVerified(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("signUpBean") SignUpBean ignored) {
		User sessionUser = sessionUser(request);
		if (sessionUser == null || sessionUser.getAccountState() != AppConstants.UserState.VERIFIED_INACTIVE.value) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return error("Verify your account before accepting the terms.");
		}
		try {
			if (!userDelegate.updateUserStateByEmail(
					sessionUser.getEmail(), AppConstants.UserState.VERIFIED_ACTIVE.value)) {
				throw new SQLException("Account activation did not update the account");
			}
			sessionUser.setAccountState(AppConstants.UserState.VERIFIED_ACTIVE.value);
			return new ModelAndView("pricing");
		} catch (SQLException ex) {
			LOGGER.error("Unable to activate verified user {}", sessionUser.getUserId(), ex);
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			return error("Account activation is temporarily unavailable. Please try again.");
		}
	}

	private boolean deliver(VerificationChallenge challenge) {
		try {
			AppConstants.sendVerificationMessage(challenge.getEmail(),
					AppConstants.verificationEmailContent(challenge.getEmail(), challenge.getToken()));
			return true;
		} catch (RuntimeException ex) {
			LOGGER.error("Verification email delivery failed for user domain; retry remains available", ex);
			return false;
		}
	}

	private ModelAndView verificationView(String email, boolean delivered) {
		SendEmailBean sendEmailBean = new SendEmailBean();
		sendEmailBean.setEmail(email);
		ModelAndView model = new ModelAndView("verifyEmail");
		model.addObject("email", email);
		model.addObject("sendEmailBean", sendEmailBean);
		model.addObject("deliveryFailed", !delivered);
		return model;
	}

	private ModelAndView profileView(User user) {
		UserBean bean = new UserBean();
		bean.setName(user.getName());
		bean.setEmail(user.getEmail());
		bean.setCompanyName(user.getCompanyName());
		ModelAndView model = new ModelAndView("userInfo");
		model.addObject("userBean", bean);
		model.addObject("name", user.getName());
		model.addObject("email", user.getEmail());
		return model;
	}

	private ModelAndView homeWithForm(SignUpBean bean) {
		ModelAndView model = new ModelAndView("home");
		model.addObject("signUpBean", bean);
		return model;
	}

	private ModelAndView error(String message) {
		ModelAndView model = new ModelAndView("error");
		model.addObject("errorText", message);
		return model;
	}

	private User sessionUser(HttpServletRequest request) {
		Object value = request.getSession(false) == null
				? null : request.getSession(false).getAttribute("loggedInUser");
		return value instanceof User ? (User) value : null;
	}
}
