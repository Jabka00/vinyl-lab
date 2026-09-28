package dev.vinyllab.service;

import dev.vinyllab.dto.CollectionRow;
import dev.vinyllab.entity.Album;
import dev.vinyllab.entity.CollectionItem;
import dev.vinyllab.entity.UserAccount;
import dev.vinyllab.exception.NotFoundException;
import dev.vinyllab.form.CollectionForm;
import dev.vinyllab.mapper.CollectionMapper;
import dev.vinyllab.repository.AlbumRepository;
import dev.vinyllab.repository.CollectionItemRepository;
import dev.vinyllab.repository.UserRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectionService {

  private final CollectionItemRepository items;
  private final AlbumRepository albums;
  private final UserRepository users;
  private final CollectionMapper collectionMapper;

  public List<CollectionRow> list(Long ownerId) {
    return items.findOwned(ownerId).stream()
        .sorted(Comparator
            .comparing(CollectionItem::getAcquiredOn, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(CollectionItem::getAddedAt, Comparator.reverseOrder()))
        .map(collectionMapper::toRow)
        .toList();
  }

  public CollectionForm form(Long id, Long ownerId) {
    return collectionMapper.toForm(owned(id, ownerId));
  }

  @Transactional
  public void create(Long ownerId, CollectionForm form) {
    CollectionItem item = new CollectionItem();
    item.setOwner(owner(ownerId));
    collectionMapper.apply(form, item, album(form.getAlbumId()));
    items.save(item);
  }

  @Transactional
  public void update(Long id, Long ownerId, CollectionForm form) {
    CollectionItem item = owned(id, ownerId);
    collectionMapper.apply(form, item, album(form.getAlbumId()));
  }

  @Transactional
  public void delete(Long id, Long ownerId) {
    items.delete(owned(id, ownerId));
  }

  private CollectionItem owned(Long id, Long ownerId) {
    List<CollectionItem> found = items.findDetailedById(id);
    if (found.isEmpty()) {
      throw new NotFoundException("Запис колекції не знайдено");
    }
    CollectionItem item = found.getFirst();
    if (!item.getOwner().getId().equals(ownerId)) {
      throw new NotFoundException("Запис колекції не знайдено");
    }
    return item;
  }

  private Album album(Long id) {
    return albums.findById(id).orElseThrow(() -> new NotFoundException("Альбом не знайдено"));
  }

  private UserAccount owner(Long id) {
    return users.findById(id).orElseThrow(() -> new NotFoundException("Користувача не знайдено"));
  }
}
