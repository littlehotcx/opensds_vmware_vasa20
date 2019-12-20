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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class CommandsUtil {

    public static String lineseparator = System.getProperty("line.separator");
    public static String COMMAND_SH = "sh";
    public static String COMMAND_EXIT = "exit\n";
    public static String COMMAND_LINE_END = "\n";

    static {
        if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
            System.out.println("window");
            COMMAND_SH = "cmd";
        } else {
            System.out.println("unix");
        }

    }

    public static CommandResult execCommand(String command) {
        return execCommand(new String[]{command}, true);
    }

    public static CommandResult execCommand(String command, boolean isNeedResultMsg) {
        return execCommand(new String[]{command}, isNeedResultMsg);
    }

    public static CommandResult execCommand(List<String> commands, boolean isNeedResultMsg) {
        return execCommand(commands == null ? null : commands.toArray(new String[]{}), isNeedResultMsg);
    }

    public static CommandResult execCommand(String[] commands, final boolean needResponse) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, "空命令");
        }

        Process process = null;

        final StringBuilder successMsg = new StringBuilder();
        final StringBuilder errorMsg = new StringBuilder();

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                // donnot use os.writeBytes(commmand), avoid chinese charset error
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            final BufferedReader successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            final BufferedReader errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            new Thread(new Runnable() {
                public void run() {

                    try {
                        if (needResponse) {
                            String s;
                            while ((s = successResult.readLine()) != null) {
                                successMsg.append(s);
                                successMsg.append(lineseparator);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            //启动两个线程,解决process.waitFor()阻塞问题
            new Thread(new Runnable() {
                public void run() {

                    try {
                        if (needResponse) {
                            String s;
                            while ((s = errorResult.readLine()) != null) {
                                errorMsg.append(s);
                                errorMsg.append(lineseparator);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            result = process.waitFor();
            if (errorResult != null) {
                errorResult.close();
            }
            if (successResult != null) {
                successResult.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }

        }
        return new CommandResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null
                : errorMsg.toString());
    }


}