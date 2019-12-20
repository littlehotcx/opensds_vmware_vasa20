/*
 * // Copyright 2019 The OpenSDS Authors.
 * //
 * // Licensed under the Apache License, Version 2.0 (the "License"); you may
 * // not use this file except in compliance with the License. You may obtain
 * // a copy of the License at
 * //
 * //     http://www.apache.org/licenses/LICENSE-2.0
 * //
 * // Unless required by applicable law or agreed to in writing, software
 * // distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * // WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * // License for the specific language governing permissions and limitations
 * // under the License.
 *
 */

package org.opensds.vasa.vasa.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import org.opensds.vasa.common.MagicNumber;

import com.vmware.vim.vasa.v20.data.vvol.xsd.TaskInfo;

public class VasaTask extends TaskInfo {
    protected final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(VasaTask.class);

    private static final long DEFAULT_TASK_TIMEOUT = 172800;

    private static List<VasaTask> vasaTaskList = new ArrayList<VasaTask>();

    private static ReentrantLock vasaTaskListLock = new ReentrantLock();

    private Timer timer;

    protected VasaTask(TaskInfo taskInfo) {
        this.name = taskInfo.getName();
        this.cancelable = taskInfo.isCancelable();
        this.cancelled = taskInfo.isCancelled();
        this.progressUpdateAvailable = taskInfo.isProgressUpdateAvailable();
        this.startTime = taskInfo.getStartTime();
        this.taskState = taskInfo.getTaskState();
        this.estimatedTimeToComplete = taskInfo.getEstimatedTimeToComplete();
        this.error = taskInfo.getError();
        this.taskId = taskInfo.getTaskId();
        this.result = taskInfo.getResult();
        this.progress = taskInfo.getProgress();
        this.arrayId = taskInfo.getArrayId();

        vasaTaskListLock.lock();
        try {
            vasaTaskList.add(this);
        } finally {
            vasaTaskListLock.unlock();
        }

        restartTimer();
    }


    /*
     * Cancel any existing timer task and
     * start a new one.
     */
    private boolean restartTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }
        this.timer = new Timer(true);

        try {
            this.timer.schedule(new TaskTimeoutTask(), DEFAULT_TASK_TIMEOUT * MagicNumber.INT1000);
            return true;
        } catch (Exception e) {
            LOGGER.debug("Could not restart task timer for taskId: " + taskId
                    + " e: " + e);
            return false;
        }
    }

    class TaskTimeoutTask extends TimerTask {
        public void run() {
            removeFromList(taskId);
        }
    }


    protected static void refreshTaskList() {
        vasaTaskListLock.lock();
        try {
            if (0 == vasaTaskList.size()) {
                return;
            }

            for (int i = 0; i < vasaTaskList.size(); i++) {
                VasaTask task = vasaTaskList.get(i);
                task.refreshTask();
                task.restartTimer();
            }

        } finally {
            vasaTaskListLock.unlock();
        }

    }


    protected void refreshTask() {

    }

    public static List<TaskInfo> lookupTaskByArrayId(String arrayId) {


        refreshTaskList();

        List<TaskInfo> result = new ArrayList<TaskInfo>();


        /**
         * 修改CodeCC问题：Category：Concurrent data access violations  Type：Unguarded read   Start
         */
        vasaTaskListLock.lock();

        try {
            if ((vasaTaskList != null) && (arrayId != null)) {

                for (VasaTask task : vasaTaskList) {
                    if (task != null
                            && arrayId.equalsIgnoreCase(task.getArrayId())
                            && task.taskState.equalsIgnoreCase("Running")) {
                        result.add(task);
                    }
                }

            }
        } finally {
            vasaTaskListLock.unlock();
        }
        /**
         * 修改CodeCC问题：Category：Concurrent data access violations  Type：Unguarded read   End
         */

        return result;
    }

    public static VasaTask lookupTaskByTaskId(String taskId) {
        vasaTaskListLock.lock();
        try {
            for (VasaTask task : vasaTaskList) {
                if (task != null && task.getTaskId().equalsIgnoreCase(taskId)) {
                    return task;
                }
            }

            return null;
        } finally {
            vasaTaskListLock.unlock();
        }
    }

    public static void removeFromList(String taskId) {
        /**
         * 修改CodeCC问题：Category：Concurrent data access violations  Type：Unguarded read   Start
         */
        vasaTaskListLock.lock();
        try {
            if (vasaTaskList != null && taskId != null) {
                boolean removed = false;

                for (int i = 0; i < vasaTaskList.size(); i++) {
                    VasaTask task = vasaTaskList.get(i);
                    if (task.getTaskId().equalsIgnoreCase(taskId)) {
                        if (task.timer != null) {
                            task.timer.cancel();
                        }

                        vasaTaskList.remove(i);
                        removed = true;
                        break;
                    }
                }

                if (removed) {
                    LOGGER.info("task " + taskId + " removed.");
                } else {
                    LOGGER.info("task " + taskId
                            + " could not be removed. Not found in list.");
                }
            }
        } finally {
            vasaTaskListLock.unlock();
        }
        /**
         * 修改CodeCC问题：Category：Concurrent data access violations  Type：Unguarded read   End
         */
    }

    /**
     * 方法 ： equals
     *
     * @param o 方法参数：o
     * @return boolean 返回结果
     */
    @Override
    public boolean equals(Object o) {
        if (null == o) {
            return false;
        }

        if (o instanceof VasaTask) {
            VasaTask task = (VasaTask) o;
            return this.getTaskId().equals(task.getTaskId());
        }

        return false;
    }

    /**
     * 方法 ： hashCode
     *
     * @return int 返回结果
     */
    @Override
    public int hashCode() {
        return this.getTaskId().hashCode();
    }

}
