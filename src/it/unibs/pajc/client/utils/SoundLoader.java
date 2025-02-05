package it.unibs.pajc.client.utils;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Enum to handle sound loading
 */
public enum SoundLoader {
	PROMOTE("promote.wav"),
	NOTIFY("notify.wav"),
	MOVE("move-self.wav"),
	MOVE_CHECK("move-check.wav"),
	CASTLE("castle.wav"),
	CAPTURE("capture.wav");
	
	final String finelname;
	final Clip clip;
	
	SoundLoader(String fileName) {
		this.finelname = fileName;
		this.clip = loadSoundTrack(fileName);
	}

	/**
	 * retrieves an audio clip given a file name
	 * @param fileName audio clip file name
	 * @return the requested audio clip
	 */
	private Clip loadSoundTrack(String fileName) {
		try {
			File audioFile = new File(System.getProperty("user.dir") + "/res/sounds/" + fileName);

			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
			
			Clip clip = AudioSystem.getClip();
			clip.open(audioStream);
			
			return clip;
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Play current sound
	 */
	public void play() {
		if(clip == null || clip.isRunning())
			return;
		
		Thread t = new Thread( () -> {
			clip.setFramePosition(0);
			clip.start();
		});
		
		t.start();
	}
}
