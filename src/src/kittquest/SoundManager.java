package kittquest;

import javax.sound.sampled.*;
import java.io.File;

/**
 * SoundManager.java - Sound Effects & Music
 * Author: [Member 6's Name]
 *
 * TODO - Member 6:
 *  - Load .wav files and play them at the right moments
 *  - Free sounds: freesound.org or mixkit.co
 *  - Put .wav files in a /sounds folder in the project
 */
public class SoundManager {

    public SoundManager() {}

    public void playBackgroundMusic() {
        System.out.println("[Sound] Background music started");
    }

    public void playJumpSound() {
        System.out.println("[Sound] Jump!");
    }

    public void playHurtSound() {
        System.out.println("[Sound] Ouch!");
    }

    public void playWinSound() {
        System.out.println("[Sound] You win!");
    }

    public void playGameOverSound() {
        System.out.println("[Sound] Game over!");
    }

    private void playClip(String filePath) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (Exception e) {
            System.out.println("[Sound] Could not load: " + filePath);
        }
    }
}