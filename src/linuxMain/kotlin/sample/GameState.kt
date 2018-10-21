package sample

sealed class GameState {
    object Running : GameState()
    object Done : GameState()
}