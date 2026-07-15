package com.example.bidifix

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.bidifix.ui.MainScreen
import com.example.bidifix.ui.theme.BiDiFixTheme
import com.example.bidifix.util.ShareIntentParser
import com.example.bidifix.viewmodel.MainViewModel

/**
 * Single activity hosting the whole Compose UI. It also receives text shared from other
 * apps (`ACTION_SEND`) and selected-text intents (`ACTION_PROCESS_TEXT`), for both a cold
 * start ([onCreate]) and while already running ([onNewIntent]).
 */
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)
        setContent {
            BiDiFixTheme {
                MainScreen(viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    /** Places any shared/selected text into the input field, without transforming it. */
    private fun handleIntent(intent: Intent?) {
        ShareIntentParser.parse(intent)?.let(viewModel::setSharedText)
    }
}
