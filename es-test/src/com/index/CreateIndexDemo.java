package com.index;

import java.io.IOException;

import org.elasticsearch.action.admin.indices.alias.Alias;
//import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
//import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import com.highLevelClientAPI.RestHighLevelClientTest;
import org.elasticsearch.client.RequestOptions;
/**
 * 
 * @Description: 建立索引
 * @author lgs
 * @date 2018年6月23日
 *
 */
public class CreateIndexDemo {

    public static void main(String[] args) {
        try (RestHighLevelClient client = RestHighLevelClientTest.getClient();) {

            // 1、建立 建立索引request 引數：索引名mess
            CreateIndexRequest request = new CreateIndexRequest("mess");

            // 2、設定索引的settings
            request.settings(Settings.builder().put("index.number_of_shards", 3) // 分片數
                    .put("index.number_of_replicas", 2) // 副本數
                    .put("analysis.analyzer.default.tokenizer", "ik_smart") // 預設分詞器
            );

            // 3、設定索引的mappings
            request.mapping(
            		 "{\n" +
            			        "  \"properties\": {\n" +
            			        "    \"message\": {\n" +
            			        "      \"type\": \"text\"\n" +
            			        "    }\n" +
            			        "  }\n" +
            			        "}", 
                    XContentType.JSON);

            // 4、 設定索引的別名
            request.alias(new Alias("mmm"));

            // 5、 傳送請求
            // 5.1 同步方式傳送請求
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

            // 6、處理響應
            boolean acknowledged = createIndexResponse.isAcknowledged();
            boolean shardsAcknowledged = createIndexResponse
                    .isShardsAcknowledged();
            System.out.println("acknowledged = " + acknowledged);
            System.out.println("shardsAcknowledged = " + shardsAcknowledged);

            // 5.1 非同步方式傳送請求
            /*ActionListener<CreateIndexResponse> listener = new ActionListener<CreateIndexResponse>() {
                @Override
                public void onResponse(
                        CreateIndexResponse createIndexResponse) {
                    // 6、處理響應
                    boolean acknowledged = createIndexResponse.isAcknowledged();
                    boolean shardsAcknowledged = createIndexResponse
                            .isShardsAcknowledged();
                    System.out.println("acknowledged = " + acknowledged);
                    System.out.println(
                            "shardsAcknowledged = " + shardsAcknowledged);
                }

                @Override
                public void onFailure(Exception e) {
                    System.out.println("建立索引異常：" + e.getMessage());
                }
            };

            client.indices().createAsync(request, listener);
            */
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}