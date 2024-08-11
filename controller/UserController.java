package com.example.documentReview.controller;

import com.example.documentReview.domain.Base64Utils;
import com.example.documentReview.domain.KeyVa;
import com.example.documentReview.domain.RasKey;
import com.example.documentReview.domain.User;
import com.example.documentReview.mapper.RasKeyMapper;
import com.example.documentReview.mapper.UserMapper;
import com.example.documentReview.utils.DateUtils;
import com.example.documentReview.utils.RSAUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户管理
 */
@Controller
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private RasKeyMapper rasKeyMapper;

    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;//设置长度


    /**
     * 登录跳转
     *
     * @param model
     * @return
     */
    @GetMapping("/login")
    public String loginGet(Model model) {
        return "login";
    }

    /**
     * 登录
     *
     * @param
     * @param model
     * @param
     * @return
     */
    @PostMapping("/login")
    public String loginPost(User user, Model model) throws Exception {
        User user1 = userMapper.selectByName(user);
        List<RasKey> byUserId = rasKeyMapper.findByUserId(user.getUserName());
        if(byUserId.isEmpty()){
            model.addAttribute("error", "用户名或密码错误，请重新登录！");
            return "login";
        }else {
            String decrypt = RSAUtil.decryptByPrivateKey(user1.getPassword(), byUserId.get(0).getPriKey());
            if(user.getPassword().equals(decrypt)){
                httpSession.setAttribute("user", user1);
                httpSession.setAttribute("limFlag", user1.getLimitFlag());
                return "dashboard";
            }else {
                model.addAttribute("error", "用户名或密码错误，请重新登录！");
                return "login";
            }
        }
    }

    /**
     * 注册
     *
     * @param model
     * @return
     */
    @GetMapping("/register")
    public String register(Model model) {
        return "register";
    }

    /**
     * 注册
     *
     * @param model
     * @return
     */
    @PostMapping("/register")
    public String registerPost(User user, Model model) throws Exception {
        System.out.println("用户名" + user.getUserName());
        try {
            userMapper.selectIsName(user);
            model.addAttribute("error", "该账号已存在！");
        } catch (Exception e) {

            //获取公、私钥值
            KeyVa key = RSAUtil.getKey();
            RasKey rasKey = new RasKey();
            rasKey.setUserName(user.getUserName());
            rasKey.setPriKey(key.getPriKey());
            rasKey.setPubKey(key.getPubKey());
            rasKey.setCraDate(new Date());
            rasKeyMapper.insert(rasKey);

            user.setAddDate(new Date());
            user.setUpdateDate(new Date());
            user.setPassword(RSAUtil.encryptByPublicKey(user.getPassword(),key.getPubKey()));
            user.setState(1);
            user.setLimitFlag("2");
            userMapper.insert(user);
            model.addAttribute("error", "恭喜您，注册成功！");
            return "login";
        }

        return "register";
    }

    /**
     * 登录跳转
     *
     * @param model
     * @return
     */
    @GetMapping("/forget")
    public String forgetGet(Model model) {
        return "forget";
    }


    @GetMapping("/userManage")
    public String userManageUpdataGet(Model model) {
        User user = (User) httpSession.getAttribute("user");
        User user1 = userMapper.selectByNameAndPwd(user);
        model.addAttribute("user", user1);
        return "user/userManage";
    }

    @GetMapping("/manageUser")
    public String userManageGet(Model model) {
        User user = (User) httpSession.getAttribute("user");
        List<User> userList = new ArrayList<>();
        List<User> users = userMapper.findAll();
        for (User user1 : users) {
            user1.setAddDateStr(DateUtils.getDateStr(user.getAddDate()));
            user1.setUpdateDateStr(DateUtils.getDateStr(user.getUpdateDate()));
            switch (user1.getState()){
                case 1:
                    user1.setStateStr("正常");
                    break;
                case 2:
                    user1.setStateStr("冻结");
                    break;
            }
            switch (user1.getLimitFlag()){
                case "0":
                    user1.setLimitFlagStr("管理员");
                    break;
                case "1":
                    user1.setLimitFlagStr("审批员");
                    break;
                case "2":
                    user1.setLimitFlagStr("用户");
                    break;
            }
            if(user.getUserName().equals(user1.getUserName())){
                continue;
            }
            userList.add(user1);
        }
        model.addAttribute("users", userList);
        return "user/manageUser";
    }

    @PostMapping("/userManage")
    public String userManagePost(Model model, User user, HttpSession httpSession) throws Exception {
        List<RasKey> byUserId = rasKeyMapper.findByUserId(user.getUserName());
        Date date = new Date();
        user.setUpdateDate(date);
        user.setPassword(RSAUtil.encryptByPublicKey(user.getPassword(),byUserId.get(0).getPubKey()));
        int i = userMapper.update(user);
        httpSession.setAttribute("user",user);
        return "redirect:userManage";
    }

    @RequestMapping("/delUser")
    @ResponseBody
    public String delUser(int id){
        userMapper.delUser(id);
        return "删除用户成功";
    }


    @RequestMapping("/setLimitUser")
    @ResponseBody
    public String setLimitUser(int id, @RequestParam("limitFlag") String limitFlag){
        User user = new User();
        user.setId(id);
        user.setLimitFlag(limitFlag);
        userMapper.updateLimit(user);
        return "设置成功";
    }

}
