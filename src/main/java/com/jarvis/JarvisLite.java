package com.jarvis;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

public class JarvisLite {

    public static void main(String[] args) {
        speak("Hello, I am Jarvis Lite. How can I assist you today?");
        try {
            LiveSpeechRecognizer recognizer = new LiveSpeechRecognizer(configure());
            recognizer.startRecognition(true);
            SpeechResult result;

            while ((result = recognizer.getResult()) != null) {
                String command = result.getHypothesis();
                System.out.println("You said: " + command);
                command = command.toLowerCase();

                if (command.contains("time")) {
                    tellTime();
                } else if (command.contains("date")) {
                    tellDate();
                } else if (command.contains("open notepad")) {
                    openApp("notepad");
                } else if (command.contains("open browser")) {
                    openBrowser("https://www.google.com");
                } else if (command.contains("search for")) {
                    String query = command.replace("search for", "").trim().replace(" ", "+");
                    openBrowser("https://www.google.com/search?q=" + query);
                } else if (command.contains("exit") || command.contains("stop")) {
                    speak("Goodbye!");
                    break;
                } else {
                    speak("I did not understand that. Can you repeat?");
                }
            }

            recognizer.stopRecognition();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // SPEAK FUNCTION
    public static void speak(String text) {
        Voice voice = VoiceManager.getInstance().getVoice("kevin16");
        if (voice != null) {
            voice.allocate();
            voice.speak(text);
        }
    }

    // CONFIGURATION FOR SPHINX
    private static Configuration configure() {
        Configuration config = new Configuration();
        config.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        config.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        config.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
        config.setUseGrammar(false);
        config.setGrammarPath("resource:/edu/cmu/sphinx/models/en-us/");
        config.setGrammarName("digits");
        return config;
    }

    // TELL TIME
    private static void tellTime() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
        speak("The time is " + time);
    }

    // TELL DATE
    private static void tellDate() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));
        speak("Today is " + date);
    }

    // OPEN ANY APP
    private static void openApp(String appName) {
        try {
            Runtime.getRuntime().exec(appName);
            speak("Opening " + appName);
        } catch (IOException e) {
            speak("Sorry, I couldn't open " + appName);
        }
    }

    // OPEN URL IN BROWSER
    private static void openBrowser(String url) {
        try {
            Desktop.getDesktop().browse(URI.create(url));
            speak("Opening browser");
        } catch (IOException e) {
            speak("Sorry, I couldn't open the browser.");
        }
    }
}
