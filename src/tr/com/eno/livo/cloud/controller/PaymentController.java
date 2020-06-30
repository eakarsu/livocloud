package tr.com.eno.livo.cloud.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

import tr.com.eno.livo.cloud.delegate.PlatformDelegate;
import tr.com.eno.livo.cloud.delegate.UserDelegate;
import tr.com.eno.livo.cloud.entity.Platform;
import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.utility.AppConstants;
import tr.com.eno.livo.cloud.viewBean.SignUpBean;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentReq;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentRequestType;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsReq;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutReq;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutRequestType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.DoExpressCheckoutPaymentRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.PaymentActionCodeType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsItemType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsType;
import urn.ebay.apis.eBLBaseComponents.SetExpressCheckoutRequestDetailsType;

import com.paypal.exception.ClientActionRequiredException;
import com.paypal.exception.HttpErrorException;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.InvalidResponseDataException;
import com.paypal.exception.MissingCredentialException;
import com.paypal.exception.SSLConfigurationException;
import com.paypal.sdk.exceptions.OAuthException;

@Controller
public class PaymentController {

	@Autowired
	private UserDelegate userDelegate;

	@Autowired
	private PlatformDelegate platformDelegate;

	@RequestMapping(value = "/pricing/paypalPayment", method = RequestMethod.POST)
	public ModelAndView paypalPayment(HttpServletRequest request, HttpServletResponse response) {

		String sandboxUrl = "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";
		try {
			String paypalServiceToken = getPaypalServiceToken();
			sandboxUrl = "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=" + paypalServiceToken;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Paypal Payment can not be created.");
			e.printStackTrace();
		}

		ModelAndView model = new ModelAndView("pricing");
		model.addObject("redirectPaypal", true);
		model.addObject("redirectPaypalUrl", sandboxUrl);
		return model;
	}

	private String getServerAddress() {
		// TODO Auto-generated method stub
		// Default server Address - localhost
		String serverIpAddress = "localhost";
		try {
			// Setting server address
			serverIpAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			System.out.println("Server address unknown host exception :");
			e1.printStackTrace();
		}
		return serverIpAddress;
	}

	@RequestMapping(value = "/pricing/paypalPayment/returnSuccess", method = RequestMethod.GET)
	public ModelAndView payReturnSuccess(@RequestParam("token") String token, @RequestParam("PayerID") String payerID,
			HttpServletRequest request, HttpServletResponse response) {
		// paymentId=PAY-17G80672A5716960NKX4VJQY
		// &token=EC-3CW8637508486474B&PayerID=VK5WR54295SRL
		System.out.println("payReturnSuccess params : " + " token : " + token + " PayerID : " + payerID);

		DoExpressCheckoutPaymentResponseType doExpressCheckoutPaymentResponseType = doExpressCheckoutPayment(token, payerID);
		doExpressCheckoutPaymentResponseType.getDoExpressCheckoutPaymentResponseDetails().getPaymentInfo().get(0).getTransactionID();

		//  insert into Platforms table
//		User user = userDelegate.getUserByEmail(userMail);
		Platform platform = new Platform();
		platform.setPlatformName(UUID.randomUUID().toString());
		platform.setCloudProvider("Amazon");
//		platform.setUserId(user.getUserId());
		platform.setPlatformType(AppConstants.PlatformType.PRO.text);
		
		try {
			platformDelegate.insertPlatform(platform);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		// transactionHistory insert
		// Seats and infrastructure

		String homePath = System.getenv("AEON_HOME");

		File pltfmPropertiesFile = new File(homePath + File.separator + "conf", "pltfm.properties");
		if (pltfmPropertiesFile.exists()) {
			OutputStream out = null;
			try {
				Properties prop = new Properties();
				prop.load(new FileInputStream(pltfmPropertiesFile));
				prop.put("applicationLimit", "10");
				prop.put("clientLimit", "10");
				prop.put("developerLimit", "10");
				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(pltfmPropertiesFile), "UTF-8");
				prop.store(osw, null);
				osw.close();
				System.out.println("Updated pltfrm.properties file for enterpricePlan.");
			} catch (IOException e) {
				System.out.println("Can not update pltfrm.properties file " + e.getMessage());
			}

		}

		ModelAndView model = new ModelAndView("pricing");

		model.addObject("payPaypal", true);
		model.addObject("user", "");
		model.addObject("payerID", payerID);

		return model;
	}
	@RequestMapping(value = "/pricing/paypalPayment/returnFail", method = RequestMethod.GET)
	public ModelAndView payReturnFail(HttpServletRequest request, HttpServletResponse response, SignUpBean signUpBean) {

		ModelAndView model = new ModelAndView("pricing");

		model.addObject("payPaypal", false);

		return model;
	}

	// Map<String, String> sdkConfig = new HashMap<String, String>();
	// sdkConfig.put("mode", "sandbox");

	// String accessToken = token;
	// APIContext apiContext = new APIContext(accessToken);
	// apiContext.setConfigurationMap(sdkConfig);
	//
	// Payment payment = new Payment( );
	// PaymentExecution paymentExecute = new PaymentExecution();
	// paymentExecute.setPayerId("VK5WR54295SRL");
	// payment.execute(apiContext, paymentExecute);
	// Get the AEON home environment variable

	private DoExpressCheckoutPaymentResponseType doExpressCheckoutPayment(String token, String PayerID) {

		DoExpressCheckoutPaymentResponseType doExpressCheckoutPaymentResponse = null;

		GetExpressCheckoutDetailsRequestType getExpressCheckoutDetailsRequest = new GetExpressCheckoutDetailsRequestType(token);
		getExpressCheckoutDetailsRequest.setVersion("104.0");

		GetExpressCheckoutDetailsReq getExpressCheckoutDetailsReq = new GetExpressCheckoutDetailsReq();
		getExpressCheckoutDetailsReq.setGetExpressCheckoutDetailsRequest(getExpressCheckoutDetailsRequest);

		Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put("mode", "sandbox");
		sdkConfig.put("acct1.UserName", "gokhan-facilitator_api1.livomobile.com");
		sdkConfig.put("acct1.Password", "PPRZE9AW7W7RH2RD");
		sdkConfig.put("acct1.Signature", "AFcWxV21C7fd0v3bYYYRCpSSRl31AvLiOz.qXc7G4.8PkBUT3Qhu-ekz");
		PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(sdkConfig);
		try {
			GetExpressCheckoutDetailsResponseType getExpressCheckoutDetailsResponse = service
					.getExpressCheckoutDetails(getExpressCheckoutDetailsReq);
			System.out.println("getExpressCheckoutDetailsResponse.toString() : " + getExpressCheckoutDetailsResponse.toString());

			getExpressCheckoutDetailsResponse.getGetExpressCheckoutDetailsResponseDetails();

			PaymentDetailsType paymentDetail = new PaymentDetailsType();
			paymentDetail.setNotifyURL("http://replaceIpnUrl.com");
			BasicAmountType orderTotal = new BasicAmountType();
			orderTotal.setValue(String.valueOf(125.00));
			orderTotal.setCurrencyID(CurrencyCodeType.fromValue("USD"));
			paymentDetail.setOrderTotal(orderTotal);
			paymentDetail.setPaymentAction(PaymentActionCodeType.fromValue("Sale"));

			System.out.println("paymentDetail.getTransactionId()  : " + paymentDetail.getTransactionId());

			List<PaymentDetailsType> paymentDetails = new ArrayList<PaymentDetailsType>();
			paymentDetails.add(paymentDetail);

			DoExpressCheckoutPaymentRequestDetailsType doExpressCheckoutPaymentRequestDetails = new DoExpressCheckoutPaymentRequestDetailsType();
			doExpressCheckoutPaymentRequestDetails.setToken(token);
			doExpressCheckoutPaymentRequestDetails.setPayerID(PayerID);
			doExpressCheckoutPaymentRequestDetails.setPaymentDetails(paymentDetails);

			DoExpressCheckoutPaymentRequestType doExpressCheckoutPaymentRequest = new DoExpressCheckoutPaymentRequestType(
					doExpressCheckoutPaymentRequestDetails);
			doExpressCheckoutPaymentRequest.setVersion("104.0");

			DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = new DoExpressCheckoutPaymentReq();
			doExpressCheckoutPaymentReq.setDoExpressCheckoutPaymentRequest(doExpressCheckoutPaymentRequest);

			return doExpressCheckoutPaymentResponse = service.doExpressCheckoutPayment(doExpressCheckoutPaymentReq);

			// System.out.println( "Transaction ID :  "
			// +doExpressCheckoutPaymentResponse.getDoExpressCheckoutPaymentResponseDetails().getPaymentInfo().get(0).getTransactionID());
			// System.out.println("doExpressCheckoutPaymentResponse getEbayTransactionID  : "
			// +doExpressCheckoutPaymentResponse.getDoExpressCheckoutPaymentResponseDetails().getPaymentInfo().get(0).getEbayTransactionID());
			// System.out.println("doExpressCheckoutPaymentResponse.toString()  :  "+doExpressCheckoutPaymentResponse.toString());

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return doExpressCheckoutPaymentResponse;

	}

	private String getPaypalServiceToken() throws SSLConfigurationException, InvalidCredentialException, HttpErrorException,
			InvalidResponseDataException, ClientActionRequiredException, MissingCredentialException, OAuthException, IOException,
			InterruptedException, ParserConfigurationException, SAXException {

		String serverIpAddress = getServerAddress();

		PaymentDetailsType paymentDetails = new PaymentDetailsType();
		paymentDetails.setPaymentAction(PaymentActionCodeType.fromValue("Sale"));
		PaymentDetailsItemType item = new PaymentDetailsItemType();
		BasicAmountType amt = new BasicAmountType();
		amt.setCurrencyID(CurrencyCodeType.fromValue("USD"));
		double itemAmount = 1.00;
		amt.setValue(String.valueOf(itemAmount));
		int itemQuantity = 1;
		item.setQuantity(itemQuantity);
		item.setName("item");
		item.setAmount(amt);

		List<PaymentDetailsItemType> lineItems = new ArrayList<PaymentDetailsItemType>();
		lineItems.add(item);
		paymentDetails.setPaymentDetailsItem(lineItems);
		BasicAmountType orderTotal = new BasicAmountType();
		orderTotal.setCurrencyID(CurrencyCodeType.fromValue("USD"));
		orderTotal.setValue(String.valueOf(itemAmount * itemQuantity));
		paymentDetails.setOrderTotal(orderTotal);
		List<PaymentDetailsType> paymentDetailsList = new ArrayList<PaymentDetailsType>();
		paymentDetailsList.add(paymentDetails);

		SetExpressCheckoutRequestDetailsType setExpressCheckoutRequestDetails = new SetExpressCheckoutRequestDetailsType();
		setExpressCheckoutRequestDetails.setReturnURL(AppConstants.CLOUD_SERVER_ADDRESS.replace("localhost", serverIpAddress)
				+ AppConstants.PAYPAL_PAYMENT_SUCCESS_METHOD);
		setExpressCheckoutRequestDetails.setCancelURL(AppConstants.CLOUD_SERVER_ADDRESS.replace("localhost", serverIpAddress)
				+ AppConstants.PAYPAL_PAYMENT_CANCEL_METHOD);

		setExpressCheckoutRequestDetails.setPaymentDetails(paymentDetailsList);

		SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(setExpressCheckoutRequestDetails);
		setExpressCheckoutRequest.setVersion("104.0");

		SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
		setExpressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutRequest);

		Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put("mode", "sandbox");
		sdkConfig.put("acct1.UserName", "gokhan-facilitator_api1.livomobile.com");
		sdkConfig.put("acct1.Password", "PPRZE9AW7W7RH2RD");
		sdkConfig.put("acct1.Signature", "AFcWxV21C7fd0v3bYYYRCpSSRl31AvLiOz.qXc7G4.8PkBUT3Qhu-ekz");
		PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(sdkConfig);

		SetExpressCheckoutResponseType setExpressCheckoutResponse = service.setExpressCheckout(setExpressCheckoutReq);
		return setExpressCheckoutResponse.getToken();
	}

}
