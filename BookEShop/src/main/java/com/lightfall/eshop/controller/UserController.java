package com.lightfall.eshop.controller;

import com.lightfall.eshop.pojo.User;
import com.lightfall.eshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 登录页面
    @RequestMapping("/login")
    public ModelAndView login(HttpServletRequest request) {
        // 查看有没有拦截器传来的信息
        String errMsg = (String) request.getAttribute("errMsg");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errMsg", errMsg);
        modelAndView.setViewName("login");
        return modelAndView;
    }

    // 登录的行为
    @RequestMapping("/loginAction")
    public ModelAndView loginAction(@RequestParam("username") String username, @RequestParam("password") String password,
                              HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        HttpSession session = request.getSession();

        // 通过输入的用户名查找表中记录
        User user = userService.loginCheck(username);
        // 如果为空，说明用户不存在
        if(user == null) {
            String errMsg = "用户不存在！";
            modelAndView.addObject("errMsg", errMsg);
            modelAndView.setViewName("login");
            System.out.println(errMsg); // log
            return modelAndView;
        }
        // 如果表中密码和输入的密码相同，则登录成功
        if(user.getPassword().equals(password)) {
            System.out.println("用户 "+username+" 登录成功"); // log
            // String nickName = userService.getNickNameByUserName(username);
            // 给 Session 加一条记录用于保存登录状态
            session.setAttribute("userInfo", username);
            int userId = userService.getUserIdByUserName(username);
            session.setAttribute("userId", userId);
            modelAndView.setViewName("redirect:/index");
            return modelAndView;
        }
        // 剩下的情况就是密码错误
        String msg = "你输入的密码有误！";
        modelAndView.addObject("errMsg", msg);
        modelAndView.setViewName("login");
        System.out.println("用户 "+username+" 密码错误"); // log
        return modelAndView;
    }

    // 注销的行为
    @RequestMapping("/logoutAction")
    public String logoutAction(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("userInfo");
        session.removeAttribute("userId");
        return "redirect:/index";
    }

    // 注册页面
    @RequestMapping("/register")
    public String register() {
        return "register";
    }

    // 注册动作
    @RequestMapping("/registerAction")
    public ModelAndView registerAction(@RequestParam("username") String username,
                                       @RequestParam("password") String password,
                                       @RequestParam("nickName") String nickName,
                                       HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        User user = new User(username, password, nickName);
        // 如果 username 重复会抛出异常
        try {
            userService.addUser(user);
        } catch (Exception e) {
            e.getStackTrace();
            // 发出错误消息
            modelAndView.addObject("errMsg", "用户名已存在！");
            modelAndView.setViewName("register");
            return modelAndView;
        }
        // 注册成功自动登录
        request.setAttribute("username", username);
        request.setAttribute("password", password);
        // 将 username 和 password 转发给登录动作
        modelAndView.setViewName("forward:/user/loginAction");
        return modelAndView;
    }
}
