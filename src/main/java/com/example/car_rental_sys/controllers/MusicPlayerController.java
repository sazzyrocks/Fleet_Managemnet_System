package com.example.car_rental_sys.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for In-Vehicle Infotainment Media and Music Player.
 */
public class MusicPlayerController {

    @FXML
    private ImageView backwardIcon, previousIcon, pauseIcon, nextIcon, forwardIcon;

    @FXML
    private Pane musicCoverPane, processBarPane;

    @FXML
    private Label songsterLabel, songNameLabel, playedTimeLabel, totalTimeLabel;

    private MediaPlayer player;
    private int currentSongIndex = 0;
    private double currentProgressSeconds = 0;
    private double totalDurationSeconds = 210; // Default fallback: 3m 30s
    private boolean isPlaying = false;
    private Timeline progressTimeline;

    private static class TrackInfo {
        String title;
        String artist;
        String filePath;

        TrackInfo(String title, String artist, String filePath) {
            this.title = title;
            this.artist = artist;
            this.filePath = filePath;
        }
    }

    private final List<TrackInfo> playlist = new ArrayList<>();

    @FXML
    private void initialize() {
        discoverTracks();
        loadTrack(currentSongIndex);
        initProgressTimeline();
    }

    private void discoverTracks() {
        playlist.clear();
        File musicDir = new File("src/main/resources/com/example/car_rental_sys/music");
        if (musicDir.exists() && musicDir.isDirectory()) {
            File[] files = musicDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
            if (files != null) {
                for (File file : files) {
                    String base = file.getName().substring(0, file.getName().length() - 4);
                    String[] parts = base.split("-");
                    String artist = parts.length > 0 ? parts[0].replace("_", " ") : "Unknown Artist";
                    String title = parts.length > 1 ? parts[1].replace("_", " ") : base.replace("_", " ");
                    playlist.add(new TrackInfo(title, artist, file.getAbsolutePath()));
                }
            }
        }

        if (playlist.isEmpty()) {
            playlist.add(new TrackInfo("Fleet Radio Station", "Ambient Cruiser", null));
            playlist.add(new TrackInfo("Highway Horizon", "Electric Pulse", null));
            playlist.add(new TrackInfo("Smooth Commute", "Urban Groove", null));
        }
    }

    private void loadTrack(int index) {
        if (playlist.isEmpty()) {
            return;
        }

        if (player != null) {
            try {
                player.stop();
            } catch (Exception ignored) {
            }
            player = null;
        }

        TrackInfo track = playlist.get(index % playlist.size());
        if (songNameLabel != null) {
            songNameLabel.setText(track.title);
        }
        if (songsterLabel != null) {
            songsterLabel.setText(track.artist);
        }

        currentProgressSeconds = 0;
        updateDisplayTimes();
        resetProgressBar();

        if (track.filePath != null) {
            try {
                Media media = new Media(new File(track.filePath).toURI().toString());
                player = new MediaPlayer(media);
                player.setOnReady(() -> {
                    Duration duration = player.getMedia().getDuration();
                    if (duration != null && !duration.isUnknown()) {
                        totalDurationSeconds = duration.toSeconds();
                        updateDisplayTimes();
                    }
                });
            } catch (Exception e) {
                // Fallback to synthetic duration
                totalDurationSeconds = 210;
            }
        } else {
            totalDurationSeconds = 210;
        }
    }

    private void initProgressTimeline() {
        progressTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (isPlaying) {
                if (player != null) {
                    currentProgressSeconds = player.getCurrentTime().toSeconds();
                } else {
                    currentProgressSeconds += 1.0;
                    if (currentProgressSeconds >= totalDurationSeconds) {
                        nextClickEvent();
                        return;
                    }
                }
                updateDisplayTimes();
                updateProgressBar();
            }
        }));
        progressTimeline.setCycleCount(Animation.INDEFINITE);
        progressTimeline.play();
    }

    private void updateDisplayTimes() {
        if (playedTimeLabel != null) {
            playedTimeLabel.setText(formatSeconds(currentProgressSeconds));
        }
        if (totalTimeLabel != null) {
            totalTimeLabel.setText(formatSeconds(totalDurationSeconds));
        }
    }

    private void updateProgressBar() {
        if (processBarPane != null && totalDurationSeconds > 0) {
            double maxWidth = 392.0;
            double width = Math.min(maxWidth, (currentProgressSeconds / totalDurationSeconds) * maxWidth);
            processBarPane.setPrefWidth(width);
        }
    }

    private void resetProgressBar() {
        if (processBarPane != null) {
            processBarPane.setPrefWidth(0);
        }
    }

    private String formatSeconds(double sec) {
        long totalSec = Math.max(0, (long) sec);
        long minutes = totalSec / 60;
        long seconds = totalSec % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @FXML
    private void pauseClickEvent() {
        isPlaying = !isPlaying;
        if (isPlaying) {
            if (player != null) {
                try {
                    player.play();
                } catch (Exception ignored) {
                }
            }
            setPlayIcon(true);
        } else {
            if (player != null) {
                try {
                    player.pause();
                } catch (Exception ignored) {
                }
            }
            setPlayIcon(false);
        }
    }

    private void setPlayIcon(boolean playing) {
        if (pauseIcon == null) {
            return;
        }
        try {
            String iconPath = playing
                    ? "/com/example/car_rental_sys/image/UI/playIcon_05.gif"
                    : "/com/example/car_rental_sys/image/UI/pause.png";
            URL resource = getClass().getResource(iconPath);
            if (resource != null) {
                pauseIcon.setImage(new Image(resource.toExternalForm()));
            }
        } catch (Exception ignored) {
        }
    }

    @FXML
    private void nextClickEvent() {
        currentSongIndex = (currentSongIndex + 1) % playlist.size();
        loadTrack(currentSongIndex);
        if (isPlaying && player != null) {
            try {
                player.play();
            } catch (Exception ignored) {
            }
        }
    }

    @FXML
    private void previousClickEvent() {
        currentSongIndex = (currentSongIndex - 1 + playlist.size()) % playlist.size();
        loadTrack(currentSongIndex);
        if (isPlaying && player != null) {
            try {
                player.play();
            } catch (Exception ignored) {
            }
        }
    }

    @FXML
    private void add10() {
        currentProgressSeconds = Math.min(totalDurationSeconds, currentProgressSeconds + 10);
        if (player != null) {
            try {
                player.seek(Duration.seconds(currentProgressSeconds));
            } catch (Exception ignored) {
            }
        }
        updateDisplayTimes();
        updateProgressBar();
    }

    @FXML
    private void minus10() {
        currentProgressSeconds = Math.max(0, currentProgressSeconds - 10);
        if (player != null) {
            try {
                player.seek(Duration.seconds(currentProgressSeconds));
            } catch (Exception ignored) {
            }
        }
        updateDisplayTimes();
        updateProgressBar();
    }
}

