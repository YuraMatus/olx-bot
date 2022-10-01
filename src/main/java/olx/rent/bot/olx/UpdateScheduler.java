package olx.rent.bot.olx;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UpdateScheduler {

    private final TelegramService telegramService;
    private final OlxPostService olxPostService;
    private final ChatSettingsDao chatSettingsDao;

    public UpdateScheduler(TelegramService telegramService,
                           OlxPostService olxPostService,
                           ChatSettingsDao chatSettingsDao) {
        this.telegramService = telegramService;
        this.olxPostService = olxPostService;
        this.chatSettingsDao = chatSettingsDao;
    }


    @Scheduled(cron = "0 0/15 * * * *")
    public void checkUpdatesAndSendIfNewOnesAre() {
        //todo add pagination in case performance issues
        var allChats = chatSettingsDao.findAll();
        for (var chat : allChats) {
            var newPosts = olxPostService.getNewPostsForChat(chat);
            telegramService.sendNewPostsToChat(chat.getChatId(), newPosts);
        }
    }
}
