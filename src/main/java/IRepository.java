public interface IRepository {
    public void createArtist(Artist artist);
    //Other CRUD for Artist

    public void createAlbum(Album album);
    //Other CRUD for Album

    public void createSong(Song song); //repo manages internal ids, abstract from caller
    //Other CRUD for Song
}
