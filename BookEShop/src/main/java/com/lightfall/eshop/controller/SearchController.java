package com.lightfall.eshop.controller;

import com.lightfall.eshop.pojo.Book;
import com.lightfall.eshop.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.Encoder;
import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    // 分页时的每页大小
    private static final int pageSize = 30;

    @RequestMapping("/searchAction")
    public String searchAction(@RequestParam("bookName") String bookName,
                               HttpServletRequest request) {
        HttpSession session = request.getSession();

        // 把图书名加入会话
        session.setAttribute("bookName", bookName);

        return "redirect:/search/searchResult/0";
    }

    @RequestMapping("/searchResult/{pageNum}")
    public ModelAndView searchResult(@PathVariable("pageNum") int pageNum,
                                     HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 登录信息
        HttpSession session = request.getSession();
        String userInfo = (String) session.getAttribute("userInfo");
        modelAndView.addObject("userInfo", userInfo);

        // 得到会话中图书的名字
        String bookName = (String) session.getAttribute("bookName");

        // 获得搜索到的图书
        List<Book> books = searchService.getSearchResultInPage(bookName, pageNum * pageSize, pageSize);
        modelAndView.addObject("bookList", books);

        int maxCounts = searchService.getSearchCount(bookName);
        Integer maxPages = maxCounts/pageSize;
        modelAndView.addObject("resultCount", maxCounts); // 结果条数
        modelAndView.addObject("maxPages", maxPages);
        modelAndView.addObject("pageNum", pageNum);
        modelAndView.setViewName("searchResult");

        return modelAndView;
    }

}
