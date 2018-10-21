package sample

import cnames.structs.SDL_Window
import kotlinx.cinterop.*
import platform.posix.EXDEV
import sdl2.*
import kotlin.math.roundToInt
import kotlin.system.getTimeMillis

@ExperimentalUnsignedTypes
class ShootumsGame {
    private val arena = Arena()
    val window: CPointer<SDL_Window> = SDL_CreateWindow(
        title = "SHIPPPPPPPPPPPPPPPPPPPPPPPP",
        x = SDL_WINDOWPOS_CENTERED.toInt(),
        y = SDL_WINDOWPOS_CENTERED.toInt(),
        w = 640,
        h = 480,
        flags = SDL_WINDOW_SHOWN
    )!!
    private val renderer = SDLRenderer(window)
    private var state: GameState = GameState.Running
    private val sdlEvent: SDL_Event = arena.alloc()


    fun run() = memScoped {
        val tickrate = 1000 / 60
        val ship = renderer.loadImage("res/images/spaceShips_001.png") ?: throw Exception("Could not load ship: ${SDL_GetError()?.toKString()}")

        while (state is GameState.Running) {
            val startTime = getTimeMillis()

            sdlEvent.resolveInputs { event ->
                when (event) {
                    SDL_QUIT -> quit()
                }
            }

            renderer.render(ship)


            val endTime = getTimeMillis()
            val timeDiff = endTime - startTime

            if (timeDiff < tickrate)
                SDL_Delay((tickrate - timeDiff).toUInt())
        }

        renderer.destroy()
        SDL_DestroyWindow(window)
        SDL_Quit()
    }

    fun quit() {
        state = GameState.Done
    }

    fun currentState() = state

    @ExperimentalUnsignedTypes
    private fun SDL_Event.resolveInputs(func: (Uint32) -> Unit) {
        SDL_PollEvent(this.ptr)

        func(this.type)

    }


}