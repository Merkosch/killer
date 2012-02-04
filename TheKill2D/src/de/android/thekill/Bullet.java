package de.android.thekill;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class Bullet extends Sprite implements IShape{

	public Bullet(float pX, float pY, TextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion);
		
		Bullet.this.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub
			
			}
		});
	}



}
