package net.buddat.ludumdare.ld31.render;

import net.buddat.ludumdare.ld31.constants.Constants;
import net.buddat.ludumdare.ld31.music.MusicDirector;
import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;

/**
 * Created with IntelliJ IDEA. User: taufiq Date: 8/12/14 Time: 11:50 AM To change this template use File | Settings |
 * File Templates.
 */
public class Volume {
	private static final String VOLUME_ICON = "volume.png";
	private static final String FONT_NAME = "NirmalaS.ttf";

	private static final int VOLUME_DIVISOR = 10;
	private static final int VOLUME_INCREMENT = MusicDirector.MAX_VOLUME / VOLUME_DIVISOR;
	private static final int VOLUME_BAR_WIDTH = 2;
	private static final int BUFFER = 5;
	private static final String DECREASE_TEXT = "-";
	private static final String INCREASE_TEXT = "+";

	private final Image volumeImage;
	private final MusicDirector musicDirector;
	private final int width;
	private final int increaseX;
	private final int volumeX;
	private final int volumeImageX;
	private final int decreaseX;
	private UnicodeFont volumeFont;

	public Volume(MusicDirector musicDirector) throws SlickException {
		volumeImage = new Image(VOLUME_ICON);
		volumeFont = new UnicodeFont(FONT_NAME, 16, false, false);
		volumeFont.addAsciiGlyphs();
		volumeFont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
		volumeFont.loadGlyphs();
		this.musicDirector = musicDirector;
		this.width = volumeFont.getWidth(DECREASE_TEXT) + BUFFER + volumeImage.getWidth() + BUFFER +
			VOLUME_DIVISOR * (VOLUME_BAR_WIDTH + BUFFER) + BUFFER + volumeFont.getWidth(INCREASE_TEXT);
		this.increaseX = Constants.GAME_WIDTH - BUFFER - volumeFont.getWidth(INCREASE_TEXT);
		this.volumeX = increaseX - BUFFER - VOLUME_DIVISOR * (VOLUME_BAR_WIDTH + BUFFER);
		this.volumeImageX = volumeX - BUFFER - volumeImage.getWidth();
		this.decreaseX = volumeImageX - BUFFER - volumeFont.getWidth(DECREASE_TEXT);
	}

	public void render(Graphics g) {
		volumeFont.drawString(decreaseX, 0, DECREASE_TEXT);
		g.drawImage(volumeImage, volumeImageX, BUFFER);
		int volume = musicDirector.getVolume();
		g.setColor(Color.white);
		for (int i = VOLUME_INCREMENT; i <= volume; i += VOLUME_INCREMENT) {
			int xOffset = (i / VOLUME_INCREMENT - 1) * (VOLUME_BAR_WIDTH + BUFFER);
			g.fillRect(volumeX + xOffset, BUFFER, VOLUME_BAR_WIDTH, volumeImage.getHeight());
		}
		volumeFont.drawString(increaseX, 0, INCREASE_TEXT);
	}

	private int calcWidth() {
		return volumeFont.getWidth("-") + BUFFER + volumeImage.getWidth() + BUFFER + VOLUME_DIVISOR * (VOLUME_BAR_WIDTH + BUFFER) + BUFFER;
	}

	public void increaseVolume() {
		musicDirector.increaseVolume();
		System.out.println("Increased volume to " + musicDirector.getVolume());
	}

	public void decreaseVolume() {
		musicDirector.decreaseVolume();
		System.out.println("Decreased volume to " + musicDirector.getVolume());
	}
}
