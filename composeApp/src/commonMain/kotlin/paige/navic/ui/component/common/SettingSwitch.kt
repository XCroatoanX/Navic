package paige.navic.ui.component.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.switch_off
import navic.composeapp.generated.resources.switch_on
import org.jetbrains.compose.resources.vectorResource

@Composable
fun SettingSwitch(
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit
) {
	Switch(
		checked = checked,
		onCheckedChange = onCheckedChange,
		thumbContent = {
			Icon(
				vectorResource(
					if (checked) Res.drawable.switch_on else Res.drawable.switch_off
				),
				contentDescription = null,
				modifier = Modifier.size(SwitchDefaults.IconSize)
			)
		}
	)
}