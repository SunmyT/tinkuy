package com.proyecto.myapplicationtinkuy

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private enum class NotificationPreference(val key: String, val title: String, val description: String) {
    Proposals(
        "received_proposals",
        "Propuestas recibidas",
        "Cuando un productor te envíe una propuesta."
    ),
    Quotes(
        "quote_updates",
        "Solicitudes de cotización",
        "Cuando se actualice una de tus solicitudes."
    ),
    Orders(
        "order_updates",
        "Cambios en mis pedidos",
        "Cuando cambie el estado de uno de tus pedidos."
    )
}

@Composable
internal fun Notifications(go: (Screen) -> Unit, back: () -> Unit) {
    val context = LocalContext.current
    val preferences = remember(context) {
        context.getSharedPreferences("notification_preferences", Context.MODE_PRIVATE)
    }
    var settings by remember(preferences) {
        mutableStateOf(NotificationPreference.entries.associateWith { preferences.getBoolean(it.key, false) })
    }

    Page(Screen.Notifications, go) {
        Column(Modifier.fillMaxSize().background(AppBackground)) {
            Header("Notificaciones", back = back)
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(48.dp).background(Accent.copy(alpha = 0.10f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        TinkuyIcon(AppIcon.Bell, modifier = Modifier.size(24.dp), tint = Accent)
                    }
                    Text(
                        "Elige qué avisos quieres activar para tu actividad en Tinkuy.",
                        color = Muted,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(Modifier.height(24.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Line)
                ) {
                    Column {
                        NotificationPreference.entries.forEachIndexed { index, preference ->
                            if (index > 0) HorizontalDivider(Modifier.padding(horizontal = 16.dp), color = Line)
                            NotificationPreferenceRow(preference, settings.getValue(preference)) { checked ->
                                settings = settings + (preference to checked)
                                preferences.edit().putBoolean(preference.key, checked).apply()
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun NotificationPreferenceRow(
    preference: NotificationPreference,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 84.dp)
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                preference.title,
                color = Ink,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(preference.description, color = Muted, style = MaterialTheme.typography.bodyMedium)
        }
        Switch(
            checked = checked,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedTrackColor = Accent,
                checkedThumbColor = Color.White,
                uncheckedTrackColor = AppBackground,
                uncheckedThumbColor = Muted,
                uncheckedBorderColor = Muted
            )
        )
    }
}
