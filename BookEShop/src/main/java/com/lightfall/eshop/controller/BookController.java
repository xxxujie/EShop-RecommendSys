package com.lightfall.eshop.controller;

import com.lightfall.eshop.pojo.Book;
import com.lightfall.eshop.service.BookService;
import com.lightfall.eshop.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import sun.net.www.http.Hurryable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private RecommendService recommendService;

    // 分页时的每页大小
    private static final int pageSize = 30;

    @RequestMapping("/detail/{bookId:[0-9]+}") // 限制只能是数字
    public String bookDetail(@PathVariable("bookId") int bookId, HttpServletRequest request, Model model) {
        // 登录验证
        HttpSession session = request.getSession();
        String userInfo = (String) session.getAttribute("userInfo");
        model.addAttribute("userInfo", userInfo);

        // 通过 id 获取图书信息
        Book book = bookService.getBookById(bookId);
        List<Book> books = new ArrayList<>();
        // 得到书的评分
        books.add(book);
        Map<Integer, BigDecimal> avgRating = recommendService.getAvgRating(books);
        if(avgRating.get(bookId) == null) {
            avgRating.put(bookId, new BigDecimal(0.000));
        }
        // 同现相似的图书，推荐给用户
        List<Book> concurSimRecs = recommendService.getConcurSimRecs(bookId);

        model.addAttribute("book", book);
        model.addAttribute("rating", avgRating);
        model.addAttribute("concurSimRecs", concurSimRecs);
        return "bookDetail";
    }

    // 书单页面
    @RequestMapping("/bookList/{categoryId:[0-9]+}/{pageNum:[0-9]+}")
    public ModelAndView showSelectedCategory(@PathVariable("categoryId") int categoryId,
                                             @PathVariable("pageNum") int pageNum, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 登录信息
        HttpSession session = request.getSession();
        String userInfo = (String) session.getAttribute("userInfo");
        modelAndView.addObject("userInfo", userInfo);

        List<Book> books;
        // 根据传来的类别获得相应的图书
        if (categoryId == 0) { // 0 代表全部图书
            books = bookService.getBooksInPage(pageNum * pageSize, pageSize);
            // 得到所有图书数量，用来计算最大页数
            int maxCounts = bookService.getBooksCount();
            Integer maxPages = maxCounts / pageSize;
            modelAndView.addObject("maxPages", maxPages);
        } else {
            books = bookService.selectCategoryInPage(categoryId, pageNum * pageSize, pageSize);
            // 取得该类的类名
            String categoryName = bookService.getCategoryName(categoryId);
            modelAndView.addObject("categoryName", categoryName);
            modelAndView.addObject("categoryId", categoryId);
            // 得到该类图书的数量，用来计算最大页数
            int maxCounts = bookService.getSelectCount(categoryId);
            Integer maxPages = maxCounts / pageSize;
            modelAndView.addObject("maxPages", maxPages);
        }
        // 得到 bookId 和评价的 Map
        Map<Integer, BigDecimal> ratingMap = recommendService.getAvgRating(books);

        modelAndView.addObject("ratingMap", ratingMap);
        modelAndView.addObject("bookList", books);

        modelAndView.addObject("pageNum", pageNum);

        modelAndView.setViewName("bookList");
        return modelAndView;
    }

    @RequestMapping("orderAction/{bookId:[0-9]+}")
    public ModelAndView orderAction(HttpServletRequest request,
                                    @PathVariable("bookId") int bookId) {
        ModelAndView modelAndView = new ModelAndView();
        // 登录信息
        HttpSession session = request.getSession();
        String userInfo = (String) session.getAttribute("userInfo");
        modelAndView.addObject("userInfo", userInfo);

        // 同现相似的图书，推荐给用户
        List<Book> concurSimRecs = recommendService.getConcurSimRecs(bookId);
        modelAndView.addObject("concurSimRecs", concurSimRecs);

        modelAndView.setViewName("order");

        return modelAndView;
    }
}
