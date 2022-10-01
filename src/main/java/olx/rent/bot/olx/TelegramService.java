package olx.rent.bot.olx;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TelegramService {
    private final TelegramBot bot;
    private final ChatSettingsDao chatSettingsDao;
    private final OlxPostService olxPostService;
    @Value("${bot.token}")
    private String botToken = "5515626331:AAHteqxCL5wn_Lb1kyqDCdhMF4VSXJlCZTI";


    public TelegramService(ChatSettingsDao chatSettingsDao, OlxPostService olxPostService) {
        this.chatSettingsDao = chatSettingsDao;
        this.olxPostService = olxPostService;
        this.bot = new TelegramBot(botToken);
        bot.setUpdatesListener(
                updates -> {
                    log.info("my updates {}", updates.toString());
                    handleUpdates(updates);
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }, e -> {
                    if (e.response() != null) {
                        // god bad response from telegram
                        e.response().errorCode();
                        e.response().description();
                    } else {
                        // probably network error
                        e.printStackTrace();
                    }
                });
    }

    public void sendNewPostsToChat(Long chatId, List<String> postsUrl) {
        postsUrl.forEach(
                url -> bot.execute(new SendMessage(chatId, url))
        );
    }

    private void handleUpdates(List<Update> updates) {
        log.info("received {} updates", updates);
        var newChats = updates.stream()
                .map(update -> new ChatSettings(update.message().chat().id(), update.message().text()))
                .toList();

        chatSettingsDao.saveAll(newChats);

        var allChats = chatSettingsDao.findAll();
        for (var chat : allChats) {
            var newPosts = olxPostService.getNewPostsForChat(chat);
            sendNewPostsToChat(chat.getChatId(), newPosts);
        }
    }
}
