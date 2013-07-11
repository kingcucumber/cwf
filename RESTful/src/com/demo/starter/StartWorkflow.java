package com.demo.starter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.demo.message.StartMessage;
import com.demo.rest.utils.JsonUtils;

public class StartWorkflow{
	private String workflowID;
	private String domain;
	private String runID;

	
	public StartWorkflow() {
		// this.workflowID = RestUtils.getRandomString(20);
		this.workflowID = "123456789";
		this.domain = "com.cisco";
	}

	public void workflowExecutionStarted(String workflowID, String domain) {

		String url = "http://localhost:8080/RESTful/services/startWorkflowService";

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		StringBuilder result = new StringBuilder();
		try {
			StartMessage sm = new StartMessage();
			sm.setWorkflowID(this.workflowID);
			sm.setDomain(this.domain);
			
			JSONArray jsnAction = new JSONArray();

			// jsnAction includes the verify,charge,ship and record ,jsnAction is an array.

			JSONObject jsnVerify = new JSONObject();
			jsnVerify.put("name", "VerifyOrderActivity");
			jsnVerify.put("type", "System.out");
			jsnVerify.put("method", "println");
			jsnVerify.put("parameters", "Verify order");
			jsnAction.put(0, jsnVerify);
			//("Verify", jsnVerify);

			JSONObject jsnCharge = new JSONObject();
			jsnCharge.put("name", "ChargeCreditCardActivity");
			jsnCharge.put("type", "System.out");
			jsnCharge.put("method", "println");
			jsnCharge.put("parameters", "Charge order");
		//	jsnAction.put("Charge", jsnCharge);
			jsnAction.put(1, jsnCharge);
			JSONObject jsnShip = new JSONObject();
			jsnShip.put("name", "ShipOrderActivity");
			jsnShip.put("type", "System.out");
			jsnShip.put("method", "println");
			jsnShip.put("parameters", "Ship order");
		//	jsnAction.put("Ship", jsnShip);
			jsnAction.put(2, jsnShip);
			JSONObject jsnRecord = new JSONObject();
			jsnRecord.put("name", "RecordOrderActivity");
			jsnRecord.put("type", "System.out");
			jsnRecord.put("method", "println");
			jsnRecord.put("parameters", "Record Completion");
		//	jsnAction.put("Record", jsnRecord);
			jsnAction.put(3, jsnRecord);
			
			
			sm.setJsnAction(jsnAction);
			
			System.out.println(jsnAction.toString());
			
			String message = JsonUtils.bean2json(sm);
			
			StringEntity content = new StringEntity(message);
			content.setContentType("application/json");
			httpPost.setEntity(content);

			HttpResponse response;
			response = httpClient.execute(httpPost);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			String ts;
			while ((ts = reader.readLine()) != null)
				result.append(ts);
			JSONObject jo = new JSONObject(result.toString());
			this.runID = jo.getString("runID");
			System.out.println("runID = " + this.runID + "\nworkflowID = "
					+ jo.getString("workflowID") + "\ndomain = "
					+ jo.getString("domain") + "\nmessage ="
					+ jo.getString("message"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException ex) {
			ex.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}

	public static void main(String[] args) {
		StartWorkflow starter = new StartWorkflow();
		starter.workflowExecutionStarted(starter.workflowID, starter.domain);

	}
}
