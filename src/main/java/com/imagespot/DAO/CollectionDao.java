package com.imagespot.DAO;

import com.imagespot.model.Collection;
import com.imagespot.model.Post;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public interface CollectionDao {
    int createCollection(String name, String description, String owner);
    ArrayList<Collection> getRecentCollections(int offset);
    ArrayList<Collection> getOwnedCollections(String username, int offset);
    ArrayList<Collection> getCollectionsWhereUserPosted(String username, int offset);
    ArrayList<Post> getPostsOfCollection(Integer idCollection, int offset);
    ArrayList<Post> getUserPostsOfCollection(String username, Integer idCollection, int offset);
    ArrayList<Post> getUserGallery(String username, Integer idCollection, int offset);
    int[] getCollectionStats(int idCollection);
    void addPostsToCollection(Integer collectionId, List<Integer> postsId);
    void removePostsToCollection(Integer collectionId, List<Integer> postsId);
    void setCollectionName(Integer collectionId, String name);
    void setCollectionDescription(Integer collectionId, String description);
    void deleteCollection(Integer collectionId);
}
