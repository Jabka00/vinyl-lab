package dev.vinyllab.mapper;

import dev.vinyllab.dto.AlbumView;
import dev.vinyllab.entity.Album;
import dev.vinyllab.entity.Artist;
import dev.vinyllab.entity.Genre;
import dev.vinyllab.form.AlbumForm;
import dev.vinyllab.util.Text;
import java.text.Collator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class AlbumMapper {

  private static final Collator COLLATOR = Collator.getInstance(Locale.forLanguageTag("uk"));

  public void apply(AlbumForm form, Album album, Artist artist, Set<Genre> genres) {
    album.setTitle(form.getTitle().trim());
    album.setReleaseYear(form.getReleaseYear());
    album.setLabel(Text.blankToNull(form.getLabel()));
    album.setArtist(artist);
    album.getGenres().clear();
    album.getGenres().addAll(genres);
  }

  public AlbumForm toForm(Album album) {
    AlbumForm form = new AlbumForm();
    form.setTitle(album.getTitle());
    form.setReleaseYear(album.getReleaseYear());
    form.setLabel(album.getLabel());
    form.setArtistId(album.getArtist().getId());
    Set<Long> genreIds = new LinkedHashSet<>();
    album.getGenres().forEach(genre -> genreIds.add(genre.getId()));
    form.setGenreIds(genreIds);
    return form;
  }

  public AlbumView toView(Album album, double averageRating, long ratingCount, Integer myScore, long copies) {
    return new AlbumView(
        album.getId(),
        album.getTitle(),
        album.getArtist().getName(),
        album.getArtist().getId(),
        album.getReleaseYear(),
        album.getLabel(),
        genreNames(album),
        averageRating,
        ratingCount,
        myScore,
        copies
    );
  }

  private List<String> genreNames(Album album) {
    return album.getGenres().stream()
        .map(Genre::getName)
        .sorted(COLLATOR)
        .toList();
  }
}
