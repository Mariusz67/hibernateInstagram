package pl.edu.agh.mwo.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.sql.*;

import static pl.edu.agh.mwo.hibernate.User.*;

public class Main {

	Session session;

	public static void main(String[] args) {
		Main main = new Main();

		System.out.println("\n ******stan początkowy**********");
		main.printUsers();

		main.addNewData();
        main.addLike();
		System.out.println("\n *****po dodaniu nowych danych******");
		main.printUsers();

		main.deleteUser();
		System.out.println("\n ***********po usunięciu userów*********");
		main.printUsers();

		main.deleteAlbum();
		System.out.println("\n ***********pousunięciu albumu**********");
		main.printUsers();

		main.deletePhoto();

		main.deletePhotoWithLikes();

//		main.removeLike();

		main.printLikesByUser();				//ok
		main.printLikesByPhoto();				//ok

		main.close();
	}

	public Main() {
		session = HibernateUtil.getSessionFactory().openSession();
	}

	public void close() {
		session.close();
		HibernateUtil.shutdown();
	}

	private void addNewData() {
		User newUser = new User();
		newUser.setUserName("Olgierd");
		newUser.setJoinDate("20210816");

		Album newAlbum = new Album();
		newAlbum.setAlbumName("zawody narciarskie");
		newAlbum.setDescription("Zakopane 2019");

		Photo newPhoto = new Photo();
		newPhoto.setPhotoName("d100");
		newPhoto.setPhotoDate("20190720");

		newUser.addAlbum(newAlbum);
		newAlbum.addPhoto(newPhoto);

		Album newAlbum2 = new Album();
		newAlbum2.setAlbumName("wakacje");
		newAlbum2.setDescription("Dębki 2018");

		Photo newPhoto2 = new Photo();
		newPhoto2.setPhotoName("d101");
		newPhoto2.setPhotoDate("20180720");

		newUser.addAlbum(newAlbum2);
		newAlbum2.addPhoto(newPhoto2);

		Transaction transaction = session.beginTransaction();
		session.save(newUser);
		transaction.commit();
	}


	private void printUsers() {
		Query<User> query = session.createQuery("from User", User.class);
		List<User> users = query.list();

		System.out.println("### Users");
		for (User user : users) {
			System.out.println(user);
			for (Album album : user.getAlbums()) {
				System.out.println(album);
				for (Photo photo : album.getPhotos()) {
					System.out.println(photo);
				}
			}
		}
	}

	private void deleteUser() {
		String hql = "from User u where u.userName='Olgierd'";
		Query<User> query = session.createQuery(hql, User.class);
		List<User> results = query.list();
		Transaction transaction = session.beginTransaction();
		for (User u : results) {
			session.delete(u);
		}
		transaction.commit();
	}


	private void deleteAlbum() {
		String hql = "from Album a where a.albumName='zawody narciarskie'";
		Query<Album> query = session.createQuery(hql, Album.class);
		List<Album> results = query.list();
		System.out.println("co jest w results");
		System.out.println(results.toString());

		Transaction transaction = session.beginTransaction();
		for (Album a : results) {
			a.getUser().removeAlbum(a);
			session.delete(a);
		}
		transaction.commit();
	}

	private void deletePhoto() {
		String hql = "from Photo p where p.id = 1";  //musi być id , nie Id
		Query<Photo> query = session.createQuery(hql, Photo.class);
		List<Photo> results = query.list();
		System.out.println("co jest w results");
		System.out.println(results.toString());

		Transaction transaction = session.beginTransaction();
		for (Photo p : results) {
			p.getAlbum().removePhoto(p);
			session.delete(p);
		}
		transaction.commit();
	}

	private void deletePhotoWithLikes() {
		String hql = "from Photo p where p.id = 2";
		Query<Photo> query = session.createQuery(hql, Photo.class);
		List<Photo> results = query.list();
		System.out.println("co jest w results");
		System.out.println(results.toString());

		Transaction transaction = session.beginTransaction();
		for (Photo p : results) {
			p.getAlbum().getUser().removeLike(p);
			p.removeLiker(p.getAlbum().getUser());
			p.getAlbum().removePhoto(p);
			session.delete(p);
		}
		transaction.commit();
	}


//	private void deletePhotoWithLikes() {
//		String hql = "from Photo p where p.photoName='b109'";
//		String sql = "delete from users_photos where photo_id = ?";
//		int index;
//
//		Query<Photo> query = session.createQuery(hql, Photo.class);
//		List<Photo> results = query.list();
//		Transaction transaction = session.beginTransaction();
//		for (Photo p : results) {
//			index = (int) p.getId();
//			System.out.println(index);
//			try {
//				Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db", "", "");
//
//				PreparedStatement statement;
//				statement = connection.prepareStatement(sql);
//
//				statement.setInt(1, index);
//
//				int affectedRecords = statement.executeUpdate();
//				System.out.println("Number of deleted records:- " + affectedRecords);
//
//				statement.close();
//				connection.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//
//			session.delete(p);
//		}
//		transaction.commit();
//	}


	private void addLike(){
//		String hql = "from Photo p where p.photoName='d100' and p.photoDate='20190720'";
		String hql = "from Photo p where p.id = 2 ";
		Query<Photo> query = session.createQuery(hql, Photo.class);
		List<Photo> results = query.list();
		System.out.println("lajkowane zdjęcie");
		System.out.println(results.toString());

//		String hql2 = "from User u where u.userName ='Olgierd' and u.joinDate='20210816'";
		String hql2 = "from User u where u.id = 1";
		Query<User> query2 = session.createQuery(hql2, User.class);
		List<User> results2 = query2.list();
		System.out.println("osoba która polubiła");
		System.out.println(results2.toString());

		User liker = results2.get(0);
		Photo likedPhoto = results.get(0);

		liker.addLike(likedPhoto);
		likedPhoto.addLiker(liker);

		Transaction transaction = session.beginTransaction();
		session.save(liker);
		session.save(likedPhoto);
		transaction.commit();
	}

	private void removeLike(){

		String hql = "from Photo p where p.id = 2 ";
		Query<Photo> query = session.createQuery(hql, Photo.class);
		List<Photo> results = query.list();
		System.out.println("zdjęcie do usunięcia polubienia");
		System.out.println(results.toString());

		String hql2 = "from User u where u.id = 1";
		Query<User> query2 = session.createQuery(hql2, User.class);
		List<User> results2 = query2.list();
		System.out.println("osoba usuwająca polubienie");
		System.out.println(results2.toString());

		User liker = results2.get(0); //pierwszy i jedyny element z listy
		Photo likedPhoto = results.get(0);

		liker.removeLike(likedPhoto);
		likedPhoto.removeLiker(liker);

		Transaction transaction = session.beginTransaction();
		session.save(liker);
		session.save(likedPhoto);
		transaction.commit();
	}

//	private void addLike() {
//		int userID = 1;
//		int photoID = 3;
//		String sql = "INSERT INTO users_photos (user_id, photo_id) VALUES (?, ?)";
//
//		try {
//			Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db", "", "");
//			PreparedStatement statement;
//			statement = connection.prepareStatement(sql);
//
//			statement.setInt(1, userID);
//			statement.setInt(2, photoID);
//
//			int affectedRecords = statement.executeUpdate();
//			System.out.println("Number of added records (likes): " + affectedRecords);
//
//			statement.close();
//			connection.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}



	private void printLikesByUser() {
		Query<User> query = session.createQuery("from User", User.class);
		List<User> users = query.list();

		System.out.println("### Users & likes");
		for (User user : users) {
			System.out.println(user);
				for (Photo p : user.getLikes()) {
					System.out.println(p);
				}

		}
	}

	private void printLikesByPhoto() {
		Query<Photo> query = session.createQuery("from Photo", Photo.class);
		List<Photo> photos = query.list();

		System.out.println("### Photos & below Likes, if any");
		for (Photo photo : photos) {
			System.out.println(photo);
			for (User u : photo.getLikers()) {
				System.out.println(u);
			}

		}
	}

}
