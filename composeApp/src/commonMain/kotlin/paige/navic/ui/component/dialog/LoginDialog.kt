package paige.navic.ui.component.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kyant.capsule.ContinuousCapsule
import com.kyant.capsule.ContinuousRoundedRectangle
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.action_cancel
import navic.composeapp.generated.resources.action_log_in
import navic.composeapp.generated.resources.option_account_navidrome_instance
import navic.composeapp.generated.resources.option_account_password
import navic.composeapp.generated.resources.option_account_username
import org.jetbrains.compose.resources.stringResource
import paige.navic.LocalCtx
import paige.navic.ui.viewmodel.LoginViewModel
import paige.navic.util.LoginState

@Composable
fun LoginDialog(
	viewModel: LoginViewModel = viewModel { LoginViewModel() },
	onDismissRequest: () -> Unit
) {
	val ctx = LocalCtx.current
	val scrollState = rememberScrollState()

	val loginState by viewModel.loginState.collectAsState()

	AlertDialog(
		onDismissRequest = {
			if (loginState !is LoginState.Loading) {
				onDismissRequest()
			}
		},
		text = {
			Column(modifier = Modifier.verticalScroll(scrollState)) {
				(loginState as? LoginState.Error)?.let {
					Text(it.error.message ?: "$it")
				}
				OutlinedTextField(
					state = viewModel.instanceState,
					label = { Text(stringResource(Res.string.option_account_navidrome_instance)) },
					placeholder = { Text("demo.navidrome.org") },
					lineLimits = TextFieldLineLimits.SingleLine,
					keyboardOptions = KeyboardOptions(
						autoCorrectEnabled = false,
						keyboardType = KeyboardType.Uri
					)
				)
				OutlinedTextField(
					state = viewModel.usernameState,
					label = { Text(stringResource(Res.string.option_account_username)) },
					lineLimits = TextFieldLineLimits.SingleLine,
					modifier = Modifier.semantics {
						contentType = ContentType.Username
					},
					keyboardOptions = KeyboardOptions(
						autoCorrectEnabled = false
					)
				)
				OutlinedSecureTextField(
					state = viewModel.passwordState,
					label = { Text(stringResource(Res.string.option_account_password)) }
				)
			}
		},
		confirmButton = {
			Button(
				shape = ContinuousCapsule,
				onClick = {
					viewModel.login()
				},
				enabled = loginState !is LoginState.Loading,
				content = {
					if (loginState !is LoginState.Loading) {
						Text(stringResource(Res.string.action_log_in))
					} else {
						CircularProgressIndicator(
							modifier = Modifier.size(20.dp)
						)
					}
				}
			)
		},
		dismissButton = {
			TextButton(
				onClick = {
					ctx.clickSound()
					onDismissRequest()
				},
				enabled = loginState !is LoginState.Loading,
				content = { Text(stringResource(Res.string.action_cancel)) }
			)
		},
		shape = ContinuousRoundedRectangle(42.dp)
	)
}