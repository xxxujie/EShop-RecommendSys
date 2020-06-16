package com.lightfall.eshop.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.lightfall.eshop.pojo.Book;
import com.lightfall.eshop.pojo.LogData;
import com.lightfall.eshop.service.BookService;
import com.lightfall.eshop.service.SearchService;
import com.lightfall.eshop.service.UserService;
import com.lightfall.eshop.utils.BookUtils;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j(topic = "userAction")
@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

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

        // 将搜索结果写入日志
        LogData<Book> bookLogData = new LogData<>();
        for (Book book : books) {
            // 如果搜索的字段和结果书名的相似度大于 0.5，就加入日志
            if (BookUtils.levenshtein(book.getBookName(), bookName) > 0.3) {
                bookLogData.setType("search");
                bookLogData.setUsername(userInfo);
                bookLogData.setData(book);
                String logData = JSON.toJSON(bookLogData).toString();
                log.info(logData);
            }
        }

        int maxCounts = searchService.getSearchCount(bookName);
        Integer maxPages = maxCounts/pageSize;
        modelAndView.addObject("resultCount", maxCounts); // 结果条数
        modelAndView.addObject("maxPages", maxPages);
        modelAndView.addObject("pageNum", pageNum);
        modelAndView.setViewName("searchResult");

        return modelAndView;
    }

}
