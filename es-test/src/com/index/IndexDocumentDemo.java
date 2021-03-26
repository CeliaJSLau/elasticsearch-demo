package com.index;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import com.highLevelClientAPI.RestHighLevelClientTest;
import org.elasticsearch.client.RequestOptions;

/**
 * 
 * @Description: 索引文件，即往索引裡面放入文件資料.類似於資料庫裡面向表裡面插入一行資料，一行資料就是一個文件
 * @author lgs
 * @date 2018年6月23日
 *
 */
public class IndexDocumentDemo {
    
    private static Logger logger = LogManager.getRootLogger();  

    public static void main(String[] args) {
        try (RestHighLevelClient client = RestHighLevelClientTest.getClient();) {
            // 1、建立索引請求
        	
        	
        	IndexRequest request = new IndexRequest("mess"); 
        	request.id("1");  //文件id  
            
            // 2、準備文件資料
            // 方式一：直接給JSON串
            String jsonString = "{" +
                    "\"user\":\"kimchy\"," +
                    "\"postDate\":\"2013-01-30\"," +
                    "\"message\":\"trying out Elasticsearch\"" +
                    "}";
            request.source(jsonString, XContentType.JSON); 
            
            // 方式二：以map物件來表示文件
            /*
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("user", "kimchy");
            jsonMap.put("postDate", new Date());
            jsonMap.put("message", "trying out Elasticsearch");
            request.source(jsonMap); 
            */
            
            // 方式三：用XContentBuilder來構建文件
            /*
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.field("user", "kimchy");
                builder.field("postDate", new Date());
                builder.field("message", "trying out Elasticsearch");
            }
            builder.endObject();
            request.source(builder); 
            */
            
            // 方式四：直接用key-value對給出
            /*
            request.source("user", "kimchy",
                            "postDate", new Date(),
                            "message", "trying out Elasticsearch");
            */
            
            //3、其他的一些可選設定
            /*
            request.routing("routing");  //設定routing值
            request.timeout(TimeValue.timeValueSeconds(1));  //設定主分片等待時長
            request.setRefreshPolicy("wait_for");  //設定重重新整理策略
            request.version(2);  //設定版本號
            request.opType(DocWriteRequest.OpType.CREATE);  //操作類別  
            */
            
            //4、傳送請求
            IndexResponse indexResponse = null;
            try {
                // 同步方式
                indexResponse = client.index(request,RequestOptions.DEFAULT);            
            } catch(ElasticsearchException e) {
                // 捕獲，並處理異常
                //判斷是否版本衝突、create但文件已存在衝突
                if (e.status() == RestStatus.CONFLICT) {
                    logger.error("衝突了，請在此寫衝突處理邏輯！\n" + e.getDetailedMessage());
                }
                
                logger.error("索引異常", e);
            }
            
            //5、處理響應
            if(indexResponse != null) {
                String index = indexResponse.getIndex();
                String type = indexResponse.getType();
                String id = indexResponse.getId();
                long version = indexResponse.getVersion();
                if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                    System.out.println("新增文件成功，處理邏輯程式碼寫到這裡。");
                } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                    System.out.println("修改文件成功，處理邏輯程式碼寫到這裡。");
                }
                // 分片處理資訊
                ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
                if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                    
                }
                // 如果有分片副本失敗，可以獲得失敗原因資訊
                if (shardInfo.getFailed() > 0) {
                    for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                        String reason = failure.reason(); 
                        System.out.println("副本失敗原因：" + reason);
                    }
                }
            }
            
            
            //非同步方式傳送索引請求
            /*ActionListener<IndexResponse> listener = new ActionListener<IndexResponse>() {
                @Override
                public void onResponse(IndexResponse indexResponse) {
                    
                }

                @Override
                public void onFailure(Exception e) {
                    
                }
            };
            client.indexAsync(request, listener);
            */
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}