package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.ItemRequest;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private long id;
    @Column(name = "name", nullable = false)
    @NotBlank(groups = {Create.class}, message = "Имя не может быть пустым")
    private String name;
    @Column(name = "description", nullable = false)
    @NotBlank(groups = {Create.class}, message = "Описание не может быть пустым")
    private String description;
    @Column(name = "owner_id", nullable = false)
    @NotBlank(groups = {Create.class}, message = "Идентификатор владельца не может быть пустым")
    private long ownerId;
    @Column(name = "available", nullable = false)
    @NotNull(groups = {Create.class}, message = "Статус не может быть null")
    private Boolean available;

    @OneToMany(mappedBy = "item", cascade = CascadeType.PERSIST)
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_request_id")
//    @Column(name = "request_id")
    private ItemRequest itemRequest;
}
