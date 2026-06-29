package com.example.billionemotosappkt.screens.dashboard.customer.components

import android.content.res.Configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerUpgradeBanner
import androidx.compose.ui.tooling.preview.Preview
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardPreviewData
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerUpgradeBanner(
	banner: CustomerUpgradeBanner,
	modifier: Modifier = Modifier,
) {
	CustomerAppCard(
		modifier = modifier,
		containerColor = androidx.compose.ui.graphics.Color(0xFF2A2110).copy(alpha = 0.22f),
		borderColor = androidx.compose.ui.graphics.Color(0xFFF59E0B).copy(alpha = 0.24f),
		content = {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
				Icon(
					imageVector = Icons.Default.AutoAwesome,
					contentDescription = null,
					tint = androidx.compose.ui.graphics.Color(0xFFFBBF24),
					modifier = Modifier.size(18.dp),
				)
				Text(
					text = banner.label.uppercase(),
					style = MaterialTheme.typography.labelSmall,
					fontWeight = FontWeight.Black,
					color = androidx.compose.ui.graphics.Color(0xFFFBBF24),
				)
			}
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				text = banner.title,
				style = MaterialTheme.typography.titleSmall,
				fontWeight = FontWeight.Black,
				color = MaterialTheme.colorScheme.onSurface,
			)
			Text(
				text = banner.description,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			Spacer(modifier = Modifier.height(12.dp))
			Button(
				onClick = { },
				colors = ButtonDefaults.buttonColors(
					containerColor = androidx.compose.ui.graphics.Color(0xFFFBBF24).copy(alpha = 0.16f),
					contentColor = androidx.compose.ui.graphics.Color(0xFFFBBF24),
				),
				modifier = Modifier
					.clip(RoundedCornerShape(14.dp))
					.border(1.dp, androidx.compose.ui.graphics.Color(0xFFFBBF24).copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
			) {
				Text(
					text = banner.buttonText,
					fontWeight = FontWeight.Black,
				)
			}
		},
	)
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
private fun CustomerUpgradeBannerPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerUpgradeBanner(banner = CustomerDashboardPreviewData.dashboardData.upgradeBanner)
	}
}
