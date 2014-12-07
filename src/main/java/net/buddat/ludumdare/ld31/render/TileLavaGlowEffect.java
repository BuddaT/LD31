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
		
		glowColor = new Color(ColorDirector.getSecondary(0));
		backColor = new Color(ColorDirector.getPrimary(0));
	}

	public void render(Graphics g, int levelXPos) {
		int scaledX = Level.getScaledX(levelXPos, getX());
		int scaledWidth = Level.getScaledX(levelXPos, getX() + 1) - scaledX;

		glowColor.a = getRemaining();
		g.setColor(glowColor);
		g.fillRect(scaledX + scaledWidth / 4, getY() * Constants.TILE_WIDTH
				+ Constants.TILE_WIDTH / 4, scaledWidth / 2,
				Constants.TILE_WIDTH / 2);

		backColor.a = getRemaining();
		g.setColor(backColor);
		g.drawRect(scaledX + scaledWidth / 4, getY() * Constants.TILE_WIDTH
				+ Constants.TILE_WIDTH / 4, scaledWidth / 2,
				Constants.TILE_WIDTH / 2);
	}

	@Override
	public void render(Graphics g) {
		// Do nothing
	}

}
