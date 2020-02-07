package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.List;

public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse setCategoryName(Integer categoryId, String categoryName);

    ServerResponse getChildrenParallelCategories(Integer categoryId);

    ServerResponse<List<Integer>> getCategoryAndDeepChildCategoryIds(Integer categoryId);
}
