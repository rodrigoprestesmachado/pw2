/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 *
*/
package dev.pw2.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Setter @Getter
public class User extends PanacheEntity {

    private String name;

    private String hash;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    // name = nome da coluna que irá armazenar a chave estrangeira
    // na tabela Message (solução a partir da JPA 2)
    @JoinColumn(name = "user_id")
    private List<Message> messages;

    @ManyToMany(cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Channel> channels;

    public User() {
        this.messages = new ArrayList<>();
        this.channels = new ArrayList<>();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public void addChannel(Channel channel) {
        this.channels.add(channel);
    }

}
