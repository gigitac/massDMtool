import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import java.io.PrintStream;
import java.util.ArrayList;

public class mainClass {
    public static void main(String[] args) {
        String reset = stackOverflow.ConsoleColors.RESET;
        String red = stackOverflow.ConsoleColors.RED;
        String green = stackOverflow.ConsoleColors.GREEN;
        String white = stackOverflow.ConsoleColors.WHITE;

        //** Do not edit anything below here unless you know what you are doing.

        // Gets the Configuration Settings you Gave
        configuration configuration = new configuration();

        // Logs in with given Token
        DiscordApi api = new DiscordApiBuilder().setToken(configuration.token).login().join();

        // Loads the whitelist
        String[] whitelist = configuration.whitelist.split(",");

        // Part that bleeps out HMAC
            // Splits token into the ID related part, TimeStamp related part, and HMAC
            String[] tokenParts = configuration.token.split(".");

            // Readies asterisks
            String asterisks = "";
            for (int i = 0; i < tokenParts[2].length() ; i++) {
                asterisks.concat("*");
            }

            // Replaces token with asterisks.
            String token = configuration.token.replaceAll(tokenParts[2], asterisks);

        // Sets up output.
        ArrayList output = new ArrayList();
            output.add("Mass DM Output");
            output.add(" ");
            output.add("Token: " + token);

        // Saves the amount of users messaged.
        Integer amountMessaged = 0;
        // Checks server presence.
        if(api.getServerById(configuration.serverID).isPresent()){
            output.add("Messaged Targets: ");
            User[] members = api.getServerById(configuration.serverID).get().getMembers().toArray(new User[0]);
            for (int i = 0; i < api.getServerById(configuration.serverID).get().getMemberCount() ; i++) {

                // Updates Message
                String message = configuration.message
                        // User Specific
                        .replaceAll("<discriminated_name>", members[i].getDiscriminatedName())
                        .replaceAll("<user_name>", members[i].getName())
                        .replaceAll("<user_id>", members[i].getIdAsString())
                        .replaceAll("<user_mention_tag>", members[i].getMentionTag())
                        // Server Specific
                        .replaceAll("<server_name>", api.getServerById(configuration.serverID).get().getName())
                        .replaceAll("<server_id>", api.getServerById(configuration.serverID).get().getIdAsString())
                        .replaceAll("<server_membercount>", String.valueOf(api.getServerById(configuration.serverID).get().getMemberCount()))
                        .replaceAll("<server_old_membercount>", String.valueOf(api.getServerById(configuration.serverID).get().getMemberCount() - 1));

                // Checks if the message was sent and updates that
                if(!configuration.whitelist.contains(members[i].getIdAsString())){
                    Boolean ifMessagePresent = members[i].sendMessage(message).isDone();
                    if(ifMessagePresent){
                        amountMessaged++;
                        output.add(members[i].getDiscriminatedName() + ": true");
                        System.out.println(members[i].getDiscriminatedName() + ":" + green + " true" + reset);
                    } else {
                        output.add(members[i].getDiscriminatedName() + ": false");
                        System.out.println(members[i].getDiscriminatedName() + ":" + red + " false" + reset);
                    }
                } else {
                    output.add(members[i].getDiscriminatedName() + ": whitelisted");
                    System.out.println(members[i].getDiscriminatedName() + ":" + white + " whitelisted " + reset);
                }
            }

            // Updates this fella
            output.set(output.indexOf("Messaged Targets: "), "Messaged Targets: " + amountMessaged + " out of " + api.getServerById(configuration.serverID).get().getMemberCount());
            filesClass.saveStringList("src/main/java/output.txt", output);
            System.out.println(green + "See output.txt");
            System.exit(200);

        } else {
            // Returns Error if server is not found.
            output.add("Guild not Found.");
            output.add(" ");
            System.out.println(red + "404: Guild not Found");
            filesClass.saveStringList("src/main/java/output.txt", output);
            System.exit(200);
        }

    }
}
