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
    }

    @Test
    void testInsertArtistFail() {
    }

    @Test
    void testInsertAlbumSuccess() {
    }

    @Test
    void testInsertAlbumFail() {
    }

    @Test
    void testInsertSongSuccess() {
    }

    @Test
    void testInsertSongFail() {
    }
}
