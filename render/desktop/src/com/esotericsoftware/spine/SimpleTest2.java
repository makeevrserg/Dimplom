/******************************************************************************
 * Spine Runtimes License Agreement
 * Last updated January 1, 2020. Replaces all prior versions.
 *
 * Copyright (c) 2013-2020, Esoteric Software LLC
 *
 * Integration of the Spine Runtimes into software or otherwise creating
 * derivative works of the Spine Runtimes is permitted under the terms and
 * conditions of Section 2 of the Spine Editor License Agreement:
 * http://esotericsoftware.com/spine-editor-license
 *
 * Otherwise, it is permitted to integrate the Spine Runtimes into software
 * or otherwise create derivative works of the Spine Runtimes (collectively,
 * "Products"), provided that each user of the Products must obtain their own
 * Spine Editor license and redistribution of the Products in any form must
 * include this license and copyright notice.
 *
 * THE SPINE RUNTIMES ARE PROVIDED BY ESOTERIC SOFTWARE LLC "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL ESOTERIC SOFTWARE LLC BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES,
 * BUSINESS INTERRUPTION, OR LOSS OF USE, DATA, OR PROFITS) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THE SPINE RUNTIMES, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *****************************************************************************/

package com.esotericsoftware.spine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.squareup.gifencoder.GifEncoder;
import com.squareup.gifencoder.ImageOptions;
import org.lwjgl.Sys;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SimpleTest2 extends ApplicationAdapter {
    SpriteBatch batch;
    SkeletonRenderer renderer;

    TextureAtlas atlas;
    Skeleton skeleton;
    SkeletonBounds bounds;
    AnimationState state;

    public void create() {
        frames = new ArrayList<>();
        batch = new SpriteBatch();
        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true);

        System.out.println("Local Path " + Gdx.files.getLocalStoragePath());
        System.out.println("External Path " + Gdx.files.getExternalStoragePath());

        atlas = new TextureAtlas(Gdx.files.internal("spineboy/spineboy-pma.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(0.3f); // Load the skeleton at 60% the size it was in Spine.
        //String js = "{'skeleton': {'hash': 'MOhkPvw8nLOrjuLm/U98aFMannI', 'spine': '3.8.55', 'x': -221.27, 'y': -8.57, 'width': 470.72, 'height': 731.57, 'images': './images/', 'audio': ''}, 'bones': [{'name': 'root'}, {'name': 'hip', 'parent': 'root', 'y': 247.47}, {'name': 'torso', 'parent': 'hip', 'length': 127.56, 'rotation': 103.82, 'x': -1.62, 'y': 4.9, 'color': 'e0da19ff'}, {'name': 'front-upper-arm', 'parent': 'torso', 'length': 69.45, 'rotation': 168.38, 'x': 103.76, 'y': 19.33, 'color': '00ff04ff'}, {'name': 'front-bracer', 'parent': 'front-upper-arm', 'length': 40.57, 'rotation': 18.3, 'x': 68.8, 'y': -0.68, 'color': '00ff04ff'}, {'name': 'front-fist', 'parent': 'front-bracer', 'length': 65.39, 'rotation': 12.43, 'x': 40.57, 'y': 0.2, 'color': '00ff04ff'}, {'name': 'front-thigh', 'parent': 'hip', 'length': 74.81, 'rotation': -95.51, 'x': -17.46, 'y': -11.64, 'color': '00ff04ff'}, {'name': 'front-shin', 'parent': 'front-thigh', 'length': 128.77, 'rotation': -2.21, 'x': 78.69, 'y': 1.6, 'color': '00ff04ff'}, {'name': 'front-foot', 'parent': 'front-shin', 'length': 91.34, 'rotation': 77.91, 'x': 128.76, 'y': -0.34, 'color': '00ff04ff'}, {'name': 'rear-upper-arm', 'parent': 'torso', 'length': 51.94, 'rotation': -169.56, 'x': 92.36, 'y': -19.22, 'color': 'ff000dff'}, {'name': 'rear-bracer', 'parent': 'rear-upper-arm', 'length': 34.56, 'rotation': 23.15, 'x': 51.36, 'color': 'ff000dff'}, {'name': 'gun', 'parent': 'rear-bracer', 'length': 43.11, 'rotation': 5.35, 'x': 34.42, 'y': -0.45, 'color': 'ff000dff'}, {'name': 'gun-tip', 'parent': 'gun', 'rotation': 6.83, 'x': 201.05, 'y': 52.14, 'color': 'ff000dff'}, {'name': 'neck', 'parent': 'torso', 'length': 25.45, 'rotation': -31.54, 'x': 127.5, 'y': -0.31, 'color': 'e0da19ff'}, {'name': 'head', 'parent': 'neck', 'length': 263.58, 'rotation': 23.18, 'x': 27.66, 'y': -0.26, 'color': 'e0da19ff'}, {'name': 'rear-thigh', 'parent': 'hip', 'length': 85.72, 'rotation': -72.54, 'x': 8.91, 'y': -5.63, 'color': 'ff000dff'}, {'name': 'rear-shin', 'parent': 'rear-thigh', 'length': 121.88, 'rotation': -19.83, 'x': 86.1, 'y': -1.33, 'color': 'ff000dff'}, {'name': 'rear-foot', 'parent': 'rear-shin', 'length': 82.57, 'rotation': 69.3, 'x': 121.46, 'y': -0.76, 'color': 'ff000dff'}], 'slots': [{'name': 'rear-upper-arm', 'bone': 'rear-upper-arm', 'attachment': 'rear-upper-arm'}, {'name': 'rear-bracer', 'bone': 'rear-bracer', 'attachment': 'rear-bracer'}, {'name': 'gun', 'bone': 'gun', 'attachment': 'gun'}, {'name': 'rear-foot', 'bone': 'rear-foot', 'attachment': 'rear-foot'}, {'name': 'rear-thigh', 'bone': 'rear-thigh', 'attachment': 'rear-thigh'}, {'name': 'rear-shin', 'bone': 'rear-shin', 'attachment': 'rear-shin'}, {'name': 'neck', 'bone': 'neck', 'attachment': 'neck'}, {'name': 'torso', 'bone': 'torso', 'attachment': 'torso'}, {'name': 'front-upper-arm', 'bone': 'front-upper-arm', 'attachment': 'front-upper-arm'}, {'name': 'head', 'bone': 'head', 'attachment': 'head'}, {'name': 'eye', 'bone': 'head', 'attachment': 'eye-indifferent'}, {'name': 'front-thigh', 'bone': 'front-thigh', 'attachment': 'front-thigh'}, {'name': 'front-foot', 'bone': 'front-foot', 'attachment': 'front-foot'}, {'name': 'front-shin', 'bone': 'front-shin', 'attachment': 'front-shin'}, {'name': 'mouth', 'bone': 'head', 'attachment': 'mouth-smile'}, {'name': 'goggles', 'bone': 'head', 'attachment': 'goggles'}, {'name': 'front-bracer', 'bone': 'front-bracer', 'attachment': 'front-bracer'}, {'name': 'front-fist', 'bone': 'front-fist', 'attachment': 'front-fist-closed'}, {'name': 'muzzle', 'bone': 'gun-tip', 'blend': 'additive'}, {'name': 'head-bb', 'bone': 'head'}], 'skins': [{'name': 'default', 'attachments': {'gun': {'gun': {'x': 77.3, 'y': 16.4, 'rotation': 60.83, 'width': 210, 'height': 203}}, 'rear-shin': {'rear-shin': {'x': 58.29, 'y': -2.75, 'rotation': 92.37, 'width': 75, 'height': 178}}, 'head-bb': {'head': {'type': 'boundingbox', 'vertexCount': 6, 'vertices': [-19.14, -70.3, 40.8, -118.07, 257.77, -115.62, 285.16, 57.18, 120.77, 164.95, -5.07, 76.95]}}, 'mouth': {'mouth-grind': {'x': 23.69, 'y': -32.24, 'rotation': -70.63, 'width': 93, 'height': 59}, 'mouth-oooo': {'x': 23.69, 'y': -32.24, 'rotation': -70.63, 'width': 93, 'height': 59}, 'mouth-smile': {'x': 23.69, 'y': -32.24, 'rotation': -70.63, 'width': 93, 'height': 59}}, 'rear-upper-arm': {'rear-upper-arm': {'x': 21.13, 'y': 4.09, 'rotation': 89.33, 'width': 40, 'height': 87}}, 'front-upper-arm': {'front-upper-arm': {'x': 25.2, 'y': 1.17, 'rotation': 97.9, 'width': 46, 'height': 97}}, 'front-bracer': {'front-bracer': {'x': 12.03, 'y': -1.68, 'rotation': 79.6, 'width': 58, 'height': 80}}, 'front-foot': {'front-foot': {'x': 29.52, 'y': 7.84, 'rotation': 18.69, 'width': 126, 'height': 69}}, 'goggles': {'goggles': {'x': 97.08, 'y': 6.54, 'rotation': -70.63, 'width': 261, 'height': 166}}, 'front-shin': {'front-shin': {'x': 55.12, 'y': -3.54, 'rotation': 96.59, 'width': 82, 'height': 184}}, 'neck': {'neck': {'x': 9.77, 'y': -3.01, 'rotation': -55.22, 'width': 36, 'height': 41}}, 'head': {'head': {'x': 128.96, 'y': 0.3, 'rotation': -70.63, 'width': 271, 'height': 298}}, 'muzzle': {'muzzle01': {'x': 159.26, 'y': 5.83, 'scaleX': 4, 'scaleY': 4, 'rotation': 0.15, 'width': 133, 'height': 79}, 'muzzle02': {'x': 191.23, 'y': 5.91, 'scaleX': 4, 'scaleY': 4, 'rotation': 0.15, 'width': 135, 'height': 84}, 'muzzle03': {'x': 230.67, 'y': 6.02, 'scaleX': 4, 'scaleY': 4, 'rotation': 0.15, 'width': 166, 'height': 106}, 'muzzle04': {'x': 218.54, 'y': 5.99, 'scaleX': 4, 'scaleY': 4, 'rotation': 0.15, 'width': 149, 'height': 90}}, 'rear-bracer': {'rear-bracer': {'x': 11.15, 'y': -2.2, 'rotation': 66.17, 'width': 56, 'height': 72}}, 'rear-thigh': {'rear-thigh': {'x': 33.11, 'y': -4.11, 'rotation': 72.54, 'width': 55, 'height': 94}}, 'front-fist': {'front-fist-closed': {'x': 35.5, 'y': 6, 'rotation': 67.16, 'width': 75, 'height': 82}, 'front-fist-open': {'x': 39.57, 'y': 7.76, 'rotation': 67.16, 'width': 86, 'height': 87}}, 'eye': {'eye-indifferent': {'x': 85.72, 'y': -28.18, 'rotation': -70.63, 'width': 93, 'height': 89}, 'eye-surprised': {'x': 85.72, 'y': -28.18, 'rotation': -70.63, 'width': 93, 'height': 89}}, 'front-thigh': {'front-thigh': {'x': 42.48, 'y': 4.45, 'rotation': 84.87, 'width': 45, 'height': 112}}, 'torso': {'torso': {'x': 63.61, 'y': 7.12, 'rotation': -94.54, 'width': 98, 'height': 180}}, 'rear-foot': {'rear-foot': {'x': 31.51, 'y': 3.58, 'rotation': 23.07, 'width': 113, 'height': 60}}}}], 'events': {'footstep': {}}, 'animations': {'run': {'slots': {'mouth': {'attachment': [{'name': 'mouth-grind'}]}}, 'bones': {'front-thigh': {'rotate': [{'angle': -4.6000000000000005}, {'time': 0.46, 'angle': 14.9}, {'time': 0.59, 'angle': -22.9}, {'time': 0.73, 'angle': -27.1}, {'time': 0.86, 'angle': 8.2}, {'time': 0.99, 'angle': 11.899999999999999}, {'time': 1.16, 'angle': 13.0}, {'time': 1.33, 'angle': -16.299999999999997}, {'time': 1.49, 'angle': -12.3}, {'time': 1.71, 'angle': 28.799999999999997}, {'time': 1.9, 'angle': 20.299999999999997}, {'time': 2.48, 'angle': 1.1}, {'time': 2.9, 'angle': 8.299999999999999}, {'time': 3.03, 'angle': 17.1}, {'time': 3.16, 'angle': 29.1}, {'time': 3.28, 'angle': -2.0}, {'time': 3.41, 'angle': 30.7}, {'time': 3.54, 'angle': -15.700000000000001}, {'time': 3.67, 'angle': -4.6000000000000005}, {'time': 3.93, 'angle': -27.799999999999997}, {'time': 4.06, 'angle': 26.8}, {'time': 4.19, 'angle': 15.700000000000001}, {'time': 4.33, 'angle': 7.9}, {'time': 4.46, 'angle': 3.8}, {'time': 4.58, 'angle': 18.9}, {'time': 4.73, 'angle': 28.2}, {'time': 4.86, 'angle': 0.0}, {'time': 4.99, 'angle': -23.599999999999998}, {'time': 5.13, 'angle': -23.599999999999998}, {'time': 5.26, 'angle': -11.100000000000001}, {'time': 5.39, 'angle': 4.6000000000000005}, {'time': 5.52, 'angle': -26.8}, {'time': 5.65, 'angle': 2.7}, {'time': 5.78, 'angle': 23.599999999999998}, {'time': 5.91, 'angle': -26.8}, {'time': 6.04, 'angle': -4.6000000000000005}], 'translate': [{'x': 1.2, 'y': -0.6}, {'time': 0.46, 'x': 0.6, 'y': 7.8}, {'time': 0.59, 'x': -9.0, 'y': -10.2}, {'time': 0.73, 'x': -6.6, 'y': -3.0}, {'time': 0.86, 'x': 9.6, 'y': 10.2}, {'time': 0.99, 'x': 2.4, 'y': 6.0}, {'time': 1.16, 'x': 1.7999999999999998, 'y': 6.6}, {'time': 1.33, 'x': -0.6, 'y': -9.6}, {'time': 1.49, 'x': 3.5999999999999996, 'y': -10.2}, {'time': 1.71, 'x': -6.6, 'y': 1.7999999999999998}, {'time': 1.9, 'x': -1.7999999999999998, 'y': 3.5999999999999996}, {'time': 2.48, 'x': 10.799999999999999, 'y': 1.2}, {'time': 2.9, 'x': 6.6, 'y': 7.199999999999999}, {'time': 3.03, 'x': -0.6, 'y': 4.2}, {'time': 3.16, 'x': -7.8, 'y': 1.7999999999999998}, {'time': 3.28, 'x': 9.0, 'y': -1.7999999999999998}, {'time': 3.41, 'x': -8.4, 'y': 0.6}, {'time': 3.54, 'x': 0.0, 'y': -1.2}, {'time': 3.67, 'x': 1.2, 'y': -0.6}, {'time': 3.93, 'x': -4.8, 'y': -1.7999999999999998}, {'time': 4.06, 'x': -2.4, 'y': 1.2}, {'time': 4.19, 'x': 0.0, 'y': 0.6}, {'time': 4.33, 'x': 0.6, 'y': 0.6}, {'time': 4.46, 'x': 3.0, 'y': 1.2}, {'time': 4.58, 'x': -0.6, 'y': 1.7999999999999998}, {'time': 4.73, 'x': -1.7999999999999998, 'y': 0.6}, {'time': 4.86, 'x': 1.7999999999999998, 'y': 0.0}, {'time': 4.99, 'x': -0.6, 'y': -0.6}, {'time': 5.13, 'x': -1.7999999999999998, 'y': -1.7999999999999998}, {'time': 5.26, 'x': 0.6, 'y': -1.2}, {'time': 5.39, 'x': 1.2, 'y': 0.6}, {'time': 5.52, 'x': -1.2, 'y': -0.6}, {'time': 5.65, 'x': 6.6, 'y': 1.7999999999999998}, {'time': 5.78, 'x': -1.2, 'y': 1.2}, {'time': 5.91, 'x': -1.2, 'y': -0.6}, {'time': 6.04, 'x': 1.2, 'y': -0.6}]}, 'torso': {'rotate': [{'angle': -39.71}, {'time': 0.2, 'angle': -57.29}, {'time': 0.4, 'angle': -39.71}, {'time': 0.6, 'angle': -57.29}, {'time': 0.8, 'angle': -39.71}]}, 'rear-thigh': {'rotate': [{'angle': 31.400000000000002}, {'time': 0.46, 'angle': 8.8}, {'time': 0.59, 'angle': -23.599999999999998}, {'time': 0.73, 'angle': -18.2}, {'time': 0.86, 'angle': 15.700000000000001}, {'time': 0.99, 'angle': 4.0}, {'time': 1.16, 'angle': 13.3}, {'time': 1.33, 'angle': -22.599999999999998}, {'time': 1.49, 'angle': -10.8}, {'time': 1.71, 'angle': 11.100000000000001}, {'time': 2.9, 'angle': 15.700000000000001}, {'time': 3.03, 'angle': 8.5}, {'time': 3.16, 'angle': 9.3}, {'time': 3.28, 'angle': -29.0}, {'time': 3.41, 'angle': -15.700000000000001}, {'time': 3.54, 'angle': -15.700000000000001}, {'time': 3.67, 'angle': -28.2}, {'time': 3.93, 'angle': -28.2}, {'time': 4.06, 'angle': 15.700000000000001}, {'time': 4.19, 'angle': 2.4}, {'time': 4.33, 'angle': 28.2}, {'time': 4.46, 'angle': 11.100000000000001}, {'time': 4.58, 'angle': 11.100000000000001}, {'time': 4.73, 'angle': 0.0}, {'time': 4.86, 'angle': 29.0}, {'time': 4.99, 'angle': -2.0}, {'time': 5.13, 'angle': -15.700000000000001}, {'time': 5.26, 'angle': 31.400000000000002}, {'time': 5.39, 'angle': 0.0}, {'time': 5.52, 'angle': 29.4}, {'time': 5.65, 'angle': 2.0}, {'time': 5.78, 'angle': 0.0}, {'time': 5.91, 'angle': 31.400000000000002}, {'time': 6.04, 'angle': 31.400000000000002}], 'translate': [{'x': -1.2, 'y': 0.0}, {'time': 0.46, 'x': 6.0, 'y': 7.199999999999999}, {'time': 0.59, 'x': -9.0, 'y': -9.0}, {'time': 0.73, 'x': -1.2, 'y': -4.8}, {'time': 0.86, 'x': 0.0, 'y': 11.4}, {'time': 0.99, 'x': 12.6, 'y': 5.3999999999999995}, {'time': 1.16, 'x': 1.7999999999999998, 'y': 7.199999999999999}, {'time': 1.33, 'x': -8.4, 'y': -10.2}, {'time': 1.49, 'x': 4.8, 'y': -9.0}, {'time': 1.71, 'x': 1.2, 'y': 2.4}, {'time': 2.9, 'x': 0.0, 'y': 9.6}, {'time': 3.03, 'x': 4.2, 'y': 4.8}, {'time': 3.16, 'x': 1.7999999999999998, 'y': 2.4}, {'time': 3.28, 'x': -2.4, 'y': -0.6}, {'time': 3.41, 'x': 0.0, 'y': -0.6}, {'time': 3.54, 'x': 0.0, 'y': -1.2}, {'time': 3.67, 'x': -1.7999999999999998, 'y': -0.6}, {'time': 3.93, 'x': -3.5999999999999996, 'y': -1.2}, {'time': 4.06, 'x': 0.0, 'y': 0.6}, {'time': 4.19, 'x': 4.8, 'y': 1.2}, {'time': 4.33, 'x': -3.5999999999999996, 'y': 1.2}, {'time': 4.46, 'x': 0.6, 'y': 1.2}, {'time': 4.58, 'x': 0.6, 'y': 1.2}, {'time': 4.73, 'x': 0.6, 'y': 0.0}, {'time': 4.86, 'x': -2.4, 'y': 0.6}, {'time': 4.99, 'x': 3.0, 'y': -0.6}, {'time': 5.13, 'x': 0.0, 'y': -3.0}, {'time': 5.26, 'x': -1.7999999999999998, 'y': 0.0}, {'time': 5.39, 'x': 2.4, 'y': 0.0}, {'time': 5.52, 'x': -3.0, 'y': 0.6}, {'time': 5.65, 'x': 3.0, 'y': 0.6}, {'time': 5.78, 'x': 1.2, 'y': 0.0}, {'time': 5.91, 'x': -0.6, 'y': 0.0}, {'time': 6.04, 'x': -1.2, 'y': 0.0}]}, 'rear-shin': {'rotate': [{'angle': 0.0}, {'time': 0.46, 'angle': 9.200000000000001}, {'time': 0.59, 'angle': -19.9}, {'time': 0.73, 'angle': -13.899999999999999}, {'time': 0.86, 'angle': 20.7}, {'time': 0.99, 'angle': 9.0}, {'time': 1.16, 'angle': 17.7}, {'time': 1.33, 'angle': -18.2}, {'time': 1.49, 'angle': -14.7}, {'time': 1.71, 'angle': 2.4}, {'time': 1.9, 'angle': -26.8}, {'time': 2.09, 'angle': 31.400000000000002}, {'time': 2.27, 'angle': 26.200000000000003}, {'time': 2.48, 'angle': 31.400000000000002}, {'time': 2.63, 'angle': 20.299999999999997}, {'time': 2.77, 'angle': 31.400000000000002}, {'time': 2.9, 'angle': 0.0}, {'time': 3.03, 'angle': 14.1}, {'time': 3.16, 'angle': 0.3}, {'time': 3.28, 'angle': 26.8}, {'time': 3.41, 'angle': 15.700000000000001}, {'time': 3.54, 'angle': 7.9}, {'time': 3.67, 'angle': 25.5}, {'time': 3.93, 'angle': -27.0}, {'time': 4.06, 'angle': -2.4}, {'time': 4.19, 'angle': 0.0}, {'time': 4.33, 'angle': 0.0}, {'time': 4.46, 'angle': 29.700000000000003}, {'time': 4.58, 'angle': 20.299999999999997}, {'time': 4.73, 'angle': 25.5}, {'time': 4.86, 'angle': -7.9}, {'time': 4.99, 'angle': 1.2}, {'time': 5.13, 'angle': -4.6000000000000005}, {'time': 5.26, 'angle': -1.0}, {'time': 5.39, 'angle': -23.599999999999998}, {'time': 5.65, 'angle': 24.5}, {'time': 5.78, 'angle': -21.6}, {'time': 5.91, 'angle': -3.1}, {'time': 6.04, 'angle': 0.0}], 'translate': [{'x': 1.7999999999999998, 'y': 0.0}, {'time': 0.46, 'x': 6.0, 'y': 7.8}, {'time': 0.59, 'x': -2.4, 'y': -5.3999999999999995}, {'time': 0.73, 'x': 1.7999999999999998, 'y': -9.6}, {'time': 0.86, 'x': -7.199999999999999, 'y': 13.2}, {'time': 0.99, 'x': 2.4, 'y': 3.0}, {'time': 1.16, 'x': -1.7999999999999998, 'y': 9.0}, {'time': 1.33, 'x': -3.0, 'y': -12.0}, {'time': 1.49, 'x': 0.6, 'y': -6.0}, {'time': 1.71, 'x': 21.599999999999998, 'y': 5.3999999999999995}, {'time': 1.9, 'x': -1.2, 'y': -0.6}, {'time': 2.09, 'x': -4.2, 'y': 0.0}, {'time': 2.27, 'x': -4.2, 'y': 2.4}, {'time': 2.48, 'x': -1.7999999999999998, 'y': 0.0}, {'time': 2.63, 'x': -0.6, 'y': 1.2}, {'time': 2.77, 'x': -4.8, 'y': 0.0}, {'time': 2.9, 'x': 0.6, 'y': 0.0}, {'time': 3.03, 'x': 0.6, 'y': 3.5999999999999996}, {'time': 3.16, 'x': 19.2, 'y': 0.6}, {'time': 3.28, 'x': -2.4, 'y': 1.2}, {'time': 3.41, 'x': 0.0, 'y': 1.7999999999999998}, {'time': 3.54, 'x': 0.6, 'y': 0.6}, {'time': 3.67, 'x': -3.5999999999999996, 'y': 2.4}, {'time': 3.93, 'x': -9.0, 'y': -4.2}, {'time': 4.06, 'x': 7.199999999999999, 'y': -1.7999999999999998}, {'time': 4.19, 'x': 4.8, 'y': 0.0}, {'time': 4.33, 'x': 7.199999999999999, 'y': 0.0}, {'time': 4.46, 'x': -27.0, 'y': 4.8}, {'time': 4.58, 'x': -0.6, 'y': 1.2}, {'time': 4.73, 'x': -1.7999999999999998, 'y': 1.2}, {'time': 4.86, 'x': 0.6, 'y': -0.6}, {'time': 4.99, 'x': 4.8, 'y': 0.6}, {'time': 5.13, 'x': 2.4, 'y': -1.2}, {'time': 5.26, 'x': 11.4, 'y': -1.2}, {'time': 5.39, 'x': -1.7999999999999998, 'y': -1.7999999999999998}, {'time': 5.65, 'x': -7.199999999999999, 'y': 6.0}, {'time': 5.78, 'x': -1.2, 'y': -1.7999999999999998}, {'time': 5.91, 'x': 18.599999999999998, 'y': -6.0}, {'time': 6.04, 'x': 1.7999999999999998, 'y': 0.0}]}, 'front-upper-arm': {'rotate': [{'angle': 15.700000000000001}, {'time': 0.46, 'angle': 12.9}, {'time': 0.59, 'angle': -18.3}, {'time': 0.86, 'angle': 23.0}, {'time': 0.99, 'angle': 7.0}, {'time': 1.16, 'angle': 15.1}, {'time': 1.33, 'angle': -15.3}, {'time': 1.49, 'angle': -17.4}, {'time': 1.71, 'angle': 23.599999999999998}, {'time': 2.63, 'angle': 5.1}, {'time': 2.9, 'angle': 13.700000000000001}, {'time': 3.03, 'angle': 21.299999999999997}, {'time': 3.16, 'angle': 23.599999999999998}, {'time': 3.28, 'angle': 7.9}, {'time': 3.41, 'angle': 28.2}, {'time': 3.54, 'angle': -21.6}, {'time': 3.67, 'angle': 0.0}, {'time': 3.93, 'angle': -27.200000000000003}, {'time': 4.06, 'angle': 4.6000000000000005}, {'time': 4.19, 'angle': -29.0}, {'time': 4.33, 'angle': 9.3}, {'time': 4.46, 'angle': 7.9}, {'time': 4.58, 'angle': 15.700000000000001}, {'time': 4.73, 'angle': 15.700000000000001}, {'time': 4.86, 'angle': 7.9}, {'time': 4.99, 'angle': -15.700000000000001}, {'time': 5.13, 'angle': -25.299999999999997}, {'time': 5.26, 'angle': 11.100000000000001}, {'time': 5.39, 'angle': -20.299999999999997}, {'time': 5.52, 'angle': -0.8999999999999999}, {'time': 5.65, 'angle': 0.0}, {'time': 5.78, 'angle': 20.299999999999997}, {'time': 5.91, 'angle': 31.400000000000002}, {'time': 6.04, 'angle': 15.700000000000001}], 'translate': [{'x': 0.0, 'y': 3.0}, {'time': 0.46, 'x': 1.2, 'y': 4.2}, {'time': 0.59, 'x': -3.0, 'y': -11.4}, {'time': 0.86, 'x': -5.3999999999999995, 'y': 6.0}, {'time': 0.99, 'x': 7.8, 'y': 6.6}, {'time': 1.16, 'x': 0.6, 'y': 9.6}, {'time': 1.33, 'x': 0.6, 'y': -16.2}, {'time': 1.49, 'x': -1.7999999999999998, 'y': -10.799999999999999}, {'time': 1.71, 'x': -2.4, 'y': 2.4}, {'time': 2.63, 'x': 13.799999999999999, 'y': 7.8}, {'time': 2.9, 'x': 1.2, 'y': 6.0}, {'time': 3.03, 'x': -3.0, 'y': 4.8}, {'time': 3.16, 'x': -1.2, 'y': 1.2}, {'time': 3.28, 'x': 1.2, 'y': 1.2}, {'time': 3.41, 'x': -1.7999999999999998, 'y': 0.6}, {'time': 3.54, 'x': -1.2, 'y': -1.7999999999999998}, {'time': 3.67, 'x': 0.6, 'y': 0.0}, {'time': 3.93, 'x': -5.3999999999999995, 'y': -2.4}, {'time': 4.06, 'x': 4.8, 'y': 2.4}, {'time': 4.19, 'x': -7.199999999999999, 'y': -1.7999999999999998}, {'time': 4.33, 'x': 1.7999999999999998, 'y': 2.4}, {'time': 4.46, 'x': 4.2, 'y': 4.2}, {'time': 4.58, 'x': 0.0, 'y': 1.7999999999999998}, {'time': 4.73, 'x': 0.0, 'y': 0.6}, {'time': 4.86, 'x': 0.6, 'y': 0.6}, {'time': 4.99, 'x': 0.0, 'y': -1.7999999999999998}, {'time': 5.13, 'x': -6.0, 'y': -4.2}, {'time': 5.26, 'x': 0.6, 'y': 1.2}, {'time': 5.39, 'x': -0.6, 'y': -1.2}, {'time': 5.52, 'x': 6.6, 'y': -0.6}, {'time': 5.65, 'x': 1.7999999999999998, 'y': 0.0}, {'time': 5.78, 'x': -0.6, 'y': 1.2}, {'time': 5.91, 'x': -1.2, 'y': 0.0}, {'time': 6.04, 'x': 0.0, 'y': 3.0}]}, 'front-bracer': {'rotate': [{'angle': 31.400000000000002}, {'time': 0.46, 'angle': 8.8}, {'time': 0.99, 'angle': 27.599999999999998}, {'time': 1.33, 'angle': -4.3}, {'time': 1.71, 'angle': -27.599999999999998}, {'time': 2.48, 'angle': 4.0}, {'time': 3.03, 'angle': 5.300000000000001}, {'time': 3.16, 'angle': -31.099999999999998}, {'time': 3.28, 'angle': 1.7000000000000002}, {'time': 5.13, 'angle': -31.099999999999998}, {'time': 5.78, 'angle': -0.8999999999999999}, {'time': 5.91, 'angle': 31.400000000000002}], 'translate': [{'x': -1.7999999999999998, 'y': 0.0}, {'time': 0.46, 'x': 61.199999999999996, 'y': 73.8}, {'time': 0.99, 'x': -3.0, 'y': 1.2}, {'time': 1.33, 'x': 7.8, 'y': -3.5999999999999996}, {'time': 1.71, 'x': -21.0, 'y': -8.4}, {'time': 2.48, 'x': 12.6, 'y': 5.3999999999999995}, {'time': 3.03, 'x': 16.2, 'y': 9.6}, {'time': 3.16, 'x': -17.4, 'y': -0.6}, {'time': 3.28, 'x': 18.0, 'y': 3.0}, {'time': 5.13, 'x': -20.4, 'y': -0.6}, {'time': 5.78, 'x': 6.6, 'y': -0.6}, {'time': 5.91, 'x': -1.7999999999999998, 'y': 0.0}]}, 'front-fist': {'rotate': [{'angle': -7.9}, {'time': 0.46, 'angle': 9.6}, {'time': 0.86, 'angle': -2.1}, {'time': 0.99, 'angle': 4.9}, {'time': 1.16, 'angle': 29.2}, {'time': 1.49, 'angle': -18.0}, {'time': 1.71, 'angle': 15.700000000000001}, {'time': 1.9, 'angle': 12.1}, {'time': 2.27, 'angle': -0.5}, {'time': 2.9, 'angle': 3.1}, {'time': 3.03, 'angle': 7.9}, {'time': 3.16, 'angle': 31.400000000000002}, {'time': 3.28, 'angle': 0.5}, {'time': 3.41, 'angle': 31.299999999999997}, {'time': 3.54, 'angle': -15.700000000000001}, {'time': 3.67, 'angle': 0.0}, {'time': 3.93, 'angle': -30.5}, {'time': 4.06, 'angle': 4.6000000000000005}, {'time': 4.46, 'angle': -2.0}, {'time': 4.58, 'angle': 30.9}, {'time': 4.73, 'angle': -26.8}, {'time': 4.86, 'angle': 0.4}, {'time': 4.99, 'angle': -30.5}, {'time': 5.13, 'angle': -11.100000000000001}, {'time': 5.26, 'angle': 9.8}, {'time': 5.39, 'angle': 7.9}, {'time': 5.78, 'angle': 3.7}, {'time': 5.91, 'angle': -26.8}, {'time': 6.04, 'angle': -7.9}], 'translate': [{'x': 0.6, 'y': -0.6}, {'time': 0.46, 'x': 63.599999999999994, 'y': 90.0}, {'time': 0.86, 'x': 11.4, 'y': -2.4}, {'time': 0.99, 'x': 7.8, 'y': 4.2}, {'time': 1.16, 'x': -34.8, 'y': 7.8}, {'time': 1.49, 'x': -4.8, 'y': -20.4}, {'time': 1.71, 'x': 0.0, 'y': 0.6}, {'time': 1.9, 'x': 1.7999999999999998, 'y': 4.8}, {'time': 2.27, 'x': 13.2, 'y': -0.6}, {'time': 2.9, 'x': 30.0, 'y': 9.6}, {'time': 3.03, 'x': 1.7999999999999998, 'y': 1.7999999999999998}, {'time': 3.16, 'x': -39.6, 'y': 0.0}, {'time': 3.28, 'x': 39.6, 'y': 1.7999999999999998}, {'time': 3.41, 'x': -39.6, 'y': 0.6}, {'time': 3.54, 'x': 0.0, 'y': -0.6}, {'time': 3.67, 'x': 33.0, 'y': 0.0}, {'time': 3.93, 'x': -13.799999999999999, 'y': -1.2}, {'time': 4.06, 'x': 6.0, 'y': 3.0}, {'time': 4.46, 'x': 6.0, 'y': -1.2}, {'time': 4.58, 'x': -34.8, 'y': 1.7999999999999998}, {'time': 4.73, 'x': -1.2, 'y': -0.6}, {'time': 4.86, 'x': 34.199999999999996, 'y': 1.2}, {'time': 4.99, 'x': -33.6, 'y': -3.0}, {'time': 5.13, 'x': 0.6, 'y': -1.2}, {'time': 5.26, 'x': 1.2, 'y': 1.7999999999999998}, {'time': 5.39, 'x': 0.6, 'y': 0.6}, {'time': 5.78, 'x': 7.8, 'y': 3.0}, {'time': 5.91, 'x': -6.0, 'y': -3.0}, {'time': 6.04, 'x': 0.6, 'y': -0.6}]}, 'rear-upper-arm': {'rotate': [{'angle': 13.3}, {'time': 0.46, 'angle': 7.3}, {'time': 0.59, 'angle': -18.700000000000003}, {'time': 0.73, 'angle': -21.400000000000002}, {'time': 0.86, 'angle': 16.200000000000003}, {'time': 0.99, 'angle': 5.4}, {'time': 1.16, 'angle': 14.6}, {'time': 1.33, 'angle': -17.6}, {'time': 1.49, 'angle': -10.8}, {'time': 1.71, 'angle': 25.5}, {'time': 2.27, 'angle': 4.6000000000000005}, {'time': 2.63, 'angle': 16.599999999999998}, {'time': 2.77, 'angle': 23.599999999999998}, {'time': 3.03, 'angle': 10.4}, {'time': 3.16, 'angle': 15.700000000000001}, {'time': 3.28, 'angle': 31.400000000000002}, {'time': 3.41, 'angle': 15.700000000000001}, {'time': 3.54, 'angle': -26.8}, {'time': 3.67, 'angle': 31.400000000000002}, {'time': 3.79, 'angle': -11.899999999999999}, {'time': 3.93, 'angle': 25.5}, {'time': 4.06, 'angle': 5.8999999999999995}, {'time': 4.19, 'angle': -23.599999999999998}, {'time': 4.33, 'angle': 20.299999999999997}, {'time': 4.46, 'angle': 20.299999999999997}, {'time': 4.58, 'angle': 20.299999999999997}, {'time': 4.73, 'angle': 11.100000000000001}, {'time': 4.86, 'angle': 31.400000000000002}, {'time': 4.99, 'angle': -11.100000000000001}, {'time': 5.13, 'angle': -9.8}, {'time': 5.26, 'angle': 11.100000000000001}, {'time': 5.39, 'angle': -15.700000000000001}, {'time': 5.52, 'angle': -18.9}, {'time': 5.65, 'angle': -11.100000000000001}, {'time': 5.78, 'angle': 7.9}, {'time': 5.91, 'angle': 23.599999999999998}, {'time': 6.04, 'angle': 13.3}], 'translate': [{'x': 0.6, 'y': 2.4}, {'time': 0.46, 'x': 5.3999999999999995, 'y': 4.8}, {'time': 0.59, 'x': -3.0, 'y': -9.6}, {'time': 0.73, 'x': -5.3999999999999995, 'y': -8.4}, {'time': 0.86, 'x': -0.6, 'y': 13.2}, {'time': 0.99, 'x': 12.0, 'y': 7.199999999999999}, {'time': 1.16, 'x': 1.2, 'y': 10.799999999999999}, {'time': 1.33, 'x': -3.0, 'y': -15.6}, {'time': 1.49, 'x': 5.3999999999999995, 'y': -10.2}, {'time': 1.71, 'x': -3.5999999999999996, 'y': 2.4}, {'time': 2.27, 'x': 1.2, 'y': 0.6}, {'time': 2.63, 'x': -0.6, 'y': 6.6}, {'time': 2.77, 'x': -0.6, 'y': 0.6}, {'time': 3.03, 'x': 6.0, 'y': 10.2}, {'time': 3.16, 'x': 0.0, 'y': 0.6}, {'time': 3.28, 'x': -1.7999999999999998, 'y': 0.0}, {'time': 3.41, 'x': 0.0, 'y': 0.6}, {'time': 3.54, 'x': -1.2, 'y': -0.6}, {'time': 3.67, 'x': -1.2, 'y': 0.0}, {'time': 3.79, 'x': 1.2, 'y': -3.0}, {'time': 3.93, 'x': -1.7999999999999998, 'y': 1.2}, {'time': 4.06, 'x': 1.7999999999999998, 'y': 1.2}, {'time': 4.19, 'x': -0.6, 'y': -0.6}, {'time': 4.33, 'x': -1.2, 'y': 2.4}, {'time': 4.46, 'x': -1.2, 'y': 2.4}, {'time': 4.58, 'x': -0.6, 'y': 1.2}, {'time': 4.73, 'x': 0.6, 'y': 1.2}, {'time': 4.86, 'x': -0.6, 'y': 0.0}, {'time': 4.99, 'x': 0.6, 'y': -1.2}, {'time': 5.13, 'x': 1.2, 'y': -1.7999999999999998}, {'time': 5.26, 'x': 0.6, 'y': 1.2}, {'time': 5.39, 'x': 0.0, 'y': -1.2}, {'time': 5.52, 'x': -0.6, 'y': -1.7999999999999998}, {'time': 5.65, 'x': 0.6, 'y': -1.2}, {'time': 5.78, 'x': 0.6, 'y': 0.6}, {'time': 5.91, 'x': -1.2, 'y': 1.2}, {'time': 6.04, 'x': 0.6, 'y': 2.4}]}, 'rear-bracer': {'rotate': [{'angle': 23.599999999999998}, {'time': 0.46, 'angle': 10.0}, {'time': 0.59, 'angle': -18.5}, {'time': 0.73, 'angle': -19.2}, {'time': 0.86, 'angle': 15.1}, {'time': 0.99, 'angle': 8.9}, {'time': 1.16, 'angle': 17.6}, {'time': 1.33, 'angle': -16.1}, {'time': 1.49, 'angle': -15.700000000000001}, {'time': 1.71, 'angle': 8.8}, {'time': 1.9, 'angle': 9.8}, {'time': 2.63, 'angle': -28.2}, {'time': 2.77, 'angle': 31.400000000000002}, {'time': 2.9, 'angle': 12.9}, {'time': 3.03, 'angle': 17.4}, {'time': 3.16, 'angle': 4.6000000000000005}, {'time': 3.28, 'angle': 26.8}, {'time': 3.41, 'angle': 0.0}, {'time': 3.54, 'angle': 0.0}, {'time': 3.67, 'angle': 15.700000000000001}, {'time': 3.79, 'angle': 4.6000000000000005}, {'time': 3.93, 'angle': -2.8000000000000003}, {'time': 4.06, 'angle': 4.0}, {'time': 4.19, 'angle': 15.700000000000001}, {'time': 4.33, 'angle': 0.0}, {'time': 4.46, 'angle': 29.0}, {'time': 4.58, 'angle': -29.4}, {'time': 4.73, 'angle': 4.6000000000000005}, {'time': 4.86, 'angle': -26.8}, {'time': 4.99, 'angle': -11.100000000000001}, {'time': 5.13, 'angle': -15.700000000000001}, {'time': 5.26, 'angle': 19.5}, {'time': 5.39, 'angle': 31.400000000000002}, {'time': 5.52, 'angle': -15.700000000000001}, {'time': 5.65, 'angle': -21.6}, {'time': 5.78, 'angle': 0.0}, {'time': 5.91, 'angle': 31.400000000000002}, {'time': 6.04, 'angle': 23.599999999999998}], 'translate': [{'x': -1.2, 'y': 1.2}, {'time': 0.46, 'x': 6.6, 'y': 10.2}, {'time': 0.59, 'x': -2.4, 'y': -8.4}, {'time': 0.73, 'x': -2.4, 'y': -6.6}, {'time': 0.86, 'x': 0.6, 'y': 9.6}, {'time': 0.99, 'x': 5.3999999999999995, 'y': 6.6}, {'time': 1.16, 'x': -1.7999999999999998, 'y': 9.6}, {'time': 1.33, 'x': -0.6, 'y': -14.399999999999999}, {'time': 1.49, 'x': 0.0, 'y': -10.2}, {'time': 1.71, 'x': 3.0, 'y': 3.5999999999999996}, {'time': 1.9, 'x': 4.8, 'y': 7.199999999999999}, {'time': 2.63, 'x': -10.799999999999999, 'y': -3.5999999999999996}, {'time': 2.77, 'x': -0.6, 'y': 0.0}, {'time': 2.9, 'x': 1.2, 'y': 4.2}, {'time': 3.03, 'x': -0.6, 'y': 3.5999999999999996}, {'time': 3.16, 'x': 1.2, 'y': 0.6}, {'time': 3.28, 'x': -1.2, 'y': 0.6}, {'time': 3.41, 'x': 0.6, 'y': 0.0}, {'time': 3.54, 'x': 0.6, 'y': 0.0}, {'time': 3.67, 'x': 0.0, 'y': 1.2}, {'time': 3.79, 'x': 2.4, 'y': 1.2}, {'time': 3.93, 'x': 4.2, 'y': -1.2}, {'time': 4.06, 'x': 4.2, 'y': 1.7999999999999998}, {'time': 4.19, 'x': 0.0, 'y': 3.0}, {'time': 4.33, 'x': 1.7999999999999998, 'y': 0.0}, {'time': 4.46, 'x': -7.199999999999999, 'y': 1.7999999999999998}, {'time': 4.58, 'x': -3.0, 'y': -0.6}, {'time': 4.73, 'x': 2.4, 'y': 1.2}, {'time': 4.86, 'x': -1.2, 'y': -0.6}, {'time': 4.99, 'x': 0.6, 'y': -1.2}, {'time': 5.13, 'x': 0.0, 'y': -2.4}, {'time': 5.26, 'x': -1.2, 'y': 3.0}, {'time': 5.39, 'x': -0.6, 'y': 0.0}, {'time': 5.52, 'x': 0.0, 'y': -2.4}, {'time': 5.65, 'x': -2.4, 'y': -3.5999999999999996}, {'time': 5.78, 'x': 1.2, 'y': 0.0}, {'time': 5.91, 'x': -1.7999999999999998, 'y': 0.0}, {'time': 6.04, 'x': -1.2, 'y': 1.2}]}, 'neck': {'rotate': [{'angle': 17.4}, {'time': 0.46, 'angle': 7.9}, {'time': 0.59, 'angle': -16.1}, {'time': 0.86, 'angle': 21.099999999999998}, {'time': 0.99, 'angle': 6.6000000000000005}, {'time': 1.16, 'angle': 14.6}, {'time': 1.33, 'angle': -16.4}, {'time': 1.49, 'angle': -10.2}, {'time': 1.71, 'angle': 25.2}, {'time': 2.27, 'angle': 4.6000000000000005}, {'time': 2.63, 'angle': 13.700000000000001}, {'time': 2.77, 'angle': 25.5}, {'time': 2.9, 'angle': 9.0}, {'time': 3.03, 'angle': 14.2}, {'time': 3.16, 'angle': 15.700000000000001}, {'time': 3.28, 'angle': 11.100000000000001}, {'time': 3.41, 'angle': -27.599999999999998}, {'time': 3.54, 'angle': -19.5}, {'time': 3.67, 'angle': 28.2}, {'time': 3.79, 'angle': -11.100000000000001}, {'time': 3.93, 'angle': 20.299999999999997}, {'time': 4.06, 'angle': 0}, {'time': 4.19, 'angle': 20.299999999999997}, {'time': 4.33, 'angle': 15.700000000000001}, {'time': 4.46, 'angle': 12.5}, {'time': 4.58, 'angle': 31.400000000000002}, {'time': 4.73, 'angle': 12.5}, {'time': 4.86, 'angle': 23.599999999999998}, {'time': 4.99, 'angle': -12.5}, {'time': 5.13, 'angle': -11.100000000000001}, {'time': 5.26, 'angle': 31.400000000000002}, {'time': 5.39, 'angle': -7.9}, {'time': 5.52, 'angle': -20.299999999999997}, {'time': 5.65, 'angle': -4.6000000000000005}, {'time': 5.78, 'angle': 20.299999999999997}, {'time': 5.91, 'angle': 15.700000000000001}, {'time': 6.04, 'angle': 17.4}], 'translate': [{'x': -0.6, 'y': 3.5999999999999996}, {'time': 0.46, 'x': 3.5999999999999996, 'y': 3.5999999999999996}, {'time': 0.59, 'x': -0.6, 'y': -14.399999999999999}, {'time': 0.86, 'x': -5.3999999999999995, 'y': 9.0}, {'time': 0.99, 'x': 10.799999999999999, 'y': 8.4}, {'time': 1.16, 'x': 1.2, 'y': 10.799999999999999}, {'time': 1.33, 'x': -1.2, 'y': -18.599999999999998}, {'time': 1.49, 'x': 7.8, 'y': -12.6}, {'time': 1.71, 'x': -4.2, 'y': 3.0}, {'time': 2.27, 'x': 2.4, 'y': 1.2}, {'time': 2.63, 'x': 1.2, 'y': 6.0}, {'time': 2.77, 'x': -3.5999999999999996, 'y': 2.4}, {'time': 2.9, 'x': 2.4, 'y': 3.0}, {'time': 3.03, 'x': 1.2, 'y': 7.8}, {'time': 3.16, 'x': 0.0, 'y': 1.7999999999999998}, {'time': 3.28, 'x': 1.2, 'y': 2.4}, {'time': 3.41, 'x': -3.0, 'y': -1.2}, {'time': 3.54, 'x': -1.2, 'y': -3.0}, {'time': 3.67, 'x': -1.7999999999999998, 'y': 0.6}, {'time': 3.79, 'x': 1.2, 'y': -2.4}, {'time': 3.93, 'x': -1.2, 'y': 2.4}, {'time': 4.06, 'x': 0.0, 'y': 0.0}, {'time': 4.19, 'x': -0.6, 'y': 1.2}, {'time': 4.33, 'x': 0.0, 'y': 2.4}, {'time': 4.46, 'x': 0.6, 'y': 1.7999999999999998}, {'time': 4.58, 'x': -0.6, 'y': 0.0}, {'time': 4.73, 'x': 0.6, 'y': 1.7999999999999998}, {'time': 4.86, 'x': -1.2, 'y': 1.2}, {'time': 4.99, 'x': 0.6, 'y': -1.7999999999999998}, {'time': 5.13, 'x': 0.6, 'y': -1.2}, {'time': 5.26, 'x': -0.6, 'y': 0.0}, {'time': 5.39, 'x': 1.2, 'y': -1.2}, {'time': 5.52, 'x': -0.6, 'y': -1.2}, {'time': 5.65, 'x': 1.2, 'y': -0.6}, {'time': 5.78, 'x': -0.6, 'y': 1.2}, {'time': 5.91, 'x': 0.0, 'y': 0.6}, {'time': 6.04, 'x': -0.6, 'y': 3.5999999999999996}]}, 'head': {'rotate': [{'angle': 0.03}, {'time': 0.1, 'angle': 12.35}, {'time': 0.2, 'angle': 25.55}, {'time': 0.4, 'angle': 11.03}, {'time': 0.5, 'angle': 12.35}, {'time': 0.6, 'angle': 25.55}, {'time': 0.8, 'angle': 11.03}]}, 'front-shin': {'rotate': [{'angle': 26.8}, {'time': 0.46, 'angle': 12.1}, {'time': 0.59, 'angle': -20.8}, {'time': 0.73, 'angle': -23.2}, {'time': 0.86, 'angle': 4.0}, {'time': 0.99, 'angle': 28.799999999999997}, {'time': 1.16, 'angle': 15.700000000000001}, {'time': 1.33, 'angle': -16.7}, {'time': 1.49, 'angle': -10.600000000000001}, {'time': 1.71, 'angle': 18.9}, {'time': 1.9, 'angle': 0.0}, {'time': 2.27, 'angle': 27.3}, {'time': 2.48, 'angle': -23.599999999999998}, {'time': 2.63, 'angle': -0.4}, {'time': 2.77, 'angle': 2.4}, {'time': 2.9, 'angle': 30.6}, {'time': 3.03, 'angle': 2.0}, {'time': 3.16, 'angle': 29.4}, {'time': 3.28, 'angle': -0.3}, {'time': 3.41, 'angle': 20.7}, {'time': 3.54, 'angle': -11.5}, {'time': 3.67, 'angle': 25.5}, {'time': 3.93, 'angle': -28.2}, {'time': 4.06, 'angle': 23.599999999999998}, {'time': 4.19, 'angle': 31.400000000000002}, {'time': 4.33, 'angle': 31.400000000000002}, {'time': 4.46, 'angle': 1.3}, {'time': 4.58, 'angle': 31.400000000000002}, {'time': 4.73, 'angle': 12.5}, {'time': 4.86, 'angle': -28.2}, {'time': 4.99, 'angle': 7.9}, {'time': 5.13, 'angle': -0.5}, {'time': 5.26, 'angle': -27.799999999999997}, {'time': 5.39, 'angle': -21.6}, {'time': 5.52, 'angle': 30.8}, {'time': 5.65, 'angle': 14.7}, {'time': 5.78, 'angle': -21.6}, {'time': 5.91, 'angle': -22.5}, {'time': 6.04, 'angle': 26.8}], 'translate': [{'x': -1.2, 'y': 0.6}, {'time': 0.46, 'x': 1.7999999999999998, 'y': 4.8}, {'time': 0.59, 'x': -3.0, 'y': -5.3999999999999995}, {'time': 0.73, 'x': -8.4, 'y': -9.0}, {'time': 0.86, 'x': 25.2, 'y': 10.799999999999999}, {'time': 0.99, 'x': -20.4, 'y': 5.3999999999999995}, {'time': 1.16, 'x': 0.0, 'y': 9.0}, {'time': 1.33, 'x': -1.2, 'y': -12.6}, {'time': 1.49, 'x': 3.0, 'y': -5.3999999999999995}, {'time': 1.71, 'x': -1.2, 'y': 3.5999999999999996}, {'time': 1.9, 'x': 16.8, 'y': 0.0}, {'time': 2.27, 'x': -9.6, 'y': 4.2}, {'time': 2.48, 'x': -1.2, 'y': -1.2}, {'time': 2.63, 'x': 16.2, 'y': -0.6}, {'time': 2.77, 'x': 2.4, 'y': 0.6}, {'time': 2.9, 'x': -22.2, 'y': 1.7999999999999998}, {'time': 3.03, 'x': 15.0, 'y': 3.0}, {'time': 3.16, 'x': -14.399999999999999, 'y': 3.0}, {'time': 3.28, 'x': 18.0, 'y': -0.6}, {'time': 3.41, 'x': -3.5999999999999996, 'y': 6.6}, {'time': 3.54, 'x': 2.4, 'y': -5.3999999999999995}, {'time': 3.67, 'x': -3.5999999999999996, 'y': 2.4}, {'time': 3.93, 'x': -10.799999999999999, 'y': -3.5999999999999996}, {'time': 4.06, 'x': -1.2, 'y': 1.2}, {'time': 4.19, 'x': -1.2, 'y': 0.0}, {'time': 4.33, 'x': -2.4, 'y': 0.0}, {'time': 4.46, 'x': 22.8, 'y': 3.0}, {'time': 4.58, 'x': -24.599999999999998, 'y': 0.0}, {'time': 4.73, 'x': 0.6, 'y': 1.7999999999999998}, {'time': 4.86, 'x': -1.7999999999999998, 'y': -0.6}, {'time': 4.99, 'x': 0.6, 'y': 0.6}, {'time': 5.13, 'x': 23.4, 'y': -1.2}, {'time': 5.26, 'x': -4.8, 'y': -1.7999999999999998}, {'time': 5.39, 'x': -1.2, 'y': -1.7999999999999998}, {'time': 5.52, 'x': -9.6, 'y': 0.6}, {'time': 5.65, 'x': 0.6, 'y': 6.0}, {'time': 5.78, 'x': -1.2, 'y': -1.7999999999999998}, {'time': 5.91, 'x': -2.4, 'y': -3.0}, {'time': 6.04, 'x': -1.2, 'y': 0.6}]}, 'front-foot': {'rotate': [{'angle': 30.0}, {'time': 0.46, 'angle': 11.299999999999999}, {'time': 0.59, 'angle': -20.299999999999997}, {'time': 0.73, 'angle': -26.0}, {'time': 0.86, 'angle': 5.0}, {'time': 1.71, 'angle': 0.0}, {'time': 1.9, 'angle': -30.7}, {'time': 2.48, 'angle': -30.5}, {'time': 2.63, 'angle': 28.2}, {'time': 3.16, 'angle': 26.200000000000003}, {'time': 3.54, 'angle': 2.1}, {'time': 3.93, 'angle': -27.9}, {'time': 4.19, 'angle': 20.9}, {'time': 5.13, 'angle': 0.5}, {'time': 5.26, 'angle': -29.0}, {'time': 5.39, 'angle': 29.4}, {'time': 5.65, 'angle': -29.8}, {'time': 5.78, 'angle': -21.6}, {'time': 5.91, 'angle': 27.200000000000003}, {'time': 6.04, 'angle': 30.0}], 'translate': [{'x': -4.2, 'y': 0.6}, {'time': 0.46, 'x': 62.4, 'y': 133.2}, {'time': 0.59, 'x': -2.4, 'y': -4.8}, {'time': 0.73, 'x': -6.0, 'y': -3.5999999999999996}, {'time': 0.86, 'x': 22.2, 'y': 12.0}, {'time': 1.71, 'x': 4.8, 'y': 0.0}, {'time': 1.9, 'x': -8.4, 'y': -0.6}, {'time': 2.48, 'x': -12.6, 'y': -1.2}, {'time': 2.63, 'x': -7.199999999999999, 'y': 2.4}, {'time': 3.16, 'x': -4.2, 'y': 2.4}, {'time': 3.54, 'x': 28.2, 'y': 6.0}, {'time': 3.93, 'x': -16.2, 'y': -6.0}, {'time': 4.19, 'x': -2.4, 'y': 4.2}, {'time': 5.13, 'x': 22.2, 'y': 1.2}, {'time': 5.26, 'x': -4.8, 'y': -1.2}, {'time': 5.39, 'x': -6.0, 'y': 1.2}, {'time': 5.65, 'x': -7.199999999999999, 'y': -1.2}, {'time': 5.78, 'x': -1.2, 'y': -1.7999999999999998}, {'time': 5.91, 'x': -5.3999999999999995, 'y': 2.4}, {'time': 6.04, 'x': -4.2, 'y': 0.6}]}, 'rear-foot': {'rotate': [{'angle': -0.8999999999999999}, {'time': 0.46, 'angle': 11.299999999999999}, {'time': 0.59, 'angle': -20.299999999999997}, {'time': 0.73, 'angle': -25.7}, {'time': 0.86, 'angle': 24.1}, {'time': 1.33, 'angle': -18.9}, {'time': 1.71, 'angle': 1.3}, {'time': 1.9, 'angle': -30.299999999999997}, {'time': 2.48, 'angle': 31.400000000000002}, {'time': 2.63, 'angle': 0.2}, {'time': 2.9, 'angle': -31.0}, {'time': 3.54, 'angle': 3.5999999999999996}, {'time': 3.67, 'angle': -29.8}, {'time': 3.93, 'angle': -25.7}, {'time': 4.19, 'angle': 26.8}, {'time': 5.65, 'angle': 4.0}, {'time': 5.78, 'angle': -20.299999999999997}, {'time': 5.91, 'angle': -0.8999999999999999}], 'translate': [{'x': 12.6, 'y': -1.2}, {'time': 0.46, 'x': 62.4, 'y': 133.79999999999998}, {'time': 0.59, 'x': -2.4, 'y': -4.8}, {'time': 0.73, 'x': -6.6, 'y': -4.2}, {'time': 0.86, 'x': -11.4, 'y': 10.2}, {'time': 1.33, 'x': -1.2, 'y': -3.5999999999999996}, {'time': 1.71, 'x': 40.199999999999996, 'y': 5.3999999999999995}, {'time': 1.9, 'x': -10.799999999999999, 'y': -1.2}, {'time': 2.48, 'x': -10.2, 'y': 0.0}, {'time': 2.63, 'x': 26.4, 'y': 0.6}, {'time': 2.9, 'x': -39.6, 'y': -1.7999999999999998}, {'time': 3.54, 'x': 30.0, 'y': 11.4}, {'time': 3.67, 'x': -7.199999999999999, 'y': -1.2}, {'time': 3.93, 'x': -6.6, 'y': -4.2}, {'time': 4.19, 'x': -4.8, 'y': 2.4}, {'time': 5.65, 'x': 4.2, 'y': 1.7999999999999998}, {'time': 5.78, 'x': -1.2, 'y': -2.4}, {'time': 5.91, 'x': 12.6, 'y': -1.2}]}, 'gun': {'rotate': [{'angle': 31.400000000000002}, {'time': 0.46, 'angle': 7.0}, {'time': 0.59, 'angle': -16.8}, {'time': 0.73, 'angle': -17.9}, {'time': 0.86, 'angle': 29.700000000000003}, {'time': 0.99, 'angle': 1.4000000000000001}, {'time': 1.16, 'angle': 15.700000000000001}, {'time': 1.33, 'angle': -28.5}, {'time': 1.49, 'angle': -12.5}, {'time': 1.71, 'angle': 1.9}, {'time': 1.9, 'angle': 25.5}, {'time': 2.09, 'angle': 24.5}, {'time': 2.27, 'angle': -25.7}, {'time': 2.77, 'angle': 30.8}, {'time': 2.9, 'angle': 18.9}, {'time': 3.03, 'angle': 12.7}, {'time': 3.16, 'angle': 0.3}, {'time': 3.28, 'angle': 31.400000000000002}, {'time': 3.41, 'angle': 12.5}, {'time': 3.54, 'angle': -0.4}, {'time': 3.67, 'angle': -31.200000000000003}, {'time': 3.79, 'angle': 0.0}, {'time': 3.93, 'angle': -0.7000000000000001}, {'time': 4.06, 'angle': 4.9}, {'time': 4.19, 'angle': 1.7000000000000002}, {'time': 4.33, 'angle': 3.8}, {'time': 4.46, 'angle': -13.700000000000001}, {'time': 4.58, 'angle': -15.700000000000001}, {'time': 4.73, 'angle': 15.700000000000001}, {'time': 4.86, 'angle': 31.400000000000002}, {'time': 4.99, 'angle': 1.0}, {'time': 5.13, 'angle': -18.9}, {'time': 5.26, 'angle': 25.5}, {'time': 5.39, 'angle': 30.299999999999997}, {'time': 5.52, 'angle': -26.8}, {'time': 5.65, 'angle': 25.0}, {'time': 5.78, 'angle': 18.9}, {'time': 5.91, 'angle': -27.799999999999997}, {'time': 6.04, 'angle': 31.400000000000002}], 'translate': [{'x': -1.7999999999999998, 'y': 0.0}, {'time': 0.46, 'x': 15.6, 'y': 13.2}, {'time': 0.59, 'x': -0.6, 'y': -5.3999999999999995}, {'time': 0.73, 'x': -1.2, 'y': -5.3999999999999995}, {'time': 0.86, 'x': -28.2, 'y': 4.8}, {'time': 0.99, 'x': 43.8, 'y': 6.0}, {'time': 1.16, 'x': 0.0, 'y': 6.6}, {'time': 1.33, 'x': -43.199999999999996, 'y': -13.2}, {'time': 1.49, 'x': 2.4, 'y': -7.199999999999999}, {'time': 1.71, 'x': 39.6, 'y': 7.8}, {'time': 1.9, 'x': -7.199999999999999, 'y': 4.8}, {'time': 2.09, 'x': -3.5999999999999996, 'y': 3.0}, {'time': 2.27, 'x': -13.2, 'y': -8.4}, {'time': 2.77, 'x': -10.2, 'y': 0.6}, {'time': 2.9, 'x': -0.6, 'y': 1.7999999999999998}, {'time': 3.03, 'x': 2.4, 'y': 7.8}, {'time': 3.16, 'x': 41.4, 'y': 1.2}, {'time': 3.28, 'x': -40.8, 'y': 0.0}, {'time': 3.41, 'x': 0.6, 'y': 1.7999999999999998}, {'time': 3.54, 'x': 34.199999999999996, 'y': -1.2}, {'time': 3.67, 'x': -33.0, 'y': -0.6}, {'time': 3.79, 'x': 6.0, 'y': 0.0}, {'time': 3.93, 'x': 9.0, 'y': -0.6}, {'time': 4.06, 'x': 7.8, 'y': 4.2}, {'time': 4.19, 'x': 3.5999999999999996, 'y': 0.6}, {'time': 4.33, 'x': 3.0, 'y': 1.2}, {'time': 4.46, 'x': 0.6, 'y': -3.0}, {'time': 4.58, 'x': 0.0, 'y': -1.2}, {'time': 4.73, 'x': 0.0, 'y': 1.2}, {'time': 4.86, 'x': -37.199999999999996, 'y': 0.0}, {'time': 4.99, 'x': 35.4, 'y': 3.5999999999999996}, {'time': 5.13, 'x': -0.6, 'y': -1.7999999999999998}, {'time': 5.26, 'x': -1.7999999999999998, 'y': 1.2}, {'time': 5.39, 'x': -5.3999999999999995, 'y': 0.6}, {'time': 5.52, 'x': -13.2, 'y': -6.6}, {'time': 5.65, 'x': -2.4, 'y': 1.7999999999999998}, {'time': 5.78, 'x': -0.6, 'y': 1.7999999999999998}, {'time': 5.91, 'x': -4.8, 'y': -1.7999999999999998}, {'time': 6.04, 'x': -1.7999999999999998, 'y': 0.0}]}, 'hip': {}}, 'events': [{'name': 'footstep'}, {'time': 0.4333, 'name': 'footstep', 'int': 1}]}}}";

        //Gdx.files.local("spineboy/spineboy-extest.json").writeString(js.replace('\'', '\"'), false);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.local("spineboy/" + gifId + ".json"));


        skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
        skeleton.setPosition(192, 250);
        skeleton.setAttachment("head-bb", "head"); // Attach "head" bounding box to "head-bb" slot.
        skeleton.setScale(1,-1);
        bounds = new SkeletonBounds(); // Convenience class to do hit detection with bounding boxes.

        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        state.setTimeScale(2.0f); // Slow all animations down to 30% speed.

        // Set animation on track 0.
        state.setAnimation(0, "run", true);


    }

    List<Pixmap> frames;

    public Pixmap ScreenShot() {
        int width = Gdx.graphics.getBackBufferWidth();
        int height = Gdx.graphics.getBackBufferHeight();
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        ByteBuffer pixels = pixmap.getPixels();
        Gdx.gl.glReadPixels(0, 0, width, height, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels);
        return pixmap;
    }

    private int[][] RotateMatrix(int[][] matrix) {

        int[][] mMatrix = new int[matrix[0].length][matrix.length];
        int mRow = 0;
        for (int col = 0; col < matrix[0].length; ++col) {

            int mCol = 0;

            for (int row = 0; row < matrix.length; ++row) {
                mMatrix[mRow][mCol] = matrix[row][matrix[col].length - col - 1];
                mCol++;
            }
            mRow++;
        }

        return mMatrix;
    }


    public void CreateGif() {
        try {
            final ImageOptions options = new ImageOptions();
            options.setDelay(0, TimeUnit.MILLISECONDS);
            int width = Gdx.graphics.getBackBufferWidth();
            int height = Gdx.graphics.getBackBufferHeight();
            final GifEncoder encoder = new GifEncoder(Gdx.files.local("imgs/" + gifId + ".gif").write(false), width, height, 0);

            final List<int[][]> pixelsList = new ArrayList<>();
            for (Pixmap pixmap : frames) {
                System.out.println("Got PixMap");
                int[][] pixels = new int[width][height];
                for (int x = 0; x < width; ++x)
                    for (int y = 0; y < height; ++y) {
                        int pixel = pixmap.getPixel(x, y);
                        int r = (pixel >> 24) & 0xFF;
                        int g = (pixel >> 16) & 0xFF;
                        int b = (pixel >> 8) & 0xFF;
                        int a = pixel & 0xFF;
                        pixel = (a << 24) + (r << 16) + (g << 8) + b; // ARGB
                        pixels[x][y] = pixel;
                    }
                System.out.println("Added Pixels");
                    System.out.println("Pixels:" + pixels.length + ";" + pixels[0].length);

                pixels = RotateMatrix(pixels);
                pixelsList.add(pixels);
                //encoder.addImage(pixels, options);
            }


            try {
                System.out.println("Started adding");
                int i = 0;
                for (int[][] pixels : pixelsList) {
                    System.out.println(i + "/" + pixelsList.size());
                    i++;
                    encoder.addImage(pixels, options);
                }
                System.out.println("Ending adding------------------------------------------------");
                encoder.finishEncoding();
                frames.clear();
                //Gdx.app.exit();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Finished");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean GifProcessing = false;

    public void GifCreation() throws IOException {
        ImageOutputStream imageOutputStream = new FileImageOutputStream(Gdx.files.local(gifId+ File.separator+"tempFile"+".gif").file());

        FileHandle tempFile = Gdx.files.local(gifId+ File.separator+"tempFile"+".png");
        PixmapIO.writePNG(tempFile,frames.get(0));
        frames.get(0).dispose();
        frames.remove(0);
        BufferedImage buffImage = ImageIO.read(tempFile.file());

        GifSequenceWriter writer = new GifSequenceWriter(imageOutputStream,buffImage.getType(),0,true);

        for (Pixmap pixmap: frames){

            tempFile = Gdx.files.local(gifId+ File.separator+"tempFile"+".png");
            PixmapIO.writePNG(tempFile,pixmap);
            pixmap.dispose();
            buffImage = ImageIO.read(tempFile.file());
            writer.writeToSequence(buffImage);
        }
        writer.close();
        imageOutputStream.close();
    }
    public void render() {
        state.update(Gdx.graphics.getDeltaTime()); // Update the animation time.

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
        if (state.apply(skeleton)) // Poses skeleton using current animations. This sets the bones' local SRT.
            skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        batch.begin();
        renderer.draw(batch, skeleton); // Draw the skeleton images.
        batch.end();

        if (frames == null)
            frames = new ArrayList<>();
        if (!GifProcessing && frames.size() >= frameCount) {
            GifProcessing = true;
            System.out.println("Start processing");
            try {
               GifCreation();
            } catch (IOException e) {
                e.printStackTrace();
            }
            long startTime = System.currentTimeMillis();
            CreateGif();
            System.out.println("Сделано за "+(System.currentTimeMillis()-startTime)/1000.0);

        } else
            frames.add(ScreenShot());
    }

    public void dispose() {
        atlas.dispose();
    }

    static LwjglApplication app;


    static String gifId="8";
    static int frameCount=60;

    public static void main(String[] args) throws Exception {

        System.out.println(Arrays.toString(args));
        if (args.length != 2 || args[0].isEmpty() || args[1].isEmpty())
           System.exit(0);
        gifId = args[0];
        frameCount = Integer.parseInt(args[1]);
        app = new LwjglApplication(new SimpleTest2(), "Diplom", 450, 450);
    }
}
