<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>

<meta http-equiv=“Content-Type” content=“text/html; charset=UTF-8″>
<body>
<h2>Hello World!</h2>

<h2>SpringMVC上传文件</h2>
<form name="form1" action="/manage/product/upload.do" method="post", enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="SpringMvc上传文件" />
</form>

<h2>富文本图片上传</h2>
<form name="form1" action="/manage/product/rich_text_img_upload.do" method="post", enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="富文本图片上传" />
</form>
</body>
</html>
