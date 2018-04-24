package es.tase.wps.model;

public class FaasProcess implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String service;
    private String network;
    private String image;
    private String envProcess;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getEnvProcess() {
		return envProcess;
	}

	public void setEnvProcess(String envProcess) {
		this.envProcess = envProcess;
	}
	
	public static class Delete{
		private String functionName;

		public String getFunctionName() {
			return functionName;
		}

		public void setFunctionName(String functionName) {
			this.functionName = functionName;
		}
	}
}
