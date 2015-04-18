package com.lab.labyrinth.graphics;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Texture {
	public static Render floor = loadBitmap("/textures/floor_10.jpg");
	public static Render finish = loadBitmap("/textures/finish.png");
	public static Render wall = loadBitmap("/textures/wall_4.png");

	public static Render loadBitmap(String fileName) {
		try {
			BufferedImage image = ImageIO.read(Texture.class.getResource(fileName));
			int width = image.getWidth();
			int height = image.getHeight();
			Render result = new Render(width, height);
			image.getRGB(0, 0, width, height, result.pixels, 0, width);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
