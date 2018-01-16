package com.hyperfit.util.wechat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

public class LogWrapper {
	private static Gson gson = new Gson();

	private Log logger = LogFactory.getLog(LogWrapper.class);

	private String successHeader = "";
	private String warnHeader = "";
	private String failHeader = "";
	private String fatalHeader = "";
	private String infoHeader = "";
	private String debugHeader = "";

	// -- Constructors & Factories

	public LogWrapper(@SuppressWarnings("rawtypes") Class clazz) {
		logger = LogFactory.getLog(clazz);
	}

	// -- Interfaces

	public void logSuccess(String msg) {
		if (logger.isInfoEnabled()) {			
			logger.info(String.format("%s%s", getSuccessHeader(), msg));
		}
	}

	public void logInfo(String msg) {
		if (logger.isInfoEnabled()) {			
			logger.info(String.format("%s%s", getInfoHeader(), msg));
		}
	}
	
	public void logDebug(String msg) {
		if (logger.isDebugEnabled()) {			
			logger.info(String.format("%s%s", getDebugHeader(), msg));
		}
	}

	public void logWarn(String msg) {
		if (logger.isWarnEnabled()) {			
			logger.warn(String.format("%s%s", getWarnHeader(), msg));
		}
	}

	public void logFail(String msg) {
		if (logger.isErrorEnabled()) {			
			logger.error(String.format("%s%s", getFailHeader(), msg));
		}
	}

	public void logFatal(String msg) {
		if (logger.isFatalEnabled()) {
			logger.fatal(String.format("%s%s", getFatalHeader(), msg));
		}
	}

	public void setAllHeaders(String funName, Object params) {
		String paramsString = gson.toJson(params);

		setSuccessHeader(funName, paramsString);
		setInfoHeader(funName, paramsString);
		setWarnHeader(funName, paramsString);
		setFailHeader(funName, paramsString);
		setFatalHeader(funName, paramsString);
		setDebugHeader(funName, paramsString);
	}

	public void setAllHeaders(String funName, Object params1, Object params2) {
		String paramsString = String.format("%s%s", gson.toJson(params1),
				gson.toJson(params2));

		setSuccessHeader(funName, paramsString);
		setInfoHeader(funName, paramsString);
		setWarnHeader(funName, paramsString);
		setFailHeader(funName, paramsString);
		setFatalHeader(funName, paramsString);
		setDebugHeader(funName, paramsString);
	}

	private String getSuccessHeader() {
		return successHeader;
	}

	private void setSuccessHeader(String funName, String params) {
		this.successHeader = String.format("Success-: %s(): %s - ", funName,
				params);
	}

	private String getWarnHeader() {
		return warnHeader;
	}

	private void setWarnHeader(String funName, String params) {
		this.warnHeader = String.format("Warn-: %s(): %s - ", funName, params);
	}

	private String getFailHeader() {
		return failHeader;
	}

	private void setFailHeader(String funName, String params) {
		this.failHeader = String
				.format("Failed-: %s(): %s - ", funName, params);
	}

	private String getFatalHeader() {
		return fatalHeader;
	}

	private void setFatalHeader(String funName, String params) {
		this.fatalHeader = String
				.format("Fatal-: %s(): %s - ", funName, params);
	}

	public String getInfoHeader() {
		return infoHeader;
	}

	private void setInfoHeader(String funName, String params) {
		this.infoHeader = String.format("Info-: %s(): %s - ", funName, params);
	}
	
	public String getDebugHeader() {
		return debugHeader;
	}
	
	private void setDebugHeader(String funName, String params) {
		this.debugHeader = String.format("Debug-: %s(): %s - ", funName, params);
	}
}
