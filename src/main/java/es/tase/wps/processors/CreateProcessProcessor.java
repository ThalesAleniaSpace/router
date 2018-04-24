package es.tase.wps.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.tase.wps.model.FaasProcess;
import es.tase.wps.model.Process;
import es.tase.wps.services.ProcessService;

public class CreateProcessProcessor implements Processor {
	
	ProcessService processService = ProcessService.getInstance();

	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		Message out = exchange.getOut();
	
		Process process = in.getBody(Process.class);
		FaasProcess faasProcess = new FaasProcess();
		
		faasProcess.setService(process.getProcessName());
		faasProcess.setNetwork("faas_network");
		faasProcess.setImage("thalesaleniaspace/container-agent");
							
		String command = process.getCommand();
		//String executor = "docker -H tcp://$(netstat -nr | grep '^0\\.0\\.0\\.0' | awk '{print $2}'):2376 run --rm -t";
		//String executor = "docker -H tcp://$(ip route | awk '/default/ { print $3 }'):2376 run --rm -t";
		String executor = "/opt/docker-run.sh";
		String finalCommand = (command != null)?command:"";
		faasProcess.setEnvProcess(executor + " " + process.getImage()  + " " + finalCommand);
		this.processService.addProcess(faasProcess);
		ObjectMapper mapper = new ObjectMapper();
		
		out.setBody(mapper.writeValueAsString(faasProcess));
	}

}
