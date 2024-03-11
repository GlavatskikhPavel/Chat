package server.services;

import server.interfaces.AuthService;

import java.util.ArrayList;
import java.util.List;

public class AuthServiceImpl implements AuthService {
    private List<UserData> users = new ArrayList<>();

    public AuthServiceImpl() {
        users.add(new UserData("user1", "123", "Alex"));
        users.add(new UserData("user2", "123", "Ivan"));
        users.add(new UserData("user3", "123", "Olga"));
    }

    @Override
    public String getNickName(String login, String password) {
        for (UserData user : users) {
            if (login.equals(user.getLogin()) && password.equals(user.getPassword())) {
                return user.getNickName();
            }
        }
        return null;
    }

    private class UserData {

        private String login;
        private String password;
        private String nickName;

        public UserData(String login, String password, String nickName) {
            this.login = login;
            this.password = password;
            this.nickName = nickName;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getNickName() {
            return nickName;
        }
    }
}
