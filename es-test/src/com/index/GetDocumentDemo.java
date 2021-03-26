package com.index;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import com.highLevelClientAPI.RestHighLevelClientTest;

/**
 * 
 * @Description: 獲取文件資料
 * @author lgs
 * @date 2018年6月23日
 *
 */
public class GetDocumentDemo {
    
    private static Logger logger = LogManager.getRootLogger();  

    public static void main(String[] args) {
        try (RestHighLevelClient client = RestHighLevelClientTest.getClient();) {
            // 1、建立獲取文件請求
            GetRequest request = new GetRequest(
                    "mess",   //索引
                    "1");     //文件id  
            
            // 2、可選的設定
            //request.routing("routing");
            //request.version(2);
            
            //request.fetchSourceContext(new FetchSourceContext(false)); //是否獲取_source欄位
            //選擇返回的欄位
            String[] includes = new String[]{"message", "*Date"};
            String[] excludes = Strings.EMPTY_ARRAY;
            FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
            request.fetchSourceContext(fetchSourceContext); 
            
            //也可寫成這樣
            /*String[] includes = Strings.EMPTY_ARRAY;
            String[] excludes = new String[]{"message"};
            FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
            request.fetchSourceContext(fetchSourceContext);*/
            
            
            // 取stored欄位
            /*request.storedFields("message"); 
            GetResponse getResponse = client.get(request);
            String message = getResponse.getField("message").getValue();*/
            
            
            //3、傳送請求        
            GetResponse getResponse = null;
            try {
                // 同步請求
                getResponse = client.get(request,RequestOptions.DEFAULT);
            } catch (ElasticsearchException e) {
                if (e.status() == RestStatus.NOT_FOUND) {
                    logger.error("沒有找到該id的文件" );
                }
                if (e.status() == RestStatus.CONFLICT) {
                    logger.error("獲取時版本衝突了，請在此寫衝突處理邏輯！" );
                }
                logger.error("獲取文件異常", e);
            }
            
            //4、處理響應
            if(getResponse != null) {
                String index = getResponse.getIndex();
                String type = getResponse.getType();
                String id = getResponse.getId();
                if (getResponse.isExists()) { // 文件存在
                    long version = getResponse.getVersion();
                    String sourceAsString = getResponse.getSourceAsString(); //結果取成 String       
                    Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();  // 結果取成Map
                    byte[] sourceAsBytes = getResponse.getSourceAsBytes();    //結果取成位元組陣列
                    
                    System.out.println("index:" + index + "  type:" + type + "  id:" + id+"content: "+ getResponse.getField("user"));
                    System.out.println(sourceAsString);
                    
                } else {
                    logger.error("沒有找到該id的文件" );
                }
            }
            
            
            //非同步方式傳送獲取文件請求
            /*
            ActionListener<GetResponse> listener = new ActionListener<GetResponse>() {
                @Override
                public void onResponse(GetResponse getResponse) {
                    
                }
            
                @Override
                public void onFailure(Exception e) {
                    
                }
            };
            client.getAsync(request, listener);
            */
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}