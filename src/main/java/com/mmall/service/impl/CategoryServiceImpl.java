package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author Jason
 * Create in 2018-06-05 10:59
 */
@Service
public class CategoryServiceImpl implements ICategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    private static Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(categoryName))
            return ServerResponse.createByErrorMsg("参数错误");

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        int resultCount = categoryMapper.insert(category);
        if (resultCount > 0)
            return ServerResponse.createBySuccess("添加类别成功");
        else
            return ServerResponse.createByErrorMsg("添加类别失败");
    }

    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if (categoryId == null || StringUtils.isBlank(categoryName))
            return ServerResponse.createByErrorMsg("参数错误");

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (resultCount > 0)
            return ServerResponse.createBySuccess("更新类别名称成功");
        else
            return ServerResponse.createByErrorMsg("更新类别名称失败");
    }

    public ServerResponse<List<Category>> getChildrenParallerCategory(Integer categoryId) {
        List<Category> list = categoryMapper.selectChildrenParallerCategoryByParentId(categoryId);
        if (CollectionUtils.isEmpty(list)) {
            logger.info("未找到该父级分类的子分类");
        }
        return ServerResponse.createBySuccess(list);
    }

    /**
     * 递归查询本节点和子节点Id
     * @param categoryId 本节点Id
     * @return
     */
    public ServerResponse selectCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildrenCategory(categorySet, categoryId);
        List<Integer> list = Lists.newArrayList();

        if (categoryId != null){
            for (Category item: categorySet) {
                list.add(item.getId());
            }
        }
        return ServerResponse.createBySuccess(list);
    }

    /**
     * 递归查询类别子节点
     *
     * @param categorySet 返回Set集合
     * @param categoryId  父级Id
     * @return
     */
    private Set<Category> findChildrenCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
        }

        // 递归退出条件
        List<Category> list = categoryMapper.selectChildrenParallerCategoryByParentId(categoryId);
        for (Category item : list) {
            findChildrenCategory(categorySet, item.getId());
        }
        return categorySet;
    }
}

