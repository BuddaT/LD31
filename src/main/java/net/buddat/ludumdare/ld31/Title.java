package net.buddat.ludumdare.ld31;

import net.buddat.ludumdare.ld31.constants.Constants;
import net.buddat.ludumdare.ld31.world.Level;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class Title {

	private static final int MOVE_PER_BEAT = Constants.TILE_WIDTH * 3;

	private static final int MAX_ITEMS = 1;

	public static final int START = 0, QUIT = 1;

	private Image titleImage;

	private UnicodeFont textFont;

	private int selectedItem = 0;

	private boolean startMoving = false;

	private int xPos = 0;

	public Title() {
		try {
			titleImage = new Image("levels/title.png");

			textFont = new UnicodeFont("Inversionz.otf", 96, false, false);
			textFont.addAsciiGlyphs();
			textFont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
			textFont.loadGlyphs();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public void render(Graphics g) {
		titleImage.draw(xPos, 0);

		for (int i = 0; i <= MAX_ITEMS; i++) {
			textFont.drawString(xPos + 650, 200 + (i * 80), (i == 0 ? "start"
					: " exit"),
					(selectedItem == i ? ColorDirector.getPrimary(0)
							: ColorDirector.getSecondary(0)));
		}
	}

	public void update(int delta, boolean onBeat, Level currentLevel) {
		if (startMoving && onBeat) {
			if (isMovedOut())
				xPos = Level.getScaledX(currentLevel.getXPosition(), 0)
						- titleImage.getWidth();
			else
				xPos -= MOVE_PER_BEAT;

		}
	}

	public void setStartMoving(boolean start) {
		startMoving = start;
	}

	public boolean isMoving() {
		return startMoving;
	}

	public boolean isMovedOut() {
		return titleImage.getWidth() / 2 <= xPos * -1;
	}

	public boolean isVisible() {
		return xPos * -1 < titleImage.getWidth();
	}

	public void setSelected(int delta) {
		selectedItem += delta;

		if (selectedItem < 0)
			selectedItem = 0;
		if (selectedItem > MAX_ITEMS)
			selectedItem = MAX_ITEMS;
	}

	public int getSelected() {
		return selectedItem;
	}
}
