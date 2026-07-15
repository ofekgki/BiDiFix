package com.example.bidifix.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp

/**
 * One labelled panel: a title, a multiline text field and an action button beneath it.
 *
 * Used for both the editable input panel and the read-only output panel. The text field
 * scrolls internally for long multiline text.
 *
 * [textDirection] controls how the field lays out its text. The input panel passes
 * [TextDirection.Ltr] so pasted/typed text is shown in raw logical order (exactly as
 * entered), without bidi reordering that would make it look already corrected. The output
 * panel keeps the [TextDirection.Content] default so the invisible controls inserted by the
 * transformer take effect and the correction is visible.
 */
@Composable
fun TextPanel(
    title: String,
    value: String,
    readOnly: Boolean,
    fieldContentDescription: String,
    buttonLabel: String,
    buttonEnabled: Boolean,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    textDirection: TextDirection = TextDirection.Content,
    onValueChange: (String) -> Unit = {},
    secondaryButtonLabel: String? = null,
    onSecondaryButtonClick: () -> Unit = {},
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                readOnly = readOnly,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true)
                    .heightIn(min = 160.dp)
                    .semantics { contentDescription = fieldContentDescription },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    textDirection = textDirection,
                ),
            )
            Button(
                onClick = onButtonClick,
                enabled = buttonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .padding(top = 12.dp),
            ) {
                Text(buttonLabel)
            }
            if (secondaryButtonLabel != null) {
                OutlinedButton(
                    onClick = onSecondaryButtonClick,
                    enabled = buttonEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .padding(top = 8.dp),
                ) {
                    Text(secondaryButtonLabel)
                }
            }
        }
    }
}
