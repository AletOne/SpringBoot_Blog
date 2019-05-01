package wang.blog.util;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JBlogUtil {

    public static Map<String, String> categoryMap;
    static {
        categoryMap = new HashMap<>();
        categoryMap.put("Java","Java");
        categoryMap.put("Web","Web");
        categoryMap.put("Linux","Linux");
        categoryMap.put("Distributed System","Distributed System");
        categoryMap.put("Database","Database");
        categoryMap.put("Algorithm","Algorithm");
        categoryMap.put("Other","Other");
    }

    private static final Logger logger = LoggerFactory.getLogger(JBlogUtil.class);
    public static String[] category = {"Java", "Web", "Linux", "Distributed System", "Database", "Algorithm", "Other"};


    public static String tranfer(String content){
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Node document = parser.parse(content);
        return renderer.render(document);
    }
}
