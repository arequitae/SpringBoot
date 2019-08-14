package com.example.demo.controller;

import com.example.demo.dto.QuestionPageDTO;
import com.example.demo.exception.CustomerException;
import com.example.demo.exception.CustomizeErrorCode;
import com.example.demo.mapper.QuestionMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.Question;
import com.example.demo.model.User;
import com.example.demo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionService questionService;

    @RequestMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id")long id,Model model){
        Question question = questionMapper.getById(id);
        if (question==null){
            throw new CustomerException(CustomizeErrorCode.PUBLISH_NOT_FOUND);
        }
        model.addAttribute("question",question);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("tag",question.getTag());
        return "publish";
    }

    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(String title, String description, String tag,
                            HttpServletRequest request, Model model,@RequestParam(value ="id", required =false) long id){
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        Cookie[] cookies = request.getCookies();
        User user = (User) request.getSession().getAttribute("user");
        if (user==null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }
        question.setCreator(user.getId());
        question.setId(id);
        model.addAttribute("title",title);
        questionService.createOrUpdate(question);
        return "redirect:/";
    }
}