package com.example.billionemotosappkt.screens.dashboard.customer.components

import android.content.res.Configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.AutoMirrored
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerFaqItem
import androidx.compose.ui.tooling.preview.Preview
import com.example.billionemotosappkt.screens.dashboard.customer.CustomerDashboardPreviewData
import com.example.billionemotosappkt.ui.theme.BilliOneMotosAppKtTheme

@Composable
fun CustomerFaqAccordion(
	items: List<CustomerFaqItem>,
	openIndex: Int?,
	onToggle: (Int?) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier.fillMaxWidth(), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
		items.forEachIndexed { index, item ->
			val isOpen = openIndex == index
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.clip(RoundedCornerShape(16.dp))
					.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.14f))
					.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
					.clickable { onToggle(if (isOpen) null else index) }
					.padding(14.dp),
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Icon(imageVector = AutoMirrored.Filled.Help, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
					Spacer(modifier = Modifier.size(10.dp))
					Text(
						text = item.question,
						style = MaterialTheme.typography.bodySmall,
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier.weight(1f),
					)
					Icon(
						imageVector = if (isOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
				if (isOpen) {
					Text(
						text = item.answer,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						modifier = Modifier.padding(top = 10.dp, start = 30.dp),
					)
				}
			}
		}
	}
}

@Preview(showBackground = true, widthDp = 420, uiMode = Configuration.UI_MODE_NIGHT_YES, backgroundColor = 0xFF090C0A)
@Composable
private fun CustomerFaqAccordionPreview() {
	BilliOneMotosAppKtTheme(darkTheme = true) {
		CustomerFaqAccordion(
			items = CustomerDashboardPreviewData.dashboardData.faqs,
			openIndex = 0,
			onToggle = {},
		)
	}
}
