package net.buddat.ludumdare.ld31.render;

import org.newdawn.slick.Graphics;

public interface Effect {

	public void render(Graphics g);

	public void update(int delta);

	public boolean hasExpired();

}
