package example.data.share.ExampleDataShareJava.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RouteController {
    @RequestMapping(value = "/{[path:[^\\.]*}")
    public RedirectView redirect() {
        return new RedirectView("/");
    }
}
