package com.example.yitiaoyu.Tool;

import com.example.yitiaoyu.pojo.entity.Combo;
import com.example.yitiaoyu.pojo.entity.ComboItem;
import com.example.yitiaoyu.pojo.entity.Dish;
import com.example.yitiaoyu.server.mapper.ComboItemMapper;
import com.example.yitiaoyu.server.mapper.ComboMapper;
import com.example.yitiaoyu.server.mapper.DishMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class YiTiaoYuToos {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private ComboMapper comboMapper;
    @Autowired
    private ComboItemMapper comboItemMapper;
    @Tool(description = "根据菜品名称查询菜品详细信息，包括价格、口味、介绍等")
    public String getDishInfo(
            @ToolParam(description = "菜品名称，例如：鲜青椒爽麻烤鱼、重庆豆花烤鱼") String dishName
    ){
        try {
            Dish dish = dishMapper.queryByName(dishName);
            if (dish==null){
                return  "未找到菜品【" + dishName + "】的信息";
            }
            return String.format(
                    "【%s】\n价格：%.2f元\n分类：%d\n口味：%s\n详细描述：%s\n人气指数：%d★",
                    dish.getName(), dish.getPrice(), dish.getCategoryId(),
                    dish.getFlavors(),   dish.getDescription(),
                    dish.getRecommendIndex()
            );
        } catch (Exception e) {
            log.error("查询菜品信息失败，菜品名称：{}", dishName, e);
            return "查询菜品信息时出现错误，请稍后重试";
        }
    }
    @Tool(description = "当用户问菜单，或者本店都有什么菜品之类的话时，用这个工具")
    public String getAllDish(){
        try {
            List<Dish> dishes = dishMapper.selectAll();
            StringBuilder stringBuilder=new StringBuilder();
            for (Dish dish : dishes) {
                String d=String.format(
                        "【%s】\n价格：%.2f元\n分类：%d\n口味：%s\n详细描述：%s\n人气指数：%d★\n",
                        dish.getName(), dish.getPrice(), dish.getCategoryId(),
                        dish.getFlavors(),   dish.getDescription(),
                        dish.getRecommendIndex()
                );
                stringBuilder.append(d);
            }
            String menu = stringBuilder.toString();
            return menu;
        } catch (Exception e) {
            log.error("查询菜品信息失败", e);
            return "查询菜品信息时出现错误，请稍后重试";
        }
    }
    @Tool(description = "当用户问套餐，或者本店都有什么套餐之类的话时，用这个工具")
    public String getAllCombo(){
        List<Combo> combos = comboMapper.selectAll();
        StringBuilder sb=new StringBuilder();
        for (Combo combo : combos) {
            List<ComboItem> comboItems = comboItemMapper.selectByComboId(combo.getId());
            StringBuilder stringBuilder=new StringBuilder();
            for (ComboItem comboItem : comboItems) {
                stringBuilder.append(comboItem).append(",");
            }
            String comboDishes = stringBuilder.toString();
           String c= String.format(
                    "【%s】\n价格：%.2f元\n包含菜品：%s\n详细描述：%s\n人气指数：%d★\n",
                    combo.getName(),combo.getPrice(),comboDishes,combo.getDescription(),combo.getRecommendIndex()
            );
           sb.append(c);
        }
        String allCombo = sb.toString();
        return allCombo;

    }

    @Tool(description = "查询店内的套餐详情信息，返回套餐详情和价格")
    public String getComboInfo(
            @ToolParam(description ="套餐菜品名称，例如：重庆豆花烤鱼套餐" ) String comboName
    ){
        try {
            Combo combo = comboMapper.selectByName(comboName);
            List<ComboItem> comboItems = comboItemMapper.selectByComboId(combo.getId());
            StringBuilder stringBuilder=new StringBuilder();
            for (ComboItem comboItem : comboItems) {
                stringBuilder.append(comboItem).append(",");
            }
            String comboDishes = stringBuilder.toString();

            if(combo==null){
                return "未找到套餐【" + comboName + "】的信息";
            }
            return String.format(
                    "【%s】\n价格：%.2f元\n包含菜品：%s\n详细描述：%s\n人气指数：%d★",
                    combo.getName(),combo.getPrice(),comboDishes,combo.getDescription(),combo.getRecommendIndex()
            );
        } catch (Exception e) {
            log.error("查询套餐信息失败，菜品名称：{}", comboName, e);
            return "查询套餐信息时出现错误，请稍后重试";
        }
    }
}
