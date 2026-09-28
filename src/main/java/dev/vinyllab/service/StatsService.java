package dev.vinyllab.service;

import dev.vinyllab.dto.CollectionStats;
import dev.vinyllab.dto.DashboardCounts;
import dev.vinyllab.dto.StatSlice;
import dev.vinyllab.entity.CollectionItem;
import dev.vinyllab.entity.Genre;
import dev.vinyllab.model.RecordCondition;
import dev.vinyllab.repository.AlbumRepository;
import dev.vinyllab.repository.ArtistRepository;
import dev.vinyllab.repository.CollectionItemRepository;
import dev.vinyllab.repository.GenreRepository;
import dev.vinyllab.repository.UserRepository;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

  private final CollectionItemRepository items;
  private final UserRepository users;
  private final ArtistRepository artists;
  private final AlbumRepository albums;
  private final GenreRepository genres;

  public CollectionStats forUser(Long ownerId) {
    List<CollectionItem> owned = items.findOwned(ownerId);
    long total = owned.size();

    Map<String, Long> byGenre = new HashMap<>();
    for (CollectionItem item : owned) {
      for (Genre genre : item.getAlbum().getGenres()) {
        byGenre.merge(genre.getName(), 1L, Long::sum);
      }
    }
    long widest = byGenre.values().stream().mapToLong(Long::longValue).max().orElse(0);
    List<StatSlice> genreSlices = byGenre.entrySet().stream()
        .sorted(Comparator.comparingLong((Map.Entry<String, Long> entry) -> entry.getValue()).reversed()
            .thenComparing(Map.Entry::getKey))
        .map(entry -> new StatSlice(entry.getKey(), entry.getValue(), width(entry.getValue(), widest)))
        .toList();

    Map<RecordCondition, Long> byCondition = owned.stream()
        .collect(Collectors.groupingBy(CollectionItem::getCondition, Collectors.counting()));
    List<StatSlice> conditionSlices = Arrays.stream(RecordCondition.values())
        .map(condition -> {
          long count = byCondition.getOrDefault(condition, 0L);
          return new StatSlice(condition.display(), count, width(count, total));
        })
        .filter(slice -> slice.count() > 0)
        .toList();

    return new CollectionStats(total, genreSlices, conditionSlices);
  }

  public DashboardCounts overview() {
    return new DashboardCounts(
        users.count(),
        artists.count(),
        albums.count(),
        genres.count(),
        items.count(),
        albums.countRatings()
    );
  }

  private int width(long count, long base) {
    if (count <= 0 || base <= 0) {
      return 0;
    }
    return (int) Math.round(count * 100.0 / base);
  }
}
