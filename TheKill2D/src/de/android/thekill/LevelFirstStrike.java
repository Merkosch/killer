package de.android.thekill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class LevelFirstStrike implements TheKillLevel{
	
    private static final int LAYER_RACETRACK = 0;
    private static final int LAYER_BORDERS = LAYER_RACETRACK + 1;
    private static final int LAYER_CARS = LAYER_BORDERS + 1;
    private static final int LAYER_OBSTACLES = LAYER_CARS + 1;
    
    private static final int LEVEL_WIDTH = 512;
    private static final int LEVEL_HEIGHT = 512;
    
    private Texture 			mBoxTexture;
    private TextureRegion 		mBoxTextureRegion;

    private Texture 			mLevelTexture;
    private TextureRegion 		mLevelTextureRegion;
    
    private PhysicsWorld 		mPhysicsWorld;
    private List<Texture> 		textureList;

	
	public LevelFirstStrike() {
		this.textureList = new ArrayList<Texture>();
	}

	public List<Texture> loadRessources(BaseGameActivity parent){
        TextureRegionFactory.setAssetBasePath("gfx/");
        
        this.mLevelTexture = new Texture(512, 512, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
        this.mLevelTextureRegion = TextureRegionFactory.createFromAsset(this.mLevelTexture, parent, "levelfirststrike.png", 0, 0);
        this.textureList.add(mLevelTexture);
        
        this.mBoxTexture = new Texture(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mBoxTextureRegion = TextureRegionFactory.createFromAsset(this.mBoxTexture, parent, "box.png", 0, 0);
        this.textureList.add(mBoxTexture);
 
        return this.textureList;
	}
	
	public void initScene(Scene scene, PhysicsWorld _mPhysicsWorld){
		this.mPhysicsWorld = _mPhysicsWorld;
        this.initLevel(scene);
        this.initLevelBorders(scene);
        this.addSprites(scene);
	}

	private void addSprites(final Scene pScene) {
	        addObstacle(pScene, 100, 100);
	        addObstacle(pScene, 120, 120);
	        addObstacle(pScene, 200, 200);
	        addObstacle(pScene, 300, 300);
	}
	
	public void addSprite(final Scene pScene, int x, int y){
		this.addObstacle(pScene, x, y);
	}

	private void addObstacle(final Scene pScene, final float pX, final float pY) {
	        final Sprite box = new Sprite(pX, pY, 32, 32, this.mBoxTextureRegion);
	        
	        final FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(0.1f, 0.5f, 0.5f);
	        final Body boxBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, box, BodyType.DynamicBody, boxFixtureDef);
	        boxBody.setLinearDamping(10);
	        boxBody.setAngularDamping(10);
	        
	        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(box, boxBody, true, true));
	
	        pScene.getChild(LAYER_OBSTACLES).attachChild(box);
	}

	private void initLevel(final Scene pScene) {
	        final IEntity levelEntity = pScene.getChild(LAYER_RACETRACK);
	        levelEntity.attachChild(new Sprite(0, 0, LEVEL_WIDTH, LEVEL_HEIGHT, mLevelTextureRegion));
	        levelEntity.attachChild(new Sprite(LEVEL_WIDTH, 0, LEVEL_WIDTH, LEVEL_HEIGHT, mLevelTextureRegion));
	}

	private void initLevelBorders(final Scene pScene) {
		final Shape topOuter = new Rectangle(	0, 				   0, 				 LEVEL_WIDTH*2, 	 2);
	    final Shape bottomOuter = new Rectangle(0, 				   LEVEL_HEIGHT - 2, LEVEL_WIDTH*2, 	 2);
	    final Shape leftOuter = new Rectangle(	0, 				   0, 				 2, 				 LEVEL_HEIGHT);
	    final Shape rightOuter = new Rectangle(	LEVEL_WIDTH*2 - 2, 0, 				 2, 				 LEVEL_HEIGHT);
	
	    final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
	    PhysicsFactory.createBoxBody(this.mPhysicsWorld, bottomOuter, BodyType.StaticBody, wallFixtureDef);
	    PhysicsFactory.createBoxBody(this.mPhysicsWorld, topOuter, BodyType.StaticBody, wallFixtureDef);
	    PhysicsFactory.createBoxBody(this.mPhysicsWorld, leftOuter, BodyType.StaticBody, wallFixtureDef);
	    PhysicsFactory.createBoxBody(this.mPhysicsWorld, rightOuter, BodyType.StaticBody, wallFixtureDef);
	
	    final IEntity firstChild = pScene.getChild(LAYER_BORDERS);
	    firstChild.attachChild(bottomOuter);
	    firstChild.attachChild(topOuter);
	    firstChild.attachChild(leftOuter);
	    firstChild.attachChild(rightOuter);
	}

	@Override
	public int getLevelWidth() {
		return LEVEL_WIDTH;
	}

	@Override
	public int getLevelHeight() {
		return LEVEL_HEIGHT;
	}

}
