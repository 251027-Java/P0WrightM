import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

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

        assertNull(newArtist);

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
    }

    @Test
    void testDeleteArtistFail() {
    }

    @Test
    void testInsertAlbumSuccess() {
    }

    @Test
    void testInsertAlbumFail() {
    }

    @Test
    void testDeleteAlbumSuccess() {
    }

    @Test
    void testDeleteAlbumFail() {
    }

    @Test
    void testInsertSongSuccess() {
    }

    @Test
    void testInsertSongFail() {
    }

    @Test
    void testDeleteSongSuccess() {
    }

    @Test
    void testDeleteSongFail() {
    }
}
