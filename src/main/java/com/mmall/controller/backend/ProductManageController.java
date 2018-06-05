package com.mmall.controller.backend;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author Jason
 * Create in 2018-06-05 16:55
 */
@RestController
@RequestMapping("/manage/product")
public class ProductManageController {

    @Resource
    private IProductService iProductService;

    @RequestMapping("product_save.do")
    public ServerResponse productSave(Product product) {
        return iProductService.productSave(product);
    }

    @RequestMapping("set_sale_status.do")
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        return iProductService.setSaleStatus(productId, status);
    }

    @RequestMapping("detail.do")
    public ServerResponse<ProductDetailVo> getDetail(Integer productId) {
        return iProductService.manageProductDetail(productId);
    }

    @RequestMapping("list.do")
    public ServerResponse getLsit(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return iProductService.getLsit(pageNum, pageSize);
    }

    @RequestMapping("search.do")
    public ServerResponse productSearch(String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return iProductService.searchProduct(productName, productId, pageNum, pageSize);
    }
}