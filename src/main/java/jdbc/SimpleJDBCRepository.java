package jdbc;


import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SimpleJDBCRepository {

    private PreparedStatement ps = null;
    private Statement st = null;
    private CustomDataSource dataSource = CustomDataSource.getInstance();
    private Connection connection = dataSource.getConnection();


    private static final String createUserSQL = """
            INSERT INTO myfirstdb.myusers(
            firstname, lastname, age)
            VALUES (?, ?, ?);
            """;
    private static final String updateUserSQL = """
            UPDATE myfirstdb.myusers
            SET firstname=?, lastname=?, age=?
            WHERE id = ?
            """;
    private static final String deleteUser = """
            DELETE FROM myfirstdb.myusers
            WHERE id = ?
            """;
    private static final String findUserByIdSQL = """
            SELECT id, firstname, lastname, age FROM myfirstdb.myusers
            WHERE id = ?
            """;
    private static final String findUserByNameSQL = """
            SELECT id, firstname, lastname, age FROM myfirstdb.myusers
            WHERE firstname LIKE CONCAT('%',?,'%')
            """;
    private static final String findAllUserSQL = """
            SELECT id, firstname, lastname, age FROM myfirstdb.myusers
            """;

    public Long createUser(User user) {
        Long id = null;

        try (PreparedStatement statement = connection.prepareStatement(createUserSQL)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setInt(3, user.getAge());
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                id = resultSet.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public User findUserById(Long userId) {
        User user = null;

        try (PreparedStatement statement = connection.prepareStatement(findUserByIdSQL)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = parserUser(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User findUserByName(String userName) {
        User user = null;

        try (PreparedStatement statement = connection.prepareStatement(findUserByNameSQL)) {
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = parserUser(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(findAllUserSQL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(parserUser(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User updateUser(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateUserSQL)) {
            statement.setLong(4, user.getId());
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setInt(3, user.getAge());
            if (statement.executeUpdate() != 0) {
                return findUserById(user.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteUser(Long userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteUser)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User parserUser(ResultSet resultSet) {
        long id = 0;
        String firstname = null;
        String lastname = null;
        int age = 0;
        try {
            id = resultSet.getLong("id");
            firstname = resultSet.getString(2);
            lastname = resultSet.getString(3);
            age = resultSet.getInt("age");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        User user = new User(id, firstname, lastname, age);
        return user;
    }
}
