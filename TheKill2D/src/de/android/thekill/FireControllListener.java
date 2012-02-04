package de.android.thekill;

import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;

import android.util.Log;

public class FireControllListener implements IAnalogOnScreenControlListener{

	private float lastX;
	private float lastY;
	
	@Override
	public void onControlChange(BaseOnScreenControl pBaseOnScreenControl, float pValueX, float pValueY) {
		
		// Nur Feuern, wenn der Controller betätigt ist
		if ((pValueX + pValueY) > 0){			
			// Durch die leider nur halbe Multitouchvariante kann es passieren,dass 
			// durch die Steuerung der Feuerknopf immer noch auf Feuern steht
			// Wenn die Koord. gleich bleiben ist das der Fall. Darum dann hier nicht feuern.
			if ((pValueX + pValueY) != (lastX + lastY)){
				Log.d("TheKill2D", "+++++FIRE+++++" + pValueX + " - " + pValueY);			
				lastX = pValueX; 
				lastY = pValueY;				
			}			
		}
	}

	@Override
	public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl) {
		// TODO Auto-generated method stub
		Log.d("TheKill2D", "+++++Klick+++++");
		
	}

}
