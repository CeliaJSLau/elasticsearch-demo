package com.index;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import com.highLevelClientAPI.RestHighLevelClientTest;
/**
 * 
 * @Description: 批量索引文件，即批量往索引裡面放入文件資料.類似於資料庫裡面批量向表裡面插入多行資料，一行資料就是一個文件
 * @author lgs
 * @date 2018年6月23日
 *
 */
public class BulkDemo {
    
    private static Logger logger = LogManager.getRootLogger();  

    public static void main(String[] args) {
        try (RestHighLevelClient client = RestHighLevelClientTest.getClient();) {
            
            // 1、建立批量操作請求
            BulkRequest request = new BulkRequest(); 
            request.add(new IndexRequest("posts")
            	    .id("1").source(XContentType.JSON,"field", "foo"));
            request.add(new IndexRequest("posts")
            	    .id("2").source(XContentType.JSON,"field", "bar"));
            request.add(new IndexRequest("posts")
            	    .id("3").source(XContentType.JSON,"field", "baz"));
            
            /*
            request.add(new DeleteRequest("mess", "_doc", "3")); 
            request.add(new UpdateRequest("mess", "_doc", "2") 
                    .doc(XContentType.JSON,"other", "test"));
            request.add(new IndexRequest("mess", "_doc", "4")  
                    .source(XContentType.JSON,"field", "baz"));
            */
            
            // 2、可選的設定
            /*
            request.timeout("2m");
            request.setRefreshPolicy("wait_for");  
            request.waitForActiveShards(2);
            */
            
            
            //3、傳送請求        
        
            // 同步請求
            BulkResponse bulkResponse = client.bulk(request,RequestOptions.DEFAULT);
            
            
            //4、處理響應
            if(bulkResponse != null) {
                for (BulkItemResponse bulkItemResponse : bulkResponse) { 
                    DocWriteResponse itemResponse = bulkItemResponse.getResponse(); 

                    if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                            || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) { 
                        IndexResponse indexResponse = (IndexResponse) itemResponse;
                        //TODO 新增成功的處理

                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) { 
                        UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                       //TODO 修改成功的處理

                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) { 
                        DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                        //TODO 刪除成功的處理
                    }
                }
            }
            
            
            //非同步方式傳送批量操作請求
            /*
            ActionListener<BulkResponse> listener = new ActionListener<BulkResponse>() {
                @Override
                public void onResponse(BulkResponse bulkResponse) {
                    
                }
            
                @Override
                public void onFailure(Exception e) {
                    
                }
            };
            client.bulkAsync(request, listener);
            */
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}