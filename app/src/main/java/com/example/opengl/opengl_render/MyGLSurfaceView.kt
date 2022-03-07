package com.example.opengl.opengl_render

import android.content.Context
import android.opengl.GLSurfaceView

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 07,March,2022
 */

class MyGLSurfaceView (context: Context) : GLSurfaceView(context) {

    private val renderer: MyGLRenderer

    init {

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = MyGLRenderer()

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)

    }
}