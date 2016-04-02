package cs301.jonesd14_hw2;

import android.graphics.Color;
import android.widget.SeekBar;

public class SeekBarHandler implements SeekBar.OnSeekBarChangeListener{

	//a number which will denote the seekbar being modified
	private int seekBarNum;
	private GridSurfaceView gridSurfaceView;

	public SeekBarHandler(int num, GridSurfaceView gsv){
		seekBarNum = num;
		gridSurfaceView = gsv;
	}
	
	/**
	 * onProgressChanged()
	 * 
	 * The overridden method which will change the square colors when the 
	 * seekBars are changed.
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean arg2) {
		//if the first (RED) seekBar if being changed
		if(seekBarNum == 1){
			gridSurfaceView.onColor.setColor(Color.rgb(progress, gridSurfaceView.greenValue, 
												gridSurfaceView.blueValue));
			gridSurfaceView.invalidate();
		}
		//if the second (GREEN) seekBar is being changed
		else if(seekBarNum == 2){
			gridSurfaceView.onColor.setColor(Color.rgb(gridSurfaceView.redValue, progress, 
												gridSurfaceView.blueValue));
			gridSurfaceView.invalidate();
		}
		//if the third (BLUE) seekBar is being changed
		else if(seekBarNum == 3){
			gridSurfaceView.onColor.setColor(Color.rgb(gridSurfaceView.redValue, gridSurfaceView.greenValue, 
					progress));
			gridSurfaceView.invalidate();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {}

}
