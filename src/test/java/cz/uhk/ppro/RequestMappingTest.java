package cz.uhk.ppro;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestMappingTest {
    @RequestMapping(value = "/test", method = {RequestMethod.GET, RequestMethod.POST})
    public static void main(String [] args)  throws IOException {
        System.out.println("HELLO WORLD");
    }
}
