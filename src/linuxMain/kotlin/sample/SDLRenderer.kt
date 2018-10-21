package sample

import cnames.structs.SDL_Window
import cnames.structs.SDL_Texture
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString
import sdl2.*

@ExperimentalUnsignedTypes
class SDLRenderer(window: CPointer<SDL_Window>) {

    private val renderer: CPointer<SDL_Renderer> =
        SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED or SDL_RENDERER_PRESENTVSYNC) ?: throw NullPointerException("SDL Renderer returned null")

    init {
        SDL_SetRenderDrawColor(renderer, 0xFF, 0xFF, 0xFF, 0xFF)
    }

    fun render(vararg textures: CPointer<SDL_Texture>) {
        SDL_RenderClear(renderer)
        for (texture in textures)
            SDL_RenderCopy(renderer, texture, null, null)

        SDL_RenderPresent(renderer)
    }


    @ExperimentalUnsignedTypes
    fun loadImage(path: String): CPointer<SDL_Texture>? {

        val img = IMG_Load(path)!!
        val finalSurface = SDL_CreateTextureFromSurface(renderer, img) ?: throw Exception("SDL Error when trying to load texture: ${SDL_GetError()?.toKString()}")
        SDL_FreeSurface(img)
        return finalSurface
    }

    fun destroy() {
        SDL_DestroyRenderer(renderer)
    }

}