package kittquest;

import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager {

    private Clip backgroundMusic;

    public SoundManager() {}

    public void playBackgroundMusic() {
        playBackgroundMusic(1);
    }

    public void playBackgroundMusic(int level) {
        stopBackgroundMusic();
        String track;
        switch (level) {
            case 0:  track = "background_menu.wav"; break;
            case 2:  track = "background_2.wav";    break;
            case 3:  track = "background_3.wav";    break;
            default: track = "background_1.wav";    break;
        }
        backgroundMusic = loadClip(track);
        if (backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
            backgroundMusic = null;
        }
    }

    public void playJumpSound()    { playOnce("jump.wav");     }
    public void playHurtSound()    { playOnce("hurt.wav");     }
    public void playWinSound()     { playOnce("win.wav");      }
    public void playGameOverSound(){ playOnce("gameover.wav"); }

    private void playOnce(String filename) {
        Clip clip = loadClip(filename);
        if (clip != null) clip.start();
    }

    private Clip loadClip(String filename) {
        try {
            URL url = getClass().getClassLoader().getResource(filename);
            System.out.println("[Sound] Looking for: " + filename + " → found: " + (url != null));
            if (url == null) return null;
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        } catch (Exception e) {
            System.out.println("[Sound] Error loading " + filename + ": " + e.getMessage());
            return null;
        }
    }
}