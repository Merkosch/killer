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

public class LevelTestLevel implements TheKillLevel{
	
    private static final int RACETRACK_WIDTH = 64;
    private static final int LEVEL_WIDTH = 800;
    private static final int LEVEL_HEIGHT = 480;

    private static final int OBSTACLE_SIZE = 16;
    private static final int CAR_SIZE = 16;

    private static final int CAMERA_WIDTH = RACETRACK_WIDTH * 5;
    private static final int CAMERA_HEIGHT = RACETRACK_WIDTH * 3;

    private static final int LAYER_RACETRACK = 0;
    private static final int LAYER_BORDERS = LAYER_RACETRACK + 1;
    private static final int LAYER_CARS = LAYER_BORDERS + 1;
    private static final int LAYER_OBSTACLES = LAYER_CARS + 1;
    
    private Texture 			mBoxTexture;
    private TextureRegion 		mBoxTextureRegion;

    private Texture 			mRacetrackTexture;
    private TextureRegion 		mRacetrackStraightTextureRegion;
    private TextureRegion 		mRacetrackCurveTextureRegion;
    
    private PhysicsWorld 		mPhysicsWorld;
    private List<Texture> 		textureList;

	
	public LevelTestLevel() {
		this.textureList = new ArrayList<Texture>();
	}

	public List<Texture> loadRessources(BaseGameActivity parent){
        TextureRegionFactory.setAssetBasePath("gfx/");
        
        this.mRacetrackTexture = new Texture(128, 256, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
        this.mRacetrackStraightTextureRegion = TextureRegionFactory.createFromAsset(this.mRacetrackTexture, parent, "racetrack_straight.png", 0, 0);
        this.mRacetrackCurveTextureRegion = TextureRegionFactory.createFromAsset(this.mRacetrackTexture, parent, "racetrack_curve.png", 0, 128);
        this.textureList.add(mRacetrackTexture);
        
        this.mBoxTexture = new Texture(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mBoxTextureRegion = TextureRegionFactory.createFromAsset(this.mBoxTexture, parent, "box.png", 0, 0);
        this.textureList.add(mBoxTexture);
        
        return this.textureList;
	}
	
	public void initScene(Scene scene, PhysicsWorld _mPhysicsWorld){
		this.mPhysicsWorld = _mPhysicsWorld;
        this.initRacetrack(scene);
        this.initRacetrackBorders(scene);
        this.initObstacles(scene);
	}

	private void initObstacles(final Scene pScene) {
	        addObstacle(pScene, CAMERA_WIDTH / 2, RACETRACK_WIDTH / 2);
	        addObstacle(pScene, CAMERA_WIDTH / 2, RACETRACK_WIDTH / 2);
	        addObstacle(pScene, CAMERA_WIDTH / 2, CAMERA_HEIGHT - RACETRACK_WIDTH / 2);
	        addObstacle(pScene, CAMERA_WIDTH / 2, CAMERA_HEIGHT - RACETRACK_WIDTH / 2);
	}

	private void addObstacle(final Scene pScene, final float pX, final float pY) {
	        final Sprite box = new Sprite(pX, pY, OBSTACLE_SIZE, OBSTACLE_SIZE, this.mBoxTextureRegion);
	        
	        final FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(0.1f, 0.5f, 0.5f);
	        final Body boxBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, box, BodyType.DynamicBody, boxFixtureDef);
	        boxBody.setLinearDamping(10);
	        boxBody.setAngularDamping(10);
	        
	        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(box, boxBody, true, true));
	
	        pScene.getChild(LAYER_OBSTACLES).attachChild(box);
	}

	private void initRacetrack(final Scene pScene) {
	        final IEntity racetrackEntity = pScene.getChild(LAYER_RACETRACK);
	
	        /* Straights. */
	        {
	                final TextureRegion racetrackHorizontalStraightTextureRegion = this.mRacetrackStraightTextureRegion.clone();
	                racetrackHorizontalStraightTextureRegion.setWidth(3 * this.mRacetrackStraightTextureRegion.getWidth());
	
	                final TextureRegion racetrackVerticalStraightTextureRegion = this.mRacetrackStraightTextureRegion;
	
	                /* Top Straight */
	                racetrackEntity.attachChild(new Sprite(RACETRACK_WIDTH, 0, 3 * RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackHorizontalStraightTextureRegion));
	                /* Bottom Straight */
	                racetrackEntity.attachChild(new Sprite(RACETRACK_WIDTH, CAMERA_HEIGHT - RACETRACK_WIDTH, 3 * RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackHorizontalStraightTextureRegion));
	
	                /* Left Straight */
	                final Sprite leftVerticalStraight = new Sprite(0, RACETRACK_WIDTH, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackVerticalStraightTextureRegion);
	                leftVerticalStraight.setRotation(90);
	                racetrackEntity.attachChild(leftVerticalStraight);
	                /* Right Straight */
	                final Sprite rightVerticalStraight = new Sprite(CAMERA_WIDTH - RACETRACK_WIDTH, RACETRACK_WIDTH, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackVerticalStraightTextureRegion);
	                rightVerticalStraight.setRotation(90);
	                racetrackEntity.attachChild(rightVerticalStraight);
	        }
	
	        /* Edges */
	        {
	                final TextureRegion racetrackCurveTextureRegion = this.mRacetrackCurveTextureRegion;
	
	                /* Upper Left */
	                final Sprite upperLeftCurve = new Sprite(0, 0, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackCurveTextureRegion);
	                upperLeftCurve.setRotation(90);
	                racetrackEntity.attachChild(upperLeftCurve);
	
	                /* Upper Right */
	                final Sprite upperRightCurve = new Sprite(CAMERA_WIDTH - RACETRACK_WIDTH, 0, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackCurveTextureRegion);
	                upperRightCurve.setRotation(180);
	                racetrackEntity.attachChild(upperRightCurve);
	
	                /* Lower Right */
	                final Sprite lowerRightCurve = new Sprite(CAMERA_WIDTH - RACETRACK_WIDTH, CAMERA_HEIGHT - RACETRACK_WIDTH, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackCurveTextureRegion);
	                lowerRightCurve.setRotation(270);
	                racetrackEntity.attachChild(lowerRightCurve);
	
	                /* Lower Left */
	                final Sprite lowerLeftCurve = new Sprite(0, CAMERA_HEIGHT - RACETRACK_WIDTH, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackCurveTextureRegion);
	                racetrackEntity.attachChild(lowerLeftCurve);
	        }
	}

	private void initRacetrackBorders(final Scene pScene) {
	        final Shape bottomOuter = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2);
	        final Shape topOuter = new Rectangle(0, 0, CAMERA_WIDTH, 2);
	        final Shape leftOuter = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
	        final Shape rightOuter = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);
	
	        final Shape bottomInner = new Rectangle(RACETRACK_WIDTH, CAMERA_HEIGHT - 2 - RACETRACK_WIDTH, CAMERA_WIDTH - 2 * RACETRACK_WIDTH, 2);
	        final Shape topInner = new Rectangle(RACETRACK_WIDTH, RACETRACK_WIDTH, CAMERA_WIDTH - 2 * RACETRACK_WIDTH, 2);
	        final Shape leftInner = new Rectangle(RACETRACK_WIDTH, RACETRACK_WIDTH, 2, CAMERA_HEIGHT - 2 * RACETRACK_WIDTH);
	        final Shape rightInner = new Rectangle(CAMERA_WIDTH - 2 - RACETRACK_WIDTH, RACETRACK_WIDTH, 2, CAMERA_HEIGHT - 2 * RACETRACK_WIDTH);
	
	        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
	        PhysicsFactory.createBoxBody(this.mPhysicsWorld, bottomOuter, BodyType.StaticBody, wallFixtureDef);
	        PhysicsFactory.createBoxBody(this.mPhysicsWorld, topOuter, BodyType.StaticBody, wallFixtureDef);
	        PhysicsFactory.createBoxBody(this.mPhysicsWorld, leftOuter, BodyType.StaticBody, wallFixtureDef);
	        PhysicsFactory.createBoxBody(this.mPhysicsWorld, rightOuter, BodyType.StaticBody, wallFixtureDef);
	
	        PhysicsFactory.createBoxBody(this.mPhysicsWorld, bottomInner, BodyType.StaticBody, wallFixtureDef);
	        PhysicsFactory.createBoxBody(this.mPhysicsWorld, topInner, BodyType.StaticBody, wallFixtureDef);
	        PhysicsFactory.createBoxBody(this.mPhysicsWorld, leftInner, BodyType.StaticBody, wallFixtureDef);
	        PhysicsFactory.createBoxBody(this.mPhysicsWorld, rightInner, BodyType.StaticBody, wallFixtureDef);
	
	        final IEntity firstChild = pScene.getChild(LAYER_BORDERS);
	        firstChild.attachChild(bottomOuter);
	        firstChild.attachChild(topOuter);
	        firstChild.attachChild(leftOuter);
	        firstChild.attachChild(rightOuter);
	
	        firstChild.attachChild(bottomInner);
	        firstChild.attachChild(topInner);
	        firstChild.attachChild(leftInner);
	        firstChild.attachChild(rightInner);
	}

	public int getLevelWidth() {
		return LEVEL_WIDTH;
	}

	public int getLevelHeight() {
		return LEVEL_HEIGHT;
	}

	public void addSprite(Scene pScene, int x, int y) {
		// TODO Auto-generated method stub
		
	}

}
