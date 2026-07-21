package tr.com.eno.livo.cloud.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HealthController {

	private static final Logger LOGGER = LoggerFactory.getLogger(HealthController.class);

	@Autowired
	private DataSource dataSource;

	@RequestMapping(value = "/health", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public ResponseEntity<String> health() {
		try (Connection connection = dataSource.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery("SELECT 1")) {
			if (result.next() && result.getInt(1) == 1) {
				return new ResponseEntity<String>("ok\n", HttpStatus.OK);
			}
		} catch (Exception ex) {
			LOGGER.warn("Database health check failed", ex);
		}
		return new ResponseEntity<String>("unavailable\n", HttpStatus.SERVICE_UNAVAILABLE);
	}
}
