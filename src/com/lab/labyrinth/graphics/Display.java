package com.lab.labyrinth.graphics;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.lab.labyrinth.Main;
import com.lab.labyrinth.account.AccountGui;
import com.lab.labyrinth.input.Game;
import com.lab.labyrinth.input.InputHandler;
import com.lab.labyrinth.launcher.OptionsGui;
import com.lab.labyrinth.level.Level;
import com.lab.labyrinth.level.LevelSerialization;
import com.lab.labyrinth.menu.MainMenuGui;

public class Display extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;
	private int width = 900;
	private int height = 650;
	private Thread thread;
	private Screen screen;
	private InputHandler input;
	private BufferedImage img, resumeOff, resumeOn, restartOff, restartOn, optionsOff, optionsOn, quitOff, quitOn, filter, cursor;
	private Graphics g;
	private JFrame frame;
	private Level level;
	private Cursor blank;
	private boolean running = false, play = true;
	private int[] pixels, rankings;
	private int minutes, seconds, fullTime, fps;

	private int frames, tickCount, tempTick, time, tempTime;
	private double unprossesedSeconds;
	private long previousTime, currentTime, passedTime;
	private boolean ticked;

	public Display(Level level) {
		this.level = level;

		frame = new JFrame("Labyrinth");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(width, height);
		frame.getContentPane().add(this);
		frame.setLocationRelativeTo(null);

		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);

		screen = new Screen(this.level, width, height);
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		input = new InputHandler();
		previousTime = System.nanoTime();
		unprossesedSeconds = 0;
		ticked = false;
		tickCount = 0;
		tempTick = 0;
		tempTime = 9;
		frames = 0;
		minutes = level.getMinTimeLimit();
		seconds = level.getSecTimeLimit();
		fullTime = minutes * 60 + seconds;

		addKeyListener(input);
		addFocusListener(input);
		addMouseListener(input);
		addMouseMotionListener(input);
		loadImages();
		blank = Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0, 0), "blank");
		start();
	}

	public void start() {
		if (running)
			return;
		running = true;
		thread = new Thread(this, "game");
		thread.start();
	}

	public synchronized void stop() {
		if (!running)
			return;
		running = false;
		try {
			thread.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public synchronized void run() {
		requestFocus();
		while (running) {
			fps();
			if (ticked) {
				renderGame();
				frames++;
			}
		}
	}

	private void tick() {
		Main.game.tick(input.key);
	}

	private void renderGame() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();

		if (Main.game.isPlay())
			renderPlay();
		if (Main.game.isFinish())
			renderFinish();
		if (Main.game.isPause())
			renderPause();

		g.dispose();
		bs.show();

	}

	private void renderPlay() {
		frame.getContentPane().setCursor(blank);
		Main.game.getSound().playAmbiance();
		screen.render();
		for (int i = 0; i < width * height; i++)
			pixels[i] = screen.pixels[i];
		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
		if (Main.game.isCountdown()) {
			countdownTimer();
		} else {
			g.setFont(new Font("Verdana", 3, 20));
			g.setColor(Color.orange);
			g.drawString(fps + "fps", (int) (frame.getWidth() - g.getFontMetrics().getStringBounds(fps + "fps", g).getWidth()) - 30, 60);
			g.drawString("Best Time " + level.getMinBestTime() + ":" + level.getSecBestTime(), (int) (frame.getWidth() - g.getFontMetrics().getStringBounds("Best Time " + level.getMinBestTime() + ":" + level.getSecBestTime(), g).getWidth()) - 30, 30);
			timer();
		}
	}

	private void renderFinish() {
		frame.getContentPane().setCursor(null);
		Main.game.setPlay(false);
		if (time > 0)
			renderWin();
		else
			renderLoose();
	}

	private void renderWin() {
		Main.game.getSound().stopAmbiance();
		Main.game.getSound().stopFootstep();
		g.setFont(new Font("Verdana", 3, 40));
		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
		g.drawImage(filter, 0, 0, getWidth(), getHeight(), null);
		g.setColor(Color.orange);
		g.drawString("Your Time", frame.getWidth() / 2 - ("Your Time".length() * 25) / 2, 100);
		g.drawString("Level Rankings", frame.getWidth() / 2 - ("Level Rankings".length() * 25) / 2, 250);
		g.setFont(new Font("Verdana", 3, 35));
		g.drawString("1st " + level.getRankings().get(0), frame.getWidth() / 2 - (("1st " + level.getRankings().get(0)).length() * 20) / 2, 300);
		g.drawString("2nd " + level.getRankings().get(1), frame.getWidth() / 2 - (("2nd " + level.getRankings().get(1)).length() * 20) / 2, 350);
		g.drawString("3rd " + level.getRankings().get(2), frame.getWidth() / 2 - (("3rd " + level.getRankings().get(2)).length() * 20) / 2, 400);
		g.setColor(Color.green);
		g.drawString(Integer.toString(time / 60) + ":" + Integer.toString(time % 60), frame.getWidth() / 2 - ((Integer.toString(time / 60) + ":" + Integer.toString(time % 60)).length() * 25) / 2, 150);
		renderQuit();
		if (play) {
			Main.game.getSound().playWin();
			play = false;
		}
	}

	private void renderLoose() {
		Main.game.getSound().stopAmbiance();
		Main.game.getSound().stopFootstep();
		g.setFont(new Font("Verdana", 3, 40));
		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
		g.drawImage(filter, 0, 0, getWidth(), getHeight(), null);
		g.setColor(Color.orange);
		g.drawString("Out of time", frame.getWidth() / 2 - ("Out of time".length() * 25) / 2, 100);
		renderQuit();
		renderRestart();
		if (play) {
			Main.game.getSound().playLoose();
			play = false;
		}
	}

	private void renderPause() {
		Main.game.getSound().stopAmbiance();
		Main.game.getSound().stopFootstep();
		frame.getContentPane().setCursor(null);
		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
		g.drawImage(filter, 0, 0, getWidth(), getHeight(), null);
		renderResume();
		renderRestart();
		renderOptions();
		renderQuit();
	}

	private void renderResume() {
		if (mouseIn(frame.getWidth() / 2 - (resumeOn.getWidth()) / 2, frame.getWidth() / 2 - (resumeOn.getWidth()) / 2 + resumeOn.getWidth(), 200, 200 + resumeOn.getHeight())) {
			g.drawImage(resumeOn, frame.getWidth() / 2 - (resumeOn.getWidth()) / 2, 190, resumeOn.getWidth(), resumeOn.getHeight(), null);
			if (InputHandler.MousePressed == 1) {
				clickCheck();
				Main.game.getSound().playButton();
				Main.game.setPlay(true);
				Main.game.setPause(false);
				Main.game.loadOptions();
			}
		} else {
			g.drawImage(resumeOff, frame.getWidth() / 2 - (resumeOff.getWidth()) / 2, 200, resumeOff.getWidth(), resumeOff.getHeight(), null);
		}
	}

	private void renderRestart() {
		if (mouseIn(frame.getWidth() / 2 - (restartOn.getWidth()) / 2, frame.getWidth() / 2 - (restartOn.getWidth()) / 2 + restartOn.getWidth(), 270, 270 + restartOn.getHeight())) {
			g.drawImage(restartOn, frame.getWidth() / 2 - (restartOn.getWidth()) / 2, 261, restartOn.getWidth(), restartOn.getHeight(), null);
			g.drawImage(restartOff, frame.getWidth() / 2 - (restartOff.getWidth()) / 2, 270, restartOff.getWidth(), restartOff.getHeight(), null);
			if (InputHandler.MousePressed == 1) {
				clickCheck();
				Main.game.getSound().playButton();
				Main.game = new Game();
				tickCount = 0;
				play = true;
			}
		} else {
			g.drawImage(restartOff, frame.getWidth() / 2 - (restartOff.getWidth()) / 2, 270, restartOff.getWidth(), restartOff.getHeight(), null);
		}
	}

	private void renderOptions() {
		if (mouseIn(frame.getWidth() / 2 - (optionsOn.getWidth()) / 2, frame.getWidth() / 2 - (optionsOn.getWidth()) / 2 + optionsOn.getWidth(), 340, 340 + optionsOn.getHeight())) {
			g.drawImage(optionsOn, frame.getWidth() / 2 - (optionsOn.getWidth()) / 2, 330, optionsOn.getWidth(), optionsOn.getHeight(), null);
			if (InputHandler.MousePressed == 1) {
				clickCheck();
				Main.game.getSound().playButton();
				new OptionsGui();
			}
		} else {
			g.drawImage(optionsOff, frame.getWidth() / 2 - (optionsOff.getWidth()) / 2, 340, optionsOff.getWidth(), optionsOff.getHeight(), null);
		}
	}

	private void renderQuit() {
		if (mouseIn(frame.getWidth() / 2 - (quitOn.getWidth()) / 2, frame.getWidth() / 2 - (quitOn.getWidth()) / 2 + quitOn.getWidth(), 410, 410 + quitOn.getHeight())) {
			g.drawImage(quitOn, frame.getWidth() / 2 - (quitOn.getWidth()) / 2, 400, quitOn.getWidth(), quitOn.getHeight(), null);
			if (InputHandler.MousePressed == 1) {
				if (time > 0)
					setRankings();
				clickCheck();
				Main.game.getSound().playButton();
				Main.game.getSound().stopFootstep();
				Main.game.getSound().stopWin();
				frame.dispose();
				new MainMenuGui();
				stop();
			}
		} else {
			g.drawImage(quitOff, frame.getWidth() / 2 - (quitOff.getWidth()) / 2, 410, quitOff.getWidth(), quitOff.getHeight(), null);
		}
	}

	private void loadImages() {
		try {
			resumeOff = ImageIO.read(Display.class.getResource("/textures/resumeOff.png"));
			resumeOn = ImageIO.read(Display.class.getResource("/textures/resumeOn.png"));
			restartOff = ImageIO.read(Display.class.getResource("/textures/restartOff.png"));
			restartOn = ImageIO.read(Display.class.getResource("/textures/restartOn.png"));
			optionsOff = ImageIO.read(Display.class.getResource("/textures/optionsOff.png"));
			optionsOn = ImageIO.read(Display.class.getResource("/textures/optionsOn.png"));
			quitOff = ImageIO.read(Display.class.getResource("/textures/quitOff.png"));
			quitOn = ImageIO.read(Display.class.getResource("/textures/quitOn.png"));
			filter = ImageIO.read(Display.class.getResource("/textures/filter.png"));
			cursor = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void fps() {
		currentTime = System.nanoTime();
		passedTime = currentTime - previousTime;
		previousTime = currentTime;
		unprossesedSeconds += passedTime / 1000000000.0;
		while (unprossesedSeconds > 1 / 60.0) {
			tick();
			unprossesedSeconds -= 1 / 60.0;
			ticked = true;
			if (!Main.game.isPause())
				tickCount++;
			if (tickCount % 60 == 0) {
				fps = frames;
				previousTime += 1000;
				frames = 0;
			}
		}
	}

	private void timer() {
		adjustColor();
		time = fullTime - tickCount / 60;
		g.drawString(Integer.toString(time / 60) + ":" + Integer.toString(time % 60), 15, 30);
		if (time < 10 && time == tempTime) {
			Main.game.getSound().playBeep();
			tempTime--;
		}
		if (time == 0) {
			play = true;
			Main.game.setFinish(true);
			tempTime = 9;
		}
	}

	private void countdownTimer() {
		g.setFont(new Font("Verdana", 3, 100));
		g.setColor(Color.red);
		if ((3 - tickCount / 60) > 0) {
			g.drawString(Integer.toString(3 - tickCount / 60), (int) (frame.getWidth() - g.getFontMetrics().getStringBounds(Integer.toString(3 - tickCount / 60), g).getWidth()) / 2, frame.getHeight()/2);
			if (tickCount / 60 == tempTick) {
				Main.game.getSound().playBeep();
				tempTick++;
			}
		} else {
			g.setColor(Color.green);
			g.getFontMetrics().getStringBounds("GO!", g);
			g.drawString("GO!", (int) (frame.getWidth() - g.getFontMetrics().getStringBounds("GO!", g).getWidth()) / 2, frame.getHeight()/2);
			if (tempTick == 3) {
				Main.game.getSound().playBeep();
				tempTick++;
			}
		}
		if ((3 - tickCount / 60) == -1) {
			Main.game.setCountdown(false);
			tickCount = 0;
			tempTick = 0;
		}
	}

	private void adjustColor() {
		if (time < 10)
			g.setColor(Color.red);
		else if (Main.game.isFinish())
			g.setColor(Color.green);
		else
			g.setColor(Color.orange);
	}

	private void setRankings() {
		rankings = new int[3];
		for (int i = 0; i < 3; i++)
			rankings[i] = Integer.parseInt(level.getRankings().get(i).substring(level.getRankings().get(i).indexOf(" ") + 2, level.getRankings().get(i).indexOf(":") - 1)) * 60 + Integer.parseInt((level.getRankings().get(i).substring(level.getRankings().get(i).indexOf(":") + 2, level.getRankings().get(i).length())));
		if (time > rankings[0]) {
			level.getRankings().remove(2);
			level.getRankings().add(2, level.getRankings().get(1));
			level.getRankings().remove(1);
			level.getRankings().add(1, level.getRankings().get(0));
			level.getRankings().remove(0);
			level.getRankings().add(0, AccountGui.Username + "  " + Integer.toString(time / 60) + " : " + Integer.toString(time % 60));
			level.setMinBestTime(time / 60);
			level.setSecBestTime(time % 60);
		} else if (time > rankings[1]) {
			level.getRankings().remove(2);
			level.getRankings().add(2, level.getRankings().get(1));
			level.getRankings().remove(1);
			level.getRankings().add(1, AccountGui.Username + "  " + Integer.toString(time / 60) + " : " + Integer.toString(time % 60));
		} else if (time > rankings[2]) {
			level.getRankings().remove(2);
			level.getRankings().add(2, AccountGui.Username + "  " + Integer.toString(time / 60) + " : " + Integer.toString(time % 60));
		}
		new LevelSerialization(level);
	}

	private boolean mouseIn(int xS, int xF, int yS, int yF) {
		if (InputHandler.MouseX <= xS)
			return false;
		if (InputHandler.MouseX >= xF)
			return false;
		if (InputHandler.MouseY <= yS)
			return false;
		if (InputHandler.MouseY >= yF)
			return false;
		return true;
	}

	private void clickCheck() {
		if (InputHandler.MousePressed == 1)
			InputHandler.MousePressed = -1;
		if (InputHandler.MousePressed == 3)
			InputHandler.MousePressed = -1;
	}
}
