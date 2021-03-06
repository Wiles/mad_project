package ca.setc.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import ca.setc.geocaching.R;
import ca.setc.geocaching.events.PhotoEvent;
import ca.setc.geocaching.events.PhotoListener;

/**
 * Takes a picture
 * 
 * Based on source from: https://github.com/commonsguy/cw-advandroid
 * 
 */
public class TakePictureActivity extends Activity {

	/** The log. */
	private final Logger log = LoggerFactory
			.getLogger(TakePictureActivity.class);
	private static final List<PhotoListener> listeners = new ArrayList<PhotoListener>();

	private SurfaceHolder previewHolder = null;
	private Camera camera = null;
	private boolean inPreview = false;
	private boolean cameraConfigured = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_picture);

		SurfaceView preview = (SurfaceView) findViewById(R.id.preview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			Camera.CameraInfo info = new Camera.CameraInfo();

			for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
				Camera.getCameraInfo(i, info);

				if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
					camera = Camera.open(i);
				}
			}
		}

		if (camera == null) {
			camera = Camera.open();
		}

		startPreview();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		if (inPreview) {
			camera.stopPreview();
		}

		camera.release();
		camera = null;
		inPreview = false;

		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.options, menu);

		return (super.onCreateOptionsMenu(menu));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.camera && inPreview) {
			camera.takePicture(null, null, photoCallback);
			inPreview = false;
		}

		return (super.onOptionsItemSelected(item));
	}

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}

		return (result);
	}

	private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPictureSizes()) {
			if (result == null) {
				result = size;
			} else {
				int resultArea = result.width * result.height;
				int newArea = size.width * size.height;

				if (newArea < resultArea) {
					result = size;
				}
			}
		}

		return (result);
	}

	private void initPreview(int width, int height) {
		if (camera != null && previewHolder.getSurface() != null) {
			try {
				camera.setPreviewDisplay(previewHolder);
			} catch (Throwable t) {
				log.error("Exception in setPreviewDisplay()", t);
				Toast.makeText(TakePictureActivity.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			if (!cameraConfigured) {
				Camera.Parameters parameters = camera.getParameters();
				Camera.Size size = getBestPreviewSize(width, height, parameters);
				Camera.Size pictureSize = getSmallestPictureSize(parameters);
				if (size != null && pictureSize != null) {
					parameters.setPreviewSize(size.width, size.height);
					parameters.setPictureSize(pictureSize.width,
							pictureSize.height);
					parameters.setPictureFormat(ImageFormat.JPEG);
					parameters.setRotation(90);
					camera.setParameters(parameters);
					camera.setDisplayOrientation(90);
					cameraConfigured = true;
				}
			}
		}
	}

	/**
	 * Add a Photo listener to be notified when a picture is taken
	 * 
	 * @param listener
	 *            the listener
	 */
	public static void addPhotoListener(PhotoListener listener) {
		listeners.add(listener);
	}

	private void startPreview() {
		if (cameraConfigured && camera != null) {
			camera.startPreview();
			inPreview = true;
		}
	}

	private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			initPreview(width, height);
			startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
		}
	};

	private Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			new SavePhotoTask().execute(data);
			finish();
		}
	};

	/**
	 * Handles saving a picture to external memory
	 */
	class SavePhotoTask extends AsyncTask<byte[], String, String> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(byte[]... jpeg) {
			File photo = new File(Environment.getExternalStorageDirectory(),
					".takeahike.jpg");

			if (photo.exists()) {
				photo.delete();
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(photo.getPath());

				fos.write(jpeg[0]);
				fos.close();
				for (PhotoListener listener : listeners) {
					listener.photoTaken(new PhotoEvent(photo));
				}
			} catch (java.io.IOException e) {
				log.error("Exception in photoCallback", e);
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException ignore) {
						// Why do you throw here Java? I've never seen it
						// handled by anyone.
					}
				}
			}

			return (null);
		}
	}
}