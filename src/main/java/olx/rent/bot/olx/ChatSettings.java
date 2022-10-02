package olx.rent.bot.olx;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "chat_settings")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatSettings {

    @Id
    private Long chatId;

    @Column(columnDefinition="TEXT")
    private String linkToSearch;

    @Column
    private String personName;
}
