package es.tase.wps.processors;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import es.tase.wps.services.ProcessService;

public class StartProcessProcessor implements Processor {
	
	ProcessService processService = ProcessService.getInstance();

	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		Message out = exchange.getOut();
		
		
		String processName = (String) in.getHeader("process-name");
		String pid = this.processService.createProcessInstance(processName);
		
		Map<String, Object> headers = out.getHeaders();
		headers.put("process-name", processName);
		headers.put("pid", pid);
	}

}
