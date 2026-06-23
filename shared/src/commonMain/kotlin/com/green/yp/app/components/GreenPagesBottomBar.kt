package com.green.yp.app.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.green.yp.app.ui.theme.DarkGreen

data class NavItem(val label: String, val icon: ImageVector)

@Composable
fun GreenPagesBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val navItems = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Explore", Icons.Default.Search),
        NavItem("Create", Icons.Default.AddCircle),
        NavItem("Messages", Icons.Default.Email),
        NavItem("Profile", Icons.Default.Person)
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = DarkGreen,
        windowInsets = WindowInsets(0.dp)
    ) {
        navItems.forEachIndexed { index, item ->
            val isSelected = selectedTab == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = DarkGreen,
                    indicatorColor = DarkGreen,
                    unselectedIconColor = DarkGreen,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

@Preview
@Composable
fun GreenPagesBottomBarPreview() {
    GreenPagesBottomBar(
        selectedTab = 0,
        onTabSelected = {}
    )
}
