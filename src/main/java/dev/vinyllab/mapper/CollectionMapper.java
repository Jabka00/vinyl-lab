package dev.vinyllab.mapper;

import dev.vinyllab.dto.CollectionRow;
import dev.vinyllab.entity.Album;
import dev.vinyllab.entity.CollectionItem;
import dev.vinyllab.entity.Genre;
import dev.vinyllab.form.CollectionForm;
import dev.vinyllab.util.Text;
import java.text.Collator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class CollectionMapper {

  private static final Collator COLLATOR = Collator.getInstance(Locale.forLanguageTag("uk"));

  public void apply(CollectionForm form, CollectionItem item, Album album) {
    item.setAlbum(album);
    item.setCondition(form.getCondition());
    item.setAcquiredOn(form.getAcquiredOn());
    item.setNotes(Text.blankToNull(form.getNotes()));
  }

  public CollectionForm toForm(CollectionItem item) {
    CollectionForm form = new CollectionForm();
    form.setAlbumId(item.getAlbum().getId());
    form.setCondition(item.getCondition());
    form.setAcquiredOn(item.getAcquiredOn());
    form.setNotes(item.getNotes());
    return form;
  }

  public CollectionRow toRow(CollectionItem item) {
    Album album = item.getAlbum();
    List<String> genres = album.getGenres().stream()
        .map(Genre::getName)
        .sorted(COLLATOR)
        .toList();
    return new CollectionRow(
        item.getId(),
        album.getId(),
        album.getTitle(),
        album.getArtist().getName(),
        item.getCondition().display(),
        item.getAcquiredOn(),
        item.getNotes(),
        genres
    );
  }
}
