package com.imagespot.DAOImpl;

import com.imagespot.Connection.ConnectionManager;
import com.imagespot.DAO.CollectionDao;
import com.imagespot.View.ViewFactory;
import com.imagespot.model.Collection;
import com.imagespot.model.Post;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollectionDaoImpl implements CollectionDao {
    private final Connection con;

    public CollectionDaoImpl() {
        con = ConnectionManager.getInstance().getConnection();
    }

    public int createCollection(String name, String description, String owner){
        PreparedStatement st;
        String query = "INSERT INTO collection(name, description, owner) VALUES (?, ?, ?) RETURNING idcollection";
        ResultSet rs;
        int collectionId = -1;

        try {
            st = con.prepareStatement(query);
            st.setString(1, name);
            st.setString(2, description);
            st.setString(3, owner);
            rs = st.executeQuery();

            if(rs.next())
                collectionId = rs.getInt(1);

        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return collectionId;
    }

    public ArrayList<Collection> getRecentCollections(int offset){
        ArrayList<Collection> collections = new ArrayList<>();
        ResultSet rs;
        Statement st;
        String query = "SELECT * FROM collection ORDER BY idcollection DESC LIMIT 20 OFFSET " + offset;

        try {
            st = con.createStatement();
            rs = st.executeQuery(query);

            while(rs.next()){
                Collection collection = new Collection(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        new UserDAOImpl().getUserInfoForPreview(rs.getString(4))
                );
                collection.setPosts(getPostsOfCollectionForPreview(collection.getIdCollection()));
                int[] stats = getCollectionStats(collection.getIdCollection());
                collection.setPostsCount(stats[0]);
                collection.setMemberCount(stats[1]);
                collections.add(collection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return collections;
    }

    public ArrayList<Collection> getOwnedCollections(String username, int offset){
        ArrayList<Collection> collections = new ArrayList<>();
        ResultSet rs;
        String query = "SELECT idcollection, name, description FROM collection" +
                " WHERE owner = ? ORDER BY idcollection DESC LIMIT 20 OFFSET ?";
        PreparedStatement st;

        try {
            st = con.prepareStatement(query);
            st.setString(1, username);
            st.setInt(2, offset);
            rs = st.executeQuery();

            while (rs.next()){
                Collection collection = new Collection();
                collection.setIdCollection(rs.getInt(1));
                collection.setName(rs.getString(2));
                collection.setDescription(rs.getString(3));
                collection.setPosts(getPostsOfCollectionForPreview(collection.getIdCollection()));
                collection.setOwner(ViewFactory.getInstance().getUser());
                int[] stats = getCollectionStats(collection.getIdCollection());
                collection.setPostsCount(stats[0]);
                collection.setMemberCount(stats[1]);
                collections.add(collection);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return collections;
    }

    public ArrayList<Collection> getCollectionsWhereUserPosted(String username, int offset){
        ArrayList<Collection> collections = new ArrayList<>();
        PreparedStatement st;
        ResultSet rs;
        String query = "SELECT DISTINCT idcollection, name, collection.description, owner FROM collection" +
                " JOIN collection_post ON idcollection = collection" +
                " JOIN post ON post = idimage WHERE profile = ? " +
                " ORDER BY idcollection DESC LIMIT 20 OFFSET ?";

        try {
            st = con.prepareStatement(query);
            st.setString(1, username);
            st.setInt(2, offset);
            rs = st.executeQuery();

            while (rs.next()){
                Collection collection = new Collection();
                collection.setIdCollection(rs.getInt(1));
                collection.setName(rs.getString(2));
                collection.setDescription(rs.getString(3));
                collection.setPosts(getPostsOfCollectionForPreview(collection.getIdCollection()));
                collection.setOwner(new UserDAOImpl().getUserInfoForPreview(rs.getString(4)));
                int[] stats = getCollectionStats(collection.getIdCollection());
                collection.setPostsCount(stats[0]);
                collection.setMemberCount(stats[1]);
                collections.add(collection);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return collections;
    }

    private ArrayList<Post> getPostsOfCollectionForPreview(Integer idCollection){
        ArrayList<Post> collectionPosts = new ArrayList<>();

        String query = "SELECT P.idimage FROM post AS P" +
                " JOIN collection_post CP" +
                " ON P.idimage = CP.post" +
                " WHERE CP.collection = ? ORDER BY CP.date DESC LIMIT 4";

        ResultSet rs;
        PreparedStatement st;

        try{
            st = con.prepareStatement(query);
            st.setInt(1, idCollection);

            rs = st.executeQuery();
            while(rs.next()){
                Post p = new PostDAOImpl().getPreviewPost(rs.getInt(1));
                collectionPosts.add(p);
            }

        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return collectionPosts;
    }

    public ArrayList<Post> getPostsOfCollection(Integer idCollection, int offset){
        ArrayList<Post> collectionPosts = new ArrayList<>();
        ResultSet rs;
        PreparedStatement st;

        String query = "SELECT idimage FROM post" +
                    " JOIN collection_post ON idimage = post WHERE collection = ?" +
                    " ORDER BY date DESC LIMIT 20 OFFSET ?";

        try{
            st = con.prepareStatement(query);
            st.setInt(1, idCollection);
            st.setInt(2, offset);

            rs = st.executeQuery();
            while(rs.next()){
                Post p = new PostDAOImpl().getPreviewPost(rs.getInt(1));
                collectionPosts.add(p);
            }

        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return collectionPosts;
    }

    public ArrayList<Post> getUserPostsOfCollection(String username, Integer idCollection, int offset){
        ArrayList<Post> userCollectionPosts = new ArrayList<>();
        ResultSet rs;
        PreparedStatement st;

        String query = "SELECT idimage FROM post" +
                    " JOIN collection_post ON idimage = post WHERE collection = ? AND profile = ?" +
                    " ORDER BY date DESC LIMIT 20 OFFSET ?";

        try {
            st = con.prepareStatement(query);
            st.setInt(1, idCollection);
            st.setString(2, username);
            st.setInt(3, offset);


            rs = st.executeQuery();
            while(rs.next()){
                Post p = new PostDAOImpl().getPreviewPost(rs.getInt(1));
                userCollectionPosts.add(p);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return userCollectionPosts;
    }

    public ArrayList<Post> getUserGallery(String username, Integer idCollection, int offset){
        ArrayList<Post> userCollectionPosts = new ArrayList<>();
        ResultSet rs;
        PreparedStatement st;

        String query = "SELECT idimage FROM post" +
                    " WHERE profile = ? AND status = 'Public' AND idimage NOT IN (" +
                    " SELECT idimage FROM post" +
                    " JOIN collection_post ON post.idimage = collection_post.post AND collection = ? )" +
                    " ORDER BY posting_date DESC LIMIT 20 OFFSET ?";

        try {
            st = con.prepareStatement(query);
            st.setString(1, username);
            st.setInt(2, idCollection);
            st.setInt(3, offset);

            rs = st.executeQuery();
            while(rs.next()){
                Post p = new PostDAOImpl().getPreviewPost(rs.getInt(1));
                userCollectionPosts.add(p);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
        return userCollectionPosts;
    }
    public int[] getCollectionStats(int idCollection){
        Statement st;
        ResultSet rs;
        String query = "SELECT * FROM collection_stats WHERE idcollection = " + idCollection;

        int[] stats = new int[2];

        try {
            st = con.createStatement();
            rs = st.executeQuery(query);

            if(rs.next()){
                stats[0] = rs.getInt(2);
                stats[1] = rs.getInt(3);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stats;
    }
    public void addPostsToCollection(Integer collectionId, List<Integer> postsId){
        PreparedStatement st;

        String query = "INSERT INTO collection_post(collection, post) VALUES (?, ?)";

        try {
            st = con.prepareStatement(query);
            for(Integer postId : postsId){
                st.setInt(1, collectionId);
                st.setInt(2, postId);
                st.execute();
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void removePostsToCollection(Integer collectionId, List<Integer> postsId){
        PreparedStatement st;

        String query = "DELETE FROM collection_post WHERE collection = ? AND post = ?";

        try {
            st = con.prepareStatement(query);
            for(Integer id : postsId){
                st.setInt(1, collectionId);
                st.setInt(2, id);
                st.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCollectionName(Integer collectionId, String name){
        PreparedStatement st;
        String query = "UPDATE collection SET name = ? WHERE idcollection = ?";
        try {
            st = con.prepareStatement(query);
            st.setString(1, name);
            st.setInt(2, collectionId);
            st.executeUpdate();
            st.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void setCollectionDescription(Integer collectionId, String description){
        PreparedStatement st;
        String query = "UPDATE collection SET description = ? WHERE idcollection = ?";
        try {
            st = con.prepareStatement(query);
            st.setString(1, description);
            st.setInt(2, collectionId);
            st.executeUpdate();
            st.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void deleteCollection(Integer collectionId){
        Statement st;
        String query = "DELETE FROM collection WHERE idcollection = '" + collectionId + "'";
        try {
            st = con.createStatement();
            st.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
