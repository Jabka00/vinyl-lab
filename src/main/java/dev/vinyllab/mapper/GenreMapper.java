package dev.vinyllab.mapper;

import dev.vinyllab.dto.GenreCard;
import dev.vinyllab.entity.Genre;
import dev.vinyllab.form.GenreForm;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

  public void apply(GenreForm form, Genre genre) {
    genre.setName(form.getName().trim());
  }

  public GenreForm toForm(Genre genre) {
    GenreForm form = new GenreForm();
    form.setName(genre.getName());
    return form;
  }

  public GenreCard toCard(Genre genre, long albumCount) {
    return new GenreCard(genre.getId(), genre.getName(), albumCount);
  }
}
