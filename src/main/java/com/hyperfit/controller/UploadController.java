package com.hyperfit.controller;

import com.hyperfit.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * <p> 文件上传控制器 </p>
 *
 * @author GuoFeng Tang
 * @version V1.0
 * @date 2017/12/3 17:53
 */
@Controller
public class UploadController extends BaseController {

    /**
     * 上传文件目录
     */
    @Value("#{configProperties['webapps.path']}")
    private String webappsPath;
    /**
     * 文件访问路径
     */
    @Value("#{configProperties['view.path']}")
    private String viewPath;

    /**
     * 上传文件
     */
    @RequestMapping("/fileUpload.do")
    @ResponseBody
    public void fileUpload(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期文件夹格式
            String dateDir = df.format(new Date()) + "/";// 日期目录
            String typeDir;// 文件类型目录
            String filePath;// 上传文件路径
            filePath = webappsPath + "/upload/";
            // response.setContentType("text/html;charset=gb2312");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");

            String attachment = "";// 文件访问路径,多个以","分隔
            for (MultipartFile file : files.values()) {
                if (file.getSize() != 0) {
                    int begin = file.getOriginalFilename().lastIndexOf(".") + 1;
                    String fileType = file.getOriginalFilename().substring(begin, file.getOriginalFilename().length());// 文件后缀名
                    typeDir = fileType + "/";
                    // 创建目录
                    File dir = new File(filePath + typeDir + dateDir);
                    if (!dir.exists()) {
                        if (!dir.mkdirs()) {
                            String msg = "{\"code\": \"2\" ,\"msg\":\"Create directory failed!\"}";
                            out.print(msg);
                            log.error("上传文件：Create directory failed!");
                            return;
                        }
                    }
                    // 生成文件名
                    String fileName = System.currentTimeMillis() + StringUtil.getRandomStr(5) + "." + fileType;
                    attachment += "," + viewPath + typeDir + dateDir + fileName;// 文件访问路径
                    File targetFile = new File(filePath + typeDir + dateDir + fileName);
                    if (!targetFile.exists()) {
                        if (targetFile.createNewFile()) {
                            file.transferTo(targetFile);
                        } else {
                            String msg = "{\"code\": \"2\" ,\"msg\":\"createNewFile  failed!\"}";
                            out.print(msg);
                            log.error("上传文件：createNewFile  failed!");
                            return;
                        }
                    }
                }
            }
            if (StringUtils.isBlank(attachment)) {
                String msg = "{\"code\": \"11\" ,\"msg\":\"file is empty\"}";
                log.error("上传文件：file is empty");
                out.print(msg);
                return;
            }
            attachment = attachment.substring(1);
            String msg = "{\"code\": \"0\" ,\"msg\":\"success\",\"data\":\"" + attachment + "\"}";
            log.info("上传文件：" + attachment);
            out.print(msg);
        } catch (Exception e) {
            log.error("上传文件失败", e);
            if (out != null) {
                String msg = "{\"code\": \"2\" ,\"msg\":\"upload error!\"}";
                out.print(msg);
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
