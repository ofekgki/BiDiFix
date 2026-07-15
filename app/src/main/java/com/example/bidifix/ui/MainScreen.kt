package com.example.bidifix.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bidifix.R
import com.example.bidifix.bidi.BidiCharacters
import com.example.bidifix.ui.components.TextPanel
import com.example.bidifix.ui.theme.BiDiTheme
import com.example.bidifix.util.ClipboardHelper
import com.example.bidifix.viewmodel.MainViewModel
import kotlinx.coroutines.launch

/** Width at or above which the two panels are shown side by side. */
private val SIDE_BY_SIDE_THRESHOLD = 600.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val copiedMessage = stringResource(R.string.copied_confirmation)
    val copiedPlainMessage = stringResource(R.string.copied_plain_confirmation)

    // Transform = Primary, Copy = Secondary (per the palette's component usage).
    val transformButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    )
    val copyButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
    )

    val onCopy: () -> Unit = {
        if (ClipboardHelper.copy(context, state.transformedText)) {
            scope.launch { snackbarHostState.showSnackbar(copiedMessage) }
        }
    }
    val onCopyPlain: () -> Unit = {
        if (ClipboardHelper.copy(context, BidiCharacters.strip(state.transformedText))) {
            scope.launch { snackbarHostState.showSnackbar(copiedPlainMessage) }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                actions = {
                    TextButton(
                        onClick = viewModel::reuseTransformed,
                        enabled = state.isSwapEnabled,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Text(stringResource(R.string.action_swap_short))
                    }
                    TextButton(
                        onClick = viewModel::clearAll,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    ) {
                        Text(stringResource(R.string.action_clear))
                    }
                },
            )
        },
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            val sideBySide = maxWidth >= SIDE_BY_SIDE_THRESHOLD

            val inputPanel: @Composable (Modifier) -> Unit = { m ->
                TextPanel(
                    title = stringResource(R.string.original_text_title),
                    subtitle = stringResource(R.string.original_text_subtitle),
                    value = state.originalText,
                    readOnly = false,
                    onValueChange = viewModel::updateOriginalText,
                    fieldContentDescription = stringResource(R.string.original_text_field_desc),
                    buttonLabel = stringResource(R.string.action_transform),
                    buttonEnabled = state.isTransformEnabled,
                    onButtonClick = viewModel::transformText,
                    panelColor = BiDiTheme.colors.inputSurface,
                    buttonColors = transformButtonColors,
                    // Show the input in raw logical order (exactly as pasted/typed),
                    // instead of letting bidi reorder it to "look" already fixed.
                    textDirection = TextDirection.Ltr,
                    modifier = m,
                )
            }
            val outputPanel: @Composable (Modifier) -> Unit = { m ->
                TextPanel(
                    title = stringResource(R.string.transformed_text_title),
                    subtitle = stringResource(R.string.transformed_text_subtitle),
                    value = state.transformedText,
                    readOnly = true,
                    fieldContentDescription = stringResource(R.string.transformed_text_field_desc),
                    buttonLabel = stringResource(R.string.action_copy),
                    buttonEnabled = state.isCopyEnabled,
                    onButtonClick = onCopy,
                    panelColor = BiDiTheme.colors.transformedSurface,
                    buttonColors = copyButtonColors,
                    secondaryButtonLabel = stringResource(R.string.action_copy_plain),
                    onSecondaryButtonClick = onCopyPlain,
                    modifier = m,
                )
            }

            if (sideBySide) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    inputPanel(Modifier.weight(1f).fillMaxHeight())
                    outputPanel(Modifier.weight(1f).fillMaxHeight())
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    inputPanel(Modifier.weight(1f).fillMaxWidth())
                    outputPanel(Modifier.weight(1f).fillMaxWidth())
                }
            }
        }
    }
}
