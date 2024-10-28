package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    final private HashMap<String, UserData> users = new HashMap<>();

    public UserResponse addUser(UserData user) {
        users.put(user.username(), user);
        // Generate an auth token
        String authToken = AuthData.generateToken(); // Assuming this method generates a new token

        // Create a response object
        UserResponse response = new UserResponse(user.username(), authToken);

        // Print the current state of users
        System.out.println(users);

        // Return the response object as JSON
        return response;
    }

    public UserData getUser(String username) {
        System.out.println("in memory");

        // Retrieve the user from the map
        UserData user = users.get(username);
        System.out.println(user);

        // Return user, can be null if not found
        return user;
    }

    public UserResponse login(String username, String password) {
        // Check if the user exists
        UserData user = users.get(username);

        if (user == null) {
            // User not found, return null or throw an exception
            return null; // This will lead to a 401 Unauthorized response in the session method
        }

        // Check if the password matches
        if (!user.password().equals(password)) {
            // Password does not match, return null or throw an exception
            return null; // This will lead to a 401 Unauthorized response in the session method
        }

        // If authentication is successful, generate a new auth token
        String authToken = AuthData.generateToken(); // Assuming you have a method to generate a token

        // Return a new UserResponse containing the username and authToken
        return new UserResponse(username, authToken);
    }


    public void logout(String authToken){

    }


//    public Collection<Pet> listPets() {
//        return pets.values();
//    }
//
//
//    public Pet getPet(int id) {
//        return pets.get(id);
//    }
//
//    public void deletePet(Integer id) {
//        pets.remove(id);
//    }
//
//    public void deleteAllPets() {
//        pets.clear();
//    }
}
