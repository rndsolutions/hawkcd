package net.hawkengine.model.dto;

public class WsContractDto {
	private String className;
	private String packageName;
	private String methodName;
	private Object result;
	private String error;
	private String errorMessage;
	private ConversionObject[] args;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public ConversionObject[] getArgs() {
		return args;
	}

	public void setArgs(ConversionObject[] args) {
		this.args = args;
	}
}

