package utils;

import java.io.InputStream;
import java.io.BufferedInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;

interface PlaySoundProps {
    public int soundLength(String path);
}

public class PlaySounds implements PlaySoundProps {

    public PlaySounds() {
    }

    public PlaySounds(String _path) {
        playSound(_path, -25.0f);
    }

    public PlaySounds(String _path, float volume) {
        playSound(_path, volume);
    }

    public void play(String _path, float volume) {
        playSound(_path, volume);

    }

    private void playSound(String _path, float volume) {
        Thread thread = new Thread(() -> {
            try (InputStream audioSrc = getClass().getClassLoader().getResourceAsStream("resource/audio/" + _path)) {
                if (audioSrc == null) {
                    System.out.println("Audio file not found!");
                    return;
                }

                InputStream bufferedIn = new BufferedInputStream(audioSrc);
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedIn);
                AudioFormat format = inputStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);

                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(inputStream);

                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volume);

                clip.start();

                // For build java project
                if (clip.isActive()) {
                    clip.close();

                };

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

    public int soundLength(String path) {
        try (InputStream audioSrc = getClass().getClassLoader().getResourceAsStream("resource/audio/" + path)) {
            if (audioSrc == null) {
                System.out.println("Audio file not found!");
                return 0;
            }

            AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioSrc);
            AudioFormat format = inputStream.getFormat();
            long frames = inputStream.getFrameLength();
            float frameRate = format.getFrameRate();

            return (int) ((Math.floor(frames / frameRate)) * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
