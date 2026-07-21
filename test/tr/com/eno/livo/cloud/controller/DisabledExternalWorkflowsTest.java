package tr.com.eno.livo.cloud.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class DisabledExternalWorkflowsTest {

	@Test
	public void cloudProvisioningFailsClosedWithoutClaimingSuccess() {
		ResponseEntity<String> response = new AWSController().createInstance();
		assertEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
		assertTrue(response.getBody().contains("No infrastructure was changed"));
	}

	@Test
	public void paymentFailsClosedWithoutClaimingACharge() {
		ResponseEntity<String> response = new PaymentController().paypalPayment();
		assertEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
		assertTrue(response.getBody().contains("No charge or platform change was made"));
	}
}
