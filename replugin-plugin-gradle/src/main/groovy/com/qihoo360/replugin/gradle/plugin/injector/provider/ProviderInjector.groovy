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

package com.qihoo360.replugin.gradle.plugin.injector.provider

import com.qihoo360.replugin.gradle.plugin.inner.Util
import com.qihoo360.replugin.gradle.plugin.injector.BaseInjector
import javassist.ClassPool

import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

/**
 * @author RePlugin Team
 */
public class ProviderInjector extends BaseInjector {

    // 处理以下方法
    public static def includeMethodCall = ['query',
                                           'getType',
                                           'insert',
                                           'bulkInsert',
                                           'delete',
                                           'update',
            /// 以下方法 replugin plugin lib 暂未支持，导致字节码修改失败。
                                           'openInputStream',
                                           'openOutputStream',
                                           'openFileDescriptor',
                                           'registerContentObserver',
                                           'acquireContentProviderClient',
                                           'notifyChange',
                                           'toCalledUri',
    ]

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
            editor = new ProviderExprEditor()
        }

        Util.newSection()
        println dir

        Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
            @Override
            FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                String filePath = file.toString()
                String relativeFilePath = filePath.replace(dir, "")
                def stream, ctCls

                try {
                    if (filePath.contains('PluginProviderClient.class')) {
                        throw new Exception('can not replace self ')
                    }

                    if (!filePath.endsWith('.class')) {
                        println "    Ignore ${relativeFilePath}"
                        return super.visitFile(file, attrs)
                    }

                    stream = new FileInputStream(filePath)
                    ctCls = pool.makeClass(stream);

                    // println ctCls.name
                    if (ctCls.isFrozen()) {
                        ctCls.defrost()
                    }

                    editor.filePath = relativeFilePath

                    (ctCls.getDeclaredMethods() + ctCls.getMethods()).each {
                        it.instrument(editor)
                    }

                    ctCls.writeFile(dir)
                } catch (Throwable t) {
                    println "    [ProviderInjector:Warning] --> ${t.toString()}"
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

        //Util.newSection()
    }
}
