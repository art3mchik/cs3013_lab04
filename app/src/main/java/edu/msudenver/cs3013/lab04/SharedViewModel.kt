package edu.msudenver.cs3013.lab04

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _savedParkingLocation = MutableLiveData<String>()
    val savedParkingLocation: LiveData<String> = _savedParkingLocation

    fun updateParkingLocation(location: String) {
        _savedParkingLocation.value = location
    }
}