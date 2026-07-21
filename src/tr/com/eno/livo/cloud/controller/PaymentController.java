package tr.com.eno.livo.cloud.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/** Payment is fail-closed until an idempotent, audited provider workflow is implemented. */
@Controller
public class PaymentController {

	@RequestMapping(value = "/pricing/paypalPayment", method = RequestMethod.POST,
			produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public ResponseEntity<String> paypalPayment() {
		return unavailable();
	}

	@RequestMapping(value = "/pricing/paypalPayment/returnSuccess", method = RequestMethod.GET,
			produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public ResponseEntity<String> payReturnSuccess() {
		return unavailable();
	}

	@RequestMapping(value = "/pricing/paypalPayment/returnFail", method = RequestMethod.GET,
			produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public ResponseEntity<String> payReturnFail() {
		return unavailable();
	}

	private ResponseEntity<String> unavailable() {
		return new ResponseEntity<String>(
				"Payment is not implemented. No charge or platform change was made.\n",
				HttpStatus.NOT_IMPLEMENTED);
	}
}
