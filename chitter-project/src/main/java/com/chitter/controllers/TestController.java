package com.chitter.controllers;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 23/7/12
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TestController {

    public SimpleJdbcTemplate db;


    @Autowired
    public void TestController(SimpleJdbcTemplate db){
        this.db = db;
    }

    @RequestMapping(value = "/test")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView("index");
        return mv;
    }

    @RequestMapping(value = "/test/tweet", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,ArrayList<HashMap<String,String>>> testTweet() {
        HashMap<String,ArrayList<HashMap<String,String>>>  x = new HashMap<String,ArrayList<HashMap<String,String>>>();
        HashMap<String,String> y = new HashMap<String, String>();
        y.put("id","1");
        y.put("name","A B C D");
        y.put("tweet", "shdfgadshjfgdihkjsafhdluidsahgfhdsgakfcghadsgfvchkjdsgajhfvgdsajhf");

        ArrayList<HashMap<String,String>> z = new ArrayList<HashMap<String,String>>();
        z.add(y);
        x.put("tweetList",z);
        return x;
    }

    @RequestMapping(value = "/users/{name}")
    public ModelAndView profileGet(@PathVariable String name) {
        ModelAndView mv = new ModelAndView("profile");
        mv.addObject("userexists",name.equals("1") || name.equals("2"));
        mv.addObject("user1",name.equals("1"));
        mv.addObject("name",name);
        return mv;
    }

    @RequestMapping(value = "/search/users")
    @ResponseBody
    public ArrayList<HashMap<String,String>> searchUsers(@RequestParam String term) {
        String searchTerm = "%"+term+"%";
        ArrayList<HashMap<String,String>> z = new ArrayList<HashMap<String,String>>();
        List<Map<String, Object>> M = db.queryForList("select name,id from users where name ilike ? limit 10", searchTerm);
        for(Map<String,Object> m : M){
            HashMap<String,String> a = new HashMap<String, String>();
            a.put("label", (String) m.get("name"));
            a.put("value", (String) String.valueOf(m.get("id")));
            z.add(a);
        }

        return z;
    }
}
