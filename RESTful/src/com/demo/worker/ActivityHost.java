package com.demo.worker;

import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.demo.rest.domain.Action;
import com.demo.rest.domain.Actor;
import com.demo.rest.domain.Actor.actorType;

public class ActivityHost {

	private Actor worker;

	public ActivityHost() {
		this.worker = new Actor();
		this.worker.setDomain("com.cisco");
		this.worker.setName("testWorker1");
		this.worker.setType(actorType.worker);
		this.worker.setVersion(0.1f);
		this.worker.setStatus(10);
		this.worker.setWorkflowID("123456789");
	}

	public void pollActivityTask(HttpClient hc, HttpPost hp) {

		JSONObject jsnObj = new JSONObject();
		HttpResponse response = null;
		try {
			jsnObj.put("workflowID", this.worker.getWorkflowID());
			jsnObj.put("domain", this.worker.getDomain());

			StringEntity content = new StringEntity(jsnObj.toString());
			content.setContentType("application/json");
			hp.setEntity(content);
			response = hc.execute(hp);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			JSONObject json = new JSONObject(response.getEntity());
			// find the activity and execute the action.

			String name = json.getString("name");
			String type = json.getString("type");
			String method = json.getString("method");
			String parameters = json.getString("parameters");

			// the worker executes the method from the server.and return the
			// result to the server.

			Class clazz = Class.forName(name);
			Method md = clazz.getMethod(method, null);
			md.invoke(clazz, null);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void registerWorker(HttpClient hc, HttpPost hp) {

		try {
			JSONObject json = new JSONObject();

			json.put("domain", this.worker.getDomain());
			json.put("name", this.worker.getName());
			json.put("type", this.worker.getType());
			json.put("version", this.worker.getVersion());
			json.put("status", this.worker.getStatus());
			json.put("workflowID", this.worker.getWorkflowID());

			StringEntity content = new StringEntity(json.toString());
			content.setContentType("application/json");
			hp.setEntity(content);
			HttpResponse response;
			response = hc.execute(hp);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		String registerWorkerUrl = "http://localhost:8080/RESTful/services/registerWorker";
		String pollActivityTaskUrl = "http://localhost:8080/RESTful/services/pollActivityTask";

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(registerWorkerUrl);
		HttpPost httpPost2 = new HttpPost(pollActivityTaskUrl);

		ActivityHost client = new ActivityHost();
		client.registerWorker(httpClient, httpPost);
		// client.pollActivityTask(httpClient, httpPost2);
	}
}
