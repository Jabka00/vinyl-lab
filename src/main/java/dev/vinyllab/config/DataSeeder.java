package dev.vinyllab.config;

import dev.vinyllab.entity.Album;
import dev.vinyllab.entity.Artist;
import dev.vinyllab.entity.CollectionItem;
import dev.vinyllab.entity.Genre;
import dev.vinyllab.entity.UserAccount;
import dev.vinyllab.model.RecordCondition;
import dev.vinyllab.model.Role;
import dev.vinyllab.repository.AlbumRepository;
import dev.vinyllab.repository.ArtistRepository;
import dev.vinyllab.repository.CollectionItemRepository;
import dev.vinyllab.repository.GenreRepository;
import dev.vinyllab.repository.UserRepository;
import dev.vinyllab.service.RatingService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

  private final GenreRepository genres;
  private final ArtistRepository artists;
  private final AlbumRepository albums;
  private final UserRepository users;
  private final CollectionItemRepository items;
  private final RatingService ratings;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (users.count() > 0) {
      return;
    }

    Map<String, Genre> genreByName = new HashMap<>();
    for (String name : new String[] {"Рок", "Джаз", "Фолк", "Електроніка", "Соул", "Поп", "Альтернатива"}) {
      Genre genre = new Genre();
      genre.setName(name);
      genreByName.put(name, genres.save(genre));
    }

    Map<String, Artist> artistByName = new HashMap<>();
    artistByName.put("Океан Ельзи", artist("Океан Ельзи", "Україна",
        "Один із найвідоміших українських рок-гуртів. На вінілі їхні альбоми звучать щільно і зібрано."));
    artistByName.put("ONUKA", artist("ONUKA", "Україна",
        "Електроніка з фольк-інструментами. Платівки Нати Жижченко добре лягають у сучасну полицю."));
    artistByName.put("DakhaBrakha", artist("DakhaBrakha", "Україна",
        "Квартет, що збирає український фольклор у потужний сценічний звук."));
    artistByName.put("The Beatles", artist("The Beatles", "Велика Британія",
        "Класика, з якої часто починається вінілова полиця."));
    artistByName.put("Pink Floyd", artist("Pink Floyd", "Велика Британія",
        "Концептуальні альбоми, які майже створені для довгого прослуховування на платівці."));
    artistByName.put("Miles Davis", artist("Miles Davis", "США",
        "Джазовий трубач, чий Kind of Blue лишається одним із найочевидніших вінілових виборів."));
    artistByName.put("Nina Simone", artist("Nina Simone", "США",
        "Голос, піаніно і гострий соул. Оригінали й перевидання однаково тримають увагу."));
    artistByName.put("Radiohead", artist("Radiohead", "Велика Британія",
        "Альтернативний рок, який на вінілі розкриває тихі деталі аранжування."));
    artistByName.put("Daft Punk", artist("Daft Punk", "Франція",
        "Французький дует. Random Access Memories — тепла, майже аналогова електронна платівка."));
    artistByName.put("Fleetwood Mac", artist("Fleetwood Mac", "США",
        "Rumours — альбом, який колекціонери шукають і в ідеальному, і в «живому» стані."));
    artistByName.put("Massive Attack", artist("Massive Attack", "Велика Британія",
        "Бристольський гурт. Mezzanine важкий, темний і дуже «вініловий» за густиною."));

    Map<String, Album> albumByTitle = new HashMap<>();
    albumByTitle.put("Модель", album(artistByName, genreByName, "Океан Ельзи", "Модель", 2001, "Lavina", "Рок"));
    albumByTitle.put("Земля", album(artistByName, genreByName, "Океан Ельзи", "Земля", 2013, "Lavina", "Рок"));
    albumByTitle.put("ONUKA", album(artistByName, genreByName, "ONUKA", "ONUKA", 2014, "Vidlik", "Електроніка", "Фолк"));
    albumByTitle.put("Light", album(artistByName, genreByName, "DakhaBrakha", "Light", 2010, "Self-released", "Фолк"));
    albumByTitle.put("Abbey Road", album(artistByName, genreByName, "The Beatles", "Abbey Road", 1969, "Apple", "Рок", "Поп"));
    albumByTitle.put("The Dark Side of the Moon", album(artistByName, genreByName, "Pink Floyd", "The Dark Side of the Moon", 1973, "Harvest", "Рок"));
    albumByTitle.put("Kind of Blue", album(artistByName, genreByName, "Miles Davis", "Kind of Blue", 1959, "Columbia", "Джаз"));
    albumByTitle.put("I Put a Spell on You", album(artistByName, genreByName, "Nina Simone", "I Put a Spell on You", 1965, "Philips", "Соул", "Джаз"));
    albumByTitle.put("In Rainbows", album(artistByName, genreByName, "Radiohead", "In Rainbows", 2007, "XL", "Альтернатива"));
    albumByTitle.put("Random Access Memories", album(artistByName, genreByName, "Daft Punk", "Random Access Memories", 2013, "Columbia", "Електроніка", "Поп"));
    albumByTitle.put("Rumours", album(artistByName, genreByName, "Fleetwood Mac", "Rumours", 1977, "Warner", "Рок", "Поп"));
    albumByTitle.put("Mezzanine", album(artistByName, genreByName, "Massive Attack", "Mezzanine", 1998, "Virgin", "Електроніка", "Альтернатива"));

    UserAccount admin = user("admin", "admin@vinyl.lab", "Admin12345", Role.ADMIN);
    UserAccount ira = user("ira", "ira@vinyl.lab", "Ira12345", Role.USER);

    rate(ira, albumByTitle.get("Земля"), 5);
    rate(ira, albumByTitle.get("Light"), 5);
    rate(ira, albumByTitle.get("Abbey Road"), 4);
    rate(ira, albumByTitle.get("Kind of Blue"), 5);
    rate(ira, albumByTitle.get("The Dark Side of the Moon"), 5);
    rate(ira, albumByTitle.get("In Rainbows"), 4);
    rate(ira, albumByTitle.get("Mezzanine"), 4);
    rate(admin, albumByTitle.get("Kind of Blue"), 5);
    rate(admin, albumByTitle.get("Abbey Road"), 5);
    rate(admin, albumByTitle.get("Random Access Memories"), 4);
    rate(admin, albumByTitle.get("Rumours"), 3);
    rate(admin, albumByTitle.get("ONUKA"), 5);
    rate(admin, albumByTitle.get("The Dark Side of the Moon"), 4);

    copy(ira, albumByTitle.get("Земля"), RecordCondition.NEAR_MINT, LocalDate.of(2022, 4, 11),
        "Купила на вінтажному маркеті, конверт чистий.", LocalDateTime.of(2022, 4, 11, 18, 0));
    copy(ira, albumByTitle.get("Light"), RecordCondition.VERY_GOOD_PLUS, LocalDate.of(2021, 9, 2),
        null, LocalDateTime.of(2021, 9, 2, 12, 30));
    copy(ira, albumByTitle.get("Abbey Road"), RecordCondition.VERY_GOOD, LocalDate.of(2019, 12, 20),
        "Перша платівка в колекції.", LocalDateTime.of(2019, 12, 20, 15, 0));
    copy(ira, albumByTitle.get("Kind of Blue"), RecordCondition.MINT, LocalDate.of(2024, 1, 15),
        "Свіже перевидання.", LocalDateTime.of(2024, 1, 15, 11, 0));
    copy(ira, albumByTitle.get("Mezzanine"), RecordCondition.GOOD, LocalDate.of(2020, 6, 1),
        "Конверт із потертостями на кутах.", LocalDateTime.of(2020, 6, 1, 19, 40));
  }

  private Artist artist(String name, String country, String biography) {
    Artist artist = new Artist();
    artist.setName(name);
    artist.setCountry(country);
    artist.setBiography(biography);
    return artists.save(artist);
  }

  private Album album(
      Map<String, Artist> artistByName,
      Map<String, Genre> genreByName,
      String artistName,
      String title,
      int year,
      String label,
      String... genreNames
  ) {
    Album album = new Album();
    album.setArtist(artistByName.get(artistName));
    album.setTitle(title);
    album.setReleaseYear(year);
    album.setLabel(label);
    for (String genreName : genreNames) {
      album.getGenres().add(genreByName.get(genreName));
    }
    return albums.save(album);
  }

  private UserAccount user(String username, String email, String password, Role role) {
    UserAccount user = new UserAccount();
    user.setUsername(username);
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(password));
    user.setRole(role);
    return users.save(user);
  }

  private void rate(UserAccount user, Album album, int score) {
    ratings.rate(user.getId(), album.getId(), score);
  }

  private void copy(
      UserAccount owner,
      Album album,
      RecordCondition condition,
      LocalDate acquiredOn,
      String notes,
      LocalDateTime addedAt
  ) {
    CollectionItem item = new CollectionItem();
    item.setOwner(owner);
    item.setAlbum(album);
    item.setCondition(condition);
    item.setAcquiredOn(acquiredOn);
    item.setNotes(notes);
    item.setAddedAt(addedAt);
    items.save(item);
  }
}
