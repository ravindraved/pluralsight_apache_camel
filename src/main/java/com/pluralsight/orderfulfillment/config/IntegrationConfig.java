package  com.pluralsight.orderfulfillment.config;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.pluralsight.orderfulfillment.order.OrderStatus;

@org.springframework.context.annotation.Configuration
public class IntegrationConfig extends CamelConfiguration {

	
	
	@Inject
	private Environment environment;
	
	@Inject
	private DataSource dataSource;
	
	@Bean
	public SqlComponent sql(){
		SqlComponent sqlComponent = 
				new SqlComponent();
		
		sqlComponent.setDataSource(dataSource);
		return sqlComponent;
		
	}

	
	@Bean
	public RouteBuilder newWebsiteOrderRoute(){
		
		return new RouteBuilder(){
			
			
			@Override
			public void configure() throws Exception {
				
				from(
						"sql:"
							+ "select if from orders.\"order\" where status = '"
							+ OrderStatus.NEW.getCode()
							+ "'"
							+ "?"
							+ "consumer.onConsume=update orders.\"order\" set status = '"
							+ OrderStatus.PROCESSING.getCode()
							+ "'"
							+ "  where id = :#id "
					).to(
							"log:com.pluralsight.orderfulfillment.order?level=INFO"
						);
				
			}
		};
	}
	
}
