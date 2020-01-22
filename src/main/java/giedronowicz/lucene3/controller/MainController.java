package giedronowicz.lucene3.controller;

import giedronowicz.lucene3.engine_8_4_1.MyResult;
import giedronowicz.lucene3.engine_8_4_1.MySearchFiles;
import giedronowicz.lucene3.model.FormQuery;
import giedronowicz.lucene3.model.Page;
import org.apache.lucene.document.Document;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;


@Controller
public class MainController {

    @PostMapping("/sending")
    public String getQuery(@RequestParam String query, Model model) {
        model.addAttribute("query", query);
        return showAll( query, model);
    }

    @GetMapping("/all")
    public String showAll(String query, Model model) {

        String params[] =
                {
                        "-index", "src\\main\\java\\giedronowicz\\lucene3\\engine_8_4_1\\index",
                        "-docs", "src\\main\\java\\giedronowicz\\lucene3\\engine_8_4_1\\buissness_domains",
                        "-paging", "10",
                        "-query", query
                };

        MySearchFiles searchFiles = new MySearchFiles();
        MyResult myResult = null;
        try {
            myResult = searchFiles.main(params);

        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("myResult", myResult );
        return "results";
    }

    @PostMapping("/results")
    public String results(@ModelAttribute FormQuery query )
    {
        System.out.println(query);
        return "results";
    }
}
