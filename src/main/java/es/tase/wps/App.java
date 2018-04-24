package es.tase.wps;

import org.apache.camel.CamelContext;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import es.tase.wps.services.ProcessService;

public final class App {

	public static void main(String[] args) throws Exception {
		
		SimpleRegistry registry = new SimpleRegistry(); 
		registry.put("processService", ProcessService.getInstance()); 
		
		CamelContext ctx = new DefaultCamelContext(registry);
		
		HttpComponent httpComponent = new HttpComponent();
		ctx.addComponent("http4", httpComponent);
		
		ctx.addRoutes(new Routes());
	
		ctx.start();
	}
}
