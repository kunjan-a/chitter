package com.chitter.controllers;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 23/7/12
 * Time: 8:22 PM
 * To change this template use File | Settings | File Templates.
 */

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class UserController {
    public final SimpleJdbcTemplate db;

    @Autowired
    public UserController(SimpleJdbcTemplate db) {
        this.db = db;
    }

    @RequestMapping("/")
    public ModelAndView index(HttpSession session) {
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("name", session.getAttribute("userName"));
        return mv;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginForm() {
        return "login";
    }

    @RequestMapping(value = "/request/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> login(@RequestParam("email") String email,
                                     @RequestParam("password") String password,
                                     HttpSession session) {

        String msg = "Invalid username/password.";
        String status = "0";
        long userID;
        try {
            Map<String, Object> userData = db.queryForMap("select id, name, email, password from users where email=?",
                    email);
            if (userData.get("password").equals(password)) {
                status = "1";
                msg = "Login successful";
                session.setAttribute("userName", userData.get("name"));
                session.setAttribute("userID", Integer.parseInt(String.valueOf(userData.get("id"))));
            }

        } catch (EmptyResultDataAccessException e) {

        }
        return ImmutableMap.of("Success", status, "msg", msg);
    }


    @RequestMapping(value = "/request/register", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> register(@RequestParam("email") String email,
                                        @RequestParam("password") String password,
                                        @RequestParam("name") String name,
                                        @RequestParam(required = false) String photoPath,
                                        HttpSession session) {

        String msg = "Registration Failed";
        String status = "0";
        long userID;
        try {
            db.update("insert into users (name, email, password) values(?, ?, ?)", name, email, password);
            userID = db.queryForLong("select currval('users_id_seq');");//"table_column_seq

            status = "1";
            msg = "Registration successful. We trust you so no need to validate the email";
            session.setAttribute("userName", name);
            session.setAttribute("userID", Integer.parseInt(String.valueOf(userID)));

        } catch (DataAccessException e) {
        }
        return ImmutableMap.of("Success", status, "msg", msg);
    }


    @RequestMapping(value = "/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}