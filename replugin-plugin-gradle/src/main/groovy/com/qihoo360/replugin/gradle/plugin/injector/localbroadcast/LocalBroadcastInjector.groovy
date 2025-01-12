/*
 * Copyright (C) 2005-2017 Qihoo 360 Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.qihoo360.replugin.gradle.plugin.injector.localbroadcast

import com.qihoo360.replugin.gradle.plugin.injector.BaseInjector
import com.qihoo360.replugin.gradle.plugin.inner.Util
import javassist.ClassPool

import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

/**
 * LocalBroadcastInjector
 *
 * 将插件中的 LocalBroadcast 调用转发到宿主
 *
 * @author RePlugin Team
 */
public class LocalBroadcastInjector extends BaseInjector {

    // 表达式编辑器
    def editor

    @Override
    def injectClass(ClassPool pool, String dir, Map config) {

        // 不处理非 build 目录下的类
/*
        if (!dir.contains('build' + File.separator + 'intermediates')) {
            println "跳过$dir"
            return
        }
*/

        if (editor == null) {
            editor = new LocalBroadcastExprEditor()
        }

        Util.newSection()
        println dir

        Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
            @Override
            FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                String filePath = file.toString()
                String relativeFilePath = filePath.replace(dir, "")
                editor.filePath = relativeFilePath

                def stream, ctCls
                try {
                    // Not processed: LocalBroadcastManager.class
                    if (filePath.contains('androidx/localbroadcastmanager/content/LocalBroadcastManager')) {
                        println "    Ignore ${relativeFilePath}"
                        return super.visitFile(file, attrs)
                    }

                    if (!filePath.endsWith('.class')) {
                        println "    Ignore ${relativeFilePath}"
                        return super.visitFile(file, attrs)
                    }

                    //println "makeClass()"
                    stream = new FileInputStream(filePath)
                    ctCls = pool.makeClass(stream);

                    //println ctCls.name
                    //println "defrost()"
                    if (ctCls.isFrozen()) {
                        ctCls.defrost()
                    }

                    /* Check method list */
                    //println "getDeclaredMethods()"
                    ctCls.getDeclaredMethods().each {
                        it.instrument(editor)
                    }

                    //println "getMethods()"
                    ctCls.getMethods().each {
                        it.instrument(editor)
                    }

                    //println "writeFile()"
                    ctCls.writeFile(dir)
                } catch (Throwable t) {
                    println "    [LocalBroadcastInjector:Warning] --> ${t.toString()}"
                    //t.printStackTrace()
                } finally {
                    if (ctCls != null) {
                        ctCls.detach()
                    }
                    if (stream != null) {
                        stream.close()
                    }
                }

                return super.visitFile(file, attrs)
            }
        })
    }
}
