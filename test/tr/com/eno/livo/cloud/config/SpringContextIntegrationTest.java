package tr.com.eno.livo.cloud.config;

import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.After;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import tr.com.eno.livo.cloud.controller.HealthController;
import tr.com.eno.livo.cloud.service.AccountRegistrationService;

public class SpringContextIntegrationTest {

	private static final String[] PROPERTIES = {
		"LIVOCLOUD_DB_DRIVER", "LIVOCLOUD_DB_URL", "LIVOCLOUD_DB_USERNAME", "LIVOCLOUD_DB_PASSWORD"
	};

	@After
	public void clearProperties() {
		for (String property : PROPERTIES) {
			System.clearProperty(property);
		}
	}

	@Test
	public void webApplicationContextMigratesAndWiresTheWorkflow() {
		System.setProperty("LIVOCLOUD_DB_DRIVER", "org.h2.Driver");
		System.setProperty("LIVOCLOUD_DB_URL", "jdbc:h2:mem:" + UUID.randomUUID()
				+ ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE");
		System.setProperty("LIVOCLOUD_DB_USERNAME", "sa");
		System.setProperty("LIVOCLOUD_DB_PASSWORD", "");

		XmlWebApplicationContext context = new XmlWebApplicationContext();
		context.setServletContext(new MockServletContext());
		context.setConfigLocation("classpath:resource/springWeb.xml");
		try {
			context.refresh();
			assertNotNull(context.getBean(AccountRegistrationService.class));
			assertNotNull(context.getBean(HealthController.class));
		} finally {
			context.close();
		}
	}
}
