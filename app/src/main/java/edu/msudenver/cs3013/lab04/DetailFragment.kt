package edu.msudenver.cs3013.lab04

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class DetailFragment : Fragment() {
    private lateinit var tvParkingLocation: TextView
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvParkingLocation = view.findViewById(R.id.tvParkingLocation)

        viewModel.savedParkingLocation.observe(viewLifecycleOwner) { location ->
            tvParkingLocation.text = location
        }
    }
}