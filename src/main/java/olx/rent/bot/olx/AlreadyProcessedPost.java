package olx.rent.bot.olx;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "processed_post")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AlreadyProcessedPost {
    @Id
    private String link;

    private Long chatId;
}
