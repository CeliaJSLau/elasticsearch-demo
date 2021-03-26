package com.index;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import com.highLevelClientAPI.RestHighLevelClientTest;
import org.apache.lucene.search.TotalHits;
/**
 * 
 * @Description: 搜尋資料
 * @author lgs
 * @date 2018年6月23日
 *
 */
public class SearchDemo {
    
    private static Logger logger = LogManager.getRootLogger();  

    public static void main(String[] args) {
        try (RestHighLevelClient client = RestHighLevelClientTest.getClient();) {
            
            // 1、建立search請求
            //SearchRequest searchRequest = new SearchRequest();
            SearchRequest searchRequest = new SearchRequest("mess"); 
//            searchRequest.types("_doc");
            
            // 2、用SearchSourceBuilder來構造查詢請求體 ,請仔細檢視它的方法，構造各種查詢的方法都在這。
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 

            //構造QueryBuilder
            /*QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("user", "kimchy")
                    .fuzziness(Fuzziness.AUTO)
                    .prefixLength(3)
                    .maxExpansions(10);
            sourceBuilder.query(matchQueryBuilder);*/
            
            sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy")); 
            sourceBuilder.from(0); 
            sourceBuilder.size(10); 
            sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS)); 
            
            //是否返回_source欄位
            //sourceBuilder.fetchSource(false);
            
            //設定返回哪些欄位
            /*String[] includeFields = new String[] {"title", "user", "innerObject.*"};
            String[] excludeFields = new String[] {"_type"};
            sourceBuilder.fetchSource(includeFields, excludeFields);*/
            
            //指定排序
            //sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC)); 
            //sourceBuilder.sort(new FieldSortBuilder("_uid").order(SortOrder.ASC));
            
            // 設定返回 profile 
            //sourceBuilder.profile(true);
            
            //將請求體加入到請求中
            searchRequest.source(sourceBuilder);
            
            // 可選的設定
            //searchRequest.routing("routing");
            
            // 高亮設定
            /*
            HighlightBuilder highlightBuilder = new HighlightBuilder(); 
            HighlightBuilder.Field highlightTitle =
                    new HighlightBuilder.Field("title"); 
            highlightTitle.highlighterType("unified");  
            highlightBuilder.field(highlightTitle);  
            HighlightBuilder.Field highlightUser = new HighlightBuilder.Field("user");
            highlightBuilder.field(highlightUser);
            sourceBuilder.highlighter(highlightBuilder);*/
            
            
            //加入聚合
            /*TermsAggregationBuilder aggregation = AggregationBuilders.terms("by_company")
                    .field("company.keyword");
            aggregation.subAggregation(AggregationBuilders.avg("average_age")
                    .field("age"));
            sourceBuilder.aggregation(aggregation);*/
            
            //做查詢建議
            /*SuggestionBuilder termSuggestionBuilder =
                    SuggestBuilders.termSuggestion("user").text("kmichy"); 
                SuggestBuilder suggestBuilder = new SuggestBuilder();
                suggestBuilder.addSuggestion("suggest_user", termSuggestionBuilder); 
            sourceBuilder.suggest(suggestBuilder);*/
            
            //3、傳送請求        
            SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
            
            
            //4、處理響應
            //搜尋結果狀態資訊
            RestStatus status = searchResponse.status();
            TimeValue took = searchResponse.getTook();
            Boolean terminatedEarly = searchResponse.isTerminatedEarly();
            boolean timedOut = searchResponse.isTimedOut();
            
            //分片搜尋情況
            int totalShards = searchResponse.getTotalShards();
            int successfulShards = searchResponse.getSuccessfulShards();
            int failedShards = searchResponse.getFailedShards();
            for (ShardSearchFailure failure : searchResponse.getShardFailures()) {
                // failures should be handled here
            }
            
            //處理搜尋命中文件結果
            SearchHits hits = searchResponse.getHits();
            
            TotalHits totalHits = hits.getTotalHits();
            float maxScore = hits.getMaxScore();
            
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                // do something with the SearchHit
                
                String index = hit.getIndex();
//                String type = hit.getType();
                String id = hit.getId();
                float score = hit.getScore();
                
                //取_source欄位值
                String sourceAsString = hit.getSourceAsString(); //取成json串
                Map<String, Object> sourceAsMap = hit.getSourceAsMap(); // 取成map物件
                //從map中取欄位值
                /*
                String documentTitle = (String) sourceAsMap.get("title"); 
                List<Object> users = (List<Object>) sourceAsMap.get("user");
                Map<String, Object> innerObject = (Map<String, Object>) sourceAsMap.get("innerObject");
                */
                System.out.println("index:" + index + "  id:" + id);
                System.out.println(sourceAsString);
                
                //取高亮結果
                /*Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField highlight = highlightFields.get("title"); 
                Text[] fragments = highlight.fragments();  
                String fragmentString = fragments[0].string();*/
            }
            
            // 獲取聚合結果
            /*
            Aggregations aggregations = searchResponse.getAggregations();
            Terms byCompanyAggregation = aggregations.get("by_company"); 
            Bucket elasticBucket = byCompanyAggregation.getBucketByKey("Elastic"); 
            Avg averageAge = elasticBucket.getAggregations().get("average_age"); 
            double avg = averageAge.getValue();
            */
            
            // 獲取建議結果
            /*Suggest suggest = searchResponse.getSuggest(); 
            TermSuggestion termSuggestion = suggest.getSuggestion("suggest_user"); 
            for (TermSuggestion.Entry entry : termSuggestion.getEntries()) { 
                for (TermSuggestion.Entry.Option option : entry) { 
                    String suggestText = option.getText().string();
                }
            }
            */
            
            //非同步方式傳送獲查詢請求
            /*
            ActionListener<SearchResponse> listener = new ActionListener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse getResponse) {
                    //結果獲取
                }
            
                @Override
                public void onFailure(Exception e) {
                    //失敗處理
                }
            };
            client.searchAsync(searchRequest, listener); 
            */
            
        } catch (IOException e) {
            logger.error(e);
        }
    }
}