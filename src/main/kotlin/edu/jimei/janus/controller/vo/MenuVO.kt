package edu.jimei.janus.controller.vo

// Corresponds to menu item structure in the frontend
data class MenuItemVO(
    val id: String,
    val label: String,
    val icon: String,
    val path: String,
    val isActive: Boolean = false
) 