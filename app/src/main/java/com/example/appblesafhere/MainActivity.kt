package com.example.appblesafhere


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import com.example.appblesafhere.presentation.Navigation
import com.example.appblesafhere.ui.theme.AppBleSafHereTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppBleSafHereTheme {
                Navigation()
            }
        }
    }
}

