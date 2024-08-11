package com.example.documentReview.controller;

import com.example.documentReview.domain.ApprFile;
import com.example.documentReview.domain.RasKey;
import com.example.documentReview.domain.User;
import com.example.documentReview.mapper.RasKeyMapper;
import com.example.documentReview.utils.DateUtils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.*;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/user")
public class RasKeyController {

    @Autowired
    private RasKeyMapper rasKeyMapper;


    @Autowired
    private HttpSession httpSession;


    @RequestMapping("/getRasKeyPri")
    @ResponseBody
    public String getRasKeyPri(String userName){
        List<RasKey> rasKeys= rasKeyMapper.findByUserId(userName);
        return rasKeys.get(0).getPriKey();
    }

    @RequestMapping("/getRasKeyPub")
    @ResponseBody
    public String getRasKeyPub(String userName){
        List<RasKey> rasKeys= rasKeyMapper.findByUserId(userName);
        return rasKeys.get(0).getPubKey();
    }

    @RequestMapping("/findAllArs")
    public String findAllArs(Model model){
        List<RasKey> rasKeys= rasKeyMapper.findAll();
        for (RasKey rasKey : rasKeys) {
            rasKey.setCraDateStr(DateUtils.getDateStr(rasKey.getCraDate()));
        }
        model.addAttribute("raskeys",rasKeys);
        return "rasKey/rasKeyManage";
    }

    @RequestMapping("/addRasKey")
    public String addRasKey(Model model) {
        User user = (User) httpSession.getAttribute("user");
        List<RasKey> rasKeys= rasKeyMapper.findByUserId(user.getUserName());
        model.addAttribute("raskeys",rasKeys);
        return "rasKey/rasKeyManage";
    }
}
