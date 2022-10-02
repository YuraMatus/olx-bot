package olx.rent.bot.olx;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TelegramService {
    private final TelegramBot bot;
    private final MessageProvider messageProvider;
    private final ChatSettingsDao chatSettingsDao;
    private final OlxPostService olxPostService;
    private final UrlValidator urlValidator;
    @Value("${bot.token}")
    private String botToken = "5515626331:AAHteqxCL5wn_Lb1kyqDCdhMF4VSXJlCZTI";


    public TelegramService(MessageProvider messageProvider,
                           ChatSettingsDao chatSettingsDao,
                           OlxPostService olxPostService) {
        this.messageProvider = messageProvider;
        this.chatSettingsDao = chatSettingsDao;
        this.olxPostService = olxPostService;
        this.bot = new TelegramBot(botToken);
        this.urlValidator = new UrlValidator();
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

    public void sendNewPostsToChat(Long chatId) {
        var chatSettings = chatSettingsDao.findById(chatId)
                .orElseThrow(() -> new RuntimeException("chat with id: " + chatId + "not found"));

        var newPostUrls = olxPostService.getNewPostsForChat(chatSettings);
        newPostUrls.forEach(
                url -> this.sendMessage(chatId, url)
        );
    }

    private void handleUpdates(List<Update> updates) {
        logUpdates(updates);

        replyToChatsWithIncorrectLink(updates);

        var newChats = getChatsWithValidLink(updates);

        chatSettingsDao.saveAll(newChats);

        newChats.forEach(
                chatSettings -> sendNewPostsToChat(chatSettings.getChatId())
        );
    }

    private void sendMessage(Long chatId, String text) {
        bot.execute(new SendMessage(chatId, text));
    }

    private void replyToChatsWithIncorrectLink(List<Update> updates) {
        updates.stream()
                .distinct()
                .filter(update -> !urlValidator.isValid(update.message().text()))
                .map(update -> update.message().chat().id())
                .forEach(chatId -> sendMessage(chatId, messageProvider.getMessageForIncorrectLink()));
    }

    private void logUpdates(List<Update> updates) {
        for (var update : updates) {
            log.info("received message {} from {} | chatId {}",
                    update.message().text(),
                    getPersonNameFromMessage(update.message()),
                    update.message().chat().id());
        }
    }

    private String getPersonNameFromMessage(Message message) {
        return message.chat().firstName() + " " + message.chat().lastName();
    }

    private List<ChatSettings> getChatsWithValidLink(List<Update> updates) {
        return updates.stream()
                .distinct()
                .map(Update::message)
                .filter(message -> urlValidator.isValid(message.text()))
                .map(message -> new ChatSettings(message.chat().id(), message.text(), getPersonNameFromMessage(message)))
                .toList();
    }
}
