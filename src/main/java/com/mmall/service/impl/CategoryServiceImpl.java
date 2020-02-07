package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(ICategoryService.class);

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if (null == parentId || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByError("添加参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0) {
            return  ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByError("添加品类失败");
    }

    @Override
    public ServerResponse setCategoryName(Integer categoryId, String categoryName) {
        if (null == categoryId || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByError("更新品类的参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessMessage("品类名修改成功");
        }
        return ServerResponse.createByError("品类名修改失败");
    }

    @Override
    public ServerResponse getChildrenParallelCategories(Integer categoryId) {
        if (null == categoryId) {
            return ServerResponse.createByError("获取子级品类参数有误");
        }

        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)) {
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询本节点的id及孩子节点的id
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Integer>> getCategoryAndDeepChildCategoryIds(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategories(categorySet, categoryId);
        if (CollectionUtils.isEmpty(categorySet)) {
            logger.info("未找到当前分类的子节点");
        }
        List<Integer> categoryIdList = Lists.newArrayList();
        if (null != categoryId) {
            for (Category categoryItem : categorySet) {
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    // 递归算法，算出子节点
    private Set<Category> findChildCategories(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (null != category) {
            categorySet.add(category);
        }
        // 查找子节点，递归算法一定要有一个退出的条件
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem : categoryList) {
            findChildCategories(categorySet, categoryItem.getId());
        }
        return categorySet;
    }

}
