package com.openclassroom.eventorias.screen.main.eventsFeed

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclassroom.eventorias.ui.theme.eventorias_black
import com.openclassroom.eventorias.ui.theme.eventorias_gray
import com.openclassroom.eventorias.ui.theme.eventorias_red
import com.openclassroom.eventorias.ui.theme.eventorias_white

/**
 * Composition to display a segmentedButton to change the events sorting.
 * @param sortOption The current sorting option.
 * @param modifier The modifier to apply to this layout.
 * @param isSegmentedButtonVisible Whether the segmented button should be visible.
 * @param onSegmentClicked A function to be called when a segment is clicked.
 */
@Composable
fun SortSegmentedButton(
    sortOption: SortOption,
    modifier: Modifier = Modifier,
    isSegmentedButtonVisible: Boolean = true, // État de visibilité du bouton
    onSegmentClicked: (SortOption) -> Unit // Gestion du clic passé en paramètre
) {
    val options = SortOption.entries.toTypedArray()
    val selectedOption = remember { mutableStateOf(sortOption) } // Gestion de la sélection

    val buttonHeight by animateDpAsState(
        targetValue = if (isSegmentedButtonVisible) 34.dp else 0.dp, // Animé selon la visibilité
        animationSpec = tween(300), label = "" // Durée de l'animation
    )

    val buttonPaddingValue by animateDpAsState(
        targetValue = if (isSegmentedButtonVisible) 8.dp else 0.dp, // Animé selon la visibilité
        animationSpec = tween(300), label = "" // Durée de l'animation
    )

    MultiChoiceSegmentedButtonRow(
        modifier = modifier.padding(
            top = buttonPaddingValue,
            bottom = buttonPaddingValue,
            start = 60.dp,
            end = 60.dp)
            .height(buttonHeight)
            .fillMaxWidth(),
    ) {
        options.forEachIndexed { index, option ->
            SegmentedButton(

                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onCheckedChange = { isChecked ->
                    if (isChecked) {
                        selectedOption.value = option
                        // Appel de la fonction pour gérer le clic
                        onSegmentClicked(option)
                    }
                },
                label = {
                    Text(
                        modifier = Modifier.padding(bottom = 0.dp),
                        fontSize = 11.sp,
                        text = option.name,
                        color = if (selectedOption.value == option) eventorias_white else eventorias_gray // Couleur du texte
                    )
                },
                checked = selectedOption.value == option,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = eventorias_red,
                    activeContentColor = eventorias_white,
                    activeBorderColor = eventorias_gray,
                    inactiveContainerColor = eventorias_black,
                    inactiveContentColor = eventorias_gray,
                    inactiveBorderColor = eventorias_gray
                )
            )
        }
    }
}

/**
 * Enumeration of the different type of events sorting
 */
enum class SortOption {
    Soon,
    Participate,
    Finished
}

@Preview(showBackground = true)
@Composable
fun SortSegmentedButtonPreview() {
    SortSegmentedButton(sortOption = SortOption.Soon,onSegmentClicked = { /* Gestion du clic */ })
}