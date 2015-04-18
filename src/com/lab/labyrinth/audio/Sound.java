package com.lab.labyrinth.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Sound {
	private Clip beep, button, loose, win, footstep, ambiance;
	private FloatControl gainControlBeep, gainControlButton, gainControlLoose, gainControlWin, gainControlFootstep, gainControlAmbiance;
	private int loop1, loop2, loop3, loop4, loop5, loop6;

	public Sound(int volume) {
		loop1 = 0;
		loop2 = 0;
		loop3 = 0;
		loop4 = 0;
		loop5 = 0;
		loop6 = 0;
		
		try {
			beep = AudioSystem.getClip();
			beep.open(AudioSystem.getAudioInputStream(new File("res/sound/beep.wav")));
			gainControlBeep = (FloatControl) beep.getControl(FloatControl.Type.MASTER_GAIN);
			gainControlBeep.setValue(volume);
			
			button = AudioSystem.getClip();
			button.open(AudioSystem.getAudioInputStream(new File("res/sound/button.wav")));
			gainControlButton = (FloatControl) button.getControl(FloatControl.Type.MASTER_GAIN);
			gainControlButton.setValue(volume);
			
			loose = AudioSystem.getClip();
			loose.open(AudioSystem.getAudioInputStream(new File("res/sound/outOfTime.wav")));
			gainControlLoose = (FloatControl) loose.getControl(FloatControl.Type.MASTER_GAIN);
			gainControlLoose.setValue(volume);
			
			win = AudioSystem.getClip();
			win.open(AudioSystem.getAudioInputStream(new File("res/sound/win.wav")));
			gainControlWin = (FloatControl) win.getControl(FloatControl.Type.MASTER_GAIN);
			gainControlWin.setValue(volume);
			
			footstep = AudioSystem.getClip();
			footstep.open(AudioSystem.getAudioInputStream(new File("res/sound/footstep.wav")));
			gainControlFootstep = (FloatControl) footstep.getControl(FloatControl.Type.MASTER_GAIN);
			gainControlFootstep.setValue(volume);
			
			ambiance = AudioSystem.getClip();
			ambiance.open(AudioSystem.getAudioInputStream(new File("res/sound/ambiance.wav")));
			gainControlAmbiance = (FloatControl) ambiance.getControl(FloatControl.Type.MASTER_GAIN);
			gainControlAmbiance.setValue(volume);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}

	public void playBeep() {
		if (loop1 == 0) {
			beep.loop(0);
			loop1 = 1;
		} else {
			beep.loop(1);
		}
	}

	public void playButton() {
		if (loop2 == 0) {
			button.loop(0);
			loop2 = 1;
		} else {
			button.loop(1);
		}
	}

	public void playLoose() {
		if (loop3 == 0) {
			loose.loop(0);
			loop3 = 1;
		} else {
			loose.loop(1);
		}
	}

	public void playWin() {
		if (loop4 == 0) {
			win.loop(0);
			loop4 = 1;
		} else {
			win.loop(1);
		}
	}
	
	public void stopWin() {
		win.stop();
	}
	
	public void playFootstep() {
		if (loop5 == 0) {
			footstep.loop(0);
			loop5 = 1;
		} else {
			footstep.loop(1);
		}
	}
	
	public void stopFootstep() {
		footstep.stop();
	}
	
	public void playAmbiance() {
		if (loop6 == 0) {
			ambiance.loop(0);
			loop6 = 1;
		} else {
			ambiance.loop(1);
		}
	}
	
	public void stopAmbiance() {
		ambiance.stop();
	}
}