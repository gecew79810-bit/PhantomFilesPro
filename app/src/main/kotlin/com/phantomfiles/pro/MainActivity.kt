package com.phantomfiles.pro

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.phantomfiles.pro.presentation.navigation.AppNavHost
import com.phantomfiles.pro.presentation.theme.PhantomTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhantomTheme {
                AppNavHost()
            }
        }
    }
}
