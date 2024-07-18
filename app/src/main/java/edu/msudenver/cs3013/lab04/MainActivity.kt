package edu.msudenver.cs3013.lab04

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import edu.msudenver.cs3013.lab04.SharedViewModel
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider


class MainActivity : AppCompatActivity() {
    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}