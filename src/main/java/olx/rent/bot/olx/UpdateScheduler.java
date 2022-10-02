package olx.rent.bot.olx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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
        log.info("planned posts updating started");
        chatSettingsDao.findAll()
                .forEach(chatSettings ->
                        telegramService.sendNewPostsToChat(chatSettings.getChatId())
                );
    }
}
