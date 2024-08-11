package com.example.documentReview.controller;

import com.example.documentReview.domain.ApprFile;
import com.example.documentReview.domain.RasKey;
import com.example.documentReview.domain.User;
import com.example.documentReview.mapper.ApprFileMapper;
import com.example.documentReview.mapper.RasKeyMapper;
import com.example.documentReview.utils.DateUtils;
import com.example.documentReview.utils.RSAUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/user")
public class ApprFileController {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private ApprFileMapper apprFileMapper;

    @Autowired
    private RasKeyMapper rasKeyMapper;

    @Value("${filePath}")
    private String filePath;

    @RequestMapping("/subApprfile")
    public String subApprfile(){
        return "/apprfile/subApprfile";
    }

    @RequestMapping("/mySubFileManagePage")
    public String mySubFileManage(Model model){
        User user = (User) httpSession.getAttribute("user");
        List<ApprFile> apprFiles = apprFileMapper.findByUserName(user.getUserName());
        for (ApprFile apprFile : apprFiles) {
            switch (apprFile.getStatus()){
                case "0":
                    apprFile.setStatusMsg("待审批");
                    break;
                case "1":
                    apprFile.setStatusMsg("审批通过");
                    break;
                case "2":
                    apprFile.setStatusMsg("审批未通过");
                    break;
            }
            apprFile.setCraDateStr(DateUtils.getDateStr(apprFile.getCraDate()));
            apprFile.setUpDataStr(DateUtils.getDateStr(apprFile.getUpDate()));

        }
        model.addAttribute("apprFiles",apprFiles);
        return "apprfile/mySubFileManage";
    }


    @RequestMapping("/apprFileManagePage")
    public String apprFileManagePage(Model model){
        User user = (User) httpSession.getAttribute("user");
        List<ApprFile> apprFiles = apprFileMapper.findByUserName(user.getUserName());
        for (ApprFile apprFile : apprFiles) {
            switch (apprFile.getStatus()){
                case "0":
                    apprFile.setStatusMsg("待审批");
                    break;
                case "1":
                    apprFile.setStatusMsg("审批通过");
                    break;
                case "2":
                    apprFile.setStatusMsg("审批未通过");
                    break;
            }
            apprFile.setCraDateStr(DateUtils.getDateStr(apprFile.getCraDate()));
            apprFile.setUpDataStr(DateUtils.getDateStr(apprFile.getUpDate()));
            apprFile.setFilePath(filePath+"\\"+apprFile.getFileName());

        }
        model.addAttribute("apprFiles",apprFiles);
        return "apprfile/apprFileManage";
    }



    @RequestMapping("/subApprfileSubmit")
    public String subApprfileSubmit(Model model){
        User user = (User) httpSession.getAttribute("user");
        List<ApprFile> apprFiles = apprFileMapper.findByUserName(user.getUserName());
        model.addAttribute("apprFiles",apprFiles);
        return "apprfile/apprFileManage";
    }

    @RequestMapping("/submitFile")
    public String submitFile(@RequestParam("files") MultipartFile files, String title, String remake) throws Exception {
        User user = (User) httpSession.getAttribute("user");
        List<RasKey> byUserId = rasKeyMapper.findByUserId(user.getUserName());
        File file = new File(filePath);
        if(!file.exists()){
            file.mkdir();
        }
        String filesPath = filePath+"\\"+files.getOriginalFilename();
        File dest = new File(filesPath);
        files.transferTo(dest);
        ApprFile apprFile = new ApprFile();
        apprFile.setFileName(files.getOriginalFilename());
        apprFile.setTitle(title);
        apprFile.setRemark(remake);
        apprFile.setFileSm(RSAUtil.encryptByPublicKey(files.getOriginalFilename(),byUserId.get(0).getPubKey()));
        apprFile.setUserName(user.getUserName());
        apprFile.setCraDate(new Date());
        apprFile.setStatus("0");
        apprFileMapper.insert(apprFile);

        return "apprfile/subApprfile";
    }

    @RequestMapping("/delAppr")
    @ResponseBody
    public String delAppr(String id){
        try {
            apprFileMapper.delAprFile(id);
            return "撤销成功";
        }catch (Exception e){
            return "撤销失败";
        }
    }


    @RequestMapping("/upAprFile")
    @ResponseBody
    public String upAprFile(int id, String status){
        try {
            ApprFile apprFile = new ApprFile();
            apprFile.setId(id);
            apprFile.setStatus(status);
            apprFile.setUpDate(new Date());
            apprFileMapper.update(apprFile);
            return "审批完成";
        }catch (Exception e){
            log.error("审批异常",e);
            return "审批异常";
        }
    }

    @RequestMapping("/downFile/{id}")
    public String downloadFile(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) {

        ApprFile byId = apprFileMapper.findById(id);
        String realPath = filePath+"\\"+byId.getFileName();
        String fileName = byId.getFileName();


        //创建文件抽象类
        File file = new File(realPath);

        //判断文件是否存在
        if(!file.exists()){
            return "文件不存在";
        }

        //从服务器通过文件输入流读入文件,然后通过文件输出流由Response写出给浏览器
        FileInputStream is = null;
        ServletOutputStream os = null;
        try {
            is = new FileInputStream(file);

            //设置响应头信息
            response.setHeader("content-disposition", "attachment:fileName="+ URLEncoder.encode(fileName, "UTF-8"));
            os = response.getOutputStream();

            //IO工具类复制操作
            IOUtils.copy(is, os);

        } catch (IOException e) {
            e.printStackTrace();
            return "文件下载错误";
        } finally {
            //关闭资源
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
        return "下载成功!!!";

    }



}
