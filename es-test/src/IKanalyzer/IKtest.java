package IKanalyzer;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.search.TotalHits;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import com.highLevelClientAPI.RestHighLevelClientTest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.ShardSearchFailure;
import java.util.HashMap;

public class IKtest {

	public static void main(String[] args) {
		try (RestHighLevelClient client = RestHighLevelClientTest.getClient();) {
//1.create index
	/*		CreateIndexRequest createIndexRequest = new CreateIndexRequest("venue");
			createIndexRequest.settings(Settings.builder().put("index.number_of_shards", 3) // ?????????
					.put("index.number_of_replicas", 2) // ?????????
					.put("analysis.analyzer.default.tokenizer", "ik_smart") // ???????????????
					);

			createIndexRequest.mapping(
					"{\"properties\":{\"title\":{\"type\":\"text\",\"analyzer\":\"ik_max_word\",\"search_analyzer\":\"ik_max_word\"},\"desc\":{\"type\":\"text\",\"analyzer\":\"ik_max_word\",\"search_analyzer\":\"ik_max_word\"},\"content\":{\"type\":\"text\",\"analyzer\":\"ik_max_word\",\"search_analyzer\":\"ik_max_word\"}}}", 
					XContentType.JSON);

			CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);

			// 6???????????????
			boolean acknowledged = createIndexResponse.isAcknowledged();
			boolean shardsAcknowledged = createIndexResponse
					.isShardsAcknowledged();
			System.out.println("acknowledged = " + acknowledged);
			System.out.println("shardsAcknowledged = " + shardsAcknowledged);
	*/
//2.fill index
			IndexRequest indexRequest = new IndexRequest("venue"); 
			indexRequest.id("1");  //??????id           	
/*
			XContentBuilder builder = XContentFactory.jsonBuilder();
			builder.startObject();
			{
				builder.field("title", "??????");
				builder.field("desc", "???????????????");
				builder.field("content", "????????????????????????");
			}
			builder.endObject();
			indexRequest.source(builder); 
*/
			
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("title", "??????");
            jsonMap.put("desc", "???????????????");
            jsonMap.put("content", "????????????????????????");
            indexRequest.source(jsonMap); 

			IndexResponse indexResponse = client.index(indexRequest,RequestOptions.DEFAULT);     
			if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
				System.out.println("?????????????????????????????????????????????????????????");
			}
			/*          
            //5???????????????
            if(indexResponse != null) {
                String index = indexResponse.getIndex();
                String id = indexResponse.getId();
                long version = indexResponse.getVersion();
                if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                    System.out.println("?????????????????????????????????????????????????????????");
                } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                    System.out.println("?????????????????????????????????????????????????????????");
                }
                // ??????????????????
                ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
                if (shardInfo.getTotal() != shardInfo.getSuccessful()) {

                }
                // ????????????????????????????????????????????????????????????
                if (shardInfo.getFailed() > 0) {
                    for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                        String reason = failure.reason(); 
                        System.out.println("?????????????????????" + reason);
                    }
                }
            }

			 */
//3.get index 
			  GetRequest getRequest = new GetRequest("venue",  "1"); 
			  GetResponse getResponse = client.get(getRequest,RequestOptions.DEFAULT);
			  if (getResponse.isExists()) {
				  System.out.println("index:" +  getResponse.getIndex() + "  id:" +  getResponse.getId() );
				  System.out.println(getResponse.getSourceAsString());
			  }
//4.add index 
			  
			   BulkRequest request = new BulkRequest(); 
//	           request.add(new IndexRequest("venue")
//	            	    .id("2").source("title", "aaaa","desc", "te","content", "good"));
			   request.add(new IndexRequest("venue").id("3").source("title", "aaaa","desc", "test","content", "good"));

	           BulkResponse bulkResponse = client.bulk(request,RequestOptions.DEFAULT);
	           if(bulkResponse != null) 
					  System.out.println(client.get(new GetRequest("venue",  "3"),RequestOptions.DEFAULT).getSourceAsString());
				  
//5.search index
			  SearchRequest searchRequest = new SearchRequest("venue"); 
			  SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
			  sourceBuilder.query(QueryBuilders.termQuery("desc", "te")); 
			  sourceBuilder.from(0); 
			  sourceBuilder.size(10); 
			  sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS)); 

			  searchRequest.source(sourceBuilder);
			  SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
			  RestStatus status = searchResponse.status();
			  TimeValue took = searchResponse.getTook();
			  Boolean terminatedEarly = searchResponse.isTerminatedEarly();
			  boolean timedOut = searchResponse.isTimedOut();

			  //??????????????????
			  int totalShards = searchResponse.getTotalShards();
			  int successfulShards = searchResponse.getSuccessfulShards();
			  int failedShards = searchResponse.getFailedShards();
			  for (ShardSearchFailure failure : searchResponse.getShardFailures()) {
				  // failures should be handled here
			  }

			  //??????????????????????????????
			  SearchHits hits = searchResponse.getHits();

			  TotalHits totalHits = hits.getTotalHits();
			  float maxScore = hits.getMaxScore();

			  SearchHit[] searchHits = hits.getHits();
			  for (SearchHit hit : searchHits) {
				  // do something with the SearchHit

				  String index = hit.getIndex();
				  //	                String type = hit.getType();
				  String id = hit.getId();
				  float score = hit.getScore();

				  //???_source?????????
				  String sourceAsString = hit.getSourceAsString(); //??????json???
				  Map<String, Object> sourceAsMap = hit.getSourceAsMap(); // ??????map??????
				  //???map???????????????
				  /*
	                String documentTitle = (String) sourceAsMap.get("title"); 
	                List<Object> users = (List<Object>) sourceAsMap.get("user");
	                Map<String, Object> innerObject = (Map<String, Object>) sourceAsMap.get("innerObject");
				   */
				  System.out.println("index:" + index + "  id:" + id);
				  System.out.println(sourceAsString);

			  }




		}catch (IOException e) {
			e.printStackTrace();
		}
	}


}
