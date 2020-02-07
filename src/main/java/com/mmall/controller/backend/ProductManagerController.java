package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("manage/product")
public class ProductManagerController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 增加产品的逻辑
            return iProductService.saveOrUpdateProduct(product);
        } else {
            return ServerResponse.createByError("用户权限不足，需要管理员权限");
        }
    }

    @RequestMapping("set_sale_state.do")
    @ResponseBody
    public ServerResponse setSaleState(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 更新产品状态
            return iProductService.setSaleState(productId, status);
        } else {
            return ServerResponse.createByError("用户权限不足，需要管理员权限");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 更新产品状态
            return iProductService.manageProductDetail(productId);
        } else {
            return ServerResponse.createByError("用户权限不足，需要管理员权限");
        }
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                   @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 更新产品状态
            return iProductService.getProductList(pageNum, pageSize);
        } else {
            return ServerResponse.createByError("用户权限不足，需要管理员权限");
        }
    }


    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> productSearch(HttpSession session, String productName, Integer productId,
                                                                 @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                                 @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 更新产品状态
            return iProductService.productSearch(productName, productId, pageNum, pageSize);
        } else {
            return ServerResponse.createByError("用户权限不足，需要管理员权限");
        }
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,
            @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        } else {
            return ServerResponse.createByError("用户权限不足，需要管理员权限");
        }

    }


    @RequestMapping("rich_text_img_upload.do")
    @ResponseBody
    public Map richTextImgUpload(HttpSession session,
                                 @RequestParam(value = "upload_file", required = false) MultipartFile file,
                                 HttpServletRequest request, HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            resultMap.put("success",false);
            resultMap.put("msg", "用户未登录，请登录管理员");
            return resultMap;
        }

        // 富文本中对于返回值有自己的要求，我们使用的是simditor,所以按照simditor的要求进行返回
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            if (StringUtils.isBlank(targetFileName)) {
                resultMap.put("success",false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);

            response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            return resultMap;
        } else {
            resultMap.put("success",false);
            resultMap.put("msg", "用户权限不足，需要管理员权限");
            return resultMap;
        }

    }
}
