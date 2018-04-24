package es.tase.wps;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import es.tase.wps.model.FaasProcessInstance;
import es.tase.wps.model.Process;
import es.tase.wps.processors.CreateProcessProcessor;
import es.tase.wps.processors.DeleteProcessProcessor;
import es.tase.wps.processors.StartProcessProcessor;
import es.tase.wps.services.ProcessAlreadyExistException;
import es.tase.wps.services.ProcessNotExistException;

public class Routes extends RouteBuilder {
	private String callbackIp;
	private String wpsPort;
	private String faasGatewayIp;
	private String faasGatewayPort;
	
	@SuppressWarnings("deprecation")
	@Override
	public void configure() throws Exception {

		callbackIp = "192.168.3.245"; //Thales
		//callbackIp = "192.168.1.34"; //Home
		wpsPort = "8082";
		faasGatewayIp = "127.0.0.1";
		faasGatewayPort = "8080";

		String envCallbackIp = System.getenv("CALLBACK_IP");
		String envWpsPort = System.getenv("WPS_PORT");
		String envFaasGatewayIp = System.getenv("FAAS_GATEWAY_IP");
		String envFaasGatewayPort = System.getenv("FAAS_GATEWAY_PORT");

		callbackIp = envCallbackIp != null ? envCallbackIp : callbackIp;
		wpsPort = envWpsPort != null ? envWpsPort : wpsPort;
		faasGatewayIp = envFaasGatewayIp != null ? envFaasGatewayIp : faasGatewayIp;
		faasGatewayPort = envFaasGatewayPort != null ? envFaasGatewayPort : faasGatewayPort;

		
		onException(ProcessNotExistException.class)
	    .handled(true)
	    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
	    .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
	    .setBody().constant("The process does not exist");
		
		onException(ProcessAlreadyExistException.class)
	    .handled(true)
	    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
	    .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
	    .setBody().constant("The process already exists");
		
		restConfiguration().component("jetty").apiContextPath("api-doc").port(wpsPort)
			.bindingMode(RestBindingMode.json)
			.dataFormatProperty("prettyPrint", "true");
		
		rest().consumes("application/json").produces("application/json")
			.post("/processes").type(Process.class).to("direct:createProcess")
			.delete("/processes/{process-name}").to("direct:deleteProcess")
			.get("/processes/executions").outTypeList(FaasProcessInstance.class).to("bean:processService?method=listProcessInstances")
			.post("/processes/run/{process-name}").to("direct:dispatcherCall");
		
		//TODO:@Dev
		rest().get("/cleanup").to("bean:processService?method=cleanup");
		
		//This endpoint was excluded from the previous definition because the json parser fails when it tries to read the stdout of the process
		//callback
		from("rest:post:processes/end:/{pid}").to("bean:processService?method=endProcessInstance(${header.pid})");
		
		from("direct:dispatcherCall").process(new StartProcessProcessor())
			.setHeader("X-Callback-Url",simple("http://"+ callbackIp + ":" + wpsPort + "/processes/end/${header.pid}")) //Thales
			.setHeader(Exchange.HTTP_METHOD, constant("POST"))
			.toD("http4://" + faasGatewayIp + ":" + faasGatewayPort + "/async-function/${header.process-name}");

		from("direct:createProcess").process(new CreateProcessProcessor())
			.setHeader(Exchange.HTTP_METHOD, constant("POST"))
			.to("http4://" + faasGatewayIp + ":" + faasGatewayPort + "/system/functions");
		
		from("direct:deleteProcess").process(new DeleteProcessProcessor())
			.setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.to("jetty:http://" + faasGatewayIp + ":" + faasGatewayPort + "/system/functions");

	}

}
