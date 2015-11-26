package com.rocket.tms.service;

import java.io.File;
import java.util.List;

import com.rocket.tms.json.ResponseJsonObj;

public interface TrucoreMSService {

	public static final String FAILED = "FAILED";

	public static final String SUCCESS = "SUCCESS";

	public ResponseJsonObj login(String jsonString);
	
	public ResponseJsonObj getPartners(String jsonString);
	
	public ResponseJsonObj getAvaliableDownloadFiles(String jsonString);
	
	public ResponseJsonObj fileUpload(String token, List<File> uploadFiles, String encryFileFoder, String pfirstname, String plastname, String pemail) throws Exception;
	
	public File fileDownload(String token, String filename, String downloadFolder) throws Exception;
	
}
