package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseEnum;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Jason
 * Create in 2018-06-05 17:15
 */
public class ProductServiceImpl implements IProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private CategoryMapper categoryMapper;

    public ServerResponse productSave(Product product) {
        if (product != null) {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] image = product.getSubImages().split(",");
                if (image.length > 0)
                    product.setMainImage(image[0]);
            }

            if (product.getId() == null) {
                int resultCount = productMapper.insert(product);
                if (resultCount > 0)
                    return ServerResponse.createBySuccessMsg("更新产品成功");
                else
                    return ServerResponse.createBySuccessMsg("更新产品失败");
            } else {
                int resultCount = productMapper.updateByPrimaryKey(product);
                if (resultCount > 0)
                    return ServerResponse.createBySuccessMsg("新增产品成功");
                else
                    return ServerResponse.createBySuccessMsg("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMsg("新增或更新产品的参数错误");
    }

    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null)
            return ServerResponse.createByErrorCodeMsg(ResponseEnum.ILLEGAL_ARGUMENT.getKey(), ResponseEnum.ILLEGAL_ARGUMENT.getValue());

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int resultCount = productMapper.updateByPrimaryKeySelective(product);
        return resultCount > 0 ? ServerResponse.createBySuccess("更新销售状态成功") : ServerResponse.createBySuccess("更新销售状态失败");
    }

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if (productId == null)
            return ServerResponse.createByErrorCodeMsg(ResponseEnum.ILLEGAL_ARGUMENT.getKey(), ResponseEnum.ILLEGAL_ARGUMENT.getValue());
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null)
            return ServerResponse.createByErrorMsg("产品已下架或删除");
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null)
            productDetailVo.setParentCategoryId(0);
        else
            productDetailVo.setParentCategoryId(category.getParentId());

        productDetailVo.setCreateTime(DateTimeUtil.dateParseStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateParseStr(product.getUpdateTime()));
        return productDetailVo;
    }

    public ServerResponse<PageInfo> getLsit(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> list = productMapper.selectList();
        List<ProductListVo> productListVoList = new ArrayList<>();
        for (Product product : list) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageResult = new PageInfo(list);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setStatus(product.getStatus());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
        return productListVo;
    }

    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName))
            productName = "%" + productName + "%";

        List<Product> list = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVo> productListVoList = new ArrayList<>();
        for (Product product : list) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(list);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }
}
