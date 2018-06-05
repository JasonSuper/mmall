package com.mmall.controller.backend;

import com.mmall.common.ServerResponse;
import com.mmall.service.ICategoryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @Author Jason
 * Create in 2018-06-05 10:43
 */
@RestController
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Resource
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        return iCategoryService.addCategory(categoryName, parentId);
    }

    @RequestMapping("set_category_name.do")
    public ServerResponse setCategoryName(HttpSession session, int categoryId, String categoryName) {
        return iCategoryService.updateCategoryName(categoryId, categoryName);
    }

    @RequestMapping("get_category.do")
    public ServerResponse getChildrenParallerCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        // 查询子节点信息，并保持平级，不进行递归
        return iCategoryService.getChildrenParallerCategory(categoryId);
    }

    @RequestMapping("get_deep_category.do")
    public ServerResponse getCatetoryAndDeepChildrenCatetory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        // 查询本id和递归子节点信息
        return iCategoryService.selectCategoryAndChildrenById(categoryId);
    }
}
