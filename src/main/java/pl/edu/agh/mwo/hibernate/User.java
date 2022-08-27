package pl.edu.agh.mwo.hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "users")
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @Column
        private String userName;

        @Column
        private String joinDate;

        @OneToMany(mappedBy="user",cascade = CascadeType.ALL)
//        @JoinColumn(name = "user_id")
        public Set<Album> albums = new HashSet<>();
        //public List<Album> albums = new ArrayList<>();

//        @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//        @JoinTable(
//            name = "users_photos", //tabela pośrednia
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "photo_id")
//        )

        @ManyToMany(mappedBy = "likers", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        public Set<Photo> likes = new HashSet<>();

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getJoinDate() {
            return joinDate;
        }

        public void setJoinDate(String joinDate) {
            this.joinDate = joinDate;
        }


        public Set<Album> getAlbums() {   //List
            return albums;
        }

        public void addAlbum(Album album) {
            albums.add(album);
            album.setUser(this); //z tutoriala Udemy, bez tego user_id będzie NULL
        }

        public void removeAlbum(Album album) {
            albums.remove(album);
        }


        public Set<Photo> getLikes() {
            return likes;
        }

        public void addLike(Photo photo) {
            likes.add(photo);
        }

        public void removeLike(Photo photo) {
            likes.remove(photo);
        }

        @Override
        public String toString() {
            return "User: " + userName + " (" + joinDate + ")";
        }


}
