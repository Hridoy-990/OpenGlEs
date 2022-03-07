package com.example.opengl.shape

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 07,March,2022
 */

var cube = floatArrayOf(

    -0.2f, -0.2f, 0.8f,  // 0. left-bottom-front
    0.2f, -0.2f, 0.8f,  // 1. right-bottom-front
    -0.2f, 0.2f, 0.8f,  // 2. left-top-front
    0.2f, 0.2f, 0.8f,  // 3. right-top-front

// BACK
    -0.2f, -0.2f, -0.8f,  // 4. left-bottom-back
    -0.2f, 0.2f, -0.8f,  // 5. left-top-back
     0.2f, -0.2f, -0.8f,  // 6. right-bottom-back
     0.2f, 0.2f, -0.8f,  // 7. right-top-back

// LEFT
    -0.2f, -0.2f, -0.8f,  // 4. left-bottom-back
    -0.2f, -0.2f, 0.8f,  // 0. left-bottom-front
    -0.2f, 0.2f, -0.8f,  // 5. left-top-back
    -0.2f, 0.2f, 0.8f,  // 2. left-top-front
// RIGHT
    0.2f, -0.2f, 0.8f,  // 1. right-bottom-front
    0.2f, -0.2f, -0.8f,  // 6. right-bottom-back
    0.2f, 0.2f, 0.8f,  // 3. right-top-front
    0.2f, 0.2f, -0.8f,  // 7. right-top-back
// TOP
    -0.2f, 0.2f, 0.8f,  // 2. left-top-front
    0.2f, 0.2f, 0.8f,  // 3. right-top-front
    -0.2f, 0.2f, -0.8f,  // 5. left-top-back
    0.2f, 0.2f, -0.8f,  // 7. right-top-back
// BOTTOM
    -0.2f, -0.2f, -0.8f,  // 4. left-bottom-back
    0.2f, -0.2f, -0.8f,  // 6. right-bottom-back
    -0.2f, -0.2f, 0.8f,  // 0. left-bottom-front
    0.2f, -0.2f, 0.8f   // 1. right-bottom-front
)

class Cube {
    private val drawOrder = shortArrayOf(
        0, 1, 2, 2, 1, 3,
        5, 4, 7, 7, 4, 6,
        8, 9, 10, 10, 9, 11,
        12, 13, 14, 14, 13, 15,
        16, 17, 18, 18, 17, 19,
        22, 23, 20, 20, 23, 21
    ) // order to draw vertices

    private val colors = arrayOf(
        floatArrayOf(1.0f, 0.5f, 0.5f, 1.0f),
        floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f),
        floatArrayOf(0.0f, 1.0f, 0.5f, 1.0f),
        floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f),
        floatArrayOf(0.0f, 0.0f, 1.0f, 1.0f),
        floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f)
    )

    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0
    private val numFaces = 6

    private val vertexCount: Int = cube.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private var vertexBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(cube.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(cube)
                // set the buffer to read the first coordinate
                position(0)
            }
        }

    // initialize byte buffer for the draw list
    private val drawListBuffer: ShortBuffer =
        // (# of coordinate values * 2 bytes per short)
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    private val vertexShaderCode =
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +
                // the matrix must be included as a modifier of gl_Position
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"

    // Use to access and set the view transformation
    private var vPMatrixHandle: Int = 0

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        return GLES20.glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    private var mProgram: Int

    init {

        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram().also {

            // add the vertex shader to program
            GLES20.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES20.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES20.glLinkProgram(it)
        }
    }

    fun draw(mvpMatrix: FloatArray) { // pass in the calculated transformation matrix
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram)
        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            // get handle to fragment shader's vColor member
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")

            for(i  in 0 until numFaces) {
                // Set color for drawing the triangle
                GLES20.glUniform4fv( mColorHandle, 1, colors[i], 0)
                drawListBuffer.position(i * 6)
                // Draw the square
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6,
                    GLES20.GL_UNSIGNED_SHORT, drawListBuffer)
            }




            // Disable vertex array
            GLES20.glDisableVertexAttribArray(it)
        }
    }

}