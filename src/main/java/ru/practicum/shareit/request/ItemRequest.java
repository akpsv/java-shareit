package ru.practicum.shareit.request;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Ещё одна сущность, которая вам понадобится, — запрос вещи ItemRequest.
 * Пользователь создаёт запрос, если нужная ему вещь не найдена при поиске.
 * В запросе указывается, что именно он ищет.
 * В ответ на запрос другие пользовали могут добавить нужную вещь.
 */

@Entity
@Table(name = "item_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_request_id")
    private long id;
    @Column(name = "description")
    private String description;
    @Column(name = "requestor_id")
    private long requestor; //Ид пользователя, который создал запрос
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "created", updatable = false)
    private Date created;

    @OneToMany(mappedBy = "itemRequest", cascade = CascadeType.PERSIST)
    private Set<Item> items; //Список ответов на запрос в виде созданных вещей
}
