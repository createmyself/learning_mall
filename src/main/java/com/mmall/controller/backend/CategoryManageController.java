package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName,
                                      @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        // 校验是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 是管理员
            // 增加我们处理分类的逻辑
            return iCategoryService.addCategory(categoryName, parentId);
        } else {
            return ServerResponse.createByError("无权限操作，需要管理员权限");
        }

    }

    @RequestMapping(value = "set_category_name.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null != user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        // 校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 是管理员
            // 修改品类名称
            return iCategoryService.setCategoryName(categoryId, categoryName);
        } else {
            return ServerResponse.createByError("无权限操作，需要管理员权限操作");
        }
    }

    @RequestMapping(value = "get_children_parallel_categories.do", method = RequestMethod.GET)
    public ServerResponse getChildrenParallelCategories(HttpSession session,
                                                      @RequestParam(value = "categoryId",defaultValue = "0")  Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 验证是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 是管理员
            // 查询子节点的category信息，并且保持平级，不递归
            return iCategoryService.getChildrenParallelCategories(categoryId);
        } else {
            // 不是管理员
            return ServerResponse.createByError("无权限操作，需要管理员权限操作");
        }
    }

    @RequestMapping(value = "get_deep_categoryIds.do", method = RequestMethod.GET)
    public ServerResponse getCategoryAndDeepChildrenCategoryIdsz(HttpSession session,
                                                               @RequestParam(value = "categoryId",defaultValue = "0")  Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 验证是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 是管理员
            // 查询子节点的categoryId，及当前子节点的id
            return iCategoryService.getCategoryAndDeepChildCategoryIds(categoryId);
        } else {
            // 不是管理员
            return ServerResponse.createByError("无权限操作，需要管理员权限操作");
        }
    }
}
