package com.example.supermarketpetproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.supermarketpetproject.core.presentation.navigation.NavGraph
import com.example.supermarketpetproject.ui.theme.SupermarketPetProjectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SupermarketPetProjectTheme {
                NavGraph()
            }
        }
    }
}