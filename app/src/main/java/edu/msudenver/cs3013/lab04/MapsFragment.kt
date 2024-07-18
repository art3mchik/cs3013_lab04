package edu.msudenver.cs3013.lab04

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.*
import edu.msudenver.cs3013.lab04.databinding.FragmentMapsBinding

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import edu.msudenver.cs3013.lab04.databinding.ActivityMapsBinding

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentMarker: Marker? = null
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.buttonParked.setOnClickListener {
            saveCurrentLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Set the map's initial position to Denver, Colorado
        val denver = LatLng(39.7392, -104.9903)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(denver, 12f))

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            } ?: run {
                // If location is null, keep the camera on Denver
                val denver = LatLng(39.7392, -104.9903)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(denver, 12f))
            }
        }
    }

    private fun saveCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)

                    val carIcon = vectorToBitmap(R.drawable.ic_car)

                    if (currentMarker != null) {
                        currentMarker?.position = currentLatLng
                    } else {
                        currentMarker = mMap.addMarker(MarkerOptions()
                            .position(currentLatLng)
                            .title("Parked Here")
                            .icon(carIcon))
                    }

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    Toast.makeText(requireContext(), "Parking location updated!", Toast.LENGTH_SHORT).show()

                    // Update ViewModel
                    viewModel.updateParkingLocation("Lat: ${it.latitude}, Long: ${it.longitude}")
                } ?: run {
                    Toast.makeText(requireContext(), "Unable to get current location", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Location permission required", Toast.LENGTH_SHORT).show()
        }
    }


    private fun vectorToBitmap(vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    enableMyLocation()
                } else {
                    Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}