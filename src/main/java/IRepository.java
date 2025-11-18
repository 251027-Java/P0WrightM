public interface IRepository {
    public boolean createArtist(Artist artist);
    //Other CRUD for Artist

    public boolean createAlbum(Album album);
    //Other CRUD for Album

    public boolean createSong(Song song); //repo manages internal ids, abstract from caller
    //Other CRUD for Song
}
