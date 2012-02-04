package de.android.thekill;

import java.util.List;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public interface TheKillLevel {
	public List<Texture> loadRessources(BaseGameActivity parent);
	public void initScene(Scene mScene, PhysicsWorld mPhysicsWorld);
	public int getLevelWidth();
	public int getLevelHeight();
	public void addSprite(final Scene pScene, int x, int y);
}
