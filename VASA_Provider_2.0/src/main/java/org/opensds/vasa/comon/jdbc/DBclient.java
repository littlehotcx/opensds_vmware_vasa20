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

package org.opensds.vasa.comon.jdbc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DBclient {

    public static void main(String[] args) throws IOException {
        String db_ip = args[0];
        String db_port = args[1];
        String db_username = args[2];
        String db_name = args[3];
        String db_password = args[4];
        String operation = args[5];
        String operation_input = args[6];
        int result_status = 1;
        String url = "jdbc:postgresql://" + db_ip + ":" + db_port + "/" + db_name;
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, db_username, db_password);
            if ("add".equalsIgnoreCase(operation)) {
                result_status = executeAdd(operation_input, connection);
            } else if ("delete".equalsIgnoreCase(operation)) {
                result_status = executeDelete(operation_input, connection);
            } else if ("update".equalsIgnoreCase(operation)) {
                result_status = executeUpdate(operation_input, connection);
            } else if ("select".equalsIgnoreCase(operation)) {
                result_status = executeSelect(operation_input, connection);
            } else if ("file".equalsIgnoreCase(operation)) {
                result_status = executeFile(operation_input, connection);
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
        } catch (SQLException e) {
            // TODO Auto-generated catch block
        } finally {
            try {
                if (null != connection) {
                    connection.close();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.exit(result_status);
        }
    }

    private static int executeFile(String filename, Connection connection) throws SQLException, IOException {
        BufferedReader bufferedReader = null;
        try {
            connection.setAutoCommit(false);
            bufferedReader = new BufferedReader(new FileReader(new File(filename.trim())));
            String readLine = bufferedReader.readLine();
            String sql = "";
            while (readLine != null) {
                readLine = readLine.trim();
                if (readLine.startsWith("values")) {
                    sql += " " + readLine;
                } else if ("".equals(readLine)) {

                } else if (readLine.endsWith(";")) {
                    sql += readLine;
                    PreparedStatement prepareStatement = connection.prepareStatement(sql);
                    prepareStatement.execute();
                    sql = "";
                } else {
                    sql += readLine;
                }
                readLine = bufferedReader.readLine();
            }
            bufferedReader.close();
            connection.commit();
            System.out.println("success");
            return 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("fail");
            e.printStackTrace();
            connection.rollback();
            return 1;
        } finally {
            if (null != bufferedReader) {
                bufferedReader.close();
            }
        }
    }

    private static int executeUpdate(String sql, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.executeUpdate(sql);
            System.out.println("success");
            return 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("fail");
            statement.close();
            return 1;
        }

    }

    private static int executeDelete(String sql, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.execute(sql);
            System.out.println("success");
            return 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("fail");
            statement.close();
            return 1;
        }
    }

    private static int executeAdd(String sql, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.execute(sql);
            System.out.println("success");
            return 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("fail");
            statement.close();
            return 1;
        }
    }


    private static int executeSelect(String sql, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            System.out.println("success");
            int cols = metaData.getColumnCount();
            String resultRow = "";
            for (int i = 1; i < cols; i++) {
                if (i == cols - 1) {
                    resultRow += metaData.getColumnName(i);
                } else {
                    resultRow += metaData.getColumnName(i) + " ";
                }
            }
            System.out.println(resultRow);
            while (resultSet.next()) {
                resultRow = "";
                for (int i = 1; i < cols; i++) {
                    try {
                        if (i == cols - 1) {
                            resultRow += resultSet.getString(i);
                        } else {
                            resultRow += resultSet.getString(i) + " ";
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
                System.out.println(resultRow);
            }
            return 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if (e.getMessage().indexOf("does not exist") >= 0) {
                System.out.println("not exist");
                return 2;
            }
            System.out.println("fail");
            return 1;
        } finally {
            statement.close();
        }
    }
}
