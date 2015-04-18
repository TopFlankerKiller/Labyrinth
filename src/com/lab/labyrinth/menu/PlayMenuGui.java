package com.lab.labyrinth.menu;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.lab.labyrinth.Main;
import com.lab.labyrinth.input.InputHandler;
import com.lab.labyrinth.launcher.LauncherGui;

public class PlayMenuGui extends Canvas {

	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private MainMenuGui menu;
	private BufferedImage standardLvlBtnOn, standardLvlBtnOff, customLvlBtnOn, customLvlBtnOff, rankingsBtnOn, rankingsBtnOff, backBtnOn, backBtnOff, logo;

	public PlayMenuGui(MainMenuGui menu) {
		this.menu = menu;
		frame = this.menu.getFrame();
		loadImages();
	}

	public void renderPlayMenu(Graphics g) {
		g.drawImage(logo, frame.getWidth() / 2 - logo.getWidth() / 2 / 2, 50, logo.getWidth() / 2, logo.getHeight() / 2, null);
		renderStandardLevels(g);
		renderCustomLevels(g);
		renderRankings(g);
		renderBack(g);
	}

	private void renderStandardLevels(Graphics g) {
		if (mouseIn(frame.getWidth() / 2 - standardLvlBtnOn.getWidth() / 2, frame.getWidth() / 2 - standardLvlBtnOn.getWidth() / 2 + standardLvlBtnOn.getWidth(), 240, 240 + standardLvlBtnOn.getHeight())) {
			g.drawImage(standardLvlBtnOn, frame.getWidth() / 2 - standardLvlBtnOn.getWidth() / 2, 240, standardLvlBtnOn.getWidth(), standardLvlBtnOn.getHeight(), null);
			g.drawImage(standardLvlBtnOff, frame.getWidth() / 2 - standardLvlBtnOff.getWidth() / 2, 250, standardLvlBtnOff.getWidth(), standardLvlBtnOff.getHeight(), null);
			if (InputHandler.MousePressed == 1) {
				clickCheck();
				Main.game.getSound().playButton();
				menu.setChoice(1);
			}
		} else {
			g.drawImage(standardLvlBtnOff, frame.getWidth() / 2 - standardLvlBtnOff.getWidth() / 2, 250, standardLvlBtnOff.getWidth(), standardLvlBtnOff.getHeight(), null);
		}
	}

	private void renderCustomLevels(Graphics g) {
		if (mouseIn(frame.getWidth() / 2 - customLvlBtnOn.getWidth() / 2, frame.getWidth() / 2 - customLvlBtnOn.getWidth() / 2 + customLvlBtnOn.getWidth(), 310, 310 + customLvlBtnOn.getHeight())) {
			g.drawImage(customLvlBtnOn, frame.getWidth() / 2 - customLvlBtnOn.getWidth() / 2, 310, customLvlBtnOn.getWidth(), customLvlBtnOn.getHeight(), null);
			g.drawImage(customLvlBtnOff, frame.getWidth() / 2 - customLvlBtnOff.getWidth() / 2, 320, customLvlBtnOff.getWidth(), customLvlBtnOff.getHeight(), null);
			if (InputHandler.MousePressed == 1) {
				clickCheck();
				Main.game.getSound().playButton();
				menu.setChoice(2);
			}
		} else {
			g.drawImage(customLvlBtnOff, frame.getWidth() / 2 - customLvlBtnOff.getWidth() / 2, 320, customLvlBtnOff.getWidth(), customLvlBtnOff.getHeight(), null);
		}
	}

	private void renderRankings(Graphics g) {
		if (mouseIn(frame.getWidth() / 2 - rankingsBtnOn.getWidth() / 2, frame.getWidth() / 2 - rankingsBtnOn.getWidth() / 2 + rankingsBtnOn.getWidth(), 380, 380 + rankingsBtnOn.getHeight())) {
			g.drawImage(rankingsBtnOn, frame.getWidth() / 2 - rankingsBtnOn.getWidth() / 2, 380, rankingsBtnOn.getWidth(), rankingsBtnOn.getHeight(), null);
			g.drawImage(rankingsBtnOff, frame.getWidth() / 2 - rankingsBtnOff.getWidth() / 2, 390, rankingsBtnOff.getWidth(), rankingsBtnOff.getHeight(), null);
			if (InputHandler.MousePressed == 1) {
				clickCheck();
				Main.game.getSound().playButton();
				menu.setChoice(3);
			}
		} else {
			g.drawImage(rankingsBtnOff, frame.getWidth() / 2 - rankingsBtnOff.getWidth() / 2, 390, rankingsBtnOff.getWidth(), rankingsBtnOff.getHeight(), null);
		}
	}

	private void renderBack(Graphics g) {
		if (mouseIn(frame.getWidth() / 2 - backBtnOn.getWidth() / 2, frame.getWidth() / 2 - backBtnOn.getWidth() / 2 + backBtnOn.getWidth(), 450, 450 + backBtnOn.getHeight())) {
			g.drawImage(backBtnOn, frame.getWidth() / 2 - backBtnOn.getWidth() / 2, 450, backBtnOn.getWidth(), backBtnOn.getHeight(), null);
			g.drawImage(backBtnOff, frame.getWidth() / 2 - backBtnOff.getWidth() / 2, 460, backBtnOff.getWidth(), backBtnOff.getHeight(), null);
			if (InputHandler.MousePressed == 1) {
				clickCheck();
				Main.game.getSound().playButton();
				new LauncherGui();
				frame.dispose();
			}
		} else {
			g.drawImage(backBtnOff, frame.getWidth() / 2 - backBtnOff.getWidth() / 2, 460, backBtnOff.getWidth(), backBtnOff.getHeight(), null);
		}
	}

	private void loadImages() {
		try {
			logo = ImageIO.read(MainMenuGui.class.getResource("/textures/logo.png"));
			standardLvlBtnOn = ImageIO.read(MainMenuGui.class.getResource("/textures/standardLevels.png"));
			customLvlBtnOn = ImageIO.read(MainMenuGui.class.getResource("/textures/customLevels.png"));
			rankingsBtnOn = ImageIO.read(MainMenuGui.class.getResource("/textures/rankings.png"));
			backBtnOn = ImageIO.read(MainMenuGui.class.getResource("/textures/back.png"));
			standardLvlBtnOff = ImageIO.read(MainMenuGui.class.getResource("/textures/standardLevelsOff.png"));
			customLvlBtnOff = ImageIO.read(MainMenuGui.class.getResource("/textures/customLevelsOff.png"));
			rankingsBtnOff = ImageIO.read(MainMenuGui.class.getResource("/textures/rankingsOff.png"));
			backBtnOff = ImageIO.read(MainMenuGui.class.getResource("/textures/backOff.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
