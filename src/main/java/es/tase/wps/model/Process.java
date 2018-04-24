package es.tase.wps.model;

import java.util.UUID;

public class Process {
    private String id;
    private String processName;
    private String image;
    private String command;
    
    public Process() {
    		this.id = UUID.randomUUID().toString();
    }
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	//docker -H tcp://$(netstat -nr | grep '^0\.0\.0\.0' | awk '{print $2}'):2376 ps
}
