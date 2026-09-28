package dev.vinyllab.repository;

import dev.vinyllab.entity.CollectionItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollectionItemRepository extends JpaRepository<CollectionItem, Long> {

  @Query("""
      select distinct c from CollectionItem c
      join fetch c.owner
      join fetch c.album a
      join fetch a.artist
      left join fetch a.genres
      where c.owner.id = :ownerId
      """)
  List<CollectionItem> findOwned(@Param("ownerId") Long ownerId);

  @Query("""
      select distinct c from CollectionItem c
      join fetch c.owner
      join fetch c.album a
      join fetch a.artist
      left join fetch a.genres
      where c.id = :id
      """)
  List<CollectionItem> findDetailedById(@Param("id") Long id);

  long countByOwnerIdAndAlbumId(Long ownerId, Long albumId);

  boolean existsByAlbumId(Long albumId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("delete from CollectionItem c where c.owner.id = :ownerId")
  void deleteForOwner(@Param("ownerId") Long ownerId);
}
