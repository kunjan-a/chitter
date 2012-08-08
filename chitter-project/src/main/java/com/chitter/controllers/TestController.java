package com.chitter.controllers;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 23/7/12
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TestController {

    public NamedParameterJdbcTemplate db;


    @Autowired
    public void TestController(NamedParameterJdbcTemplate db) {
        this.db = db;
    }

    @RequestMapping(value = "/test")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView("index");
        return mv;
    }

    @RequestMapping(value = "/test/tweet", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, ArrayList<HashMap<String, String>>> testTweet() {
        HashMap<String, ArrayList<HashMap<String, String>>> x = new HashMap<String, ArrayList<HashMap<String, String>>>();
        HashMap<String, String> y = new HashMap<String, String>();
        y.put("id", "1");
        y.put("name", "A B C D");
        y.put("tweet", "shdfgadshjfgdihkjsafhdluidsahgfhdsgakfcghadsgfvchkjdsgajhfvgdsajhf");

        ArrayList<HashMap<String, String>> z = new ArrayList<HashMap<String, String>>();
        z.add(y);
        x.put("tweetList", z);
        return x;
    }

    @RequestMapping(value = "/search/users")
    @ResponseBody
    public ArrayList<HashMap<String, String>> searchUsers(@RequestParam String term) {
        String searchTerm = "%" + term + "%";
        ArrayList<HashMap<String, String>> z = new ArrayList<HashMap<String, String>>();

        String sql = "select name,id from users where name ilike :searchTerm limit 10";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource("searchTerm", searchTerm);
        List<Map<String, Object>> M = db.queryForList(sql, namedParameters);

        for (Map<String, Object> m : M) {
            HashMap<String, String> a = new HashMap<String, String>();
            a.put("label", (String) m.get("name"));
            a.put("value", (String) String.valueOf(m.get("id")));
            z.add(a);
        }

        return z;
    }
}
