package org.exmaralda.common.helpers;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

public class PHPUpload {

	/**
	 * @param phpscript
	 * http://www.exmaralda.org/xml/templates/libupload.php
	 * @param file
	 * @return
	 */
	public static String uploadFile(String phpscript, File file) {
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost(phpscript);

		MultipartEntity mpEntity = new MultipartEntity();
		ContentBody cbFile = new FileBody(file, "text/xml");
		mpEntity.addPart("userfile", cbFile);

		httppost.setEntity(mpEntity);
		System.out.println("executing request " + httppost.getRequestLine());
		HttpResponse response;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			return ("FAILURE" + e.getLocalizedMessage());
		} catch (IOException e) {
			return ("FAILURE:" + e.getLocalizedMessage());
		}
		HttpEntity resEntity = response.getEntity();
		/*
		 * if (resEntity != null) {
		 * System.out.println(EntityUtils.toString(resEntity)); } if (resEntity
		 * != null) { resEntity.consumeContent(); }
		 */
		httpclient.getConnectionManager().shutdown();
		return "SUCCESS" + response.getStatusLine().toString();
	}

}