package cz.uhk.ppro.album;

import cz.uhk.ppro.album.category.Category;
import cz.uhk.ppro.user.User;
import cz.uhk.ppro.user.UserService;
import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserService userService;

    private AlbumService albumService;
    @Captor
    ArgumentCaptor<Album> albumArgumentCaptor;

    @Captor
    ArgumentCaptor<Long> idArgumentCaptor;

    @BeforeEach
    void setUp() {
        albumService = new AlbumService(albumRepository, userService);
    }

    @Test
    void saveAlbum() {

        Album album = new Album();
        album.setId(1L);
        album.setName("Test album");
        album.setScale("1:24");
        album.setAuthor("Petr Špinler");
        album.setPublisher("PKAA");

        albumService.saveAlbum(album);

        Mockito.verify(albumRepository, Mockito.times(1)).save(albumArgumentCaptor.capture());

        Assertions.assertThat(albumArgumentCaptor.getValue().getId()).isEqualTo(1L);
        Assertions.assertThat(albumArgumentCaptor.getValue().getName()).isEqualTo("Test album");
    }

    @Test
    void getAllActiveAlbums() {
        Album album1 = new Album();
        album1.setId(1L);
        album1.setName("Test album 1");
        Album album2 = new Album();
        album2.setId(2L);
        album2.setName("Test album 2");

        List<Album> albumList = new ArrayList<>();
        albumList.add(album1);
        albumList.add(album2);

        Mockito.when(albumRepository.findAlbumsByOrderByName()).thenReturn(albumList);
        List<Album> albumList1 = albumService.getAllActiveAlbums();

        Assertions.assertThat(albumList1.size()).isEqualTo(2);

        Assertions.assertThat(albumList1.get(0).getId()).isEqualTo(1L);
        Assertions.assertThat(albumList1.get(0).getName()).isEqualTo("Test album 1");

        Assertions.assertThat(albumList1.get(1).getId()).isEqualTo(2L);
        Assertions.assertThat(albumList1.get(1).getName()).isEqualTo("Test album 2");
    }

    @Test
    void getAllLogedInUserAlbums() {

        User loggedUser = new User();
        loggedUser.setId(1L);
        loggedUser.setUsername("Test user");
        Album album1 = new Album();
        album1.setId(1L);
        album1.setName("Test album 1");
        Album album2 = new Album();
        album2.setId(2L);
        album2.setName("Test album 2");

        List<Album> albumList = new ArrayList<>();
        albumList.add(album1);
        albumList.add(album2);

        loggedUser.setAlbums(albumList);

        List<Album> albumList1 = loggedUser.getAlbums();

        Mockito.when(albumRepository.findAlbumsByUser_IdOrderByName(1L)).thenReturn(albumList1);

        List<Album> albumList2 = albumService.getAlbumsByUserId(1L);

        Assertions.assertThat(albumList2.get(0).getId()).isEqualTo(1L);
        Assertions.assertThat(albumList2.get(0).getName()).isEqualTo("Test album 1");

        Assertions.assertThat(albumList2.get(1).getId()).isEqualTo(2L);
        Assertions.assertThat(albumList2.get(1).getName()).isEqualTo("Test album 2");

    }

    @Test
    void getAlbumById() {

        Album album = new Album();
        album.setId(1L);
        album.setName("Test album");

        Mockito.when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        Album album1 = albumService.getAlbumById(1L).get();

        Assertions.assertThat(album1.getId()).isEqualTo(1L);
        Assertions.assertThat(album1.getName()).isEqualTo("Test album");
    }

    @Test
    void deleteAlbumById() {
        albumService.deleteAlbumById(1L);
        Mockito.verify(albumRepository, Mockito.times(1)).deleteAlbumById(idArgumentCaptor.capture());
        Assertions.assertThat(idArgumentCaptor.getValue()).isEqualTo(1L);
    }

    @Test
    void getByKeyword() {
        Album album1 = new Album();
        album1.setId(1L);
        album1.setName("Test album");

        Album album2 = new Album();
        album2.setId(2L);
        album2.setName("Test album 1");

        List<Album> albumList = new ArrayList<>();
        albumList.add(album1);
        albumList.add(album2);

        Mockito.when(albumRepository.findByKeyword("Test album")).thenReturn(albumList);
        List<Album> albumList1 = albumService.getByKeyword("Test album");

        Assertions.assertThat(albumList1.get(0).getId()).isEqualTo(1L);
        Assertions.assertThat(albumList1.get(0).getName()).isEqualTo("Test album");

        Assertions.assertThat(albumList1.get(1).getId()).isEqualTo(2L);
        Assertions.assertThat(albumList1.get(1).getName()).isEqualTo("Test album 1");

    }

    @Test
    void getByCategory() {
        Album album1 = new Album();
        album1.setId(1L);
        album1.setName("Test album");

        Album album2 = new Album();
        album2.setId(2L);
        album2.setName("Test album 1");

        List<Album> albumList = new ArrayList<>();
        albumList.add(album1);
        albumList.add(album2);

        Category category = new Category();
        category.setAlbums(albumList);
        category.setName("Test");

        album1.setCategory(category);
        album2.setCategory(category);

        Mockito.when(albumRepository.findByCategory(category)).thenReturn(category.getAlbums());
        List<Album> albumList1 = albumService.getByCategory(category);

        Assertions.assertThat(albumList1.get(0).getId()).isEqualTo(1L);
        Assertions.assertThat(albumList1.get(0).getName()).isEqualTo("Test album");

        Assertions.assertThat(albumList1.get(1).getId()).isEqualTo(2L);
        Assertions.assertThat(albumList1.get(1).getName()).isEqualTo("Test album 1");

    }
}