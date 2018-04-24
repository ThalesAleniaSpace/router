package es.tase.wps.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.tase.wps.model.FaasProcess;
import es.tase.wps.services.ProcessService;

public class DeleteProcessProcessor implements Processor {
	
	ProcessService processService = ProcessService.getInstance();

	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		Message out = exchange.getOut();
	
		String processName = (String) in.getHeader("process-name");
		
		FaasProcess.Delete process = new FaasProcess.Delete();
		process.setFunctionName(processName);
		
		this.processService.deleteProcess(processName);
		ObjectMapper mapper = new ObjectMapper();
		out.setBody(mapper.writeValueAsString(process));
	}

}
