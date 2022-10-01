package olx.rent.bot.olx;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OlxParser {
    public List<String> getAllPostsUrls(String link) {
        Document doc;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc.body().select("*[data-cy='l-card']").select("a").stream()
                .map(a -> a.attr("href"))
                .collect(Collectors.toList());
    }
}
