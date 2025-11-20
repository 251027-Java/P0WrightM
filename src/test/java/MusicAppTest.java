import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MusicAppTest {

    @Mock
    private IRepository repo;
    @Mock
    private ISongSearcher searcher;
    @Mock
    private IEmbedder embedder;
    //MusicSearch musicSearch;

    @InjectMocks
    private MusicApp musicApp;

    @Test
    void testInsertArtistSuccess() {

        when(repo.getArtist("TestArtist")).thenReturn(null);

        when(repo.createArtist(any())).thenReturn(true);

        Artist newArtist = musicApp.insertArtistRepo("TestArtist");

        assertEquals("TestArtist", newArtist.getName());

        verify(repo, times(1)).createArtist(newArtist);
    }

    @Test
    void testInsertArtistExists() {
        Artist testArtist = new Artist("TestArtist");

        when(repo.getArtist("TestArtist")).thenReturn(testArtist);

        Artist newArtist = musicApp.insertArtistRepo("TestArtist");

        assertEquals("TestArtist", newArtist.getName());

        verify(repo, never()).createArtist(any());
    }

    @Test
    void testInsertArtistRepoFailure() {
        when(repo.getArtist("TestArtist")).thenReturn(null);

        when(repo.createArtist(any())).thenReturn(false);

        Artist newArtist = musicApp.insertArtistRepo("TestArtist");

        assertNull(newArtist);

        verify(repo, times(1)).createArtist(any());
    }

    @Test
    void testDeleteArtistSuccess() {

        Artist testArtist = new Artist("TestArtist");

        when(repo.getArtist("TestArtist")).thenReturn(testArtist);
        when(repo.deleteArtist("TestArtist")).thenReturn(true);

        boolean didDelete = musicApp.deleteArtistRepo("TestArtist");

        assertTrue(didDelete);

        verify(repo, times(1)).deleteArtist("TestArtist");
    }

    @Test
    void testDeleteArtistFail() {

        when(repo.getArtist("TestArtist")).thenReturn(null);

        boolean didDelete = musicApp.deleteArtistRepo("TestArtist");

        assertFalse(didDelete);

        verify(repo, never()).deleteArtist(any());
    }

    @Test
    void testDeleteArtistRepoFailure() {

        Artist testArtist = new Artist("TestArtist");
        when(repo.getArtist("TestArtist")).thenReturn(testArtist);

        when(repo.deleteArtist(any())).thenReturn(false);

        boolean didDelete = musicApp.deleteArtistRepo("TestArtist");

        assertFalse(didDelete);

        verify(repo, times(1)).deleteArtist("TestArtist");
    }

    @Test
    void testInsertAlbumSuccess() {
        when(repo.getAlbum("TestAlbum", 2000)).thenReturn(null);

        when(repo.createAlbum(any())).thenReturn(true);

        String[] artists = "TestArtist".split(" ");

        Album newAlbum = musicApp.insertAlbumRepo(artists, "TestAlbum", 2000);

        assertEquals("TestAlbum", newAlbum.getTitle());
        assertEquals(artists, newAlbum.getArtists());
        assertEquals(2000, newAlbum.getReleaseYear());

        verify(repo, times(1)).createAlbum(newAlbum);
    }

    @Test
    void testInsertAlbumExists() {
        String[] artists = "TestArtist".split(" ");
        Album testAlbum = new Album(artists, "TestAlbum", 2000);

        when(repo.getAlbum("TestAlbum", 2000)).thenReturn(testAlbum);

        Album newAlbum = musicApp.insertAlbumRepo(artists, "TestAlbum", 2000);

        assertEquals("TestAlbum", newAlbum.getTitle());
        assertEquals(artists, newAlbum.getArtists());
        assertEquals(2000, newAlbum.getReleaseYear());

        verify(repo, never()).createAlbum(any());
    }

    @Test
    void testInsertAlbumRepoFailure() {
        when(repo.getAlbum("TestAlbum", 2000)).thenReturn(null);

        when(repo.createAlbum(any())).thenReturn(false);

        String[] artists = "TestArtist".split(" ");

        Album newAlbum = musicApp.insertAlbumRepo(artists, "TestAlbum", 2000);

        assertNull(newAlbum);

        verify(repo, times(1)).createAlbum(any());
    }

    @Test
    void testDeleteAlbumSuccess() {
        String[] artists = "TestArtist".split(" ");
        Album testAlbum = new Album(artists, "TestAlbum", 2000);

        when(repo.getAlbum("TestAlbum", 2000)).thenReturn(testAlbum);
        when(repo.deleteAlbum("TestAlbum", 2000)).thenReturn(true);

        boolean didDelete = musicApp.deleteAlbumRepo("TestAlbum", 2000);

        assertTrue(didDelete);

        verify(repo, times(1)).deleteAlbum("TestAlbum", 2000);
    }

    @Test
    void testDeleteAlbumFail() {
        when(repo.getAlbum("TestAlbum", 2000)).thenReturn(null);

        boolean didDelete = musicApp.deleteAlbumRepo("TestAlbum", 2000);

        assertFalse(didDelete);

        verify(repo, never()).deleteAlbum("TestAlbum", 2000);
    }

    @Test
    void testDeleteAlbumRepoFailure() {
        String[] artists = "TestArtist".split(" ");
        Album testAlbum = new Album(artists, "TestAlbum", 2000);

        when(repo.getAlbum("TestAlbum", 2000)).thenReturn(testAlbum);
        when(repo.deleteAlbum("TestAlbum", 2000)).thenReturn(false);

        boolean didDelete = musicApp.deleteAlbumRepo("TestAlbum", 2000);

        assertFalse(didDelete);

        verify(repo, times(1)).deleteAlbum("TestAlbum", 2000);
    }

    @Test
    void testInsertSongSuccess() {
        when(repo.getSong("TestSong", "TestAlbum", 2000)).thenReturn(null);

        when(repo.createSong(any())).thenReturn(true);

        String[] artists = "TestArtist".split(" ");
        Album testAlbum = new Album(artists, "TestAlbum", 2000);

        Song newSong = musicApp.insertSongRepo(artists, testAlbum, "TestSong",400, "SampleLyrics");

        assertEquals("TestSong", newSong.getTitle());
        assertEquals(artists, newSong.getArtists());
        assertEquals("TestAlbum", newSong.getAlbum());
        assertEquals(400, newSong.getLength());
        assertEquals("SampleLyrics", newSong.getLyrics());

        verify(repo, times(1)).createSong(newSong);
    }

    @Test
    void testInsertSongExists() {
        String[] artists = "TestArtist".split(" ");
        Album testAlbum = new Album(artists, "TestAlbum", 2000);
        Song testSong = new Song(artists, "TestAlbum", 2000, "TestSong",400, "SampleLyrics");

        when(repo.getSong("TestSong", "TestAlbum", 2000)).thenReturn(testSong);

        Song newSong = musicApp.insertSongRepo(artists, testAlbum, "TestSong",400, "SampleLyrics");

        assertEquals("TestSong", newSong.getTitle());
        assertEquals(artists, newSong.getArtists());
        assertEquals("TestAlbum", newSong.getAlbum());
        assertEquals(400, newSong.getLength());
        assertEquals("SampleLyrics", newSong.getLyrics());

        verify(repo, never()).createSong(any());
    }

    @Test
    void testInsertSongRepoFailure() {
        when(repo.getSong("TestSong", "TestAlbum", 2000)).thenReturn(null);

        when(repo.createSong(any())).thenReturn(false);

        String[] artists = "TestArtist".split(" ");
        Album testAlbum = new Album(artists, "TestAlbum", 2000);

        Song newSong = musicApp.insertSongRepo(artists, testAlbum, "TestSong",400, "SampleLyrics");

        assertNull(newSong);

        verify(repo, times(1)).createSong(any());
    }

    @Test
    void testDeleteSongSuccess() {
        String[] artists = "TestArtist".split(" ");
        Song testSong = new Song(artists, "TestAlbum", 2000, "TestSong", 300, "SampleLyrics");

        when(repo.getSong("TestSong", "TestAlbum", 2000)).thenReturn(testSong);
        when(repo.deleteSong("TestSong", "TestAlbum", 2000)).thenReturn(true);

        boolean didDelete = musicApp.deleteSongRepo("TestSong", "TestAlbum", 2000);

        assertTrue(didDelete);

        verify(repo, times(1)).deleteSong("TestSong", "TestAlbum", 2000);
    }

    @Test
    void testDeleteSongFail() {
        when(repo.getSong("TestSong", "TestAlbum", 2000)).thenReturn(null);

        boolean didDelete = musicApp.deleteSongRepo("TestSong", "TestAlbum", 2000);

        assertFalse(didDelete);

        verify(repo, never()).deleteSong("TestSong", "TestAlbum", 2000);
    }

    @Test
    void testDeleteSongRepoFailure() {
        String[] artists = "TestArtist".split(" ");
        Song testSong = new Song(artists, "TestAlbum", 2000, "TestSong", 300, "SampleLyrics");

        when(repo.getSong("TestSong", "TestAlbum", 2000)).thenReturn(testSong);
        when(repo.deleteSong("TestSong", "TestAlbum", 2000)).thenReturn(false);

        boolean didDelete = musicApp.deleteSongRepo("TestSong", "TestAlbum", 2000);

        assertFalse(didDelete);

        verify(repo, times(1)).deleteSong("TestSong", "TestAlbum", 2000);
    }
}
