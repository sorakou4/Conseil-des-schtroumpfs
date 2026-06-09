package vue;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class MediaManager {

    private static Clip   clip;
    private static double currentVolume = 0.2;

    public static void play(String resourcePath) {
        stop();
        try {
            InputStream is = MediaManager.class.getResourceAsStream(resourcePath);
            if (is == null) { System.err.println("Musique introuvable : " + resourcePath); return; }
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            clip = AudioSystem.getClip();
            clip.open(ais);
            setVolume(currentVolume);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            System.err.println("Erreur musique : " + e.getMessage());
        }
    }

    public static void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }

    public static void setVolume(double volume) {
        currentVolume = volume;
        if (clip != null && clip.isOpen()) {
            FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log10(Math.max(volume, 0.0001)) * 20);
            fc.setValue(Math.max(fc.getMinimum(), Math.min(fc.getMaximum(), dB)));
        }
    }

    public static void playSfx(String resourcePath) {
        try {
            InputStream is = MediaManager.class.getResourceAsStream(resourcePath);
            if (is == null) { System.err.println("SFX introuvable : " + resourcePath); return; }
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            Clip sfx = AudioSystem.getClip();
            sfx.open(ais);
            FloatControl fc = (FloatControl) sfx.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log10(Math.max(currentVolume, 0.0001)) * 20);
            fc.setValue(Math.max(fc.getMinimum(), Math.min(fc.getMaximum(), dB)));
            sfx.start();
        } catch (Exception e) {
            System.err.println("Erreur SFX : " + e.getMessage());
        }
    }
}