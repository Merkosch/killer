package de.android.thekill;

import java.util.ArrayList;
import java.util.List;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.sprite.TiledSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class CharacterLoader {
	
    private static final int RACETRACK_WIDTH = 64;

    private static final int OBSTACLE_SIZE = 16;
    private static final int CAR_SIZE = 32;

    private static final int CAMERA_WIDTH = RACETRACK_WIDTH * 5;
    private static final int CAMERA_HEIGHT = RACETRACK_WIDTH * 3;

    private static final int LAYER_RACETRACK = 0;
    private static final int LAYER_BORDERS = LAYER_RACETRACK + 1;
    private static final int LAYER_CARS = LAYER_BORDERS + 1;
    private static final int LAYER_OBSTACLES = LAYER_CARS + 1;

    private Texture 			mVehiclesTexture;
    private TiledTextureRegion 	mVehiclesTextureRegion;

    private Texture 			bulletTexture;
    private TextureRegion 		bulletTextureRegion;
	
	private Camera 				mCamera;
    private AnimatedSprite 		mPlayer;
    private Body 				mPlayerBody;
    private int 				characterNumber;
	private PhysicsWorld 		mPhysicsWorld; 

    private List<Texture> 		textureList;

	private float 				controlX;
	private float 				controlY;


	
	public CharacterLoader(Camera _mCamera) {
		this.textureList = new ArrayList<Texture>();
		this.mCamera = _mCamera;
	}
	
	public List<Texture> loadRessources(TheKillActivity parent){
        TextureRegionFactory.setAssetBasePath("gfx/");

        this.mVehiclesTexture = new Texture(512, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mVehiclesTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mVehiclesTexture, parent, "player1.png", 0, 0, 8, 1);
        this.textureList.add(mVehiclesTexture);
        
        this.bulletTexture = new Texture(2, 2, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.bulletTextureRegion = TextureRegionFactory.createFromAsset(this.bulletTexture, parent, "bullet.png", 0, 0);
        this.textureList.add(bulletTexture);
        
        return this.textureList;
	}
	
	public void initCharacter(int _characterNumber, PhysicsWorld _mPhysicsWorld, Scene pScene){
		this.characterNumber = _characterNumber;
		this.mPhysicsWorld = _mPhysicsWorld;	
		this.initCar(pScene, mPhysicsWorld);
	}
	
	private void initCar(final Scene pScene, IUpdateHandler mCamera) {
        this.mPlayer = new AnimatedSprite(20, 20, CAR_SIZE, CAR_SIZE, this.mVehiclesTextureRegion);
        this.mPlayer.setCurrentTileIndex(characterNumber);
//        this.mPlayer.animate(100);
        
        final FixtureDef carFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
        this.mPlayerBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, this.mPlayer, BodyType.DynamicBody, carFixtureDef);
        
        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(this.mPlayer, this.mPlayerBody, true, false));

        pScene.getChild(LAYER_CARS).attachChild(this.mPlayer);
	}

	public AnimatedSprite getCharacter() {
		return mPlayer;
	}

	public Body getmCharacterBody() {
		return mPlayerBody;
	}
	
	public void fireBullet(final Scene pScene){
		
		// koordinaten vom Steuerkreuz
		float x = this.controlX;
		float y = this.controlY;
		
		// Postion vom Charakter
		final float posX = this.mPlayer.getX();
		final float posY = this.mPlayer.getY();
		
		// Die Koordinaten vom Steuerkreuz sind nicht immer maximal sondern auch mal
		// nur leicht angetippt... oder so. 
		// Hier werden die x y Koordinaten normalisiert.
		final float mindestAbstand = 14;
		float abstand = (float)Math.sqrt(Math.pow(12*x, 2)+Math.pow(12*y, 2));
		float faktor  = mindestAbstand / abstand; 
		x = mindestAbstand * x * faktor; 
		y = mindestAbstand * y * faktor; 
		
		// Setzen der Postion. Mittelpunkt des Charakters +16+16 +x+y Kordinaten 
		// welche nach der Normalisierung genau der Radius+2 (14) sein sollten.
		final Bullet bullet = new Bullet( posX + 16 + x , posY + 16 + y ,this.bulletTextureRegion);
		final FixtureDef bulletFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		final Body body   = PhysicsFactory.createBoxBody(this.mPhysicsWorld, bullet, BodyType.DynamicBody, bulletFixtureDef);
        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(bullet, body, true, false));
		
        // Die Beschleunigung von der aktuellen Position in die Sichtrichtung 
        body.setLinearVelocity(Vector2Pool.obtain( x, y ));
		
		pScene.getChild(LAYER_OBSTACLES).attachChild(bullet);
	}

	public void setControlValues(float pValueX, float pValueY) {
		this.controlX = pValueX;
		this.controlY = pValueY;
	}

}
