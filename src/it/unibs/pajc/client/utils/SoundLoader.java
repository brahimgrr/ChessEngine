package it.unibs.pajc.client.utils;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public enum SoundLoader {
	PROMOTE("promote.wav"),
	NOTIFY("notify.wav"),
	MOVE("move-self.wav"),
	MOVE_CHECK("move-check.wav"),
	CASTLE("castle.wav"),
	CAPTURE("capture.wav");
	
	String finelname;
	Clip clip;
	
	SoundLoader(String fname) {
		this.finelname = fname;
		this.clip = loadSoundTrack(fname);
	}
	
	private Clip loadSoundTrack(String fname) {
		
		try {

			File audioFile = new File(System.getProperty("user.dir") + "/res/sounds/" + fname);

			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
			
			Clip clip = AudioSystem.getClip();
			clip.open(audioStream);
			
			return clip;
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
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
