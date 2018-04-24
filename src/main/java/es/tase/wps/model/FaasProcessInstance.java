package es.tase.wps.model;

import java.util.Date;
import java.util.UUID;

public class FaasProcessInstance implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
    private Date created;
    private Date finished;
    private FaasProcess faasProcess;

    public FaasProcessInstance() {
    }

    public FaasProcessInstance(FaasProcess faasProcess) {
        this.id = UUID.randomUUID().toString();
        this.created = new Date();
        this.faasProcess = faasProcess;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getFinished() {
		return finished;
	}

	public void setFinished() {
		this.finished = new Date();
	}

	public Date getCreated() {
		return created;
	}

	public FaasProcess getFaasProcess() {
		return faasProcess;
	}

	public void setFaasProcess(FaasProcess faasProcess) {
		this.faasProcess = faasProcess;
	}
	
}
