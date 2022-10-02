package olx.rent.bot.olx;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class MessageProvider {

    private final static List<String> incorrectLinkMessage = List.of(
            "Ти не знаєш як лінка виглядає? Введи нормальну",
            "Як я за цим тобі щось найду йопта? я олх бот, а не якась хуйня бот",
            "Клас, знову треба пояснювати людині як виглядає лінка"
    );

    public String getMessageForIncorrectLink() {
        Random rand = new Random();
        return incorrectLinkMessage.get(rand.nextInt(incorrectLinkMessage.size()));
    }
}
