/**
 * Created by jelink on 11/11/15.
 */

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;

import java.util.Scanner;
import java.util.UUID;

public class MainClass {private static Cluster cluster;

    private Session session;
    private String keyspace = "lingga";
    private String server_ip = "167.205.35.19";
    private User user;

    public MainClass() {
        Cluster.Builder builder = Cluster.builder();
        builder.addContactPoint(server_ip);
        cluster = builder.build();
        session = cluster.connect(keyspace);
    }

    public static void main (String[] args) {
        MainClass client = new MainClass();
        String command = "";
        while (!command.equals("exit")) {
            client.showInstruction();
            if (client.user != null) {
                System.out.print(client.user.getUsername());
            }
            System.out.print(">> ");
            Scanner scn = new Scanner(System.in);

            command = scn.next();

            if (command.toLowerCase().startsWith("reg")) {
                String username = scn.next();
                String password = scn.next();

                int register_code = client.register(username, password);
                if (register_code == 0) {
                    System.out.println("Registrasi berhasil");
                } else {
                    System.out.println("Registrasi gagal");
                }
                scn.nextLine();
            } else if (command.toLowerCase().startsWith("login")) {
                String username = scn.next();
                String password = scn.next();

                client.user = client.login(username, password);
                if (client.user != null) {
                    System.out.println("Login berhasil");
                } else {
                    System.out.println("Login gagal");
                }
                scn.nextLine();
            } else if (command.toLowerCase().startsWith("fol")) {
                String username = scn.next();

                int follow_code = client.follow(username);
                if (follow_code == -1) {
                    System.out.println("follow gagal");
                } else {
                    System.out.println("follow berhasil");
                }
                scn.nextLine();
            } else if (command.toLowerCase().startsWith("tweet")) {
                String tweet = scn.nextLine();
                tweet = tweet.substring(1);

                int tweet_code = client.tweet(tweet);
                if (tweet_code == -1) {
                    System.out.println("tweet gagal");
                } else {
                    System.out.println("tweet berhasil");
                }
            } else if (command.toLowerCase().startsWith("userl")) {
                String username = scn.next();

                int code = client.userline(username);
                if (code == -1) {
                    System.out.println("error");
                }
                scn.nextLine();
            } else if (command.toLowerCase().startsWith("timel")) {
                String username = scn.next();

                int code = client.timeline(username);
                if (code == -1) {
                    System.out.println("error");
                }
                scn.nextLine();
            } else if (command.toLowerCase().startsWith("logout")) {
                client.logout();
                scn.nextLine();
            }
            System.out.println("(enter to continue)");
            scn.nextLine();
        }
        System.out.println("exiting program");
    }

    private void showInstruction() {
        System.out.println("=========================== SimpleTweet ===========================");
        System.out.println("Pilih perintah-perintah di bawah ini, ikuti formatnya dengan benar");
        if (user != null) {
            System.out.println("follow <username>");
            System.out.println("tweet <tweet_content>");
            System.out.println("userline <username>");
            System.out.println("timeline <username>");
            System.out.println("logout");
        } else {
            System.out.println("register <username> <password>");
            System.out.println("login <username> <password>");
        }
        System.out.println("exit");
    }

    private int register (String username, String password) {
        if (user != null) {
            return -1;
        }
        ResultSet results = session.execute("SELECT * FROM users WHERE username='" + username + "'");
        if (results.one() != null) {
            return -1;
        }
        session.execute("INSERT INTO users (username, password) VALUES ('" + username + "', '" + password + "')");
        return 0;
    }

    private User login (String username, String password) {
        if (user != null) {
            return null;
        }
        ResultSet results = session.execute("SELECT * FROM users WHERE username ='" + username + "' AND password ='" + password + "'");
        User user = null;
        Row row = results.one();
        if (row != null) {
            user = new User(row.getString("username"), row.getString("password"));
        }

        return user;
    }

    private int follow (String username) {
        // prekondisi
        if (user == null || user.getUsername().equals(username)) return -1;

        // body
        ResultSet results = session.execute("SELECT * FROM users WHERE username ='" + username + "'");
        if (results.one() == null) {
            return -1;
        } else {
            session.execute("INSERT INTO friends (username, friend, since) VALUES ('" + user.getUsername() + "', '" + username + "', 'now')" );
            session.execute("INSERT INTO followers (username, follower, since) VALUES ('" + username + "', '" + user.getUsername() + "', 'now')" );
        }

        return 0;
    }

    private int tweet (String tweet_str) {
        // prekondisi
        if (user == null) return -1;

        // body
        Tweet tweet = new Tweet(UUID.randomUUID(), user.getUsername(), tweet_str);
        UUID timeuuid = UUIDs.timeBased();

        // masukkan ke tweet
        session.execute("INSERT INTO tweets (tweet_id, username, body) VALUES (" + tweet.getTweetId() + ", '" + tweet.getUsername() + "', '" + tweet.getBody() + "')");

        // masukkan ke userline
        session.execute("INSERT INTO userline (username, time, tweet_id) VALUES ('" + user.getUsername() + "', " + timeuuid + ", " + tweet.getTweetId() + ")");

        // masukkan ke timeline sendiri
        session.execute("INSERT INTO timeline (username, time, tweet_id) VALUES ('" + user.getUsername() + "', " + timeuuid + ", " + tweet.getTweetId() + ")");

        // masukkan ke timeline orang lain
        ResultSet results = session.execute("SELECT * FROM followers WHERE username ='" + user.getUsername() + "'");
        for (Row row : results) {
            String follower = row.getString("follower");
            session.execute("INSERT INTO timeline (username, time, tweet_id) VALUES ('" + follower + "', " + timeuuid + ", " + tweet.getTweetId() + ")");
        }

        return 0;
    }

    private int userline (String username) {
        // prekondisi
        if (user == null) return -1;

        // body
        ResultSet results = session.execute("SELECT * FROM userline WHERE username ='" + username + "'");
        for (Row row : results) {
            Row tweet = session.execute("SELECT * FROM tweets WHERE tweet_id=" + row.getUUID("tweet_id")).one();
            if (tweet != null) {
                System.out.println(tweet.getString("username") + " : " + tweet.getString("body"));
            } else {
                return -1;
            }
        }
        return 0;
    }

    private int timeline (String username) {
        // prekondisi
        if (user == null) return -1;

        // body
        ResultSet results = session.execute("SELECT * FROM timeline WHERE username ='" + username + "'");
        for (Row row : results) {
            Row tweet = session.execute("SELECT * FROM tweets WHERE tweet_id=" + row.getUUID("tweet_id")).one();
            if (tweet != null) {
                System.out.println(tweet.getString("username") + " : " + tweet.getString("body"));
            } else {
                return -1;
            }
        }
        return 0;
    }

    private void logout () {
        user = null;
    }
}
