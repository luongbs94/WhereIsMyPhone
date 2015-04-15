package com.ln.whereismyphone;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.SystemClock;

public class FlashAlert implements Runnable {

	private Camera camera = null;
	public volatile boolean done;
	private boolean isFlashOn;
	private boolean mFinished;
	private Parameters params;
	private int repeat;
	private int time_off;
	private int time_on;

	public FlashAlert(int i, int j, int k) {
		isFlashOn = false;
		mFinished = false;
		done = true;
		time_on = 200;
		time_off = 200;
		repeat = 0;

		time_on = i;
		time_off = j;
		repeat = k;

		getCamera();
	}

	private void getCamera() {
		if (camera != null) {
			return;
		}

		if (camera == null) {
			try {
				camera = Camera.open();
				params = camera.getParameters();
				camera.startPreview();
			} catch (RuntimeException runtimeexception) {

			}

		}

		return;

	}

	public boolean isRunning() {
		return !mFinished;
	}

	public void run() {

		if (repeat == 0) {
			while (!mFinished ) {
				turnOnFlash();
				SystemClock.sleep(time_on);
				turnOffFlash();
				SystemClock.sleep(time_off);
			}

		} else {
			int i = 0;
			while (!mFinished && i < repeat) {
				turnOnFlash();
				SystemClock.sleep(time_on);
				turnOffFlash();
				SystemClock.sleep(time_off);
				i ++;
			}
		}

		turnOffFlash();
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}

		return;

	}

	public void stop() {
		mFinished = true;
	}

	public void turnOffFlash() {
		if (!isFlashOn || camera == null || params == null) {
			return;
		} else {
			params.setFlashMode("off");
			camera.setParameters(params);
			isFlashOn = false;

		}
	}

	public void turnOnFlash() {
		if (isFlashOn || camera == null || params == null) {
			return;
		} else {
			params.setFlashMode("torch");
			camera.setParameters(params);
			isFlashOn = true;
			return;
		}
	}
}
