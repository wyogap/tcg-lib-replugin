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

package com.qihoo360.replugin.gradle.plugin

/**
 * @author RePlugin Team
 */
class AppConstant {

    /** ??? */
    def static final VER = "2.3.3"

    /** ????????? */
    def static final TAG = "< replugin-plugin-v${VER} >"

    /** ???????? */
    def static final USER_CONFIG = "repluginPluginConfig"

    /** ??Task? */
    def static final TASKS_GROUP = "replugin-plugin"

    /** Task?? */
    def static final TASKS_PREFIX = "rp"


    /** ??Task:??????app */
    def static final TASK_FORCE_STOP_HOST_APP = TASKS_PREFIX + "ForceStopHostApp"

    /** ??Task:????app */
    def static final TASK_START_HOST_APP = TASKS_PREFIX + "StartHostApp"

    /** ??Task:????app */
    def static final TASK_RESTART_HOST_APP = TASKS_PREFIX + "RestartHostApp"


    /** ??Task:???? */
    def static final TASK_INSTALL_PLUGIN = TASKS_PREFIX + "InstallPlugin"

    /** ??Task:???? */
    def static final TASK_UNINSTALL_PLUGIN = TASKS_PREFIX + "UninstallPlugin"

    /** ??Task:???? */
    def static final TASK_RUN_PLUGIN = TASKS_PREFIX + "RunPlugin"

    /** ??Task:??????? */
    def static final TASK_INSTALL_AND_RUN_PLUGIN = TASKS_PREFIX + "InstallAndRunPlugin"


    /** ???? */
    static final String CONFIG_EXAMPLE = '''
// ??plugin????android???????????android?????
apply plugin: 'replugin-plugin-gradle\'
repluginPluginConfig {
    pluginName = "demo3"
    hostApplicationId = "com.qihoo360.replugin.sample.host"
    hostAppLauncherActivity = "com.qihoo360.replugin.sample.host.MainActivity"
}
'''

    private AppConstant() {}
}
