package es.tase.wps.services;

import java.util.Collection;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import es.tase.wps.model.FaasProcess;
import es.tase.wps.model.FaasProcessInstance;

public class ProcessService {
	private IMap<String, FaasProcess> processes;
	private IMap<String, FaasProcessInstance> processInstances;
	private String hazelcastIp;
	private String hazelcastPort;
	
	private static ProcessService instance;

	private ProcessService() {
		hazelcastPort = "5701";
		hazelcastIp = "127.0.0.1";

		String envHazelcastIp = System.getenv("HAZELCAST_IP");
		String envHazelcastPort = System.getenv("HAZELCAST_PORT");

		hazelcastIp = envHazelcastIp != null ? envHazelcastIp : hazelcastIp;
		hazelcastPort = envHazelcastPort != null ? envHazelcastPort : hazelcastPort;
		
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.addAddress( hazelcastIp + ":" + hazelcastPort );
		HazelcastInstance hzClient = HazelcastClient.newHazelcastClient(clientConfig);
		processes = hzClient.getMap("processes");
		processInstances = hzClient.getMap("processInstances");
	}

	public static ProcessService getInstance() {
		if (instance == null) {
			instance = new ProcessService();
		}

		return instance;
	}

	// Processes
	public void addProcess(FaasProcess faasProcess) throws ProcessAlreadyExistException {

		if (processes.containsKey(faasProcess.getService()))
			throw new ProcessAlreadyExistException();

		this.processes.put(faasProcess.getService(), faasProcess);
	}

	public void deleteProcess(String processName) throws ProcessNotExistException {
		if (!processes.containsKey(processName)) {
			throw new ProcessNotExistException();
		}
		processes.remove(processName);
	}

	// ProcessInstances

	public Collection<FaasProcessInstance> listProcessInstances() {
		return processInstances.values();
	}

	public void endProcessInstance(String id) {
		// TODO: @Dev
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		FaasProcessInstance faasProcessInstance = processInstances.get(id);
		faasProcessInstance.setFinished();
		processInstances.put(faasProcessInstance.getId(), faasProcessInstance);
	}

	public String createProcessInstance(String name) throws ProcessNotExistException {
		FaasProcess faasProcess = processes.get(name);
		if (faasProcess == null)
			throw new ProcessNotExistException();

		FaasProcessInstance faasProcessInstance = new FaasProcessInstance(faasProcess);
		processInstances.put(faasProcessInstance.getId(), faasProcessInstance);
		return faasProcessInstance.getId();
	}

	//TODO: @Dev
	public void cleanup() {
		this.processes.clear();
		this.processInstances.clear();
	}
}
