package dev.vinyllab.entity;

import dev.vinyllab.model.RecordCondition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "collection_items")
public class CollectionItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private UserAccount owner;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "album_id", nullable = false)
  private Album album;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private RecordCondition condition;

  private LocalDate acquiredOn;

  @Column(length = 1000)
  private String notes;

  @Column(nullable = false)
  private LocalDateTime addedAt;

  @PrePersist
  void prePersist() {
    if (addedAt == null) {
      addedAt = LocalDateTime.now();
    }
  }
}
