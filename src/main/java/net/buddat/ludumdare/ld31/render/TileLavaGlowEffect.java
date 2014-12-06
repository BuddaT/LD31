package net.buddat.ludumdare.ld31.render;

import net.buddat.ludumdare.ld31.ColorDirector;
import net.buddat.ludumdare.ld31.constants.Constants;
import net.buddat.ludumdare.ld31.world.Level;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class TileLavaGlowEffect extends TileEffect {

	private final Color glowColor, backColor;

	public TileLavaGlowEffect(int tileX, int tileY, int duration) {
		super(tileX, tileY, duration);
		
		glowColor = new Color(ColorDirector.getPrimary(0));
		backColor = new Color(255, 255, 255);
	}

	public void render(Graphics g, int playerX) {
		int scaledX = Level.getScaledX(playerX, getX());
		int scaledWidth = Level.getScaledX(playerX, getX() + 1) - scaledX;

		backColor.a = 1.0f - getRemaining() * 2;
		g.setColor(backColor);
		g.fillOval(scaledX + scaledWidth / 6, getY() * Constants.TILE_WIDTH
				+ Constants.TILE_WIDTH / 6, scaledWidth / 1.5f,
				Constants.TILE_WIDTH / 1.5f);

		glowColor.a = 1.0f - getRemaining() * 2;
		g.setColor(glowColor);
		g.fillOval(scaledX + scaledWidth / 4, getY() * Constants.TILE_WIDTH
				+ Constants.TILE_WIDTH / 4, scaledWidth / 2,
				Constants.TILE_WIDTH / 2);
	}

	@Override
	public void render(Graphics g) {
		// Do nothing
	}

}
