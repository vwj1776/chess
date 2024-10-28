package dataaccess;

import model.UserData;

public interface DataAccess {
    UserResponse addUser(UserData user);

    UserData getUser(String username);

    UserResponse login(String username, String password);

    void logout(String authToken);


//    Collection<Pet> listPets() throws ResponseException;
//
//    Pet getPet(int id) throws ResponseException;
//
//    void deletePet(Integer id) throws ResponseException;
//
//    void deleteAllPets() throws ResponseException;
}
