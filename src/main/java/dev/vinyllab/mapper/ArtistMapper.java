package dev.vinyllab.mapper;

import dev.vinyllab.dto.AlbumView;
import dev.vinyllab.dto.ArtistView;
import dev.vinyllab.entity.Artist;
import dev.vinyllab.form.ArtistForm;
import dev.vinyllab.util.Text;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ArtistMapper {

  public void apply(ArtistForm form, Artist artist) {
    artist.setName(form.getName().trim());
    artist.setCountry(form.getCountry().trim());
    artist.setBiography(Text.blankToNull(form.getBiography()));
  }

  public ArtistForm toForm(Artist artist) {
    ArtistForm form = new ArtistForm();
    form.setName(artist.getName());
    form.setCountry(artist.getCountry());
    form.setBiography(artist.getBiography());
    return form;
  }

  public ArtistView toView(Artist artist, long albumCount, List<AlbumView> albums) {
    return new ArtistView(
        artist.getId(),
        artist.getName(),
        artist.getCountry(),
        artist.getBiography(),
        albumCount,
        albums
    );
  }
}
