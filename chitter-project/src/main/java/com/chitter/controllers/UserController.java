package com.chitter.controllers;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 23/7/12
 * Time: 8:22 PM
 * To change this template use File | Settings | File Templates.
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class UserController {
    public final SimpleJdbcTemplate db;

    @Autowired
    public UserController(SimpleJdbcTemplate db) {this.db = db;}

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/user/login", method = RequestMethod.GET)
    public String loginForm() {
        return "index";
    }

    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public ModelAndView login(@RequestParam("username") String userName,
                              @RequestParam("password") String password,
                              HttpSession session) {
        ModelAndView mv = new ModelAndView("/index");
        long userID;
        try {
            Map<String, Object> userData = db.queryForMap("select id, username, password from users where username=?",
                    userName);
            if (!userData.get("password").equals(password)) {
                mv.addObject("message", "Invalid password.");
                return mv;
            }
            userID = Integer.parseInt(String.valueOf(userData.get("id")));
        } catch (EmptyResultDataAccessException e) {
            db.update("insert into users (username, password) values(?, ?)", userName, password);
            userID = db.queryForLong("select currval('users_id_seq');");//"CALL IDENTITY()");
        }
        session.setAttribute("userName", userName);
        session.setAttribute("userID", userID);
        mv.setViewName("redirect:/test");
        return mv;
    }

    @RequestMapping(value = "/user/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}