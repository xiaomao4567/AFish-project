package com.example.yitiaoyu.loader;

import com.example.yitiaoyu.pojo.entity.Combo;
import com.example.yitiaoyu.pojo.entity.ComboItem;
import com.example.yitiaoyu.pojo.entity.Dish;
import com.example.yitiaoyu.server.mapper.ComboItemMapper;
import com.example.yitiaoyu.server.mapper.ComboMapper;
import com.example.yitiaoyu.server.mapper.DishMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MenuKnowledgeLoader {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private ComboMapper comboMapper;
    @Autowired
    private ComboItemMapper comboItemMapper;
    @Autowired
    private VectorStore vectorStore;

    @PostConstruct
    public void loadMenuToVectorStore(){
        try {
            log.info("开始加载知识库...");
            List<Document> dishDocs= loadDishesFromDB();
            List<Document> ComboDocs= loadComboFromDB();
            List<Document> addAllDocs=new ArrayList<>();
            addAllDocs.addAll(dishDocs);
            addAllDocs.addAll(ComboDocs);
            if(!addAllDocs.isEmpty()){
                vectorStore.add(addAllDocs);
                log.info("✅ 知识库加载完成，共 {} 条记录", addAllDocs.size());
            }else {
                log.warn("⚠️ 没有加载到任何数据");
            }
        } catch (Exception e) {
            log.error("❌ 知识库加载失败", e);
        }
    }

    private List<Document> loadDishesFromDB() {
        try {
            List<Dish> dishes = dishMapper.selectAll();
            if (dishes==null||dishes.isEmpty()){
                log.warn("没有找到菜品数据");
                return new ArrayList<>();
            }
            return dishes.stream()
                    .map(this::convertDishToDocument)
                    .filter(document -> document!=null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("加载菜品数据失败", e);
            return new ArrayList<>();
        }
    }
    private List<Document> loadComboFromDB() {
        try {
            List<Combo> combos = comboMapper.selectAll();
            if(combos==null||combos.isEmpty()){
                log.warn("没有找到套餐数据");
                return new ArrayList<>();
            }
            return combos.stream()
                    .map(this::convertComboToDocument)
                    .filter(document -> document!=null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("加载套餐数据失败", e);
            return new ArrayList<>();
        }
    }
    private Document convertDishToDocument(Dish dish) {
        try {
            String name =safeString(dish.getName(), "未知菜品");
            Double price=dish.getPrice()!=null?dish.getPrice().doubleValue():0.0;
            Long category= dish.getCategoryId()!=null? dish.getCategoryId() : 0;
            String flavor=safeString(dish.getFlavors(),"未知口味");
            String description=safeString(dish.getDescription(), "");
            int popularity=dish.getRecommendIndex()!=null?dish.getRecommendIndex():0;


            String text = String.format(
                    "【菜品】%s\n价格：%.2f元\n分类：%d\n口味：%s\n介绍：%s\n人气：%d/5",
                    name, price, category, flavor,  description, popularity
            );
            Document document=new Document(text);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("id",dish.getId()!=null?dish.getId():0L);
            metadata.put("type", "dish");
            metadata.put("name", name);
            metadata.put("category", category);
            metadata.put("is_recommended", dish.getRecommendIndex() != null ? dish.getRecommendIndex() : false);
            document.getMetadata().putAll(metadata);
            return document;
        } catch (Exception e) {
            log.error("转换菜品文档失败: {}", dish.getName(), e);
            return null;
        }
    }
    private Document convertComboToDocument(Combo combo) {
        try {
            List<ComboItem> comboItems = comboItemMapper.selectByComboId(combo.getId());
            StringBuilder stringBuilder=new StringBuilder();
            for (ComboItem comboItem : comboItems) {
                stringBuilder.append(comboItem).append(",");
            }
            String comboDishes = stringBuilder.toString();
            String name=safeString(combo.getName(), "");
            Double price=combo.getPrice()!=null?combo.getPrice().doubleValue():0.0;
            String description=safeString(combo.getDescription(),"");
            String dishes=safeString(comboDishes,"");
            int popularity=combo.getRecommendIndex()!=null?combo.getRecommendIndex():0;

            String text = String.format(
                    "【套餐】%s\n价格：%.2f元\n介绍：%s\n包含菜品：%s\n人气：%d/5",
                    name, price, description, dishes, popularity
            );
            Document document=new Document(text);
            Map<String,Object> metadata=new HashMap<>();
            metadata.put("id",combo.getId()!=null?combo.getId():0L);
            metadata.put("type", "combo");
            metadata.put("name", name);
            metadata.put("is_recommended",combo.getRecommendIndex()!=null?combo.getRecommendIndex():0);
            document.getMetadata().putAll(metadata);
            return document;
        } catch (Exception e) {
            log.error("转换套餐文档失败: {}", combo.getName(), e);
            return null;
        }
    }
    private String safeString(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

}
