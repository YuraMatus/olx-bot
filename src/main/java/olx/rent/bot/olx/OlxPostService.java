package olx.rent.bot.olx;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OlxPostService {

    private final OlxParser parser;
    private final AlreadyProcessedPostDao alreadyProcessedPostDao;

    @Value("${olx.host}")
    private String olxHost = "https://www.olx.ua";

    public OlxPostService(OlxParser olxParser,
                          AlreadyProcessedPostDao alreadyProcessedPostDao) {
        this.alreadyProcessedPostDao = alreadyProcessedPostDao;
        this.parser = olxParser;
    }

    public List<String> getNewPostsForChat(ChatSettings chatSettings) {
        var allPostUrlsForChat = parser.getAllPostsUrls(chatSettings.getLinkToSearch()).stream()
                .map(this::appendHost)
                .toList();

        var normalizedLinksOfNewPosts = allPostUrlsForChat.stream()
                .filter(link -> !alreadyProcessedPostDao.checkIfPostProcessedByLink(link))
                .toList();

        markPostsAsProcessed(normalizedLinksOfNewPosts);
        return normalizedLinksOfNewPosts;
    }

    private void markPostsAsProcessed(List<String> processedPostsUrls) {
        var processedPostsDb = processedPostsUrls.stream()
                .map(AlreadyProcessedPost::new)
                .toList();
        alreadyProcessedPostDao.saveAll(processedPostsDb);
    }

    private String appendHost(String uri) {
        return olxHost + uri;
    }

}
