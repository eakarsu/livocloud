package tr.com.eno.livo.cloud.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/** Cloud provisioning remains deliberately disabled until a governed workflow exists. */
@Controller
public class AWSController {

	@RequestMapping(value = "/createInstance", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public ResponseEntity<String> createInstance() {
		return new ResponseEntity<String>(
				"Cloud provisioning is not implemented. No infrastructure was changed.\n",
				HttpStatus.NOT_IMPLEMENTED);
	}
}
