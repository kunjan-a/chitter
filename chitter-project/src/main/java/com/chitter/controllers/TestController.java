package com.chitter.controllers;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 23/7/12
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class TestController {
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
}
