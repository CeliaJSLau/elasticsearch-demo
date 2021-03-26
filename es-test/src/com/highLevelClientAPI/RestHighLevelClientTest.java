package com.highLevelClientAPI;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;

public class RestHighLevelClientTest {
	

	private static RestHighLevelClient client = null;

	public static void main(String[] args) {
		try {
//			init();
			index();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static RestHighLevelClient getClient() {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));

        return client;
    }
	
//	private static void init() {
//		
//		client = new RestHighLevelClient(RestClient.builder(
//                new HttpHost("localhost", 9200, "http"),
//                new HttpHost("localhost", 9201, "http")));
//
//	}
	private static void index() {
		IndexRequest request = new IndexRequest("posts"); 
		request.id("1"); 
		String jsonString = "{" +
		        "\"user\":\"kimchy\"," +
		        "\"postDate\":\"2013-01-30\"," +
		        "\"message\":\"trying out Elasticsearch\"" +
		        "}";
		request.source(jsonString, XContentType.JSON); 
	}
	private static void index1() {
		
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("user", "celia");
		jsonMap.put("postDate", new Date());
		jsonMap.put("message", "trying out Elasticsearch");
		IndexRequest indexRequest = new IndexRequest("posts")
				.id("2").source(jsonMap); 
	}
}
