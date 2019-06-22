package com.example.testapp

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.networkmodel.ResponseModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.iswm.socket.ISWMSocket
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import org.json.JSONObject
import android.widget.Spinner as Spinner1

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, ISWMSocket.ISWMSocketInterface {
    override fun onOpen(p0: WebSocket?, p1: Response?) {
    }

    override fun onFailure(p0: WebSocket?, p1: Throwable?, p2: Response?) {
        mSocket.retry()
    }

    override fun onClosing(p0: WebSocket?, p1: Int, p2: String?) {
        mSocket.detach()
    }

    override fun onMessage(p0: WebSocket?, p1: String?) {
        Log.i("String Response", p1)
        mJsonObject = JSONObject(p1)
        if (mJsonObject.has("d")) {
            if (JSONObject(mJsonObject.optString("d")).has("data")) {
                try {
                    val parsedModel = Gson().fromJson(p1, ResponseModel::class.java)
                    val lat = parsedModel.d.data.lat
                    val long = parsedModel.d.data.lng
                    // Add a marker in Sydney and move the camera
                    val value = LatLng(lat, long)
                    valuelist.add(value)
                    drawRoute(value, valuelist)
                } catch (e: Exception) {
                    Log.i("Exception", e.toString())
                }
            }

        }
    }

    override fun onMessage(p0: WebSocket?, p1: ByteString?) {
    }

    private lateinit var mMap: GoogleMap
    private lateinit var mSocket: ISWMSocket
    private lateinit var mJsonObject: JSONObject
    private  var valuelist = ArrayList<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val mSpinner = findViewById<android.widget.Spinner>(R.id.spinner)

        mSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(isNetworkAvailable(this@MapsActivity)) {
                    mSocket.subscribeTopic(
                        StringBuilder().append(resources.getString(R.string.topic_data)).append(
                            parent!!.getItemAtPosition(position).toString()
                        ).toString()
                    )
                }else{
                    showAlert(this@MapsActivity,"",resources.getString(R.string.network_error))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        mSocket = ISWMSocket(this@MapsActivity, resources.getString(R.string.url), resources.getString(R.string.port))
        mSocket.start()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    /**
     * Adding marker on map and drawing the route between two end points
     */
    private fun drawRoute(latLng: LatLng, valueList: ArrayList<LatLng>) {

        runOnUiThread {
            mMap.addMarker(MarkerOptions().position(latLng).title(resources.getString(R.string.marker_title)))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
            mMap.addPolyline(PolylineOptions().clickable(true).addAll(valueList).color(Color.MAGENTA))

        }
    }

    /**
     * Is network available boolean.
     *
     * @param context the context
     * @return the boolean
     */
    fun isNetworkAvailable(context: Context?): Boolean {
        val connectMgr = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager ?: return false
            val netInfo = connectMgr.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    /**
     * Show alert.
     *
     * @param context the context
     * @param title   the title
     * @param message the message
     */
    fun showAlert(context: Context, title: String, message: String) {
            try {
                if (!(context as Activity).isFinishing) {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle(title).setMessage(message).setCancelable(false)
                    builder.setPositiveButton(R.string.ok
                    ) { dialog, _ ->
                        dialog.cancel()
                    }
                    builder.show()
                }
            } catch (e: Exception) {
                Log.e("Exception", "Dialog exception", e)
            }

    }


}
