package sample

import kotlinx.cinterop.*
import kotlinx.coroutines.runBlocking
import sdl2.*
import kotlin.math.roundToInt
import kotlin.system.getTimeMillis

@ExperimentalUnsignedTypes
fun main(): Unit = runBlocking {
    memScoped {
        val window = SDL_CreateWindow(
            title = "Hello Kotlin/Native",
            x = SDL_WINDOWPOS_CENTERED.toInt(),
            y = SDL_WINDOWPOS_CENTERED.toInt(),
            w = 640,
            h = 480,
            flags = SDL_WINDOW_SHOWN
        )!!

        val gameContext = GameContext(window)

        paintScreen(gameContext)


        gameContext.loop {

        }


        SDL_DestroyWindow(window)
        SDL_Quit()

    }
}


@ExperimentalUnsignedTypes
data class GameContext(
    val window: CPointer<SDL_Window>,
    val primarySurface: CPointer<SDL_Surface> = SDL_GetWindowSurface(window)!!
) {
    private var state: GameState = GameState.Running

    fun loop(function: () -> Unit) {
        val tickrate = 1000 / 60

        while (state is GameState.Running) {
            val startTime = getTimeMillis()
            function()
            val endTime = getTimeMillis()
            val timeDiff = endTime - startTime

            if (timeDiff < tickrate)
                SDL_Delay((tickrate - timeDiff).toUInt())
        }
    }

    fun quit() {
        state = GameState.Done
    }

    fun currentState() = state
}

sealed class GameState {
    object Running : GameState()
    object Done : GameState()
}


fun CPointer<SDL_Surface>.fill(rect: CValue<SDL_Rect>, r: Uint8, g: Uint8, b: Uint8) =
    SDL_FillRect(this, rect, SDL_MapRGB(this.pointed.format, r, g, b))

@ExperimentalUnsignedTypes
fun paintScreen(context: GameContext) = memScoped {
    val step: Float = 255f / 639

    val rect = alloc<SDL_Rect>()

    for (line in 0..639) {
        val color = (line * step).roundToInt()

        rect.apply {
            x = line
            y = 0
            w = 1
            h = 480
        }

        context.primarySurface.fill(rect.readValue(), color.toUByte(), color.toUByte(), color.toUByte())
    }


    SDL_UpdateWindowSurface(context.window)
}
