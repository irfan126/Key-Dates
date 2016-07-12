package uk.co.taskiq.keydates;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import app.AppController;

/**
 * Created by Administrator on 26/02/2016.
 */
public class DisplayImageFragment extends Fragment {


    // Log tag
    private static final String TAG = DisplayImageFragment.class.getSimpleName();

    private String userID;
    private String email = null;
    private String USERID = null;

    ImageView _ImageView;
    ImageLoader imageLoader;



    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();

    String savedItemClicked;

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF startPoint = new PointF();
    PointF midPoint = new PointF();
    float oldDist = 1f;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {


        Bundle bundle = this.getArguments();
        String imagePath = bundle.getString("imagePath");


        View view = inflater.inflate(R.layout.displayimage_fragment, container, false);


        _ImageView = (ImageView) view.findViewById(R.id.imageView1);


        /**
         * set on touch listner on image
         */
        _ImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ImageView view = (ImageView) v;
                System.out.println("matrix=" + savedMatrix.toString());
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:

                        savedMatrix.set(matrix);
                        startPoint.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:

                        oldDist = spacing(event);

                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(midPoint, event);
                            mode = ZOOM;
                        }
                        break;

                    case MotionEvent.ACTION_UP:

                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;

                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);

                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDist / oldDist;
                                matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                            }
                        }
                        break;
                }
                view.setImageMatrix(matrix);

                return true;
            }

            @SuppressLint("FloatMath")
            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return FloatMath.sqrt(x * x + y * y);
            }

            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }
        });


        if(!imagePath.equals("0")) {

            Log.d(TAG, "file" + imagePath);

            Bitmap b = null;
            FileInputStream fis;
            try {
                fis = getActivity().openFileInput(imagePath);
                b = BitmapFactory.decodeStream(fis);
                fis.close();

            }
            catch (FileNotFoundException e) {
                Log.d(TAG, "file not found");
                e.printStackTrace();
            }
            catch (IOException e) {
                Log.d(TAG, "io exception");
                e.printStackTrace();
            }

            _ImageView.setImageBitmap(b);


        }

        else {
            _ImageView.setImageResource(R.drawable.no_image_black);}



        return view;

    }


    }
