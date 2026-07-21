package tr.com.eno.livo.cloud.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

/** Applies versioned schema changes before any DAO is allowed to serve traffic. */
public class DatabaseMigration {

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void migrate() {
		if (dataSource == null) {
			throw new IllegalStateException("Migration data source is not configured");
		}
		Flyway.configure()
				.dataSource(dataSource)
				.locations("classpath:db/migration")
				.load()
				.migrate();
	}
}
