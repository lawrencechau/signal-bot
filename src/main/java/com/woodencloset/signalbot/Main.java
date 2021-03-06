package com.woodencloset.signalbot;

import com.woodencloset.signalbot.responders.MtgFetchResponder;

import org.apache.commons.cli.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.whispersystems.libsignal.InvalidKeyException;

import java.io.Console;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Security;
import java.util.prefs.BackingStoreException;

public class Main {

    static private SignalBot bot = new SignalBot();

    public static void main(String[] args) throws IOException, InvalidKeyException, BackingStoreException, Exception {        
        Security.addProvider(new BouncyCastleProvider());

        Options options = new Options();
        OptionGroup commands = new OptionGroup();
        commands.setRequired(true);
        commands.addOption(new Option("r", "register", true, "Register user with given phone number. Sends a verification SMS."));
        commands.addOption(new Option("v", "verify", true, "Verify user with given verification code."));
        commands.addOption(new Option("l", "listen", false, "Listen to incoming messages"));
        commands.addOption(new Option("t", "test", false, "Test all responders using text input"));
        options.addOptionGroup(commands);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            String header = "Signal bot service\n\n";
            String footer = "";
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("signal-bot", header, options, footer, true);
            System.exit(1);
        }

        bot.addResponder(new MtgFetchResponder());

        if (cmd.hasOption("register")) {
            bot.register(cmd.getOptionValue("register"));
        } else if (cmd.hasOption("verify")) {
            bot.verify(cmd.getOptionValue("verify"));
        } else if (cmd.hasOption("listen")) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> bot.stopListening()));
            bot.listen();
        } else if (cmd.hasOption("test")) {
            Console console = System.console();
            while (true) {
                String input = console.readLine("Enter input (ENTER to exit): ");
                if (input.isEmpty()) break;
                bot.testResponders(input);
            }
            System.out.println("Empty input, exiting.");
        }
    }
}
