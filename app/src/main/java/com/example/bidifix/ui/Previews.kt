package com.example.bidifix.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bidifix.bidi.BidiTransformer
import com.example.bidifix.bidi.SampleInputs
import com.example.bidifix.ui.components.TextPanel
import com.example.bidifix.ui.theme.BiDiFixTheme
import com.example.bidifix.ui.theme.BiDiTheme

/** Preview of both panels stacked, showing real transformer output, in light and dark. */
@Preview(name = "Panels (light)", showBackground = true, widthDp = 380, heightDp = 720)
@Preview(
    name = "Panels (dark)",
    showBackground = true,
    widthDp = 380,
    heightDp = 720,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun StackedPanelsPreview() {
    val sample = SampleInputs.DEFAULT
    val transformed = remember { BidiTransformer().transform(sample) }
    BiDiFixTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TextPanel(
                    title = "Original Text",
                    subtitle = "Paste or type mixed Hebrew/English text",
                    value = sample,
                    readOnly = false,
                    fieldContentDescription = "Original text input",
                    buttonLabel = "Transform",
                    buttonEnabled = true,
                    onButtonClick = {},
                    panelColor = BiDiTheme.colors.inputSurface,
                    buttonColors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    modifier = Modifier.height(320.dp),
                )
                TextPanel(
                    title = "Transformed Text",
                    subtitle = "Bidi-corrected — ready to copy",
                    value = transformed,
                    readOnly = true,
                    fieldContentDescription = "Transformed text output",
                    buttonLabel = "Copy to Clipboard",
                    buttonEnabled = true,
                    onButtonClick = {},
                    panelColor = BiDiTheme.colors.transformedSurface,
                    buttonColors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                    secondaryButtonLabel = "Copy plain (no marks)",
                    modifier = Modifier.height(320.dp),
                )
            }
        }
    }
}
