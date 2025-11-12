public interface IRepository {
    public int createArtist(Artist artist);
    //Other CRUD for Artist

    public int createAlbum(Album album);
    //Other CRUD for Album

    public int createSong(Song song); //returns id of inserted song
    //Other CRUD for Song
}
