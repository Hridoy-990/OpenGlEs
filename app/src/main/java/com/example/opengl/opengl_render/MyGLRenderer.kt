package com.example.opengl.opengl_render


import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.opengl.shape.Square
import com.example.opengl.shape.Triangle
import javax.microedition.khronos.opengles.GL10

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 07,March,2022
 */
class MyGLRenderer : GLSurfaceView.Renderer {

    private lateinit var mTriangle: Triangle
    private lateinit var mSquare: Square
    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)

    @Volatile
    var angle: Float = 0f

    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        val scratch = FloatArray(16)
        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        // Create a rotation transformation for the triangle
//        val time = SystemClock.uptimeMillis() % 4000L
//        val angle = 0.090f * time.toInt()
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

       // mTriangle.draw(scratch)

        mSquare.draw(scratch)

    }

    override fun onSurfaceCreated(p0: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        mTriangle = Triangle()
        mSquare = Square()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    companion object {
        const val TAG = "MY_GL_RENDER"
    }


}