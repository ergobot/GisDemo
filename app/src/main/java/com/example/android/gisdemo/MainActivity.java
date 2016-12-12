package com.example.android.gisdemo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISLayerInfo;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;


public class MainActivity extends AppCompatActivity {

    MapView mapView;
    ProgressDialog progress;
    ArcGISDynamicMapServiceLayer dynamicLayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (MapView)findViewById(R.id.map);
        dynamicLayer = new ArcGISDynamicMapServiceLayer("https://gis.iowadot.gov/public/rest/services/Traffic_Safety/Crash_Data/MapServer");
        mapView.addLayer(dynamicLayer);
        dynamicLayer.setOpacity((float) 0.5);
        Button queryButton = (Button)findViewById(R.id.querybutton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryData();
            }
        });

    }

    private void queryData(){
//        dynamicLayer = new ArcGISDynamicMapServiceLayer("https://gis.iowadot.gov/public/rest/services/Traffic_Safety/Crash_Data/MapServer");

        ArcGISLayerInfo crashDataLayer = dynamicLayer.getLayers()[0];
//        QueryTask queryTask = new QueryTask(crashDataLayer.get);

//        QueryTask queryTask = new QueryTask(dynamicLayer.getQueryUrl(0));
//        QueryParameters queryParameters = new QueryParameters();
//        queryParameters.setWhere("REPORT==2");
//
//        try{
//            FeatureResult results = queryTask.execute(queryParameters);
//        }catch(Exception e){
//            e.printStackTrace();
//        }

//        String targetLayer = queryLayer.concat("/3");
        // major cause = animal
        String where = "MAJCSE=1";
        String[] queryArray = { dynamicLayer.getQueryUrl(0), where };
        AsyncQueryTask ayncQuery = new AsyncQueryTask();
        ayncQuery.execute(queryArray);

        Log.i("layerinfo", "insert layer info here");
    }


    /**
     *
     * Query Task executes asynchronously.
     *
     */
    private class AsyncQueryTask extends AsyncTask<String, Void, FeatureResult> {

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(MainActivity.this);

            progress = ProgressDialog.show(MainActivity.this, "",
                    "Please wait....query task is executing");

        }

        /**
         * First member in string array is the query URL; second member is the
         * where clause.
         */
        @Override
        protected FeatureResult doInBackground(String... queryArray) {

            if (queryArray == null || queryArray.length <= 1)
                return null;

            String url = queryArray[0];
            QueryParameters qParameters = new QueryParameters();
            String whereClause = queryArray[1];
//            SpatialReference sr = SpatialReference.create(102100);
//            qParameters.setGeometry(mMapView.getExtent());
//            qParameters.setOutSpatialReference(sr);
//            qParameters.setReturnGeometry(true);
            qParameters.setWhere(whereClause);

            QueryTask qTask = new QueryTask(url);

            try {
                FeatureResult results = qTask.execute(qParameters);
                return results;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(FeatureResult results) {

            String message = "No result comes back";

            if (results != null) {
                int size = (int) results.featureCount();
//                Point mapPoint = mapView.toMapPoint(point.getX(), point.getY());
//                //Here point is your MotionEvent point
//                SpatialReference spacRef = SpatialReference.create(4326);
//                //4326 is for Geographic coordinate systems (GCSs)
//                Point ltLn = (Point)
//                        GeometryEngine.project(mapPoint,mMapView.getSpatialReference(), spacRef );

                for (Object element : results) {
                    progress.incrementProgressBy(size / 100);
                    if (element instanceof Feature) {


                        Feature feature = (Feature) element;
                        Log.i("feature","feature info here");
//                        // turn feature into graphic
//                        Graphic graphic = new Graphic(feature.getGeometry(),
//                                feature.getSymbol(), feature.getAttributes());
//                        // add graphic to layer
//                        graphicsLayer.addGraphic(graphic);
                    }
                }
                // update message with results
                message = String.valueOf(results.featureCount())
                        + " results have returned from query.";

            }
            progress.dismiss();
            Toast toast = Toast.makeText(MainActivity.this, message,
                    Toast.LENGTH_LONG);
            toast.show();
//            boolQuery = false;

        }

    }


    @Override
    protected void onPause() {
        super.onPause();

        // Call MapView.pause to suspend map rendering while the activity is paused, which can save battery usage.
        if (mapView != null)
        {
            mapView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Call MapView.unpause to resume map rendering when the activity returns to the foreground.
        if (mapView != null)
        {
            mapView.unpause();
        }
    }




}
