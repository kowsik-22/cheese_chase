package com.example.task2

sealed class screen (val route : String) {
    object Home : screen(route = "home_screen")
    object Game : screen(route = "game_screen")


}